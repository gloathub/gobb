# Gobb

**Go + BB**

Gobb is a Go-native implementation of the
[Babashka](https://babashka.org/) experience. It uses
[Gloat](https://gloathub.org/) to build native binaries and
[Glojure](https://github.com/glojurelang/glojure) as its full Clojure runtime,
without SCI, GraalVM, or a JVM.

The project aims to make as much existing BB code and behavior work as
possible while adding first-class native and WebAssembly compilation:

```text
gobb build script.clj -o app --platform linux/amd64
```

> [!IMPORTANT]
> Gobb is at the foundation stage. The architecture, automation, website, and
> implementation roadmap exist; the Gobb runtime has not been implemented yet.

## Goals

- Preserve BB's familiar script, expression, stdin, task, dependency, pod, and
  REPL workflows.
- Use Glojure directly instead of rebuilding BB's SCI host layer.
- Run the same project dynamically or compile it through Gloat.
- Target Linux, macOS, Windows, WASI, and browser WebAssembly.
- Grow support for Java-dependent Clojure libraries through
  [gojava](https://github.com/gloathub/gojava) and reusable Go-backed
  compatibility shims.
- Test compatibility against a pinned BB executable and publish the measured
  progress.

General Java bytecode execution is not a goal. Libraries that refer to Java
classes become compatible as those classes gain Glojure/gojava or native Go
implementations.

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
bindings, loading, and runtime compilation. Gobb supplies BB-compatible CLI
policy, tasks, dependency handling, bundled features, platform capabilities,
and packaging.

Reusable runtime, compiler, and Java compatibility improvements belong in
Glojure, gojava, or Gloat rather than Gobb-specific forks.

See the
[implementation details](https://clojurestar.github.io/gobb/implementation/)
for the full design.

## Project Status

Gobb is currently establishing its repository foundation:

- [x] Architecture and implementation roadmap
- [x] Makes-managed project and website automation
- [x] MkDocs project website
- [ ] BB compatibility ledger and differential test harness
- [ ] First Gloat-compiled Glojure runtime executable
- [ ] BB-compatible execution shell
- [ ] Project dependencies, tasks, pods, and bundled libraries
- [ ] Production `gobb build` command

Follow the [live roadmap](https://clojurestar.github.io/gobb/roadmap/) for
current progress.

## Website

The project website is built with MkDocs Material. All tools are installed
locally through [Makes](https://github.com/makeplus/makes).

Start the development server:

```bash
make serve
```

The site is available at <http://127.0.0.1:8000/gobb/>.

Build the strict production site:

```bash
make site
```

Publish the generated site to the `gh-pages` branch:

```bash
make publish
```

The public site is <https://clojurestar.github.io/gobb/>.

## Repository Layout

```text
.
├── Makefile          # Makes-managed repository automation
├── ReadMe.md         # Project overview
├── note/             # Local, ignored planning notes
└── www/              # MkDocs source and website automation
```

Runtime and compatibility source trees will be added as their corresponding
roadmap milestones begin.

## License

Gobb is released under the
[Eclipse Public License 2.0](License) (`EPL-2.0`).

Code adapted from upstream projects retains its original copyright and
attribution notices.

## Related Projects

- [Babashka](https://github.com/babashka/babashka) — behavior and
  compatibility target
- [Glojure](https://github.com/glojurelang/glojure) — full Clojure runtime in
  Go
- [Gloat](https://github.com/gloathub/gloat) — Clojure-to-Go compilation and
  cross-compilation
- [gojava](https://github.com/gloathub/gojava) — JVM-faithful APIs implemented
  in Go
- [Makes](https://github.com/makeplus/makes) — reproducible repository
  automation
