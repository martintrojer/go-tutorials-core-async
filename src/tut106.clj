;; http://talks.golang.org/2012/concurrency.slide#39

(ns tut106
  (:require [clojure.core.async :refer [go >! >!! <! <!! chan]]))

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

;; ---

;; The Clojure timings can be greatly improved by various JVM settings
;; since this benchmark is GC dominated.

;; I've gotten this snippet down to < 200ms.
