(ns counter.db-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [counter.db :as db]
            [cljs.spec.alpha :as s]))

(deftest spec-test
  (testing "Test db spec"
    (is (s/valid? ::db/db db/default-db))))

(deftest local-storage-test
  (testing "Test save and load todos"
    (let [todos (sorted-map 0 {:title "Some task" :description ""})]
      (db/save-todo-items (assoc db/default-db :todo-items todos))
      (let [loaded (db/load-todo-items)]
        (is (= loaded todos)))))
  (testing "Test load invalid local storage"
    (.setItem js/localStorage db/ls-key "abc")
    (let [todos (db/load-todo-items)]
      (is (= todos db/default-todo-items))))
  (testing "Test load empty local storage"
    (.removeItem js/localStorage db/ls-key)
    (let [todos (db/load-todo-items)]
      (is (= todos db/default-todo-items)))))