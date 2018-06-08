(ns context-aware-app.user.entry)

(defn create [id name wallet_value &  [ lots ]]
  {:id id
   :name name
   :wallet {:value wallet_value}
   :lots (or lots [])})