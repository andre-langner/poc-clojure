(ns authorizer.handler.transaction_test
  (:require [clojure.test :refer :all]
            [authorizer.handler.account_test :as account-test]
            [authorizer.handler.request :as request]))

(deftest transaction-authorization
  (testing "Test transaction without initialized account"
    (let [result (request/operation-factory {:transaction {:merchant "Burger King" :amount 20 :time "2019-02-13T10:00:00.000Z"}})
          expected {:activeCard false :availableLimit 0 :violations ["card-not-active"]}]
      (is (= result expected))))

  (account-test/account-initialization)

  (testing "Test transaction successful"
    (let [result (request/operation-factory {:transaction {:merchant "Burger King" :amount 20 :time "2019-02-13T10:00:00.000Z"}})
          expected {:activeCard true :availableLimit 980 :violations []}]
      (is (= result expected))))

  (testing "Test transaction violation - double-transaction"
    (let [result (request/operation-factory {:transaction {:merchant "Burger King" :amount 20 :time "2019-02-13T10:02:00.000Z"}})
          expected {:activeCard true :availableLimit 980 :violations ["doubled-transaction"]}]
      (is (= result expected))))

  (testing "Test transaction successful - Same transaction after 2 min"
    (let [result (request/operation-factory {:transaction {:merchant "Burger King" :amount 20 :time "2019-02-13T10:02:01.000Z"}})
          expected {:activeCard true :availableLimit 960 :violations []}]
      (is (= result expected))))

  (testing "Test transaction violation - high-frequency-small-interval"
    (let [result_1 (request/operation-factory {:transaction {:merchant "Submarino" :amount 20 :time "2019-02-13T11:03:00.000Z"}})
          expected_1 {:activeCard true :availableLimit 940 :violations []}
          result_2 (request/operation-factory {:transaction {:merchant "Americanas" :amount 20 :time "2019-02-13T11:04:00.000Z"}})
          expected_2 {:activeCard true :availableLimit 920 :violations []}
          result_3 (request/operation-factory {:transaction {:merchant "Kabum" :amount 20 :time "2019-02-13T11:05:00.000Z"}})
          expected_3 {:activeCard true :availableLimit 920 :violations ["high-frequency-small-interval"]}
          result_4 (request/operation-factory {:transaction {:merchant "Kabum" :amount 20 :time "2019-02-13T11:05:01.000Z"}})
          expected_4 {:activeCard true :availableLimit 900 :violations []}]
      (is (and (= result_1 expected_1) (= result_2 expected_2) (= result_3 expected_3) (= result_4 expected_4)))))

  (testing "Test transaction violation - insuficient-limit"
    (let [result (request/operation-factory {:transaction {:merchant "Shoptime" :amount 901 :time "2019-02-13T12:00:00.000Z"}})
          expected {:activeCard true :availableLimit 900 :violations ["insufficient-limit"]}]
      (is (= result expected))))

  (testing "Test transaction successful after 2 min"
    (let [result (request/operation-factory {:transaction {:merchant "Shoptime" :amount 900 :time "2019-02-13T12:00:00.000Z"}})
          expected {:activeCard true :availableLimit 0 :violations []}]
      (is (= result expected))))
  )
