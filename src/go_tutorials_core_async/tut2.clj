;; http://tour.golang.org/#63

(ns go-tutorials-core-async.tut2
  (:use [clojure.core.async]))

(defn sum [vals ch]
  (go (>! ch (reduce + vals))))

(let [vals [7 2 8 -9 4 0]
      ch (chan)
      t! #(<!! (go (<! ch)))]
  (go (sum (take 3 vals) ch))
  (go (sum (drop 3 vals) ch))

  (let [x (t!)
        y (t!)]
    (println x y (+ x y))))
