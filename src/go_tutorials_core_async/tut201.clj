;; http://talks.golang.org/2013/advconc.slide#14 (and later)

(ns go-tutorials-core-async.tut201
  (:use [clojure.core.async]
        [clojure.set]
        [go-tutorials-core-async.http]
        [go-tutorials-core-async.logger]))

(defn subscription [url interval max-pending]
  (let [update-ch (chan)
        quit-ch (chan)
        res-ch (chan)]
    (go
     (log url "started")
     (async-get res-ch url)
     (loop [[fst & rst :as updates] []
            seen-ids (->> updates (map :id) (into #{}))]
       (let [query [res-ch quit-ch (timeout interval)]
             query (if-not (nil? fst) (conj query [update-ch fst]) ;; potentially append the put query
                           query)
             [msg ch] (alts! query)]
         (cond
          (= ch quit-ch) (close! update-ch)
          (= ch update-ch) (recur rst seen-ids)
          (= ch res-ch) (let [entries (get-blog-entries msg)
                              new-entries (remove #(seen-ids (:id %)) entries)]
                          (log "got" (count entries) ": new" (count new-entries) ":" url)
                          (recur (take max-pending (concat updates new-entries))
                                 (union seen-ids (->> new-entries (map :id) (into #{})))))
          :else (do
                  (async-get res-ch url)
                  (recur updates seen-ids)))))
     (log url "subscriber quitting"))
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
    (log (<!! all-ch))
    (Thread/sleep 1000))
  (close! all-ch)
  (doseq [qc (map second chs)]
    (>!! qc true)))
