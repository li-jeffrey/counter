(ns counter.subs
  (:require
    [re-frame.core :as re-frame]
    [clojure.string :as str]))

(re-frame/reg-sub
 ::name
 (fn [db]
   (:name db)))

(re-frame/reg-sub
  ::count
  (fn [db]
    (:count db)))

(defn get-visible-items
  [{:keys [todo-items]} [_ title-filter]]
  (if (empty? title-filter)
    todo-items
    (filter #(str/includes? (:title (second %)) title-filter) todo-items)))

(re-frame/reg-sub
  ::visible-items
  get-visible-items)