(ns context-aware-app.util
  (:require
    [clojure.core.match :refer [match]])

  (:import
    [clojure.lang IExceptionInfo]))


(defn index-by [k s]
  (into {} (for [e s] [(k e) e])))

;; [:error {:type type, :details details, :class class_name}]
(defn exception-error [^Throwable e]
  (let [details (ex-data e)
        [type details] (cond

                         (and (instance? IExceptionInfo e) (map? details))
                         [(get details :type :exception) details]

                         (instance? IExceptionInfo e)
                         [:exception details]

                         :default
                         [:exception {:message (str e)}])]
    [:error {:type type, :details details, :class (.getName (class e))}]))


;; [:ok updated_context] | [:error reason]
(defn with-result-or-error [f k context]
  {:pre [(fn? f)]}
  (match (f)

         [:ok result]
         [:ok (assoc context k result)]

         [:error reason]
         [:error reason]))


;; [:ok updated-context] | [:error reason]
(defn until-first-error
  "Do apply fs until first error"
  [fs context & [ on-result-fn ]]
  (let [on-result-fn (fn [c]
                       (when on-result-fn
                         (on-result-fn c)))]
    (if (not-empty fs)

      (let [f       (first fs)
            rest_fs (rest fs)
            result  (try
                      (f context)

                      (catch Throwable e
                        (exception-error e)))]
        (match result

               [:ok updated_context]
               (until-first-error rest_fs updated_context on-result-fn)

               [:error reason]
               (do
                 (on-result-fn context)
                 [:error reason])))

      (do
        (on-result-fn context)
        [:ok context]))))