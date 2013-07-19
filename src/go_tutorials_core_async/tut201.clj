;; http://talks.golang.org/2013/advconc.slide#14

(ns go-tutorials-core-async.tut201
  (:use [clojure.core.async]
        [go-tutorials-core-async.http]))

(defn subscription [url interval]
  (let [updates (chan)
        quit (chan)]
    (thread
     (loop [seen #{}]
       (let [[_ ch] (alts!! [quit (timeout interval)])]
         (when-not (= ch quit)
           (>!! updates (blocking-get url))
           (recur seen)))))
    [updates quit]))
