(ns go-tutorials-core-async.http
  (:use [clojure.core.async])
  (:require [clj-http.client]
            [org.httpkit.client]))

(defn blocking-get [url]
  (clj-http.client/get url))

(defn async-get [url result]
  (org.httpkit.client/get url #(go (>! result %))))
