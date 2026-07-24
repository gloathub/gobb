# Gobb Website

The Gobb documentation site is built with MkDocs Material and published from
the `gh-pages` branch.

From the repository root:

```bash
make serve
make publish
```

`make serve` installs the required tools through Makes and starts the local
development server at <http://127.0.0.1:8000/>.

`make publish` builds the strict production site and force-pushes the generated
site to the `gh-pages` branch.

The site can also be built without serving or publishing:

```bash
make site
```
