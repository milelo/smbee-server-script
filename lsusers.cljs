#!/usr/bin/env -S nbb --classpath "/home/smbee-admin/smbee-server-script"
(ns lsusers
  (:require
   ["argparse" :as argparse :refer [ArgumentParser]]
   [cljs-bean.core :refer [bean ->clj ->js]];https://github.com/mfikes/cljs-bean
   [clojure.string :refer [split]]
   [promesa.core :as p]
   ["zx" :as zx]
   ["zx$fs" :as fs]
   ["zx$os" :as os]
   [lib.utils :as utils :refer [$]]))

(set! (.-verbose zx/$) false)

(def args (let [parser (ArgumentParser. #js {:prog "lsusers"
                                             :description "list users"})]
            (bean (.parse_args parser (->js (vec *command-line-args*))))))

(let [;https://medium.com/factory-mind/regex-tutorial-a-simple-cheatsheet-by-examples-649dc1c3f285
      entry-re #"^([^:]*):([^:]*):([^:]*):([^:]*):([^:]*):([^:]*):([^:]*)"
      gecos-re #"^([^,]*)?\,?([^,]*)?\,?([^,]*)?\,?([^,]*)?\,?([^,]*)?"]
  (defn- parse-passwd [output]
    (let [parse-entry (fn [entry]
                        (let [m (zipmap [:entry :user :pw :uid :gid :gecos :home-dir :shell] (re-find entry-re entry))
                              m (into m (map (fn [k v]
                                               (when v
                                                 [k v]))
                                             [:name :room-number :work-phone :home-phone :other]
                                             (rest (re-find gecos-re (:gecos m)))))]
                          m))]
      (map parse-entry (split output \newline)))))

(defn list-users []
  (println)
  (p/let [output ($ "getent passwd | grep /home/")
          ;output ($ "getent passwd")
          user-info (parse-passwd (-> output bean :stdout))]
    (doseq [{:keys [user name] :as _e} user-info]
      ;(pprint _e)
      (println (if name
                 (str user ", " name)
                 user)))))

(list-users)