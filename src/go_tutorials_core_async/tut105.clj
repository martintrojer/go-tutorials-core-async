;; http://talks.golang.org/2012/concurrency.slide#38

(ns go-tutorials-core-async.tut105
  (:use [clojure.core.async]))

(defn boring [msg quit-ch]
  (let [ch (chan)]
    (go
     (loop [i 0]
       (let [[_ mch] (alts! [(timeout (rand-int 1000)) quit-ch])]
         (if (= mch quit-ch)
           (>! quit-ch "See you!")
           (do
             (>! ch (str msg i))
             (recur (inc i)))))))
    ch))

(let [quit (chan)
      joe (boring "Joe" quit)]
  (dotimes [_ 5]
    (println (<!! joe)))
  (println "You talk to much.")
  (>!! quit :stop)
  (println "Joe says" (<!! quit)))
