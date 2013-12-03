;; sieve of eratosthenes

(ns sieve
  (:require [clojure.core.async :refer [go-loop >! <! <!! chan]]))

(defn prime-filter [in out prime]
  (go-loop [i (<! in)]
           (when-not (zero? (mod i prime))
             (>! out i))
           (recur (<! in))))

(let [ch (chan)]
  (go-loop [i 2] (>! ch i) (recur (inc i)))

  (loop [ch ch, i 100]
    (when (pos? i)
      (let [prime (<!! ch)
            next-ch (chan)]
        (println prime)
        (prime-filter ch next-ch prime)
        (recur next-ch (dec i))))))
