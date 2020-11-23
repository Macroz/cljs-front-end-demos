(ns cljs-front-end-demos.reagent.counter-hooks
  (:require [reagent.core :as reagent]))

(def hooks (reagent/atom nil))
(def hook-counts (atom {}))
(def rendered-hooks (atom #{}))

(defn use-hook [initial-value]
  (let [seqno (get @hook-counts (reagent/current-component) 0)
        path [(reagent/current-component) seqno]
        value (get-in @hooks path)]
    (swap! hook-counts assoc (reagent/current-component) (inc seqno))
    (swap! rendered-hooks conj path)
    (prn :rendering path)
    (when-not value
      (swap! hooks assoc-in path initial-value))
    (reagent/cursor hooks path)))

(defn use-state [initial-value]
  (let [state (use-hook initial-value)]
    [state
     #(reset! state %)]))

(defn use-counter [initial-value]
  (let [[state set-state!] (use-state initial-value)]
    [state
     #(set-state! (inc @state))]))

(defn view-title [& texts]
  (into [:h1] texts))

(defn view-counter [count]
  [view-title "Counter " [:div.inline-block.w5.fg-gray count]])

(defn view-center [& children]
  (into [:div.w100.h100.flex-row.align-center.justify-center]
        children))

(defn container-counter [i remove-counter!]
  (let [[counter-value action-increase-counter] (use-counter i)
        [counter-value2 action-increase-counter2] (use-counter i)]
    [:div.m2.flex-row
     (pr-str i)
     [view-counter @counter-value]
     [:button.btn.btn-primary {:on-click action-increase-counter} "Click"]
     [:button.btn.btn-secondary.ml1 {:on-click remove-counter!} "✖"]]))

(defn app-counter []
  (reset! rendered-hooks #{})
  (reset! hook-counts {})
  #_(reagent/after-render (fn []
                            (let [unrendered-hooks (remove @rendered-hooks
                                                           (keys @hooks))]
                              (swap! hooks #(apply dissoc % unrendered-hooks)))))
  (let [[counters set-counters!] (use-state (range 3))
        remove-counter! (fn [i] (set-counters! (remove #{i} @counters)))]
    [view-center
     [:div.m2.flex-col
      ;; [:div (pr-str @hooks)]
      ;; [:div (count @hooks)]
      ;; [:div (pr-str @rendered-hooks)]
      (doall (for [i @counters]
               ^{:key i}
               [container-counter i (partial remove-counter! i)]))]]))
