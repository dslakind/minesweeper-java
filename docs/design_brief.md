# Minesweeper Design Brief

## Overview

This project is an object-oriented Java implementation of Minesweeper. It supports two related use cases:

1. A command-line solver for the PC/UVa 110102/10189 programming challenge.
2. An interactive Swing game with randomized boards, revealing, flagging, and win/loss detection.

Both applications share the same field and square model. The project uses only the Java standard library and can be compiled directly with `javac`.

## Goals

- Model a rectangular Minesweeper field using clear, focused classes.
- Calculate the number of neighboring mines for every square.
- Parse and format fields according to the programming-challenge specification.
- Support the rules needed for an interactive game.
- Keep presentation code separate from game rules and field calculations.
- Validate malformed input and invalid coordinates with useful exceptions.
- Make model behavior testable without launching the GUI.

## Requirements and constraints

For command-line input:

- Each field contains between 1 and 100 rows and columns.
- `*` represents a mine and `.` represents a safe square.
- Every row must have the width declared in the field header.
- The header `0 0` ends the input and is not processed.
- Each solved field is labeled `Field #x:` and separated from the next field by one blank line.

For interactive play:

- A left-click reveals a square.
- A right-click toggles a flag on a hidden square.
- Revealing a mine ends the game in a loss.
- Revealing all safe squares ends the game in a win.
- Revealing a zero-count square expands through connected zero squares and reveals their numbered boundary.
- Player actions cannot change the model after the game ends.

## Architecture

The design separates data, game rules, input/output, and graphical presentation.

| Component | Responsibility |
| --- | --- |
| `Square` | Stores mine presence, adjacent-mine count, and visibility state. |
| `SquareState` | Defines the `HIDDEN`, `REVEALED`, and `FLAGGED` square states. |
| `MineField` | Owns the square grid, calculates hints, validates coordinates, and reveals safe regions. |
| `MineFieldGenerator` | Creates random fields containing an exact number of uniquely positioned mines. |
| `Game` | Applies player actions and determines win or loss transitions. |
| `GameStatus` | Defines the `IN_PROGRESS`, `WON`, and `LOST` game states. |
| `MineFieldReader` | Parses command-line field data from a `Scanner`. |
| `SolutionFormatter` | Converts a field into the required challenge-output format. |
| `MinesweeperApplication` | Coordinates the command-line reader and formatter. |
| `MinesweeperGUI` | Renders the current game and converts mouse input into model operations. |
| `MinesweeperGUIApplication` | Starts the Swing interface on the Event Dispatch Thread. |

The principal dependency flow is:

```text
Command line: Scanner -> MineFieldReader -> MineField -> SolutionFormatter -> PrintStream

Interactive:  MinesweeperGUI -> Game -> MineField -> Square
                       |             |
                       |             +-> GameStatus
                       +-> MineFieldGenerator
```

## Key design decisions

### Object-based field model

`MineField` stores a `Square[][]` instead of maintaining parallel arrays for mines, counts, and states. This keeps all data for a position together and gives both applications one consistent model. The additional object overhead is acceptable for the supported field sizes.

### Iterative region revealing

Zero-region expansion uses a queue-based breadth-first traversal. A separate boolean matrix records scheduled positions, preventing repeated processing. This iterative approach also avoids the risk of stack overflow that a recursive flood-fill could introduce on larger empty fields.

### Separation of game rules and presentation

`Game` decides whether actions are legal and whether the result is a win or loss. `MinesweeperGUI` only forwards input and redraws the resulting state. It does not place mines, calculate hints, or perform region expansion.

The command-line formatter is also independent of interactive square visibility. `SolutionFormatter` always emits the complete solution required by the challenge, while the GUI respects `SquareState` and `GameStatus`.

### Testable randomness

`MineFieldGenerator` accepts a `Random` instance through its constructor. Production code can use the default random source, while tests can provide a fixed seed and verify repeatable layouts.

### Input validation

Invalid field dimensions, inconsistent row widths, unsupported symbols, null dependencies, and out-of-range coordinates are rejected near the point where they enter the model. This prevents partially valid fields from propagating through the applications.

## Current status

The command-line solver and beginner-level Swing game are implemented. Standalone test programs cover:

- Field construction and adjacent-mine calculations
- Input parsing and output formatting
- Coordinate and constructor validation
- Numbered-square and zero-region revealing
- Flag behavior
- Win and loss transitions
- Actions attempted after game completion
- Random field dimensions, mine counts, and seeded repeatability
- End-to-end command-line application output

The test suite does not require JUnit; each tester throws `AssertionError` on failure and prints a success message when it completes.

## Next steps

### Near term

- Add selectable beginner, intermediate, and expert board configurations.
- Guarantee that the first revealed square is safe, preferably with an empty opening region.
- Add a remaining-mine counter and elapsed-time display.
- Distinguish the exploded mine, other mines, and incorrect flags after a loss.
- Add a restart control that preserves the selected difficulty.

### Quality and maintainability

- Introduce a standard build and test workflow, such as Gradle with JUnit 5.
- Convert the standalone tester classes into independently reported unit tests.
- Add tests for first-click safety and each difficulty configuration.
- Extract GUI display decisions into testable presentation helpers where practical.
- Add package-level JavaDoc and generate API documentation as part of the build.

### User experience

- Improve keyboard navigation, focus indicators, and screen-reader labels.
- Replace font-dependent mine and flag symbols with reliable bundled icons.
- Allow the window and squares to resize while retaining usable proportions.
- Add optional sound, high scores, and saved preferences after the core accessibility work is complete.

The recommended implementation order is difficulty configuration, first-click safety, mine counter and timer, loss-state presentation, and then build/test modernization. This sequence improves gameplay first while keeping each change small enough to verify independently.
