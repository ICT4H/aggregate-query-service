(defproject aggregate-query-service "0.1.0-SNAPSHOT"
  :description "Aggregate Query Service"
  :url "https://github.com/ICT4H"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/data.json "0.2.5"]
                 [org.clojure/java.jdbc "0.3.6"]]
  :main ^:skip-aot aggregate-query-service.core
  :resource-paths ["src/main/resource"]
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
