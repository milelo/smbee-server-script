(ns lib.utils
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

(let [;https://medium.com/factory-mind/regex-tutorial-a-simple-cheatsheet-by-examples-649dc1c3f285
      entry-re #"^([^:]*):([^:]*):([^:]*):([^:]*):([^:]*):([^:]*):([^:]*)"
      gecos-re #"^([^,]*)?\,?([^,]*)?\,?([^,]*)?\,?([^,]*)?\,?([^,]*)?"]
  (defn parse-passwd-entry [entry]
    (let [m (zipmap [:entry :user :pw :uid :gid :gecos :home-dir :shell] (re-find entry-re entry))
          m (into m (map (fn [k v]
                           (when v
                             [k v]))
                         [:full-name :room-number :work-phone :home-phone :other]
                         (rest (re-find gecos-re (:gecos m)))))]
      m)))
