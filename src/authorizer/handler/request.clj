(ns authorizer.handler.request
  (:require [clojure.string :as string]
            [cheshire.core :refer :all]
            [clojure.data.json :as data-json]
            [authorizer.service.account :as account]
            [authorizer.service.transaction :as transaction])
  (:gen-class))

(defn operation-factory
  [operation]
  (case (ffirst operation)
    :account (account/create (:account operation))
    :transaction (transaction/authorize (:transaction operation))
    {:violations ["operation-not-expected"]}))

(defn- exec-operation
  [json]
  (let [operation (parse-string json true)]
    (-> operation
        (operation-factory)
        (data-json/write-str)
        (str \newline))))


(defn handle
  [body]
  (->> body
       (string/split-lines)
       (map exec-operation)
       ))
