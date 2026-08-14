# Minesweeper

An object-oriented Java implementation of Minesweeper with two application modes:

- A command-line solver for the classic programming challenge.
- An interactive Swing game with random minefields, region revealing, flags, and win/loss detection.

The project uses only the Java standard library and includes standalone tester classes that do not require JUnit.

## Current functionality

### Command-line solver

- Reads multiple rectangular minefields from standard input.
- Accepts fields from 1 to 100 rows and columns.
- Uses `0 0` as the input terminator.
- Calculates adjacent-mine counts for every safe square.
- Produces numbered output such as `Field #1:`.
- Inserts one blank line between field outputs.
- Validates dimensions, row lengths, and field symbols.

### Interactive game

- Generates random minefields with an exact number of mines.
- Displays a playable 9-by-9 beginner board with 10 mines.
- Reveals individual numbered squares.
- Reveals connected zero regions and their numbered boundaries.
- Supports right-click flagging and unflagging.
- Detects wins and losses.
- Prevents further model changes after the game ends.
- Displays all mine locations after a loss.
- Starts a new random game from the GUI.
- Uses status text and traditional Minesweeper-style number colors.

## Controls

| Input | Action |
| --- | --- |
| Left-click | Reveal a square |
| Right-click | Toggle a flag |
| New Game button | Start a new 9-by-9 game |

## Design

| Class | Responsibility |
| --- | --- |
| `MinesweeperApplication` | Coordinates command-line input, processing, and output. |
| `MineFieldReader` | Reads dimension pairs and field rows from a `Scanner`. |
| `SolutionFormatter` | Formats solved fields according to the challenge specification. |
| `MineField` | Owns the `Square[][]`, calculates hints, and reveals safe regions. |
| `Square` | Stores mine presence, adjacent-mine count, and local state. |
| `SquareState` | Defines the `HIDDEN`, `REVEALED`, and `FLAGGED` states. |
| `Game` | Processes player actions and determines wins and losses. |
| `GameStatus` | Defines the `IN_PROGRESS`, `WON`, and `LOST` states. |
| `MineFieldGenerator` | Creates randomized minefield layouts with unique mine positions. |
| `MinesweeperGUI` | Displays the game and translates mouse input into `Game` operations. |
| `MinesweeperGUIApplication` | Launches the Swing interface on the Event Dispatch Thread. |

The core model deliberately uses `Square` objects rather than parallel primitive arrays. This adds minor memory overhead, but it keeps mine presence, hint count, and game state together. A `Square[][]` also maps naturally to both the problem input and the GUI button grid.

The GUI does not calculate hints, reveal regions, place mines, or determine game results. It delegates player actions to `Game` and refreshes the display from the resulting model state.

## Project structure

```text
minesweeper/
├── src/
│   └── minesweeper/
│       ├── Game.java
│       ├── GameStatus.java
│       ├── MineField.java
│       ├── MineFieldGenerator.java
│       ├── MineFieldReader.java
│       ├── MinesweeperApplication.java
│       ├── MinesweeperGUI.java
│       ├── MinesweeperGUIApplication.java
│       ├── SolutionFormatter.java
│       ├── Square.java
│       └── SquareState.java
├── test/
│   └── minesweeper/
│       ├── GameTester.java
│       ├── MineFieldGeneratorTester.java
│       ├── MineFieldReaderTest.java
│       ├── MineFieldTester.java
│       └── MinesweeperApplicationTest.java
├── resources/
│   ├── sample_input.txt
│   └── sample_output.txt
├── docs/
│   ├── design_brief.md
│   └── minesweeper.md
└── out/
```

All Java files use:

```java
package minesweeper;
```

## Requirements

- JDK 8 or later
- A graphical desktop environment to run the Swing game
- A terminal or Java-capable IDE

No external libraries or build tools are required.

## Compile

Run these commands from the project root—the directory containing `src`, `test`, and `resources`.

Compile the production classes:

```bash
mkdir -p out
javac -d out src/minesweeper/*.java
```

Compile the tester classes:

```bash
javac -cp out -d out test/minesweeper/*.java
```

Compiled classes are placed under `out/minesweeper/`.

## Run the command-line solver

Run the solver with the sample input:

```bash
java -cp out minesweeper.MinesweeperApplication \
  < resources/sample_input.txt
```

The application reads from standard input, so another valid input file can be substituted after the `<` operator.

To capture and compare the output:

```bash
java -cp out minesweeper.MinesweeperApplication \
  < resources/sample_input.txt \
  > /tmp/minesweeper_actual.txt

diff -u resources/sample_output.txt /tmp/minesweeper_actual.txt
```

If `diff` prints nothing, the files match exactly.

## Run the interactive game

Launch the Swing interface:

```bash
java -cp out minesweeper.MinesweeperGUIApplication
```

The launcher creates the GUI on Swing's Event Dispatch Thread. The initial version uses the beginner configuration of 9 rows, 9 columns, and 10 mines.

## Run the tests

Run each standalone tester from the project root:

```bash
java -cp out minesweeper.MineFieldTester
java -cp out minesweeper.MineFieldReaderTest
java -cp out minesweeper.MinesweeperApplicationTest
java -cp out minesweeper.GameTester
java -cp out minesweeper.MineFieldGeneratorTester
```

Each tester throws an `AssertionError` when a check fails and prints a success message when all checks pass.

The tests cover:

- Field construction and validation
- Mine detection and adjacent-mine counts
- Coordinate validation
- Iterative zero-region revealing
- Flag behavior
- Win and loss transitions
- Actions after game completion
- Random field dimensions and exact mine counts
- Rectangular fields
- Repeatable generation with a seeded `Random`
- Command-line parsing, formatting, and application output

## Command-line input format

Each field begins with two integers:

```text
rows columns
```

The next `rows` lines contain exactly `columns` characters:

- `*` represents a mine.
- `.` represents a safe square.

The pair `0 0` ends the input and is not processed as a field.

Example:

```text
4 4
*...
....
.*..
....
0 0
```

## Command-line output format

Each field is numbered beginning with 1. Mines remain `*`; every safe square is replaced by its adjacent-mine count.

```text
Field #1:
*100
2210
1*10
1110
```

When several fields are processed, one empty line appears between their outputs.

## Planned improvements

- Add selectable beginner, intermediate, and expert difficulties.
- Add a mine counter and elapsed-time display.
- Guarantee that the first revealed square is safe.
- Improve mine and flag graphics with reliable image icons.
- Highlight the selected mine and incorrect flags after a loss.
- Preserve square proportions when resizing the window.
- Add keyboard accessibility and clearer focus behavior.
- Add automated tests for GUI-independent presentation decisions where practical.

Challenge-output formatting remains separate from the interactive display. `SolutionFormatter` always shows the complete solved field, while `MinesweeperGUI` respects each square's state and the current `GameStatus`.

## Generated files

Compiled `.class` files belong in `out/` and should not be committed to source control. A suitable `.gitignore` entry is:

```gitignore
out/
*.class
```
