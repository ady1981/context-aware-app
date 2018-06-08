(ns context-aware-app.user.db
  (:require
    [clojure.core.match :refer [match]]
    [crypto.random :as random]

    [context-aware-app.user.entry :as entry]
    [context-aware-app.util :as util]))


(def ^:private db
  (atom
    {:db (->> [(entry/create "1" "Vasya" 100)
               (entry/create "2" "Petya" 100)]
              (util/index-by :id))
     :stashes {}}))


(defn find-by-id [id]
  (if-let [user (get-in @db [:db id])]
    [:ok user]
    [:error {:type :invalid_user, :details :not_found}]))


(defn enumerate []
  [:ok (->> @db :db vals (sort-by :id))])


(defn- with-error-stash [db stash [type details]]
  (assoc db :stashes {stash [:error {:type type, :details details}]}))


(defn update-by-id! [id update-fn]
  (let [stash (random/hex 16)
        update-fn-safe (fn [user]
                         (try
                           (update-fn user)
                           (catch Throwable e
                             (util/exception-error e))))
        updated_db (swap! db
                          (fn [db]
                            (if-let [user (get-in db [:db id])]

                              (let [result (update-fn-safe user)
                                    updated_db (assoc db :stashes {stash result})]
                                (match result

                                       [:ok (updated_user :guard map?)]
                                       (let [updated_user (assoc updated_user :id id)]
                                         (assoc-in updated_db [:db id] updated_user))

                                       [:error _reason]
                                       updated_db

                                       [:ok _]
                                       (with-error-stash db stash [:invalid_update_result :not_map])

                                       any
                                       (with-error-stash db stash [:invalid_update_result {:provided any}])))

                              (with-error-stash db stash [:invalid_user :not_found]))))]

    (match (get-in updated_db [:stashes stash])

           [:ok _]
           [:ok (get-in updated_db [:db id])]

           [:error reason]
           [:error reason])))
