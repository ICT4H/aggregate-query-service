(ns aggregate-query-service.core-test
  (:require [clojure.test :refer :all]
            [aggregate-query-service.core :refer :all :as aqs]))

(defn test-config-mapping
  []
  (aqs/read-config "resources/sample_config.json"))

(deftest read-config-file
  (testing "read json config and map it to json"
    (is (= [{:queryGroupname "Query Group 1" :queries [{:queryName "Query 1", :query "select * from something;"} {:queryName "Query 2", :query "select * from something_else;"}]} {:queryGroupname "Query Group 2", :queries [{:queryName "Query 1", :query "select * from one_more_thing;"} {:queryName "Query 2", :query "select * from another_thing;"}]}]
           (test-config-mapping)))))

