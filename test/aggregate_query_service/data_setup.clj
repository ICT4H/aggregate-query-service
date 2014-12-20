(ns aggregate-query-service.data-setup
  (:import (java.sql BatchUpdateException)
           (org.sqlite.javax SQLiteConnectionPoolDataSource))
  (:require [clojure.java.jdbc :as jdbc]))

(def db-spec {:datasource (doto (new SQLiteConnectionPoolDataSource)
                            (.setUrl "jdbc:sqlite:db/test.db"))})

(defn insert-in-table-name-map
  [{table-name :table-name, data-to-insert :data}]
  (jdbc/insert! db-spec (keyword table-name) data-to-insert))


(defn perform-table-operation [{table-name :table-name, [& table-structure] :table-structure} table-operation]
  (try (jdbc/db-do-commands db-spec
                            (apply table-operation (keyword table-name)
                                   table-structure))
       (catch BatchUpdateException e))
  )

(defn drop-table [table]
  (perform-table-operation table jdbc/drop-table-ddl))

(defn create-table [table]
  (perform-table-operation table jdbc/create-table-ddl))


(def data-to-insert '(
                       {
                        :table-name      "something"
                        :table-structure [[:id "int"] [:name "text"]]
                        :data            {:id 1 :name "Some Name"}
                        }
                       {
                        :table-name      "something_else"
                        :table-structure [[:id "int"] [:name "text"]]
                        :data            {:id 2 :name "Some Other Name"}
                        }
                       {
                        :table-name      "one_more_thing"
                        :table-structure [[:id "int"] [:name "text"]]
                        :data            {:id 6 :name "One More Name"}
                        }
                       {
                        :table-name      "another_thing"
                        :table-structure [[:id "int"] [:name "text"]]
                        :data            {:id 4 :name "Another Name"}
                        }))

(defn setup-data []
  (dorun (map create-table data-to-insert))
  (dorun (map insert-in-table-name-map data-to-insert))
  )


(defn tear-down []
  (dorun (map drop-table data-to-insert))
  )