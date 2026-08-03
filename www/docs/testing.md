---
title: Testing
description: Current Gobb results against Babashka library tests and examples
---

# Testing

Gobb measures compatibility against Babashka **v1.12.218** at commit `0fb349c414e717800be775ba9cb77c95a9eb700d`. These are committed full-suite snapshots; building this website never downloads Babashka or reruns either suite.

## Library tests

Status: **FAIL**

| Libraries | Test namespaces | Cases run | Cases passed | Cases failed | Cases blocked | Tests | Assertions passed | Failures | Errors |
| ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: |
| 111 | 261 | 261 | 94 | 10 | 157 | 3601 | 6752 | 153 | 214 |

Run the complete suite with:

```sh
make test-lib_tests
```

Limit an investigative run to selected namespaces without replacing the published full-suite snapshot:

```sh
make test-lib_tests LIB_TESTS='namespace.one namespace.two'
```

### Per-namespace results

| Namespace | Status | Tests | Assertions passed | Failures | Errors | Note |
| --- | --- | ---: | ---: | ---: | ---: | --- |
| `aero.core-test` | pass | 28 | 58 | 0 | 0 |  |
| `again.core-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: deftype |
| `arrangement.core-test` | pass | 9 | 110 | 0 | 0 |  |
| `babashka.curl-test` | pass | 1 | 0 | 0 | 0 |  |
| `babashka.process-exec-test` | fail | 6 | 0 | 3 | 5 |  |
| `babashka.process-test` | blocked | — | — | — | — | exit 2: actual: "unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.NewIllegalArgumentError/n/nGLJ Stack:/n%!s(<nil>):%!d(<nil>):%!d(<nil>):/tgithub.com:glojurelang:glojure:pkg:lang.NewIllegalArgumentError/n" |
| `babashka.statecharts-test` | blocked | — | — | — | — | exit 1: failed to load /com/fulcrologic/guardrails/malli/core: not found in load path |
| `better-cond.core-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `bond.assertions-test` | pass | 5 | 32 | 0 | 0 |  |
| `bond.james-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `bond.target-data` | pass | 0 | 0 | 0 | 0 |  |
| `borkdude.deps.smoke-test` | fail | 1 | 0 | 0 | 1 |  |
| `camel-snake-kebab.core-test` | pass | 2 | 130 | 0 | 0 |  |
| `camel-snake-kebab.extras-test` | pass | 1 | 6 | 0 | 0 |  |
| `camel-snake-kebab.internals.string-separator-test` | pass | 1 | 22 | 0 | 0 |  |
| `cheshire.test.core` | blocked | — | — | — | — | exit 1: unable to resolve symbol: JsonFactory |
| `clarktown.core-test` | pass | 1 | 1 | 0 | 0 |  |
| `clarktown.parsers.bold-test` | pass | 1 | 3 | 0 | 0 |  |
| `clarktown.parsers.code-block-test` | pass | 1 | 2 | 0 | 0 |  |
| `clarktown.parsers.empty-block-test` | pass | 1 | 3 | 0 | 0 |  |
| `clarktown.parsers.heading-block-test` | pass | 2 | 9 | 0 | 0 |  |
| `clarktown.parsers.horizontal-line-block-test` | pass | 1 | 8 | 0 | 0 |  |
| `clarktown.parsers.inline-code-test` | pass | 1 | 2 | 0 | 0 |  |
| `clarktown.parsers.italic-test` | pass | 1 | 3 | 0 | 0 |  |
| `clarktown.parsers.link-and-image-test` | pass | 1 | 5 | 0 | 0 |  |
| `clarktown.parsers.quote-block-test` | pass | 1 | 4 | 0 | 0 |  |
| `clarktown.parsers.strikethrough-test` | pass | 1 | 2 | 0 | 0 |  |
| `cli-matic.core-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `cli-matic.help-gen-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `cli-matic.presets-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `cli-matic.utils-candidates-test` | pass | 2 | 10 | 0 | 0 |  |
| `cli-matic.utils-convert-config-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `cli-matic.utils-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `cli-matic.utils-v2-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `clj-commons.digest-test` | fail | 1 | 3 | 0 | 6 |  |
| `clj-http.lite.client-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `clj-http.lite.test-runner` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `clj-stacktrace.core-test` | pass | 3 | 19 | 0 | 0 |  |
| `clj-stacktrace.repl-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `clj-yaml.core-test` | fail | 26 | 31 | 15 | 11 |  |
| `cljc.java-time-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `clojure-csv.test.core` | pass | 11 | 61 | 0 | 0 |  |
| `clojure-csv.test.utils` | pass | 5 | 30 | 0 | 0 |  |
| `clojure.algo.test-monads` | pass | 11 | 28 | 0 | 0 |  |
| `clojure.core.cache-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `clojure.core.cache.wrapped-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `clojure.data.csv-test` | pass | 4 | 26 | 0 | 0 |  |
| `clojure.data.generators-test` | pass | 3 | 100 | 0 | 0 |  |
| `clojure.data.json-compat-0-1-test` | fail | 39 | 55 | 1 | 0 |  |
| `clojure.data.json-test` | fail | 66 | 99 | 1 | 0 |  |
| `clojure.data.json-test-suite-test` | pass | 128 | 128 | 0 | 0 |  |
| `clojure.data.zip-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: QName |
| `clojure.math.test-combinatorics` | pass | 18 | 999 | 0 | 0 |  |
| `clojure.math.test-numeric-tower` | pass | 10 | 95 | 0 | 0 |  |
| `clojure.term.colors-test` | pass | 1 | 0 | 0 | 0 |  |
| `clojure.test-clojure.instr` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `clojure.test-clojure.spec` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `clojure.tools.gitlibs.test-impl` | pass | 1 | 16 | 0 | 0 |  |
| `clojure.tools.namespace.dependency-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `clojure.tools.namespace.find-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: deftype |
| `clojure.tools.namespace.move-test` | pass | 1 | 6 | 0 | 0 |  |
| `clojure.tools.namespace.parse-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: deftype |
| `clojure.tools.namespace.test-helpers` | pass | 0 | 0 | 0 | 0 |  |
| `clojure.tools.test-gitlibs` | pass | 4 | 14 | 0 | 0 |  |
| `cloverage.args-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/java/classpath: not found in load path |
| `cloverage.dependency-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `cloverage.instrument-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/tools/namespace/file: not found in load path |
| `cloverage.report.console-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: deftype |
| `cloverage.source-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: deftype |
| `cognitect.test-runner-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: deftype |
| `cognitect.test-runner.sample-property-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: deftype |
| `cognitect.test-runner.samples-test` | pass | 2 | 3 | 0 | 0 |  |
| `com.potetm.fusebox.bulwark-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `com.potetm.fusebox.circuit-breaker-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `com.potetm.fusebox.fallback-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `com.potetm.fusebox.memoize-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `com.potetm.fusebox.registry-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `com.potetm.fusebox.retry-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `com.rpl.specter.cljs-test-helpers` | pass | 0 | 0 | 0 | 0 |  |
| `com.rpl.specter.core-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: deftype |
| `com.rpl.specter.test-helpers` | blocked | — | — | — | — | exit 1: unable to resolve symbol: deftype |
| `com.rpl.specter.zipper-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: deftype |
| `com.stuartsierra.component-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `com.stuartsierra.dependency-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `com.wsscode.misc.coll-test` | pass | 28 | 71 | 0 | 0 |  |
| `com.wsscode.misc.macros-test` | pass | 1 | 2 | 0 | 0 |  |
| `com.wsscode.misc.math-test` | pass | 6 | 9 | 0 | 0 |  |
| `com.wsscode.misc.refs-test` | pass | 4 | 11 | 0 | 0 |  |
| `com.wsscode.misc.uuid-test` | pass | 1 | 1 | 0 | 0 |  |
| `comb.test.template` | pass | 2 | 6 | 0 | 0 |  |
| `contajners.impl-test` | pass | 5 | 7 | 0 | 0 |  |
| `core-match.core-tests` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `cprop.smoke-test` | pass | 1 | 0 | 0 | 0 |  |
| `crispin.core-test` | blocked | — | — | — | — | exit 1: NO_SOURCE_PATH: defrecord protocol implementations are not yet supported |
| `datalog.parser-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `datalog.parser.impl-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `datalog.parser.pull-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `datalog.parser.test.util` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `datalog.unparser-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `docopt.core-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `doric.test.core` | pass | 15 | 56 | 0 | 0 |  |
| `doric.test.doctest` | pass | 0 | 0 | 0 | 0 |  |
| `doric.test.readme` | fail | 1 | 2 | 9 | 1 |  |
| `edn-query-language.core-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `environ.core-test` | pass | 1 | 9 | 0 | 0 |  |
| `exoscale.coax-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `exoscale.interceptor-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/core/async: not found in load path |
| `exoscale.lingo.test.core-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `expectations.clojure.test-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `expound.print-length-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `expound.problems-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `expound.spec-gen` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `expound.specs-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `expound.test-utils` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `failjure.test-core` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `gaka.core-test` | pass | 7 | 39 | 0 | 0 |  |
| `hasch.test` | pass | 7 | 26 | 0 | 0 |  |
| `hato.client-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `helins.binf.test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: DirectByteBuffer |
| `hiccup.core-test` | pass | 5 | 66 | 0 | 0 |  |
| `hiccup2.core-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: java.net.URI |
| `hickory.test.convert` | blocked | — | — | — | — | exit 1: unable to resolve symbol: DocumentType |
| `hickory.test.core` | blocked | — | — | — | — | exit 1: unable to resolve symbol: DocumentType |
| `hickory.test.hiccup-utils` | pass | 8 | 50 | 0 | 0 |  |
| `hickory.test.render` | blocked | — | — | — | — | exit 1: unable to resolve symbol: DocumentType |
| `hickory.test.select` | blocked | — | — | — | — | exit 1: unable to resolve symbol: DocumentType |
| `hickory.test.zip` | blocked | — | — | — | — | exit 1: unable to resolve symbol: DocumentType |
| `honey.sql-test` | pass | 47 | 160 | 0 | 0 |  |
| `honey.sql.helpers-test` | pass | 31 | 157 | 0 | 0 |  |
| `honey.sql.postgres-test` | pass | 20 | 41 | 0 | 0 |  |
| `honeysql.core-test` | blocked | — | — | — | — | exit 1: NO_SOURCE_PATH: defrecord protocol implementations are not yet supported |
| `honeysql.format-test` | blocked | — | — | — | — | exit 1: NO_SOURCE_PATH: defrecord protocol implementations are not yet supported |
| `httpkit.client-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `hugsql.babashka-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: deftype |
| `integrant.core-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `integrant.test.bar` | pass | 0 | 0 | 0 | 0 |  |
| `integrant.test.baz` | pass | 0 | 0 | 0 | 0 |  |
| `integrant.test.foo` | pass | 0 | 0 | 0 | 0 |  |
| `integrant.test.quz` | pass | 0 | 0 | 0 | 0 |  |
| `io.aviso.binary-test` | pass | 4 | 9 | 0 | 0 |  |
| `jasentaa.collections-test` | pass | 3 | 13 | 0 | 0 |  |
| `jasentaa.parser.basic-test` | pass | 4 | 15 | 0 | 0 |  |
| `jasentaa.parser.combinators-test` | pass | 3 | 11 | 0 | 0 |  |
| `jasentaa.position-test` | pass | 5 | 13 | 0 | 0 |  |
| `jasentaa.test-helpers` | pass | 0 | 0 | 0 | 0 |  |
| `jasentaa.worked-example-1` | pass | 1 | 7 | 0 | 0 |  |
| `jasentaa.worked-example-2` | pass | 2 | 2 | 0 | 0 |  |
| `java-http-clj.smoke-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: HttpClient |
| `lambdaisland.regal-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `lambdaisland.regal.test-util` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `linked.map-test` | blocked | — | — | — | — | exit 1: NO_SOURCE_PATH: wrong number of arguments: expected 1, got 2 |
| `loom.test.alg` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `loom.test.alg-generic` | blocked | — | — | — | — | exit 1: unable to resolve symbol: deftype |
| `loom.test.attr` | pass | 1 | 11 | 0 | 0 |  |
| `loom.test.compliance-tester` | pass | 0 | 0 | 0 | 0 |  |
| `loom.test.derived` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `loom.test.flow` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `loom.test.graph` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `loom.test.label` | pass | 1 | 8 | 0 | 0 |  |
| `loom.test.network-simplex` | fail | 10 | 15 | 5 | 0 |  |
| `malli.clj-kondo-test` | blocked | — | — | — | — | exit 1: failed to load /fipp/edn: not found in load path |
| `malli.core-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: deftype |
| `malli.destructure-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: deftype |
| `malli.dot-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: deftype |
| `malli.error-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: deftype |
| `malli.experimental-test` | blocked | — | — | — | — | exit 1: failed to load /fipp/edn: not found in load path |
| `malli.experimental.time-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: deftype |
| `malli.experimental.time.transform-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: deftype |
| `malli.instrument-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: deftype |
| `malli.json-schema-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: deftype |
| `malli.plantuml-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: deftype |
| `malli.provider-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: deftype |
| `malli.registry-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: deftype |
| `malli.swagger-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: deftype |
| `malli.transform-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: deftype |
| `malli.util-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: deftype |
| `markdown.md-test` | blocked | — | — | — | — | exit 1: NO_SOURCE_PATH: wrong number of arguments: expected 1, got 2 |
| `me.raynes.core-test` | blocked | — | — | — | — | exit 1: EvalASTMaybeHostForm: io/Coercions |
| `meander.defsyntax-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `meander.defsyntax-test.gh-145` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `meander.epsilon-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `meander.interpreter.epsilon-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `meander.match.check.epsilon-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `meander.match.epsilon-test` | pass | 0 | 0 | 0 | 0 |  |
| `meander.match.ir.epsilon-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `meander.matrix.epsilon-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `meander.strategy.epsilon-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: deftype |
| `meander.substitute.epsilon-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: deftype |
| `meander.syntax.epsilon-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `medley.core-test` | pass | 43 | 196 | 0 | 0 |  |
| `meta-merge.core-test` | pass | 1 | 17 | 0 | 0 |  |
| `minimallist.core-test` | pass | 2 | 338 | 0 | 0 |  |
| `minimallist.util-test` | pass | 4 | 15 | 0 | 0 |  |
| `missing.test.assertions-test` | pass | 2 | 1 | 0 | 0 |  |
| `missing.test.old-methods` | pass | 0 | 0 | 0 | 0 |  |
| `msgpack.core-check` | blocked | — | — | — | — | exit 1: unable to resolve symbol: deftype |
| `msgpack.core-test` | fail | 10 | 2 | 0 | 188 |  |
| `multigrep.core-test` | pass | 2 | 2 | 0 | 0 |  |
| `net.cgrand.xforms-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `nextjournal.markdown-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `nextjournal.markdown.multi-threading-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `odoyle.rules-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `ol.sfv.api-test` | pass | 10 | 64 | 0 | 0 |  |
| `ol.sfv.conformance-test` | fail | 2821 | 2700 | 119 | 2 |  |
| `ol.sfv.error-test` | pass | 1 | 136 | 0 | 0 |  |
| `ol.sfv.example-test` | pass | 4 | 31 | 0 | 0 |  |
| `ol.sfv.parser-test` | pass | 11 | 134 | 0 | 0 |  |
| `ol.sfv.serialization-test` | pass | 6 | 34 | 0 | 0 |  |
| `omniconf.core-test` | pass | 1 | 4 | 0 | 0 |  |
| `orchestra.core-test` | blocked | — | — | — | — | exit 1: {} |
| `orchestra.expound-test` | blocked | — | — | — | — | exit 1: {} |
| `orchestra.make-fns` | blocked | — | — | — | — | exit 1: {} |
| `orchestra.many-fns` | blocked | — | — | — | — | exit 1: {} |
| `orchestra.reload-test` | blocked | — | — | — | — | exit 1: {} |
| `plumbing.core-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: deftype |
| `portal.bench` | pass | 0 | 0 | 0 | 0 |  |
| `portal.e2e` | pass | 0 | 0 | 0 | 0 |  |
| `portal.runtime.cson-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `portal.runtime.fs-test` | pass | 1 | 8 | 0 | 0 |  |
| `portal.test-runner` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `postmortem.core-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: deftype |
| `postmortem.instrument-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: deftype |
| `progrock.core-test` | pass | 5 | 15 | 0 | 0 |  |
| `promesa.tests.core-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `qbits.auspex-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `reifyhealth.specmonstah.core-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `reifyhealth.specmonstah.spec-gen-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `reifyhealth.specmonstah.test-data` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `rewrite-clj.node-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `rewrite-clj.node.coercer-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `rewrite-clj.paredit-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `rewrite-clj.parser-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: deftype |
| `rewrite-clj.zip-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `rewrite-clj.zip.subedit-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `riddley.walk-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: clojure.lang.Compiler |
| `ruuter.core-test` | pass | 3 | 7 | 0 | 0 |  |
| `schema.coerce-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: deftype |
| `schema.core-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: deftype |
| `schema.experimental.abstract-map-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: deftype |
| `schema.macros-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: deftype |
| `schema.test-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: deftype |
| `schema.utils-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: deftype |
| `selmer.core-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `selmer.our-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `slingshot.slingshot-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `slingshot.support-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `slingshot.test-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `sluj.core-test` | pass | 3 | 18 | 0 | 0 |  |
| `swirrl.dogstatsd-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `table.core-test` | pass | 26 | 26 | 0 | 0 |  |
| `table.width-test` | pass | 4 | 4 | 0 | 0 |  |
| `test-check.smoke-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: deftype |
| `testdoc.core-test` | pass | 11 | 39 | 0 | 0 |  |
| `testdoc.style.code-first-test` | pass | 2 | 11 | 0 | 0 |  |
| `testdoc.style.repl-test` | pass | 2 | 10 | 0 | 0 |  |
| `vault.client.http-test` | blocked | — | — | — | — | exit 1: EvalASTMaybeHostForm: Runtime/getRuntime |
| `vault.lease-test` | blocked | — | — | — | — | exit 1: EvalASTMaybeHostForm: Runtime/getRuntime |
| `version-clj.compare-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `version-clj.core-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `version-clj.split-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `version-clj.via-use-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |

## Examples

Status: **FAIL**

Every upstream `.clj` example is compiled. Safe deterministic examples are then run under BB and Gobb and compared; environmental or interactive examples carry an explicit execution waiver. A waiver does not waive compilation.

| Total | Passed | Failed | Execution waivers | Compiled | Compile failures |
| ---: | ---: | ---: | ---: | ---: | ---: |
| 34 | 2 | 32 | 27 | 2 | 32 |

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
| `cprop.clj` | waived | fail | fail | exit 1: failed to load /clojure/pprint: not found in load path |
| `db_who.clj` | waived | fail | fail | exit 1: failed to load /clojure/pprint: not found in load path |
| `digitalocean-ping.clj` | waived | fail | fail | exit 1: unable to resolve symbol: runtime.GOOS |
| `download-aliases.clj` | waived | fail | fail | exit 1: <unknown-file>:1:1:	(compile (quote main.core)) |
| `fzf.clj` | waived | fail | fail | exit 1: unable to resolve symbol: runtime.GOOS |
| `hsqldb_unused_vars.clj` | waived | fail | fail | exit 1: failed to load /clojure/pprint: not found in load path |
| `htmx_todoapp.clj` | waived | fail | fail | exit 1: failed to load /clojure/java/browse: not found in load path |
| `http-server.clj` | waived | fail | fail | exit 1: failed to load /clojure/java/browse: not found in load path |
| `http_server_from_scratch.clj` | waived | fail | fail | exit 1: <unknown-file>:1:1:	(compile (quote main.core)) |
| `httpkit_server.clj` | waived | fail | fail | exit 1: failed to load /clojure/pprint: not found in load path |
| `image-viewer.clj` | waived | fail | fail | exit 1: failed to load /clojure/java/browse: not found in load path |
| `is_tty.clj` | run | pass | pass |  |
| `logger.clj` | run | pass | pass |  |
| `ls_jar.clj` | waived | fail | fail | exit 1: <unknown-file>:1:1:	(compile (quote main.core)) |
| `memo.clj` | waived | fail | fail | exit 1: unsupported value type lang.ArityFn: {<nil> [<ADDR> <ADDR> <ADDR> <nil> <nil> <nil>] map[] 2 {<nil> 2 <ADDR>} 2} |
| `mysql_cmdline.clj` | waived | fail | fail | exit 1: EvalASTMaybeHostForm: shell/sh |
| `normalize-keywords.clj` | waived | fail | fail | exit 1: failed to load /rewrite_clj/node: not found in load path |
| `notes.clj` | waived | fail | fail | exit 1: failed to load /clojure/pprint: not found in load path |
| `outdated.clj` | waived | fail | fail | exit 1: <unknown-file>:1:1:	(compile (quote main.core)) |
| `pom_version_get.clj` | run | fail | fail | exit 1: failed to load /clojure/data/xml: not found in load path |
| `pom_version_get_xml_zip.clj` | waived | fail | fail | exit 1: failed to load /babashka/deps: not found in load path |
| `pom_version_set.clj` | waived | fail | fail | exit 1: failed to load /clojure/data/xml: not found in load path |
| `portal.clj` | waived | fail | fail | exit 1: failed to load /babashka/deps: not found in load path |
| `process_builder.clj` | run | fail | fail | exit 1: unable to resolve symbol: ProcessBuilder |
| `pst.clj` | waived | fail | fail | exit 1: EvalASTMaybeHostForm: java.time.ZonedDateTime/now |
| `random_doc.clj` | waived | fail | fail | exit 1: cannot compile /clojure/repl: filesystem is not writable |
| `sqlite.clj` | waived | fail | fail | exit 1: bad binding form: :ns |
| `torrent-viewer.clj` | waived | fail | fail | exit 1: failed to load /bencode/core: not found in load path |
| `tree.clj` | run | fail | fail | exit 1: failed to load /clojure/tools/cli: not found in load path |
| `vim.clj` | waived | fail | fail | exit 1: unable to resolve symbol: ProcessBuilder |
| `whatsapp_frequencies.clj` | waived | fail | fail | exit 1: failed to load /clojure/pprint: not found in load path |
| `which.clj` | run | fail | fail | exit 1: unsupported value type lang.ArityFn: {<nil> [<ADDR> <ADDR> <ADDR> <nil> <nil> <nil>] map[] 2 {<nil> 2 <ADDR>} 2} |
| `wiki-translate.clj` | waived | fail | fail | exit 1: unable to resolve symbol: runtime.GOOS |
| `xml-example.clj` | run | fail | fail | exit 1: EvalASTMaybeHostForm: xml/indent-str |

## Result policy

Both targets run every applicable case before returning a nonzero status when failures remain. Raw logs and filtered-run results stay under `.cache/upstream-tests/`; only an unfiltered full run updates the committed snapshots rendered here.
