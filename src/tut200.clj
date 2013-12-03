;; http://talks.golang.org/2013/advconc.slide#6

(ns tut200
  (:require [clojure.core.async :refer [go-loop timeout >! >!! <! <!! chan]]))

(defn player [name table]
  (go-loop []
           (let [ball (<! table)]
             (println name ball)
             (<! (timeout (rand-int 100)))
             (>! table (inc ball))
             (recur))))

(let [table (chan)]
  (player "ping" table)
  (player "pong" table)

  (>!! table 0)      ;; go-lang will detect a deadlock if this commented out
  (Thread/sleep 1000)
  (<!! table)  ;; take the ball off the table
  (println "game over"))
