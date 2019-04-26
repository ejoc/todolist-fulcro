(ns todolist.ui.form
  (:require
    [fulcro.client.dom :as dom :refer [div ul li p h3 input]]
    [fulcro.client.primitives :as prim :refer [defsc]]
    [fulcro.client.mutations :as m :refer [defmutation]]
    [fulcro.ui.form-state :as fs]
    [cljs.spec.alpha :as s]
    [clojure.string :as str]
    [todolist.model.todolist :as tl]
    [todolist.utils :as utils]))

(s/def ::title (s/and string? #(seq (str/trim %))))

(defn add-item*
  [state-map id title]
  (let [item-ident [:item/by-id id]
        item {:db/id id :item/title title}]
    (assoc-in state-map item-ident item)))

(defsc Form [this {:keys [:db/id] :as props}]
  {:query [:db/id :item/title fs/form-config-join]
   :ident [:item/by-id :db/id]
   :form-fields #{:item/title}}
  (dom/div
   (dom/div :.ui.input
     (utils/input-with-label this :item/title "Title: " "Title is required."))
   (dom/button :.ui.icon.button {;; :disabled (or (not (fs/checked? props)) (fs/invalid-spec? props))
                                 :onClick #(prim/transact! this `[(tl/submit-item {:id ~id :delta ~(fs/dirty-fields props true)})
                                                                  (edit-new-item {})])} (dom/i :.plus.icon))))
;; (input-with-label this name "Name:" "")

(defmutation edit-new-item [_]
  (action [{:keys [state]}]
    (let [item-id (prim/tempid)
          item-ident [:item/by-id item-id]]
      (swap! state
        (fn [s] (-> s
                    (add-item* item-id "")
                    (assoc :root/item item-ident)
                    (fs/add-form-config* Form [:item/by-id item-id])))))))

(def ui-form (prim/factory Form {:keyfn :db/id}))
