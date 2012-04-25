(ns lazybot.plugins.hello-world-test
  (:use [lazybot.core]
        [lazybot.registry]
        [lazybot.plugins.hello-world] :reload-all)
  (:use [clojure.test]))

(deftest test-basic
  (let [bot (ref {})]
    (load-this-plugin nil bot)
    (defn mock-send-message [com-m s & args]
      (is (= "Hello, World!" s))
      true)
    (binding [lazybot.registry/send-message mock-send-message]
      (let [resp (respond {:bot bot :command "hiworld"})]
        (is (true? (resp nil)))))))

(deftest test-not-full-trigger
  (let [bot (ref {})]
    (load-this-plugin nil bot)
      (let [resp (respond {:bot bot :command "hiworl"})]
        (is (nil? (resp nil))))))

(deftest test-more-than-trigger
  (let [bot (ref {})]
    (load-this-plugin nil bot)
    (let [resp (respond {:bot bot :command "hiworlds"})]
      (is (nil? (resp nil))))))