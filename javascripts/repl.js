(function () {
  "use strict";

  function InputQueue(schedule) {
    this.schedule = schedule;
    this.pendingRead = null;
    this.bytes = new Uint8Array(0);
    this.offset = 0;
  }

  InputQueue.prototype.waiting = function () {
    return this.pendingRead !== null;
  };

  InputQueue.prototype.write = function (bytes) {
    this.bytes = bytes;
    this.offset = 0;
    this.pump();
  };

  InputQueue.prototype.read = function (
    buffer, offset, length, callback
  ) {
    this.pendingRead = {
      buffer: buffer,
      offset: offset,
      length: length,
      callback: callback
    };
    this.schedule(this.pump.bind(this));
  };

  InputQueue.prototype.pump = function () {
    if (!this.pendingRead || this.offset >= this.bytes.length) return;

    var read = this.pendingRead;
    var count = Math.min(
      read.length,
      this.bytes.length - this.offset
    );
    var chunk = this.bytes.subarray(this.offset, this.offset + count);
    this.pendingRead = null;
    this.offset += count;
    read.buffer.set(chunk, read.offset);
    read.callback(null, count);

    if (this.offset >= this.bytes.length) {
      this.bytes = new Uint8Array(0);
      this.offset = 0;
    }
  };

  function taskScheduler() {
    var callbacks = [];
    var channel = new MessageChannel();

    channel.port1.onmessage = function () {
      var callback = callbacks.shift();
      if (callback) callback();
    };

    return function (callback) {
      callbacks.push(callback);
      channel.port2.postMessage(null);
    };
  }

  if (typeof module !== "undefined") {
    module.exports = InputQueue;
  }
  if (typeof document === "undefined") return;

  var running = false;

  function append(output, input, text, className) {
    var span = document.createElement("span");
    if (className) span.className = className;
    span.textContent = text;
    output.insertBefore(span, input);
    input.scrollIntoView({ block: "nearest" });
  }

  function start() {
    var loading = document.getElementById("repl-loading");
    if (!loading || running) return;
    running = true;

    var shell = document.getElementById("repl-shell");
    var output = document.getElementById("repl-output");
    var input = document.getElementById("repl-input");
    var terminal = document.getElementById("repl-terminal");
    var share = document.getElementById("repl-share");
    var clear = document.getElementById("repl-clear");
    var notice = document.getElementById("repl-notice");
    var history = [];
    var historyIndex = 0;
    var stdin = new InputQueue(taskScheduler());
    var noticeTimer = null;
    var replayQueue = sharedHistory();
    var replayLast = replayQueue.pop() || "";
    var clearSharedURL = window.location.hash.indexOf("#s:") === 0;

    function decodeBase64(encoded) {
      var binary = atob(encoded);
      var bytes = new Uint8Array(binary.length);
      for (var index = 0; index < binary.length; index += 1) {
        bytes[index] = binary.charCodeAt(index);
      }
      return new TextDecoder("utf-8").decode(bytes);
    }

    function encodeBase64(text) {
      var bytes = new TextEncoder().encode(text);
      var binary = "";
      for (var index = 0; index < bytes.length; index += 1) {
        binary += String.fromCharCode(bytes[index]);
      }
      return btoa(binary);
    }

    function sharedHistory() {
      if (window.location.hash.indexOf("#s:") !== 0) return [];

      try {
        var decoded = JSON.parse(
          decodeBase64(window.location.hash.slice(3))
        );
        if (
          Array.isArray(decoded) &&
          decoded.every(function (source) {
            return typeof source === "string";
          })
        ) {
          return decoded;
        }
      } catch (error) {
        console.warn("Could not decode shared Gobb REPL state", error);
      }
      return [];
    }

    function shareURL() {
      var url = new URL(window.location.href);
      url.hash = "s:" + encodeBase64(JSON.stringify(history));
      return url.href;
    }

    function copyText(text) {
      if (navigator.clipboard && navigator.clipboard.writeText) {
        return navigator.clipboard.writeText(text);
      }

      return new Promise(function (resolve, reject) {
        var copy = document.createElement("textarea");
        copy.value = text;
        copy.style.position = "fixed";
        copy.style.opacity = "0";
        document.body.appendChild(copy);
        copy.select();
        try {
          if (!document.execCommand("copy")) {
            throw new Error("copy command failed");
          }
          resolve();
        } catch (error) {
          reject(error);
        } finally {
          copy.remove();
        }
      });
    }

    function showNotice(message) {
      window.clearTimeout(noticeTimer);
      notice.textContent = message;
      notice.hidden = false;
      noticeTimer = window.setTimeout(function () {
        notice.hidden = true;
      }, 1800);
    }

    function focusAtEnd(element) {
      var selection = window.getSelection();
      var range = document.createRange();
      element.focus();
      range.selectNodeContents(element);
      range.collapse(false);
      selection.removeAllRanges();
      selection.addRange(range);
    }

    function fail(error) {
      shell.hidden = true;
      loading.hidden = false;
      loading.innerHTML = "";
      append(loading, null, "Could not load Gobb: " +
             (error.message || error),
             "repl-error");
      console.error(error);
    }

    function evaluate(source) {
      if (!stdin.waiting()) return;
      if (!source) return;

      append(output, input, source + "\n", "repl-entered");
      history.push(source);
      historyIndex = history.length;
      input.textContent = "";

      // fmt.Scanln in Gobb reads one whitespace-free token. URI encoding
      // preserves arbitrary strings and multiline forms in that token.
      stdin.write(new TextEncoder().encode(
        encodeURIComponent(source) + "\n"
      ));
    }

    function submit() {
      var source = input.innerText.replace(/\u00a0/g, " ").trim();
      evaluate(source);
    }

    input.addEventListener("keydown", function (event) {
      if (event.key === "Enter" && !event.shiftKey) {
        event.preventDefault();
        submit();
      } else if (event.key === "ArrowUp" && history.length) {
        event.preventDefault();
        historyIndex = Math.max(0, historyIndex - 1);
        input.textContent = history[historyIndex];
      } else if (event.key === "ArrowDown" && history.length) {
        event.preventDefault();
        historyIndex = Math.min(history.length, historyIndex + 1);
        input.textContent =
          historyIndex === history.length ? "" : history[historyIndex];
      }
    });

    clear.addEventListener("click", function () {
      Array.from(output.children).forEach(function (child) {
        if (child !== input) child.remove();
      });
      if (stdin.waiting()) {
        append(output, input, "user=> ");
      }
      input.focus();
    });

    share.addEventListener("click", function () {
      copyText(shareURL()).then(function () {
        showNotice("Copied sharable REPL state URL");
      }).catch(function (error) {
        showNotice("Could not copy REPL state URL");
        console.error(error);
      });
      input.focus();
    });

    var script = document.createElement("script");
    script.src = new URL("wasm_exec.js", window.location.href).href;
    script.onload = function () {
      var go = new Go();
      var decoder = new TextDecoder("utf-8");
      var originalRead = globalThis.fs.read;
      var outputTail = "";
      var promptReady = false;

      globalThis.fs.fstat = function (fd, callback) {
        callback(null, {
          dev: 0, ino: 0, mode: 8592, nlink: 0, uid: 0, gid: 0,
          rdev: 0, size: 0, blksize: 0, blocks: 0,
          atimeMs: 0, mtimeMs: 0, ctimeMs: 0,
          isDirectory: function () { return false; },
          isFile: function () { return false; },
          isBlockDevice: function () { return false; },
          isCharacterDevice: function () { return fd < 3; },
          isSymbolicLink: function () { return false; },
          isFIFO: function () { return false; },
          isSocket: function () { return false; }
        });
      };

      globalThis.fs.writeSync = function (_fd, buffer) {
        var text = decoder.decode(buffer);
        append(output, input, text);
        outputTail = (outputTail + text).slice(-200);
        if (/(?:^|\n)[^\n]*=> $/.test(outputTail)) {
          promptReady = true;
        }
        return buffer.length;
      };

      globalThis.fs.read = function (
        fd, buffer, offset, length, position, callback
      ) {
        if (fd !== 0) {
          return originalRead(fd, buffer, offset, length, position, callback);
        }
        stdin.read(buffer, offset, length, callback);
        if (!promptReady) return;
        promptReady = false;

        if (replayQueue.length) {
          var source = replayQueue.shift();
          window.setTimeout(function () {
            evaluate(source);
          }, 0);
        } else {
          if (replayLast) {
            input.textContent = replayLast;
            replayLast = "";
            focusAtEnd(input);
          } else {
            input.focus();
          }
          if (clearSharedURL) {
            window.history.replaceState(
              null,
              "",
              window.location.pathname + window.location.search
            );
            clearSharedURL = false;
          }
        }
      };

      WebAssembly.instantiateStreaming(
        fetch(new URL("gobb.wasm", window.location.href)),
        go.importObject
      ).then(function (result) {
        loading.hidden = true;
        shell.hidden = false;

        // Let the browser paint the terminal before starting the Wasm
        // runtime. Go's first stdin read then yields through taskScheduler,
        // keeping the page responsive while the REPL is running.
        requestAnimationFrame(function () {
          setTimeout(function () {
            go.run(result.instance).then(function () {
              input.contentEditable = "false";
              append(output, input, "\nREPL exited.\n", "repl-muted");
            }).catch(fail);
          }, 0);
        });
      }).catch(fail);
    };
    script.onerror = function () {
      fail(new Error("wasm_exec.js failed to load"));
    };
    document.head.appendChild(script);
  }

  if (typeof document$ !== "undefined") {
    document$.subscribe(start);
  } else if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", start);
  } else {
    start();
  }
}());
