(ns counter.views
  (:require
    [re-frame.core :as re-frame]
    [re-com.core :as re-com]
    [re-com.core :refer [p]]
    [counter.subs :as subs]
    [reagent.core :as reagent]))

(defn title []
  (let [name (re-frame/subscribe [::subs/name])]
    [re-com/title
     :label (str "Demo " @name " app")
     :level :level1]))

(defn counter-panel []
  (let [count @(re-frame/subscribe [::subs/count])]
    [re-com/h-box
     :gap "10px"
     :children [[re-com/title
                 :label (str "Counter: " count)
                 :level :level3]
                [re-com/button
                 :label "Add"
                 :class "btn-info"
                 :on-click #(re-frame/dispatch [:add-to-counter 1])]
                [re-com/button
                 :label "Remove"
                 :class "btn-warning"
                 :disabled? (<= count 0)
                 :on-click #(re-frame/dispatch [:add-to-counter -1])]]
     ]))

(defn todo-panel []
  (let [title-filter (reagent/atom nil)]
    (fn []
      [re-com/v-box
       :gap "10px"
       :children (concat [[re-com/title
                           :label "To do list"
                           :level :level3]
                          [re-com/input-text
                           :placeholder "Search title"
                           :model title-filter
                           :change-on-blur? false
                           :on-change #(reset! title-filter %)]]
                         (let [visible-items @(re-frame/subscribe [::subs/visible-items @title-filter])
                               first-item-id (first (first visible-items))]
                           (map (fn [[id item]]
                                  [re-com/h-box
                                   :gap "10px"
                                   :children [[re-com/v-box
                                               :gap "10px"
                                               :children [[re-com/input-text
                                                           :placeholder "Item title"
                                                           :model (:title item)
                                                           :on-change #(re-frame/dispatch [:save-item id (assoc item :title %)])]
                                                          [re-com/input-textarea
                                                           :placeholder "Description"
                                                           :model (:description item)
                                                           :on-change #(re-frame/dispatch [:save-item id (assoc item :description %)])]]]
                                              [re-com/v-box
                                               :gap "10px"
                                               :children (concat (if (= id first-item-id)
                                                                   [[re-com/md-circle-icon-button
                                                                     :md-icon-name "zmdi-plus"
                                                                     :on-click #(re-frame/dispatch [:add-item])]]
                                                                   [])
                                                                 [[re-com/md-circle-icon-button
                                                                   :md-icon-name "zmdi-minus"
                                                                   :on-click #(re-frame/dispatch [:remove-item id])]])]]])
                                visible-items)))])))

(defn main-panel []
  [re-com/v-box
   :padding "20px 40px 20px 40px"
   :height "100%"
   :children [[title]
              [counter-panel]
              [todo-panel]
              ]])
