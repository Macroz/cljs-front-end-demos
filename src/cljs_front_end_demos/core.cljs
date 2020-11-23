(ns cljs-front-end-demos.core
  (:require [reagent.core :as reagent]
            [cljs-front-end-demos.reagent.counter]
            [cljs-front-end-demos.reagent.counter-hooks]
            [cljs-front-end-demos.reagent.temperature]
            [cljs-front-end-demos.reagent.flight-booker]
            [cljs-front-end-demos.reagent.rolls]
            [cljs-front-end-demos.reagent.todo]
            [cljs-front-end-demos.re-frame.todo]))

(enable-console-print!)

(defn on-js-reload [])

(defonce state-navigation
  (let [state (reagent/atom (.. js/window -location -pathname))]
    (.addEventListener js/window
                       "popstate"
                       (fn [event]
                         (reset! state (js->clj (or (.-state event) "/")))))
    state))

(defn action-navigate-to [path name]
  (reset! state-navigation path)
  (.pushState (.-history js/window) (clj->js @state-navigation) name path))

(defn container-link [path name & [class]]
  [:div.mh1.mb05.text-overflow
   [:a.text-uppercase
    {:href ""
     :class [class (when (= path @state-navigation) "selected")]
     :on-click (fn [event]
                 (.preventDefault event)
                 (action-navigate-to path name))}
    name]])

(defn view-navigation []
  [:div.flex-row.flex-wrap.p1
   [container-link "/" "Home"]
   [container-link "/reagent/counter" "Counter"]
   [container-link "/reagent/counter-hooks" "Counter Hooks" :fg-lightgray]
   [container-link "/reagent/temperature" "Temperature"]
   [container-link "/reagent/flight-booker" "Flight Booker" :fg-lightgray]
   [container-link "/reagent/rolls" "Rolls" :fg-lightgray]
   [container-link "/reagent/todo" "Reagent To-do App"]
   [container-link "/re-frame/todo" "re-frame To-do App"]])

(defn view-center [& children]
  (into [:div.w100.h100.flex-row.align-center.justify-center]
        children))

(defn panel-home []
  [:div.w100.p2
   [view-center
    [:div.mt2.overflow-hidden
     [:h1.mb1 "ClojureScript Front-end Demos"]

     [:p "Here are self-contained ClojureScript front-end demos including:"]
     [:ul
      [:li "Reagent and re-frame for state handling,"]
      [:li "styling using CSS, reusable components and utility classes,"]
      [:li "useful greppable naming conventions and"]
      [:li "browser location and history management."]]

     [:p "Future may include:"]
     [:ul
      [:li "iteration on the patterns,"]
      [:li "documentation practices,"]
      [:li "component guide,"]
      [:li "form validation,"]
      [:li "backend integration,"]
      [:li "testing on different levels,"]
      [:li "progressive loading of content,"]
      [:li "offline support,"]
      [:li "distributed data handling,"]
      [:li "responsive design and"]
      [:li "SSR, PWA, A11Y, L18N…"]]

     [:p "Contact: " [:a {:href "http://markku.rontu.net/"} "Markku Rontu"]]]]])

(defn container-root []
  [:<>
   [view-navigation]
   (case @state-navigation
     "/reagent/counter" [cljs-front-end-demos.reagent.counter/app-counter]
     "/reagent/counter-hooks" [cljs-front-end-demos.reagent.counter-hooks/app-counter]
     "/reagent/temperature" [cljs-front-end-demos.reagent.temperature/app-temperature]
     "/reagent/flight-booker" [cljs-front-end-demos.reagent.flight-booker/app-flight-booker]
     "/reagent/rolls" [cljs-front-end-demos.reagent.rolls/app-rolls]
     "/reagent/todo" [cljs-front-end-demos.reagent.todo/app-todo]
     "/re-frame/todo" [cljs-front-end-demos.re-frame.todo/app-todo]
     "/" [panel-home])])

(reagent/render-component [container-root]
                          (.getElementById js/document "app"))
