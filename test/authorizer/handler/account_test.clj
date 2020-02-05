(ns authorizer.handler.account_test
  (:require [clojure.test :refer :all]
            [authorizer.handler.request :as request]))

(deftest account-initialization
  (testing "Test account initialization"
    (let [result (request/operation-factory {:account {:activeCard true :availableLimit 1000}})
          expected {:activeCard true :availableLimit 1000 :violations []}]
      (is (= result expected))))

  (testing "Test account initialization with violation - account-already-initialized"
    (let [result (request/operation-factory {:account {:activeCard true :availableLimit 1000}})
          expected {:activeCard true :availableLimit 1000 :violations ["account-already-initialized"]}]
      (is (= result expected))))
  )