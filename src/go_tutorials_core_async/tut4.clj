;; http://tour.golang.org/#66

(ns go-tutorials-core-async.tut4
  (:use [clojure.core.async]))

(defn fib-q [res-ch quit-ch]
  (go
   (loop [x 0 y 1]
     (let [[msg chan] (alts! [[res-ch x] quit-ch])]
       (if (= chan quit-ch)
         (println "quit")
         (recur y (+ x y)))))))

(let [res-ch (chan)
      quit-ch (chan)
      t! #(<!! (go (<! res-ch)))]
  (go
   (doseq [_ (range 10)]
     (println (t!)))
   (>! quit-ch 0))

  (fib-q res-ch quit-ch))
