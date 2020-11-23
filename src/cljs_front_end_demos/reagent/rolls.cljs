(ns cljs-front-end-demos.reagent.rolls
  (:require [medley.core :refer [indexed]]
            [reagent.core :as reagent]
            [cljs-front-end-demos.words :as words]))

(def app-state (reagent/atom {}))

(defn make-item [s]
  {:name s})

(defn view-item [item]
  [:div.text-center.w8.lh2 (:name item)])

(defn view-roll [{:keys [rotation items] :as roll}]
  (let [x 0
        x0 0
        y0 500
        z0 0
        r 300.0
        p 1000.0
        n (count items)]
    [:div.flex1.h1000.overflow-hidden {:style {:position :relative}}
     [:div {:style {:position :absolute
                    :transform-style :preserve-3d
                    :perspective-origin "4rem 0"
                    :perspective (str p "px")
                    :transform (str "translate3d(" x0 "px," y0 "px," z0 "px)")}}
      [:div {:style {:position :absolute
                     :width 1000.0
                     :height 1000.0
                     :background-color "rgba(248,248,248,0.5)"
                     :transform-style :preserve-3d
                     :transform (str "translate3d(0px, -500px, -250px)")}}]
      [:div {:style {:position :absolute
                     :width 1000.0
                     :height 1000.0
                     :background-color "rgba(248,248,248,0.5)"
                     :transform-style :preserve-3d
                     :transform (str "translate3d(0px, -500px, -50px)")}}]
      [:div {:style {:position :absolute
                     :width 1000.0
                     :height 1000.0
                     :background-color "rgba(248,248,248,0.5)"
                     :transform-style :preserve-3d
                     :transform (str "translate3d(0px, -500px, 200px)")}}]
      [:div {:style {:position :absolute
                     :transform-style :preserve-3d
                     :transform-origin :center
                     :transform (str "rotateX(" rotation "deg)")}}
       (for [[i item] (indexed items)
             :let [y (* r (Math/cos (* 2.0 Math/PI (/ i n))))
                   z (* r (Math/sin (* 2.0 Math/PI (/ i n))))]]
         ^{:key (:name item)}
         [:div
          {:style {:position :absolute
                   ;;:background-color "rgba(248,248,248,0.6)"
                   :transform-style :preserve-3d
                   :transform (str "translate3d(" x "px," y "px," z "px) rotateX(" (- rotation) "deg)")}}
          [view-item item]])]]]))

(defn app-rolls []
  [:div.m2.flex-row.justify-start.align-start
   (for [[i roll] (indexed (:rolls @app-state))]
     ^{:key i}
     [view-roll roll])])

#_(.addEventListener js/window "wheel"
                     (fn [event]
                       (.preventDefault event)
                       (.stopPropagation event)
                       (swap! app-state
                            update
                            :rotation + (* (.-deltaY event) 0.1))))

(defn rotation []
  (swap! app-state update :rolls (fn [rolls]
                                   (map-indexed #(update %2 :rotation (if (odd? %1) + -) (+ 0.2 (* 0.2 %1))) rolls))))

(defn rotation-loop []
  (rotation)
  (.requestAnimationFrame js/window rotation-loop))

(defonce rotation-looper (rotation-loop))

(def init
  (reset! app-state
          {:rolls
           (repeatedly 3
                       (fn []
                         {:rotation 0
                          :items (map make-item
                                      (take (+ 20 (rand-int 50)) (shuffle words/words)))}))}))
