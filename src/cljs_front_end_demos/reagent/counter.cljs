(ns cljs-front-end-demos.reagent.counter
  (:require [reagent.core :as reagent]
            [clojure.string :as str]))

(defonce state-counter (reagent/atom nil))

(defn view-title [& texts]
  (into [:h1] texts))

(defn view-counter [count]
  [view-title "Counter " [:div.inline-block.w5.fg-gray count]])

(defn subscribe-counter-value []
  (get-in @state-counter [:counter :value] 0))

(defn action-increase-counter []
  (swap! state-counter
         (fn [state-counter]
           (update-in state-counter [:counter :value] inc))))

(defn view-center [& children]
  (into [:div.w100.h100.flex-row.align-center.justify-center]
        children))

(defn app-counter []
  (let [counter-value (subscribe-counter-value)
        on-click (fn [_event] (action-increase-counter))]
    [view-center
     [:div.m2.flex-row
      [view-counter counter-value]
      [:button.btn.btn-primary {:on-click on-click} "Click"]]]))
