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
| 111 | 261 | 261 | 2 | 2 | 257 | 9 | 48 | 20 | 2 |

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
| `aero.core-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `again.core-test` | blocked | — | — | — | — | exit 1: failed to load /again/core: not found in load path |
| `arrangement.core-test` | blocked | — | — | — | — | exit 1: failed to load /arrangement/core: not found in load path |
| `babashka.curl-test` | pass | 1 | 0 | 0 | 0 |  |
| `babashka.process-exec-test` | blocked | — | — | — | — | exit 1: failed to load /babashka/process_exec_test: not found in load path |
| `babashka.process-test` | blocked | — | — | — | — | exit 1: failed to load /babashka/process_test: not found in load path |
| `babashka.statecharts-test` | blocked | — | — | — | — | exit 1: failed to load /com/fulcrologic/statecharts: not found in load path |
| `better-cond.core-test` | blocked | — | — | — | — | exit 1: failed to load /better_cond/core: not found in load path |
| `bond.assertions-test` | blocked | — | — | — | — | exit 1: failed to load /bond/assertions: not found in load path |
| `bond.james-test` | blocked | — | — | — | — | exit 1: failed to load /bond/james: not found in load path |
| `bond.target-data` | blocked | 0 | 0 | 0 | 0 | exit 1: {:test 0, :pass 0, :fail 0, :error 0} |
| `borkdude.deps.smoke-test` | fail | 1 | 0 | 0 | 1 |  |
| `camel-snake-kebab.core-test` | blocked | — | — | — | — | exit 1: failed to load /camel_snake_kebab/core: not found in load path |
| `camel-snake-kebab.extras-test` | blocked | — | — | — | — | exit 1: failed to load /camel_snake_kebab/core: not found in load path |
| `camel-snake-kebab.internals.string-separator-test` | blocked | — | — | — | — | exit 1: failed to load /camel_snake_kebab/internals/string_separator: not found in load path |
| `cheshire.test.core` | blocked | — | — | — | — | exit 1: failed to load /cheshire/factory: not found in load path |
| `clarktown.core-test` | blocked | — | — | — | — | exit 1: failed to load /clarktown/core: not found in load path |
| `clarktown.parsers.bold-test` | blocked | — | — | — | — | exit 1: failed to load /clarktown/parsers/bold: not found in load path |
| `clarktown.parsers.code-block-test` | blocked | — | — | — | — | exit 1: failed to load /clarktown/parsers/code_block: not found in load path |
| `clarktown.parsers.empty-block-test` | blocked | — | — | — | — | exit 1: failed to load /clarktown/parsers/empty_block: not found in load path |
| `clarktown.parsers.heading-block-test` | blocked | — | — | — | — | exit 1: failed to load /clarktown/parsers/heading_block: not found in load path |
| `clarktown.parsers.horizontal-line-block-test` | blocked | — | — | — | — | exit 1: failed to load /clarktown/parsers/horizontal_line_block: not found in load path |
| `clarktown.parsers.inline-code-test` | blocked | — | — | — | — | exit 1: failed to load /clarktown/parsers/inline_code: not found in load path |
| `clarktown.parsers.italic-test` | blocked | — | — | — | — | exit 1: failed to load /clarktown/parsers/italic: not found in load path |
| `clarktown.parsers.link-and-image-test` | blocked | — | — | — | — | exit 1: failed to load /clarktown/parsers/link_and_image: not found in load path |
| `clarktown.parsers.quote-block-test` | blocked | — | — | — | — | exit 1: failed to load /clarktown/parsers/quote_block: not found in load path |
| `clarktown.parsers.strikethrough-test` | blocked | — | — | — | — | exit 1: failed to load /clarktown/parsers/strikethrough: not found in load path |
| `cli-matic.core-test` | blocked | — | — | — | — | exit 1: failed to load /cli_matic/platform: not found in load path |
| `cli-matic.help-gen-test` | blocked | — | — | — | — | exit 1: failed to load /cli_matic/help_gen: not found in load path |
| `cli-matic.presets-test` | blocked | — | — | — | — | exit 1: failed to load /cljc/java_time/zone_id: not found in load path |
| `cli-matic.utils-candidates-test` | blocked | — | — | — | — | exit 1: failed to load /cli_matic/utils_candidates: not found in load path |
| `cli-matic.utils-convert-config-test` | blocked | — | — | — | — | exit 1: failed to load /cli_matic/optionals: not found in load path |
| `cli-matic.utils-test` | blocked | — | — | — | — | exit 1: failed to load /cli_matic/utils: not found in load path |
| `cli-matic.utils-v2-test` | blocked | — | — | — | — | exit 1: failed to load /cli_matic/optionals: not found in load path |
| `clj-commons.digest-test` | blocked | — | — | — | — | exit 1: failed to load /clj_commons/digest: not found in load path |
| `clj-http.lite.client-test` | blocked | — | — | — | — | exit 1: failed to load /clj_http/lite/client: not found in load path |
| `clj-http.lite.test-runner` | blocked | — | — | — | — | exit 1: failed to load /clj_http/lite/client: not found in load path |
| `clj-stacktrace.core-test` | blocked | — | — | — | — | exit 1: failed to load /clj_stacktrace/core: not found in load path |
| `clj-stacktrace.repl-test` | blocked | — | — | — | — | exit 1: failed to load /clj_stacktrace/utils: not found in load path |
| `clj-yaml.core-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.Import |
| `cljc.java-time-test` | blocked | — | — | — | — | exit 1: failed to load /cljc/java_time/temporal/chrono_field: not found in load path |
| `clojure-csv.test.core` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.Import |
| `clojure-csv.test.utils` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.Import |
| `clojure.algo.test-monads` | blocked | — | — | — | — | exit 1: failed to load /clojure/algo/monads: not found in load path |
| `clojure.core.cache-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/core/cache: not found in load path |
| `clojure.core.cache.wrapped-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/core/cache/wrapped: not found in load path |
| `clojure.data.csv-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.Import |
| `clojure.data.generators-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/data/generators: not found in load path |
| `clojure.data.json-compat-0-1-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/data/json: not found in load path |
| `clojure.data.json-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/data/json: not found in load path |
| `clojure.data.json-test-suite-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/data/json: not found in load path |
| `clojure.data.zip-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/data/xml: not found in load path |
| `clojure.math.test-combinatorics` | blocked | — | — | — | — | exit 1: failed to load /clojure/math/combinatorics: not found in load path |
| `clojure.math.test-numeric-tower` | blocked | — | — | — | — | exit 1: failed to load /clojure/math/numeric_tower: not found in load path |
| `clojure.term.colors-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/term/colors: not found in load path |
| `clojure.test-clojure.instr` | blocked | — | — | — | — | exit 1: failed to load /clojure/spec/alpha: not found in load path |
| `clojure.test-clojure.spec` | blocked | — | — | — | — | exit 1: failed to load /clojure/spec/alpha: not found in load path |
| `clojure.tools.gitlibs.test-impl` | blocked | — | — | — | — | exit 1: failed to load /clojure/tools/gitlibs/impl: not found in load path |
| `clojure.tools.namespace.dependency-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/tools/namespace/dependency: not found in load path |
| `clojure.tools.namespace.find-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.Import |
| `clojure.tools.namespace.move-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/tools/namespace/move: not found in load path |
| `clojure.tools.namespace.parse-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/tools/namespace/parse: not found in load path |
| `clojure.tools.namespace.test-helpers` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.Import |
| `clojure.tools.test-gitlibs` | blocked | — | — | — | — | exit 1: failed to load /clojure/tools/gitlibs: not found in load path |
| `cloverage.args-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/tools/cli: not found in load path |
| `cloverage.dependency-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/tools/namespace/dependency: not found in load path |
| `cloverage.instrument-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/pprint: not found in load path |
| `cloverage.report.console-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: cloverage.report.console |
| `cloverage.source-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/tools/namespace/file: not found in load path |
| `cognitect.test-runner-test` | blocked | — | — | — | — | exit 1: failed to load /cognitect/test_runner: not found in load path |
| `cognitect.test-runner.sample-property-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/test/check: not found in load path |
| `cognitect.test-runner.samples-test` | pass | 2 | 3 | 0 | 0 |  |
| `com.potetm.fusebox.bulwark-test` | blocked | — | — | — | — | exit 1: failed to load /com/potetm/fusebox/bulkhead: not found in load path |
| `com.potetm.fusebox.circuit-breaker-test` | blocked | — | — | — | — | exit 1: failed to load /com/potetm/fusebox/circuit_breaker: not found in load path |
| `com.potetm.fusebox.fallback-test` | blocked | — | — | — | — | exit 1: failed to load /com/potetm/fusebox/fallback: not found in load path |
| `com.potetm.fusebox.memoize-test` | blocked | — | — | — | — | exit 1: failed to load /com/potetm/fusebox/memoize: not found in load path |
| `com.potetm.fusebox.registry-test` | blocked | — | — | — | — | exit 1: failed to load /com/potetm/fusebox/registry: not found in load path |
| `com.potetm.fusebox.retry-test` | blocked | — | — | — | — | exit 1: failed to load /com/potetm/fusebox/retry: not found in load path |
| `com.rpl.specter.cljs-test-helpers` | blocked | 0 | 0 | 0 | 0 | exit 1: {:test 0, :pass 0, :fail 0, :error 0} |
| `com.rpl.specter.core-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `com.rpl.specter.test-helpers` | blocked | — | — | — | — | exit 1: failed to load /clojure/test/check/generators: not found in load path |
| `com.rpl.specter.zipper-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `com.stuartsierra.component-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/set: not found in load path |
| `com.stuartsierra.dependency-test` | blocked | — | — | — | — | exit 1: failed to load /com/stuartsierra/dependency: not found in load path |
| `com.wsscode.misc.coll-test` | blocked | — | — | — | — | exit 1: failed to load /com/wsscode/misc/coll: not found in load path |
| `com.wsscode.misc.macros-test` | blocked | — | — | — | — | exit 1: failed to load /com/wsscode/misc/macros: not found in load path |
| `com.wsscode.misc.math-test` | blocked | — | — | — | — | exit 1: failed to load /com/wsscode/misc/math: not found in load path |
| `com.wsscode.misc.refs-test` | blocked | — | — | — | — | exit 1: failed to load /com/wsscode/misc/refs: not found in load path |
| `com.wsscode.misc.uuid-test` | blocked | — | — | — | — | exit 1: failed to load /com/wsscode/misc/uuid: not found in load path |
| `comb.test.template` | blocked | — | — | — | — | exit 1: failed to load /comb/template: not found in load path |
| `contajners.impl-test` | blocked | — | — | — | — | exit 1: failed to load /contajners/impl: not found in load path |
| `core-match.core-tests` | blocked | — | — | — | — | exit 1: failed to load /clojure/core/match: not found in load path |
| `cprop.smoke-test` | blocked | — | — | — | — | exit 1: failed to load /cprop/core: not found in load path |
| `crispin.core-test` | blocked | — | — | — | — | exit 1: failed to load /crispin/core: not found in load path |
| `datalog.parser-test` | blocked | — | — | — | — | exit 1: failed to load /datalog/parser: not found in load path |
| `datalog.parser.impl-test` | blocked | — | — | — | — | exit 1: failed to load /datalog/parser/impl: not found in load path |
| `datalog.parser.pull-test` | blocked | — | — | — | — | exit 1: failed to load /datalog/parser/pull: not found in load path |
| `datalog.parser.test.util` | blocked | — | — | — | — | exit 1: failed to load /:as/test: not found in load path |
| `datalog.unparser-test` | blocked | — | — | — | — | exit 1: failed to load /datalog/unparser: not found in load path |
| `docopt.core-test` | blocked | — | — | — | — | exit 1: failed to load /docopt/core: not found in load path |
| `doric.test.core` | blocked | — | — | — | — | exit 1: failed to load /doric/core: not found in load path |
| `doric.test.doctest` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.Import |
| `doric.test.readme` | blocked | — | — | — | — | exit 1: unable to resolve symbol: github.com:glojurelang:glojure:pkg:lang.Import |
| `edn-query-language.core-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/spec/alpha: not found in load path |
| `environ.core-test` | blocked | — | — | — | — | exit 1: failed to load /environ/core: not found in load path |
| `exoscale.coax-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/spec/alpha: not found in load path |
| `exoscale.interceptor-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/core/async: not found in load path |
| `exoscale.lingo.test.core-test` | blocked | — | — | — | — | exit 1: failed to load /exoscale/lingo: not found in load path |
| `expectations.clojure.test-test` | blocked | — | — | — | — | exit 1: failed to load /expectations/clojure/test: not found in load path |
| `expound.print-length-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/spec/alpha: not found in load path |
| `expound.problems-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/spec/alpha: not found in load path |
| `expound.spec-gen` | blocked | — | — | — | — | exit 1: failed to load /clojure/spec/alpha: not found in load path |
| `expound.specs-test` | blocked | — | — | — | — | exit 1: failed to load /expound/specs: not found in load path |
| `expound.test-utils` | blocked | — | — | — | — | exit 1: failed to load /clojure/spec/alpha: not found in load path |
| `failjure.test-core` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `gaka.core-test` | blocked | — | — | — | — | exit 1: failed to load /gaka/core: not found in load path |
| `hasch.test` | blocked | — | — | — | — | exit 1: failed to load /hasch/core: not found in load path |
| `hato.client-test` | blocked | — | — | — | — | exit 1: failed to load /hato/client: not found in load path |
| `helins.binf.test` | blocked | — | — | — | — | exit 1: failed to load /helins/binf: not found in load path |
| `hiccup.core-test` | fail | 5 | 45 | 20 | 1 |  |
| `hiccup2.core-test` | blocked | — | — | — | — | exit 1: failed to load /hiccup/util: not found in load path |
| `hickory.test.convert` | blocked | — | — | — | — | exit 1: failed to load /hickory/convert: not found in load path |
| `hickory.test.core` | blocked | — | — | — | — | exit 1: failed to load /hickory/core: not found in load path |
| `hickory.test.hiccup-utils` | blocked | — | — | — | — | exit 1: failed to load /hickory/hiccup_utils: not found in load path |
| `hickory.test.render` | blocked | — | — | — | — | exit 1: failed to load /hickory/core: not found in load path |
| `hickory.test.select` | blocked | — | — | — | — | exit 1: failed to load /hickory/core: not found in load path |
| `hickory.test.zip` | blocked | — | — | — | — | exit 1: failed to load /hickory/core: not found in load path |
| `honey.sql-test` | blocked | — | — | — | — | exit 1: failed to load /honey/sql: not found in load path |
| `honey.sql.helpers-test` | blocked | — | — | — | — | exit 1: failed to load /honey/sql: not found in load path |
| `honey.sql.postgres-test` | blocked | — | — | — | — | exit 1: failed to load /honey/sql/helpers: not found in load path |
| `honeysql.core-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `honeysql.format-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `httpkit.client-test` | blocked | — | — | — | — | exit 1: failed to load /org/httpkit/client: not found in load path |
| `hugsql.babashka-test` | blocked | — | — | — | — | exit 1: failed to load /hugsql/core: not found in load path |
| `integrant.core-test` | blocked | — | — | — | — | exit 1: failed to load /integrant/core: not found in load path |
| `integrant.test.bar` | blocked | 0 | 0 | 0 | 0 | exit 1: {:test 0, :pass 0, :fail 0, :error 0} |
| `integrant.test.baz` | blocked | 0 | 0 | 0 | 0 | exit 1: {:test 0, :pass 0, :fail 0, :error 0} |
| `integrant.test.foo` | blocked | 0 | 0 | 0 | 0 | exit 1: {:test 0, :pass 0, :fail 0, :error 0} |
| `integrant.test.quz` | blocked | 0 | 0 | 0 | 0 | exit 1: {:test 0, :pass 0, :fail 0, :error 0} |
| `io.aviso.binary-test` | blocked | — | — | — | — | exit 1: failed to load /io/aviso/binary: not found in load path |
| `jasentaa.collections-test` | blocked | — | — | — | — | exit 1: failed to load /jasentaa/collections: not found in load path |
| `jasentaa.parser.basic-test` | blocked | — | — | — | — | exit 1: failed to load /jasentaa/monad: not found in load path |
| `jasentaa.parser.combinators-test` | blocked | — | — | — | — | exit 1: failed to load /jasentaa/monad: not found in load path |
| `jasentaa.position-test` | blocked | — | — | — | — | exit 1: failed to load /jasentaa/position: not found in load path |
| `jasentaa.test-helpers` | blocked | — | — | — | — | exit 1: failed to load /jasentaa/monad: not found in load path |
| `jasentaa.worked-example-1` | blocked | — | — | — | — | exit 1: failed to load /jasentaa/monad: not found in load path |
| `jasentaa.worked-example-2` | blocked | — | — | — | — | exit 1: failed to load /jasentaa/monad: not found in load path |
| `java-http-clj.smoke-test` | blocked | — | — | — | — | exit 1: failed to load /java_http_clj/core: not found in load path |
| `lambdaisland.regal-test` | blocked | — | — | — | — | exit 1: failed to load /lambdaisland/regal: not found in load path |
| `lambdaisland.regal.test-util` | blocked | — | — | — | — | exit 1: failed to load /lambdaisland/regal: not found in load path |
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
| `malli.core-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/test/check/generators: not found in load path |
| `malli.destructure-test` | blocked | — | — | — | — | exit 1: failed to load /malli/core: not found in load path |
| `malli.dot-test` | blocked | — | — | — | — | exit 1: failed to load /malli/dot: not found in load path |
| `malli.error-test` | blocked | — | — | — | — | exit 1: failed to load /malli/core: not found in load path |
| `malli.experimental-test` | blocked | — | — | — | — | exit 1: failed to load /malli/dev: not found in load path |
| `malli.experimental.time-test` | blocked | — | — | — | — | exit 1: failed to load /malli/core: not found in load path |
| `malli.experimental.time.transform-test` | blocked | — | — | — | — | exit 1: failed to load /malli/core: not found in load path |
| `malli.instrument-test` | blocked | — | — | — | — | exit 1: failed to load /malli/core: not found in load path |
| `malli.json-schema-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/test/check/generators: not found in load path |
| `malli.plantuml-test` | blocked | — | — | — | — | exit 1: failed to load /malli/plantuml: not found in load path |
| `malli.provider-test` | blocked | — | — | — | — | exit 1: failed to load /malli/core: not found in load path |
| `malli.registry-test` | blocked | — | — | — | — | exit 1: failed to load /malli/core: not found in load path |
| `malli.swagger-test` | blocked | — | — | — | — | exit 1: failed to load /malli/core: not found in load path |
| `malli.transform-test` | blocked | — | — | — | — | exit 1: failed to load /malli/core: not found in load path |
| `malli.util-test` | blocked | — | — | — | — | exit 1: failed to load /malli/core: not found in load path |
| `markdown.md-test` | blocked | — | — | — | — | exit 1: failed to load /markdown/core: not found in load path |
| `me.raynes.core-test` | blocked | — | — | — | — | exit 1: failed to load /me/raynes/fs: not found in load path |
| `meander.defsyntax-test` | blocked | — | — | — | — | exit 1: failed to load /meander/epsilon: not found in load path |
| `meander.defsyntax-test.gh-145` | blocked | — | — | — | — | exit 1: failed to load /meander/epsilon: not found in load path |
| `meander.epsilon-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/spec/alpha: not found in load path |
| `meander.interpreter.epsilon-test` | blocked | — | — | — | — | exit 1: failed to load /meander/interpreter/epsilon: not found in load path |
| `meander.match.check.epsilon-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/spec/alpha: not found in load path |
| `meander.match.epsilon-test` | blocked | — | — | — | — | exit 1: failed to load /meander/match/epsilon: not found in load path |
| `meander.match.ir.epsilon-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/spec/alpha: not found in load path |
| `meander.matrix.epsilon-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/spec/alpha: not found in load path |
| `meander.strategy.epsilon-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/test/check/clojure_test: not found in load path |
| `meander.substitute.epsilon-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/test/check/clojure_test: not found in load path |
| `meander.syntax.epsilon-test` | blocked | — | — | — | — | exit 1: failed to load /meander/epsilon: not found in load path |
| `medley.core-test` | blocked | — | — | — | — | exit 1: failed to load /medley/core: not found in load path |
| `meta-merge.core-test` | blocked | — | — | — | — | exit 1: failed to load /meta_merge/core: not found in load path |
| `minimallist.core-test` | blocked | — | — | — | — | exit 1: failed to load /minimallist/core: not found in load path |
| `minimallist.util-test` | blocked | — | — | — | — | exit 1: failed to load /minimallist/util: not found in load path |
| `missing.test.assertions-test` | blocked | — | — | — | — | exit 1: failed to load /missing/test/assertions: not found in load path |
| `missing.test.old-methods` | blocked | — | — | — | — | exit 1: failed to load /missing/test/assertions: not found in load path |
| `msgpack.core-check` | blocked | — | — | — | — | exit 1: failed to load /msgpack/core: not found in load path |
| `msgpack.core-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/walk: not found in load path |
| `multigrep.core-test` | blocked | — | — | — | — | exit 1: failed to load /multigrep/core: not found in load path |
| `net.cgrand.xforms-test` | blocked | — | — | — | — | exit 1: failed to load /net/cgrand/xforms: not found in load path |
| `nextjournal.markdown-test` | blocked | — | — | — | — | exit 1: failed to load /matcher_combinators/ansi_color: not found in load path |
| `nextjournal.markdown.multi-threading-test` | blocked | — | — | — | — | exit 1: failed to load /nextjournal/markdown: not found in load path |
| `odoyle.rules-test` | blocked | — | — | — | — | exit 1: failed to load /odoyle/rules: not found in load path |
| `ol.sfv.api-test` | blocked | — | — | — | — | exit 1: failed to load /ol/sfv: not found in load path |
| `ol.sfv.conformance-test` | blocked | — | — | — | — | exit 1: failed to load /alphabase/base32: not found in load path |
| `ol.sfv.error-test` | blocked | — | — | — | — | exit 1: failed to load /ol/sfv: not found in load path |
| `ol.sfv.example-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/walk: not found in load path |
| `ol.sfv.parser-test` | blocked | — | — | — | — | exit 1: failed to load /ol/sfv/impl: not found in load path |
| `ol.sfv.serialization-test` | blocked | — | — | — | — | exit 1: failed to load /ol/sfv: not found in load path |
| `omniconf.core-test` | blocked | — | — | — | — | exit 1: failed to load /omniconf/core: not found in load path |
| `orchestra.core-test` | blocked | — | — | — | — | exit 1: {} |
| `orchestra.expound-test` | blocked | — | — | — | — | exit 1: {} |
| `orchestra.make-fns` | blocked | — | — | — | — | exit 1: {} |
| `orchestra.many-fns` | blocked | — | — | — | — | exit 1: {} |
| `orchestra.reload-test` | blocked | — | — | — | — | exit 1: {} |
| `plumbing.core-test` | blocked | — | — | — | — | exit 1: failed to load /schema/core: not found in load path |
| `portal.bench` | blocked | 0 | 0 | 0 | 0 | exit 1: {:test 0, :pass 0, :fail 0, :error 0} |
| `portal.e2e` | blocked | — | — | — | — | exit 1: failed to load /portal/colors: not found in load path |
| `portal.runtime.cson-test` | blocked | — | — | — | — | exit 1: failed to load /portal/runtime/cson: not found in load path |
| `portal.runtime.fs-test` | blocked | — | — | — | — | exit 1: failed to load /portal/runtime/fs: not found in load path |
| `portal.test-runner` | blocked | — | — | — | — | exit 1: failed to load /portal/api: not found in load path |
| `postmortem.core-test` | blocked | — | — | — | — | exit 1: failed to load /postmortem/core: not found in load path |
| `postmortem.instrument-test` | blocked | — | — | — | — | exit 1: failed to load /postmortem/core: not found in load path |
| `progrock.core-test` | blocked | — | — | — | — | exit 1: failed to load /progrock/core: not found in load path |
| `promesa.tests.core-test` | blocked | — | — | — | — | exit 1: failed to load /promesa/core: not found in load path |
| `qbits.auspex-test` | blocked | — | — | — | — | exit 1: failed to load /qbits/auspex: not found in load path |
| `reifyhealth.specmonstah.core-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/spec/alpha: not found in load path |
| `reifyhealth.specmonstah.spec-gen-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/spec/alpha: not found in load path |
| `reifyhealth.specmonstah.test-data` | blocked | — | — | — | — | exit 1: failed to load /clojure/spec/alpha: not found in load path |
| `rewrite-clj.node-test` | blocked | — | — | — | — | exit 1: failed to load /rewrite_clj/node: not found in load path |
| `rewrite-clj.node.coercer-test` | blocked | — | — | — | — | exit 1: failed to load /rewrite_clj/node: not found in load path |
| `rewrite-clj.paredit-test` | blocked | — | — | — | — | exit 1: failed to load /rewrite_clj/node: not found in load path |
| `rewrite-clj.parser-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/tools/reader: not found in load path |
| `rewrite-clj.zip-test` | blocked | — | — | — | — | exit 1: failed to load /rewrite_clj/node: not found in load path |
| `rewrite-clj.zip.subedit-test` | blocked | — | — | — | — | exit 1: failed to load /rewrite_clj/zip: not found in load path |
| `riddley.walk-test` | blocked | — | — | — | — | exit 1: failed to load /riddley/compiler: not found in load path |
| `ruuter.core-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `schema.coerce-test` | blocked | — | — | — | — | exit 1: failed to load /schema/core: not found in load path |
| `schema.core-test` | blocked | — | — | — | — | exit 1: failed to load /:as/pprint: not found in load path |
| `schema.experimental.abstract-map-test` | blocked | — | — | — | — | exit 1: failed to load /schema/core: not found in load path |
| `schema.macros-test` | blocked | — | — | — | — | exit 1: failed to load /schema/core: not found in load path |
| `schema.test-test` | blocked | — | — | — | — | exit 1: failed to load /schema/core: not found in load path |
| `schema.utils-test` | blocked | — | — | — | — | exit 1: failed to load /schema/utils: not found in load path |
| `selmer.core-test` | blocked | — | — | — | — | exit 1: failed to load /selmer/filters: not found in load path |
| `selmer.our-test` | blocked | — | — | — | — | exit 1: failed to load /selmer/parser: not found in load path |
| `slingshot.slingshot-test` | blocked | — | — | — | — | exit 1: failed to load /slingshot/slingshot: not found in load path |
| `slingshot.support-test` | blocked | — | — | — | — | exit 1: failed to load /slingshot/slingshot: not found in load path |
| `slingshot.test-test` | blocked | — | — | — | — | exit 1: failed to load /slingshot/slingshot: not found in load path |
| `sluj.core-test` | blocked | — | — | — | — | exit 1: failed to load /sluj/core: not found in load path |
| `swirrl.dogstatsd-test` | blocked | — | — | — | — | exit 1: failed to load /swirrl/dogstatsd: not found in load path |
| `table.core-test` | blocked | — | — | — | — | exit 1: failed to load /table/core: not found in load path |
| `table.width-test` | blocked | — | — | — | — | exit 1: failed to load /table/width: not found in load path |
| `test-check.smoke-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/test/check: not found in load path |
| `testdoc.core-test` | blocked | — | — | — | — | exit 1: failed to load /testdoc/core: not found in load path |
| `testdoc.style.code-first-test` | blocked | — | — | — | — | exit 1: failed to load /testdoc/style/code_first: not found in load path |
| `testdoc.style.repl-test` | blocked | — | — | — | — | exit 1: failed to load /testdoc/style/repl: not found in load path |
| `vault.client.http-test` | blocked | — | — | — | — | exit 1: failed to load /vault/authenticate: not found in load path |
| `vault.lease-test` | blocked | — | — | — | — | exit 1: failed to load /vault/lease: not found in load path |
| `version-clj.compare-test` | blocked | — | — | — | — | exit 1: failed to load /version_clj/compare: not found in load path |
| `version-clj.core-test` | blocked | — | — | — | — | exit 1: failed to load /version_clj/core: not found in load path |
| `version-clj.split-test` | blocked | — | — | — | — | exit 1: failed to load /version_clj/split: not found in load path |
| `version-clj.via-use-test` | blocked | — | — | — | — | exit 1: failed to load /version_clj/core: not found in load path |

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
