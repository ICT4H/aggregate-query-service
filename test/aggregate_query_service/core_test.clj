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

(deftest sql-construction
  (testing "Construct sql by replacing start-date and end-date"
    (is (= "select * from something where start-date > 10-11-2014 and end-date < 10-11-2015;"
           (aqs/construct-sql {:query "select * from something where start-date > startDate and end-date < endDate;" :start-date "10-11-2014" :end-date "10-11-2015"})))))