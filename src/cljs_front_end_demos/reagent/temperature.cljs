(ns cljs-front-end-demos.reagent.temperature
  (:require [reagent.core :as reagent]
            [cljs.tools.reader.edn :as edn]
            [cljs-front-end-demos.util :refer [debounce format]]))

(defn view-center [& children]
  (into [:div.w100.h100.flex-row.align-center.justify-center]
        children))

(defn celcius->fahrenheit [c]
  (+ (* c (/ 9 5)) 32))

(defn celcius->kelvin [c]
  (+ c 273.15))

(defn fahrenheit->celsius [f]
  (* (- f 32) (/ 5 9)))

(defn kelvin->celcius [k]
  (- k 273.15))

(defn convert-temperature [x from-unit to-unit]
  ((case [from-unit to-unit]
     [:c :f] celcius->fahrenheit
     [:c :k] celcius->kelvin
     [:f :c] fahrenheit->celsius
     [:f :k] (comp celcius->kelvin fahrenheit->celsius)
     [:k :c] kelvin->celcius
     [:k :f ](comp celcius->fahrenheit kelvin->celcius))
   x))

(declare app-state)

(defn subscribe-celcius []
  (reagent/cursor app-state [:celcius]))

(defn subscribe-fahrenheit []
  (reagent/cursor app-state [:fahrenheit]))

(defn subscribe-kelvin []
  (reagent/cursor app-state [:kelvin]))

(defn subscribe-history []
  (reagent/cursor app-state [:history]))

(def action-add-history
  (debounce (fn [h]
              (when-not (= (first @(subscribe-history)) h)
                (swap! (subscribe-history) (comp distinct conj) h)))
            1000))

(defn action-change-celcius [c]
  (try
    (reset! (subscribe-celcius) c)
    (let [c (edn/read-string (str c))
          f (celcius->fahrenheit c)
          k (celcius->kelvin c)]
      (reset! (subscribe-fahrenheit) f)
      (reset! (subscribe-kelvin) k)
      (action-add-history [c :c :f :k]))
    (catch js/Error e)))

(defn action-change-fahrenheit [f]
  (try
    (reset! (subscribe-fahrenheit) f)
    (let [f (edn/read-string (str f))
          c (fahrenheit->celsius f)
          k (celcius->kelvin c)]
      (reset! (subscribe-celcius) c)
      (reset! (subscribe-kelvin) k)
      (action-add-history [f :f :c :k]))
    (catch js/Error e)))

(defn action-change-kelvin [k]
  (try
    (reset! (subscribe-kelvin) k)
    (let [k (edn/read-string (str k))
          c (kelvin->celcius k)
          f (celcius->fahrenheit c)]
      (reset! (subscribe-celcius) c)
      (reset! (subscribe-fahrenheit) f)
      (action-add-history [k :k :c :f]))
    (catch js/Error e)))

(defn action-select-history [[from from-unit & to-units]]
  ((case from-unit
     :c action-change-celcius
     :f action-change-fahrenheit
     :k action-change-kelvin) from))

(defonce app-state
  (reagent/atom {:celcius 0
                 :fahrenheit (celcius->fahrenheit 0)
                 :kelvin (celcius->kelvin 0)}))

(defn format-number [x]
  (cond (= x (js/Math.round x)) x
        (= (str x) (format "%.1f" x)) x
        :else (format "%.2f" x)))

(defn view-temperature [x unit]
  (let [s (format-number x)]
    [:span
     (case unit
       :c (str s "°C")
       :f (str s "°F")
       :k (str s "K"))]))

(defn view-temperature-input [{:keys [id name value on-change]}]
  [:div.flex-row.fs150.flex-s1
   [:label.va-b.flex1 {:for id} name]
   [:input.va-b.p0.ml1.flex1.flex-s1.text-overflow
    {:id id
     :size 5
     :value value
     :on-change (fn [event]
                  (.preventDefault event)
                  (on-change (.. event -target -value)))}]])

(defn app-temperature []
  [view-center
   [:div.flex-column.align-start.justify-start
    [:h1.mv2 "Temperature Converter"]
    [view-temperature-input {:id :celcius
                             :name "Celcius"
                             :value @(subscribe-celcius)
                             :on-change action-change-celcius}]
    [view-temperature-input {:id :fahrenheit
                             :name "Fahrenheit"
                             :value @(subscribe-fahrenheit)
                             :on-change action-change-fahrenheit}]
    [view-temperature-input {:id :kelvin
                             :name "Kelvin"
                             :value @(subscribe-kelvin)
                             :on-change action-change-kelvin}]
    [view-center
     (into [:div.mt2]
           (for [[from from-unit & to-units :as h] (take 10 @(subscribe-history))]
             [:div.flex-row.mb05
              (into [:a.cp05.fg-gray.pointer
                     {:on-click (fn [event]
                                  (.preventDefault event)
                                  (action-select-history h))}
                     [view-temperature from from-unit]]
                    (for [to-unit to-units]
                      [view-temperature (convert-temperature from from-unit to-unit) to-unit]))]))]]])
