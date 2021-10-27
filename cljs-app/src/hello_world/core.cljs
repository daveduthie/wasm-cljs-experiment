(ns hello-world.core
  (:require
   [clojure.core.async :refer [<! go]]
   [reagent.core :as r]
   [clojure.core.async.interop :refer-macros [<p!]]
   [goog.dom :as gdom]
   ["mustache-rs/mustache_rs" :as mustache-rs]
   [reagent.dom :as dom]))

(defn set-context [ctx]
  (go (.set-context (<p! mustache-rs) ctx)))

(defn template
  [str]
  (go (.new (.-Mustache (<p! mustache-rs)) str)))

(defn mustache-test
  []
  (let [*state (r/atom nil)]
    (go (let [tmpls [(<! (template "Hello {{ calc.who }}"))
                     (<! (template "Hello {{ calc.whoElse }} {{ calc.doesnt.resolve.to.anything }}"))]]
          (<! (set-context #js {:calc #js {:who "The Who", :whoElse "Elsie"}}))
          (reset! *state
                  (map (fn [tmpl]
                         {:deps     (set (.deps tmpl #js ["calc"]))
                          :rendered (.render tmpl)})
                       tmpls))))
    (fn []
      [:ul
       (doall (map (fn [{:keys [deps rendered]}]
                     [:li {:key rendered} rendered " "
                      [:code (pr-str deps)]]) @*state))])))

(defn simple-component []
  [:div
   [:p "CLJS + Wasm Hello World"]
   [mustache-test]])

(dom/render [simple-component] (gdom/getElement "app"))
