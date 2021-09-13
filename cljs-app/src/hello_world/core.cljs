(ns hello-world.core
  (:require
   [clojure.core.async :refer [go]]
   [clojure.core.async.interop :refer-macros [<p!]]
   [goog.dom :as gdom]
   [reagent.dom :as dom]
   [wasm-calc]))

(defn greet [name_]
  (go (.greet (<p! wasm-calc) name_)))

(defn simple-component []
  [:div
   [:p "CLJS + Wasm Hello World"]
   [:button
    {:on-click #(greet "from WebAssembly")}
    "Hi"]])

(dom/render [simple-component] (gdom/getElement "app"))
