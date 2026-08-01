---
title: Projects and dependencies
description: Gobb project discovery, dependency coordinates, caches, and build behavior
---

# Projects and dependencies

Gobb resolves BB-style project configuration without starting a JVM. The
resolved source and resource roots feed both runtime evaluation and
`gobb build`.

## Project discovery

With no explicit option, Gobb looks for `deps.edn` and `bb.edn` beside an
invoked script, then in the current directory. If both files exist in the
selected directory, `bb.edn` is merged after `deps.edn`.

```console
$ gobb --config config/ci.edn -e '(require (quote app.core))'
$ gobb --deps-root /workspace --config config/ci.edn app.clj
```

Relative paths and local dependencies are resolved from the configuration
directory, or from `--deps-root` when supplied. The selected configuration is
available as the `babashka.config` system property.

## Paths, aliases, and overrides

Gobb supports project `:paths`, `:classpath`, `:deps`, and the standard alias
path/dependency keys:

- `:extra-paths` and `:replace-paths`;
- `:extra-deps`, `:replace-deps`, and `:override-deps`;
- `-A:dev:test` or `-A dev,test`;
- `-Sdeps EDN`.

An explicit `-cp` or `--classpath` replaces the project-derived classpath,
matching BB's precedence.

## Dependency coordinates

```clojure
{:paths ["src" "resources"]
 :deps
 {local/tool {:local/root "../tool"}
  io.github.example/tool
  {:git/url "https://github.com/example/tool.git"
   :git/sha "0123456789abcdef"}
  medley/medley {:mvn/version "1.4.0"}}}
```

Local dependencies recursively read their own `bb.edn` or `deps.edn`. Git
dependencies require a pinned SHA or tag and are checked out once. Maven
artifacts are resolved from configured repositories, Maven Central, and
Clojars; common compile/runtime transitive dependencies, properties,
dependency management, scopes, optional dependencies, and exclusions are
interpreted from POM files.

Gobb extracts Maven artifacts into source load roots. Portable Clojure source
and resources work; JVM bytecode-only artifacts do not become executable
without a JVM and remain an explicit architecture limit.

Maven resolution uses `curl` and `unzip` on the native host. They are only
needed when a project declares Maven dependencies; installing and running Gobb
itself does not require them.

## Cache

Git checkouts, Maven artifacts, and extracted sources use:

1. `GOBB_CACHE`;
2. `$XDG_CACHE_HOME/gobb`;
3. `$HOME/.cache/gobb`;
4. the operating-system temporary directory.

Run `gobb prepare` to resolve and cache a project's dependencies without
executing code. Inspect the result with:

```console
$ gobb print-deps --format deps
$ gobb print-deps --format classpath
```

## Compiled projects

`gobb build` stages the resolved source graph and gives it to Gloat. The same
project fixture is tested as a native executable, a WASI module, and browser
Wasm:

```console
$ gobb build src/app.clj -o app
$ gobb build src/app.clj -o app.wasm --platform wasip1/wasm
$ gobb build src/app.clj -o app.wasm --platform js/wasm
```

The Gobb executable carries the portable source graph used by its own runtime.
During a build, Gobb scans the project sources, selects referenced bundled
namespaces, follows their transitive namespace dependencies, and stages that
closure beside the project's own sources. The resulting program can therefore
use bundled namespaces such as `clojure.java.io` and `babashka.fs` without a
separate Gobb source checkout at build time. A namespace supplied by the
project or one of its dependencies takes precedence over the bundled version;
Gobb omits the bundled source and its bundled-only dependency closure.

Build target names must use `OS/ARCH`. Gobb verifies that Gloat creates the
requested file and removes its temporary source staging directory after both
successful and failed builds.

Dependency download and Git operations happen on the native build host.
Compiled WASI and browser artifacts contain the selected portable source and
do not perform dynamic dependency resolution in the sandbox or browser.
