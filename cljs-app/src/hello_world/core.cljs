(ns hello-world.core
  (:require
   [clojure.core.async :refer [<! go]]
   [clojure.core.async.interop :refer-macros [<p!]]
   [goog.dom :as gdom]
   [mustache-rs]
   [reagent.dom :as dom]))

(defn set-context [ctx]
  (go (.set-context (<p! mustache-rs) ctx)))

(defn template
  [str]
  (go (.new (.-Mustache (<p! mustache-rs)) str)))

(defn test-mustache
  []
  (go (let [tmpls [(<! (template "Hello {{ greet.who }}"))
                   (<! (template "Hello {{ greet.whoElse }}"))]]
        (<! (set-context #js {:greet #js {:who "The Who", :whoElse "Elsie"}}))
        (prn :deps (set (mapcat #(.deps % #js ["greet"]) tmpls)))
        (prn (mapv #(.render %) tmpls)))))

(defn simple-component []
  [:div
   [:p "CLJS + Wasm Hello World"]
   [:button {:on-click #(test-mustache)} "Mustache"]])

(dom/render [simple-component] (gdom/getElement "app"))
