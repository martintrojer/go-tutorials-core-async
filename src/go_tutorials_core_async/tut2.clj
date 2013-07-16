;; http://tour.golang.org/#63

(ns go-tutorials-core-async.tut2
  (:use [clojure.core.async]))

(defn sum [vals ch]
  (go (>! ch (reduce + vals))))

(let [vals [7 2 8 -9 4 0]
      ch (chan)]
  (go (sum (take 3 vals) ch))
  (go (sum (drop 3 vals) ch))

  (let [x (<!! ch)
        y (<!! ch)]
    (println x y (+ x y))))
