(ns sre.execution.executor
  (:require [clojure.zip :as z]
            [sre.execution.dsl :refer :all])
  (:import (clojure.lang LazySeq PersistentHashMap PersistentList))
  (:import (java.util Vector)))

(defrecord SearchTreeNode [^PersistentHashMap var-lkp ^PersistentList tasks])

(defn search-tree-zipper [^SearchTreeNode root env]
  (z/zipper (fn [^SearchTreeNode node] (some? (:tasks node)))
            (fn [^SearchTreeNode node]
              (let [[first & rest] (:tasks node)
                    var-lkps (call-task env first (:var-lkp node))
                    children (map #(->SearchTreeNode %1 rest) var-lkps)]
                ; very unintuitive, but an empty children list will result in
                ; a single child having the node `nil`, and this will happen
                ; when executing an unsatisfiable operation
                children))
            (fn [^SearchTreeNode _ ^LazySeq c] c)
            root))

(defn execute [^PersistentList search-plan var-lkp vars-to-return env]
  (let [^PersistentList execution-plan (map #(create-task env %1)
                                            search-plan)
        ^SearchTreeNode root (->SearchTreeNode var-lkp execution-plan)
        search-zipper (search-tree-zipper root env)
        indexed-vars (map-indexed vector vars-to-return)
        tuple-size (count vars-to-return)]

    (if (< tuple-size 1)
      ; corner case
      (Vector.)
      ; depth-first
      (loop [direction :down
             zipper search-zipper
             ^Vector results (Vector.)]
        (case direction
          :down (if (-> zipper z/node :tasks empty?)
                  ; we have a full match so append row to results.
                  ; side-effects below. cover your eyes.
                  (let [^SearchTreeNode node (-> zipper z/node)
                        a (make-array Object tuple-size)]
                    (doseq [[i var] indexed-vars]
                      (aset a i (get-in node [:var-lkp var])))
                    (.addElement results a)
                    (recur :right zipper results))
                  ; partial match
                  (if (-> zipper z/down z/node nil?)
                    ; bang, partial match with no children -> unsatisfiable
                    (recur :right zipper results)
                    ; go deeper...
                    (recur :down (-> zipper z/down) results)))
          :right (if-let [next (-> zipper z/right)]
                   (recur :down next results)
                   ; branch completed
                   (if-let [next (-> zipper z/up)]
                     (recur :right next results)
                     ; whole search completed, recursion base case reached
                     results)))))))

