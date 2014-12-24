(ns aggregatequeryservice.runqueries_test
  (:import (java.io FileNotFoundException)
           (org.sqlite.javax SQLiteConnectionPoolDataSource))
  (:use [midje.sweet])
  (:require [aggregatequeryservice.runqueries :refer :all :as aqs]
            [aggregatequeryservice.utils :refer :all :as utils]
            [clojure-test-datasetup.core :as ds]))

(def db-spec {:datasource (doto (new SQLiteConnectionPoolDataSource)
                            (.setUrl "jdbc:sqlite:db/test.db"))})


(defn test-config-mapping
  ([]
    (utils/read-config "resources/sample_config.json"))
  ([config-file]
    (utils/read-config config-file)))

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

(facts "End to end integration test"
       (with-state-changes [(before :facts (dorun (ds/setup-dataset "resources/test-dataset.json" db-spec)))
                            (after :facts (ds/tear-down-dataset "resources/test-dataset.json" db-spec))]
                           (fact "Read JSON and fire queries and return back the result set"
                                 (aqs/run-queries-and-get-results "resources/sample_config.json" (get db-spec :datasource) (hash-map))
                                 =>
                                 '({:result         ({:name "Some Name", :id 1} {:id 15, :name "Some First Name"}),
                                    :queryGroupname "Query Group 1", :queryName "Query 1", :query "select * from something;"}
                                    {:result         ({:name "Some Other Name", :id 2}),
                                     :queryGroupname "Query Group 1", :queryName "Query 2", :query "select * from something_else;"}
                                    {:result         ({:name "One More Name", :id 6}),
                                     :queryGroupname "Query Group 2", :queryName "Query 1", :query "select * from one_more_thing;"}
                                    {:result         ({:name "Another Name", :id 4}),
                                     :queryGroupname "Query Group 2", :queryName "Query 2", :query "select * from another_thing;"}))))