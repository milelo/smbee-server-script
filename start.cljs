#!/usr/bin/env -S nbb -cp "/home/smbee-admin/smbee-server-script/"
(ns start
  (:require
   ["argparse" :as argparse :refer [ArgumentParser]]
   [cljs-bean.core :refer [bean ->clj ->js]];https://github.com/mfikes/cljs-bean
   [promesa.core :as p]
   ;[clojure.pprint :refer [cl-format]]
   ["zx" :as zx]
   ["zx$path" :as path]
   ["zx$fs" :as fs]
   ["zx$os" :as os]
   ["zx$globby" :as globby]
   [lib.utils :as utils :refer [$]]
   [clojure.string :refer [split]]
   ["js-yaml" :as yaml]
   ["child_process" :refer [spawn exec]]))

(def _args (let [parser (ArgumentParser. #js {:prog "start"
                                              :description "Start vscode-server on SMBeeFirmware"})]
             (bean (.parse_args parser (->js (vec *command-line-args*))))))

(defn get-cs-port
  "Get the port-number string from a code-server config file."
  [config-path]
  (let [yaml (-> (fs/readFileSync config-path "utf8") yaml/load ->clj)
        port (-> yaml :bind-addr (split \:) second js/parseInt)]
    (when (int? port) port)))

(defn new-cs-config-yaml
  "Create a new YAML config with a unique port."
  []
  ;search all user code-server config files for used ports an choose a new one.
  (p/let [;https://www.npmjs.com/package/js-yaml
          ;find home dirs
          hds (globby "/home/*" #js {:expandDirectories false
                                     :onlyFiles false})
          ports (p/all
                 (for [hd hds]
                   (p/let [path (path/join hd ".config/code-server/config.yaml")
                           exists (fs/pathExists path)]
                     (when exists
                       (get-cs-port path)))))
          nextport (inc (reduce max 8080 ports))
          new-yaml (yaml/dump #js {:bind-addr (str "127.0.0.1:" nextport)
                                   :auth "none"
                                   :cert false})]
    new-yaml))

(set! (.-verbose zx/$) false)

(defn main []
  (p/let [{:keys [username]} (->clj (os/userInfo))
          ;Ensure users account has full-name set:
          pwe ($ "getent passwd" username)
          {:keys [full-name]} (-> pwe str utils/parse-passwd)
          _ (set! (.-verbose zx/$) true);Must enable to receive password prompt
          full-name (or (not-empty full-name)
                        (p/let [fname (zx/question "Your full name? ")
                                _ ($ "chfn -f" fname)]
                          fname))
          firmware-path (path/join (os/homedir) "SMBeeFirmware")
          firmware-dir? (fs/pathExists firmware-path)
          _ (when-not firmware-dir?
              ($ "git clone https://github.com/milelo/SMBeeFirmware.git" firmware-path))
          ;
          ;ensure there is a code-server config file with a unique port for this user
          config-path (path/join (os/homedir) ".config/code-server/config.yaml")
          config-file? (fs/pathExists config-path)
          cs-port (and config-file? (get-cs-port config-path))
          _ (when-not cs-port
              (p/let [yaml (new-cs-config-yaml)]
                (fs/ensureFileSync config-path)
                (fs/writeFileSync config-path yaml)))
          cs-port (or cs-port (get-cs-port config-path))
          spawned (spawn "code-server");https://nodejs.org/api/child_process.html
          client-port 8080
          hostname "smbee-zero.local"]
    (.on spawned "exit" (fn [exit-code]
                          (when (> exit-code 0)
                            (println "\ncode-server terminated with code:" exit-code))))
    (println "Hello" full-name)
    (println "Starting code-server; pid:" (-> spawned bean :pid))
    (println "\nOpen a client terminal and enter:")
    ;see https://coder.com/docs/code-server/latest/guide
    (println (str "ssh -N -L " client-port ":127.0.0.1:" cs-port " " username "@" hostname))
    (println "\nIn a client browser open:")
    (println (str "http://localhost:" client-port "/?folder=" firmware-path))))

(main)

