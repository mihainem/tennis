(ns tennis.core-test
  (:require [clojure.test :refer [deftest testing is]]
            [tennis.core :refer [increase-score config]]))


(deftest test-scores
  (testing "increase simple game"
    (is (= (increase-score :player1 {:player1 [2 [0] 0] :player2 [2 [0] 0]}) {:player1 [3 [0] 0] :player2 [2 [0] 0]}) true)
    (is (= (increase-score :player1 {:player1 [0 [1] 0] :player2 [0 [0] 0]}) {:player1 [1 [1] 0] :player2 [0 [0] 0]}) true))
  (testing "increase game and set"
    (is (= (increase-score :player1 {:player1 [3 [0] 0] :player2 [2 [0] 0]}) {:player1 [0 [1] 0] :player2 [0 [0] 0]}) true)
    (is (= (increase-score :player1 {:player1 [3 [0] 0] :player2 [0 [0] 0]}) {:player1 [0 [1] 0] :player2 [0 [0] 0]}) true)
    (is (= (increase-score :player1 {:player1 [4 [0] 0] :player2 [3 [0] 0]}) {:player1 [0 [1] 0] :player2 [0 [0] 0]}) true))
  (testing "increase on opponent having advantage"
    (is (= (increase-score :player1 {:player1 [3 [0] 0] :player2 [4 [0] 0]}) {:player1 [3 [0] 0] :player2 [3 [0] 0]}) true))
  (testing "give advantage to player"
    (is (= (increase-score :player1 {:player1 [3 [5] 0] :player2 [3 [4] 0]}) {:player1 [4 [5] 0] :player2 [3 [4] 0]}) true))
  (testing "increase player's set and match"
    (is (= (increase-score :player1 {:player1 [3 [5] 0] :player2 [0 [4] 0]}) {:player1 [0 [6 0] 1] :player2 [0 [4 0] 0]}) true)
    (is (= (increase-score :player1 {:player1 [3 [6] 0] :player2 [0 [5] 0]}) {:player1 [0 [7 0] 1] :player2 [0 [5 0] 0]}) true)
    (is (= (increase-score :player1 {:player1 [3 [7] 0] :player2 [1 [6] 0]}) {:player1 [0 [8 0] 1] :player2 [0 [6 0] 0]}) true)
    (is (= (increase-score :player1 {:player1 [3 [3 6] 0] :player2 [0 [6 7] 1]}) {:player1 [0 [3 7] 0] :player2 [0 [6 7] 1]}) true))
  (testing "increase final game/ Player has won / Game Set and Match"
    (is (= (:max-matches config) 3) true)
    (is (= (increase-score :player1 {:player1 [3 [6 6 5] 2] :player2 [1 [3 2 4] 0]}) {:player1 [3 [6 6 6] 3] :player2 [1 [3 2 4] 0]}) true))
  (testing "increase only player's set when score is tight"
    (is (= (increase-score :player1 {:player1 [3 [5] 0] :player2 [0 [5] 0]}) {:player1 [0 [6] 0] :player2 [0 [5] 0]}) true)
    (is (= (increase-score :player1 {:player1 [3 [6] 2] :player2 [2 [6] 0]}) {:player1 [0 [7] 2] :player2 [0 [6] 0]}) true)))

