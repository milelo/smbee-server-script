#!/usr/bin/env -S nbb -cp "/home/smbee-admin/smbee-server-script/"
(ns mkuser
  (:require
   ["argparse" :as argparse :refer [ArgumentParser]]
   [clojure.pprint :refer [pprint]]
   [cljs-bean.core :refer [bean ->clj ->js]];https://github.com/mfikes/cljs-bean
   [clojure.string :refer [split join]]
   [promesa.core :as p]
   ["zx" :as zx :refer [question]]
   ["zx$fs" :as fs]
   ["zx$os" :as os]
   ["zx$path" :as path]
   [lib.utils :as utils :refer [$]]))

(set! (.-verbose zx/$) true)

(def args (let [parser (ArgumentParser. #js {:prog "sudo mkuser"
                                             :description "Add a new user"})]
            (.add_argument parser "username" #js {:help "New-users name"})
            (bean (.parse_args parser (->js (vec *command-line-args*))))))

(def username (:username args))

;(.dir js/console args)

(p/let [_ ($ "adduser --gecos '' --disabled-password" (:username args))
        _ ($ "chpasswd <<<" (str username \: username));username:password
        firmware-path (path/join "/home/" username "/SMBeeFirmware")
        firmware-dir? (fs/pathExists firmware-path)
        _ (when-not firmware-dir?
            ($ "git clone https://github.com/milelo/SMBeeFirmware.git" firmware-path))
          ;
        ])








