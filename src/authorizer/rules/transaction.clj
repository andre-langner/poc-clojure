(ns authorizer.rules.transaction
  (:require [clj-time.coerce :as c]
            [clj-time.core :as t]
            [authorizer.rules.common :as common])
  (:gen-class))

(defn- get-time
  [transaction]
  (c/from-string (:time transaction)))

(defn- verify-time-interval
  "Verify that the transaction occurred between the time of the new transaction at an interval in minutes ago"
  [base-transaction new-transaction interval]
  (let [new-transaction-time (get-time new-transaction)
        base-transaction-time (get-time base-transaction)
        must-verify (>= (c/to-long new-transaction-time) (c/to-long base-transaction-time))]
    (if must-verify (<= (t/in-millis (t/interval base-transaction-time new-transaction-time)) (* (* interval 60) 1000)) false)))

(defn- get-transaction-list-by-interval
  "Get all transactions that occur minutes before the current transaction"
  [transaction-list new-transaction interval]
  (let [filtered-transaction-list (filterv #(eval (verify-time-interval % new-transaction interval)) transaction-list)]
    (if-not (nil? filtered-transaction-list) filtered-transaction-list [])))

(defn- high-frequency-small-interval?
  "There should not be more than 3 transactions on a 2 minute interval: high-frequency-small-interval"
  [transaction-list-between-interval]
  (if (>= (count transaction-list-between-interval) 2) (str "high-frequency-small-interval")))

(defn- doubled-transaction?
  "There should not be more than 2 similar transactions (same amount and merchant) in a 2 minutes interval: doubled-transaction"
  [transaction-list-between-interval new-transaction]
  (if
    (>= (count
          (filterv #(and (common/same-value? % new-transaction :amount)
                         (common/same-value? % new-transaction :merchant))
                   transaction-list-between-interval))
        1) "doubled-transaction"))

(defn authorization?
  "Do all validations for a transaction authorization"
  [transaction-list new-transaction]

  (let [transaction-list-between-two-min (get-transaction-list-by-interval transaction-list new-transaction 2)]
    (common/make-validations (high-frequency-small-interval? transaction-list-between-two-min)
                             (doubled-transaction? transaction-list-between-two-min new-transaction))))