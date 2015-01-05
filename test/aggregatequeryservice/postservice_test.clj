(ns aggregatequeryservice.postservice-test
  (:import (org.sqlite.javax SQLiteConnectionPoolDataSource))
  (:use [midje.sweet])
  (:require [clojure-test-datasetup.core :as ds]
            [http.async.client :refer :all :as h]
            [aggregatequeryservice.postservice :as ap]
            [clojure.data.json :as json]))

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
                         :header-1 "header-1"
                         ))

(defn mock-http-requests [& args]
  args
  )

(facts "Post Contents to Some URL"
       (with-state-changes [(before :facts (dorun (ds/setup-dataset "resources/postservice-dataset.json" db-spec)))
                            (after :facts (ds/tear-down-dataset "resources/postservice-dataset.json" db-spec))]
                           (fact "post a template"
                                 (with-redefs [h/POST mock-http-requests
                                               h/await mock-http-requests
                                               h/string mock-http-requests]
                                   (let [response (flatten (ap/run-queries-render-templates-post "http_config.json" (get db-spec :datasource) query-params-map extra-params http-post-headers))]
                                     (nth response 1)
                                     =>
                                     "mocked_uri"
                                     (json/read-str (nth response 3))
                                     =>
                                     (json/read-str "{\n\"dataSet\": \"Rendered Dataset\",\n\"period\": \"20141111\",\n\"orgUnit\": \"71345684\",\n\"dataValues\": [\n{\n\"dataElement\": \"AiPqHCbJQJ1\",\n\"categoryOptionCombo\": \"u2QXNMacZLt\",\n\"value\": \"110\"\n},\n{\n\"dataElement\": \"AiPqHCbJQJ1\",\n\"categoryOptionCombo\": \"DA2N93v7s0O\",\n\"value\": \"First\"\n}\n]\n}")
                                     (nth response 5)
                                     =>
                                     http-post-headers)))))