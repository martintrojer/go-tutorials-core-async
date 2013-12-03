;; http://talks.golang.org/2012/concurrency.slide#26

(ns tut101
  (:require [clojure.core.async :refer [go-loop timeout >! <! <!! chan]]))

(defn boring [msg]
  (let [ch (chan)]
    (go-loop [i 0]
      (>! ch (str msg i))
      (<! (timeout (rand-int 1000)))
      (recur (inc i)))
    ch))

(let [joe-ch (boring "Joe")
      ann-ch (boring "Ann")]
  (dotimes [_ 5]
    (println (<!! joe-ch))
    (println (<!! ann-ch)))
  (println "You're both boring, I'm leaving"))
