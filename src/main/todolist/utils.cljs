(ns todolist.utils
  (:require
    [fulcro.client.dom :as dom :refer [input]]
    [fulcro.client.primitives :as prim]
    [fulcro.ui.form-state :as fs]
    [fulcro.client.mutations :as m]))

(def integer-fields #{::person-age})

(defn render-field [component field renderer]
  (let [form         (prim/props component)
        entity-ident (prim/get-ident component form)
        id           (str (first entity-ident) "-" (second entity-ident))
        is-dirty?    (fs/dirty? form field)
        clean?       (not is-dirty?)
        validity     (fs/get-spec-validity form field)
        is-invalid?  (= :invalid validity)
        value        (get form field "")]
    (renderer {:ident entity-ident
               :value value
               :id    id})))

(defn input-with-label
  "A non-library helper function, written by you to help lay out your form."
  ([component field field-label validation-string input-element]
   (render-field component field
     (fn [{:keys [invalid? id dirty?]}]
       (input {:id              id
               :input-generator input-element} field-label))))
  ([component field field-label validation-string]
   (render-field component field
     (fn [{:keys [invalid? id dirty? value invalid ident]}]
       (input {:value    value
               :id       id
               :error    (when invalid? validation-string)
               :warning  (when dirty? "(unsaved)")
               :onChange (if (integer-fields field)
                               #(m/set-integer! component field :event %)
                               #(m/set-string! component field :event %))})))))
