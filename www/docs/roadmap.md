---
title: Roadmap
description: Current Gobb implementation progress and upcoming milestones
---

# Roadmap

<div class="roadmap-summary">
  <div>
    <p class="summary-label">Current phase</p>
    <p class="summary-value">Java compatibility</p>
  </div>
  <div>
    <p class="summary-label">Completed milestones</p>
    <p class="summary-value">6 / 14</p>
  </div>
  <div>
    <p class="summary-label">Last updated</p>
    <p class="summary-value">July 27, 2026</p>
  </div>
</div>

<div class="overall-progress" aria-label="Overall roadmap progress">
  <span style="width: 43%"></span>
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
      <li class="task-done">Runtime <code>(ns ...)</code> support with Gloat <code>0.1.64</code> and Glojure <code>0.7.2</code></li>
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

## Current work

<div class="milestone milestone-active">
  <div class="milestone-marker">6</div>
  <div>
    <span class="status-badge status-active">In progress</span>
    <h3>Java compatibility</h3>
    <p>
      Implement BB's highest-value exposed Java surface through Glojure,
      gojava, and reusable Go adapters without introducing a JVM.
    </p>
    <ul class="task-list">
      <li>Rank the 583 exposed classes by BB and bundled-library demand</li>
      <li>Assign each class to Glojure, gojava, a Go adapter, or an explicit limitation</li>
      <li>Complete the core <code>java.lang</code>, file, path, and stream wave</li>
      <li>Cover constructors, methods, fields, and instance checks</li>
      <li>Add library-shaped Java compatibility fixtures</li>
      <li>Verify applicable behavior across native and Wasm targets</li>
      <li>Update the generated inventory with class-level evidence</li>
    </ul>
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
make inventory      # Generate the complete BB surface ledger and report
make repl-wasm      # Build the browser BB REPL with Gloat
make source-ledger  # Print the generated namespace ledger
```

`make clean` removes generated source and the Gobb binary. `make realclean`
also removes the downloaded Babashka source checkout.

## Known gaps

- Most Babashka implementation namespaces still depend on SCI, JVM classes,
  GraalVM substitutions, or libraries that have not been ported.
- Working-directory and explicit runtime classpaths work; project-derived
  classpaths, `bb.edn`, and `deps.edn` are not supported yet.
- Tasks, subprocesses, filesystem compatibility, networking, pods, and
  network REPL services remain unimplemented.
- The native and browser REPLs are focused proofs. Terminal line editing and
  command history, nREPL, socket REPL, browser filesystem loading, and the rest
  of BB's interactive surface remain.
- The current source reader wraps input in one `do` form. Shebang handling and
  exact BB reader edge cases remain for a later CLI-compatibility pass.

## Next concrete slice

Rank the exposed Java classes by demand, assign implementation ownership, and
complete the first class wave needed by core BB namespaces and libraries.

## Planned milestones

<div class="roadmap-grid">
  <article class="roadmap-card">
    <span>07</span>
    <h3>Projects and dependencies</h3>
    <p>Support <code>bb.edn</code>, <code>deps.edn</code>, Git, Maven source, and resources.</p>
  </article>
  <article class="roadmap-card">
    <span>08</span>
    <h3>Tasks and commands</h3>
    <p>Port BB tasks, exec functions, process pipelines, and aliases.</p>
  </article>
  <article class="roadmap-card">
    <span>09</span>
    <h3>Batteries included</h3>
    <p>Bring over BB's bundled libraries in tested dependency-shaped waves.</p>
  </article>
  <article class="roadmap-card">
    <span>10</span>
    <h3>Interactive services</h3>
    <p>Extend the basic terminal/browser REPLs with editing, nREPL, socket REPL, pods, and servers.</p>
  </article>
  <article class="roadmap-card">
    <span>11</span>
    <h3>Production build command</h3>
    <p>Finalize deterministic native and Wasm builds through Gloat.</p>
  </article>
  <article class="roadmap-card">
    <span>12</span>
    <h3>Website and live Gobb</h3>
    <p>Keep compatibility reports current and add a browser-Wasm playground.</p>
  </article>
  <article class="roadmap-card">
    <span>13</span>
    <h3>Compatibility closure</h3>
    <p>Close remaining BB gaps, establish performance baselines, and release.</p>
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
