(ns counter.subs-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [counter.db :as db]
            [counter.subs :as subs]))

(deftest get-todo-items-test
  (testing "Test get visible items empty filter"
    (let [items (subs/get-visible-items db/default-db "")]
      (is (= items db/default-todo-items))))
  (testing "Test get visible items non-empty filter"
    (let [db (assoc db/default-db :todo-items (sorted-map 0 {:title "Some task" :description ""}))
          filtered (subs/get-visible-items db "Some")
          filtered-no-result (subs/get-visible-items db "abc")]
      (is (not (empty? filtered)))
      (is (= {:title "Some task" :description ""} (second (first filtered))))
      (is (empty? filtered-no-result)))))