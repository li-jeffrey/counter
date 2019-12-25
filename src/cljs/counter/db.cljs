(ns counter.db
  (:require [cljs.reader]
            [cljs.spec.alpha :as s]
            [re-frame.core :as re-frame]))

;; DB-spec
(s/def ::name string?)
(s/def ::count int?)
(s/def ::todo-item (s/map-of #{:title :description} string?))
(s/def ::todo-items (s/and
                      (s/map-of int? ::todo-item)
                      #(instance? PersistentTreeMap %)))
(s/def ::db (s/keys :req-un [::name ::count ::todo-items]))

;; DB
(def default-todo-items
  (sorted-map 0 {:title "" :description ""}))

(def default-db
  {:name       "re-frame"
   :count      0
   :todo-items default-todo-items
   })

;; Local store methods
(def ls-key "counter-app")

(defn save-todo-items
  "Puts todos into localStorage"
  [db]
  (.setItem js/localStorage ls-key (str (:todo-items db))))

(defn load-todo-items
  "Retrieves the todos from localStorage. Returns default-todos if the stored copy is invalid."
  []
  (let [data (some->> (.getItem js/localStorage ls-key)
                        (cljs.reader/read-string))]
    (if (map? data)
      (let [stored (into (sorted-map) data)]
        (if (and (not (empty? stored))
                 (s/valid? ::todo-items stored))
          stored
          default-todo-items))
      default-todo-items)))

(re-frame/reg-cofx
  :load-todo-items
  (fn [cofx _]
    ;; put the localstore todos into the coeffect under :local-store-todos
    (assoc cofx :todo-items
                ;; read in todos from localstore, and process into a sorted map
                (load-todo-items))))