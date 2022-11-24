#!/usr/bin/env -S nbb -cp "/home/smbee-admin/smbee-server-script/"
(ns newuser
  (:require 
   ["argparse" :as argparse :refer [ArgumentParser]]
   [clojure.pprint :refer [pprint]]
   [cljs-bean.core :refer [bean ->clj ->js]];https://github.com/mfikes/cljs-bean
   [clojure.string :refer [split join]]
   [promesa.core :as p]
   ["zx" :as zx :refer [question]]
   ["zx$fs" :as fs]
   ["zx$os" :as os]
   [lib.utils :as utils :refer [$]]))

(set! (.-verbose zx/$) true)

(def args (let [parser (ArgumentParser. #js {:prog "nbb newuser.cljs"
                                             :description "Add a new user"})]
            (.add_argument parser "username" #js {:help "new user name"})
            (bean (.parse_args parser (->js (vec *command-line-args*))))))

(def username (:username args))

;(.dir js/console args)

(p/let [op ($ "adduser --gecos '' --disabled-password" (:username args))
        op ($ "chpasswd <<<" (str username \: username));username:password
        ])








