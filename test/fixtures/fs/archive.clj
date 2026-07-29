(require '[babashka.fs :as fs])

(fs/create-dirs "input/nested")
(fs/write-lines "input/root.txt" ["root"])
(fs/write-lines "input/nested/child.txt" ["child"])
(fs/write-lines "input/nested/ignored.edn" ["ignored"])

(fs/zip "bundle.zip" ["input"])
(fs/unzip "bundle.zip" "output")

(let [files
      (mapv #(str (fs/relativize "output" %))
            (sort-by str
                     (fs/glob "output" "**/*.txt")))]
  (prn
   {:files files
    :root (fs/read-all-lines "output/input/root.txt")
    :child
    (fs/read-all-lines "output/input/nested/child.txt")}))
