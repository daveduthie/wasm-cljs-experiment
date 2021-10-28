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
  (-> mustache-rs
      (.then #(.new (.-Mustache %) str))))

(def mustache-templates
  ["Hello {{ calc.who }}"
   "Hello {{ calc.whoElse }} {{ calc.doesnt.resolve.to.anything }}"])

(defn mustache-test
  []
  (let [*state (r/atom nil)]
    (go (<! (set-context #js {:calc #js {:who "The Who", :whoElse "Elsie"}}))
        (reset! *state
                (map (fn [tpl mustache]
                       {:tpl      tpl
                        :deps     (set (.deps mustache #js ["calc"]))
                        :rendered (.render mustache)})
                     mustache-templates
                     (<p! (js/Promise.all (map template mustache-templates))))))
    (fn []
      [:ul
       (doall (map (fn [{:keys [tpl deps rendered]}]
                     [:li {:key rendered}
                      tpl " => "
                      rendered " (depends on " [:code (pr-str deps)] ")"]) @*state))])))

(defn simple-component []
  [:div
   [:p "CLJS + Wasm Hello World"]
   [mustache-test]])

(dom/render [simple-component] (gdom/getElement "app"))
