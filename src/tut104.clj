;; http://talks.golang.org/2012/concurrency.slide#34

(ns tut104
  (:require [clojure.core.async :refer [go-loop >! <! <!! alt! chan]]
            [tut101]))

(defn fan-in [in1 in2]
  (let [ch (chan)]
    (go-loop []
             ;; note alt! result-expr syntax
             (alt! [in1 in2]
                   ([msg _] (>! ch msg)))
             (recur))
    ch))

(let [ch (fan-in (tut101/boring "Joe") (tut101/boring "Ann"))]
  (dotimes [_ 10]
    (println (<!! ch)))
  (println "You're both boring, I'm leaving"))
