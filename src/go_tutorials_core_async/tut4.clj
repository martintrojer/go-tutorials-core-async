;; http://tour.golang.org/#66

(ns go-tutorials-core-async.tut4
  (:use [clojure.core.async]))

(defn fib-q [res-ch quit-ch]
  (go
   (loop [x 0 y 1]
     ;; note we're putting onto res-ch inside the alts!
     (let [[_ chan] (alts! [[res-ch x] quit-ch])]
       (if (= chan quit-ch)
         (println "quit")
         (recur y (+ x y)))))))

(let [res-ch (chan)
      quit-ch (chan)]
  (go
   (dotimes [_ 10]
     (println (<! res-ch)))
   (>! quit-ch 0))

  ;; note that fib-q go 'process' are created after the consumer above

  (fib-q res-ch quit-ch)
  )
