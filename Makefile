R := https://github.com/makeplus/makes
M := $(or $(MAKES_REPO_DIR),.cache/makes)
$(shell [ -d '$M' ] || git clone -q $R '$M')

include $M/init.mk

ifdef MAKES_REPO_DIR
MAKES_LOCAL_DIR ?= $(TOP)/.cache/local
endif

include $M/clean.mk

MAKES-CLEAN :=

default:: site

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

.PHONY: site serve publish serve-www publish-www
