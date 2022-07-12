# tennis
# This project controls tennis game scores and shows feedback on how the score looks currently

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
(tennis-set-score :random)  ;; add game to random player
(tennis-set-score :reset)   ;; reset game
```
