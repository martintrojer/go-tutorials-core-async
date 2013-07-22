;; http://talks.golang.org/2013/advconc.slide#14 (and later)

(ns go-tutorials-core-async.tut201
  (:use [clojure.core.async]
        [clojure.set]
        [go-tutorials-core-async.http]))

(def get-entries (partial get-blog-entries blocking-get))

(defn subscription [url interval max-pending]
  (let [update-ch (chan)
        quit-ch (chan)]
    ;; thread because we using a blocking get call
    (thread
     (loop [[fst & rst :as updates] (get-entries url)
            seen-ids (->> updates (map :id) (into #{}))]
       (let [query [quit-ch (timeout interval)]
             query (if-not (nil? fst) (conj query [update-ch fst]) ;; potentially append the put query
                           query)
             [_ ch] (alts!! query)]
         (cond
          (= ch quit-ch) (close! update-ch)
          (= ch update-ch) (recur rst seen-ids)
          :else (let [entries (get-entries url)
                      new-entries (remove #(seen-ids (:id %)) entries)]
                  (println "got" (count entries) ": new" (count new-entries) ":" url)
                  (recur (take max-pending (concat updates new-entries))
                         (union seen-ids (->> new-entries (map :id) (into #{}))))))))
     (println url "subscriber quitting"))
    [update-ch quit-ch]))

(defn fan-in [& ins]
  (let [ch (chan)]
    (doseq [i ins]
      (go (loop [v (<! i)]
            (when v (>! ch v))
            (recur (<! i)))))
    ch))

(let [feeds ["http://feeds.feedburner.com/GDBcode?format=xml"
             "http://blog.golang.org/feeds/posts/default"
             "http://feeds.feedburner.com/GoogleOpenSourceBlog?format=xml"]
      chs (mapv #(subscription % 1000 500) feeds)
      all-ch (apply fan-in (map first chs))]
  (dotimes [_ 5]
    (println (<!! all-ch))
    (Thread/sleep 1000))
  (close! all-ch)
  (doseq [qc (map second chs)]
    (>!! qc true)))
