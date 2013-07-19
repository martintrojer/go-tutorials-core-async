(ns go-tutorials-core-async.http
  (:require [clj-http.client :as client]))

;; use (non-blocking) http-get -- is there a callback option?

(defn blocking-get [url]
  (clj-http.client/get url
                       ))
