#!/usr/bin/env -S nbb --classpath /home/mike/smbee-server-script/
(ns test
  (:require
   [utils :refer [$]]))

($ :echo "${PWD}")