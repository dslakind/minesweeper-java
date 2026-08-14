# Minesweeper Design Notes
## David Lakind

## Implement in this order:
SquareState (DONE)
Square (DONE)
MineField (Done)
MineFieldReader (Done)
SolutionFormatter  (Done)
MinesweeperApplication  (Done)
GameStatus and Game later

### Potential Objects (Nouns)
- Minesweeper
- Game
- Field
- Mine
- Square
    - Safe square ```"."```
    - Mine Square ```"*"```


### attributes / properties
- M X N field with M rows and N columns
- Location
- number in a square
- adjacent squares
- hint numbers

### Behaviors
- play
- get input for ```n``` and ```m``` the number of rows and cols
- display field
- find mine
- find all mines
- n = m = 0 represents the end of input and should not be processed.

### Constraints
- 0 < n, m ≤ 100
- 