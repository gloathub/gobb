R := https://github.com/makeplus/makes
M := $(or $(MAKES_REPO_DIR),.cache/makes)
$(shell [ -d '$M' ] || git clone -q $R '$M')

include $M/init.mk

ifdef MAKES_REPO_DIR
MAKES_LOCAL_DIR ?= $(TOP)/.cache/local
endif

BABASHKA-VERSION ?= 1.12.218

GLOAT-DIR ?= $(or $(GLOAT_DIR),$(LOCAL-CACHE)/gloat-$(GLOAT-VERSION))

include $M/babashka.mk
include $M/gloat.mk
include $M/gh.mk
include $M/node.mk
include $M/rg.mk
include $M/wasmtime.mk
include $M/clean.mk

BABASHKA-SOURCE-TAG := v1.12.218
BABASHKA-SOURCE-REVISION := 0fb349c414e717800be775ba9cb77c95a9eb700d
BABASHKA-SOURCE-CACHE := $(LOCAL-CACHE)/babashka-src-$(BABASHKA-SOURCE-TAG)
BABASHKA-SOURCE := $(or $(BABASHKA_DIR),$(BABASHKA-SOURCE-CACHE))
BABASHKA-SOURCE-STAMP := $(LOCAL-CACHE)/.babashka-src-$(BABASHKA-SOURCE-TAG)
BABASHKA-FS-REVISION := 3fdcbcb8de6af0c880a0082700a295c55ffd2ecd
BABASHKA-FS-STAMP := $(LOCAL-CACHE)/.babashka-fs-$(BABASHKA-FS-REVISION)
BABASHKA-FS-SOURCE := $(BABASHKA-SOURCE)/fs
BABASHKA-PROCESS-REVISION := 16a84e0af0da51b8c84e289970f6b7cc35b35d18
BABASHKA-PROCESS-STAMP := $(LOCAL-CACHE)/.babashka-process-$(BABASHKA-PROCESS-REVISION)
BABASHKA-PROCESS-SOURCE := $(BABASHKA-SOURCE)/process
BABASHKA-CURL-REVISION := e936acd40544eb637b6041c7e89454b21eb7ee34
BABASHKA-CURL-STAMP := $(LOCAL-CACHE)/.babashka-curl-$(BABASHKA-CURL-REVISION)
BABASHKA-CURL-SOURCE := $(BABASHKA-SOURCE)/babashka.curl
BABASHKA-HTTP-CLIENT-TAG := v0.4.23
BABASHKA-HTTP-CLIENT-REVISION := d56bc7f86903d09ff3faef1500ad36005dab037f
BABASHKA-HTTP-CLIENT-SOURCE := $(LOCAL-CACHE)/http-client-0.4.23
BABASHKA-HTTP-CLIENT-STAMP := $(LOCAL-CACHE)/.http-client-0.4.23
CLOJURE-DATA-CSV-TAG := data.csv-1.0.0
CLOJURE-DATA-CSV-REVISION := 72561ee39463afd83acb8b328579e1c54ff68ecc
CLOJURE-DATA-CSV-SOURCE := $(LOCAL-CACHE)/data.csv-1.0.0
CLOJURE-DATA-CSV-STAMP := $(LOCAL-CACHE)/.data.csv-1.0.0
CHESHIRE-TAG := 6.2.0
CHESHIRE-REVISION := 2143a93711400d9078980aabd630963a8182fe23
CHESHIRE-SOURCE := $(LOCAL-CACHE)/cheshire-6.2.0
CHESHIRE-STAMP := $(LOCAL-CACHE)/.cheshire-6.2.0
CLJ-YAML-TAG := v1.0.29
CLJ-YAML-REVISION := 57c817a20910003583b0b0dde16a76ee101fd7e7
CLJ-YAML-SOURCE := $(LOCAL-CACHE)/clj-yaml-1.0.29
CLJ-YAML-STAMP := $(LOCAL-CACHE)/.clj-yaml-1.0.29
TRANSIT-CLJ-TAG := v1.1.357
TRANSIT-CLJ-REVISION := 89144172fb10568df433d0255058f947ac70ab53
TRANSIT-CLJ-SOURCE := $(LOCAL-CACHE)/transit-clj-1.1.357
TRANSIT-CLJ-STAMP := $(LOCAL-CACHE)/.transit-clj-1.1.357
BABASHKA-CLI-TAG := v0.8.67
BABASHKA-CLI-REVISION := 4ca06937cd9d50917ad7112855815960365cb474
BABASHKA-CLI-SOURCE := $(LOCAL-CACHE)/babashka-cli-0.8.67
BABASHKA-CLI-SOURCE-ROOT := $(BABASHKA-CLI-SOURCE)/src
BABASHKA-CLI-STAMP := $(LOCAL-CACHE)/.babashka-cli-0.8.67
HICCUP-TAG := 2.0.0-RC1
HICCUP-REVISION := 327d5408af94b4ef9560c39ab0afcfe5afe3c9a5
HICCUP-SOURCE := $(LOCAL-CACHE)/hiccup-2.0.0-RC1
HICCUP-STAMP := $(LOCAL-CACHE)/.hiccup-2.0.0-RC1
TOOLS-LOGGING-TAG := v1.3.0
TOOLS-LOGGING-REVISION := be30369b2fcf5403ae75cebbc9e645046525f437
TOOLS-LOGGING-SOURCE := $(LOCAL-CACHE)/tools.logging-1.3.0
TOOLS-LOGGING-STAMP := $(LOCAL-CACHE)/.tools.logging-1.3.0
TIMBRE-TAG := v6.8.0
TIMBRE-REVISION := cd00a1175147d447812be02d33e3d6e054864b9b
TIMBRE-SOURCE := $(LOCAL-CACHE)/timbre-6.8.0
TIMBRE-STAMP := $(LOCAL-CACHE)/.timbre-6.8.0
CLOJURE-SOURCE-TAG := clojure-1.12.4
CLOJURE-SOURCE-REVISION := b4ea0f824b2eea039dfc06b796ed601e35cbeab6
CLOJURE-SOURCE := $(LOCAL-CACHE)/clojure-1.12.4
CLOJURE-ZIP-SOURCE := $(CLOJURE-SOURCE)/src/clj/clojure/zip.clj
CLOJURE-SOURCE-STAMP := $(LOCAL-CACHE)/.clojure-1.12.4
REWRITE-CLJ-TAG := v1.2.54
REWRITE-CLJ-REVISION := b2436aa4ee60b406407420dbf15e61bf13a8c18d
REWRITE-CLJ-SOURCE := $(LOCAL-CACHE)/rewrite-clj-1.2.54
REWRITE-CLJ-SOURCE-ROOT := $(REWRITE-CLJ-SOURCE)/src
REWRITE-CLJ-STAMP := $(LOCAL-CACHE)/.rewrite-clj-1.2.54
SOURCE-STAGE := $(TOP)/.cache/source-stage
SOURCE-STAGE-STAMP := $(SOURCE-STAGE)/.stamp
REPL-SOURCE-STAGE := $(TOP)/.cache/repl-source-stage
REPL-SOURCE-STAGE-STAMP := $(REPL-SOURCE-STAGE)/.stamp
SOURCE-MANIFEST := $(TOP)/src/babashka-source.edn
STAGE-SOURCES := $(TOP)/util/stage-sources
VERSION-FILE := $(TOP)/VERSION
GOBB-SOURCES := $(shell \
  find '$(TOP)/src' -type f -name '*.clj' 2>/dev/null)
BABASHKA-CLI-SOURCES := $(shell \
  find '$(BABASHKA-CLI-SOURCE-ROOT)' -type f \
    \( -name '*.clj' -o -name '*.cljc' \) 2>/dev/null)
BABASHKA-SOURCES := $(shell \
  find '$(BABASHKA-SOURCE)/src' -type f -name '*.clj' 2>/dev/null)
GOBB := $(TOP)/bin/gobb
GOBB-WASM := $(TOP)/www/docs/repl/gobb.wasm
WASM-EXEC := $(TOP)/www/docs/repl/wasm_exec.js
RELEASE := $(TOP)/util/release
RELEASE-DIST := $(TOP)/util/release-dist
DIST := $(TOP)/dist
RELEASE-BUILD := $(TOP)/.cache/release
SMOKE-DIR := $(TOP)/.cache/smoke
SMOKE-SOURCE := $(TOP)/test/fixtures/gobb/build_smoke.clj
SMOKE-NATIVE := $(SMOKE-DIR)/native
SMOKE-WASI := $(SMOKE-DIR)/wasi.wasm
SMOKE-BROWSER := $(SMOKE-DIR)/browser.wasm
COMPAT-FIXTURES := $(TOP)/compat/fixtures.edn
COMPAT-RUNNER := $(TOP)/util/compat
COMPAT-DIR := $(TOP)/.cache/compat
COMPAT-REPORT := $(TOP)/www/docs/compatibility.md
CAPABILITY-SPEC := $(TOP)/compat/capabilities.edn
CAPABILITY-RUNNER := $(TOP)/util/capabilities
CAPABILITY-SOURCE := $(TOP)/src/gobb/capability_matrix.clj
CAPABILITY-REPORT := $(TOP)/www/docs/platforms.md
CAPABILITY-STAGE := $(TOP)/.cache/capability-stage
CAPABILITY-STAGE-STAMP := $(CAPABILITY-STAGE)/.stamp
CAPABILITY-DIR := $(TOP)/.cache/capabilities
CAPABILITY-NATIVE := $(CAPABILITY-DIR)/native
CAPABILITY-WASI := $(CAPABILITY-DIR)/wasi.wasm
CAPABILITY-BROWSER := $(CAPABILITY-DIR)/browser.wasm
CAPABILITY-TEST := $(TOP)/test/capabilities
INVENTORY-SPEC := $(TOP)/compat/inventory.edn
INVENTORY-LEDGER := $(TOP)/compat/ledger.edn
INVENTORY-RUNNER := $(TOP)/util/inventory
INVENTORY-REPORT := $(TOP)/www/docs/inventory.md
JAVA-COMPAT-SPEC := $(TOP)/compat/java-compat.edn
JAVA-COMPAT-LEDGER := $(TOP)/compat/java-classes.edn
JAVA-COMPAT-RUNNER := $(TOP)/util/java-compat
JAVA-COMPAT-REPORT := $(TOP)/www/docs/java-compatibility.md
JAVA-COMPAT-DIR := $(TOP)/.cache/java-compat
JAVA-COMPAT-SOURCE := $(TOP)/test/fixtures/gobb/java_compat_probe.clj
JAVA-COMPAT-NATIVE := $(JAVA-COMPAT-DIR)/native
JAVA-COMPAT-WASI := $(JAVA-COMPAT-DIR)/wasi.wasm
JAVA-COMPAT-BROWSER := $(JAVA-COMPAT-DIR)/browser.wasm
JAVA-COMPAT-TEST := $(TOP)/test/java-compat
PREFIX ?= $(if $(filter 0,$(shell id -u)),/usr/local,$(HOME)/.local)

MAKES-CLEAN := \
  $(GOBB) \
  $(DIST) \
  $(RELEASE-BUILD) \
  $(SOURCE-STAGE) \
  $(REPL-SOURCE-STAGE) \
  $(SMOKE-DIR) \
  $(CAPABILITY-STAGE) \
  $(CAPABILITY-DIR) \
  $(JAVA-COMPAT-DIR) \
  $(COMPAT-DIR) \
  $(GOBB-WASM) \
  $(WASM-EXEC) \

MAKES-REALCLEAN := \
  $(BABASHKA-SOURCE-CACHE) \
  $(BABASHKA-SOURCE-STAMP) \
  $(BABASHKA-FS-STAMP) \
  $(BABASHKA-PROCESS-STAMP) \
  $(BABASHKA-CURL-STAMP) \
  $(BABASHKA-HTTP-CLIENT-SOURCE) \
  $(BABASHKA-HTTP-CLIENT-STAMP) \
  $(CLOJURE-DATA-CSV-SOURCE) \
  $(CLOJURE-DATA-CSV-STAMP) \
  $(CHESHIRE-SOURCE) \
  $(CHESHIRE-STAMP) \
  $(CLJ-YAML-SOURCE) \
  $(CLJ-YAML-STAMP) \
  $(TRANSIT-CLJ-SOURCE) \
  $(TRANSIT-CLJ-STAMP) \
  $(BABASHKA-CLI-SOURCE) \
  $(BABASHKA-CLI-STAMP) \
  $(HICCUP-SOURCE) \
  $(HICCUP-STAMP) \
  $(TOOLS-LOGGING-SOURCE) \
  $(TOOLS-LOGGING-STAMP) \
  $(TIMBRE-SOURCE) \
  $(TIMBRE-STAMP) \
  $(CLOJURE-SOURCE) \
  $(CLOJURE-SOURCE-STAMP) \
  $(REWRITE-CLJ-SOURCE) \
  $(REWRITE-CLJ-STAMP) \

default:: build

$(CAPABILITY-SOURCE): $(CAPABILITY-SPEC) $(CAPABILITY-RUNNER) $(BB)
	@$(ECHO) "* Generating the platform capability contract"
	$Q $(BB) '$(CAPABILITY-RUNNER)' \
	  '$(CAPABILITY-SPEC)' \
	  '$(CAPABILITY-SOURCE)' \
	  '$(CAPABILITY-REPORT)'
	$Q touch '$@'
	@$(ECHO)

$(CAPABILITY-REPORT): $(CAPABILITY-SOURCE)
	$Q test -f '$@'
	$Q touch '$@'

capabilities: _capabilities

_capabilities: $(CAPABILITY-SPEC) $(CAPABILITY-RUNNER) $(BB)
	@$(ECHO) "* Generating the platform capability contract"
	$Q $(BB) '$(CAPABILITY-RUNNER)' \
	  '$(CAPABILITY-SPEC)' \
	  '$(CAPABILITY-SOURCE)' \
	  '$(CAPABILITY-REPORT)'
	@$(ECHO)

ifndef BABASHKA_DIR
$(BABASHKA-SOURCE-STAMP):
	@$(ECHO) "* Downloading Babashka $(BABASHKA-SOURCE-TAG) source"
	$Q $(RM) -r '$(BABASHKA-SOURCE-CACHE)'
	$Q git clone$(if $Q, -q) --depth=1 \
	  --branch $(BABASHKA-SOURCE-TAG) \
	  --config advice.detachedHead=false \
	  https://github.com/babashka/babashka '$(BABASHKA-SOURCE-CACHE)'
	$Q test "$$(git -C '$(BABASHKA-SOURCE-CACHE)' rev-parse HEAD)" = \
	  "$(BABASHKA-SOURCE-REVISION)"
	$Q touch '$@'
	@$(ECHO)
BABASHKA-SOURCE-DEP := $(BABASHKA-SOURCE-STAMP)

$(BABASHKA-FS-STAMP): $(BABASHKA-SOURCE-STAMP)
	@$(ECHO) "* Downloading the pinned babashka.fs source"
	$Q git -C '$(BABASHKA-SOURCE)' submodule update --init --depth=1 fs
	$Q test "$$(git -C '$(BABASHKA-FS-SOURCE)' rev-parse HEAD)" = \
	  "$(BABASHKA-FS-REVISION)"
	$Q touch '$@'
	@$(ECHO)

BABASHKA-FS-DEP := $(BABASHKA-FS-STAMP)

$(BABASHKA-PROCESS-STAMP): $(BABASHKA-SOURCE-STAMP)
	@$(ECHO) "* Downloading the pinned babashka.process source"
	$Q git -C '$(BABASHKA-SOURCE)' submodule update --init --depth=1 process
	$Q test "$$(git -C '$(BABASHKA-PROCESS-SOURCE)' rev-parse HEAD)" = \
	  "$(BABASHKA-PROCESS-REVISION)"
	$Q touch '$@'
	@$(ECHO)

BABASHKA-PROCESS-DEP := $(BABASHKA-PROCESS-STAMP)

$(BABASHKA-CURL-STAMP): $(BABASHKA-SOURCE-STAMP)
	@$(ECHO) "* Downloading the pinned babashka.curl source"
	$Q git -C '$(BABASHKA-SOURCE)' submodule update --init --depth=1 babashka.curl
	$Q test "$$(git -C '$(BABASHKA-CURL-SOURCE)' rev-parse HEAD)" = \
	  "$(BABASHKA-CURL-REVISION)"
	$Q touch '$@'
	@$(ECHO)

BABASHKA-CURL-DEP := $(BABASHKA-CURL-STAMP)
else
BABASHKA-SOURCE-DEP := $(BABASHKA-SOURCE)
BABASHKA-FS-DEP := $(BABASHKA-FS-SOURCE)
BABASHKA-PROCESS-DEP := $(BABASHKA-PROCESS-SOURCE)
BABASHKA-CURL-DEP := $(BABASHKA-CURL-SOURCE)
endif

$(BABASHKA-HTTP-CLIENT-STAMP):
	@$(ECHO) "* Downloading the pinned babashka.http-client source"
	$Q $(RM) -r '$(BABASHKA-HTTP-CLIENT-SOURCE)'
	$Q git clone$(if $Q, -q) --depth=1 \
	  --branch '$(BABASHKA-HTTP-CLIENT-TAG)' \
	  --config advice.detachedHead=false \
	  https://github.com/babashka/http-client \
	  '$(BABASHKA-HTTP-CLIENT-SOURCE)'
	$Q test "$$(git -C '$(BABASHKA-HTTP-CLIENT-SOURCE)' rev-parse HEAD)" = \
	  "$(BABASHKA-HTTP-CLIENT-REVISION)"
	$Q touch '$@'
	@$(ECHO)

BABASHKA-HTTP-CLIENT-DEP := $(BABASHKA-HTTP-CLIENT-STAMP)

$(CLOJURE-DATA-CSV-STAMP):
	@$(ECHO) "* Downloading the pinned clojure.data.csv source"
	$Q $(RM) -r '$(CLOJURE-DATA-CSV-SOURCE)'
	$Q git clone$(if $Q, -q) --depth=1 \
	  --branch '$(CLOJURE-DATA-CSV-TAG)' \
	  --config advice.detachedHead=false \
	  https://github.com/clojure/data.csv \
	  '$(CLOJURE-DATA-CSV-SOURCE)'
	$Q test "$$(git -C '$(CLOJURE-DATA-CSV-SOURCE)' rev-parse HEAD)" = \
	  "$(CLOJURE-DATA-CSV-REVISION)"
	$Q touch '$@'
	@$(ECHO)

CLOJURE-DATA-CSV-DEP := $(CLOJURE-DATA-CSV-STAMP)

$(CHESHIRE-STAMP):
	@$(ECHO) "* Downloading the pinned Cheshire source"
	$Q $(RM) -r '$(CHESHIRE-SOURCE)'
	$Q git clone$(if $Q, -q) --depth=1 \
	  --branch '$(CHESHIRE-TAG)' \
	  --config advice.detachedHead=false \
	  https://github.com/dakrone/cheshire \
	  '$(CHESHIRE-SOURCE)'
	$Q test "$$(git -C '$(CHESHIRE-SOURCE)' rev-parse HEAD)" = \
	  "$(CHESHIRE-REVISION)"
	$Q touch '$@'
	@$(ECHO)

CHESHIRE-DEP := $(CHESHIRE-STAMP)

$(CLJ-YAML-STAMP):
	@$(ECHO) "* Downloading the pinned clj-yaml source"
	$Q $(RM) -r '$(CLJ-YAML-SOURCE)'
	$Q git clone$(if $Q, -q) --depth=1 \
	  --branch '$(CLJ-YAML-TAG)' \
	  --config advice.detachedHead=false \
	  https://github.com/clj-commons/clj-yaml \
	  '$(CLJ-YAML-SOURCE)'
	$Q test "$$(git -C '$(CLJ-YAML-SOURCE)' rev-parse HEAD)" = \
	  "$(CLJ-YAML-REVISION)"
	$Q touch '$@'
	@$(ECHO)

CLJ-YAML-DEP := $(CLJ-YAML-STAMP)

$(TRANSIT-CLJ-STAMP):
	@$(ECHO) "* Downloading the pinned transit-clj source"
	$Q $(RM) -r '$(TRANSIT-CLJ-SOURCE)'
	$Q git clone$(if $Q, -q) --depth=1 \
	  --branch '$(TRANSIT-CLJ-TAG)' \
	  --config advice.detachedHead=false \
	  https://github.com/cognitect/transit-clj \
	  '$(TRANSIT-CLJ-SOURCE)'
	$Q test "$$(git -C '$(TRANSIT-CLJ-SOURCE)' rev-parse HEAD)" = \
	  "$(TRANSIT-CLJ-REVISION)"
	$Q touch '$@'
	@$(ECHO)

TRANSIT-CLJ-DEP := $(TRANSIT-CLJ-STAMP)

$(BABASHKA-CLI-STAMP):
	@$(ECHO) "* Downloading the pinned babashka.cli source"
	$Q $(RM) -r '$(BABASHKA-CLI-SOURCE)'
	$Q git clone$(if $Q, -q) --depth=1 \
	  --branch '$(BABASHKA-CLI-TAG)' \
	  --config advice.detachedHead=false \
	  https://github.com/babashka/cli \
	  '$(BABASHKA-CLI-SOURCE)'
	$Q test "$$(git -C '$(BABASHKA-CLI-SOURCE)' rev-parse HEAD)" = \
	  "$(BABASHKA-CLI-REVISION)"
	$Q touch '$@'
	@$(ECHO)

BABASHKA-CLI-DEP := $(BABASHKA-CLI-STAMP)

$(HICCUP-STAMP):
	@$(ECHO) "* Downloading the pinned Hiccup source"
	$Q $(RM) -r '$(HICCUP-SOURCE)'
	$Q git clone$(if $Q, -q) --depth=1 \
	  --branch '$(HICCUP-TAG)' \
	  --config advice.detachedHead=false \
	  https://github.com/weavejester/hiccup \
	  '$(HICCUP-SOURCE)'
	$Q test "$$(git -C '$(HICCUP-SOURCE)' rev-parse HEAD)" = \
	  "$(HICCUP-REVISION)"
	$Q touch '$@'
	@$(ECHO)

HICCUP-DEP := $(HICCUP-STAMP)

$(TOOLS-LOGGING-STAMP):
	@$(ECHO) "* Downloading the pinned tools.logging source"
	$Q $(RM) -r '$(TOOLS-LOGGING-SOURCE)'
	$Q git clone$(if $Q, -q) --depth=1 \
	  --branch '$(TOOLS-LOGGING-TAG)' \
	  --config advice.detachedHead=false \
	  https://github.com/clojure/tools.logging \
	  '$(TOOLS-LOGGING-SOURCE)'
	$Q test "$$(git -C '$(TOOLS-LOGGING-SOURCE)' rev-parse HEAD)" = \
	  "$(TOOLS-LOGGING-REVISION)"
	$Q touch '$@'
	@$(ECHO)

TOOLS-LOGGING-DEP := $(TOOLS-LOGGING-STAMP)

$(TIMBRE-STAMP):
	@$(ECHO) "* Downloading the pinned Timbre source"
	$Q $(RM) -r '$(TIMBRE-SOURCE)'
	$Q git clone$(if $Q, -q) --depth=1 \
	  --branch '$(TIMBRE-TAG)' \
	  --config advice.detachedHead=false \
	  https://github.com/taoensso/timbre \
	  '$(TIMBRE-SOURCE)'
	$Q test "$$(git -C '$(TIMBRE-SOURCE)' rev-parse HEAD)" = \
	  "$(TIMBRE-REVISION)"
	$Q touch '$@'
	@$(ECHO)

TIMBRE-DEP := $(TIMBRE-STAMP)

$(CLOJURE-SOURCE-STAMP):
	@$(ECHO) "* Downloading the pinned Clojure source"
	$Q $(RM) -r '$(CLOJURE-SOURCE)'
	$Q git clone$(if $Q, -q) --depth=1 \
	  --branch '$(CLOJURE-SOURCE-TAG)' \
	  --config advice.detachedHead=false \
	  https://github.com/clojure/clojure \
	  '$(CLOJURE-SOURCE)'
	$Q test "$$(git -C '$(CLOJURE-SOURCE)' rev-parse HEAD)" = \
	  "$(CLOJURE-SOURCE-REVISION)"
	$Q touch '$@'
	@$(ECHO)

CLOJURE-SOURCE-DEP := $(CLOJURE-SOURCE-STAMP)

$(REWRITE-CLJ-STAMP):
	@$(ECHO) "* Downloading the pinned rewrite-clj source"
	$Q $(RM) -r '$(REWRITE-CLJ-SOURCE)'
	$Q git clone$(if $Q, -q) --depth=1 \
	  --branch '$(REWRITE-CLJ-TAG)' \
	  --config advice.detachedHead=false \
	  https://github.com/clj-commons/rewrite-clj \
	  '$(REWRITE-CLJ-SOURCE)'
	$Q test "$$(git -C '$(REWRITE-CLJ-SOURCE)' rev-parse HEAD)" = \
	  "$(REWRITE-CLJ-REVISION)"
	$Q touch '$@'
	@$(ECHO)

REWRITE-CLJ-DEP := $(REWRITE-CLJ-STAMP)

deps: \
  $(BABASHKA-SOURCE-DEP) \
  $(BABASHKA-FS-DEP) \
  $(BABASHKA-PROCESS-DEP) \
  $(BABASHKA-CURL-DEP) \
  $(BABASHKA-HTTP-CLIENT-DEP) \
  $(CLOJURE-DATA-CSV-DEP) \
  $(CHESHIRE-DEP) \
  $(CLJ-YAML-DEP) \
  $(TRANSIT-CLJ-DEP) \
  $(BABASHKA-CLI-DEP) \
  $(HICCUP-DEP) \
  $(TOOLS-LOGGING-DEP) \
  $(TIMBRE-DEP) \
  $(CLOJURE-SOURCE-DEP) \
  $(REWRITE-CLJ-DEP)
	$Q test -f '$(BABASHKA-SOURCE)/src/babashka/main.clj'
	$Q test -f '$(BABASHKA-FS-SOURCE)/src/babashka/fs.cljc'
	$Q test -f '$(BABASHKA-PROCESS-SOURCE)/src/babashka/process.cljc'
	$Q test -f '$(BABASHKA-CURL-SOURCE)/src/babashka/curl.clj'
	$Q test -f '$(BABASHKA-HTTP-CLIENT-SOURCE)/src/babashka/http_client.clj'
	$Q test -f '$(CLOJURE-DATA-CSV-SOURCE)/src/main/clojure/clojure/data/csv.clj'
	$Q test -f '$(CHESHIRE-SOURCE)/src/cheshire/core.clj'
	$Q test -f '$(CLJ-YAML-SOURCE)/src/clojure/clj_yaml/core.clj'
	$Q test -f '$(TRANSIT-CLJ-SOURCE)/src/main/clojure/cognitect/transit.clj'
	$Q test -f '$(BABASHKA-CLI-SOURCE-ROOT)/babashka/cli.cljc'
	$Q test -f '$(HICCUP-SOURCE)/src/hiccup/core.clj'
	$Q test -f '$(TOOLS-LOGGING-SOURCE)/src/main/clojure/clojure/tools/logging.clj'
	$Q test -d '$(TIMBRE-SOURCE)/src'
	$Q test -f '$(CLOJURE-ZIP-SOURCE)'
	$Q test -f '$(REWRITE-CLJ-SOURCE-ROOT)/rewrite_clj/zip.cljc'
ifndef BABASHKA_DIR
	$Q test "$$(git -C '$(BABASHKA-SOURCE)' rev-parse HEAD)" = \
	  "$(BABASHKA-SOURCE-REVISION)"
endif

$(SOURCE-STAGE-STAMP): \
  $(BABASHKA-SOURCE-DEP) \
  $(BABASHKA-SOURCES) \
  $(BB) \
  $(GOBB-SOURCES) \
  $(BABASHKA-CLI-DEP) \
  $(HICCUP-DEP) \
  $(TOOLS-LOGGING-DEP) \
  $(TIMBRE-DEP) \
  $(CLOJURE-SOURCE-DEP) \
  $(BABASHKA-CLI-SOURCES) \
  $(CLOJURE-ZIP-SOURCE) \
  $(SOURCE-MANIFEST) \
  $(STAGE-SOURCES) \
  $(TOP)/Makefile \
  $(VERSION-FILE)
	@$(ECHO) "* Staging Gobb and selected Babashka sources"
	$Q $(BB) '$(STAGE-SOURCES)' \
	  '$(SOURCE-MANIFEST)' \
	  '$(BABASHKA-SOURCE)' \
	  '$(TOP)/src' \
	  '$(SOURCE-STAGE)' \
	  '$(VERSION-FILE)' \
	  'gobb.cli' \
	  '$(BABASHKA-CLI-SOURCE-ROOT)' \
	  '$(CLOJURE-ZIP-SOURCE)'
	$Q touch '$@'
	@$(ECHO)

stage: $(SOURCE-STAGE-STAMP)

$(REPL-SOURCE-STAGE-STAMP): \
  $(BABASHKA-SOURCE-DEP) \
  $(BABASHKA-SOURCES) \
  $(BB) \
  $(GOBB-SOURCES) \
  $(BABASHKA-CLI-DEP) \
  $(CLOJURE-SOURCE-DEP) \
  $(BABASHKA-CLI-SOURCES) \
  $(CLOJURE-ZIP-SOURCE) \
  $(SOURCE-MANIFEST) \
  $(STAGE-SOURCES) \
  $(TOP)/Makefile \
  $(VERSION-FILE)
	@$(ECHO) "* Staging the Gobb browser REPL"
	$Q $(BB) '$(STAGE-SOURCES)' \
	  '$(SOURCE-MANIFEST)' \
	  '$(BABASHKA-SOURCE)' \
	  '$(TOP)/src' \
	  '$(REPL-SOURCE-STAGE)' \
	  '$(VERSION-FILE)' \
	  'gobb.web-repl' \
	  '$(BABASHKA-CLI-SOURCE-ROOT)' \
	  '$(CLOJURE-ZIP-SOURCE)'
	$Q touch '$@'
	@$(ECHO)

$(CAPABILITY-STAGE-STAMP): \
  $(BABASHKA-SOURCE-DEP) \
  $(BABASHKA-SOURCES) \
  $(BB) \
  $(GOBB-SOURCES) \
  $(BABASHKA-CLI-DEP) \
  $(CLOJURE-SOURCE-DEP) \
  $(BABASHKA-CLI-SOURCES) \
  $(CLOJURE-ZIP-SOURCE) \
  $(CAPABILITY-SOURCE) \
  $(SOURCE-MANIFEST) \
  $(STAGE-SOURCES) \
  $(TOP)/Makefile \
  $(VERSION-FILE)
	@$(ECHO) "* Staging the cross-target capability probe"
	$Q $(BB) '$(STAGE-SOURCES)' \
	  '$(SOURCE-MANIFEST)' \
	  '$(BABASHKA-SOURCE)' \
	  '$(TOP)/src' \
	  '$(CAPABILITY-STAGE)' \
	  '$(VERSION-FILE)' \
	  'gobb.capability-probe' \
	  '$(BABASHKA-CLI-SOURCE-ROOT)' \
	  '$(CLOJURE-ZIP-SOURCE)'
	$Q touch '$@'
	@$(ECHO)

$(GOBB): $(SOURCE-STAGE-STAMP) $(GLOAT)
	@$(ECHO) "* Building Gobb"
	$Q mkdir -p '$(@D)'
	$Q $(GLOAT) '$(SOURCE-STAGE)' \
	  --out='$@' \
	  --force \
	  --quiet \
	  --ext=goimports \
	  --module=github.com/clojurestar/gobb
	@$(ECHO)

build: $(GOBB)

$(GOBB-WASM): $(REPL-SOURCE-STAGE-STAMP) $(GLOAT)
	@$(ECHO) "* Building the Gobb browser REPL"
	$Q mkdir -p '$(@D)'
	$Q $(GLOAT) '$(REPL-SOURCE-STAGE)' \
	  --out='$@' \
	  --to=js \
	  --force \
	  --quiet \
	  --ext=goimports \
	  --module=github.com/clojurestar/gobb
	@$(ECHO)

$(WASM-EXEC): $(GLOAT)
	@$(ECHO) "* Installing the Go Wasm browser runtime"
	$Q mkdir -p '$(@D)'
	$Q go="$$( $(GLOAT) --which=go )"; \
	  cp "$$($$go env GOROOT)/lib/wasm/wasm_exec.js" '$@'
	@$(ECHO)

repl-wasm: $(GOBB-WASM) $(WASM-EXEC)

install: $(GOBB)
	$Q install -d '$(DESTDIR)$(PREFIX)/bin'
	$Q install -m 0755 '$(GOBB)' '$(DESTDIR)$(PREFIX)/bin/gobb'

$(SMOKE-NATIVE): $(GOBB) $(GLOAT) $(SMOKE-SOURCE)
	@$(ECHO) "* Building the native Gobb smoke program"
	$Q mkdir -p '$(@D)'
	$Q GOBB_GLOAT='$(GLOAT)' '$(GOBB)' build \
	  '$(SMOKE-SOURCE)' -o '$@'
	@$(ECHO)

$(SMOKE-WASI): $(GOBB) $(GLOAT) $(SMOKE-SOURCE)
	@$(ECHO) "* Building the WASI Gobb smoke program"
	$Q mkdir -p '$(@D)'
	$Q GOBB_GLOAT='$(GLOAT)' '$(GOBB)' build \
	  '$(SMOKE-SOURCE)' -o '$@' --platform wasip1/wasm
	@$(ECHO)

$(SMOKE-BROWSER): $(GOBB) $(GLOAT) $(SMOKE-SOURCE)
	@$(ECHO) "* Building the browser-Wasm Gobb smoke program"
	$Q mkdir -p '$(@D)'
	$Q GOBB_GLOAT='$(GLOAT)' '$(GOBB)' build \
	  '$(SMOKE-SOURCE)' -o '$@' --platform js/wasm
	@$(ECHO)

$(CAPABILITY-NATIVE): $(CAPABILITY-STAGE-STAMP) $(GLOAT)
	@$(ECHO) "* Building the native capability probe"
	$Q mkdir -p '$(@D)'
	$Q $(GLOAT) '$(CAPABILITY-STAGE)' \
	  --out='$@' \
	  --force \
	  --quiet \
	  --ext=goimports \
	  --module=github.com/clojurestar/gobb
	@$(ECHO)

$(CAPABILITY-WASI): $(CAPABILITY-STAGE-STAMP) $(GLOAT)
	@$(ECHO) "* Building the WASI capability probe"
	$Q mkdir -p '$(@D)'
	$Q $(GLOAT) '$(CAPABILITY-STAGE)' \
	  --out='$@' \
	  --platform=wasip1/wasm \
	  --force \
	  --quiet \
	  --ext=goimports \
	  --module=github.com/clojurestar/gobb
	@$(ECHO)

$(CAPABILITY-BROWSER): $(CAPABILITY-STAGE-STAMP) $(GLOAT)
	@$(ECHO) "* Building the browser-Wasm capability probe"
	$Q mkdir -p '$(@D)'
	$Q $(GLOAT) '$(CAPABILITY-STAGE)' \
	  --out='$@' \
	  --to=js \
	  --force \
	  --quiet \
	  --ext=goimports \
	  --module=github.com/clojurestar/gobb
	@$(ECHO)

smoke: $(SMOKE-NATIVE) $(SMOKE-WASI) $(SMOKE-BROWSER) $(WASMTIME) $(NODE)
	@$(ECHO) "* Executing Gobb smoke programs"
	$Q expected=$$('$(GOBB)' -cp '$(TOP)/test/fixtures' -e \
	    "(require '[gobb.build-smoke :as smoke]) (smoke/-main)"); \
	  native=$$('$(SMOKE-NATIVE)'); \
	  wasi=$$('$(WASMTIME)' '$(SMOKE-WASI)'); \
	  go=$$('$(GLOAT)' --which=go); \
	  goroot=$$($$go env GOROOT); \
	  browser=$$(env -i HOME=/tmp \
	    PATH='$(dir $(NODE)):/usr/bin:/bin' \
	    "$$goroot/lib/wasm/go_js_wasm_exec" '$(SMOKE-BROWSER)'); \
	  test "$$native" = "$$expected"; \
	  test "$$wasi" = "$$expected"; \
	  test "$$browser" = "$$expected"; \
	  echo "$$expected"
	@$(ECHO)

capability-test: \
  $(CAPABILITY-NATIVE) \
  $(CAPABILITY-WASI) \
  $(CAPABILITY-BROWSER) \
  $(CAPABILITY-TEST) \
  $(WASMTIME) \
  $(NODE) \
  $(BB)
	@$(ECHO) "* Executing native, WASI, and browser capability probes"
	$Q mkdir -p '$(CAPABILITY-DIR)'
	$Q GOBB_CAPABILITY_ENV=visible \
	  '$(CAPABILITY-NATIVE)' > '$(CAPABILITY-DIR)/native.edn'
	$Q '$(WASMTIME)' --env GOBB_CAPABILITY_ENV=visible \
	  '$(CAPABILITY-WASI)' > '$(CAPABILITY-DIR)/wasi.edn'
	$Q go=$$('$(GLOAT)' --which=go); \
	  goroot=$$($$go env GOROOT); \
	  env -i HOME=/tmp GOBB_CAPABILITY_ENV=visible \
	    PATH='$(dir $(NODE)):/usr/bin:/bin' \
	    "$$goroot/lib/wasm/go_js_wasm_exec" \
	    '$(CAPABILITY-BROWSER)' > '$(CAPABILITY-DIR)/browser.edn'
	$Q $(BB) '$(CAPABILITY-TEST)' \
	  '$(CAPABILITY-SPEC)' \
	  '$(CAPABILITY-DIR)/native.edn' \
	  '$(CAPABILITY-DIR)/wasi.edn' \
	  '$(CAPABILITY-DIR)/browser.edn'
	@$(ECHO)

inventory: \
  $(BABASHKA-SOURCE-DEP) \
  $(BB) \
  $(CAPABILITY-SPEC) \
  $(INVENTORY-SPEC) \
  $(INVENTORY-RUNNER)
	@$(ECHO) "* Inventorying the complete BB compatibility surface"
	$Q $(BB) '$(INVENTORY-RUNNER)' \
	  '$(INVENTORY-SPEC)' \
	  '$(BABASHKA-SOURCE)' \
	  '$(BB)' \
	  '$(INVENTORY-LEDGER)' \
	  '$(INVENTORY-REPORT)' \
	  '$(JAVA-COMPAT-SPEC)'
	@$(ECHO)

java-compat: inventory $(JAVA-COMPAT-SPEC) $(JAVA-COMPAT-RUNNER)
	@$(ECHO) "* Ranking and assigning the BB Java compatibility surface"
	$Q $(BB) '$(JAVA-COMPAT-RUNNER)' \
	  '$(JAVA-COMPAT-SPEC)' \
	  '$(INVENTORY-LEDGER)' \
	  '$(BABASHKA-SOURCE)' \
	  '$(JAVA-COMPAT-LEDGER)' \
	  '$(JAVA-COMPAT-REPORT)'
	@$(ECHO)

$(JAVA-COMPAT-NATIVE): $(JAVA-COMPAT-SOURCE) $(GLOAT)
	@$(ECHO) "* Building the native Java compatibility probe"
	$Q mkdir -p '$(@D)'
	$Q $(GLOAT) '$(JAVA-COMPAT-SOURCE)' \
	  --out='$@' \
	  --force \
	  --quiet \
	  --ext=goimports \
	  --module=github.com/clojurestar/gobb
	@$(ECHO)

$(JAVA-COMPAT-WASI): $(JAVA-COMPAT-SOURCE) $(GLOAT)
	@$(ECHO) "* Building the WASI Java compatibility probe"
	$Q mkdir -p '$(@D)'
	$Q $(GLOAT) '$(JAVA-COMPAT-SOURCE)' \
	  --out='$@' \
	  --platform=wasip1/wasm \
	  --force \
	  --quiet \
	  --ext=goimports \
	  --module=github.com/clojurestar/gobb
	@$(ECHO)

$(JAVA-COMPAT-BROWSER): $(JAVA-COMPAT-SOURCE) $(GLOAT)
	@$(ECHO) "* Building the browser Java compatibility probe"
	$Q mkdir -p '$(@D)'
	$Q $(GLOAT) '$(JAVA-COMPAT-SOURCE)' \
	  --out='$@' \
	  --to=js \
	  --force \
	  --quiet \
	  --ext=goimports \
	  --module=github.com/clojurestar/gobb
	@$(ECHO)

java-compat-test: \
  $(JAVA-COMPAT-NATIVE) \
  $(JAVA-COMPAT-WASI) \
  $(JAVA-COMPAT-BROWSER) \
  $(JAVA-COMPAT-TEST) \
  $(WASMTIME) \
  $(NODE) \
  $(BB)
	@$(ECHO) "* Executing native, WASI, and browser Java compatibility probes"
	$Q '$(JAVA-COMPAT-NATIVE)' > '$(JAVA-COMPAT-DIR)/native.edn'
	$Q '$(WASMTIME)' --dir /tmp '$(JAVA-COMPAT-WASI)' \
	  > '$(JAVA-COMPAT-DIR)/wasi.edn'
	$Q go=$$('$(GLOAT)' --which=go); \
	  goroot=$$($$go env GOROOT); \
	  env -i HOME=/tmp PATH='$(dir $(NODE)):/usr/bin:/bin' \
	    "$$goroot/lib/wasm/go_js_wasm_exec" \
	    '$(JAVA-COMPAT-BROWSER)' > '$(JAVA-COMPAT-DIR)/browser.edn'
	$Q $(BB) '$(JAVA-COMPAT-TEST)' \
	  '$(JAVA-COMPAT-DIR)/native.edn' \
	  '$(JAVA-COMPAT-DIR)/wasi.edn' \
	  '$(JAVA-COMPAT-DIR)/browser.edn'
	@$(ECHO)

test: \
  $(GOBB) \
  $(BB) \
  $(RG) \
  $(BABASHKA-FS-DEP) \
  $(BABASHKA-PROCESS-DEP) \
  $(BABASHKA-CURL-DEP) \
  $(BABASHKA-HTTP-CLIENT-DEP) \
  $(CLOJURE-DATA-CSV-DEP) \
  $(CHESHIRE-DEP) \
  $(CLJ-YAML-DEP) \
  $(TRANSIT-CLJ-DEP) \
  $(BABASHKA-CLI-DEP) \
  $(CLOJURE-SOURCE-DEP) \
  smoke \
  capability-test
	$Q GOBB='$(GOBB)' BB='$(BB)' test/gobb
	$Q GOBB='$(GOBB)' BB='$(BB)' test/fs
	$Q GOBB='$(GOBB)' BB='$(BB)' test/java-io
	$Q GOBB='$(GOBB)' BB='$(BB)' test/data-csv
	$Q GOBB='$(GOBB)' BB='$(BB)' test/cheshire
	$Q GOBB='$(GOBB)' BB='$(BB)' test/clj-yaml
	$Q GOBB='$(GOBB)' BB='$(BB)' test/transit
	$Q GOBB='$(GOBB)' BB='$(BB)' test/babashka-cli
	$Q GOBB='$(GOBB)' BB='$(BB)' test/hiccup
	$Q GOBB='$(GOBB)' BB='$(BB)' test/logging
	$Q GOBB='$(GOBB)' BB='$(BB)' test/clojure-zip
	$Q GOBB='$(GOBB)' BB='$(BB)' test/process
	$Q GOBB='$(GOBB)' BB='$(BB)' \
	  GO="$$('$(GLOAT)' --which=go)" test/curl
	$Q GOBB='$(GOBB)' BB='$(BB)' \
	  GO="$$('$(GLOAT)' --which=go)" test/http-client
	$Q GOBB='$(GOBB)' test/java-lang
	$Q GOBB='$(GOBB)' GOBB_GLOAT='$(GLOAT)' \
	  WASMTIME='$(WASMTIME)' NODE='$(NODE)' test/projects
	$Q GOBB='$(GOBB)' BB='$(BB)' test/tasks
	$Q GOBB='$(GOBB)' \
	  GO="$$('$(GLOAT)' --which=go)" test/interactive-services

compat: _compat

_compat: $(GOBB) $(BB) $(GLOAT) $(COMPAT-FIXTURES) $(COMPAT-RUNNER)
	@$(ECHO) "* Comparing Gobb with Babashka"
	$Q STRICT='$(STRICT)' $(BB) '$(COMPAT-RUNNER)' \
	  '$(COMPAT-FIXTURES)' \
	  '$(BB)' \
	  '$(GOBB)' \
	  '$(GLOAT)' \
	  '$(TOP)/test/fixtures' \
	  '$(COMPAT-DIR)' \
	  '$(COMPAT-REPORT)'
	@$(ECHO)

release-prep:
	@$(if $(filter command line,$(origin VERSION)),,\
	  $(error VERSION is required on the command line))
	$Q '$(RELEASE)' prepare '$(VERSION)'

release-dist: $(SOURCE-STAGE-STAMP) $(GLOAT)
	@$(if $(filter command line,$(origin VERSION)),,\
	  $(error VERSION is required on the command line))
	$Q '$(RELEASE-DIST)' \
	  '$(VERSION)' \
	  '$(GLOAT)' \
	  '$(SOURCE-STAGE)' \
	  '$(BABASHKA-SOURCE)' \
	  '$(TOP)' \
	  '$(DIST)' \
	  '$(RELEASE-BUILD)'

release: $(GH) $(WASMTIME)
	@$(if $(filter command line,$(origin VERSION)),,\
	  $(error VERSION is required on the command line))
	$Q GH='$(GH)' WASMTIME='$(WASMTIME)' \
	  '$(RELEASE)' publish '$(VERSION)'

source-ledger: $(SOURCE-STAGE-STAMP)
	@cat '$(SOURCE-STAGE)/ledger.edn'

site: capabilities inventory java-compat repl-wasm
	$(MAKE) -C www site

serve publish: capabilities inventory java-compat repl-wasm
	$(MAKE) -C www $@

serve-www: serve

publish-www: publish

clean::
	$(MAKE) -C www clean

realclean::
	$(MAKE) -C www realclean

distclean::
	$(MAKE) -C www distclean

include $M/shell.mk
