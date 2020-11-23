(ns cljs-front-end-demos.reagent.flight-booker)

(defn view-center [& children]
  [:div.w100.h100.flex-row.align-center.justify-center
   (into [:div] children)])

(defn view-select [{:keys [options]}]
  (into [:select]
        (for [{:keys [key title]} options]
          [:option {:key key} title])))

(defn view-date []
  [:input {:type :date}])

(defn app-flight-booker []
  [view-center
   [:h1.mv2 "Flight Booker"]
   [:form.flex-col
    [view-select {:options [{:key :one-way :title "One-way flight"}
                            {:key :return :title "Return flight"}]}]
    [view-date]
    [view-date]]])
