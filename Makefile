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
include $M/wasmtime.mk
include $M/clean.mk

BABASHKA-SOURCE-TAG := v1.12.218
BABASHKA-SOURCE-REVISION := 0fb349c414e717800be775ba9cb77c95a9eb700d
BABASHKA-SOURCE-CACHE := $(LOCAL-CACHE)/babashka-src-$(BABASHKA-SOURCE-TAG)
BABASHKA-SOURCE := $(or $(BABASHKA_DIR),$(BABASHKA-SOURCE-CACHE))
BABASHKA-SOURCE-STAMP := $(LOCAL-CACHE)/.babashka-src-$(BABASHKA-SOURCE-TAG)
SOURCE-STAGE := $(TOP)/.cache/source-stage
SOURCE-STAGE-STAMP := $(SOURCE-STAGE)/.stamp
SOURCE-MANIFEST := $(TOP)/src/babashka-source.edn
STAGE-SOURCES := $(TOP)/util/stage-sources
VERSION-FILE := $(TOP)/VERSION
GOBB-SOURCES := $(shell \
  find '$(TOP)/src/gobb' -type f -name '*.clj' 2>/dev/null)
BABASHKA-SOURCES := $(shell \
  find '$(BABASHKA-SOURCE)/src' -type f -name '*.clj' 2>/dev/null)
GOBB := $(TOP)/bin/gobb
RELEASE := $(TOP)/util/release
RELEASE-DIST := $(TOP)/util/release-dist
DIST := $(TOP)/dist
RELEASE-BUILD := $(TOP)/.cache/release
PREFIX ?= $(if $(filter 0,$(shell id -u)),/usr/local,$(HOME)/.local)

MAKES-CLEAN := \
  $(GOBB) \
  $(DIST) \
  $(RELEASE-BUILD) \
  $(SOURCE-STAGE) \

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

install: $(GOBB)
	$Q install -d '$(DESTDIR)$(PREFIX)/bin'
	$Q install -m 0755 '$(GOBB)' '$(DESTDIR)$(PREFIX)/bin/gobb'

test: $(GOBB) $(BB)
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

site:
	$(MAKE) -C www site

serve publish:
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
