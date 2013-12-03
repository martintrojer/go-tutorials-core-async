(ns logger)

(def ^:private log-agent (agent 0))

(defn log [& strs]
  (send log-agent (fn [ctr] (println (apply str (interpose " " strs))) (inc ctr))))
