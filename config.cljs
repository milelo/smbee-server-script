#!/usr/bin/env nbb
(ns config
  (:require
   [clojure.pprint :refer [pprint]]
   [cljs-bean.core :refer [bean ->clj ->js]];https://github.com/mfikes/cljs-bean
   [clojure.string :refer [split]]
   [promesa.core :as p]
   ["zx" :as zx :refer [question]]
   ["zx$fs" :as fs]
   ["zx$os" :as os]))

(set! (.-verbose zx/$) false)

(defmacro $ [cmd-str]
  (list 'zx/$ (array cmd-str)))

(defn add-user [])

(defn load-user [])

(let [;https://medium.com/factory-mind/regex-tutorial-a-simple-cheatsheet-by-examples-649dc1c3f285
      entry-re #"^([^:]*):([^:]*):([^:]*):([^:]*):([^:]*):([^:]*):([^:]*)"
      gecos-re #"^([^,]*)?\,?([^,]*)?\,?([^,]*)?\,?([^,]*)?\,?([^,]*)?"]
  (defn- parse-passwd [output]
    (let [prase-entry (fn [entry]
                        (let [m (zipmap [:entry :user :pw :uid :gid :gecos :home-dir :shell] (re-find entry-re entry))
                              m (into m (map (fn [k v]
                                               (when v
                                                 [k v]))
                                             [:name :room-number :work-phone :home-phone :other]
                                             (rest (re-find gecos-re (:gecos m)))))]
                          m))]
      (map prase-entry (split output \newline)))))

(defn list-users []
  (p/let [output ($ "getent passwd | grep /home/")
          ;output ($ "getent passwd")
          user-info (parse-passwd (-> output bean :stdout))]
    (doseq [{:keys [user name] :as _e} user-info]
      ;(pprint _e)
      (println (if name
                 (str user ", " name)
                 user)))))

(defn play []
  (p/let [_ ($ "sudo adduser tmp")]
    (println {:platform (os/platform)})
    (println "open a terminal and run:")
    (println "ssh -N -L 8080:127.0.0.1:8080 smbee@smbee-zero.local")
    ;(println "Browse to http://localhost:8080/?folder=/home/smbee/SMBeeFirmware")
    (println "Done.")))

(defn choose-option []
  (println "\n==============================\n
            1 Add user
            2 load user
            3 list users
            4 play
            ")
  (p/let [option (question "select option? ")]
    (println)
    (case option
      \1 (add-user)
      \2 (load-user)
      \3 (list-users)
      \4 (play)
      (println "invalid option"))))

(choose-option)










