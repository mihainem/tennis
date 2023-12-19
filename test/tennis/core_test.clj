(ns tennis.core-test
  (:require [clojure.test :refer [deftest testing is]]
            [tennis.core :refer [increase-score config]]))

(defn- vectors->map [player1vec player2vec]
  (let [[game1 sets1 match1] player1vec
        [game2 sets2 match2] player2vec]
    {:player1 {:point game1 :sets sets1 :match match1}
     :player2 {:point game2 :sets sets2 :match match2}}))



(deftest test-scores
  (testing "increase simple game"
    (is (= (increase-score :player1 (vectors->map [0 [5 6] 0] [1 [7 7] 1])) (vectors->map [1 [5 6] 0] [1 [7 7] 1])))
    (is (= (increase-score :player1 (vectors->map [2 [0] 0] [2 [0] 0])) (vectors->map [3 [0] 0] [2 [0] 0])) true)
    (is (= (increase-score :player1 (vectors->map [2 [0] 0] [2 [0] 0])) (vectors->map [3 [0] 0] [2 [0] 0])) true)
    (is (= (increase-score :player1 (vectors->map [0 [1] 0] [0 [0] 0])) (vectors->map [1 [1] 0] [0 [0] 0])) true))
  (testing "increase game and set"
    (is (= (increase-score :player2 (vectors->map [3 [2] 0] [4 [3] 0])) (vectors->map [0 [2] 0] [0 [4] 0])))
    (is (= (increase-score :player1 (vectors->map [3 [0] 0] [2 [0] 0])) (vectors->map [0 [1] 0] [0 [0] 0])) true)
    (is (= (increase-score :player1 (vectors->map [3 [0] 0] [0 [0] 0])) (vectors->map [0 [1] 0] [0 [0] 0])) true)
    (is (= (increase-score :player1 (vectors->map [4 [0] 0] [3 [0] 0])) (vectors->map [0 [1] 0] [0 [0] 0])) true))
  (testing "increase on opponent having advantage"
    (is (= (increase-score :player1 (vectors->map [3 [0] 0] [4 [0] 0])) (vectors->map [3 [0] 0] [3 [0] 0])) true))
  (testing "give advantage to player"
    (is (= (increase-score :player1 (vectors->map [3 [5] 0] [3 [4] 0])) (vectors->map [4 [5] 0] [3 [4] 0])) true))
  (testing "increase player's set and match"
    (is (= (increase-score :player1 (vectors->map [3 [5] 0] [0 [4] 0])) (vectors->map [0 [6 0] 1] [0 [4 0] 0])) true)
    (is (= (increase-score :player1 (vectors->map [3 [6] 0] [0 [5] 0])) (vectors->map [0 [7 0] 1] [0 [5 0] 0])) true)
    (is (= (increase-score :player1 (vectors->map [3 [7] 0] [1 [6] 0])) (vectors->map [0 [8 0] 1] [0 [6 0] 0])) true)
    (is (= (increase-score :player1 (vectors->map [3 [3 6] 0] [0 [6 7] 1])) (vectors->map [0 [3 7] 0] [0 [6 7] 1])) true))
  (testing "increase final game/ Player has won / Game Set and Match"
    (is (= (:max-matches config) 3) true)
    (is (= (increase-score :player1 (vectors->map [3 [6 6 5] 2] [1 [3 2 4] 0])) (vectors->map [3 [6 6 6] 3] [1 [3 2 4] 0])) true))
  (testing "increase only player's set when score is tight"
    (is (= (increase-score :player1 (vectors->map [3 [5] 0] [0 [5] 0])) (vectors->map [0 [6] 0] [0 [5] 0])) true)
    (is (= (increase-score :player1 (vectors->map [3 [6] 2] [2 [6] 0])) (vectors->map [0 [7] 2] [0 [6] 0])) true)))

