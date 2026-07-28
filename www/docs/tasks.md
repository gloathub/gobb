---
title: Tasks and processes
description: BB-compatible task graphs and native process pipelines in Gobb
---

# Tasks and processes

Gobb evaluates `bb.edn` tasks with the full Glojure runtime. Named tasks work
both through BB's explicit `run` command and as direct commands:

```console
$ gobb run build
$ gobb test --focus unit
$ gobb tasks
$ gobb doc build
```

Use `--prn` to print a task result and `--parallel` to execute independent
dependency levels concurrently:

```console
$ gobb run --parallel --prn release
```

## Task configuration

```clojure
{:tasks
 {:requires ([project.build :as build])
  :init (def build-state (atom {}))
  :enter (println "Starting" (:name (current-task)))
  :leave (println "Finished" (:name (current-task)))

  clean
  {:doc "Remove generated files."
   :task (build/clean)}

  test
  {:depends [clean]
   :task (build/test)}

  release
  {:depends [test]
   :task (build/release test)}}}
```

Gobb supports:

- global and per-task `:requires`;
- one-time global `:init`;
- dependency ordering through `:depends`;
- global or per-task `:enter` and `:leave` hooks;
- dependency results as task-named Vars;
- `current-task`, including the result during `:leave`;
- task arguments through `*command-line-args*`;
- qualified function tasks;
- nested `run`;
- `:doc`, `:private`, and dash-prefixed private tasks.

Task graphs reject missing dependencies and cycles before executing work.

## Processes and pipelines

The task `shell` helper is backed directly by Go's `os/exec` package:

```clojure
{:tasks
 {message
  (-> (shell {:out :string} "printf 'Go + bb!\n'")
      (shell {:out :string} "cat")
      :out)

  check
  (shell {:dir "test"
          :extra-env {"MODE" "ci"}}
         "sh" "-c" "run-tests \"$MODE\"")}}
```

Process options cover inherited or captured input/output/error streams,
working directory, environment replacement or extension, continued nonzero
exits, and custom error functions. Results contain `:cmd`, `:exit`, `:out`,
and `:err`.

Subprocesses are a native capability. WASI preview 1 and browsers cannot spawn
host processes, so the same operation returns Gobb's structured
`:gobb/unsupported-capability` error on those targets.
