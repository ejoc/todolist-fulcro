(ns todolist.ui.root
  (:require
    [fulcro.client.dom :as dom :refer [div button p h3]]
    [fulcro.client.primitives :as prim :refer [defsc]]
    [todolist.model.todolist :as tl]
    [todolist.ui.form :as form]
    [taoensso.timbre :as log]))

(defsc ListItem [this {:keys [db/id item/title item/completed]}]
  {:query [:db/id :item/title :item/completed]
   :ident [:item/by-id :db/id]
   :initial-state (fn [{:keys [id title completed]}] {:db/id id :item/title title :item/completed completed} )
   }
  (div :.item
   (div :.right.floated.content
     (button :.ui.icon.mini.circular.button {:onClick #(prim/transact! this `[(tl/complete-item {:id ~id})])} (dom/i :.check.icon)))
   (div :.content
     (let [text-style (if completed "line-through" "none")]
       (dom/span {:style {:textDecoration text-style}} title)))))

(def ui-listitem (prim/factory ListItem {:keyfn :db/id}))

(defsc Root [this {:keys [root/item todolist]}]
  {:query         [{:todolist (prim/get-query ListItem)}
                   {:root/item (prim/get-query form/Form)}]
   :initial-state {:todolist []}
   :componentDidMount (fn [] (prim/transact! this `[(form/edit-new-item {})]))}
  (div :.ui.segments
    (div :.ui.top.attached.segment
      (h3 :.ui.header
        "TodoList"))
    (div :.ui.attached.segment
      (div :.content
        (div {:style {:margin "0 auto" :maxWidth "600px"}}
          (when (:item/title item)
            (form/ui-form item))
          (div :.ui.middle.aligned.divided.list
            (map ui-listitem todolist)))))))
