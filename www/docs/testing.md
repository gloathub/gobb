---
title: Testing
description: Current Gobb results against Babashka library tests and examples
---

# Testing

Gobb measures compatibility against Babashka **v1.12.218** at commit `0fb349c414e717800be775ba9cb77c95a9eb700d`. These are committed full-suite snapshots; building this website never downloads Babashka or reruns either suite.

## Library tests

Status: **BLOCKED**

| Tests | Assertions passed | Failures | Errors |
| ---: | ---: | ---: | ---: |
| — | — | — | — |

Current blocker: `exit 1: <BABASHKA>/test-resources/lib_tests/babashka/run_all_libtests.clj: String index out of range`

Run the complete suite with:

```sh
make test-lib_tests
```

Limit an investigative run to selected namespaces without replacing the published full-suite snapshot:

```sh
make test-lib_tests LIB_TESTS='namespace.one namespace.two'
```

## Examples

Status: **FAIL**

Every upstream `.clj` example is compiled. Safe deterministic examples are then run under BB and Gobb and compared; environmental or interactive examples carry an explicit execution waiver. A waiver does not waive compilation.

| Total | Passed | Failed | Execution waivers | Compiled | Compile failures |
| ---: | ---: | ---: | ---: | ---: | ---: |
| 34 | 1 | 33 | 27 | 1 | 33 |

Run the complete suite with:

```sh
make test-examples
```

Limit an investigative run without replacing the published full-suite snapshot:

```sh
make test-examples EXAMPLES='which.clj xml-example.clj'
```

### Per-example results

| Example | Scenario | Compile | Result | Note |
| --- | --- | --- | --- | --- |
| `cprop.clj` | waived | fail | fail | exit 1: failed to load /babashka/classpath: not found in load path |
| `db_who.clj` | waived | fail | fail | exit 1: failed to load /clojure/java/shell: not found in load path |
| `digitalocean-ping.clj` | waived | fail | fail | exit 1: failed to load /babashka/curl: not found in load path |
| `download-aliases.clj` | waived | fail | fail | exit 1: failed to load /clojure/edn: not found in load path |
| `fzf.clj` | waived | fail | fail | exit 1: failed to load /babashka/process: not found in load path |
| `hsqldb_unused_vars.clj` | waived | fail | fail | exit 1: failed to load /babashka/pods: not found in load path |
| `htmx_todoapp.clj` | waived | fail | fail | exit 1: <preloads>: String index out of range |
| `http-server.clj` | waived | fail | fail | exit 1: failed to load /babashka/fs: not found in load path |
| `http_server_from_scratch.clj` | waived | fail | fail | exit 1: failed to load /clojure/java/io: not found in load path |
| `httpkit_server.clj` | waived | fail | fail | exit 1: failed to load /clojure/pprint: not found in load path |
| `image-viewer.clj` | waived | fail | fail | exit 1: failed to load /clojure/java/browse: not found in load path |
| `is_tty.clj` | run | fail | fail | exit 1: failed to load /babashka/process: not found in load path |
| `logger.clj` | run | pass | pass |  |
| `ls_jar.clj` | waived | fail | fail | exit 1: failed to load /clojure/java/io: not found in load path |
| `memo.clj` | waived | fail | fail | exit 1: unsupported value type lang.ArityFn: {<nil> [<ADDR> <ADDR> <ADDR> <nil> <nil> <nil>] map[] 2 {<nil> 2 <ADDR>} 2} |
| `mysql_cmdline.clj` | waived | fail | fail | exit 1: EvalASTMaybeHostForm: shell/sh |
| `normalize-keywords.clj` | waived | fail | fail | exit 1: failed to load /babashka/pods: not found in load path |
| `notes.clj` | waived | fail | fail | exit 1: failed to load /clojure/java/io: not found in load path |
| `outdated.clj` | waived | fail | fail | exit 1: failed to load /clojure/edn: not found in load path |
| `pom_version_get.clj` | run | fail | fail | exit 1: failed to load /clojure/data/xml: not found in load path |
| `pom_version_get_xml_zip.clj` | waived | fail | fail | exit 1: failed to load /babashka/deps: not found in load path |
| `pom_version_set.clj` | waived | fail | fail | exit 1: failed to load /clojure/data/xml: not found in load path |
| `portal.clj` | waived | fail | fail | exit 1: failed to load /babashka/deps: not found in load path |
| `process_builder.clj` | run | fail | fail | exit 1: failed to load /clojure/java/io: not found in load path |
| `pst.clj` | waived | fail | fail | exit 1: EvalASTMaybeHostForm: java.time.ZonedDateTime/now |
| `random_doc.clj` | waived | fail | fail | exit 1: cannot compile /clojure/repl: filesystem is not writable |
| `sqlite.clj` | waived | fail | fail | exit 1: bad binding form: :ns |
| `torrent-viewer.clj` | waived | fail | fail | exit 1: failed to load /clojure/java/io: not found in load path |
| `tree.clj` | run | fail | fail | exit 1: <preloads>: String index out of range |
| `vim.clj` | waived | fail | fail | exit 1: failed to load /clojure/java/io: not found in load path |
| `whatsapp_frequencies.clj` | waived | fail | fail | exit 1: failed to load /clojure/java/io: not found in load path |
| `which.clj` | run | fail | fail | exit 1: failed to load /clojure/java/io: not found in load path |
| `wiki-translate.clj` | waived | fail | fail | exit 1: failed to load /babashka/curl: not found in load path |
| `xml-example.clj` | run | fail | fail | exit 1: EvalASTMaybeHostForm: xml/indent-str |

## Result policy

Both targets run every applicable case before returning a nonzero status when failures remain. Raw logs and filtered-run results stay under `.cache/upstream-tests/`; only an unfiltered full run updates the committed snapshots rendered here.
