(ns authorizer.controller.routes
  (:require [compojure.core :refer :all]
            [clojure.java.io :as io]
            (ring.middleware [multipart-params :as mp])
            [authorizer.handler.request :as request])
  (:gen-class))

(defn- index [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    "Server running"})

(defn- process-file [req]
  {:status  200
   :headers {"Content-Type" "text/plain"}
   :body    (-> req
                (get :multipart-params)
                (get "file")
                (get :tempfile)
                (io/input-stream)
                (slurp)
                (str)
                (request/handle))})

(defroutes app-routes
           (GET "/" [] index)
           (mp/wrap-multipart-params (POST "/" [] process-file)))