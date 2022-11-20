(ns utils
  (:require
   [clojure.string :refer [join]]
   ["zx" :as zx]))

(defn $ [& cmd-strs]
  (zx/$ (array (join \space (map (fn [x]
                                   (cond
                                     (string? x) x
                                     (or (symbol? x) (keyword? x)) (name x)
                                     :else (str x)))
                                 cmd-strs)))))
