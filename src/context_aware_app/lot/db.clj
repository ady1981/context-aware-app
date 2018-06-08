(ns context-aware-app.lot.db
  (:require
    [context-aware-app.lot.entry :as entry]
    [context-aware-app.util :as util]))

(def db
  (atom {:db (->> [(entry/create "1" "Apple" 10)
                   (entry/create "2" "Banana" 20)
                   (entry/create "3" "Nuts" 30)]
                  (util/index-by :id))}))


(defn find-by-id [lot_id]
  (if-let [lot (get-in @db [:db lot_id])]
    [:ok lot]
    [:error {:type :invalid_lot, :details :not_found}]))


(defn enumerate []
  [:ok (->> @db :db vals (sort-by :id))])

