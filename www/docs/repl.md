---
title: Gobb Browser REPL
description: Run Gobb's Babashka-derived REPL in your browser
hide:
- navigation
- toc
---

This is Babashka's reusable REPL loop, compiled to browser Wasm by Gloat.
Forms are read and evaluated by the full Glojure runtime.
There is no SCI, JVM, or GraalVM involved.

<div id="repl-loading">
  <span class="repl-spinner"></span>
  Loading Gobb…
</div>

<div id="repl-shell" hidden>
  <div id="repl-toolbar">
    <span>Gobb · Go + bb!</span>
    <button id="repl-clear" type="button">Clear</button>
  </div>
  <div id="repl-terminal"
       onclick="if (!window.getSelection().toString()) document.getElementById('repl-input').focus()">
    <div id="repl-output">
      <span id="repl-input" contenteditable="true"
            aria-label="REPL input" autocomplete="off"
            autocapitalize="off" spellcheck="false"></span>
    </div>
  </div>
  <p class="repl-hint">
    Enter evaluates · Shift+Enter adds a line · ↑/↓ recalls history ·
    <code>:repl/help</code> shows help
  </p>
</div>
