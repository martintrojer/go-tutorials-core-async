;; http://talks.golang.org/2012/concurrency.slide#30

(ns go-tutorials-core-async.tut103
  (:use [clojure.core.async])
  (:require [go-tutorials-core-async.tut102 :as tut102]))

(defn boring [msg]
  (let [ch (chan)
        wait (chan)]
    (go
     (loop [i 0]
       (>! ch {:str (str msg i) :wait wait})
       (<! (timeout (rand-int 1000)))
       (<! wait)
       (recur (inc i))))
    ch))

(let [ch (tut102/fan-in (boring "Joe") (boring "Ann"))]
  (dotimes [_ 5]
    (let [msg1 (<!! ch)
          msg2 (<!! ch)]
      (println (:str msg1))
      (println (:str msg2))
      (>!! (:wait msg1) :foo)
      (>!! (:wait msg2) :bar)))
  (println "You're both boring, I'm leaving"))
