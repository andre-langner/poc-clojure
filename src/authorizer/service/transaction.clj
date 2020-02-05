(ns authorizer.service.transaction
  (:require [authorizer.repository.account :as account]
            [authorizer.repository.transaction :as transaction]
            [authorizer.rules.transaction :as transaction-rules]
            [authorizer.rules.account :as account-rules]
            [authorizer.service.common :as common]
            )
  (:gen-class))

(defn authorize
  [new-transaction]
  (let [account (account/get-account)
        transaction-list (transaction/get-transactions)
        violations-account (account-rules/updateLimit? account (:amount new-transaction))
        violations-transaction (transaction-rules/authorization? transaction-list new-transaction)
        all-violations (into [] (concat violations-account violations-transaction))]
    (if (common/no-violations? all-violations)
      (do (transaction/add-transaction new-transaction)
          (account/update-available-limit (- (:availableLimit account) (:amount new-transaction)))))
    (common/return-account all-violations)))