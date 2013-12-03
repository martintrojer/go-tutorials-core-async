(ns tuthttp
  (:require [clojure.core.async :refer [go >!]]
            [clj-http.client]
            [org.httpkit.client]
            [clojure.xml :as xml]
            [clojure.zip :as zip]))

(defn blocking-get [url]
  (clj-http.client/get url))

(defn async-get [result url]
  (org.httpkit.client/get url #(go (>! result %))))

(defn get-blog-entries [data]
  (letfn [(zip-str [s]
            (zip/xml-zip (xml/parse (java.io.ByteArrayInputStream. (.getBytes s)))))
          (get-data [c]
            (let [get-content (fn [tag] (->> c (filter #(= tag (:tag %))) first :content first))]
              {:id (get-content :id)
               :title (get-content :title)
               :body (get-content :content)}))]
    (->> data
         :body
         zip-str
         zip/children
         (filter #(= :entry (:tag %)))
         (map :content)
         (map get-data))))
