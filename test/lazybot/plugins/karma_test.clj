(ns lazybot.plugins.karma-test
  (:use [lazybot.core]
        [lazybot.registry]
        [lazybot.plugins.karma] :reload-all)
  (:use [clojure.test])
  (:use [somnium.congomongo :only [fetch-one destroy!]]))

(defn fetch-record
  "Fetch a record from storage"
  [nick server channel]
  (fetch-one :karma :where {:nick nick :server server :channel channel}))

(defn delete-record!
  "Delete record from storage"
  [nick server channel]
  (if-let [karma-record (fetch-record nick server channel)]
    (destroy! :karma karma-record)))

(defn assert-value
  "Assert the karma after a command. Clear the existing value prior to running."
  ([nick command expected-value] (assert-value "irc.freenode.net" "#tempchan" nick command expected-value))
  ([irc-name channel nick command expected-value]
    (let [bot (ref {})
          com (ref {:server irc-name})]
      (initiate-mongo)
      (delete-record! nick irc-name channel)
      (load-this-plugin nil bot)
      (defn mock-send-message [com-m s & args]
        (is (= expected-value s))
        true)
      (binding [lazybot.registry/send-message mock-send-message]
        (let [resp (respond {:bot bot :command command })]
          (is (true? (resp {:com com :bot bot :channel channel :args [nick]}))))))))

(deftest test-inc
  (assert-value "test-user-inc" "inc" "1"))

(deftest test-dec
  (assert-value "test-user-dec" "dec" "-1"))

(deftest test-invalid-command
  (assert-value "test-user-inv" "invalid-command" "You want me to leave karma the same? Fine, I will."))
