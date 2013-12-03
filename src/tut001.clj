;; http://tour.golang.org/#62

(ns tut1
  (:require [clojure.core.async :refer [go timeout <!]]))

(go
 (dotimes [_ 5]
   (<! (timeout 100))
   (println "world")))

(println "hello")
