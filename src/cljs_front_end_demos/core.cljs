(ns cljs-front-end-demos.core
  (:require [reagent.core :as reagent]
            [cljs-front-end-demos.reagent.counter]
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

(defn container-link [path name]
  [:div.mh1
   [:a.text-uppercase
    {:href ""
     :class (when (= path @state-navigation) "selected")
     :on-click (fn [event]
                 (.preventDefault event)
                 (action-navigate-to path name))}
    name]])

(defn view-navigation []
  [:div.w100.min-narrow.flex-row.justify-around.p1
   [container-link "/" "Home"]
   [container-link "/reagent/counter" "Counter"]
   [container-link "/reagent/todo" "Reagent To-do App"]
   [container-link "/re-frame/todo" "re-frame To-do App"]])

(defn view-center [& children]
  (into [:div.w100.h100.flex-row.align-center.justify-center]
        children))

(defn panel-home []
  [view-center
   [:div.w100.min-narrow.p2.mt4
    [:h1.mb1 "ClojureScript Front-end Demos"]
    [:p "Here are self-contained ClojureScript front-end demos including:"
     [:ul
      [:li "Reagent and re-frame for state handling,"]
      [:li "styling using CSS, reusable components and utility classes,"]
      [:li "useful greppable naming conventions and"]
      [:li "browser location and history management."]]]

    [:p "Future may include:"
     [:ul
      [:li "iteration on the patterns,"]
      [:li "documentation practices,"]
      [:li "component guide,"]
      [:li "backend integration,"]
      [:li "testing on different levels,"]
      [:li "progressive loading of content,"]
      [:li "offline support,"]
      [:li "distributed data handling,"]
      [:li "responsive design and"]
      [:li "SSR, PWA, A11Y, L18N…"]]]

    [:p "Contact " [:a {:href "http://markku.rontu.net/"} "Markku Rontu"]]]])

(defn container-root []
  [:div.w100
   [view-navigation]
   (case @state-navigation
     "/reagent/counter" [cljs-front-end-demos.reagent.counter/app-counter]
     "/reagent/todo" [cljs-front-end-demos.reagent.todo/app-todo]
     "/re-frame/todo" [cljs-front-end-demos.re-frame.todo/app-todo]
     "/" [panel-home])])

(reagent/render-component [container-root]
                          (.getElementById js/document "app"))
