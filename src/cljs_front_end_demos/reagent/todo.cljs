(ns cljs-front-end-demos.reagent.todo
  (:require [medley.core :refer [index-by]]
            [reagent.core :as reagent]
            [cljs-front-end-demos.util :refer [mapck]]
            [cljs-front-end-demos.words :as words]
            [clojure.string :as str]))

;;; Model, pure functions

(defn make-todo [text]
  {:id (random-uuid)
   :text text})

(defn get-todo-by-id [state id]
  (get-in state [::todo-by-id id]))

(defn get-todo-ids [state]
  (::todo-ids state))

(defn get-todos [state]
  (mapv (partial get-todo-by-id state)
        (::todo-ids state)))

(defn get-next-todo-id [state todo-id]
  (first (last (partition-by #{todo-id} (get-todo-ids state)))))

(defn get-previous-todo-id [state todo-id]
  (first (last (partition-by #{todo-id} (get-todo-ids state)))))

(defn get-sort [state]
  (::sort state))

(defn set-todos [state todos]
  (assoc state
         ::todo-by-id (index-by :id todos)
         ::todo-ids (mapv :id todos)))

(defn set-todo [state todo]
  (assoc-in state [::todo-by-id (:id todo)] todo))

(defn add-todos [state todos]
  (set-todos state (concat todos (get-todos state))))

(defn set-sort [state sort]
  (assoc-in state [::sort] sort))

(defn add-todos-texts [state texts]
  (add-todos state (mapv make-todo texts)))

(defn add-todo-text [state text]
  (add-todos state [(make-todo text)]))

(defn toggle-todo [state todo-id]
  (update-in state [::todo-by-id todo-id :done?] not))

(defn set-todo-text [state todo-id text]
  (assoc-in state [::todo-by-id todo-id :text] text))

(defn sort-todos-by [state k]
  (let [{:keys [key dir]} (get-sort state)
        dir (if (= k key)
              (case dir
                :asc :desc
                :asc)
              :asc)
        sorted-todos (->> (get-todos state)
                          (sort-by (comp str/lower-case str k)))
        sorted-todos (if (= :asc dir)
                       sorted-todos
                       (reverse sorted-todos))]
    (-> state
        (set-todos sorted-todos)
        (set-sort {:key k :dir dir}))))

(defn set-add-todo-text [state text]
  (assoc state ::add-todo-text text))

;;; State, the one atom

(defonce state-app (reagent/atom {}))

;;; Subscriptions, read state

(defn subscribe-todo-by-id [todo-id]
  (reagent/cursor state-app [::todo-by-id todo-id]))

(defn subscribe-items-to-do []
  (reagent/track #(count (remove :done? (get-todos @state-app)))))

(defn subscribe-todo-ids []
  (reagent/track #(get-todo-ids @state-app)))

(defn subscribe-todos []
  (reagent/track #(get-todos @state-app)))

(defn subscribe-next-todo-id [todo-id]
  (reagent/track #(get-next-todo-id @state-app todo-id)))

(defn subscribe-sort []
  (reagent/track #(get-sort @state-app)))

(defn subscribe-add-todo-text []
  (reagent/cursor state-app [::add-todo-text]))

;;; Actions, modify state

(defn action-add-todos [texts]
  (swap! state-app add-todos-texts texts))

(defn action-add-todo [text]
  (swap! state-app add-todo-text text))

(defn action-toggle-todo [todo-id]
  (swap! state-app toggle-todo todo-id))

(defn action-set-todo-text [todo-id text]
  (swap! state-app set-todo-text todo-id text))

(defn action-sort-todos-by [k]
  (swap! state-app sort-todos-by k))

(defn action-set-add-todo-text [text]
  (swap! state-app set-add-todo-text text))

;;; Components, view and container

(defn view-checkbox
  "Displays a checkbox."
  [{:keys [id class tab-index checked? on-change]}]
  (let [wrapped-on-change (fn [event]
                            (.preventDefault event)
                            (.stopPropagation event)
                            (when on-change
                              (on-change checked?)))]
    [:span.lh100.va-m.fs150 {:id (when id id)
                             :class class
                             :tab-index (or tab-index 0)
                             :role :checkbox
                             :aria-checked checked?
                             :aria-label (if checked? "checked" "unchecked")
                             :on-click wrapped-on-change
                             :on-key-press #(when (= (.-key %) " ")
                                              (wrapped-on-change %))}
     (if checked? "☑" "☐")]))

(defn view-todo [{:keys [todo toggle-todo set-todo-text next-todo-id]}]
  (let [todo-input-id (str "rg-todo-input-" (:id todo))
        on-toggle-todo (fn [event]
                         (.preventDefault event)
                         (toggle-todo))
        on-start-editing (fn [event]
                           (.preventDefault event)
                           (doto (.getElementById js/document todo-input-id)
                             (.focus)
                             (.select)))
        on-change (fn [event]
                    (.preventDefault event)
                    (set-todo-text (.. event -target -value)))]
    [:tr.view-todo (when (:done? todo) {:class :done})
     [:td.view-todo--state.w7.text-center.pointer {:on-click on-toggle-todo}
      [view-checkbox {:checked? (:done? todo) :on-change toggle-todo}]]
     [:td.view-todo--text.pr1 {:on-click on-start-editing}
      [:input.w100.text-overflow
       {:id todo-input-id
        :value (:text todo)
        :on-key-press (fn [event]
                        (when (= 13 (.-which event))
                          (.preventDefault event)
                          (let [next-element-id (str "rg-todo-input-" next-todo-id)
                                next-element (.getElementById js/document next-element-id)]
                            (when next-element
                              (doto next-element
                                (.focus)
                                (.select))))))
        :on-change on-change}]]]))

(defn container-todo [todo-id]
  [view-todo
   {:todo @(subscribe-todo-by-id todo-id)
    :toggle-todo (partial action-toggle-todo todo-id)
    :set-todo-text (partial action-set-todo-text todo-id)
    :next-todo-id @(subscribe-next-todo-id todo-id)}])

(defn container-add-todo []
  (let [text @(subscribe-add-todo-text)
        disabled? (str/blank? text)
        add-todo (fn []
                   (action-add-todo text)
                   (action-set-add-todo-text nil)
                   (js/window.scrollTo 0 0))
        set-text (fn [event]
                   (.preventDefault event)
                   (action-set-add-todo-text (.. event -target -value)))
        on-start-editing (fn [event]
                           (.preventDefault event)
                           (doto (.getElementById js/document "container-add-todo--input")
                             (.focus)
                             (.select)))
        on-key-press (fn [event]
                       (when (and (not disabled?) (= 13 (.-which event)))
                         (.preventDefault event)
                         (add-todo)))]
    [:div.container-add-todo.pb1
     [:label.fs125 {:for :container-add-todo--input} "Add to-do"]
     [:div.w100.mt05.flex-row
      [:div.flex1.container-add-todo--input-wrapper.ph1.mr1.pv05 {:on-click on-start-editing}
       [:input.w100 {:id :container-add-todo--input
                     :value text
                     :placeholder "I need to …"
                     :on-change set-text
                     :on-key-press on-key-press}]]
      [:button.btn.btn-primary {:class (when disabled? "btn-primary--disabled")
                                :on-click (when-not disabled? add-todo)}
       [:span "Add"]]]]))


(defn container-sort-indicator [k]
  (let [sort @(subscribe-sort)]
    [:span.container-sort-indicator.ph05
     (when (= k (:key sort))
       (case (:dir sort)
         :asc "⮧"
         "⮥"))]))

(defn container-items-to-do []
  (let [items-to-do-count @(subscribe-items-to-do)]
    [:span (str "You have " items-to-do-count " items to do.")]))

(defn view-todos [{:keys [todo-ids sort-indicator sort-todos-by items-to-do todo-component]}]
  [:div.view-todos
   [:table.w100
    [:thead
     [:tr
      [:th.sticky.t11.mt2.bgw.bb2.fs125.pointer.w7 {:on-click #(sort-todos-by :done?)}
       "Status" [sort-indicator :done?]]
      [:th.sticky.t11.mt2.bgw.bb2.fs125.pointer.minw0.maxw0 {:on-click #(sort-todos-by :text)}
       [:div.flex-row.align-end.justify-between
        [:div.va-b.text-left
         "Description" [sort-indicator :text]]
        [:div.va-b.text-right.text-overflow [items-to-do]]]]]]
    [:tbody (mapck todo-component
                   identity
                   todo-ids)]
    [:tfoot
     [:tr [:th.bt2 {:col-span 2}]]]]])

(defn container-todos []
  [view-todos
   {:todo-ids @(subscribe-todo-ids)
    :todo-component container-todo
    :sort-indicator container-sort-indicator
    :sort-todos-by action-sort-todos-by
    :items-to-do container-items-to-do}])

(defn view-app-title []
  [:h1.flex1.text-left "To-do App"])

(defn app-todo []
  [:div.m2
   [:header.sticky.t0.mt2.pt1.bgw [view-app-title]]
   [:header.sticky.t4.mt2.pt1.bgw [container-add-todo]]
   [container-todos]])

;;; Demo

(defonce demo-data
  (action-add-todos (words/sentences 100)))
