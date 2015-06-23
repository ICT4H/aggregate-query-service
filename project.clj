(defproject org.ict4h/aggregate-query-service "1.0.2-SNAPSHOT"
            :description "Aggregate Query Service"
            :url "https://github.com/ICT4H/aggregate-query-service"
            :license {:name "Apache License, Version 2.0"
                      :url  "http://www.apache.org/licenses/LICENSE-2.0.html"}
            :dependencies [[org.clojure/clojure "1.6.0"]
                           [cheshire "5.4.0"]
                           [org.clojure/java.jdbc "0.3.6"]
                           [midje "1.6.3"]
                           [org.xerial/sqlite-jdbc "3.8.7"]
                           [org.bahmni/clojure-test-datasetup "1.0"]
                           [freemarker-clj "0.1.0"]
                           [http.async.client "0.5.2"]
                           [yesql "0.4.0"]
                           [org.slf4j/slf4j-log4j12 "1.5.6"]
                           [org.clojure/tools.logging "0.3.1"]]
            :main aggregatequeryservice.postservice
            :resource-paths ["resources" "resources/queries"]
            :target-path "target/%s"
            :profiles {:uberjar {:aot :all}}
            :aliases {"test" ["midje"]}
            :group-id "org.ict4h"
            :artifact-id "aggregate-query-service"
            :parent [org.sonatype.oss/oss-parent "7"]
            :packaging "jar"
            :name "aggregate-query-service"
            :deploy-repositories [["snapshots" {:url       "https://oss.sonatype.org/content/repositories/snapshots"
                                                :id        "sonatype-nexus-snapshots"
                                                :name      "Sonatype Nexus Snapshots"
                                                :releases  false
                                                :snapshots true
                                                :username  "ict4h"
                                                :password  :env}]
                                  ["releases" {:url       "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
                                               :id        "sonatype-nexus-staging"
                                               :name      "Sonatype Nexus Staging"
                                               :releases  true
                                               :snapshots true
                                               :username  "ict4h"
                                               :password  :env}]]
            :plugins [[lein-midje "3.1.3"]]
            :scm {:name                 "git"
                  :tag                  "HEAD"
                  :url                  "https://github.com/ICT4H/aggregate-query-service"
                  :connection           "scm:git:git@github.com:ICT4H/aggregate-query-service.git"
                  :developer-connection "scm:git:git@github.com:ICT4H/aggregate-query-service.git"}
            :pom-addition [:developers [:developer
                                        [:id 1]
                                        [:name "ICT4H"]
                                        [:email "ict4h@thoughtworks.com"]
                                        [:organization "ThoughtWorks, Inc."]]])
