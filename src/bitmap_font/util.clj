(ns bitmap-font.util)

;;; file utilities
(defn file-exists? [path]
  (let [f (java.io.File. path)]
    (and (.exists f) (.isFile f))))

(defn directory-exists? [path]
  (let [f (java.io.File. path)]
    (and (.exists f) (.isDirectory f))))

