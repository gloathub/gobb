(require '[babashka.fs :as fs])

(let [root (fs/path (first *command-line-args*))
      source (fs/path root "source")
      nested (fs/path source "nested")
      deep (fs/path nested "deep")
      text (fs/path nested "hello.txt")
      copied (fs/path root "copied.txt")
      moved (fs/path root "moved.txt")
      temporary (fs/create-temp-file {:dir root
                                      :prefix "gobb-"
                                      :suffix ".tmp"})
      relative-names
      (fn [paths]
        (mapv #(str (fs/relativize source %))
              (sort-by str paths)))
      walk-events (atom [])
      event-names
      (fn [event]
        (vec
         (sort
          (map second
               (filter #(= event (first %))
                       @walk-events)))))
      scoped-path (atom nil)
      scoped-result
      (fs/with-temp-dir
        [directory {:dir root :prefix "scoped-"}]
        (reset! scoped-path directory)
        (fs/directory? directory))]
  (fs/create-dirs deep)
  (fs/write-lines text ["one" "two"])
  (fs/write-lines (fs/path source "root.clj") ["root"])
  (fs/write-lines (fs/path nested "one.clj") ["one"])
  (fs/write-lines (fs/path deep "two.clj") ["two"])
  (fs/write-lines (fs/path nested ".hidden.clj") ["hidden"])
  (fs/set-posix-file-permissions temporary "rw-r-----")
  (fs/copy text copied)
  (fs/move copied moved)
  (fs/walk-file-tree
   source
   {:pre-visit-dir
    (fn [candidate _]
      (swap! walk-events conj
             [:pre (str (fs/relativize source candidate))])
      :continue)
    :visit-file
    (fn [candidate _]
      (swap! walk-events conj
             [:file (str (fs/relativize source candidate))])
      :continue)
    :post-visit-dir
    (fn [candidate _]
      (swap! walk-events conj
             [:post (str (fs/relativize source candidate))])
      :continue)})
  (let [archive-source (fs/path root "archive.txt")
        unpacked-directory (fs/path root "unpacked")
        _ (fs/write-lines archive-source ["compressed" "content"])
        gzip-file (fs/gzip archive-source)
        written (fs/gunzip gzip-file unpacked-directory)]
    (prn
     {:paths [(str (fs/path "a" "b" "c"))
            (str (fs/file "a" "b" "c"))
            (str (fs/normalize "a/./b/../c"))
            (str (fs/parent "a/b/c"))
            (str (fs/file-name "a/b/c.txt"))
            (mapv str (fs/components "a/b/c"))
            (fs/split-ext "a/b/c.txt")
            (fs/strip-ext "a/b/c.txt")
            (fs/extension "a/b/c.txt")]
    :predicates [(fs/relative? "a/b")
                 (fs/absolute? root)
                 (fs/directory? nested)
                 (fs/regular-file? text)
                 (fs/exists? moved)
                 (fs/same-file? text text)
                 (fs/starts-with? text source)
                 (fs/ends-with? text "hello.txt")]
    :contents [(fs/read-all-lines text)
               (fs/read-all-lines moved)
               (mapv fs/file-name (sort-by str (fs/list-dir root "*.txt")))]
    :glob [(relative-names (fs/glob source "*.clj"))
           (relative-names (fs/glob source "**/*.clj"))
           (relative-names
            (fs/glob source "*.clj" {:recursive true}))
           (relative-names
            (fs/glob source "**/*.clj" {:hidden true}))
           (relative-names
            (fs/match source "regex:.*\\.txt"
                      {:recursive true}))]
    :walk {:pre (event-names :pre)
           :file (event-names :file)
           :post (event-names :post)}
    :gzip [(pos? written)
           (fs/read-all-lines
            (fs/path unpacked-directory "archive.txt"))]
    :permissions
    [(fs/posix->str (fs/posix-file-permissions temporary))
     (fs/posix->str (fs/str->posix "rwxr-x---"))]
    :temporary [(fs/regular-file? temporary)
                (= "tmp" (fs/extension temporary))
                scoped-result
                (fs/exists? @scoped-path)]})
    (fs/delete temporary)
    (fs/delete-tree source)
    (fs/delete moved)
    (fs/delete archive-source)
    (fs/delete gzip-file)
    (fs/delete-tree unpacked-directory)
    (prn {:remaining (mapv fs/file-name (fs/list-dir root))
          :deleted [(fs/exists? temporary)
                    (fs/exists? source)
                    (fs/exists? moved)]})))
