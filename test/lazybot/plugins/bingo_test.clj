(ns lazybot.plugins.bingo-test
  (:use [lazybot.core]
        [lazybot.registry]
        [lazybot.plugins.bingo] :reload-all)
  (:use [clojure.test]))

(deftest test-basic
  (let [bot (ref {})]
    (load-this-plugin nil bot)
    (defn mock-send-message [com-m s & args]
      (is (= "Found hi on bennett's buzzword bingo card" s))
      true)
    (binding [lazybot.registry/send-message mock-send-message]
      (let [hook-resp (pull-hooks bot :on-message)]
        (is (nil? ((first hook-resp) {:message "hi"
                                       :nick "bennett"})))))))