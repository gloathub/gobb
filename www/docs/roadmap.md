---
title: Roadmap
description: Current Gobb implementation progress and upcoming milestones
---

# Roadmap

<div class="roadmap-summary">
  <div>
    <p class="summary-label">Current phase</p>
    <p class="summary-value">Foundation</p>
  </div>
  <div>
    <p class="summary-label">Completed milestones</p>
    <p class="summary-value">0 / 14</p>
  </div>
  <div>
    <p class="summary-label">Last updated</p>
    <p class="summary-value">July 24, 2026</p>
  </div>
</div>

<div class="overall-progress" aria-label="Overall roadmap progress">
  <span style="width: 4%"></span>
</div>

The architecture and detailed roadmap are written. Repository automation and
this website are the first implementation work; the Gobb runtime itself has
not yet been built.

## Current work

<div class="milestone milestone-active">
  <div class="milestone-marker">0</div>
  <div>
    <span class="status-badge status-active">In progress</span>
    <h3>Repository foundation and Makes automation</h3>
    <p>
      Establish reproducible Makes-managed tooling, cleanup, website commands,
      and the initial project structure.
    </p>
    <ul class="task-list">
      <li class="task-done">Architecture and complete roadmap written</li>
      <li class="task-done">Initial MkDocs website created</li>
      <li class="task-done"><code>make serve</code> and <code>make publish</code> wired</li>
      <li>Validate clean tool provisioning and GitHub Pages publication</li>
      <li>Add runtime build and test targets as implementation starts</li>
    </ul>
  </div>
</div>

<div class="milestone milestone-next">
  <div class="milestone-marker">1</div>
  <div>
    <span class="status-badge status-next">Up next</span>
    <h3>Compatibility ledger and test inventory</h3>
    <p>
      Inventory BB's CLI, configuration, bundled namespaces, Java classes,
      platforms, and tests in one machine-readable compatibility ledger.
    </p>
  </div>
</div>

<div class="milestone milestone-next">
  <div class="milestone-marker">2</div>
  <div>
    <span class="status-badge status-next">Then</span>
    <h3>Prove the execution architecture</h3>
    <p>
      Produce the first Gloat-compiled Gobb executable that evaluates
      expressions, files, and stdin through the full Glojure runtime.
    </p>
  </div>
</div>

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
