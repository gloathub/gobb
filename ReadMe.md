# Gobb

**Go + BB**

Gobb (pronounced "Joby"; rhymes with "Moby") is the
[Babashka](https://github.com/babashka/babashka) source code compiled by
[Gloat](https://gloathub.org/) using the Go hosted
[Glojure](https://github.com/glojurelang/glojure) runtime; without SCI,
GraalVM, or a JVM.

The project aims to make as much existing BB code and behavior work as
possible while adding first-class native and WebAssembly compilation:

```text
gobb build script.clj -o app --platform linux/amd64
```

> [!IMPORTANT]
> Gobb is an early architecture proof, not yet a general BB replacement.
> The first native executable evaluates expressions, files, and stdin through
> Glojure and is compiled by Gloat.

## Install

You can install Gobb by downloading a [pre-built release binary](
https://github.com/clojurestar/gobb/releases/) and adding it to a directory in
your `PATH`, or by simply running the `make install` command.

Building and installing Gobb requires only `git`, `make`, `curl`, and a `bash`
binary.
You do not need Go, BB, Glojure, Gloat, GraalVM, Java, or a JVM pre-installed.

```bash
git clone https://github.com/clojurestar/gobb
make -C gobb install
```

The default installation PREFIX is `$HOME/.local`, or `/usr/local` when
running as root.
Ensure `$PREFIX/bin` is in your `PATH` for a user installation.

Set an explicit prefix when needed:

```bash
make -C gobb install PREFIX=/some/path
```

Prebuilt archives for supported platforms are available from
[GitHub Releases](https://github.com/clojurestar/gobb/releases).


## Gobb's Goals

The main point of this project is to show that Oracle GraalVM's `native-image`
compiler is no longer needed to compile Clojure code to native binaries and
shared libraries.
The point is made by taking one of Clojure's most popular projects and using
Gloat to do far more than GraalVM can, with software that is truly open.

Oracle GraalVM (GFTC licensed) `native-image` compiler:
* Is infamously slow (minutes vs seconds)
* Doesn't cross-compile
* Only supports `Windows/amd64`, `macOS/arm64`, and `Linux/amd64+arm64`.
  * No mac/Intel, no Wasm, no BSD, no 32-bit
* Produces images that cannot load Clojure code dynamically
  * Requires the SCI runtime reimplementation of the Clojure runtime
* Is not OSI open source licensed

> [!NOTE]
> The first four limits apply to every GraalVM edition, including Community and
> Mandrel.
> The licensing limit applies to Oracle GraalVM specifically, which is what
> Babashka's own release workflow builds with.

Gloat/Glojure solves all of these problems.
It compiles very fast and cross-compiles to ~25 platforms including mac/Intel,
BSDs, Wasm and 32-bit (Gobb releases ship [prebuilt binaries for 15 of those](
https://github.com/clojurestar/gobb/releases/)).
It replaces the need for SCI with the Glojure runtime which is a faithful port
of Clojure hosted on the Go language.
All under an open, OSI-approved license.

Gobb aims to:

- Create a `bb` that is as compatible as possible with Babashka, built on Go.
- Build from pinned Babashka source code and move forward in sync with bb.
- Use the fully Clojure-capable Glojure runtime instead of SCI.
- Build with Gloat rather than GraalVM.
- Run the same project dynamically or compile it to binary through Gloat.
- Offer full Go host interop, plus a growing subset of commonly used Java
  classes via the [gojava](https://github.com/gloathub/gojava) project.
- Test compatibility against a pinned BB executable and publish the measured
  progress.


## Architecture

```text
BB-compatible CLI and project behavior
                  |
          Gobb host layer
                  |
       Glojure runtime/compiler
                  |
       gojava and Go adapters
                  |
           Go host platform
                  |
       Gloat build orchestration
```

Glojure supplies Clojure reading, evaluation, namespaces, Vars, dynamic
bindings, loading, and runtime compilation.
Gobb supplies BB-compatible CLI policy, tasks, dependency handling, bundled
features, platform capabilities, and packaging.

Reusable runtime, compiler, and Java compatibility improvements belong in
Glojure, gojava, or Gloat rather than Gobb-specific forks.

See the [implementation details](https://gobb.site/implementation/) for the
full design.

## Project Status

Gobb has completed its repository foundation and started native execution:

- [x] Architecture and implementation roadmap
- [x] Makes-managed project and website automation
- [x] MkDocs project website
- [x] Pinned, Make-managed Babashka source integration
- [x] First Gloat-compiled Glojure runtime executable
- [x] Initial source ledger and differential BB tests
- [x] Browser-Wasm BB REPL proof at [gobb.site/repl](https://gobb.site/repl/)
- [x] Babashka-derived native CLI REPL
- [x] BB-compatible Glojure execution shell
- [x] Generated BB surface inventory and compatibility backlog
- [ ] Project dependencies, tasks, pods, and bundled libraries
- [ ] Production `gobb build` command

Follow the [live roadmap](https://gobb.site/roadmap/) for current progress.

## Build and Test

Build the native executable:

```bash
make build
bin/gobb -e '(+ 1 2)'
```

Start the native REPL:

```text
$ bin/gobb
Gobb v0.1.1
Babashka v1.12.218
Type :repl/help for help
user=>
```

The Makefile downloads and verifies the pinned Babashka source checkout in the
ignored local cache.
Gobb does not use Git submodules or commit copied Babashka source.
For development, select an existing checkout with:

```bash
make build BABASHKA_DIR=~/src/babashka
```

Run the native and differential BB tests with `make test`.
Generate the BB-vs-Gobb compatibility report with `make compat`. Add
`STRICT=1` to fail when the harness finds an unexpected result:

```bash
make compat
make compat STRICT=1
```

Generate and validate the complete BB surface inventory with `make inventory`.
It discovers the pinned BB CLI, built-in namespaces and dependencies, exposed
Java classes, upstream tests, examples, and platform features. Gobb's
machine-readable states, milestone assignments, rationales, and evidence live
in `compat/inventory.edn`; the generated full ledger is
`compat/ledger.edn`.

CI regenerates the inventory and strict comparison, rejects tracked-report
drift, publishes both Markdown summaries in the workflow run, and retains the
machine-readable ledger and per-fixture diagnostics as artifacts.

Print the generated Babashka namespace compatibility ledger with `make
source-ledger`.

Runtime namespaces load from the working directory by default. Add source
roots with BB-compatible classpath options:

```bash
gobb -cp src -e "(require '[example.core :as example]) (example/run)"
gobb --classpath src:lib script.clj
```

Classpath lists use the platform path separator and are reflected in the
`java.class.path` system property.

Gobb supports BB-style namespace-main, qualified-main, and exec-function
invocation:

```bash
gobb -cp src -m example.core alpha beta
gobb -cp src -m example.core/alternate alpha
gobb -cp src -x example.core/run --name Gobb --count 2
```

Use `BABASHKA_PRELOADS` or `--init FILE` to establish runtime state before
evaluation. Reader conditionals recognize `:clj`, `:bb`, and the additional
`:gobb` feature. Runtime classpaths also provide namespaces,
`data_readers.clj`, and resources through `clojure.java.io/resource`.

## Build Programs

The initial `gobb build` spike compiles a dependency-free namespace containing
`-main` through Gloat:

```bash
gobb build src/example/core.clj -o example
gobb build src/example/core.clj -o example.wasm --platform wasip1/wasm
gobb build src/example/core.clj -o example.wasm --platform js/wasm
```

Gobb uses the `gloat` executable on `PATH`. Set `GOBB_GLOAT=/path/to/gloat`
to select it explicitly. Project dependencies, resources, and `bb.edn` build
configuration remain later milestones.

## Makefile Targets

| Target | Description |
| --- | --- |
| `build` | Build the native `bin/gobb` executable with Gloat. |
| `install` | Install `gobb` under `PREFIX/bin`. |
| `deps` | Download and verify the pinned Babashka source checkout. |
| `stage` | Generate the source tree selected from Gobb and Babashka. |
| `test` | Build Gobb and run native and differential BB tests. |
| `smoke` | Build and execute equivalent interpreted, native, WASI, and browser-Wasm smoke programs. |
| `inventory` | Generate and validate the complete machine-readable BB surface ledger and website report. |
| `repl-wasm` | Compile the Babashka-derived browser REPL and install its Go Wasm runtime. |
| `source-ledger` | Print the generated Babashka namespace compatibility ledger. |
| `release-prep VERSION=X.Y.Z` | Update `VERSION` and prepend generated release notes to `Changes`. |
| `release-dist VERSION=X.Y.Z` | Build the cross-platform release archives and checksums. |
| `release VERSION=X.Y.Z` | Test, package, tag, push, and create the GitHub release. |
| `site` | Build the MkDocs website in strict mode. |
| `serve` | Serve the website locally with live reload. |
| `publish` | Build and publish the website to the `gh-pages` branch. |
| `clean` | Remove the Gobb binary, staged source, and generated website. |
| `realclean` | Also remove downloaded Babashka source and the website environment. |
| `distclean` | Also remove the locally bootstrapped Makes checkout. |

`serve-www` and `publish-www` are aliases for `serve` and `publish`.
Set `BABASHKA_DIR` to use a local Babashka checkout or `GLOAT_DIR` to use a
local Gloat checkout.

## Releases

Release archives contain the executable, README, changelog, Gobb license,
third-party notices, and upstream license texts. Unix, macOS, FreeBSD, and
Wasm artifacts use `.tar.gz`; Windows artifacts use `.zip`. SHA-256 checksums
are published alongside the archives.

### Release Platforms

Gobb builds release binaries for:

| Platform | Architectures | Release identifiers |
| --- | --- | --- |
| Linux | `amd64`, `arm64`, ARMv6 | `linux_amd64`, `linux_arm64`, `linux_armv6` |
| macOS | `amd64`, `arm64` | `darwin_amd64`, `darwin_arm64` |
| Windows | `amd64`, `arm64` | `windows_amd64`, `windows_arm64` |
| FreeBSD | `amd64`, `arm64` | `freebsd_amd64`, `freebsd_arm64` |
| OpenBSD | `amd64`, `arm64` | `openbsd_amd64`, `openbsd_arm64` |
| NetBSD | `amd64`, `arm64` | `netbsd_amd64`, `netbsd_arm64` |
| WASI | `wasm` | `wasip1_wasm` |
| Browser WebAssembly | `wasm` | `js_wasm` |

Prepare and review a release, then publish it:

```bash
make release-prep VERSION=0.1.0
git add VERSION Changes
git commit -m 'Version 0.1.0'
make release VERSION=0.1.0
```

`make release` can perform the preparation and version commit itself when
starting from a clean checkout. After publishing the GitHub release and its
assets, it publishes the website as the final step.

## Website

The project website is built with MkDocs Material.
All tools are installed locally through
[Makes](https://github.com/makeplus/makes).

Start the development server:

```bash
make serve
```

The site is available at <http://127.0.0.1:8000/>.

Build the strict production site:

```bash
make site
```

Publish the generated site to the `gh-pages` branch:

```bash
make publish
```

The public site is <https://gobb.site/>.
Its [live REPL](https://gobb.site/repl/) runs Babashka's reusable REPL loop,
adapted for Gobb and compiled to browser WebAssembly by Gloat. Evaluation is
performed by Glojure rather than SCI.

## Repository Layout

```text
.
├── Makefile           # Makes-managed build, tests, and website automation
├── ReadMe.md          # Project overview
├── src/               # Gobb source and Babashka source-selection manifest
├── test/              # Native and differential BB tests
├── util/              # Source staging tools
└── www/               # MkDocs source and website automation
```

## License

Gobb is released under the
[Eclipse Public License 2.0](License) (`EPL-2.0`).

Code adapted from upstream projects retains its original copyright and
attribution notices.

## Related Projects

- [Babashka](https://github.com/babashka/babashka) -
  Behavior and compatibility target
- [Glojure](https://github.com/glojurelang/glojure) -
  Full Clojure runtime in Go
- [Gloat](https://github.com/gloathub/gloat) -
  Clojure-to-Go compilation and cross-compilation
- [gojava](https://github.com/gloathub/gojava) -
  JVM-faithful APIs implemented in Go
- [Makes](https://github.com/makeplus/makes) -
  Reproducible dependency automation
