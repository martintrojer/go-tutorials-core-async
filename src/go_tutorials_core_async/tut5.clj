;;http://tour.golang.org/#68

(ns go-tutorials-core-async.tut5
  (:use [clojure.core.async]))

(defn walk [tree ch]
  (letfn [(walker [t]
            (go
             (when t
               (<! (walker (:left t)))
               (>! ch (:value t))
               (<! (walker (:right t))))))]
    (go
     (<! (walker tree))
     (close! ch))))

(defn same [t1 t2]
  (let [ch1 (chan)
        ch2 (chan)
        drain #(loop [v (<!! %) res []]
                 (if v (recur (<!! %) (conj res v)) res))]

    (walk t1 ch1)
    (walk t2 ch2)

    (= (drain ch1) (drain ch2))))

(defrecord Tree [left value right])

(def t1 (Tree.
         (Tree.
          (Tree. nil 1 nil)
          1
          (Tree. nil 2 nil))
         3
         (Tree.
          (Tree. nil 5 nil)
          8
          (Tree. nil 13 nil))))

(def t2 (Tree.
         (Tree.
          (Tree.
           (Tree. nil 1 nil)
           1
           (Tree. nil 2 nil))
          3
          (Tree. nil 5 nil))
         8
         (Tree. nil 13 nil)))


(same t1 t2)
