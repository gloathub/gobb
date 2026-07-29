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
SOURCE-STAGE := $(TOP)/.cache/source-stage
SOURCE-STAGE-STAMP := $(SOURCE-STAGE)/.stamp
REPL-SOURCE-STAGE := $(TOP)/.cache/repl-source-stage
REPL-SOURCE-STAGE-STAMP := $(REPL-SOURCE-STAGE)/.stamp
SOURCE-MANIFEST := $(TOP)/src/babashka-source.edn
STAGE-SOURCES := $(TOP)/util/stage-sources
VERSION-FILE := $(TOP)/VERSION
GOBB-SOURCES := $(shell \
  find '$(TOP)/src/gobb' -type f -name '*.clj' 2>/dev/null)
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
else
BABASHKA-SOURCE-DEP := $(BABASHKA-SOURCE)
BABASHKA-FS-DEP := $(BABASHKA-FS-SOURCE)
endif

deps: $(BABASHKA-SOURCE-DEP) $(BABASHKA-FS-DEP)
	$Q test -f '$(BABASHKA-SOURCE)/src/babashka/main.clj'
	$Q test -f '$(BABASHKA-FS-SOURCE)/src/babashka/fs.cljc'
ifndef BABASHKA_DIR
	$Q test "$$(git -C '$(BABASHKA-SOURCE)' rev-parse HEAD)" = \
	  "$(BABASHKA-SOURCE-REVISION)"
endif

$(SOURCE-STAGE-STAMP): \
  $(BABASHKA-SOURCE-DEP) \
  $(BABASHKA-SOURCES) \
  $(BB) \
  $(GOBB-SOURCES) \
  $(SOURCE-MANIFEST) \
  $(STAGE-SOURCES) \
  $(VERSION-FILE)
	@$(ECHO) "* Staging Gobb and selected Babashka sources"
	$Q $(BB) '$(STAGE-SOURCES)' \
	  '$(SOURCE-MANIFEST)' \
	  '$(BABASHKA-SOURCE)' \
	  '$(TOP)/src' \
	  '$(SOURCE-STAGE)' \
	  '$(VERSION-FILE)'
	$Q touch '$@'
	@$(ECHO)

stage: $(SOURCE-STAGE-STAMP)

$(REPL-SOURCE-STAGE-STAMP): \
  $(BABASHKA-SOURCE-DEP) \
  $(BABASHKA-SOURCES) \
  $(BB) \
  $(GOBB-SOURCES) \
  $(SOURCE-MANIFEST) \
  $(STAGE-SOURCES) \
  $(VERSION-FILE)
	@$(ECHO) "* Staging the Gobb browser REPL"
	$Q $(BB) '$(STAGE-SOURCES)' \
	  '$(SOURCE-MANIFEST)' \
	  '$(BABASHKA-SOURCE)' \
	  '$(TOP)/src' \
	  '$(REPL-SOURCE-STAGE)' \
	  '$(VERSION-FILE)' \
	  'gobb.web-repl'
	$Q touch '$@'
	@$(ECHO)

$(CAPABILITY-STAGE-STAMP): \
  $(BABASHKA-SOURCE-DEP) \
  $(BABASHKA-SOURCES) \
  $(BB) \
  $(GOBB-SOURCES) \
  $(CAPABILITY-SOURCE) \
  $(SOURCE-MANIFEST) \
  $(STAGE-SOURCES) \
  $(VERSION-FILE)
	@$(ECHO) "* Staging the cross-target capability probe"
	$Q $(BB) '$(STAGE-SOURCES)' \
	  '$(SOURCE-MANIFEST)' \
	  '$(BABASHKA-SOURCE)' \
	  '$(TOP)/src' \
	  '$(CAPABILITY-STAGE)' \
	  '$(VERSION-FILE)' \
	  'gobb.capability-probe'
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

test: $(GOBB) $(BB) $(RG) $(BABASHKA-FS-DEP) smoke capability-test
	$Q GOBB='$(GOBB)' BB='$(BB)' test/gobb
	$Q GOBB='$(GOBB)' BB='$(BB)' test/fs
	$Q GOBB='$(GOBB)' test/java-lang
	$Q GOBB='$(GOBB)' GOBB_GLOAT='$(GLOAT)' \
	  WASMTIME='$(WASMTIME)' NODE='$(NODE)' test/projects
	$Q GOBB='$(GOBB)' BB='$(BB)' test/tasks

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
