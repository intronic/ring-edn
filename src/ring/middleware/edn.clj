(ns ring.middleware.edn
  (:require clojure.edn))

(defn- edn-request?
  [req]
  (if-let [^String type (:content-type req)]
    (not (empty? (re-find #"^application/(vnd.+)?edn" type)))))

(defprotocol EdnRead
  (-read-edn [this opts]))

(extend-type String
  EdnRead
  (-read-edn [s opts]
    (clojure.edn/read-string opts s)))

(extend-type java.io.InputStream
  EdnRead
  (-read-edn [is opts]
    (clojure.edn/read
     (merge {:eof nil} opts)
     (java.io.PushbackReader.
      (java.io.InputStreamReader.
       is "UTF-8")))))

(defn wrap-edn-params
  [handler & [read-opts]]
  (fn [req]
    (if-let [body (and (edn-request? req) (:body req))]
      (let [edn-params (binding [*read-eval* false] (-read-edn body read-opts))
            req* (assoc req
                   :edn-params edn-params
                   :params (merge (if (map? edn-params) edn-params) (:params req)))]
        (handler req*))
      (handler req))))
