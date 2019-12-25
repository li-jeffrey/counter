(ns counter.events-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [counter.events :as events]))

(deftest todo-item-events-test
  (testing "Test alloc item"
    (let [todos (sorted-map 0 {:title "a" :description "b"})
          added-item (events/alloc-item todos)]
      (is (= added-item (sorted-map 0 {:title "a" :description "b"}
                                    1 {:title "" :description ""})))))
  (testing "Test dissoc item"
    (let [todos (sorted-map 0 {:title "a" :description "b"}
                            1 {:title "" :description ""})
          removed-item (events/dissoc-item todos 1)]
      (is (= removed-item (sorted-map 0 {:title "a" :description "b"})))))
  (testing "Test dissoc last item"
    (let [todos (sorted-map 0 {:title "a" :description "b"})
          removed-item (events/dissoc-item todos 0)]
      (is (= removed-item (sorted-map 0 {:title "" :description ""}))))))
