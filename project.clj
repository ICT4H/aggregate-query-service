(defproject org.ict4h/aggregate-query-service "0.1.0-SNAPSHOT"
            :description "Aggregate Query Service"
            :url "https://github.com/ICT4H/aggregate-query-service"
            :license {:name "Apache License, Version 2.0"
                      :url  "http://www.apache.org/licenses/LICENSE-2.0.html"}
            :dependencies [[org.clojure/clojure "1.6.0"]
                           [org.clojure/data.json "0.2.5"]
                           [org.clojure/java.jdbc "0.3.6"]
                           [midje "1.6.3"]
                           [org.xerial/sqlite-jdbc "3.8.7"]
                           [clojure-test-datasetup/clojure-test-datasetup "0.1.0-SNAPSHOT"]]
            :main ^:skip-aot aggregate-query-service.core
            :resource-paths ["resource"]
            :target-path "target/%s"
            :profiles {:uberjar {:aot :all}}
            :aliases {"test" ["midje"]}
            :group-id "org.ict4h"
            :artifact-id "aggregate-query-service"
            :parent [org.sonatype.oss/oss-parent "7"]
            :packaging "jar"
            :name "aggregate-query-service"
            :repositories [["snapshots" {:url       "https://oss.sonatype.org/content/repositories/snapshots"
                                         :id        "sonatype-nexus-snapshots"
                                         :name      "Sonatype Nexus Snapshots"
                                         :releases  false
                                         :snapshots true
                                         :username "ict4h"
                                         :password :env}]]
            :plugins [[lein-midje "3.1.3"]]
            :scm {:name                 "git"
                  :tag                  "HEAD"
                  :url                  "https://github.com/ICT4H/aggregate-query-service"
                  :connection           "scm:git:git@github.com:ICT4H/aggregate-query-service.git"
                  :developer-connection "scm:git:git@github.com:ICT4H/aggregate-query-service.git"})
