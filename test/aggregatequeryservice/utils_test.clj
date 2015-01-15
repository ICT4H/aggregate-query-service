(ns aggregatequeryservice.utils-test
  (:import (java.io FileNotFoundException)
           (connectionprovider TestConnectionProvider))
  (:use [midje.sweet])
  (:require [aggregatequeryservice.utils :as utils]))

(defn test-config-mapping
  ([]
    (utils/read-config-to-map "sample_config.json"))
  ([config-file]
    (utils/read-config-to-map config-file)))

(def connection-provider (doto (new TestConnectionProvider "jdbc:sqlite:db/test1.db")))
(defn add [a b]
  (+ a 1))

(def add1 (partial add 1))

(facts "Reading Config File"
       (fact "Throws exception if file not found"
             (test-config-mapping "doesn'texist") => (throws FileNotFoundException))

       (fact "If file valid, read and convert to hash-map"
             (test-config-mapping) => [{:queryGroupname "Query Group 1" :queries [{:queryName "Query 1", :query "select * from something;"}
                                                                                  {:queryName "Query 2", :query "select * from something_else;"}]}
                                       {:queryGroupname "Query Group 2", :queries [{:queryName "Query 1", :query "select * from one_more_thing;"}
                                                                                   {:queryName "Query 2", :query "select * from another_thing;"}]}]))
(facts "Database connection wrapper"
       (fact "Return should be the function partial return"
             (utils/do-with-connection add1 connection-provider)
             =>
             2))