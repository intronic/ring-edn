# ring-edn

A [Ring](https://github.com/mmcgrana/ring) middleware that augments :params by parsing a request body as [Extensible Data Notation](https://github.com/edn-format/edn) (EDN).

The parsed EDN body is available in the request map under the :edn-params key. The EDN body can be arbitrary clojure data.

If the request body is a map then the edn-params are also merged with the :params request map. If a key occurs in both edn-params and params, the original params mapping remains since the edn-params values are recorded in their own map.

The handler accepts an optional reader options that are passed to the EDN read or read-string function.
This allows parsing tagged literals, among other things:

```
(def app
  (-> handler
      (wrap-edn-params {:readers *data-readers*})))
```

## Where

  * [Source repository](https://github.com/fogus/ring-edn) *-- patches welcomed*

## Usage

### Leiningen

In your `:dependencies` section add the following:

    [intronic/ring-edn "0.3.2"]

### Ring

*the [examples directory of the ring-edn project](http://github.com/fogus/ring-edn/tree/master/examples/awe) contains the source for the following*

To use this middleware using Ring and [Compojure](https://github.com/weavejester/compojure), create a new Leiningen project with a `project.clj` file of the form:

```clojure
(defproject awesomeness "0.0.1"
  :description "true power awesomeness"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [ring "1.0.2"]
                 [compojure "1.0.1"]
                 [fogus/ring-edn "0.2.0"]]
  :main awesome-app)
```

Next, create a file in `src` called `my_awesome_service.clj` with the following:

```clojure
(ns my-awesome-service
  (:use compojure.core)
  (:use ring.middleware.edn))

(defn generate-response [data & [status]]
  {:status (or status 200)
   :headers {"Content-Type" "application/edn"}
   :body (pr-str data)})

(defroutes handler
  (GET "/" []
       (generate-response {:hello :cleveland}))

  (PUT "/" [name]
       (generate-response {:hello name})))

(def app
  (-> handler
      wrap-edn-params))
```

And finally, create another file in `src` named `awesome_app.clj` with the following:

```clojure
(ns awesome-app
  (:use ring.adapter.jetty)
  (:require [my-awesome-service :as awe]))

(defn -main
  [& args]
  (run-jetty #'awe/app {:port 8080}))
```

### Testing

Run this app in your console with `lein run` and test with `curl` using the following:

```sh
$ curl -X GET http://localhost:8080/

#=> {:hello :cleveland}

$ curl -X PUT -H "Content-Type: application/edn" \
  -d '{:name :barnabas}' \
  http://localhost:8080/

#=> {:hello :barnabas}%
```

## Acknowledgment(s)

Thanks to [Mark McGranaghan](http://markmcgranaghan.com/) for his work on Ring and [ring-json-params](https://github.com/mmcgrana/ring-json-params) on which this project was based.

## License

Copyright (C) 2012,2013 Fogus

Distributed under the Eclipse Public License, the same as Clojure.
