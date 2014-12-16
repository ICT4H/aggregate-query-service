(ns aggregate-query-service.core-test
  (:import (java.io FileNotFoundException))
  (:use midje.sweet)
  (:require [clojure.test :refer :all]
            [aggregate-query-service.core :refer :all :as aqs]))

(defn test-config-mapping
  ([]
    (aqs/read-config "resources/sample_config.json"))
  ([config-file]
    (aqs/read-config config-file)))

(facts "Reading Config File"
       (fact "Throws exception if file not found"
             (test-config-mapping "doesn'texist") => (throws FileNotFoundException))

       (fact "If file valid, read and convert to hash-map"
             (test-config-mapping) => [{:queryGroupname "Query Group 1" :queries [{:queryName "Query 1", :query "select * from something;"}
                                                                                  {:queryName "Query 2", :query "select * from something_else;"}]}
                                       {:queryGroupname "Query Group 2", :queries [{:queryName "Query 1", :query "select * from one_more_thing;"}
                                                                                   {:queryName "Query 2", :query "select * from another_thing;"}]}]))

(facts "Create self sufficient query object"
       (fact "Add query group name to query object"
             (aqs/get-queries (first (test-config-mapping)))
             =>
             (list {:queryGroupname "Query Group 1" :query "select * from something;" :queryName "Query 1"}
                   {:queryGroupname "Query Group 1" :queryName "Query 2", :query "select * from something_else;"})))

(facts "Render a list of queries with appropriate parameters"
       (fact "Render queries with appropriate params"
             (aqs/render-queries
               (hash-map :endDate "renderedEndDate" :startDate "renderedStartDate")
               (list {:queryGroupname "Query Group 1" :query "select * from something where endDate < :endDate:;" :queryName "Query 1"}
                     {:queryGroupname "Query Group 1" :queryName "Query 2", :query "select * from something_else where startDate > :startDate:;"}))
             =>
             (list {:queryGroupname "Query Group 1" :query "select * from something where endDate < renderedEndDate;" :queryName "Query 1"}
                   {:queryGroupname "Query Group 1" :queryName "Query 2", :query "select * from something_else where startDate > renderedStartDate;"})))

(facts "Render a single Query"
       (fact "Render query with appropriate params"
             (aqs/render-query (hash-map :renderThis "rdThis" :renderThat "rdThat" :replaceThis "rpThis" :replaceThat "rpThat")
                               {:query "select :renderThis: from :renderThat: and :replaceThis: and :replaceThat:;"})
             =>
             {:query "select rdThis from rdThat and rpThis and rpThat;"}))