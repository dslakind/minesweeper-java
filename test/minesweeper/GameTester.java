package minesweeper;

/** Exercises game actions, state transitions, and coordinate validation. */
public class GameTester {

    /**
     * Runs all {@link Game} tests.
     *
     * @param args command-line arguments; ignored
     */
    public static void main(String[] args) {
        testConstructorAndInitialState();
        testNumberedSquareReveal();
        testZeroRegionRevealAndWin();
        testFinalSafeSquareWinsGame();
        testMineRevealLosesGame();
        testFlaggingAndUnflagging();
        testFlaggedSquareCannotBeRevealed();
        testRevealedSquareCannotBeFlagged();
        testActionsAfterWinDoNothing();
        testActionsAfterLossDoNothing();
        testInvalidCoordinates();

        System.out.println("All Game tests passed.");
    }

    /** Verifies construction, initial status, dimensions, and null rejection. */
    private static void testConstructorAndInitialState() {
        MineField field = new MineField(
            new String[]{
                "*.",
                ".."
            }
        );

        Game game = new Game(field);

        check(
            game.getStatus() == GameStatus.IN_PROGRESS,
            "A new game should be in progress"
        );
        check(game.numRows() == 2, "Game should report 2 rows");
        check(game.numCols() == 2, "Game should report 2 columns");
        check(
            game.getSquare(0, 0) == field.getSquare(0, 0),
            "Game should provide access to squares in its field"
        );

        expectException(
            NullPointerException.class,
            () -> new Game(null),
            "Game should reject a null MineField"
        );
    }

    /** Verifies that revealing a numbered square does not expand a region. */
    private static void testNumberedSquareReveal() {
        Game game = new Game(
            new MineField(
                new String[]{
                    "*..",
                    "...",
                    "..."
                }
            )
        );

        check(
            game.getSquare(0, 1).getAdjacentMineCount() == 1,
            "Test setup requires a numbered starting square"
        );

        game.revealSquare(0, 1);

        checkState(
            game,
            0,
            1,
            SquareState.REVEALED,
            "Selected numbered square should be revealed"
        );
        checkState(
            game,
            1,
            1,
            SquareState.HIDDEN,
            "A numbered square should not reveal its neighbors"
        );
        check(
            game.getStatus() == GameStatus.IN_PROGRESS,
            "Game should remain in progress when safe squares remain hidden"
        );
    }

    /** Verifies zero-region expansion and the resulting win. */
    private static void testZeroRegionRevealAndWin() {
        Game game = new Game(
            new MineField(
                new String[]{
                    "*...",
                    "....",
                    "....",
                    "...."
                }
            )
        );

        check(
            game.getSquare(3, 3).getAdjacentMineCount() == 0,
            "Test setup requires a zero-count starting square"
        );

        game.revealSquare(3, 3);

        for (int row = 0; row < game.numRows(); row++) {
            for (int col = 0; col < game.numCols(); col++) {
                Square square = game.getSquare(row, col);

                if (square.hasMine()) {
                    checkState(
                        game,
                        row,
                        col,
                        SquareState.HIDDEN,
                        "Region expansion should not reveal a mine"
                    );
                } else {
                    checkState(
                        game,
                        row,
                        col,
                        SquareState.REVEALED,
                        "Region expansion should reveal every reachable safe square"
                    );
                }
            }
        }

        check(
            game.getStatus() == GameStatus.WON,
            "Revealing every safe square through region expansion should win"
        );
    }

    /** Verifies that revealing the last safe square wins the game. */
    private static void testFinalSafeSquareWinsGame() {
        Game game = new Game(
            new MineField(
                new String[]{
                    "*.",
                    "**"
                }
            )
        );

        game.revealSquare(0, 1);

        checkState(
            game,
            0,
            1,
            SquareState.REVEALED,
            "The final safe square should be revealed"
        );
        check(
            game.getStatus() == GameStatus.WON,
            "Revealing the final safe square should win the game"
        );
    }

    /** Verifies that revealing a mine loses the game. */
    private static void testMineRevealLosesGame() {
        Game game = new Game(
            new MineField(
                new String[]{
                    "**",
                    ".."
                }
            )
        );

        game.revealSquare(0, 0);

        checkState(
            game,
            0,
            0,
            SquareState.REVEALED,
            "Selected mine should be revealed"
        );
        checkState(
            game,
            0,
            1,
            SquareState.HIDDEN,
            "Other mines should remain hidden"
        );
        check(
            game.getStatus() == GameStatus.LOST,
            "Revealing a mine should lose the game"
        );
    }

    /** Verifies flag placement and removal. */
    private static void testFlaggingAndUnflagging() {
        Game game = new Game(
            new MineField(
                new String[]{
                    "*.",
                    ".."
                }
            )
        );

        game.toggleFlag(0, 0);
        checkState(
            game,
            0,
            0,
            SquareState.FLAGGED,
            "A hidden square should become flagged"
        );

        game.toggleFlag(0, 0);
        checkState(
            game,
            0,
            0,
            SquareState.HIDDEN,
            "A flagged square should become hidden when toggled again"
        );

        check(
            game.getStatus() == GameStatus.IN_PROGRESS,
            "Flagging should not change the game status"
        );
    }

    /** Verifies that a flagged square cannot be revealed. */
    private static void testFlaggedSquareCannotBeRevealed() {
        Game game = new Game(
            new MineField(
                new String[]{
                    "*.",
                    ".."
                }
            )
        );

        game.toggleFlag(0, 0);
        game.revealSquare(0, 0);

        checkState(
            game,
            0,
            0,
            SquareState.FLAGGED,
            "A flagged mine should not be revealed"
        );
        check(
            game.getStatus() == GameStatus.IN_PROGRESS,
            "Trying to reveal a flagged mine should not lose the game"
        );
    }

    /** Verifies that a revealed square cannot be flagged. */
    private static void testRevealedSquareCannotBeFlagged() {
        Game game = new Game(
            new MineField(
                new String[]{
                    "*..",
                    "...",
                    "..."
                }
            )
        );

        game.revealSquare(0, 1);
        game.toggleFlag(0, 1);

        checkState(
            game,
            0,
            1,
            SquareState.REVEALED,
            "A revealed square should not become flagged"
        );
    }

    /** Verifies that player actions are ignored after a win. */
    private static void testActionsAfterWinDoNothing() {
        Game game = new Game(
            new MineField(
                new String[]{
                    "*.",
                    "**"
                }
            )
        );

        game.revealSquare(0, 1);
        check(
            game.getStatus() == GameStatus.WON,
            "Test setup should produce a win"
        );

        game.revealSquare(0, 0);
        game.toggleFlag(1, 0);

        checkState(
            game,
            0,
            0,
            SquareState.HIDDEN,
            "A mine should not be revealed after the game is won"
        );
        checkState(
            game,
            1,
            0,
            SquareState.HIDDEN,
            "A square should not be flagged after the game is won"
        );
        check(
            game.getStatus() == GameStatus.WON,
            "The game should remain won after ignored actions"
        );
    }

    /** Verifies that player actions are ignored after a loss. */
    private static void testActionsAfterLossDoNothing() {
        Game game = new Game(
            new MineField(
                new String[]{
                    "*.",
                    ".."
                }
            )
        );

        game.revealSquare(0, 0);
        check(
            game.getStatus() == GameStatus.LOST,
            "Test setup should produce a loss"
        );

        game.revealSquare(1, 1);
        game.toggleFlag(0, 1);

        checkState(
            game,
            1,
            1,
            SquareState.HIDDEN,
            "A safe square should not be revealed after a loss"
        );
        checkState(
            game,
            0,
            1,
            SquareState.HIDDEN,
            "A square should not be flagged after a loss"
        );
        check(
            game.getStatus() == GameStatus.LOST,
            "The game should remain lost after ignored actions"
        );
    }

    /** Verifies that square operations reject out-of-range coordinates. */
    private static void testInvalidCoordinates() {
        Game game = new Game(
            new MineField(
                new String[]{
                    "*.",
                    ".."
                }
            )
        );

        expectException(
            IndexOutOfBoundsException.class,
            () -> game.revealSquare(-1, 0),
            "revealSquare should reject a negative row"
        );
        expectException(
            IndexOutOfBoundsException.class,
            () -> game.revealSquare(0, -1),
            "revealSquare should reject a negative column"
        );
        expectException(
            IndexOutOfBoundsException.class,
            () -> game.revealSquare(2, 0),
            "revealSquare should reject a row beyond the field"
        );
        expectException(
            IndexOutOfBoundsException.class,
            () -> game.toggleFlag(0, 2),
            "toggleFlag should reject a column beyond the field"
        );
        expectException(
            IndexOutOfBoundsException.class,
            () -> game.getSquare(2, 0),
            "getSquare should reject an invalid position"
        );

        game.revealSquare(0, 0);

        expectException(
            IndexOutOfBoundsException.class,
            () -> game.revealSquare(-1, 0),
            "revealSquare should still validate coordinates after game over"
        );
        expectException(
            IndexOutOfBoundsException.class,
            () -> game.toggleFlag(-1, 0),
            "toggleFlag should still validate coordinates after game over"
        );
    }

    /**
     * Checks the state of one game square.
     *
     * @param game game containing the square
     * @param row square row
     * @param col square column
     * @param expectedState expected state
     * @param message failure message
     */
    private static void checkState(
        Game game,
        int row,
        int col,
        SquareState expectedState,
        String message
    ) {
        SquareState actualState = game.getSquare(row, col).getState();

        check(
            actualState == expectedState,
            message + " at (" + row + ", " + col + ")"
                + "; expected " + expectedState
                + " but found " + actualState
        );
    }

    /**
     * Fails the test run when a condition is false.
     *
     * @param condition condition that must hold
     * @param message failure message
     */
    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    /**
     * Verifies that an action throws the expected exception type.
     *
     * @param expectedType expected exception class
     * @param action action to execute
     * @param message failure message
     */
    private static void expectException(
        Class<? extends Throwable> expectedType,
        Runnable action,
        String message
    ) {
        try {
            action.run();
        } catch (Throwable exception) {
            if (expectedType.isInstance(exception)) {
                return;
            }

            throw new AssertionError(
                message + "; expected "
                    + expectedType.getSimpleName()
                    + " but received "
                    + exception.getClass().getSimpleName(),
                exception
            );
        }

        throw new AssertionError(
            message + "; expected " + expectedType.getSimpleName()
        );
    }
}
