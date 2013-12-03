;; http://tour.golang.org/#63

(ns tut2
  (:require [clojure.core.async :refer [go chan <! <!!]]))

(defn sum [vals ch]
  (go (>! ch (clojure.core/reduce + vals))))

(let [vals [7 2 8 -9 4 0]
      ch (chan)]
  (go (sum (take 3 vals) ch))
  (go (sum (drop 3 vals) ch))

  (let [x (<!! ch)
        y (<!! ch)]
    (println x y (+ x y))))
