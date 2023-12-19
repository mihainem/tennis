# Tennis Game Simulator

This Clojure project provides a tennis game simulation allowing you to report scores for a tennis match within a REPL environment. The code handles state between subsequent calls in the REPL, but the state is not stored once the REPL is stopped.

## Example Interaction with REPL:

```clojure
(tennis-set-score)
;; Output: 0 : 0

(tennis-set-score :player-1-scored)
;; Output: 15 : 0

(tennis-set-score :player-1-scored)
;; Output: 30 : 0

(tennis-set-score :player-2-scored)
;; Output: 30 : 15

(tennis-set-score :player-1-scored)
;; Output: 40 : 15

(tennis-set-score :player-1-scored)
;; Output: WON! : 15

(tennis-set-score)
;; Output: WON! : 15

(tennis-set-score :reset)
;; Output: 0 : 0

(tennis-set-score)
;; Output: 0 : 0
```

## Extended Functionality:

The initial task has been extended, and the implementation now includes not only points and sets but also match and game tracking.

## How to Run the Game Simulation:

```bash
clj -M:run
```

How to Run Tests:
```bash
clj -M:test
```
How to Run the REPL:
```bash
clj
(load-file "src/tennis/core.clj")
(use 'tennis.core)
```
Available Functions in REPL:
(tennis-set-score :player-1-scored): Add a game to player 1.
(tennis-set-score :player-2-scored): Add a game to player 2.
(tennis-set-score :random-player): Add a game to a random player.
(tennis-set-score :reset): Reset the game.
