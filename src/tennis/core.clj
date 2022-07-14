(ns tennis.core)

(def config {:max-matches 3
             :labels [0 15 30 40 "A"]})

(defn label [i]
  (nth (:labels config) i))

(def default-scores {:player1 {:point 0
                               :sets [0]
                               :match 0}
                     :player2 {:point 0
                               :sets [0]
                               :match 0}})

(def scores (atom default-scores))


(defn- adversary-advantage?
  "when adversary has advantage, then it defaults to 40-40(deuce) for both players"
  [point point-adv]
  (= (- point point-adv) -1))

(defn- inc-point-only?
  "when winning a point increases only the point score"
  [point point-adv]
  (or (< point 3)
      (= point point-adv)))


(defn- inc-set-match?
  "when winning a point leads to increase set and match scores"
  [point point-adv set set-adv]
  (and (>= (- point point-adv) 1)
       (>= set 5)
       (>= (- set set-adv) 1)))


(defn- inc-set-only?
  "when winning a point increases only set score"
  [point point-adv set set-adv]
  (and
   (not (inc-set-match? point point-adv set set-adv))
   (>= point 3)
   (>= (- point point-adv) 1)
   (or (< set 6)
       (= (- set set-adv) -1)
       (= (- set set-adv) 0))))


(defn- inc-set [sets set end-match?]
  (if end-match?
    (conj (pop sets) (inc set))
    (conj (pop sets) (inc set) 0)))


(defn increase-score
  "Increase one of the player's score
   with one argument - convenient to serve the game,
   with two arguments - useful to test specific state change."
  ([player] (increase-score player @scores))
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
       (let [end-match? (= (inc match) max-matches)]
         (-> score
             (assoc-in [player :point] (if end-match? point 0))
             (assoc-in [player :sets] (inc-set sets set end-match?))
             (assoc-in [player :match] (inc match))
             (assoc-in [adversary :point] (if end-match? point-adv 0))
             (assoc-in [adversary :sets] (if end-match? sets-adv (conj sets-adv 0)))))))))

(defn- print-total-score [new-state]
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

(defn- print-points-score [old-state new-state]
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

(add-watch scores :watcher
           (fn [_ _ old-state new-state]
             (print-total-score new-state)
             (print-points-score old-state new-state)))


(defn- reset-score [score]
  (reset! scores score))


(defn- add-point-to [player]
  (->> (increase-score player)
       (reset! scores)))


(defn tennis-set-simulator [player]
  (case player
    :reset (reset-score default-scores)
    :random-player (add-point-to (rand-nth [:player1 :player2]))
    :player-1-scored (add-point-to :player1)
    :player-2-scored (add-point-to :player2)))


(defn -main [& _]
  (let [{:keys [max-matches]} config]
    (loop [state (tennis-set-simulator :reset)]
      (let [match1 (:match (:player1 state))
            match2 (:match (:player2 state))]
        (Thread/sleep 300)
        (when (< (max match1 match2) max-matches)
          (recur (tennis-set-simulator :random-player)))))))
