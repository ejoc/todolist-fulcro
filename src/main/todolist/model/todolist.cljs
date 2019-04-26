(ns todolist.model.todolist
  (:require
    [fulcro.incubator.mutation-interface :as mi :refer [declare-mutation]]
    [fulcro.client.mutations :as m :refer [defmutation]]
    [fulcro.ui.form-state :as fs]))

(declare-mutation submit-item `submit-item)
(defmutation submit-item [{:keys [id delta]}]
  (action [{:keys [state]}]
    (swap! state fs/entity->pristine* [:item/by-id id])
    (swap! state update :todolist conj [:item/by-id id]))
  (remote [env] true))

(declare-mutation complete-item `complete-item)
(defmutation complete-item [{:keys [id]}]
  (action [{:keys [state]}]
    (swap! state assoc-in [:item/by-id id :item/completed] true))
  (remote [env] true))

(defn sort-todolist-by*
  [state-map field]
  (let [todolist-idents  (get-in state-map [:todolist] [])
        todolist        (map (fn [item-ident] (get-in state-map item-ident)) todolist-idents)
        sorted-todolist (sort-by field todolist)
        new-idents     (mapv (fn [item] [:item/by-id (:db/id item)]) sorted-todolist)]
    (assoc-in state-map [:todolist] new-idents)))

(defmutation sort-todolist [no-params]
  (action [{:keys [state]}]
    (swap! state sort-todolist-by* :db/id)))

