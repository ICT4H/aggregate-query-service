(ns aggregatequeryservice.utils-test
  (:import (java.io FileNotFoundException))
  (:use [midje.sweet])
  (:require [aggregatequeryservice.utils :as utils]))

(defn test-config-mapping
  ([]
    (utils/read-config "sample_config.json"))
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
