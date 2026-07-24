# Gobb

**Go + BB**

Gobb is a Go-native implementation of the [Babashka](https://babashka.org/)
experience.
It uses [Gloat](https://gloathub.org/) to build native binaries and
[Glojure](https://github.com/glojurelang/glojure) as its full Clojure runtime,
without SCI, GraalVM, or a JVM.

The project aims to make as much existing BB code and behavior work as
possible while adding first-class native and WebAssembly compilation:

```text
gobb build script.clj -o app --platform linux/amd64
```

> [!IMPORTANT]
> Gobb is an early architecture proof, not yet a general BB replacement.
> The first native executable evaluates expressions, files, and stdin through
> Glojure and is compiled by Gloat.

## Goals

- Create a compatible-as-possible bb built on Go.
- Use the fully Clojure capable Glojure runtime instead of SCI.
- Build with Gloat rather than GraalVM.
  - Builds in seconds vs minutes
  - Supports myriad platforms beyond GraalVM (including Wasm)
- Run the same project dynamically or compile it through Gloat.
- Target Linux, macOS, Windows, WASI, and browser WebAssembly.
- Grow support for Java-dependent Clojure libraries through
  [gojava](https://github.com/gloathub/gojava) and reusable Go-backed
  compatibility shims.
- Test compatibility against a pinned BB executable and publish the measured
  progress.

General Java bytecode execution is not a goal.
Libraries that refer to Java classes become compatible as those classes gain
Glojure/gojava or native Go implementations.

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
- [ ] BB-compatible execution shell
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
Print the generated Babashka namespace compatibility ledger with `make
source-ledger`.

Install Gobb for the current user:

```bash
make install
```

The default prefix is `$HOME/.local`, or `/usr/local` when running as root.
Override it explicitly when needed:

```bash
make install PREFIX=/some/path
```

## Makefile Targets

| Target | Description |
| --- | --- |
| `build` | Build the native `bin/gobb` executable with Gloat. |
| `install` | Install `gobb` under `PREFIX/bin`. |
| `deps` | Download and verify the pinned Babashka source checkout. |
| `stage` | Generate the source tree selected from Gobb and Babashka. |
| `test` | Build Gobb and run native and differential BB tests. |
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

The release matrix covers Linux (`amd64`, `arm64`, and ARMv6), macOS
(`amd64` and `arm64`), Windows (`amd64` and `arm64`), FreeBSD (`amd64` and
`arm64`), WASI, and browser WebAssembly.

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
  Jreproducible repository
  automation
