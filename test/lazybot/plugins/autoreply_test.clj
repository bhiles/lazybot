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

(deftest test-basic
  (let [bot (ref {})
        com (ref {:server "irc.freenode.net"})]
    (load-this-plugin nil bot)
    (dosync (alter bot assoc :config config))
    (defn mock-send-message [com-m s & args]
      (is (= "Nooooo, that's so out of date! Please see instead https://clojure.github.com/takemethere/zomg.html and try to stop linking to rich's repo." s))
      true)
    (binding [lazybot.registry/send-message mock-send-message]
      (let [hook-resp (pull-hooks bot :on-message)]
        (is (true? ((first hook-resp) {:bot bot
                                       :com com
                                       :channel "#tempchan"
                                       :message "https://richhickey.github.com/takemethere/zomg.html"})))))))