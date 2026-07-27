R := https://github.com/makeplus/makes
M := $(or $(MAKES_REPO_DIR),../.cache/makes)
$(shell [ -d '$M' ] || git clone -q $R '$M')

include $M/init.mk

ifdef MAKES_REPO_DIR
MAKES_LOCAL_DIR ?= $(TOP)/.cache/local
endif

include $M/clean.mk

PYTHON-VENV := $(ROOT)/venv

include $M/python.mk
include $M/typos.mk

SHELL-DEPS += $(PYTHON-VENV)

include $M/shell.mk

export UV_LINK_MODE := copy
export UV_CACHE_DIR := $(TOP)/.cache/uv
export NO_MKDOCS_2_WARNING := 1

REPO ?= git@github.com:clojurestar/gobb

MAKES-CLEAN := \
  site \
  hooks/__pycache__ \

MAKES-REALCLEAN := $(PYTHON-VENV)

DEPS := $(PYTHON-VENV)

default:: site

site: $(DEPS)
	mkdocs build --strict -d $@

serve: $(DEPS)
	mkdocs serve --livereload

lint: $(TYPOS)
	typos --config _typos.toml docs mkdocs.yaml ReadMe.md

publish: site
	cd $< && \
	  git init && \
	  git add -A && \
	  git commit -m 'Deploy Gobb website' && \
	  git push -f $(REPO) HEAD:gh-pages
	$(RM) -r $<

.PHONY: site serve lint publish
