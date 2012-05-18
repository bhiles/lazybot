(ns lazybot.plugins.bingo-test
  (:use [lazybot.core]
        [lazybot.registry]
        [lazybot.plugins.bingo] :reload-all)
  (:use [clojure.test]))

;(deftest test-basic
;  (let [bot (ref {})]
;    (load-this-plugin nil bot)
;    (defn mock-send-message [com-m s & args]
;      (is (= "Found hi on bennett's buzzword bingo card" s))
;      true)
;    (binding [lazybot.registry/send-message mock-send-message]
;      (let [hook-resp (pull-hooks bot :on-message)]
;        (is (nil? ((first hook-resp) {:message "hi there"
;                                      :nick "bennett"})))))))

(deftest test-all-words
  (let [bot (ref {})]
    (load-this-plugin nil bot)
    (defn mock-send-message-first [com-m s & args]
      (is (= "Found hi on bennett's buzzword bingo card" s))
      true)
    (binding [lazybot.registry/send-message mock-send-message-first]
      (let [hook-resp (pull-hooks bot :on-message)]
        (is (nil? ((first hook-resp) {:message "hi there"
                                      :nick "bennett"})))))
    (defn mock-send-message-bingo [com-m s & args]
      (is (= "bennett!!! You just got BINGO!!! You should probably read a book and learn some new words" s))
      true)
    (binding [lazybot.registry/send-message mock-send-message-bingo]
      (let [hook-resp (pull-hooks bot :on-message)]
        (is (nil? ((first hook-resp) {:message "bye there"
                                      :nick "bennett"})))))))

(deftest test-after-bingo
  (test-all-words)
  (let [bot (ref {})]
    (defn mock-send-message-first [com-m s & args]
      (is (= "Found hi on bennett's buzzword bingo card" s))
      true)
    (binding [lazybot.registry/send-message mock-send-message-first]
      (let [hook-resp (pull-hooks bot :on-message)]
        (is (nil? ((first hook-resp) {:message "hi there"
                                      :nick "bennett"})))))))

(deftest test-create-card
  (let [bot (ref {})]
    (load-this-plugin nil bot)
    (defn mock-send-message-first [com-m s & args]
      (is (= "New bingo card created" s))
      true)
    (binding [lazybot.registry/send-message mock-send-message-first]
      (let [resp (respond {:bot bot :command "create-card" })]
        (is (true? (resp {:bot bot :args ["a" "b"]})))))))

(deftest test-reset-card
  (let [bot (ref {})]
    (load-this-plugin nil bot)
    (defn mock-send-message-first [com-m s & args]
      (is (= "Bingo card has been reset" s))
      true)
    (binding [lazybot.registry/send-message mock-send-message-first]
      (let [resp (respond {:bot bot :command "reset-card" })]
        (is (true? (resp {})))))))