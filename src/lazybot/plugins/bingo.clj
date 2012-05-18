(ns lazybot.plugins.bingo
  (:use [lazybot registry info]))

(def words (ref #{"hi" "bye"}))
(def remaining-words (ref #{"hi" "bye"}))

(defn reset []
  "Reset the bingo card"
  (dosync (ref-set remaining-words @words)))

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
              (do
                (send-message com-m (str nick "!!! You just got BUZZWORD BINGO!!! You should probably read a book and learn some new words"))
                (reset))
              (send-message com-m (str "Found " word " on " nick "'s buzzword bingo card"))))))))
  (:cmd
    "Creates a bingo card from a set of words"
    #{"create-card"}
    (fn [{:keys [com bot channel args] :as com-m}]
      (let [new-words (set args)]
        (dosync
          (ref-set words (into #{} new-words))
          (ref-set remaining-words @words))
        (send-message com-m "New bingo card created"))))
  (:cmd
    "Resets a bingo card so that no words have been crossed off"
    #{"reset-card"}
    (fn [com-m]
      (reset)
      (send-message com-m "Bingo card has been reset"))))