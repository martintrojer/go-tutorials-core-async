;;http://tour.golang.org/#70

(ns go-tutorials-core-async.tut6
  (:use [clojure.core.async]))

(defprotocol Fetcher
    (fetch [this url]))

(defn crawl [url depth fetcher ch]
  (let [seen (atom #{})
        crawler (fn crawler [url depth]
                  (go
                   (when (and (pos? depth) (not (@seen url)))
                     (swap! seen conj url)
                     (when-let [res (fetch fetcher url)]
                       (>! ch (assoc res :url url))
                       (doseq [u (:urls res)]
                         (crawler u (dec depth)))))))]
    (crawler url depth)))

(defrecord FakeFetcher []
  Fetcher
  (fetch [_ url]
    ({"http://golang.org/"
      {:body "The Go Programming Language"
       :urls ["http://golang.org/pkg/"
              "http://golang.org/cmd/"]}

      "http://golang.org/pkg/"
      {:body "Packages"
       :urls ["http://golang.org/"
              "http://golang.org/cmd/"
              "http://golang.org/pkg/fmt/"
              "http://golang.org/pkg/os/"]}

      "http://golang.org/pkg/fmt/"
      {:body "Package fmt"
       :urls ["http://golang.org/"
              "http://golang.org/pkg/"]}

      "http://golang.org/pkg/os/"
      {:body "Package os"
       :urls ["http://golang.org/"
              "http://golang.org/pkg/"]}}
     url)))

(let [ch (chan)]
  (crawl "http://golang.org/" 4 (->FakeFetcher) ch)
  (loop []
    (let [tmo (timeout 500)
          [msg chan] (alts!! [tmo ch])]
      (if (= chan tmo)
        (println "done")
        (do
          (println "Found" (:url msg) (:body msg))
          (recur))))))
