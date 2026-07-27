R := https://github.com/makeplus/makes
M := $(or $(MAKES_REPO_DIR),.cache/makes)
$(shell [ -d '$M' ] || git clone -q $R '$M')

include $M/init.mk

ifdef MAKES_REPO_DIR
MAKES_LOCAL_DIR ?= $(TOP)/.cache/local
endif

GLOAT-DIR ?= $(or $(GLOAT_DIR),$(LOCAL-CACHE)/gloat-$(GLOAT-VERSION))

include $M/babashka.mk
include $M/gloat.mk
include $M/gh.mk
include $M/node.mk
include $M/wasmtime.mk
include $M/clean.mk

BABASHKA-SOURCE-TAG := v1.12.218
BABASHKA-SOURCE-REVISION := 0fb349c414e717800be775ba9cb77c95a9eb700d
BABASHKA-SOURCE-CACHE := $(LOCAL-CACHE)/babashka-src-$(BABASHKA-SOURCE-TAG)
BABASHKA-SOURCE := $(or $(BABASHKA_DIR),$(BABASHKA-SOURCE-CACHE))
BABASHKA-SOURCE-STAMP := $(LOCAL-CACHE)/.babashka-src-$(BABASHKA-SOURCE-TAG)
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
PREFIX ?= $(if $(filter 0,$(shell id -u)),/usr/local,$(HOME)/.local)

MAKES-CLEAN := \
  $(GOBB) \
  $(DIST) \
  $(RELEASE-BUILD) \
  $(SOURCE-STAGE) \
  $(REPL-SOURCE-STAGE) \
  $(SMOKE-DIR) \
  $(GOBB-WASM) \
  $(WASM-EXEC) \

MAKES-REALCLEAN := \
  $(BABASHKA-SOURCE-CACHE) \
  $(BABASHKA-SOURCE-STAMP) \

default:: build

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
else
BABASHKA-SOURCE-DEP := $(BABASHKA-SOURCE)
endif

deps: $(BABASHKA-SOURCE-DEP)
	$Q test -f '$(BABASHKA-SOURCE)/src/babashka/main.clj'
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

test: $(GOBB) $(BB) smoke
	$Q GOBB='$(GOBB)' BB='$(BB)' test/gobb

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

site: repl-wasm
	$(MAKE) -C www site

serve publish: repl-wasm
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
