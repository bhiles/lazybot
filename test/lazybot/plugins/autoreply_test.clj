(ns lazybot.plugins.autoreply-test
  (:use [lazybot.core]
        [lazybot.registry]
        [lazybot.plugins.autoreply] :reload-all)
  (:use [clojure.test]))

(def config {"irc.freenode.net"
             {:autoreply
              {:autoreplies
               {"#tempchan"
                {#".*(https?://)richhickey(.github.com/\S*).*"
                 "Nooooo, that's so out of date! Please see instead $1clojure$2 and try to stop linking to rich's repo."}}}}})

(defn assert-valid
  "Asserts the plugin responds correctly."
  ([message expected-response] (assert-valid "irc.freenode.net" "#tempchan" message expected-response))
  ([irc-name channel message expected-response]
    (let [bot (ref {})
          com (ref {:server irc-name})]
      (load-this-plugin nil bot)
      (dosync (alter bot assoc :config config))
      (defn mock-send-message [com-m s & args]
        (is (= expected-response s))
        true)
      (binding [lazybot.registry/send-message mock-send-message]
        (let [hook-resp (pull-hooks bot :on-message)]
          (is (true? ((first hook-resp) {:bot bot
                                         :com com
                                         :channel channel
                                         :message message}))))))))

(defn assert-invalid
  "Asserts the plugin doesn't respond"
  ([message] (assert-invalid "irc.freenode.net" "#tempchan" message ))
  ([irc-name channel message]
    (let [bot (ref {})
          com (ref {:server "irc.freenode.net"})]
      (load-this-plugin nil bot)
      (dosync (alter bot assoc :config config))
      (let [hook-resp (pull-hooks bot :on-message)]
        (is (nil? ((first hook-resp) {:bot bot
                                      :com com
                                      :channel "#tempchan"
                                      :message "not a message that has a reply"})))))))

(deftest test-basic
  (let [message "https://richhickey.github.com/takemethere/zomg.html"
        expected-response "Nooooo, that's so out of date! Please see instead https://clojure.github.com/takemethere/zomg.html and try to stop linking to rich's repo."]
    (assert-valid message expected-response)))

(deftest test-no-reponse
  (assert-invalid "not a message that has a reply"))
