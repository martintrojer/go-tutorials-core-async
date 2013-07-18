;; http://talks.golang.org/2012/concurrency.slide#39

(ns go-tutorials-core-async.tut106
  (:use [clojure.core.async]))

(defn f [left right]
  (go (>! left (inc (<! right)))))

(let [leftmost (chan)
      rightmost (loop [n 100000 left leftmost]
                  (if-not (pos? n)
                    left
                    (let [right (chan)]
                      (f left right)
                      (recur (dec n) right))))]
  (time
   (do
     (>!! rightmost 1)
     (println (<!! leftmost)))))

;; "Elapsed time: 1184.47656 msecs"

;; $ go run tut106.go
;; 67.87ms

;; that makes core.async an order of magnitude slower
