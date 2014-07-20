(ns cljs-idb.core)

(println "hello world")

(def db-name "Files")
(def +store-name+ "FileStore")


(def idb-factory
  (or js/window.indexedDB
      js/window.mozIndexedDB
      js/window.webkitIndexedDB
      js/window.msIndexedDB))


(def idb-transaction
  (or js/window.IDBTransaction
      js/window.webkitIDBTransaction
      js/window.msIDBTransaction))


(def idb-keyrange
  (or js/window.IDBKeyRange
      js/window.webkitIDBKeyRange
      js/window.msIDBKeyRange))

(defprotocol IIndexedDB
  (-init [this db-name])
  (-set [this value])
  (-get [this id]))

(deftype IndexedDB [^:mutable idb-database ^:mutable state]
  IIndexedDB
  (-init [this db-name]
    (let [req (. idb-factory (open db-name 1))]
      (set! (.-onerror req)
            (fn [event]
              (println "err" event)))
      (set! (.-onsuccess req)
            (fn [event]
              (set! idb-database (-> event .-target .-result))
              (println "succ" event)))
      (set! (.-onupgradeneeded req)
            (fn [event]
              (set! idb-database (-> event .-target .-result))

              (let [obj-store (. idb-database (createObjectStore +store-name+ #js {:keyPath "id" :autoIncrement true}))]
                (println idb-database))

              (println "upg" event)))))

  (-set [this value]
    (println "set" value)
    (let [idb-transaction (. idb-database (transaction #js [+store-name+] "readwrite"))
          objectstore (. idb-transaction (objectStore +store-name+))
          ]
      (println idb-transaction)
      (println objectstore)
      (let [req (. objectstore (put value))]
        (set! (.-onsuccess req)
              (fn [event]
                (let [val (-> event .-target .-result)]
                  (println (.-id val)))
                ))
        (set! (.-onerror req)
              (fn [event]))
        )
      )
    )

  (-get [this id]
    (let [idb-transaction (. idb-database (transaction #js [+store-name+] "readwrite"))
          objectstore (. idb-transaction (objectStore +store-name+))
          ]
      (println idb-transaction)
      (println objectstore)
      (let [req (. objectstore (get id))]
        (set! (.-onsuccess req)
              (fn [event]
                (let [val (-> event .-target .-result)]
                  (def y val)
                  )))
        (set! (.-onerror req)
              (fn [event]))
        )
      )
    )
  )


(def x (IndexedDB. 1 2))
(-init x db-name)
;; (-set x {:value 1})
