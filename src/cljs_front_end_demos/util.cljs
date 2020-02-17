(ns cljs-front-end-demos.util
  (:require [goog.string :as gstring])
  (:import [goog.async Debouncer]))

(def conjv
  "Like `conj` but defaults to a vector for nil."
  (fnil conj []))

(defn mapck
  "Like regular `map` but wraps each `f` in a Reagent component with key `k`."
  [f k coll]
  (map (fn [x]
         ^{:key (k x)} [f x])
       coll))

(defn sort-boolean
  "Comparator for booleans that sorts them in the correct order."
  [b]
  (case b
    true 1
    false 2
    2))

(defn prepend
  "Prepends `xs` to the given collection `coll`.

  Same as into but arguments are reversed."
  [coll xs]
  (into xs coll))

(defn debounce
  "Debounces the given `f` with the `interval`.

  No more than one call will be done within the interval."
  [f interval]
  (let [devounce (Debouncer. f interval)]
    (fn [& args] (.apply (.-fire devounce) devounce (to-array args)))))

(defn format
  "Formats a string using goog.string.format.

  (format \"Cost: %.2f\" 10.0234)"
  [fmt & args]
  (apply gstring/format fmt args))
