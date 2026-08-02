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
| 111 | 261 | 261 | 50 | 23 | 188 | 404 | 2610 | 51 | 479 |

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
| `aero.core-test` | blocked | — | — | — | — | exit 1: bad binding form: :aero.core/incomplete? |
| `again.core-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: deftype |
| `arrangement.core-test` | pass | 9 | 110 | 0 | 0 |  |
| `babashka.curl-test` | pass | 1 | 0 | 0 | 0 |  |
| `babashka.process-exec-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `babashka.process-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `babashka.statecharts-test` | blocked | — | — | — | — | exit 1: failed to load /com/fulcrologic/guardrails/malli/core: not found in load path |
| `better-cond.core-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: clojure.spec.alpha.Spec |
| `bond.assertions-test` | pass | 5 | 32 | 0 | 0 |  |
| `bond.james-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `bond.target-data` | pass | 0 | 0 | 0 | 0 |  |
| `borkdude.deps.smoke-test` | fail | 1 | 0 | 0 | 1 |  |
| `camel-snake-kebab.core-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `camel-snake-kebab.extras-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `camel-snake-kebab.internals.string-separator-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `cheshire.test.core` | blocked | — | — | — | — | exit 1: unable to resolve symbol: JsonFactory |
| `clarktown.core-test` | fail | 1 | 0 | 1 | 0 |  |
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
| `cli-matic.core-test` | blocked | — | — | — | — | exit 1: failed to load /expound/alpha: not found in load path |
| `cli-matic.help-gen-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `cli-matic.presets-test` | blocked | — | — | — | — | exit 1: failed to load /cljc/java_time/zone_id: not found in load path |
| `cli-matic.utils-candidates-test` | pass | 2 | 10 | 0 | 0 |  |
| `cli-matic.utils-convert-config-test` | blocked | — | — | — | — | exit 1: failed to load /expound/alpha: not found in load path |
| `cli-matic.utils-test` | blocked | — | — | — | — | exit 1: failed to load /expound/alpha: not found in load path |
| `cli-matic.utils-v2-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `clj-commons.digest-test` | blocked | — | — | — | — | exit 1: EvalASTMaybeHostForm: Byte/TYPE |
| `clj-http.lite.client-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `clj-http.lite.test-runner` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `clj-stacktrace.core-test` | fail | 3 | 3 | 0 | 2 |  |
| `clj-stacktrace.repl-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `clj-yaml.core-test` | fail | 26 | 31 | 14 | 12 |  |
| `cljc.java-time-test` | blocked | — | — | — | — | exit 1: failed to load /cljs/java_time/interop: not found in load path |
| `clojure-csv.test.core` | fail | 11 | 23 | 0 | 38 |  |
| `clojure-csv.test.utils` | fail | 5 | 2 | 0 | 28 |  |
| `clojure.algo.test-monads` | blocked | — | — | — | — | exit 1: EvalASTMaybeHostForm: clojure.lang.Compiler/specials |
| `clojure.core.cache-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `clojure.core.cache.wrapped-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `clojure.data.csv-test` | fail | 4 | 19 | 3 | 2 |  |
| `clojure.data.generators-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: java.util.Random |
| `clojure.data.json-compat-0-1-test` | blocked | — | — | — | — | exit 1: failed to load pprint/utilities: not found in load path |
| `clojure.data.json-test` | blocked | — | — | — | — | exit 1: failed to load pprint/utilities: not found in load path |
| `clojure.data.json-test-suite-test` | blocked | — | — | — | — | exit 1: failed to load pprint/utilities: not found in load path |
| `clojure.data.zip-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/data/xml: not found in load path |
| `clojure.math.test-combinatorics` | pass | 18 | 999 | 0 | 0 |  |
| `clojure.math.test-numeric-tower` | blocked | — | — | — | — | exit 1: unable to resolve symbol: java.math.BigInteger |
| `clojure.term.colors-test` | pass | 1 | 0 | 0 | 0 |  |
| `clojure.test-clojure.instr` | blocked | — | — | — | — | exit 1: unable to resolve symbol: clojure.spec.alpha.Spec |
| `clojure.test-clojure.spec` | blocked | — | — | — | — | exit 1: unable to resolve symbol: clojure.spec.alpha.Spec |
| `clojure.tools.gitlibs.test-impl` | pass | 1 | 16 | 0 | 0 |  |
| `clojure.tools.namespace.dependency-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `clojure.tools.namespace.find-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: java.net.URLClassLoader |
| `clojure.tools.namespace.move-test` | pass | 1 | 6 | 0 | 0 |  |
| `clojure.tools.namespace.parse-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: deftype |
| `clojure.tools.namespace.test-helpers` | pass | 0 | 0 | 0 | 0 |  |
| `clojure.tools.test-gitlibs` | fail | 4 | 2 | 0 | 7 |  |
| `cloverage.args-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/tools/cli: not found in load path |
| `cloverage.dependency-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/tools/namespace/dependency: not found in load path |
| `cloverage.instrument-test` | blocked | — | — | — | — | exit 1: failed to load pprint/utilities: not found in load path |
| `cloverage.report.console-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: cloverage.report.console |
| `cloverage.source-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/tools/namespace/file: not found in load path |
| `cognitect.test-runner-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: java.net.URLClassLoader |
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
| `com.wsscode.misc.coll-test` | fail | 28 | 58 | 0 | 8 |  |
| `com.wsscode.misc.macros-test` | pass | 1 | 2 | 0 | 0 |  |
| `com.wsscode.misc.math-test` | pass | 6 | 9 | 0 | 0 |  |
| `com.wsscode.misc.refs-test` | pass | 4 | 11 | 0 | 0 |  |
| `com.wsscode.misc.uuid-test` | pass | 1 | 1 | 0 | 0 |  |
| `comb.test.template` | pass | 2 | 6 | 0 | 0 |  |
| `contajners.impl-test` | blocked | — | — | — | — | exit 1: failed to load /contajners/impl: not found in load path |
| `core-match.core-tests` | blocked | — | — | — | — | exit 1: unable to resolve symbol: definterface |
| `cprop.smoke-test` | pass | 1 | 0 | 0 | 0 |  |
| `crispin.core-test` | blocked | — | — | — | — | exit 1: NO_SOURCE_PATH: error reading crispin/core.cljc: crispin/core.cljc:35:26: invalid regex: error parsing regexp: invalid character class range: `/p{XDigit}` |
| `datalog.parser-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `datalog.parser.impl-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `datalog.parser.pull-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `datalog.parser.test.util` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `datalog.unparser-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `docopt.core-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `doric.test.core` | pass | 15 | 56 | 0 | 0 |  |
| `doric.test.doctest` | pass | 0 | 0 | 0 | 0 |  |
| `doric.test.readme` | fail | 1 | 2 | 9 | 1 |  |
| `edn-query-language.core-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: clojure.spec.alpha.Spec |
| `environ.core-test` | fail | 1 | 0 | 0 | 1 |  |
| `exoscale.coax-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: clojure.spec.alpha.Spec |
| `exoscale.interceptor-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/core/async: not found in load path |
| `exoscale.lingo.test.core-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: clojure.spec.alpha.Spec |
| `expectations.clojure.test-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `expound.print-length-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/spec/alpha: not found in load path |
| `expound.problems-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/spec/alpha: not found in load path |
| `expound.spec-gen` | blocked | — | — | — | — | exit 1: failed to load /clojure/spec/alpha: not found in load path |
| `expound.specs-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/spec/alpha: not found in load path |
| `expound.test-utils` | blocked | — | — | — | — | exit 1: failed to load /clojure/spec/alpha: not found in load path |
| `failjure.test-core` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `gaka.core-test` | pass | 7 | 39 | 0 | 0 |  |
| `hasch.test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: MessageDigest |
| `hato.client-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/spec/alpha: not found in load path |
| `helins.binf.test` | blocked | — | — | — | — | exit 1: EvalASTMaybeHostForm: StandardCharsets/UTF_8 |
| `hiccup.core-test` | fail | 5 | 46 | 20 | 0 |  |
| `hiccup2.core-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: java.net.URI |
| `hickory.test.convert` | blocked | — | — | — | — | exit 1: unable to resolve symbol: DocumentType |
| `hickory.test.core` | blocked | — | — | — | — | exit 1: unable to resolve symbol: DocumentType |
| `hickory.test.hiccup-utils` | pass | 8 | 50 | 0 | 0 |  |
| `hickory.test.render` | blocked | — | — | — | — | exit 1: unable to resolve symbol: DocumentType |
| `hickory.test.select` | blocked | — | — | — | — | exit 1: unable to resolve symbol: DocumentType |
| `hickory.test.zip` | blocked | — | — | — | — | exit 1: unable to resolve symbol: DocumentType |
| `honey.sql-test` | fail | 47 | 8 | 1 | 150 |  |
| `honey.sql.helpers-test` | fail | 31 | 32 | 0 | 125 |  |
| `honey.sql.postgres-test` | fail | 20 | 0 | 0 | 41 |  |
| `honeysql.core-test` | blocked | — | — | — | — | exit 1: NO_SOURCE_PATH: defrecord protocol implementations are not yet supported |
| `honeysql.format-test` | blocked | — | — | — | — | exit 1: NO_SOURCE_PATH: defrecord protocol implementations are not yet supported |
| `httpkit.client-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `hugsql.babashka-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: deftype |
| `integrant.core-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `integrant.test.bar` | pass | 0 | 0 | 0 | 0 |  |
| `integrant.test.baz` | pass | 0 | 0 | 0 | 0 |  |
| `integrant.test.foo` | pass | 0 | 0 | 0 | 0 |  |
| `integrant.test.quz` | pass | 0 | 0 | 0 | 0 |  |
| `io.aviso.binary-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `jasentaa.collections-test` | pass | 3 | 13 | 0 | 0 |  |
| `jasentaa.parser.basic-test` | pass | 4 | 15 | 0 | 0 |  |
| `jasentaa.parser.combinators-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `jasentaa.position-test` | fail | 5 | 8 | 0 | 5 |  |
| `jasentaa.test-helpers` | pass | 0 | 0 | 0 | 0 |  |
| `jasentaa.worked-example-1` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `jasentaa.worked-example-2` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `java-http-clj.smoke-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: HttpClient |
| `lambdaisland.regal-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `lambdaisland.regal.test-util` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `linked.map-test` | blocked | — | — | — | — | exit 1: NO_SOURCE_PATH: wrong number of arguments: expected 1, got 2 |
| `loom.test.alg` | blocked | — | — | — | — | exit 1: failed to load /loom/graph: not found in load path |
| `loom.test.alg-generic` | blocked | — | — | — | — | exit 1: failed to load /loom/alg_generic: not found in load path |
| `loom.test.attr` | blocked | — | — | — | — | exit 1: failed to load /loom/graph: not found in load path |
| `loom.test.compliance-tester` | blocked | — | — | — | — | exit 1: failed to load /loom/graph: not found in load path |
| `loom.test.derived` | blocked | — | — | — | — | exit 1: failed to load /loom/derived: not found in load path |
| `loom.test.flow` | blocked | — | — | — | — | exit 1: failed to load /loom/graph: not found in load path |
| `loom.test.graph` | blocked | — | — | — | — | exit 1: failed to load /loom/graph: not found in load path |
| `loom.test.label` | blocked | — | — | — | — | exit 1: failed to load /loom/graph: not found in load path |
| `loom.test.network-simplex` | blocked | — | — | — | — | exit 1: failed to load /loom/network_simplex: not found in load path |
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
| `me.raynes.core-test` | blocked | — | — | — | — | exit 1: failed to load /me/raynes/fs: not found in load path |
| `meander.defsyntax-test` | blocked | — | — | — | — | exit 1: failed to load pprint/utilities: not found in load path |
| `meander.defsyntax-test.gh-145` | blocked | — | — | — | — | exit 1: failed to load pprint/utilities: not found in load path |
| `meander.epsilon-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/spec/alpha: not found in load path |
| `meander.interpreter.epsilon-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: ClassNotFoundException |
| `meander.match.check.epsilon-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/spec/alpha: not found in load path |
| `meander.match.epsilon-test` | blocked | — | — | — | — | exit 1: failed to load pprint/utilities: not found in load path |
| `meander.match.ir.epsilon-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/spec/alpha: not found in load path |
| `meander.matrix.epsilon-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/spec/alpha: not found in load path |
| `meander.strategy.epsilon-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: deftype |
| `meander.substitute.epsilon-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: deftype |
| `meander.syntax.epsilon-test` | blocked | — | — | — | — | exit 1: failed to load pprint/utilities: not found in load path |
| `medley.core-test` | fail | 43 | 169 | 2 | 22 |  |
| `meta-merge.core-test` | pass | 1 | 17 | 0 | 0 |  |
| `minimallist.core-test` | pass | 2 | 338 | 0 | 0 |  |
| `minimallist.util-test` | pass | 4 | 15 | 0 | 0 |  |
| `missing.test.assertions-test` | blocked | — | — | — | — | exit 1: bad binding form: :throw? |
| `missing.test.old-methods` | blocked | — | — | — | — | exit 1: bad binding form: :throw? |
| `msgpack.core-check` | blocked | — | — | — | — | exit 1: EvalASTMaybeHostForm: Charset/forName |
| `msgpack.core-test` | blocked | — | — | — | — | exit 1: EvalASTMaybeHostForm: Charset/forName |
| `multigrep.core-test` | pass | 2 | 2 | 0 | 0 |  |
| `net.cgrand.xforms-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: satisfies? |
| `nextjournal.markdown-test` | blocked | — | — | — | — | exit 1: failed to load /matcher_combinators/ansi_color: not found in load path |
| `nextjournal.markdown.multi-threading-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: proxy |
| `odoyle.rules-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/spec/alpha: not found in load path |
| `ol.sfv.api-test` | fail | 10 | 56 | 0 | 2 |  |
| `ol.sfv.conformance-test` | blocked | — | — | — | — | exit 1: failed to load /alphabase/base32: not found in load path |
| `ol.sfv.error-test` | fail | 1 | 100 | 0 | 9 |  |
| `ol.sfv.example-test` | fail | 4 | 20 | 0 | 6 |  |
| `ol.sfv.parser-test` | fail | 11 | 113 | 1 | 14 |  |
| `ol.sfv.serialization-test` | fail | 6 | 30 | 0 | 4 |  |
| `omniconf.core-test` | blocked | — | — | — | — | exit 1: failed to load pprint/utilities: not found in load path |
| `orchestra.core-test` | blocked | — | — | — | — | exit 1: {} |
| `orchestra.expound-test` | blocked | — | — | — | — | exit 1: {} |
| `orchestra.make-fns` | blocked | — | — | — | — | exit 1: {} |
| `orchestra.many-fns` | blocked | — | — | — | — | exit 1: {} |
| `orchestra.reload-test` | blocked | — | — | — | — | exit 1: {} |
| `plumbing.core-test` | blocked | — | — | — | — | exit 1: failed to load pprint/utilities: not found in load path |
| `portal.bench` | pass | 0 | 0 | 0 | 0 |  |
| `portal.e2e` | pass | 0 | 0 | 0 | 0 |  |
| `portal.runtime.cson-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: Byte |
| `portal.runtime.fs-test` | pass | 1 | 8 | 0 | 0 |  |
| `portal.test-runner` | blocked | — | — | — | — | exit 1: unable to resolve symbol: clojure.lang.Namespace |
| `postmortem.core-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: deftype |
| `postmortem.instrument-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: deftype |
| `progrock.core-test` | pass | 5 | 15 | 0 | 0 |  |
| `promesa.tests.core-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: reify |
| `qbits.auspex-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: CompletableFuture |
| `reifyhealth.specmonstah.core-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/spec/alpha: not found in load path |
| `reifyhealth.specmonstah.spec-gen-test` | blocked | — | — | — | — | exit 1: failed to load /clojure/spec/alpha: not found in load path |
| `reifyhealth.specmonstah.test-data` | blocked | — | — | — | — | exit 1: failed to load /clojure/spec/alpha: not found in load path |
| `rewrite-clj.node-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `rewrite-clj.node.coercer-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `rewrite-clj.paredit-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `rewrite-clj.parser-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: deftype |
| `rewrite-clj.zip-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `rewrite-clj.zip.subedit-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `riddley.walk-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: clojure.lang.Compiler |
| `ruuter.core-test` | blocked | — | — | — | — | exit 1: failed to load /ruuter/core: not found in load path |
| `schema.coerce-test` | blocked | — | — | — | — | exit 1: failed to load pprint/utilities: not found in load path |
| `schema.core-test` | blocked | — | — | — | — | exit 1: failed to load pprint/utilities: not found in load path |
| `schema.experimental.abstract-map-test` | blocked | — | — | — | — | exit 1: failed to load pprint/utilities: not found in load path |
| `schema.macros-test` | blocked | — | — | — | — | exit 1: failed to load pprint/utilities: not found in load path |
| `schema.test-test` | blocked | — | — | — | — | exit 1: failed to load pprint/utilities: not found in load path |
| `schema.utils-test` | blocked | — | — | — | — | exit 1: EvalASTMaybeHostForm: clojure.lang.Compiler/CHAR_MAP |
| `selmer.core-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `selmer.our-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `slingshot.slingshot-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `slingshot.support-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `slingshot.test-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `sluj.core-test` | pass | 3 | 18 | 0 | 0 |  |
| `swirrl.dogstatsd-test` | blocked | — | — | — | — | exit 1: failed to load /swirrl/dogstatsd: not found in load path |
| `table.core-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `table.width-test` | blocked | — | — | — | — | exit 1: <unknown-file>:2:1:	(load-file "<CACHE>/lib_tests/run_all_libtests.clj") |
| `test-check.smoke-test` | blocked | — | — | — | — | exit 1: unable to resolve symbol: deftype |
| `testdoc.core-test` | fail | 11 | 35 | 0 | 1 |  |
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
