(ns go-tutorials-core-async.http
  (:use [clojure.core.async])
  (:require [clj-http.client]
            [org.httpkit.client]
            [clojure.xml :as xml]
            [clojure.zip :as zip]))

(defn blocking-get [url]
  (clj-http.client/get url))

(defn async-get [url result]
  (org.httpkit.client/get url #(go (>! result %))))

(defn get-blog-entries [f url]
  (letfn [(zip-str [s]
            (zip/xml-zip (xml/parse (java.io.ByteArrayInputStream. (.getBytes s)))))
          (get-data [c]
            (let [get-content (fn [tag] (->> c (filter #(= tag (:tag %))) first :content first))]
              {:id (get-content :id)
               :title (get-content :title)
               :body (get-content :content)}))]
    (->> url
         f
         :body
         zip-str
         zip/children
         (filter #(= :entry (:tag %)))
         (map :content)
         (map get-data))))
