;; http://talks.golang.org/2012/concurrency.slide#50

(ns go-tutorials-core-async.tut107
  (:use [clojure.core.async]))

(defn fake-search [kind]
  (fn [ch query]
    (go
     (<! (timeout (rand-int 100)))
     (>! ch [kind query]))))

(def web1 (fake-search :web1))
(def web2 (fake-search :web2))
(def image1 (fake-search :image1))
(def image2 (fake-search :image2))
(def video1 (fake-search :video1))
(def video2 (fake-search :video2))

(defn fastest [query & replicas]
  (let [ch (chan)]
    (doseq [replica replicas]
      (replica ch query))
    ch))

(defn google [query]
  (let [ch (chan)]
    (go (>! ch (<! (fastest query web1 web2))))
    (go (>! ch (<! (fastest query image1 image2))))
    (go (>! ch (<! (fastest query video1 video2))))
    (loop [i 0 ret []]
      (if (= i 3)
        ret
        (recur (inc i) (conj ret
                             (alt!! [ch (timeout 80)]
                                    ([v] v))))))))

(doseq [_ (range 10)]
  (println (google "clojure")))
