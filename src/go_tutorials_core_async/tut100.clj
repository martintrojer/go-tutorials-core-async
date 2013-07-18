;; http://talks.golang.org/2012/concurrency.slide#16

(ns go-tutorials-core-async.tut100
  (:use [clojure.core.async]))

(def run (atom true))

(go
 (while @run
   (println "boring!")
   (<! (timeout (rand-int 1000)))))

(Thread/sleep 2000)
(println "You're boring, I'm leaving")
(reset! run false)
