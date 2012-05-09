(ns lazybot.plugins.bingo
  (:use [lazybot registry info]))

(def words (ref #{"hi" "bye"}))
(def remaining-words (ref #{"hi" "bye"}))

(defplugin
  (:hook
    :on-message
    (fn [{:keys [nick message] :as com-m}]
      (println "message is " message)
      (doseq [word @words]
        (println "word is " word)
        (if-let [match (re-find (re-pattern word) message)]
          (do
            (println "match value is " match)
            (println "remainign words before " @remaining-words)
            (dosync
              (ref-set remaining-words (into #{} (remove #{word} @remaining-words))))
            (println "remainign words after " @remaining-words)
            (if (empty? @remaining-words)
              (send-message com-m (str nick "!!! You just got BINGO!!! You should probably read a book and learn some new words"))
              (send-message com-m (str "Found " word " on " nick "'s buzzword bingo card")))))))))


