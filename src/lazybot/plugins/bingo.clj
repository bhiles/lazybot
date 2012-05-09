(ns lazybot.plugins.bingo
  (:use [lazybot registry info]))

(def words (ref #{"hi" "bye"}))
(def remaining-words (ref #{"hi" "bye"}))
(def word-count (ref {}))

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
            (println "word count before " @word-count)
            (println "remainign words before " @remaining-words)
            (dosync
              (ref-set remaining-words (into #{} (remove #{word} @remaining-words)))
              (alter word-count assoc (keyword word) (+ 1 ((keyword word) @word-count 0))))
            (println "word count after " @word-count)
            (println "remainign words after " @remaining-words)
            ; if remaining-words is empty, the say ":nick Dude, you need to use some new words you just won BUZZWORD BINGO!"
            ; "Found WORD on :nick's buzzword bingo card"
            (send-message com-m (str "Found " word " on " nick "'s buzzword bingo card"))))))))


