package minesweeper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Arrays;

/** Exercises minefield construction, validation, and region-reveal behavior. */
public class MineFieldTest {


    /** Verifies field dimensions, square states, and adjacent-mine counts. */
    @Test
    void testConstructionAndMineCounts() {
        String[] rows = {
            "*...",
            "....",
            ".*..",
            "...."
        };

        MineField field = new MineField(rows);

        assertTrue(field.numRows() == 4, "Expected 4 rows");
        assertTrue(field.numCols() == 4, "Expected 4 columns");

        String[] expected = {
            "*100",
            "2210",
            "1*10",
            "1110"
        };

        for (int row = 0; row < field.numRows(); row++) {
            for (int col = 0; col < field.numCols(); col++) {
                Square square = field.getSquare(row, col);
                char expectedValue = expected[row].charAt(col);

                assertTrue(
                    square.getState() == SquareState.HIDDEN,
                    "New square should be hidden at ("
                        + row + ", " + col + ")"
                );

                if (expectedValue == '*') {
                    assertTrue(
                        square.hasMine(),
                        "Expected mine at (" + row + ", " + col + ")"
                    );
                } else {
                    assertTrue(
                        !square.hasMine(),
                        "Expected safe square at ("
                            + row + ", " + col + ")"
                    );

                    int expectedCount =
                        Character.getNumericValue(expectedValue);

                    assertTrue(
                        square.getAdjacentMineCount() == expectedCount,
                        "Incorrect count at (" + row + ", " + col
                            + "): expected " + expectedCount
                            + " but found "
                            + square.getAdjacentMineCount()
                    );
                }
            }
        }

        assertTrue(
            !field.allSafeSquaresRevealed(),
            "New field should not report all safe squares revealed"
        );
    }

    /** Verifies valid-coordinate detection and invalid-coordinate exceptions. */
    @Test
    void testCoordinateValidation() {
        MineField field = new MineField(
            new String[]{
                "..",
                ".."
            }
        );

        assertTrue(field.isValidSquare(0, 0), "(0, 0) should be valid");
        assertTrue(field.isValidSquare(1, 1), "(1, 1) should be valid");
        assertTrue(!field.isValidSquare(-1, 0), "Negative row should be invalid");
        assertTrue(!field.isValidSquare(0, -1), "Negative column should be invalid");
        assertTrue(!field.isValidSquare(2, 0), "Row 2 should be invalid");
        assertTrue(!field.isValidSquare(0, 2), "Column 2 should be invalid");

        assertThrows(
            IndexOutOfBoundsException.class,
            () -> field.getSquare(-1, 0),
            "getSquare should reject an invalid position"
        );

        assertThrows(
            IndexOutOfBoundsException.class,
            () -> field.revealRegion(-1, 0),
            "revealRegion should reject a negative row"
        );

        assertThrows(
            IndexOutOfBoundsException.class,
            () -> field.revealRegion(0, -1),
            "revealRegion should reject a negative column"
        );

        assertThrows(
            IndexOutOfBoundsException.class,
            () -> field.revealRegion(2, 0),
            "revealRegion should reject a row beyond the field"
        );

        assertThrows(
            IndexOutOfBoundsException.class,
            () -> field.revealRegion(0, 2),
            "revealRegion should reject a column beyond the field"
        );
    }

    /** Verifies that revealing a numbered square does not reveal neighbors. */
    @Test
    void testNumberedSquareReveal() {
        MineField field = new MineField(
            new String[]{
                "*..",
                "...",
                "..."
            }
        );

        assertTrue(
            field.getSquare(0, 1).getAdjacentMineCount() == 1,
            "Starting square should be numbered"
        );

        field.revealRegion(0, 1);

        checkState(
            field,
            0,
            1,
            SquareState.REVEALED,
            "Selected numbered square should be revealed"
        );

        for (int row = 0; row < field.numRows(); row++) {
            for (int col = 0; col < field.numCols(); col++) {
                if (row != 0 || col != 1) {
                    checkState(
                        field,
                        row,
                        col,
                        SquareState.HIDDEN,
                        "A numbered square should not reveal neighboring squares"
                    );
                }
            }
        }

        assertTrue(
            !field.allSafeSquaresRevealed(),
            "Revealing one numbered square should not complete this field"
        );
    }

    /** Verifies expansion through a connected region of zero-count squares. */
    @Test
    void testZeroRegionReveal() {
        MineField field = new MineField(
            new String[]{
                "*...",
                "....",
                "....",
                "...."
            }
        );

        assertTrue(
            field.getSquare(3, 3).getAdjacentMineCount() == 0,
            "Starting square should have zero adjacent mines"
        );

        field.revealRegion(3, 3);

        for (int row = 0; row < field.numRows(); row++) {
            for (int col = 0; col < field.numCols(); col++) {
                Square square = field.getSquare(row, col);

                if (square.hasMine()) {
                    assertTrue(
                        square.getState() == SquareState.HIDDEN,
                        "Region reveal must not reveal a mine"
                    );
                } else {
                    assertTrue(
                        square.getState() == SquareState.REVEALED,
                        "Safe square should be revealed at ("
                            + row + ", " + col + ")"
                    );
                }
            }
        }

        checkState(
            field,
            0,
            1,
            SquareState.REVEALED,
            "Numbered boundary square should be revealed"
        );

        checkState(
            field,
            1,
            0,
            SquareState.REVEALED,
            "Numbered boundary square should be revealed"
        );

        checkState(
            field,
            1,
            1,
            SquareState.REVEALED,
            "Diagonal numbered boundary should be revealed"
        );

        assertTrue(
            field.allSafeSquaresRevealed(),
            "Region reveal should reveal every safe square in this field"
        );
    }

    /** Verifies that a mine barrier prevents expansion into another region. */
    @Test
    void testDisconnectedRegionRemainsHidden() {
        MineField field = new MineField(
            new String[]{
                ".....",
                ".....",
                "*****",
                ".....",
                "....."
            }
        );

        field.revealRegion(0, 0);

        // The upper zero region and its numbered boundary should be revealed.
        for (int row = 0; row <= 1; row++) {
            for (int col = 0; col < field.numCols(); col++) {
                checkState(
                    field,
                    row,
                    col,
                    SquareState.REVEALED,
                    "Upper region should be revealed"
                );
            }
        }

        // The mine barrier must remain hidden.
        for (int col = 0; col < field.numCols(); col++) {
            checkState(
                field,
                2,
                col,
                SquareState.HIDDEN,
                "Mine barrier should remain hidden"
            );
        }

        // The disconnected lower region must remain hidden.
        for (int row = 3; row <= 4; row++) {
            for (int col = 0; col < field.numCols(); col++) {
                checkState(
                    field,
                    row,
                    col,
                    SquareState.HIDDEN,
                    "Disconnected region should remain hidden"
                );
            }
        }

        assertTrue(
            !field.allSafeSquaresRevealed(),
            "Hidden disconnected safe region should prevent completion"
        );
    }

    /** Verifies that region reveal does not reveal a selected mine. */
    @Test
    void testMineIsNotRevealed() {
        MineField field = new MineField(
            new String[]{
                "*.",
                ".."
            }
        );

        field.revealRegion(0, 0);

        checkState(
            field,
            0,
            0,
            SquareState.HIDDEN,
            "revealRegion should not reveal a mine"
        );

        checkState(
            field,
            0,
            1,
            SquareState.HIDDEN,
            "Selecting a mine should not reveal other squares"
        );
    }

    /** Verifies that region reveal preserves flagged squares. */
    @Test
    void testFlaggedSquareIsNotRevealed() {
        MineField field = new MineField(
            new String[]{
                "...",
                "...",
                "..."
            }
        );

        Square center = field.getSquare(1, 1);
        center.toggleFlag();

        checkState(
            field,
            1,
            1,
            SquareState.FLAGGED,
            "Center square should be flagged"
        );

        field.revealRegion(0, 0);

        for (int row = 0; row < field.numRows(); row++) {
            for (int col = 0; col < field.numCols(); col++) {
                if (row == 1 && col == 1) {
                    checkState(
                        field,
                        row,
                        col,
                        SquareState.FLAGGED,
                        "Region reveal should preserve the flag"
                    );
                } else {
                    checkState(
                        field,
                        row,
                        col,
                        SquareState.REVEALED,
                        "Unflagged empty square should be revealed"
                    );
                }
            }
        }

        assertTrue(
            !field.allSafeSquaresRevealed(),
            "Flagged safe square should prevent completion"
        );

        center.toggleFlag();
        field.revealRegion(1, 1);

        checkState(
            field,
            1,
            1,
            SquareState.REVEALED,
            "Square should be revealable after removing its flag"
        );

        assertTrue(
            field.allSafeSquaresRevealed(),
            "All safe squares should be revealed after removing the flag"
        );
    }

    /** Verifies that revealing an already revealed region is harmless. */
    @Test
    void testRepeatedReveal() {
        MineField field = new MineField(
            new String[]{
                "*..",
                "...",
                "..."
            }
        );

        field.revealRegion(0, 1);
        field.revealRegion(0, 1);

        checkState(
            field,
            0,
            1,
            SquareState.REVEALED,
            "Repeated reveal should leave the square revealed"
        );

        checkState(
            field,
            1,
            1,
            SquareState.HIDDEN,
            "Repeated reveal of a numbered square should not expand"
        );
    }

    /** Verifies iterative expansion across a large empty field. */
    @Test
    void testLargeEmptyRegion() {
        int size = 100;
        String[] rows = new String[size];

        char[] symbols = new char[size];
        Arrays.fill(symbols, '.');
        String emptyRow = new String(symbols);

        Arrays.fill(rows, emptyRow);

        MineField field = new MineField(rows);
        field.revealRegion(size / 2, size / 2);

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                checkState(
                    field,
                    row,
                    col,
                    SquareState.REVEALED,
                    "Large empty region was not completely revealed"
                );
            }
        }

        assertTrue(
            field.allSafeSquaresRevealed(),
            "Large empty field should be completed by one reveal"
        );
    }

    /** Verifies rejection of null, empty, irregular, and invalid field data. */
    @Test
    void testConstructorValidation() {
        assertThrows(
            NullPointerException.class,
            () -> new MineField((String[]) null),
            "Constructor should reject null"
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> new MineField(new String[0]),
            "Constructor should reject an empty array"
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> new MineField(new String[]{""}),
            "Constructor should reject empty rows"
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> new MineField(new String[]{"...", ".."}),
            "Constructor should reject nonrectangular fields"
        );

        assertThrows(
            NullPointerException.class,
            () -> new MineField(new String[]{null}),
            "Constructor should reject a null first row"
        );

        assertThrows(
            NullPointerException.class,
            () -> new MineField(new String[]{"...", null}),
            "Constructor should reject subsequent null rows"
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> new MineField(new String[]{"..X", "..."}),
            "Constructor should reject invalid characters"
        );
    }

    /**
     * Checks the state of one field square.
     *
     * @param field field containing the square
     * @param row square row
     * @param col square column
     * @param expectedState expected state
     * @param message failure message
     */
    private static void checkState(
        MineField field,
        int row,
        int col,
        SquareState expectedState,
        String message
    ) {
        SquareState actualState = field.getSquare(row, col).getState();

        assertTrue(
            actualState == expectedState,
            message + " at (" + row + ", " + col + ")"
                + "; expected " + expectedState
                + " but found " + actualState
        );
    }
}
