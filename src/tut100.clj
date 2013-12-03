;; http://talks.golang.org/2012/concurrency.slide#16

(ns tut100
  (:require [clojure.core.async :refer [go timeout]]))

(def run (atom true))

(go
 (while @run
   (println "boring!")
   (<! (timeout (rand-int 1000)))))

(Thread/sleep 2000)
(println "You're boring, I'm leaving")
(reset! run false)
