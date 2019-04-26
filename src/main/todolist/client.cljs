(ns todolist.client
  (:require [fulcro.client :as fc]
            [todolist.ui.root :as root]
            [todolist.ui.form :as f]
            [todolist.model.todolist :as tl]
            [fulcro.client.network :as net]
            [fulcro.client.data-fetch :as df]))

(defonce SPA (atom nil))

(defn mount []
  (reset! SPA (fc/mount @SPA root/Root "app")))

(defn start []
  (mount))

(def secured-request-middleware
  ;; The CSRF token is embedded via server_components/html.clj
  (->
    (net/wrap-csrf-token (or js/fulcro_network_csrf_token "TOKEN-NOT-IN-HTML!"))
    (net/wrap-fulcro-request)))

(defn ^:export init []
  (reset! SPA (fc/make-fulcro-client
                {:client-did-mount (fn [app]
                                     (df/load app :todolist root/ListItem {:post-mutation `tl/sort-todolist}))
                 ;; This ensures your client can talk to a CSRF-protected server.
                 ;; See middleware.clj to see how the token is embedded into the HTML
                 :networking       {:remote (net/fulcro-http-remote
                                              {:url                "/api"
                                               :request-middleware secured-request-middleware})}}))
  (start))
