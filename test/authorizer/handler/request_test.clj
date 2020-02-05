(ns authorizer.handler.request_test
  (:require [clojure.test :refer :all]
            [authorizer.handler.request :as request]
            [authorizer.handler.transaction_test :as transaction-test]))

(deftest handle-operations

  (transaction-test/transaction-authorization)

  (testing "Test invalid request"
    (let [result (request/operation-factory {:deleteTransaction {:merchant "Shoptime" :amount 900 :time "2019-02-13T12:00:00.000Z"}})
          expected {:violations ["operation-not-expected"]}]
      (is (= result expected))))
  )
