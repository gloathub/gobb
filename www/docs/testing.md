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
| 111 | 261 | 261 | 12 | 32 | 217 | 230 | 759 | 37 | 450 |

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
| `aero.core-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.NewMultiFn |
| `again.core-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.NewMultiFn |
| `arrangement.core-test` | fail | 9 | 100 | 0 | 1 |  |
| `babashka.curl-test` | pass | 1 | 0 | 0 | 0 |  |
| `babashka.process-exec-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `babashka.process-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `babashka.statecharts-test` | blocked | — | — | — | — | exit 1: failed to load /com/fulcrologic/statecharts: not found in load path |
| `better-cond.core-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: Object |
| `bond.assertions-test` | fail | 5 | 0 | 0 | 5 |  |
| `bond.james-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `bond.target-data` | blocked | 0 | 0 | 0 | 0 | exit 1: {:test 0, :pass 0, :fail 0, :error 0} |
| `borkdude.deps.smoke-test` | fail | 1 | 0 | 0 | 1 |  |
| `camel-snake-kebab.core-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.NewMultiFn |
| `camel-snake-kebab.extras-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.NewMultiFn |
| `camel-snake-kebab.internals.string-separator-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.NewMultiFn |
| `cheshire.test.core` | blocked | — | — | — | — | exit 1: unable to resolve symbol: JsonFactory |
| `clarktown.core-test` | fail | 1 | 0 | 0 | 1 |  |
| `clarktown.parsers.bold-test` | pass | 1 | 3 | 0 | 0 |  |
| `clarktown.parsers.code-block-test` | fail | 1 | 0 | 0 | 2 |  |
| `clarktown.parsers.empty-block-test` | pass | 1 | 3 | 0 | 0 |  |
| `clarktown.parsers.heading-block-test` | pass | 2 | 9 | 0 | 0 |  |
| `clarktown.parsers.horizontal-line-block-test` | pass | 1 | 8 | 0 | 0 |  |
| `clarktown.parsers.inline-code-test` | pass | 1 | 2 | 0 | 0 |  |
| `clarktown.parsers.italic-test` | pass | 1 | 3 | 0 | 0 |  |
| `clarktown.parsers.link-and-image-test` | pass | 1 | 5 | 0 | 0 |  |
| `clarktown.parsers.quote-block-test` | fail | 1 | 3 | 0 | 1 |  |
| `clarktown.parsers.strikethrough-test` | pass | 1 | 2 | 0 | 0 |  |
| `cli-matic.core-test` | blocked | — | — | — | — | exit 1: failed to load /cli_matic/platform: not found in load path |
| `cli-matic.help-gen-test` | blocked | — | — | — | — | exit 1: failed to load /cli_matic/platform_macros: not found in load path |
| `cli-matic.presets-test` | blocked | — | — | — | — | exit 1: failed to load /cljc/java_time/zone_id: not found in load path |
| `cli-matic.utils-candidates-test` | blocked | — | — | — | — | exit 1: failed to load /cli_matic/utils_candidates: not found in load path |
| `cli-matic.utils-convert-config-test` | blocked | — | — | — | — | exit 1: failed to load /cli_matic/optionals: not found in load path |
| `cli-matic.utils-test` | blocked | — | — | — | — | exit 1: failed to load /cli_matic/utils: not found in load path |
| `cli-matic.utils-v2-test` | blocked | — | — | — | — | exit 1: failed to load /cli_matic/platform_macros: not found in load path |
| `clj-commons.digest-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.NewMultiFn |
| `clj-http.lite.client-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `clj-http.lite.test-runner` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `clj-stacktrace.core-test` | fail | 3 | 3 | 0 | 2 |  |
| `clj-stacktrace.repl-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `clj-yaml.core-test` | fail | 26 | 31 | 10 | 16 |  |
| `cljc.java-time-test` | blocked | — | — | — | — | exit 1: failed to load /cljc/java_time/temporal/chrono_field: not found in load path |
| `clojure-csv.test.core` | fail | 11 | 7 | 0 | 54 |  |
| `clojure-csv.test.utils` | fail | 5 | 0 | 0 | 30 |  |
| `clojure.algo.test-monads` | blocked | — | — | — | — | exit 1: EvalASTMaybeHostForm: clojure.lang.Compiler/specials |
| `clojure.core.cache-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/core/cache: not found in load path |
| `clojure.core.cache.wrapped-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/core/cache/wrapped: not found in load path |
| `clojure.data.csv-test` | fail | 4 | 19 | 3 | 2 |  |
| `clojure.data.generators-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: java.util.Random |
| `clojure.data.json-compat-0-1-test` | blocked | — | — | — | — | exit 1: failed to load pprint/utilities: not found in load path |
| `clojure.data.json-test` | blocked | — | — | — | — | exit 1: failed to load pprint/utilities: not found in load path |
| `clojure.data.json-test-suite-test` | blocked | — | — | — | — | exit 1: failed to load pprint/utilities: not found in load path |
| `clojure.data.zip-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/data/xml: not found in load path |
| `clojure.math.test-combinatorics` | fail | 18 | 0 | 0 | 122 |  |
| `clojure.math.test-numeric-tower` | blocked | — | — | — | — | exit 1: failed to load /clojure/math/numeric_tower: not found in load path |
| `clojure.term.colors-test` | pass | 1 | 0 | 0 | 0 |  |
| `clojure.test-clojure.instr` | blocked | — | — | — | — | exit 1: failed to load /clojure/spec/alpha: not found in load path |
| `clojure.test-clojure.spec` | blocked | — | — | — | — | exit 1: failed to load /clojure/spec/alpha: not found in load path |
| `clojure.tools.gitlibs.test-impl` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.NewDelay |
| `clojure.tools.namespace.dependency-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/tools/namespace/dependency: not found in load path |
| `clojure.tools.namespace.find-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/tools/namespace/find: not found in load path |
| `clojure.tools.namespace.move-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/tools/namespace/move: not found in load path |
| `clojure.tools.namespace.parse-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/tools/namespace/parse: not found in load path |
| `clojure.tools.namespace.test-helpers` | blocked | 0 | 0 | 0 | 0 | exit 1: {:test 0, :pass 0, :fail 0, :error 0} |
| `clojure.tools.test-gitlibs` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.NewDelay |
| `cloverage.args-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/tools/cli: not found in load path |
| `cloverage.dependency-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/tools/namespace/dependency: not found in load path |
| `cloverage.instrument-test` | blocked | — | — | — | — | exit 1: failed to load pprint/utilities: not found in load path |
| `cloverage.report.console-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: cloverage.report.console |
| `cloverage.source-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/tools/namespace/file: not found in load path |
| `cognitect.test-runner-test` | blocked | — | — | — | — | exit 1: failed to load /cognitect/test_runner: not found in load path |
| `cognitect.test-runner.sample-property-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.NewMultiFn |
| `cognitect.test-runner.samples-test` | pass | 2 | 3 | 0 | 0 |  |
| `com.potetm.fusebox.bulwark-test` | blocked | — | — | — | — | exit 1: failed to load /com/potetm/fusebox/bulkhead: not found in load path |
| `com.potetm.fusebox.circuit-breaker-test` | blocked | — | — | — | — | exit 1: failed to load /com/potetm/fusebox/circuit_breaker: not found in load path |
| `com.potetm.fusebox.fallback-test` | blocked | — | — | — | — | exit 1: failed to load /com/potetm/fusebox/fallback: not found in load path |
| `com.potetm.fusebox.memoize-test` | blocked | — | — | — | — | exit 1: failed to load /com/potetm/fusebox/memoize: not found in load path |
| `com.potetm.fusebox.registry-test` | blocked | — | — | — | — | exit 1: failed to load /com/potetm/fusebox/registry: not found in load path |
| `com.potetm.fusebox.retry-test` | blocked | — | — | — | — | exit 1: failed to load /com/potetm/fusebox/retry: not found in load path |
| `com.rpl.specter.cljs-test-helpers` | blocked | 0 | 0 | 0 | 0 | exit 1: {:test 0, :pass 0, :fail 0, :error 0} |
| `com.rpl.specter.core-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.NewMultiFn |
| `com.rpl.specter.test-helpers` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.NewMultiFn |
| `com.rpl.specter.zipper-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.NewMultiFn |
| `com.stuartsierra.component-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.NewMultiFn |
| `com.stuartsierra.dependency-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.NewMultiFn |
| `com.wsscode.misc.coll-test` | fail | 28 | 51 | 0 | 15 |  |
| `com.wsscode.misc.macros-test` | pass | 1 | 2 | 0 | 0 |  |
| `com.wsscode.misc.math-test` | fail | 6 | 8 | 0 | 1 |  |
| `com.wsscode.misc.refs-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.NewMultiFn |
| `com.wsscode.misc.uuid-test` | fail | 1 | 0 | 1 | 0 |  |
| `comb.test.template` | fail | 2 | 0 | 0 | 6 |  |
| `contajners.impl-test` | blocked | — | — | — | — | exit 1: failed to load /contajners/impl: not found in load path |
| `core-match.core-tests` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.NewMultiFn |
| `cprop.smoke-test` | fail | 1 | 0 | 0 | 1 |  |
| `crispin.core-test` | blocked | — | — | — | — | exit 1: NO_SOURCE_PATH: error reading crispin/core.cljc: crispin/core.cljc:35:26: invalid regex: error parsing regexp: invalid character class range: `/p{XDigit}` |
| `datalog.parser-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.NewMultiFn |
| `datalog.parser.impl-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.NewMultiFn |
| `datalog.parser.pull-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.NewMultiFn |
| `datalog.parser.test.util` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `datalog.unparser-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.NewMultiFn |
| `docopt.core-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `doric.test.core` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.NewLazySeq |
| `doric.test.doctest` | blocked | 0 | 0 | 0 | 0 | exit 1: {:test 0, :pass 0, :fail 0, :error 0} |
| `doric.test.readme` | fail | 1 | 0 | 0 | 1 |  |
| `edn-query-language.core-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: Object |
| `environ.core-test` | blocked | — | — | — | — | exit 1: EvalASTMaybeHostForm: System/getProperties |
| `exoscale.coax-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: Object |
| `exoscale.interceptor-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/core/async: not found in load path |
| `exoscale.lingo.test.core-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: Object |
| `expectations.clojure.test-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.NewMultiFn |
| `expound.print-length-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/spec/alpha: not found in load path |
| `expound.problems-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/spec/alpha: not found in load path |
| `expound.spec-gen` | blocked | — | — | — | — | exit 1: failed to load /clojure/spec/alpha: not found in load path |
| `expound.specs-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/spec/alpha: not found in load path |
| `expound.test-utils` | blocked | — | — | — | — | exit 1: failed to load /clojure/spec/alpha: not found in load path |
| `failjure.test-core` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.NewMultiFn |
| `gaka.core-test` | fail | 7 | 0 | 0 | 7 |  |
| `hasch.test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.NewMultiFn |
| `hato.client-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/spec/alpha: not found in load path |
| `helins.binf.test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.NewMultiFn |
| `hiccup.core-test` | fail | 5 | 45 | 20 | 1 |  |
| `hiccup2.core-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.NewMultiFn |
| `hickory.test.convert` | blocked | — | — | — | — | exit 1: failed to load /hickory/convert: not found in load path |
| `hickory.test.core` | blocked | — | — | — | — | exit 1: failed to load /hickory/core: not found in load path |
| `hickory.test.hiccup-utils` | blocked | — | — | — | — | exit 1: failed to load /hickory/hiccup_utils: not found in load path |
| `hickory.test.render` | blocked | — | — | — | — | exit 1: failed to load /hickory/core: not found in load path |
| `hickory.test.select` | blocked | — | — | — | — | exit 1: failed to load /hickory/core: not found in load path |
| `hickory.test.zip` | blocked | — | — | — | — | exit 1: failed to load /hickory/core: not found in load path |
| `honey.sql-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `honey.sql.helpers-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `honey.sql.postgres-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `honeysql.core-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.NewMultiFn |
| `honeysql.format-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.NewMultiFn |
| `httpkit.client-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.NewDelay |
| `hugsql.babashka-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: *clojure-version* |
| `integrant.core-test` | blocked | — | — | — | — | exit 1: failed to load /integrant/core: not found in load path |
| `integrant.test.bar` | blocked | 0 | 0 | 0 | 0 | exit 1: {:test 0, :pass 0, :fail 0, :error 0} |
| `integrant.test.baz` | blocked | 0 | 0 | 0 | 0 | exit 1: {:test 0, :pass 0, :fail 0, :error 0} |
| `integrant.test.foo` | blocked | 0 | 0 | 0 | 0 | exit 1: {:test 0, :pass 0, :fail 0, :error 0} |
| `integrant.test.quz` | blocked | 0 | 0 | 0 | 0 | exit 1: {:test 0, :pass 0, :fail 0, :error 0} |
| `io.aviso.binary-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.NewLazySeq |
| `jasentaa.collections-test` | fail | 3 | 1 | 0 | 10 |  |
| `jasentaa.parser.basic-test` | fail | 4 | 0 | 0 | 15 |  |
| `jasentaa.parser.combinators-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: clojure.lang.Symbol |
| `jasentaa.position-test` | fail | 5 | 0 | 0 | 9 |  |
| `jasentaa.test-helpers` | blocked | 0 | 0 | 0 | 0 | exit 1: {:test 0, :pass 0, :fail 0, :error 0} |
| `jasentaa.worked-example-1` | blocked | — | — | — | — | exit 1: unable to resolve symbol: clojure.lang.Symbol |
| `jasentaa.worked-example-2` | blocked | — | — | — | — | exit 1: unable to resolve symbol: clojure.lang.Symbol |
| `java-http-clj.smoke-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: HttpClient |
| `lambdaisland.regal-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `lambdaisland.regal.test-util` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `linked.map-test` | blocked | — | — | — | — | exit 1: NO_SOURCE_PATH: failed to load /linked/map_test: not found in load path |
| `loom.test.alg` | blocked | — | — | — | — | exit 1: failed to load /loom/graph: not found in load path |
| `loom.test.alg-generic` | blocked | — | — | — | — | exit 1: failed to load /loom/alg_generic: not found in load path |
| `loom.test.attr` | blocked | — | — | — | — | exit 1: failed to load /loom/graph: not found in load path |
| `loom.test.compliance-tester` | blocked | — | — | — | — | exit 1: failed to load /loom/graph: not found in load path |
| `loom.test.derived` | blocked | — | — | — | — | exit 1: failed to load /loom/derived: not found in load path |
| `loom.test.flow` | blocked | — | — | — | — | exit 1: failed to load /loom/graph: not found in load path |
| `loom.test.graph` | blocked | — | — | — | — | exit 1: failed to load /loom/graph: not found in load path |
| `loom.test.label` | blocked | — | — | — | — | exit 1: failed to load /loom/graph: not found in load path |
| `loom.test.network-simplex` | blocked | — | — | — | — | exit 1: failed to load /loom/network_simplex: not found in load path |
| `malli.clj-kondo-test` | blocked | — | — | — | — | exit 1: failed to load /malli/clj_kondo: not found in load path |
| `malli.core-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.NewMultiFn |
| `malli.destructure-test` | blocked | — | — | — | — | exit 1: failed to load /malli/core: not found in load path |
| `malli.dot-test` | blocked | — | — | — | — | exit 1: failed to load /malli/dot: not found in load path |
| `malli.error-test` | blocked | — | — | — | — | exit 1: failed to load /malli/core: not found in load path |
| `malli.experimental-test` | blocked | — | — | — | — | exit 1: failed to load /malli/dev: not found in load path |
| `malli.experimental.time-test` | blocked | — | — | — | — | exit 1: failed to load /malli/core: not found in load path |
| `malli.experimental.time.transform-test` | blocked | — | — | — | — | exit 1: failed to load /malli/core: not found in load path |
| `malli.instrument-test` | blocked | — | — | — | — | exit 1: failed to load /malli/core: not found in load path |
| `malli.json-schema-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.NewMultiFn |
| `malli.plantuml-test` | blocked | — | — | — | — | exit 1: failed to load /malli/plantuml: not found in load path |
| `malli.provider-test` | blocked | — | — | — | — | exit 1: failed to load /malli/core: not found in load path |
| `malli.registry-test` | blocked | — | — | — | — | exit 1: failed to load /malli/core: not found in load path |
| `malli.swagger-test` | blocked | — | — | — | — | exit 1: failed to load /malli/core: not found in load path |
| `malli.transform-test` | blocked | — | — | — | — | exit 1: failed to load /malli/core: not found in load path |
| `malli.util-test` | blocked | — | — | — | — | exit 1: failed to load /malli/core: not found in load path |
| `markdown.md-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.NewMultiFn |
| `me.raynes.core-test` | blocked | — | — | — | — | exit 1: failed to load /me/raynes/fs: not found in load path |
| `meander.defsyntax-test` | blocked | — | — | — | — | exit 1: failed to load /meander/epsilon: not found in load path |
| `meander.defsyntax-test.gh-145` | blocked | — | — | — | — | exit 1: failed to load /meander/epsilon: not found in load path |
| `meander.epsilon-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/spec/alpha: not found in load path |
| `meander.interpreter.epsilon-test` | blocked | — | — | — | — | exit 1: failed to load /meander/interpreter/epsilon: not found in load path |
| `meander.match.check.epsilon-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/spec/alpha: not found in load path |
| `meander.match.epsilon-test` | blocked | — | — | — | — | exit 1: failed to load /meander/match/epsilon: not found in load path |
| `meander.match.ir.epsilon-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/spec/alpha: not found in load path |
| `meander.matrix.epsilon-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/spec/alpha: not found in load path |
| `meander.strategy.epsilon-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.NewMultiFn |
| `meander.substitute.epsilon-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.NewMultiFn |
| `meander.syntax.epsilon-test` | blocked | — | — | — | — | exit 1: failed to load /meander/epsilon: not found in load path |
| `medley.core-test` | fail | 43 | 107 | 2 | 81 |  |
| `meta-merge.core-test` | fail | 1 | 2 | 0 | 15 |  |
| `minimallist.core-test` | fail | 2 | 317 | 0 | 21 |  |
| `minimallist.util-test` | fail | 4 | 12 | 0 | 3 |  |
| `missing.test.assertions-test` | blocked | — | — | — | — | exit 1: failed to load /missing/test/assertions: not found in load path |
| `missing.test.old-methods` | blocked | — | — | — | — | exit 1: failed to load /missing/test/assertions: not found in load path |
| `msgpack.core-check` | blocked | — | — | — | — | exit 1: EvalASTMaybeHostForm: Charset/forName |
| `msgpack.core-test` | blocked | — | — | — | — | exit 1: EvalASTMaybeHostForm: Charset/forName |
| `multigrep.core-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.NewMultiFn |
| `net.cgrand.xforms-test` | blocked | — | — | — | — | exit 1: failed to load /net/cgrand/xforms: not found in load path |
| `nextjournal.markdown-test` | blocked | — | — | — | — | exit 1: failed to load /matcher_combinators/ansi_color: not found in load path |
| `nextjournal.markdown.multi-threading-test` | blocked | — | — | — | — | exit 1: failed to load /nextjournal/markdown: not found in load path |
| `odoyle.rules-test` | blocked | — | — | — | — | exit 1: failed to load /odoyle/rules: not found in load path |
| `ol.sfv.api-test` | blocked | — | — | — | — | exit 1: failed to load /ol/sfv: not found in load path |
| `ol.sfv.conformance-test` | blocked | — | — | — | — | exit 1: failed to load /alphabase/base32: not found in load path |
| `ol.sfv.error-test` | blocked | — | — | — | — | exit 1: failed to load /ol/sfv: not found in load path |
| `ol.sfv.example-test` | blocked | — | — | — | — | exit 1: failed to load /ol/sfv: not found in load path |
| `ol.sfv.parser-test` | blocked | — | — | — | — | exit 1: failed to load /ol/sfv/impl: not found in load path |
| `ol.sfv.serialization-test` | blocked | — | — | — | — | exit 1: failed to load /ol/sfv: not found in load path |
| `omniconf.core-test` | blocked | — | — | — | — | exit 1: failed to load pprint/utilities: not found in load path |
| `orchestra.core-test` | blocked | — | — | — | — | exit 1: {} |
| `orchestra.expound-test` | blocked | — | — | — | — | exit 1: {} |
| `orchestra.make-fns` | blocked | — | — | — | — | exit 1: {} |
| `orchestra.many-fns` | blocked | — | — | — | — | exit 1: {} |
| `orchestra.reload-test` | blocked | — | — | — | — | exit 1: {} |
| `plumbing.core-test` | blocked | — | — | — | — | exit 1: failed to load /schema/core: not found in load path |
| `portal.bench` | blocked | 0 | 0 | 0 | 0 | exit 1: {:test 0, :pass 0, :fail 0, :error 0} |
| `portal.e2e` | blocked | 0 | 0 | 0 | 0 | exit 1: {:test 0, :pass 0, :fail 0, :error 0} |
| `portal.runtime.cson-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.NewMultiFn |
| `portal.runtime.fs-test` | fail | 1 | 3 | 1 | 3 |  |
| `portal.test-runner` | blocked | — | — | — | — | exit 1: unable to resolve symbol: clojure.lang.IRef |
| `postmortem.core-test` | blocked | — | — | — | — | exit 1: failed to load /postmortem/core: not found in load path |
| `postmortem.instrument-test` | blocked | — | — | — | — | exit 1: failed to load /postmortem/core: not found in load path |
| `progrock.core-test` | fail | 5 | 7 | 0 | 8 |  |
| `promesa.tests.core-test` | blocked | — | — | — | — | exit 1: failed to load /promesa/core: not found in load path |
| `qbits.auspex-test` | blocked | — | — | — | — | exit 1: failed to load /qbits/auspex: not found in load path |
| `reifyhealth.specmonstah.core-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/spec/alpha: not found in load path |
| `reifyhealth.specmonstah.spec-gen-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/spec/alpha: not found in load path |
| `reifyhealth.specmonstah.test-data` | blocked | — | — | — | — | exit 1: failed to load /clojure/spec/alpha: not found in load path |
| `rewrite-clj.node-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.NewMultiFn |
| `rewrite-clj.node.coercer-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.NewMultiFn |
| `rewrite-clj.paredit-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.NewMultiFn |
| `rewrite-clj.parser-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: *clojure-version* |
| `rewrite-clj.zip-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.NewMultiFn |
| `rewrite-clj.zip.subedit-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.NewMultiFn |
| `riddley.walk-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: clojure.lang.Compiler |
| `ruuter.core-test` | blocked | — | — | — | — | exit 1: failed to load /ruuter/core: not found in load path |
| `schema.coerce-test` | blocked | — | — | — | — | exit 1: failed to load /schema/core: not found in load path |
| `schema.core-test` | blocked | — | — | — | — | exit 1: failed to load /schema/core: not found in load path |
| `schema.experimental.abstract-map-test` | blocked | — | — | — | — | exit 1: failed to load /schema/core: not found in load path |
| `schema.macros-test` | blocked | — | — | — | — | exit 1: failed to load /schema/core: not found in load path |
| `schema.test-test` | blocked | — | — | — | — | exit 1: failed to load /schema/core: not found in load path |
| `schema.utils-test` | blocked | — | — | — | — | exit 1: failed to load /schema/utils: not found in load path |
| `selmer.core-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `selmer.our-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `slingshot.slingshot-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `slingshot.support-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `slingshot.test-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `sluj.core-test` | blocked | — | — | — | — | exit 1: failed to load /sluj/core: not found in load path |
| `swirrl.dogstatsd-test` | blocked | — | — | — | — | exit 1: failed to load /swirrl/dogstatsd: not found in load path |
| `table.core-test` | blocked | — | — | — | — | exit 1: failed to load /table/core: not found in load path |
| `table.width-test` | blocked | — | — | — | — | exit 1: failed to load /table/width: not found in load path |
| `test-check.smoke-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.NewMultiFn |
| `testdoc.core-test` | fail | 8 | 3 | 0 | 10 |  |
| `testdoc.style.code-first-test` | fail | 2 | 0 | 0 | 2 |  |
| `testdoc.style.repl-test` | fail | 2 | 0 | 0 | 4 |  |
| `vault.client.http-test` | blocked | — | — | — | — | exit 1: EvalASTMaybeHostForm: Runtime/getRuntime |
| `vault.lease-test` | blocked | — | — | — | — | exit 1: EvalASTMaybeHostForm: Runtime/getRuntime |
| `version-clj.compare-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.NewMultiFn |
| `version-clj.core-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.NewMultiFn |
| `version-clj.split-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.NewMultiFn |
| `version-clj.via-use-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.NewMultiFn |

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
| `cprop.clj` | waived | fail | fail | exit 1: failed to load /babashka/classpath: not found in load path |
| `db_who.clj` | waived | fail | fail | exit 1: failed to load /clojure/java/shell: not found in load path |
| `digitalocean-ping.clj` | waived | fail | fail | exit 1: failed to load /clojure/java/shell: not found in load path |
| `download-aliases.clj` | waived | fail | fail | exit 1: <unknown-file>:1:1:	(compile (quote main.core)) |
| `fzf.clj` | waived | fail | fail | exit 1: unable to resolve symbol: runtime.GOOS |
| `hsqldb_unused_vars.clj` | waived | fail | fail | exit 1: failed to load /clojure/pprint: not found in load path |
| `htmx_todoapp.clj` | waived | fail | fail | exit 1: <preloads>: String index out of range |
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
| `outdated.clj` | waived | fail | fail | exit 1: failed to load /clojure/java/shell: not found in load path |
| `pom_version_get.clj` | run | fail | fail | exit 1: failed to load /clojure/data/xml: not found in load path |
| `pom_version_get_xml_zip.clj` | waived | fail | fail | exit 1: failed to load /babashka/deps: not found in load path |
| `pom_version_set.clj` | waived | fail | fail | exit 1: failed to load /clojure/data/xml: not found in load path |
| `portal.clj` | waived | fail | fail | exit 1: failed to load /babashka/deps: not found in load path |
| `process_builder.clj` | run | fail | fail | exit 1: unable to resolve symbol: ProcessBuilder |
| `pst.clj` | waived | fail | fail | exit 1: EvalASTMaybeHostForm: java.time.ZonedDateTime/now |
| `random_doc.clj` | waived | fail | fail | exit 1: cannot compile /clojure/repl: filesystem is not writable |
| `sqlite.clj` | waived | fail | fail | exit 1: bad binding form: :ns |
| `torrent-viewer.clj` | waived | fail | fail | exit 1: failed to load /bencode/core: not found in load path |
| `tree.clj` | run | fail | fail | exit 1: <preloads>: String index out of range |
| `vim.clj` | waived | fail | fail | exit 1: unable to resolve symbol: ProcessBuilder |
| `whatsapp_frequencies.clj` | waived | fail | fail | exit 1: failed to load /clojure/pprint: not found in load path |
| `which.clj` | run | fail | fail | exit 1: unsupported value type lang.ArityFn: {<nil> [<ADDR> <ADDR> <ADDR> <nil> <nil> <nil>] map[] 2 {<nil> 2 <ADDR>} 2} |
| `wiki-translate.clj` | waived | fail | fail | exit 1: unable to resolve symbol: runtime.GOOS |
| `xml-example.clj` | run | fail | fail | exit 1: EvalASTMaybeHostForm: xml/indent-str |

## Result policy

Both targets run every applicable case before returning a nonzero status when failures remain. Raw logs and filtered-run results stay under `.cache/upstream-tests/`; only an unfiltered full run updates the committed snapshots rendered here.
