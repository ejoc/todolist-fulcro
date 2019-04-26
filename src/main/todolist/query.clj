(ns todolist.query
  (:require
   [datomic.client.api :as d]))

(defn qe
  [query conn & args]
  (apply d/q query (d/db conn) args))

(defn find-all-by
  "Returns all entities possessing attr."
  [conn attr]
  (qe '[:find (pull ?e [*])
         :in $ ?attr
         :where [?e ?attr]]
       conn attr))

(defn entity
  [conn id]
  (ffirst (qe '[:find (pull ?eid [*])
                :in $ ?eid
                :where [?eid]]
              conn id)))

(defn find-by
  "Returns the unique entity identified by attr and val."
  [db attr val]
  (qe '[:find (pull ?e [*])
        :in $ ?attr ?val
        :where [?e ?attr ?val]]
      db attr val))


(defn insert
  [conn item]
  (d/transact conn {:tx-data [item]}))

;; {:db/id tempid :item/title title :item/completed false}
