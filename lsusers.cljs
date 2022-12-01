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

(defn- parse-passwds [output]
  (map utils/parse-passwd (split output \newline)))

(defn list-users []
  (println)
  (p/let [output ($ "getent passwd | grep /home/")
          ;output ($ "getent passwd")
          user-info (-> output str parse-passwds)]
    (doseq [{:keys [user full-name] :as _e} user-info]
      ;(pprint _e)
      (println (if full-name
                 (str user ", " full-name)
                 user)))))

(list-users)