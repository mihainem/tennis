# tennis
# Write the code (implementation + tests) in clojure giving ability to report score of single set of a tennis match in REPL. Code should handle state between subsequent calls of functions in REPL. After REPL is stopped state don't have to be stored anymore.

# Example interaction with REPL:
```(tennis-set-simulator)
0 : 0
(tennis-set-simulator :player-1-scored)
15 : 0
(tennis-set-simulator :player-1-scored)
30 : 0
(tennis-set-simulator :player-2-scored)
30 : 15
(tennis-set-simulator :player-1-scored)
40 : 15
(tennis-set-simulator :player-1-scored)
WON! : 15
(tennis-set-simulator)
WON! : 15
(tennis-set-simulator :reset)
0 : 0
(tennis-set-simulator)
0 : 0
```
# extended the task and so I implemented not only point and set but also: match, game.


# This project contains a simulation which controls tennis game scores and shows feedback on how the score looks currently
## Run game simulation:
```
clj -M:run
```
## Run tests:

clj -M:test


## Run the repl:
```
clj
(load-file "src/tennis/core.clj")
(use 'tennis.core)
```


Available functions:
```
(tennis-set-score :player1) ;; add game to player1 
(tennis-set-score :player2) ;; add game to player2
(tennis-set-score :random-player)  ;; add game to random player
(tennis-set-score :reset)   ;; reset game
```
