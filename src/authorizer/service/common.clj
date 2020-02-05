(ns authorizer.service.common
  (:require [authorizer.repository.account :as account])
  (:gen-class))

(defn return-account
  [violations]
  (let [account (account/get-account)]
    (-> account
        (assoc :violations violations)
        (dissoc :initialized)
        (eval))))

(defn no-violations?
  [violations]
  (= (count violations) 0))
