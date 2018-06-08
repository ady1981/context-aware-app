(ns context-aware-app.lot.entry)

(defn create [lot_id name price_value]
  {:id lot_id
   :name    name
   :price   {:value price_value}})