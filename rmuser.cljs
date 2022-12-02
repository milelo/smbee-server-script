#!/usr/bin/env -S nbb -cp "/home/smbee-admin/smbee-server-script/"
(ns rmuser
  (:require
   [promesa.core :as p]
   [lib.utils :refer [$]]
   [cljs-bean.core :refer [bean ->js]]
   ["argparse" :as argparse :refer [ArgumentParser]]
   ["zx$path" :as path]))

(def args (let [parser (ArgumentParser. #js {:prog "nbb rmuser.cljs"
                                             :description "Remove a user and user-files"})]
            (.add_argument parser "username" #js {:help "user name"})
            (bean (.parse_args parser (->js (vec *command-line-args*))))))

(defn main [{:keys [username]}] 
  (p/let [_ (p/catch ($ 'killall '-u username) p/resolved); ignore 'rejected' if nothing to kill
          _ ($ :deluser username)
          _ ($ 'rm '-fr (path/join "/home/" username))
          ]
    ))

(main args)