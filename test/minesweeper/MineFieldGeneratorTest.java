package minesweeper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Random;

/** Exercises randomized minefield generation and input validation. */
public class MineFieldGeneratorTest {


    /** Verifies generation of a typical square field. */
    @Test
    void testOrdinarySquareField() {
        MineFieldGenerator generator = new MineFieldGenerator();
        MineField field = generator.generate(8, 8, 10);

        verifyGeneratedField(field, 8, 8, 10);
    }

    /** Verifies generation of a field wider than it is tall. */
    @Test
    void testWideRectangularField() {
        MineFieldGenerator generator =
            new MineFieldGenerator(new Random(12345L));
        MineField field = generator.generate(2, 5, 4);

        verifyGeneratedField(field, 2, 5, 4);
    }

    /** Verifies generation of a field taller than it is wide. */
    @Test
    void testTallRectangularField() {
        MineFieldGenerator generator =
            new MineFieldGenerator(new Random(54321L));
        MineField field = generator.generate(5, 2, 4);

        verifyGeneratedField(field, 5, 2, 4);
    }

    /** Verifies the smallest legal field. */
    @Test
    void testSingleSquareField() {
        MineFieldGenerator generator =
            new MineFieldGenerator(new Random(50L));
        MineField field = generator.generate(1, 1, 0);

        verifyGeneratedField(field, 1, 1, 0);
    }

    /** Verifies generation of a mine-free field. */
    @Test
    void testZeroMines() {
        MineFieldGenerator generator =
            new MineFieldGenerator(new Random(100L));
        MineField field = generator.generate(3, 5, 0);

        verifyGeneratedField(field, 3, 5, 0);

        for (int row = 0; row < field.numRows(); row++) {
            for (int col = 0; col < field.numCols(); col++) {
                Square square = field.getSquare(row, col);

                assertTrue(
                    square.getAdjacentMineCount() == 0,
                    "A field without mines should contain only zero counts"
                );
            }
        }
    }

    /** Verifies the largest legal mine count for a field. */
    @Test
    void testMaximumLegalMineCount() {
        int rows = 3;
        int columns = 4;
        int mineCount = rows * columns - 1;

        MineFieldGenerator generator =
            new MineFieldGenerator(new Random(200L));
        MineField field = generator.generate(rows, columns, mineCount);

        verifyGeneratedField(field, rows, columns, mineCount);
    }

    /** Verifies deterministic layouts when generators use the same seed. */
    @Test
    void testRepeatabilityWithFixedSeed() {
        long seed = 987654321L;

        MineField first = new MineFieldGenerator(new Random(seed))
            .generate(7, 11, 15);
        MineField second = new MineFieldGenerator(new Random(seed))
            .generate(7, 11, 15);

        assertTrue(
            layoutsMatch(first, second),
            "Generators using the same seed should create the same layout"
        );

        verifyGeneratedField(first, 7, 11, 15);
        verifyGeneratedField(second, 7, 11, 15);
    }

    /** Verifies generation using the beginner difficulty settings. */
    @Test
    void testBeginnerConfiguration() {
        MineFieldGenerator generator =
            new MineFieldGenerator(new Random(500L));
        MineField field = generator.generate(Difficulty.BEGINNER);

        verifyGeneratedField(field, 9, 9, 10);
    }

    /** Verifies generation using the intermediate difficulty settings. */
    @Test
    void testIntermediateConfiguration() {
        MineFieldGenerator generator =
            new MineFieldGenerator(new Random(600L));
        MineField field = generator.generate(Difficulty.INTERMEDIATE);

        verifyGeneratedField(field, 16, 16, 40);
    }

    /** Verifies generation using the expert difficulty settings. */
    @Test
    void testExpertConfiguration() {
        MineFieldGenerator generator =
            new MineFieldGenerator(new Random(700L));
        MineField field = generator.generate(Difficulty.EXPERT);

        verifyGeneratedField(field, 16, 30, 99);
    }

    /** Verifies that the generator rejects a null difficulty. */
    @Test
    void testNullDifficulty() {
        MineFieldGenerator generator =
            new MineFieldGenerator(new Random(800L));

        assertThrows(
            NullPointerException.class,
            () -> generator.generate((Difficulty) null),
            "Generator should reject a null Difficulty"
        );
    }

    /** Verifies that the generator rejects a null random source. */
    @Test
    void testNullRandom() {
        assertThrows(
            NullPointerException.class,
            () -> new MineFieldGenerator(null),
            "Constructor should reject a null Random"
        );
    }

    /** Verifies rejection of nonpositive dimensions. */
    @Test
    void testInvalidDimensions() {
        MineFieldGenerator generator =
            new MineFieldGenerator(new Random(300L));

        assertThrows(
            IllegalArgumentException.class,
            () -> generator.generate(0, 5, 0),
            "Generator should reject zero rows"
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> generator.generate(-1, 5, 0),
            "Generator should reject negative rows"
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> generator.generate(5, 0, 0),
            "Generator should reject zero columns"
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> generator.generate(5, -1, 0),
            "Generator should reject negative columns"
        );
    }

    /** Verifies rejection of mine counts outside the legal range. */
    @Test
    void testInvalidMineCounts() {
        MineFieldGenerator generator =
            new MineFieldGenerator(new Random(400L));

        assertThrows(
            IllegalArgumentException.class,
            () -> generator.generate(2, 3, -1),
            "Generator should reject a negative mine count"
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> generator.generate(2, 3, 6),
            "Generator should reject one mine for every square"
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> generator.generate(2, 3, 7),
            "Generator should reject more mines than squares"
        );
    }

    /**
     * Verifies dimensions, mine count, initial states, and adjacency counts.
     *
     * @param field generated field
     * @param expectedRows expected row count
     * @param expectedColumns expected column count
     * @param expectedMineCount expected mine count
     */
    private static void verifyGeneratedField(
        MineField field,
        int expectedRows,
        int expectedColumns,
        int expectedMineCount
    ) {
        assertTrue(field != null, "Generator should return a MineField");
        assertTrue(
            field.numRows() == expectedRows,
            "Expected " + expectedRows + " rows but found "
                + field.numRows()
        );
        assertTrue(
            field.numCols() == expectedColumns,
            "Expected " + expectedColumns + " columns but found "
                + field.numCols()
        );

        int actualMineCount = 0;

        for (int row = 0; row < field.numRows(); row++) {
            for (int col = 0; col < field.numCols(); col++) {
                Square square = field.getSquare(row, col);

                assertTrue(
                    square.getState() == SquareState.HIDDEN,
                    "Generated square should be hidden at ("
                        + row + ", " + col + ")"
                );

                if (square.hasMine()) {
                    actualMineCount++;
                } else {
                    int expectedAdjacentCount =
                        countAdjacentMines(field, row, col);

                    assertTrue(
                        square.getAdjacentMineCount()
                            == expectedAdjacentCount,
                        "Incorrect adjacent-mine count at ("
                            + row + ", " + col + "): expected "
                            + expectedAdjacentCount + " but found "
                            + square.getAdjacentMineCount()
                    );
                }
            }
        }

        assertTrue(
            actualMineCount == expectedMineCount,
            "Expected " + expectedMineCount + " mines but found "
                + actualMineCount
        );
    }

    /**
     * Independently counts mines adjacent to a square.
     *
     * @param field field to inspect
     * @param row square row
     * @param col square column
     * @return number of neighboring mines
     */
    private static int countAdjacentMines(
        MineField field,
        int row,
        int col
    ) {
        int count = 0;

        for (int neighborRow = row - 1;
                neighborRow <= row + 1;
                neighborRow++) {
            for (int neighborCol = col - 1;
                    neighborCol <= col + 1;
                    neighborCol++) {
                boolean isCurrentSquare =
                    neighborRow == row && neighborCol == col;

                if (!isCurrentSquare
                        && field.isValidSquare(neighborRow, neighborCol)
                        && field.getSquare(neighborRow, neighborCol).hasMine()) {
                    count++;
                }
            }
        }

        return count;
    }

    /**
     * Compares the mine placement of two fields.
     *
     * @param first first field
     * @param second second field
     * @return {@code true} when dimensions and mine positions match
     */
    private static boolean layoutsMatch(
        MineField first,
        MineField second
    ) {
        if (first.numRows() != second.numRows()
                || first.numCols() != second.numCols()) {
            return false;
        }

        for (int row = 0; row < first.numRows(); row++) {
            for (int col = 0; col < first.numCols(); col++) {
                if (first.getSquare(row, col).hasMine()
                        != second.getSquare(row, col).hasMine()) {
                    return false;
                }
            }
        }

        return true;
    }
}
