(ns tennis.core)

(def config {:max-matches 3
             :labels [0 15 30 40 "A"]})


(def default-scores {:player1 [0 [0] 0]
                     :player2 [0 [0] 0]})

(def scores (atom default-scores))


(defn inc-game-only?
  "when winning a game increases only the game score"
  [game game-adv]
  (or (< game 3)
      (= game game-adv)))


(defn inc-set-match?
  "when winning a game leads to increase set and match scores"
  [game game-adv set set-adv]
  (and (>= (- game game-adv) 1)
       (>= set 5)
       (>= (- set set-adv) 1)))


(defn inc-set-only?
  "when winning a game increases only set score"
  [game game-adv set set-adv]
  (and (>= game 3)
       (>= (- game game-adv) 1)
       (or (< set 6)
           (= (- set set-adv) -1)
           (= (- set set-adv) 0))))


(defn adversary-advantage?
  "when adversary has advantage, then it defaults to 40-40(deuce) for both players"
  [game game-adv]
  (= (- game game-adv) -1))


(defn increase-score
  ([player] (increase-score player @scores))
  ([player score]
   (let [[game sets match] (player score)
         [adversary [game-adv sets-adv match-adv]] (->> (remove #(= (key %) player) score)
                                                        (first))
         set (last sets)
         set-adv (last sets-adv)
         {:keys [max-matches]} config]
     (cond
       (>= (max match match-adv) max-matches)
       score

         ;;when winning a game increases only the game score 
       (inc-game-only? game game-adv)
       {player [(inc game) sets match]
        adversary [game-adv sets-adv match-adv]}

         ;;when winning a game increases set and match scores
       (inc-set-match? game game-adv set set-adv)
       (let [end-match? (= (inc match) max-matches)]
         (if end-match?
           {player [game (conj (pop sets) (inc set)) (inc match)]
            adversary [game-adv (conj (pop sets-adv) set-adv) match-adv]}

           {player [0 (conj (pop sets) (inc set) 0) (inc match)]
            adversary [0 (conj (pop sets-adv) set-adv 0) match-adv]}))


         ;;when winning a game increases only set score 
       (inc-set-only? game game-adv set set-adv)
       {player [0 (conj (pop sets) (inc set)) match]
        adversary [0 sets-adv match-adv]}

         ;;when adversary has advantage, then it defaults to 40-40(deuce) for both players
       (adversary-advantage? game game-adv)
       {player [3 sets match]
        adversary [3 sets-adv match-adv]}))))

(defn print-score [new-state]
  (let [{:keys [labels max-matches]} config
        [game1 sets1 match1] (:player1 new-state)
        [game2 sets2 match2] (:player2 new-state)]
    (if (>= (max match1 match2) max-matches)
      (println (if (= match1 max-matches)
                 "PLAYER 1"
                 "PLAYER 2") "HAS WON! "
               match1 "-" match2 "\n
              ... to restart the game use command: clj -M:run\n
              ... or to reset the game use command (tennis-set-score :reset)")
      (do
        (println "PLAYER 1   |   PLAYER 2")
        (println "Game:  " (nth labels game1) " | "
                 (nth labels game2))
        (println "Set:   " (last sets1) " | " (last sets2))
        (println "Match: " match1 " | " match2)
        (println "All sets: " (map vector sets1 sets2))))))

(add-watch scores :watcher
           (fn [_ _ _ new-state]
             (print-score new-state)))


(defn reset-score [score]
  (reset! scores score))


(defn add-game-to [player]
  (->> (increase-score player)
       (reset! scores)))


(defn tennis-set-simulator [player]
  (case player
    :reset (reset-score default-scores)
    :random (add-game-to (rand-nth [:player1 :player2]))
    :player-1-scored (add-game-to :player1)
    :player-2-scored (add-game-to :player2)))


(defn -main [& _]
  (let [{:keys [max-matches]} config]
    (loop [state (tennis-set-simulator :reset)]
      (let [match1 (last (:player1 state))
            match2 (last (:player2 state))]
        (Thread/sleep 500)
        (when (< (max match1 match2) max-matches)
          (recur (tennis-set-simulator :random)))))))
