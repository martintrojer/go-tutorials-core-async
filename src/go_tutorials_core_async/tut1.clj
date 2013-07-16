;; http://tour.golang.org/#62

(ns go-tutorials-core-async.tut1
  (:use [clojure.core.async]))

(go
 (dotimes [_ 5]
   (<! (timeout 100))
   (println "world")))

(println "hello")
