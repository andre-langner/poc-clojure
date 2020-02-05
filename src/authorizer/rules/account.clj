(ns authorizer.rules.account
  (:require [authorizer.rules.common :as common])
  (:gen-class))

(defn- account-already-initialized?
  "Verify if the account was initialized"
  [account]
  (if (:initialized account) "account-already-initialized"))

(defn- card-not-active?
  "Verify if the card is active"
  [account]
  (if (not (:activeCard account)) "card-not-active"))

(defn- insufficient-limit?
  "Verify if the card have limit available "
  [account amount]
  (if (:initialized account)
    (if (> amount (:availableLimit account)) "insufficient-limit")))

(defn creation?
  "Validate rules for creation of an account"
  [account]
  (common/make-validations (account-already-initialized? account)))

(defn updateLimit?
  "Validate rules for update available limit from account"
  [account amount]
  (common/make-validations (card-not-active? account)
                           (insufficient-limit? account amount)))