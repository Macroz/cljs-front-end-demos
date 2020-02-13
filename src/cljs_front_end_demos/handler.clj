(ns cljs-front-end-demos.handler
  (:require [ring.middleware.resource :refer [wrap-resource]]
            [clojure.string :as str]))


(defn- wrap-default-index [next-handler]
  (fn [request]
    (next-handler
     (if (or (str/starts-with? (:uri request) "/css/")
             (str/starts-with? (:uri request) "/js/"))
       request
       (assoc request :uri "/index.html")))))

(def handler
  (-> (fn [_] {:status 404 :body "not found"})
      (wrap-resource "public")
      wrap-default-index))
