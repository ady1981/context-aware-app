(defproject context-aware-app "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                 [org.clojure/clojure "1.8.0"]

                 [org.clojure/core.match "0.3.0-alpha4"]

                 [crypto-random "1.2.0"]
                 ]
  :profiles {
             :dev     {:source-paths ["env/dev"]}

             :uberjar {:env {:production "true"}
                       :omit-source true}}
  )
