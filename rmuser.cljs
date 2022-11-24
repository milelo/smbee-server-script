#!/usr/bin/env -S nbb -cp "/home/smbee-admin/smbee-server-script/"
(ns rmuser
  (:require
   [lib.utils :refer [$]]
   [cljs-bean.core :refer [bean ->clj ->js]]
   ["argparse" :as argparse :refer [ArgumentParser]]))

(def args (let [parser (ArgumentParser. #js {:prog "nbb rmuser.cljs"
                                             :description "Remove a user and user-files"})]
            (.add_argument parser "username" #js {:help "user name"})
            (bean (.parse_args parser (->js (vec *command-line-args*))))))

(def username (:username args))

($ :deluser username)
($ :rm "-fr" (str "/home/" username))