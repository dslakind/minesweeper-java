# Minesweeper

An object-oriented Java implementation of Minesweeper with two application modes:

* A command-line solver for the classic programming challenge.
* An interactive Swing game with random minefields, selectable difficulties, region revealing, flags, a remaining-mine counter, an elapsed timer, and win/loss detection.

The application uses only the Java standard library. It includes standalone tester classes that do not require JUnit.

## Current functionality

### Command-line solver

* Reads multiple rectangular minefields from standard input.
* Accepts fields from 1 to 100 rows and columns.
* Uses `0 0` as the input terminator.
* Calculates adjacent-mine counts for every safe square.
* Produces numbered output such as `Field #1:`.
* Inserts one blank line between field outputs.
* Validates dimensions, row lengths, and field symbols.

### Interactive game

* Generates random minefields with an exact number of mines.
* Provides three selectable difficulty levels:

  * Beginner: 9-by-9 with 10 mines
  * Intermediate: 16-by-16 with 40 mines
  * Expert: 16-by-30 with 99 mines
* Uses the selected difficulty when starting a new game.
* Preserves the selected difficulty when restarting.
* Reveals individual numbered squares.
* Reveals connected zero regions and their numbered boundaries.
* Supports right-click flagging and unflagging.
* Displays the configured mine count minus the number of placed flags.
* Starts an elapsed timer on the first reveal.
* Stops the timer after a win or loss.
* Resets the timer when a new game starts.
* Detects wins and losses.
* Prevents further model changes after the game ends.
* Displays all mine locations after a loss.
* Uses status text and traditional Minesweeper-style number colors.

## Controls

| Input               | Action                                         |
| ------------------- | ---------------------------------------------- |
| Left-click          | Reveal a square                                |
| Right-click         | Toggle a flag                                  |
| Difficulty selector | Select the configuration for the next game     |
| New Game button     | Start a new game using the selected difficulty |

## Design

| Class                       | Responsibility                                                       |
| --------------------------- | -------------------------------------------------------------------- |
| `MinesweeperApplication`    | Coordinates command-line input, processing, and output.              |
| `MineFieldReader`           | Reads dimension pairs and field rows from a `Scanner`.               |
| `SolutionFormatter`         | Formats solved fields according to the challenge specification.      |
| `MineField`                 | Owns the `Square[][]`, calculates hints, and reveals safe regions.   |
| `Square`                    | Stores mine presence, adjacent-mine count, and local state.          |
| `SquareState`               | Defines the `HIDDEN`, `REVEALED`, and `FLAGGED` states.              |
| `Game`                      | Processes player actions and determines wins and losses.             |
| `GameStatus`                | Defines the `IN_PROGRESS`, `WON`, and `LOST` states.                 |
| `Difficulty`                | Defines the dimensions and mine count for each difficulty level.     |
| `MineFieldGenerator`        | Creates randomized minefield layouts with unique mine positions.     |
| `MinesweeperGUI`            | Displays the game and translates mouse input into `Game` operations. |
| `MinesweeperGUIApplication` | Launches the Swing interface on the Event Dispatch Thread.           |

The core model deliberately uses `Square` objects rather than parallel primitive arrays. This adds minor memory overhead, but it keeps mine presence, hint count, and game state together. A `Square[][]` also maps naturally to both the problem input and the GUI button grid.

The GUI does not calculate hints, reveal regions, place mines, or determine game results. It delegates player actions to `Game` and refreshes the display from the resulting model state.

## Project structure

```text
minesweeper/
├── build.gradle
├── settings.gradle
├── gradlew
├── gradlew.bat
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── src/
│   └── minesweeper/
│       ├── Difficulty.java
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
└── build/                       Generated by Gradle
```

All Java files use:

```java
package minesweeper;
```

## Requirements

* JDK 21
* A graphical desktop environment to run the Swing game
* A terminal or Java-capable IDE

A system-wide Gradle installation is not required. The project includes the Gradle Wrapper, which downloads and uses the configured Gradle version automatically.

The application uses only the Java standard library. The tester classes do not require JUnit.

## Build

Run commands from the project root—the directory containing `build.gradle`, `src`, `test`, and `resources`.

Compile only the production sources:

```bash
./gradlew classes
```

Run a complete build, including all tester classes:

```bash
./gradlew clean build
```

Generated classes and other build artifacts are placed under `build/`.

The first Wrapper command may download the configured Gradle distribution. Subsequent commands reuse the downloaded distribution.

## Run the command-line solver

Run the solver with the sample input:

```bash
./gradlew runConsole < resources/sample_input.txt
```

The application reads from standard input, so another valid input file can be substituted after the `<` operator.

To capture and compare the output, use Gradle’s quiet mode so its build messages are not written to the output file:

```bash
./gradlew -q runConsole \
  < resources/sample_input.txt \
  > /tmp/minesweeper_actual.txt

diff -u resources/sample_output.txt /tmp/minesweeper_actual.txt
```

If `diff` prints nothing, the files match exactly.

## Run the interactive game

Launch the Swing interface:

```bash
./gradlew run
```

The launcher creates the GUI on Swing’s Event Dispatch Thread. The selected difficulty determines the dimensions and mine count used when starting a new game.

## Run the tests

Run all standalone tester classes with one command:

```bash
./gradlew check
```

A complete build also runs all testers:

```bash
./gradlew build
```

An individual tester can be run through its corresponding Gradle task. For example:

```bash
./gradlew runGameTester
./gradlew runMineFieldTester
```

Each tester throws an `AssertionError` when a check fails and prints a success message when all checks pass. The Gradle build fails if any tester exits unsuccessfully.

The tests cover:

* Field construction and validation
* Mine detection and adjacent-mine counts
* Coordinate validation
* Iterative zero-region revealing
* Flag behavior
* Remaining-mine calculations
* Win and loss transitions
* Actions after game completion
* Difficulty configurations
* Random field dimensions and exact mine counts
* Rectangular fields
* Repeatable generation with a seeded `Random`
* Command-line parsing, formatting, and application output

## Command-line input format

Each field begins with two integers:

```text
rows columns
```

The next `rows` lines contain exactly `columns` characters:

* `*` represents a mine.
* `.` represents a safe square.

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

* Guarantee that the first revealed square is safe.
* Improve mine and flag graphics with reliable image icons.
* Highlight the selected mine and incorrect flags after a loss.
* Preserve square proportions when resizing the window.
* Add keyboard accessibility and clearer focus behavior.
* Migrate the standalone tester classes to a standard testing framework such as JUnit.
* Add automated tests for GUI-independent presentation decisions where practical.

Challenge-output formatting remains separate from the interactive display. `SolutionFormatter` always shows the complete solved field, while `MinesweeperGUI` respects each square’s state and the current `GameStatus`.

## Generated files

Gradle-generated output and local build state should not be committed:

```gitignore
.gradle/
build/
out/
*.class
```

The Gradle Wrapper files are intentionally committed:

* `gradlew`
* `gradlew.bat`
* `gradle/wrapper/gradle-wrapper.jar`
* `gradle/wrapper/gradle-wrapper.properties`

These files allow the project to use a consistent Gradle version without requiring a system-wide Gradle installation.
