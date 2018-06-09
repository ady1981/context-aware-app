(ns context-aware-app.processing
  (:require
    [clojure.core.match :refer [match]]

    [context-aware-app.user.db :as user-db]
    [context-aware-app.lot.db :as lot-db]

    [context-aware-app.util :as util]))


;; [:ok updated_user] | [:error reason]
(defn buy-lot [user_id lot_id]
  (let [with-lot-fn (partial
                      util/with-result-or-error
                      #(lot-db/find-by-id lot_id)
                      :lot)

        buy-lot-fn (fn [{:keys [lot] :as ctx}]
                     (util/with-result-or-error
                       #(user-db/update-by-id!
                          user_id
                          (fn [user]
                            (let [wallet_v (get-in user [:wallet :value])
                                  price_v (get-in lot [:price :value])]
                              (if (>= wallet_v price_v)
                                (let [updated_user (-> user
                                                       (update-in [:wallet :value]
                                                                  -
                                                                  price_v)
                                                       (update-in [:lots]
                                                                  conj
                                                                  {:lot_id lot_id
                                                                   :price price_v}))]
                                  [:ok updated_user])
                                [:error {:type :invalid_wallet_value
                                         :details {:code :not_enough
                                                   :provided wallet_v
                                                   :required price_v}}]))))
                       :user
                       ctx))

        fs [with-lot-fn
            buy-lot-fn]]

    (match (util/until-first-error fs {})

           [:ok {:user updated_user}]
           [:ok updated_user]

           [:error reason]
           [:error reason])))