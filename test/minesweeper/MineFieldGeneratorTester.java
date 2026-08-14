package minesweeper;

import java.util.Random;

public class MineFieldGeneratorTester {

    public static void main(String[] args) {
        testOrdinarySquareField();
        testWideRectangularField();
        testTallRectangularField();
        testSingleSquareField();
        testZeroMines();
        testMaximumLegalMineCount();
        testRepeatabilityWithFixedSeed();
        testNullRandom();
        testInvalidDimensions();
        testInvalidMineCounts();

        System.out.println("All MineFieldGenerator tests passed.");
    }

    private static void testOrdinarySquareField() {
        MineFieldGenerator generator = new MineFieldGenerator();
        MineField field = generator.generate(8, 8, 10);

        verifyGeneratedField(field, 8, 8, 10);
    }

    private static void testWideRectangularField() {
        MineFieldGenerator generator =
            new MineFieldGenerator(new Random(12345L));
        MineField field = generator.generate(2, 5, 4);

        verifyGeneratedField(field, 2, 5, 4);
    }

    private static void testTallRectangularField() {
        MineFieldGenerator generator =
            new MineFieldGenerator(new Random(54321L));
        MineField field = generator.generate(5, 2, 4);

        verifyGeneratedField(field, 5, 2, 4);
    }

    private static void testSingleSquareField() {
        MineFieldGenerator generator =
            new MineFieldGenerator(new Random(50L));
        MineField field = generator.generate(1, 1, 0);

        verifyGeneratedField(field, 1, 1, 0);
    }

    private static void testZeroMines() {
        MineFieldGenerator generator =
            new MineFieldGenerator(new Random(100L));
        MineField field = generator.generate(3, 5, 0);

        verifyGeneratedField(field, 3, 5, 0);

        for (int row = 0; row < field.numRows(); row++) {
            for (int col = 0; col < field.numCols(); col++) {
                Square square = field.getSquare(row, col);

                check(
                    square.getAdjacentMineCount() == 0,
                    "A field without mines should contain only zero counts"
                );
            }
        }
    }

    private static void testMaximumLegalMineCount() {
        int rows = 3;
        int columns = 4;
        int mineCount = rows * columns - 1;

        MineFieldGenerator generator =
            new MineFieldGenerator(new Random(200L));
        MineField field = generator.generate(rows, columns, mineCount);

        verifyGeneratedField(field, rows, columns, mineCount);
    }

    private static void testRepeatabilityWithFixedSeed() {
        long seed = 987654321L;

        MineField first = new MineFieldGenerator(new Random(seed))
            .generate(7, 11, 15);
        MineField second = new MineFieldGenerator(new Random(seed))
            .generate(7, 11, 15);

        check(
            layoutsMatch(first, second),
            "Generators using the same seed should create the same layout"
        );

        verifyGeneratedField(first, 7, 11, 15);
        verifyGeneratedField(second, 7, 11, 15);
    }

    private static void testNullRandom() {
        expectException(
            NullPointerException.class,
            () -> new MineFieldGenerator(null),
            "Constructor should reject a null Random"
        );
    }

    private static void testInvalidDimensions() {
        MineFieldGenerator generator =
            new MineFieldGenerator(new Random(300L));

        expectException(
            IllegalArgumentException.class,
            () -> generator.generate(0, 5, 0),
            "Generator should reject zero rows"
        );
        expectException(
            IllegalArgumentException.class,
            () -> generator.generate(-1, 5, 0),
            "Generator should reject negative rows"
        );
        expectException(
            IllegalArgumentException.class,
            () -> generator.generate(5, 0, 0),
            "Generator should reject zero columns"
        );
        expectException(
            IllegalArgumentException.class,
            () -> generator.generate(5, -1, 0),
            "Generator should reject negative columns"
        );
    }

    private static void testInvalidMineCounts() {
        MineFieldGenerator generator =
            new MineFieldGenerator(new Random(400L));

        expectException(
            IllegalArgumentException.class,
            () -> generator.generate(2, 3, -1),
            "Generator should reject a negative mine count"
        );
        expectException(
            IllegalArgumentException.class,
            () -> generator.generate(2, 3, 6),
            "Generator should reject one mine for every square"
        );
        expectException(
            IllegalArgumentException.class,
            () -> generator.generate(2, 3, 7),
            "Generator should reject more mines than squares"
        );
    }

    private static void verifyGeneratedField(
        MineField field,
        int expectedRows,
        int expectedColumns,
        int expectedMineCount
    ) {
        check(field != null, "Generator should return a MineField");
        check(
            field.numRows() == expectedRows,
            "Expected " + expectedRows + " rows but found "
                + field.numRows()
        );
        check(
            field.numCols() == expectedColumns,
            "Expected " + expectedColumns + " columns but found "
                + field.numCols()
        );

        int actualMineCount = 0;

        for (int row = 0; row < field.numRows(); row++) {
            for (int col = 0; col < field.numCols(); col++) {
                Square square = field.getSquare(row, col);

                check(
                    square.getState() == SquareState.HIDDEN,
                    "Generated square should be hidden at ("
                        + row + ", " + col + ")"
                );

                if (square.hasMine()) {
                    actualMineCount++;
                } else {
                    int expectedAdjacentCount =
                        countAdjacentMines(field, row, col);

                    check(
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

        check(
            actualMineCount == expectedMineCount,
            "Expected " + expectedMineCount + " mines but found "
                + actualMineCount
        );
    }

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

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

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
