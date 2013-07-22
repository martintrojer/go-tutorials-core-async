;; sieve of eratosthenes

(ns go-tutorials-core-async.sieve
  (:use [clojure.core.async]))

(defn prime-filter [in out prime]
  (go
   (loop [i (<! in)]
     (when-not (zero? (mod i prime))
       (>! out i))
     (recur (<! in)))))

(let [ch (chan)]
  (go (loop [i 2] (>! ch i) (recur (inc i))))

  (loop [ch ch, i 100]
    (when (pos? i)
      (let [prime (<!! ch)
            next-ch (chan)]
        (println prime)
        (prime-filter ch next-ch prime)
        (recur next-ch (dec i))))))
