(ns authorizer.rules.common
  (:gen-class))

(defn make-validations
  [& args]
  (->>
    (reduce conj [] args)
    (remove nil?)
    (into [])))

(defn same-value?
  [map1 map2 key]
  (= (get map1 key) (get map2 key)))