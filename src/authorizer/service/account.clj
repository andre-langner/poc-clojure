(ns authorizer.service.account
  (:require [authorizer.repository.account :as account]
            [authorizer.rules.account :as rules]
            [authorizer.service.common :as common])
  (:gen-class))

(defn create
  [account-info]
  (let [account (account/get-account)
        violations (rules/creation? account)]
    (if (common/no-violations? violations)
      (account/initialize-account (:activeCard account-info) (:availableLimit account-info)))
    (common/return-account violations)))