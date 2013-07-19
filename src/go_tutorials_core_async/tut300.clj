;; http://golang.org/doc/codewalk/sharemem/

(ns go-tutorials-core-async.tut300
  (:use [clojure.core.async]
        [go-tutorials-core-async.http]))

(defn state-monitor [interval]
  (let [updates (chan)]
    (go
     (loop [statuses {}]
       (let [[msg ch] (alts! [updates (timeout interval)])]
         (cond
          (and msg (= ch updates))
          (recur (-> statuses
                    (assoc-in [(:url msg) :status] (:status msg))
                    (update-in [(:url msg) :count] (fnil inc 0))))

          (= ch updates) :stop

          :else
          (do
            (doseq [s statuses] (println s))
            (recur statuses)))))
     (println "state-monitor stopping"))
    updates))

(defn poller [in out status]
   (go
    (loop []
      (let [resource (<!! in)
            res-ch (chan)]
        (when resource
          (println "polling" resource)
          (async-get (:url resource) res-ch)
          (let [res (<! res-ch)]
            (when (:error res)
              (swap! (:error-count resource) inc))
            (>! status (assoc res :url (:url resource))))
          (>! out resource)
          (recur))))
   (println "poller thread shutting down")))

(defn wait-and-schedule [resource out]
  (go
   (println "waiting for" (:url resource))
   (<! (timeout (+ 10000 (rand-int 100) (* 10000 @(:error-count resource)))))
   (println "scheduling" (:url resource))
   (>! out resource)))

(def run (atom true))

(let [urls ["http://www.google.com/"
            "http://golang.org/"
            "http://blog.golang.org/"]
      pending (chan)
      complete (chan)
      status (state-monitor 5000)]

  (dotimes [_ 2]
    (poller pending complete status))

  ;; create and seed resources
  (go
   (doseq [u urls]
     ;; we use an atom here to simulate handing over mutable state
     (>! pending {:url u :error-count (atom 0)})))

  (go
   (loop []
     (when @run
       (let [[resource ch] (alts! [complete (timeout 1000)])]
         (when (= ch complete)
           (wait-and-schedule resource pending))
         (recur))))
   (close! pending)
   (close! complete)
   (close! status)))

;; (reset! run false)
