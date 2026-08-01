---
title: Roadmap
description: Current Gobb implementation progress and upcoming milestones
---

# Roadmap

<div class="roadmap-summary">
  <div>
    <p class="summary-label">Current phase</p>
    <p class="summary-value">Production build command</p>
  </div>
  <div>
    <p class="summary-label">Completed milestones</p>
    <p class="summary-value">12 / 17</p>
  </div>
  <div>
    <p class="summary-label">Last updated</p>
    <p class="summary-value">July 31, 2026</p>
  </div>
</div>

<div class="overall-progress" aria-label="Overall roadmap progress">
  <span style="width: 71%"></span>
</div>

The first native Gobb executable is now working. It evaluates expressions,
files, and stdin through Glojure and is compiled to a native binary by Gloat.
The same architecture now powers native and live browser-Wasm REPLs using
Babashka's reusable REPL loop. A strict differential harness records current
BB compatibility for interpreted and compiled programs. Gobb remains an
architecture proof, not yet a general BB replacement.
The generated BB surface ledger now tracks 1,022 CLI, project, namespace,
library, Java-class, platform, upstream-test, and representative-program items
with explicit compatibility states and milestone assignments.

## Current work

<div class="milestone milestone-active">
  <div class="milestone-marker">12</div>
  <div>
    <span class="status-badge status-active">In progress</span>
    <h3>Production build command</h3>
    <p>
      Turn <code>gobb build</code> into a self-contained, reproducible native
      and Wasm compiler frontend through Gloat.
    </p>
    <ul class="task-list">
      <li class="task-done">Embed Gobb's selected runtime source graph in the Gobb executable</li>
      <li class="task-done">Select the transitive bundled namespace closure required by each program</li>
      <li class="task-done">Stage project, dependency, and selected bundled sources together for Gloat</li>
      <li class="task-done">Compile and run bundled <code>clojure.java.io</code> and <code>babashka.fs</code> use in native, WASI, and browser-Wasm project tests</li>
      <li>Allow project namespaces to override bundled namespaces without duplicate inputs</li>
      <li>Make repeated builds byte-for-byte reproducible; Gloat currently embeds its random build path</li>
      <li>Finalize production diagnostics and output validation for every target</li>
    </ul>
  </div>
</div>

## Completed foundation

<div class="milestone milestone-done">
  <div class="milestone-marker">0</div>
  <div>
    <span class="status-badge status-done">Complete</span>
    <h3>Repository foundation and Makes automation</h3>
    <p>
      Establish reproducible Makes-managed tooling, cleanup, website commands,
      and the initial project structure.
    </p>
    <ul class="task-list">
      <li class="task-done">Architecture and complete roadmap written</li>
      <li class="task-done">Initial MkDocs website created</li>
      <li class="task-done"><code>make serve</code> and <code>make publish</code> wired</li>
      <li class="task-done"><code>gobb.site</code> custom domain configured</li>
      <li class="task-done">Branded social cards generated from the hero artwork</li>
      <li class="task-done">Social-card image URLs cache-busted on each website build</li>
      <li class="task-done">Clean Make-managed tool provisioning validated</li>
      <li class="task-done">Runtime build and test targets added</li>
      <li class="task-done">Install, cross-platform release, and final website publication automated</li>
      <li class="task-done">OpenBSD and NetBSD <code>amd64</code>/<code>arm64</code> release artifacts verified</li>
      <li class="task-done">Live browser-Wasm REPL added to the website</li>
      <li class="task-done">Shareable browser REPL history URLs</li>
    </ul>
  </div>
</div>

## Completed execution architecture

<div class="milestone milestone-done">
  <div class="milestone-marker">1</div>
  <div>
    <span class="status-badge status-done">Complete</span>
    <h3>Prove the execution architecture</h3>
    <p>
      Expand the working native executable into the complete execution proof,
      including runtime namespace loading and the first Wasm artifact.
    </p>
    <ul class="task-list">
      <li class="task-done"><code>make build</code> produces <code>bin/gobb</code></li>
      <li class="task-done"><code>gobb -e EXPR [ARGS...]</code></li>
      <li class="task-done"><code>gobb FILE [ARGS...]</code></li>
      <li class="task-done">Source evaluation from stdin</li>
      <li class="task-done">Definitions persist across multiple forms</li>
      <li class="task-done">Useful CLI errors and nonzero exit status</li>
      <li class="task-done">Focused differential tests against BB</li>
      <li class="task-done">Runtime <code>require</code> from the working directory and <code>-cp</code>/<code>--classpath</code></li>
      <li class="task-done">WASI and browser-Wasm smoke artifacts</li>
      <li class="task-done">Babashka REPL loop compiled with Gloat for the browser</li>
      <li class="task-done"><code>gobb</code> starts a native <code>user=&gt;</code> REPL</li>
      <li class="task-done">Native multiline input, help, exit, and error recovery</li>
      <li class="task-done">Persistent REPL values and <code>*1</code> history verified</li>
      <li class="task-done">BB-compatible <code>babashka.version</code>, <code>babashka.file</code>, and initial <code>java.class.path</code> properties</li>
      <li class="task-done">REPL errors expose the underlying evaluator message</li>
      <li class="task-done">Runtime <code>(ns ...)</code> support verified with Gloat <code>0.1.67</code> and Glojure <code>0.7.3</code></li>
      <li class="task-done">Dependency-free <code>gobb build</code> compilation spike</li>
      <li class="task-done">One portable smoke program produces equivalent evaluated and compiled output</li>
      <li class="task-done">Native, WASI, and browser-Wasm smoke artifacts are executed by tests</li>
    </ul>
  </div>
</div>

## Completed compatibility harness

<div class="milestone milestone-done">
  <div class="milestone-marker">2</div>
  <div>
    <span class="status-badge status-done">Complete</span>
    <h3>Differential BB compatibility harness</h3>
    <p>
      Run the same fixtures under the pinned BB executable and the current
      Gobb, preserving complete diagnostics for every unexpected difference.
    </p>
    <ul class="task-list">
      <li class="task-done">Build a reusable fixture runner for pinned BB and current Gobb</li>
      <li class="task-done">Compare standard output</li>
      <li class="task-done">Compare standard error</li>
      <li class="task-done">Compare exit status</li>
      <li class="task-done">Compare structured exception data</li>
      <li class="task-done">Compare expected filesystem effects</li>
      <li class="task-done">Normalize isolated working directories and platform separators automatically</li>
      <li class="task-done">Provide explicit timestamp, process ID, and ephemeral-port normalizers</li>
      <li class="task-done">Persist complete reproduction diagnostics while keeping successful output concise</li>
      <li class="task-done">Cover initial CLI parsing and invocation</li>
      <li class="task-done">Cover initial reader behavior and printing</li>
      <li class="task-done">Cover namespace creation and switching</li>
      <li class="task-done">Cover dynamic Vars and script loading</li>
      <li class="task-done">Cover initial error and exit behavior</li>
      <li class="task-done">Support interpreted and <code>gobb build</code> compiled programs</li>
      <li class="task-done">Generate a website compatibility report with <code>make compat</code></li>
      <li class="task-done">Publish the compatibility summary and full diagnostics in CI</li>
    </ul>
  </div>
</div>

## Completed Glojure-native shell

<div class="milestone milestone-done">
  <div class="milestone-marker">3</div>
  <div>
    <span class="status-badge status-done">Complete</span>
    <h3>Glojure-native BB execution shell</h3>
    <p>
      Replace the host responsibilities that SCI performs inside BB with a
      Gobb layer built directly on Glojure.
    </p>
    <ul class="task-list">
      <li class="task-done">Current execution namespace and <code>*file*</code></li>
      <li class="task-done">Standard input, output, and error bindings</li>
      <li class="task-done"><code>*command-line-args*</code></li>
      <li class="task-done">Environment and working-directory state</li>
      <li class="task-done">Runtime load paths and classpath resources</li>
      <li class="task-done">Data readers and default data-reader behavior</li>
      <li class="task-done">Preloads and repeated <code>require</code></li>
      <li class="task-done"><code>:bb</code> and <code>:gobb</code> reader features</li>
      <li class="task-done">Expression, file, stdin, namespace-main, and exec-function invocation</li>
      <li class="task-done">Shutdown hooks and controlled exits</li>
      <li class="task-done">Source-aware error formatting without Go panic leakage</li>
      <li class="task-done">Native interrupt behavior where supported</li>
      <li class="task-done">Runtime and compiled execution agree on namespace, binding, and loader behavior</li>
      <li class="task-done">No Gobb-owned runtime source depends on SCI</li>
    </ul>
  </div>
</div>

## Completed compatibility inventory

<div class="milestone milestone-done">
  <div class="milestone-marker">4</div>
  <div>
    <span class="status-badge status-done">Complete</span>
    <h3>Compatibility ledger and test inventory</h3>
    <p>
      Turn BB's complete user-visible surface into a machine-readable backlog
      before starting the larger feature ports.
    </p>
    <ul class="task-list">
      <li class="task-done">Inventory BB CLI commands and options</li>
      <li class="task-done">Inventory <code>bb.edn</code>, <code>deps.edn</code>, tasks, and exec behavior</li>
      <li class="task-done">Inventory bundled namespaces and libraries</li>
      <li class="task-done">Inventory BB-exposed Java classes</li>
      <li class="task-done">Inventory REPL, pod, server, process, platform, and architecture features</li>
      <li class="task-done">Map upstream BB tests and representative programs</li>
      <li class="task-done">Assign every item a compatibility state and require test or implementation evidence for support claims</li>
      <li class="task-done">Record rationales for intentional differences and platform limits</li>
      <li class="task-done">Generate concise Markdown and website reports from the ledger</li>
    </ul>
  </div>
</div>

## Completed platform contract

<div class="milestone milestone-done">
  <div class="milestone-marker">5</div>
  <div>
    <span class="status-badge status-done">Complete</span>
    <h3>Platform capabilities</h3>
    <p>
      Define and test consistent host behavior for native, WASI, and
      browser-Wasm targets before higher-level libraries depend on it.
    </p>
    <ul class="task-list">
      <li class="task-done">Define the capability contract and target matrix</li>
      <li class="task-done">Classify filesystem, process, environment, clock, random, signal, and network behavior</li>
      <li class="task-done">Provide stable host-adapter boundaries for reusable runtime code</li>
      <li class="task-done">Return structured unsupported-capability errors</li>
      <li class="task-done">Test native, WASI, and browser-Wasm behavior independently</li>
      <li class="task-done">Record platform-limited and unavailable behavior in the generated inventory</li>
      <li class="task-done">Publish the platform capability matrix</li>
    </ul>
  </div>
</div>

## Completed Java compatibility wave

<div class="milestone milestone-done">
  <div class="milestone-marker">6</div>
  <div>
    <span class="status-badge status-done">Complete</span>
    <h3>Java compatibility</h3>
    <p>
      Implement BB's highest-value exposed Java surface through Glojure,
      gojava, and reusable Go adapters without introducing a JVM.
    </p>
    <ul class="task-list">
      <li class="task-done">Rank all 583 exposed classes by pinned BB source, test, and example demand</li>
      <li class="task-done">Assign every class to Glojure, gojava, a Go adapter, or an explicit limitation</li>
      <li class="task-done">Implement the first <code>java.lang</code>, file, path, and stream wave in Glojure</li>
      <li class="task-done">Cover constructors, methods, static fields, catches, and instance checks</li>
      <li class="task-done">Add BB/library-shaped Java compatibility fixtures</li>
      <li class="task-done">Verify the Glojure bridge across native, WASI, and browser-Wasm builds</li>
      <li class="task-done">Update the generated inventory with class-level ownership and evidence</li>
    </ul>
    <p>
      The generated <a href="../java-compatibility/">Java compatibility report</a>
      contains the full ranked ledger. The reusable bridge implementation and
      its AOT compiler support are tested in Glojure, and Gobb's compiled probe
      passes under native, WASI, and browser-Wasm.
    </p>
  </div>
</div>

## Completed projects and dependencies

<div class="milestone milestone-done">
  <div class="milestone-marker">7</div>
  <div>
    <span class="status-badge status-done">Complete</span>
    <h3>Projects and dependencies</h3>
    <p>
      Support the project files and dependency sources that BB uses to build
      its runtime classpath.
    </p>
    <ul class="task-list">
      <li class="task-done">Discover and read <code>bb.edn</code> and <code>deps.edn</code></li>
      <li class="task-done">Resolve project paths and recursive local dependencies</li>
      <li class="task-done">Resolve pinned Git dependencies into the Gobb cache</li>
      <li class="task-done">Resolve source-bearing Maven coordinates and common transitive POM dependencies without a JVM</li>
      <li class="task-done">Merge aliases, <code>-Sdeps</code>, and explicit <code>-cp</code>/<code>--classpath</code> entries</li>
      <li class="task-done">Load source and non-source resources from the resolved classpath</li>
      <li class="task-done">Report precise invalid configuration, coordinate, path, download, and extraction errors</li>
      <li class="task-done">Cache dependencies with <code>prepare</code> and inspect them with <code>print-deps</code></li>
      <li class="task-done">Exercise project loading under interpreted and compiled Gobb</li>
      <li class="task-done">Run dependency-bearing builds under native, WASI, and browser-Wasm</li>
    </ul>
    <p>
      See <a href="../projects/">Projects and dependencies</a> for discovery,
      cache, coordinate, build, and target behavior. Maven artifacts must
      contain portable source; Gobb does not execute JVM bytecode.
    </p>
  </div>
</div>

## Babashka source integration

Gobb builds against Babashka source without vendoring it and without adding
Git submodules to this repository.

- The default build downloads Babashka `v1.12.218` at commit
  `0fb349c414e717800be775ba9cb77c95a9eb700d` into the ignored Makes cache.
- `BABASHKA_DIR=/path/to/babashka` selects a development checkout instead.
- Babashka's own submodules will be initialized individually only when a
  selected capability needs one.
- A generated source staging tree gives each namespace exactly one provider
  and rejects duplicates or missing selected files.
- The machine-readable source ledger currently discovers all 67 core
  Babashka namespaces: 1 is compiled unchanged, 1 is adapted at staging time,
  and 65 are deferred pending compatibility work.
- The upstream proof namespace is `babashka.impl.exceptions`; the executable
  entry point is the Gobb-owned `gobb.cli`.
- `babashka.impl.clojure.main/repl` and its binding helper are projected from
  the pinned source into `gobb.bb-repl`. The staging ledger records the small
  set of JVM/SCI-specific adaptations rather than vendoring a fork.

The build interface is now:

```sh
make deps           # Download and verify pinned Babashka source
make build          # Build bin/gobb with Gloat
make test           # Run native and differential BB tests
make smoke          # Execute interpreted, native, WASI, and browser-Wasm proof
make capabilities   # Generate the runtime and website platform matrix
make capability-test # Execute the matrix under all three target families
make compat         # Generate the BB-vs-Gobb compatibility report
make test-lib_tests # Run Babashka's library test suite with Gobb
make test-examples  # Compile and exercise Babashka's examples with Gobb
make testing-report # Render committed upstream-suite result snapshots
make inventory      # Generate the complete BB surface ledger and report
make java-compat    # Rank and assign all exposed Java classes
make java-compat-test # Exercise the core class wave on all target families
make repl-wasm      # Build the browser BB REPL with Gloat
make source-ledger  # Print the generated namespace ledger
```

`make clean` removes generated source and the Gobb binary. `make realclean`
also removes the downloaded Babashka source checkout.

## Known gaps

- Most Babashka implementation namespaces still depend on SCI, JVM classes,
  GraalVM substitutions, or libraries that have not been ported.
- Project-derived classpaths work, but Maven bytecode-only libraries remain
  unavailable and advanced POM/BOM edge cases are not yet tools.deps-complete.
- Local EDN pods work, including `bb.edn` `:path` coordinates. Registry
  downloads, socket transport, JSON and Transit payloads, async handlers, and
  deferred pod namespaces remain.
- Native terminal editing, nREPL, and socket REPL are implemented. WASI and
  browser targets still cannot open raw network listeners.
- Core Ring HTTP serving works through `org.httpkit.server`; WebSockets,
  channels, and advanced http-kit options remain.
- The current source reader wraps input in one `do` form. Shebang handling and
  exact BB reader edge cases remain for a later CLI-compatibility pass.

## Next concrete slice

Finalize deterministic native and Wasm program builds through Gloat.

## Completed tasks and commands

<div class="milestone milestone-done">
  <div class="milestone-marker">8</div>
  <div>
    <span class="status-badge status-done">Complete</span>
    <h3>Tasks and commands</h3>
    <p>
      Port BB's task model, command selection, exec functions, and process
      pipelines onto the resolved project basis.
    </p>
    <ul class="task-list">
      <li class="task-done">Dispatch named tasks and the explicit <code>run</code> command</li>
      <li class="task-done">Evaluate global and per-task <code>:init</code> and <code>:requires</code></li>
      <li class="task-done">Order task graphs through <code>:depends</code></li>
      <li class="task-done">Support task arguments, <code>:private</code>, and <code>:doc</code></li>
      <li class="task-done">Run <code>:enter</code> and <code>:leave</code> hooks</li>
      <li class="task-done">Implement sequential and parallel task execution</li>
      <li class="task-done">Port process invocation and pipelines without JVM process classes</li>
      <li class="task-done">Preserve task exit status, streams, environment, and working directory</li>
      <li class="task-done">Exercise task and command behavior against pinned BB fixtures</li>
    </ul>
    <p>
      See <a href="../tasks/">Tasks and processes</a> for command, graph,
      hook, parallelism, process-option, and target behavior.
    </p>
  </div>
</div>

## Completed batteries-included wave

<div class="milestone milestone-done">
  <div class="milestone-marker">9</div>
  <div>
    <span class="status-badge status-done">Complete</span>
    <h3>Batteries included</h3>
    <p>
      Bring over BB's bundled libraries in tested dependency-shaped waves.
    </p>
    <ul class="task-list">
      <li class="task-done">
        Port <code>babashka.fs</code> and its filesystem dependency wave
        <ul>
          <li class="task-done">Pin and verify the upstream <code>babashka.fs</code> source revision</li>
          <li class="task-done">Implement Go-backed core paths, predicates, file I/O, copies, moves, links, and temporary files</li>
          <li class="task-done">Differentially test the core filesystem wave against pinned BB</li>
          <li class="task-done">Implement recursive visitors, glob and regex matching, and temporary-directory macro compatibility</li>
          <li class="task-done">Implement zip, unzip, gzip, and gunzip through Go's archive packages</li>
          <li class="task-done">Implement POSIX permission conversion, inspection, mutation, and creation options</li>
          <li class="task-done">Classify JVM <code>FileTime</code>-style attributes as a remaining compatibility-closure item</li>
        </ul>
      </li>
      <li class="task-done">
        Port the public <code>babashka.process</code> API onto the native process adapter
        <ul>
          <li class="task-done">Pin and verify the upstream <code>babashka.process</code> source revision</li>
          <li class="task-done">Implement tokenization, process, builder, pipeline, <code>sh</code>, <code>shell</code>, and <code>$</code> entry points</li>
          <li class="task-done">Differentially test results, environment, working directory, failures, and pipelines against pinned BB</li>
          <li class="task-done">Implement string and byte capture, file redirects, pre-start, exit, and shutdown callbacks</li>
          <li class="task-done">Classify direct stream records, true process destruction, and replace-image <code>exec</code> as compatibility-closure items</li>
        </ul>
      </li>
      <li class="task-done">
        Port curl, HTTP client, and portable networking helpers
        <ul>
          <li class="task-done">Pin and verify the upstream <code>babashka.curl</code> source revision</li>
          <li class="task-done">Implement native methods, headers, parameters, bodies, redirects, byte responses, and error maps</li>
          <li class="task-done">Differentially test file URLs and local HTTP requests without a public network dependency</li>
          <li class="task-done">Pin and implement the core <code>babashka.http-client</code> client, method, async, redirect, and function-client surface</li>
          <li class="task-done">Record live streaming, custom interceptors, WebSocket, JVM constructors, and browser Fetch as target-specific closure work</li>
        </ul>
      </li>
      <li class="task-done">
        Fill out <code>clojure.java.io</code> compatibility used by bundled libraries
        <ul>
          <li class="task-done">Implement Go-backed files, streams, readers, writers, copying, resources, parents, and deletion</li>
          <li class="task-done">Differentially test the core public surface against pinned BB</li>
          <li class="task-done">Track all Gobb-owned source namespaces as Make rebuild inputs</li>
          <li class="task-done">Record true URL objects, character encodings, and JVM protocol-extension edge cases for compatibility closure</li>
        </ul>
      </li>
      <li class="task-done">
        Bring over common data libraries for JSON, CSV, YAML, and Transit
        <ul>
          <li class="task-done">Pin <code>clojure.data.csv</code> 1.0.0 as the behavioral source reference</li>
          <li class="task-done">Implement and differentially test core CSV reading, writing, separators, quoting, and newlines</li>
          <li class="task-done">Record alternate CSV quote characters, custom quote predicates, and lazy reads as partial behavior</li>
          <li class="task-done">Pin Cheshire 6.2.0 and implement its core JSON parsing, generation, stream, and sequence APIs</li>
          <li class="task-done">Record Cheshire factories, custom encoders, Smile, and strict duplicate-key detection as partial behavior</li>
          <li class="task-done">Pin clj-yaml 1.0.29 and implement its high-level parsing, generation, key conversion, and multi-document APIs</li>
          <li class="task-done">Record SnakeYAML-specific low-level, marked-node, and unsafe Java APIs as non-portable partial behavior</li>
          <li class="task-done">Pin transit-clj 1.1.357 and implement common Transit JSON stream values</li>
          <li class="task-done">Record Transit cache compaction, custom handlers, tagged values, and non-JSON formats as partial behavior</li>
        </ul>
      </li>
      <li class="task-done">
        Port CLI, template, logging, and source-rewriting libraries in dependency order
        <ul>
          <li class="task-done">Compile pinned external <code>.clj</code>/<code>.cljc</code> source trees without vendoring them into Gobb</li>
          <li class="task-done">Pin and differentially test the core <code>babashka.cli</code> 0.8.67 API</li>
          <li class="task-done">Pin Hiccup 2.0.0-RC1 and implement core Hiccup/Hiccup2 rendering</li>
          <li class="task-done">Record Hiccup page, form, middleware, URI, and compiler optimization APIs as partial behavior</li>
          <li class="task-done">Implement and differentially test common tools.logging and Timbre behavior</li>
          <li class="task-done">Compile and differentially test pinned Clojure 1.12.4 <code>clojure.zip</code> source</li>
          <li class="task-done">Pin rewrite-clj 1.2.54 and assign its Glojure <code>lang.ArityFn</code> AOT blocker to compatibility closure</li>
        </ul>
      </li>
      <li class="task-done">Run each delivered library wave against pinned BB and compiled Gobb</li>
      <li class="task-done">Record native, WASI, browser, JVM-only, and compiler-dependent limitations in the generated inventory</li>
    </ul>
  </div>
</div>

## Completed website and live Gobb

<div class="milestone milestone-done">
  <div class="milestone-marker">10</div>
  <div>
    <span class="status-badge status-done">Complete</span>
    <h3>Website and live Gobb</h3>
    <p>
      Publish the tested project state and a real browser-Wasm Gobb experience.
    </p>
    <ul class="task-list">
      <li class="task-done">Serve the MkDocs site at <code>https://gobb.site/</code></li>
      <li class="task-done">Generate compatibility, inventory, Java, and platform reports from tested ledgers</li>
      <li class="task-done">Compile Babashka's reusable REPL loop to browser Wasm with Gloat</li>
      <li class="task-done">Provide a responsive terminal-style browser REPL</li>
      <li class="task-done">Replay and share complete REPL state through copyable URLs</li>
      <li class="task-done">Publish branded, cache-busted social cards</li>
      <li class="task-done">Keep <code>make serve</code>, <code>make site</code>, and <code>make publish</code> reproducible</li>
    </ul>
  </div>
</div>

## Completed interactive services

<div class="milestone milestone-done">
  <div class="milestone-marker">11</div>
  <div>
    <span class="status-badge status-done">Complete</span>
    <h3>Interactive services</h3>
    <p>
      Extend Gobb from basic evaluation loops into native interactive and
      network services while keeping host limitations explicit.
    </p>
    <ul class="task-list">
      <li class="task-done">Use Glojure's readline terminal with editing, completion, highlighting, and persistent <code>~/.gobb_history</code></li>
      <li class="task-done">Implement <code>--socket-repl</code> and <code>socket-repl</code> with BB-compatible defaults</li>
      <li class="task-done">Implement <code>--nrepl-server</code> and <code>nrepl-server</code> with bencoded evaluation responses</li>
      <li class="task-done">Expose programmatic <code>babashka.nrepl.server</code> start and stop operations</li>
      <li class="task-done">Expose named <code>clojure.core.server</code> socket REPL lifecycle operations</li>
      <li class="task-done">Load, invoke, and unload local EDN subprocess pods</li>
      <li class="task-done">Load local <code>:path</code> pods declared in <code>bb.edn</code></li>
      <li class="task-done">Serve Ring handlers through the core <code>org.httpkit.server</code> API</li>
      <li class="task-done">Test socket REPL, nREPL, pod, project-pod, and HTTP behavior against real native processes</li>
      <li class="task-done">Record registry pods, alternate payload formats, WebSockets, and non-native listeners as explicit compatibility limits</li>
    </ul>
  </div>
</div>

## Planned milestones

<div class="roadmap-grid">
  <article class="roadmap-card">
    <span>13</span>
    <h3>Upstream conformance harness</h3>
    <p>
      Run Babashka's library tests and examples against Gobb, keep complete
      diagnostics, and publish committed full-suite snapshots.
    </p>
    <ul>
      <li>Add a filterable <code>make test-lib_tests</code> target</li>
      <li>Compile every upstream <code>.clj</code> example</li>
      <li>Run safe deterministic examples against BB and Gobb</li>
      <li>Track explicit environmental and interactive waivers</li>
      <li>Continue through all cases and fail after recording the summary</li>
      <li>Detect additions or removals in the upstream example set</li>
      <li>Publish current results on the Testing page without rerunning tests</li>
    </ul>
  </article>
  <article class="roadmap-card">
    <span>14</span>
    <h3>Library-test closure</h3>
    <p>
      Make every applicable test in Babashka's
      <code>test-resources/lib_tests</code> corpus pass, with documented
      waivers for genuinely non-portable behavior.
    </p>
  </article>
  <article class="roadmap-card">
    <span>15</span>
    <h3>Example-program closure</h3>
    <p>
      Close compilation and execution gaps across Babashka's examples and
      replace temporary waivers whenever portable automation is possible.
    </p>
  </article>
  <article class="roadmap-card">
    <span>16</span>
    <h3>Final compatibility and release</h3>
    <p>
      Close the remaining BB CLI, namespace, Java, and upstream-test gaps,
      establish performance baselines, and release the full implementation.
    </p>
  </article>
</div>

## What counts as full Gobb?

A release can claim full Gobb status when:

- applicable user-visible BB tests pass or have documented waivers;
- every BB-exposed Java class has a compatibility disposition;
- tasks, dependencies, pods, REPLs, bundled libraries, and build mode have
  end-to-end coverage;
- native and Wasm differences are tested and documented;
- no execution path depends on SCI, a JVM, or GraalVM;
- this site reflects the tested state of the release.
