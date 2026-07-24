---
title: Roadmap
description: Current Gobb implementation progress and upcoming milestones
---

# Roadmap

<div class="roadmap-summary">
  <div>
    <p class="summary-label">Current phase</p>
    <p class="summary-value">Native execution</p>
  </div>
  <div>
    <p class="summary-label">Completed milestones</p>
    <p class="summary-value">1 / 14</p>
  </div>
  <div>
    <p class="summary-label">Last updated</p>
    <p class="summary-value">July 24, 2026</p>
  </div>
</div>

<div class="overall-progress" aria-label="Overall roadmap progress">
  <span style="width: 12%"></span>
</div>

The first native Gobb executable is now working. It evaluates expressions,
files, and stdin through Glojure and is compiled to a native binary by Gloat.
This is an architecture proof, not yet a general BB replacement.

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
      <li class="task-done">Clean Make-managed tool provisioning validated</li>
      <li class="task-done">Runtime build and test targets added</li>
    </ul>
  </div>
</div>

## Current work

<div class="milestone milestone-active">
  <div class="milestone-marker">2</div>
  <div>
    <span class="status-badge status-active">In progress</span>
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
      <li>Runtime <code>require</code> and classpath loading</li>
      <li>WASI and browser-Wasm smoke artifacts</li>
    </ul>
  </div>
</div>

<div class="milestone milestone-next">
  <div class="milestone-marker">1</div>
  <div>
    <span class="status-badge status-next">Started</span>
    <h3>Compatibility ledger and test inventory</h3>
    <p>
      Inventory BB's CLI, configuration, bundled namespaces, Java classes,
      platforms, and tests. The source namespace portion now exists; the
      broader user-visible compatibility inventory remains.
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
  Babashka namespaces: 1 is compiled unchanged from upstream and 66 are
  deferred pending compatibility work.
- The upstream proof namespace is `babashka.impl.exceptions`; the executable
  entry point is the Gobb-owned `gobb.cli`.

The build interface is now:

```sh
make deps           # Download and verify pinned Babashka source
make build          # Build bin/gobb with Gloat
make test           # Run native and differential BB tests
make source-ledger  # Print the generated namespace ledger
```

`make clean` removes generated source and the Gobb binary. `make realclean`
also removes the downloaded Babashka source checkout.

## Known gaps

- Most Babashka implementation namespaces still depend on SCI, JVM classes,
  GraalVM substitutions, or libraries that have not been ported.
- Runtime `require`, project classpaths, `bb.edn`, and `deps.edn` are not
  supported yet.
- Tasks, subprocesses, filesystem compatibility, networking, pods, and REPL
  services remain unimplemented.
- Native builds are verified; Wasm builds have not yet been attempted.
- The current source reader wraps input in one `do` form. Shebang handling and
  exact BB reader edge cases remain for a later CLI-compatibility pass.

## Next concrete slice

Add runtime `require` and ordered classpath loading to the native executable,
extend the differential harness for namespace loading, then compile the same
smoke program to WASI.

## Planned milestones

<div class="roadmap-grid">
  <article class="roadmap-card">
    <span>03</span>
    <h3>Differential BB harness</h3>
    <p>Compare BB and Gobb output, errors, exit status, and side effects.</p>
  </article>
  <article class="roadmap-card">
    <span>04</span>
    <h3>Glojure-native BB shell</h3>
    <p>Replace SCI's host responsibilities with Glojure-native execution.</p>
  </article>
  <article class="roadmap-card">
    <span>05</span>
    <h3>Platform capabilities</h3>
    <p>Define consistent native, WASI, and browser-Wasm host behavior.</p>
  </article>
  <article class="roadmap-card">
    <span>06</span>
    <h3>Java compatibility</h3>
    <p>Expand gojava and Glojure support based on BB and library demand.</p>
  </article>
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
    <p>Add terminal REPL, nREPL, socket REPL, pods, and servers.</p>
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
