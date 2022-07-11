# tennis
# This project controls tennis game scores and shows feedback on how the score looks currently

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
(tennis-game :player1) ;; add game to player1 
(tennis-game :player2) ;; add game to player2
(tennis-game :random)  ;; add game to random player
(tennis-game :reset)   ;; reset game
```
