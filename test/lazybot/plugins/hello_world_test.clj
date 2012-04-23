(ns lazybot.plugins.hello_world_test
  (:use [lazybot.core]
        [lazybot.registry]
        [lazybot.plugins.hello-world] :reload-all)
  (:use [clojure.test]))

(deftest test-hello-world
  (let [bot (ref {})]
    (load-this-plugin nil bot)
    (defn mock-send-message [com-m s & args]
      (is (= "Hello, World!" s)))
    (binding [lazybot.registry/send-message mock-send-message]
      (respond {:bot bot :command "hiworld"}))))

(deftest test-hello-world-not
  (let [bot (ref {})]
    (load-this-plugin nil bot)
      (let [resp (respond {:bot bot :command "hiworl"})]
        (println "response is " resp)
        (is (= nil resp)))))