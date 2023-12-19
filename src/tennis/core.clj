(ns tennis.core)

(def config {:max-matches 3
             :labels [0 15 30 40 "A"]})


(def default-scores {:player1 {:point 0
                               :sets [0]
                               :match 0}
                     :player2 {:point 0
                               :sets [0]
                               :match 0}})

(def scores-atom (atom default-scores))


(defn label
  "get proper label for the number of points a player has"
  [i]
  (nth (:labels config) i))


(defn- adversary-advantage?
  "when adversary has advantage, then it defaults to 40-40(deuce) for both players"
  [point point-adv]
  (and (>= point 3)
       (= (- point point-adv) -1)))

(defn- inc-point-only?
  "when winning a point increases only the point score"
  [point point-adv]
  (or (< point 3)
      (= point point-adv)))

(defn- inc-set-only?
  "when winning a point increases only set score"
  [point point-adv set set-adv]
  (and
   (or  (< (- point point-adv) 1)
        (< set 5)
        (< (- set set-adv) 1))
   (>= point 3)
   (or (< set 6)
       (<= (abs (- set set-adv)) 1))))


(defn- inc-set-match?
  "when winning a point leads to increase set and match scores"
  [point point-adv set set-adv]
  (and (>= (- point point-adv) 1)
       (>= set 5)
       (>= (- set set-adv) 1)))


(defn- inc-set
  "when increasing the set does not end the game 
   add a new set starting from 0"
  [sets set end-game?]
  (if (not end-game?)
    (conj (pop sets) (inc set) 0)
    (conj (pop sets) (inc set))))


(defn increase-score
  "increase one of the player's score
   with one argument - convenient to serve the game,
   with two arguments - useful to test specific state change."
  ([player] (increase-score player @scores-atom))
  ([player score]
   (let [{:keys [point sets match]} (player score)
         [adversary {point-adv :point
                     sets-adv :sets
                     match-adv :match}] (->> (remove #(= (key %) player) score)
                                             (first))
         set (last sets)
         set-adv (last sets-adv)
         {:keys [max-matches]} config]
     (cond
       (>= (max match match-adv) max-matches)
       score

       ;;when adversary has advantage, then it defaults to 40-40(deuce) for both players
       (adversary-advantage? point point-adv)
       (-> score
           (assoc-in [player :point] 3)
           (assoc-in [adversary :point] 3))

       ;;when winning a point increases only the point score 
       (inc-point-only? point point-adv)
       (assoc-in score [player :point] (inc point))

       ;;when winning a point increases only set score 
       (inc-set-only? point point-adv set set-adv)
       (-> score
           (assoc-in [player :point] 0)
           (assoc-in [player :sets] (conj (pop sets) (inc set)))
           (assoc-in [adversary :point] 0))

       ;;when winning a point increases set and match scores
       (inc-set-match? point point-adv set set-adv)
       (let [end-game? (= (inc match) max-matches)]
         (-> score
             (assoc-in [player :point] (if end-game? point 0))
             (assoc-in [player :sets] (inc-set sets set end-game?))
             (assoc-in [player :match] (inc match))
             (assoc-in [adversary :point] (if end-game? point-adv 0))
             (assoc-in [adversary :sets] (if end-game? sets-adv (conj sets-adv 0)))))))))

(defn- print-total-score
  "print all scores: points, sets, matches"
  [new-state]
  (let [{:keys [max-matches]} config
        {point1 :point sets1 :sets match1 :match} (:player1 new-state)
        {point2 :point sets2 :sets match2 :match} (:player2 new-state)]
    (if (>= (max match1 match2) max-matches)
      (println (if (= match1 max-matches)
                 "PLAYER 1"
                 "PLAYER 2") "HAS WON! "
               match1 "-" match2 "\n
              ... to restart the game use command: clj -M:run\n
              ... or to reset the game use command (tennis-set-score :reset)")
      (do
        (println "PLAYER 1   |   PLAYER 2")
        (println "point:  " (label point1) " | "
                 (label point2))
        (println "Set:   " (last sets1) " | " (last sets2))
        (println "Match: " match1 " | " match2)
        (println "All sets: " (map vector sets1 sets2))))))

(defn- print-points-score
  "print only points scored in a set and mark the 'WON!' case"
  [old-state new-state]
  (let [{old-point1 :point} (:player1 old-state)
        {old-point2 :point} (:player2 old-state)
        {point1 :point} (:player1 new-state)
        {point2 :point} (:player2 new-state)]
    (if (and (= (max point1 point2) 0)
             (> (max old-point1 old-point2) 0))
      (if (> old-point1 old-point2)
        (println (str "WON! : " (label old-point2)))
        (println (str (label old-point1) " : WON!")))
      (println (label point1) " : " (label point2)))))

(add-watch scores-atom :watcher
           (fn [_ _ old-state new-state]
             (print-total-score new-state)
             (print-points-score old-state new-state)))


(defn- reset-score
  "set scores atom to specific value"
  [score]
  (reset! scores-atom score))


(defn- add-point-to
  "increase score for one player and save results"
  [player]
  (->> (increase-score player)
       (reset! scores-atom)))


(defn tennis-set-simulator
  "simulator interface to control it's features"
  [player]
  (case player
    :reset (reset-score default-scores)
    :random-player (add-point-to (rand-nth [:player1 :player2]))
    :player-1-scored (add-point-to :player1)
    :player-2-scored (add-point-to :player2)))


(defn -main
  "starts a full game simulation and prints scores on each winning point"
  [& _]
  (let [{:keys [max-matches]} config]
    (loop [state (tennis-set-simulator :reset)]
      (let [match1 (:match (:player1 state))
            match2 (:match (:player2 state))]
        (Thread/sleep 300)
        (when (< (max match1 match2) max-matches)
          (recur (tennis-set-simulator :random-player)))))))
