(ns lazybot.plugins.eball_test
  (:use [lazybot.core]
        [lazybot.registry]
        [lazybot.plugins.eball] :reload-all)
  (:use [clojure.test]
        [clojure.string :only [split trim]]))

(defn assert-valid
  "Asserts the plugin responds correctly."
  ([command] (assert-valid command "fakenick"))
  ([command nick]
    (let [bot (ref {})]
      (load-this-plugin nil bot)
      (defn mock-send-message [com-m s & args]
        (let [message-parts (split s #": ")]
          (is (= (trim (first message-parts)) nick))
          (is (true? (some #(= (trim (second message-parts)) %) responses)))
          true))
      (binding [lazybot.registry/send-message mock-send-message]
        (let [resp (respond {:bot bot :command command})]
          (is (true? (resp {:bot bot :nick nick}))))))))

(defn assert-invalid
  "Asserts the command doesn't return a response"
  [command]
  (let [bot (ref {})]
    (load-this-plugin nil bot)
    (let [resp (respond {:bot bot :command command})]
      (is (nil? (resp nil))))))

(deftest test-8ball
  (assert-valid "8ball"))

(deftest test-will
  (assert-valid "will"))

(deftest test-should
  (assert-valid "should"))

(deftest test-nick
  (assert-valid "8ball" "mynick"))

(deftest test-scentence
  (assert-invalid "will I win the lottery?"))

(deftest test-chars-next-to-value
  (assert-invalid "Pigs will fly, 8ball?"))

(deftest test-uppercase
  (assert-invalid "Should I write another test?"))

(deftest test-not-trigger
  (assert-invalid "8bal"))