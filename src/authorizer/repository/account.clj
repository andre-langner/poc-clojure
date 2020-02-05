(ns authorizer.repository.account
  (:gen-class))

(def base-account {:initialized false :activeCard false :availableLimit 0})
(def account (atom base-account))

(defn initialize-account
  [active-card available-limit]
  (swap! account assoc :initialized true :activeCard (boolean active-card) :availableLimit (int available-limit)))

(defn get-account
  []
  (eval @account))

(defn update-available-limit
  [value]
  (swap! account assoc :availableLimit value))
