;; http://tour.golang.org/#65

(ns go-tutorials-core-async.tut3
  (:use [clojure.core.async]))

(defn fib-n [n ch]
  (go
   (loop [x 0 y 1 ctr 0]
     (if (< ctr n)
       (do
         (>! ch x)
         (recur y (+ x y) (inc ctr)))
       (close! ch)))))

(let [ch (chan 10)
      t! #(<!! (go (<! ch)))]

  (fib-n 20 ch)
  (loop [v (t!)]
    (when v
      (println v)
      (recur (t!)))))
