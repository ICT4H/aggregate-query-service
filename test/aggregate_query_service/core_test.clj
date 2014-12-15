(ns aggregate-query-service.core-test
  (:require [clojure.test :refer :all]
            [aggregate-query-service.core :refer :all :as aqs]))

(defn test-config-mapping
  ([]
    (aqs/read-config "resources/sample_config.json"))
  ([config-file]
    (aqs/read-config config-file)))

(deftest read-config-file-failure
  (testing "Throw exception on file read failure"
    (is (thrown? Exception (test-config-mapping "doesn'texist")))))

(deftest read-config-file-success
  (testing "read json config and map it to json"
    (is (= [{:queryGroupname "Query Group 1" :queries [{:queryName "Query 1", :query "select * from something;"} {:queryName "Query 2", :query "select * from something_else;"}]} {:queryGroupname "Query Group 2", :queries [{:queryName "Query 1", :query "select * from one_more_thing;"} {:queryName "Query 2", :query "select * from another_thing;"}]}]
           (test-config-mapping)))))

(deftest query-self-sufficiency
  (testing "Add query group name to query object"
    (is (= `({:queryGroupname "Query Group 1" :query "select * from something;" :queryName "Query 1"} {:queryGroupname "Query Group 1" :queryName "Query 2", :query "select * from something_else;"})
(aqs/get-queries (first (test-config-mapping)))))))

(deftest queries-rendering
  (testing "Render queries with appropriate params"
    (is (= '({:queryGroupname "Query Group 1" :query "select * from something where endDate < renderedEndDate;" :queryName "Query 1"} {:queryGroupname "Query Group 1" :queryName "Query 2", :query "select * from something_else where startDate > renderedStartDate;"})
           (aqs/render-queries
             (hash-map :endDate "renderedEndDate" :startDate "renderedStartDate")
             '({:queryGroupname "Query Group 1" :query "select * from something where endDate < :endDate:;" :queryName "Query 1"} {:queryGroupname "Query Group 1" :queryName "Query 2", :query "select * from something_else where startDate > :startDate:;"})
             )))))

(deftest query-rendering
  (testing "Render query with appropriate params"
    (is (= {:query "select rdThis from rdThat and rpThis and rpThat;"}
           (aqs/render-query (hash-map :renderThis "rdThis" :renderThat "rdThat" :replaceThis "rpThis" :replaceThat "rpThat") {:query "select :renderThis: from :renderThat: and :replaceThis: and :replaceThat:;"})))))
