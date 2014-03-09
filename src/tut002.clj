;; http://tour.golang.org/#63

(ns tut2
  (:require [clojure.core.async :refer [chan put! <!!]]))

(defn sum [vals ch]
  (put! ch (clojure.core/reduce + vals)))

(let [vals [7 2 8 -9 4 0]
      ch (chan)]
  (sum (take 3 vals) ch)
  (sum (drop 3 vals) ch)

  (let [x (<!! ch)
        y (<!! ch)]
    (println x y (+ x y))))
