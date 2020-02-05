(ns authorizer.repository.transaction
  (:gen-class))

(def transaction-collection (atom []))

(defn add-transaction [transaction]
  (let [merchant (str (get transaction :merchant))
        amount (int (get transaction :amount))
        time (str (get transaction :time))]
    (swap! transaction-collection conj {:merchant merchant
                                        :amount   amount
                                        :time     time})))

(defn get-transactions []
  (sort-by :time @transaction-collection))
