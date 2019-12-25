(ns counter.events
  (:require
    [re-frame.core :as re-frame]
    [counter.db :as db]
    [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]
    [cljs.spec.alpha :as s]))

(defn empty-or
  "Returns the collection if it is not empty, or the specified value"
  [coll default]
  (if (empty? coll)
    default
    coll))

(defn check-and-throw
  "Throws an exception if `db` doesn't match the Spec `a-spec`."
  [a-spec db]
  (when-not (s/valid? a-spec db)
    (throw (ex-info (str "spec check failed: " (s/explain-str a-spec db)) {}))))

;; now we create an interceptor using `after`
(def check-spec (re-frame/after (partial check-and-throw ::db/db)))

(re-frame/reg-event-fx
  ::initialize-db
  [(re-frame/inject-cofx :load-todo-items) check-spec]
  (fn-traced [{:keys [todo-items]} _]
             {:db (assoc db/default-db :todo-items todo-items)}))

(re-frame/reg-event-db
  :add-to-counter
  (fn-traced [db [_ amount]]
             (update db :count #(+ % amount))))

(defn alloc-item [todos]
  "Allocates a new item to the todos map"
  (-> (last todos)
      first
      inc
      (#(assoc todos % {:title "" :description ""}))))

(re-frame/reg-event-db
  :add-item
  [check-spec]
  (fn-traced [db _]
             (update db :todo-items #(alloc-item %))))

(def ->local-store (re-frame/after db/save-todo-items))

(re-frame/reg-event-db
  :save-item
  [check-spec ->local-store]
  (fn-traced [db [_ id item]]
             (assoc-in db [:todo-items id] item)))

(defn dissoc-item [todos id]
  (let [after (dissoc todos id)]
    (if (empty? after)
      db/default-todo-items
      after)))

(re-frame/reg-event-db
  :remove-item
  [check-spec ->local-store]
  (fn-traced [db [_ id]]
             (update db
                     :todo-items
                     #(dissoc-item % id))))