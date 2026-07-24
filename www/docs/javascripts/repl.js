(function () {
  "use strict";

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
    var clear = document.getElementById("repl-clear");
    var history = [];
    var historyIndex = 0;
    var pendingRead = null;

    function fail(error) {
      loading.innerHTML = "";
      append(loading, null, "Could not load Gobb: " + error.message,
             "repl-error");
      console.error(error);
    }

    function submit() {
      if (!pendingRead) return;
      var source = input.innerText.replace(/\u00a0/g, " ").trim();
      if (!source) return;

      append(output, input, source + "\n", "repl-entered");
      history.push(source);
      historyIndex = history.length;
      input.textContent = "";

      // fmt.Scanln in Gobb reads one whitespace-free token. URI encoding
      // preserves arbitrary strings and multiline forms in that token.
      var bytes = new TextEncoder().encode(
        encodeURIComponent(source) + "\n"
      );
      var read = pendingRead;
      pendingRead = null;
      if (bytes.length > read.length) {
        fail(new Error("expression exceeds the REPL input buffer"));
        return;
      }
      read.buffer.set(bytes, read.offset);
      read.callback(null, bytes.length);
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
      input.focus();
    });

    var script = document.createElement("script");
    script.src = new URL("wasm_exec.js", window.location.href).href;
    script.onload = function () {
      var go = new Go();
      var decoder = new TextDecoder("utf-8");
      var originalRead = globalThis.fs.read;

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
        append(output, input, decoder.decode(buffer));
        return buffer.length;
      };

      globalThis.fs.read = function (
        fd, buffer, offset, length, position, callback
      ) {
        if (fd !== 0) {
          return originalRead(fd, buffer, offset, length, position, callback);
        }
        pendingRead = {
          buffer: buffer, offset: offset, length: length, callback: callback
        };
        input.focus();
      };

      WebAssembly.instantiateStreaming(
        fetch(new URL("gobb.wasm", window.location.href)),
        go.importObject
      ).then(function (result) {
        loading.hidden = true;
        shell.hidden = false;
        go.run(result.instance).then(function () {
          pendingRead = null;
          input.contentEditable = "false";
          append(output, input, "\nREPL exited.\n", "repl-muted");
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
