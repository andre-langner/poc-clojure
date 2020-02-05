(ns authorizer.core
  (:require [org.httpkit.server :as server]
            [compojure.core :refer :all]
            [ring.middleware.defaults :refer :all]
            [authorizer.controller.routes :as routes]
            )
  (:gen-class))

(defn -main
  "Main entry point"
  [& args]
  (let [port (Integer/parseInt (or (System/getenv "PORT") "3000"))]
    ; Server with Ring.defaults middleware
    (server/run-server (wrap-defaults #'routes/app-routes api-defaults) {:port port})
    (println (str "Running webserver at http:/127.0.0.1:" port "/"))))
