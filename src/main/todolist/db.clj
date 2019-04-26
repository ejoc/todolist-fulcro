(ns todolist.db
  (:require
   [todolist.server-components.config :refer [config]]
   [mount.core :refer [defstate]]
   [datomic.client.api :as d]
   [taoensso.timbre :as log]))

;; (set! *warn-on-reflection* false)

(def cfg {:server-type :peer-server
          :access-key "myaccesskey"
          :secret "mysecret"
          :endpoint "localhost:8998"})

;; (defstate client :start (d/client cfg) :stop nil)

;; (def conn (delay (d/connect (d/client cfg) {:db-name "hello"})))

(defn start-conn [config]
  (log/info config)
  (d/connect (d/client cfg) {:db-name "hello"}))

;; (defstate conn :start (start-conn config))

(defstate conn :start (d/connect (d/client cfg) {:db-name "hello"}))

;; (defstate db :start (d/db conn) :stop nil)

(def todolist-schema [{:db/ident :item/title
                       :db/valueType :db.type/string
                       :db/cardinality :db.cardinality/one
                       :db/doc "The title of the todolist item"}

                      {:db/ident :item/completed
                       :db/valueType :db.type/boolean
                       :db/cardinality :db.cardinality/one
                       :db/doc "Completed of the todolist item"}])

;; (d/transact conn {:tx-data [{:item/title title :item/completed false}]})
