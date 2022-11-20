(ns start
  (:require
   ["argparse" :as argparse :refer [ArgumentParser]]
   [clojure.pprint :refer [pprint]]
   [cljs-bean.core :refer [bean ->clj ->js]];https://github.com/mfikes/cljs-bean
   [clojure.string :refer [split join]]
   [promesa.core :as p]
   ["zx" :as zx :refer [question]]
   ["path" :as path]
   ["zx$fs" :as fs]
   ["zx$os" :as os]
   [utils :as utils :refer [$]])
  )

;git clone https://github.com/milelo/SMBeeFirmware.git
; check free port -  ss -tulw
;config code-server

(p/let [p1 (path/resolve "~/SMBeeFirmware")
        ;p ($ 'echo "$HOME/SMBeeFirmware")
       ; r (fs/pathExists "$HOME/SMBeeFirmware")
        ]
  (prn p1)
  #_(when-not r
    ($ "git clone https://github.com/milelo/SMBeeFirmware.git ~/SMBeeFirmware")
    ))
