(ns aggregatequeryservice.postservice-test
  (:import (org.sqlite.javax SQLiteConnectionPoolDataSource))
  (:use midje.sweet)
  (:require [clojure-test-datasetup.core :as ds]
            [aggregatequeryservice.postservice :as ap]))

(def db-spec {:datasource (doto (new SQLiteConnectionPoolDataSource)
                            (.setUrl "jdbc:sqlite:db/postservicetest.db"))})

(def extra-params (hash-map
                    :dataset "Rendered Dataset"
                    :period "20141111"
                    :organization "71345684"
                    ))

(def query-params-map (hash-map
                        "random" "110"))

(def http-post-headers (hash-map
                         ))

(facts "Post Contents to Some URL"
       (with-state-changes [(before :facts (dorun (ds/setup-dataset "resources/postservice-dataset.json" db-spec)))
                            (after :facts (ds/tear-down-dataset "resources/postservice-dataset.json" db-spec))]
                           (fact "post a template"
                                 (clojure.data.json/read-str (ap/run-queries-render-templates-post "resources/http_config.json" (get db-spec :datasource) query-params-map extra-params {}))
                                  =>
                                 (clojure.data.json/read-str "{\"dataSet\": \"Rendered Dataset\",\"period\": \"20141111\",\"orgUnit\": \"71345684\",\"dataValues\": [{\"dataElement\": \"AiPqHCbJQJ1\",\"categoryOptionCombo\": \"u2QXNMacZLt\",\"value\": \"110\"},{\"dataElement\": \"AiPqHCbJQJ1\",\"categoryOptionCombo\": \"DA2N93v7s0O\",\"value\": \"First\"}]}"))))