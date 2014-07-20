(ns repl
  (:require
   [clojure.java.io :as io]

   [cemerick.piggieback]
   [weasel.repl.websocket]

   [org.httpkit.server]
   [compojure.core :refer [GET defroutes]]
   [compojure.route :as route]
   [compojure.handler :as handler]))


(defroutes router
  (GET "/" [] (io/resource "public/index.html"))
  (route/resources "/")
  (route/not-found "Page not found"))


(def app
  (-> (handler/site router)))


(defn run! []
  (defonce ^:private server
    (org.httpkit.server/run-server #'app {:port 8888 :join? false}))
  server)


(defn cljs! []
  (cemerick.piggieback/cljs-repl
   :repl-env (weasel.repl.websocket/repl-env
              :ip "0.0.0.0" :port 8889)))
