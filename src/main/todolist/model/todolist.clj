(ns todolist.model.todolist
  (:require
    [com.wsscode.pathom.connect :as pc]
    [todolist.server-components.pathom-wrappers :refer [defmutation defresolver]]
    [taoensso.timbre :as log]
    [fulcro.client.primitives :as prim]
    [todolist.query :refer [find-all-by insert]]))

;; temp
(defonce id (atom 1000))
(defn next-id [] (swap! id inc))

;; (fn [[{:db/keys [id]}]] {:db/id id}) old
(defresolver todolist-resolver
  [{:keys [db/conn]} input]
  {
   ::pc/output [:todolist [:db/id :item/title :item/completed]]}
  (log/info "Todolist resolver")
  (let [items (find-all-by conn :item/title)]
    (log/info "todolist: " items)
    {:todolist (mapv
                (fn [[item]] item)
                items)}))

(defmutation submit-item
  [{:keys [db/conn]} {:keys [id delta]}]
  {::pc/input #{:db/id}
   ::pc/output [:tempids]}
  (log/info "Add item server ???" id delta)
  (let [ids    (map (fn [[k v]] (second k)) delta)
        title  (get-in (into {} (map (fn [[k v]] (first v)) delta)) [:item/title :after])
        item   (insert conn {:item/title title :item/completed false})
        new-id ((comp last first :tempids) item)
        remaps (into {} (keep (fn [v] (when (prim/tempid? v) [v new-id])) ids))]
    (log/info ids)
    (log/info "remaps" remaps)
    {:tempids remaps}))

(defmutation complete-item
  [{:keys [db/conn]} {:keys [id]}]
  {::pc/input #{:db/id}
   ::pc/output [:db/id]}
  (log/info "Complete todo " id)
  (let [item-completed (insert conn {:db/id id :item/completed true})])
  {:db/id id})
