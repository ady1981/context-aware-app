(ns context-aware-app.processing-repl
  (:require
    [context-aware-app.user.db :as user-db]
    [context-aware-app.user.entry :as user-entry]

    [context-aware-app.lot.db :as lot-db]

    [context-aware-app.processing :refer :all]
    [context-aware-app.util :as util]))

#_(user-db/enumerate)
#_(user-db/find-by-id "1")

#_(lot-db/enumerate)
#_(lot-db/find-by-id "1")

;;;;

#_(user-db/update-by-id! "1" (fn [user] [:ok (update-in user [:wallet :value] - 10)]))
#_(user-db/update-by-id! "1" (fn [user] :xxx))
#_(user-db/update-by-id! "1" (fn [user] (/ 1 0)))

;;;;

#_(buy-lot "1" "1")
#_(buy-lot "1" "2")
#_(buy-lot "1" "3")
#_(buy-lot "4" "1")
#_(buy-lot "1" "4")


;;;;
#_(try
  (util/throw-exception :invalid_xxx :invalid_value)
  (catch Throwable e
    (util/exception-error e)))

;;;;

(context-aware-app.user.db/enumerate)

