(defproject macroz/cljs-front-end-demos "1.0.0"
  :description "ClojureScript front-end demos"
  :url "https://github.com/Macroz/cljs-front-end-demos"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.9.3"

  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.597"]
                 [reagent "0.9.1"]
                 [medley "1.2.0"]
                 [re-frame "0.11.0"]]

  :plugins [[lein-figwheel "0.5.18"]
            [lein-cljsbuild "1.1.7" :exclusions [[org.clojure/clojure]]]]

  :source-paths ["src"]

  :cljsbuild {:builds
              [{:id "dev"
                :source-paths ["src"]
                :figwheel {:on-jsload "cljs-front-end-demos.core/on-js-reload"}
                :compiler {:main cljs-front-end-demos.core
                           :asset-path "/js/compiled/out"
                           :output-to "resources/public/js/compiled/cljs_front_end_demos.js"
                           :output-dir "resources/public/js/compiled/out"
                           :source-map-timestamp true
                           :preloads [devtools.preload]}}
               {:id "min"
                :source-paths ["src"]
                :compiler {:output-to "resources/public/js/compiled/cljs_front_end_demos.js"
                           :main cljs-front-end-demos.core
                           :optimizations :advanced
                           :pretty-print false}}]}

  :figwheel {:css-dirs ["resources/public/css"]
             :ring-handler cljs-front-end-demos.handler/handler}


  :profiles {:dev {:dependencies [[binaryage/devtools "1.0.0"]
                                  [figwheel-sidecar "0.5.19"]
                                  [cider/piggieback "0.4.2"]]
                   :plugins [[lein-ancient "0.6.15"]]
                   :source-paths ["src" "dev"]
                   :repl-options {:nrepl-middleware [cider.piggieback/wrap-cljs-repl]}
                   :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                                     :target-path]}})
