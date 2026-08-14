package minesweeper;

import java.util.List;
import java.util.Scanner;

public class MineFieldReaderTest {

    public static void main(String[] args) {
        testValidInput();
        testImmediateTerminator();
        testNullScanner();
        testInvalidHeaders();
        testInvalidDimensions();
        testIncorrectRowLength();
        testMissingRows();
        testInvalidCharacter();

        System.out.println("All MineFieldReader tests passed.");
    }

    private static void testValidInput() {
        String input =
            "4 4\n"
            + "*...\n"
            + "....\n"
            + ".*..\n"
            + "....\n"
            + "3 5\n"
            + "**...\n"
            + ".....\n"
            + ".*...\n"
            + "0 0\n";

        List<MineField> fields = readFields(input);

        check(fields.size() == 2, "Expected exactly two fields");

        checkField(
            fields.get(0),
            new String[] {
                "*100",
                "2210",
                "1*10",
                "1110"
            }
        );

        checkField(
            fields.get(1),
            new String[] {
                "**100",
                "33200",
                "1*100"
            }
        );
    }

    private static void testImmediateTerminator() {
        List<MineField> fields = readFields("0 0\n");

        check(
            fields.isEmpty(),
            "An immediate 0 0 should produce an empty list"
        );
    }

    private static void testNullScanner() {
        expectException(
            NullPointerException.class,
            () -> new MineFieldReader(null),
            "Constructor should reject a null Scanner"
        );
    }

    private static void testInvalidHeaders() {
        expectException(
            IllegalStateException.class,
            () -> readFields("four 4\n"),
            "Reader should reject a noninteger row count"
        );

        expectException(
            IllegalStateException.class,
            () -> readFields("4 four\n"),
            "Reader should reject a noninteger column count"
        );

        expectException(
            IllegalStateException.class,
            () -> readFields("4"),
            "Reader should reject a missing column count"
        );
    }

    private static void testInvalidDimensions() {
        expectException(
            IllegalStateException.class,
            () -> readFields("0 4\n"),
            "Reader should reject a zero row count"
        );

        expectException(
            IllegalStateException.class,
            () -> readFields("4 0\n"),
            "Reader should reject a zero column count"
        );

        expectException(
            IllegalStateException.class,
            () -> readFields("-1 4\n"),
            "Reader should reject a negative row count"
        );

        expectException(
            IllegalStateException.class,
            () -> readFields("4 -1\n"),
            "Reader should reject a negative column count"
        );

        expectException(
            IllegalStateException.class,
            () -> readFields("101 4\n"),
            "Reader should reject more than 100 rows"
        );

        expectException(
            IllegalStateException.class,
            () -> readFields("4 101\n"),
            "Reader should reject more than 100 columns"
        );
    }

    private static void testIncorrectRowLength() {
        String input =
            "2 3\n"
            + "...\n"
            + "..\n"
            + "0 0\n";

        expectException(
            IllegalStateException.class,
            () -> readFields(input),
            "Reader should reject a row with the wrong length"
        );
    }

    private static void testMissingRows() {
        String input =
            "2 3\n"
            + "...\n";

        expectException(
            IllegalStateException.class,
            () -> readFields(input),
            "Reader should reject a field with missing rows"
        );
    }

    private static void testInvalidCharacter() {
        String input =
            "1 3\n"
            + ".X.\n"
            + "0 0\n";

        expectException(
            IllegalArgumentException.class,
            () -> readFields(input),
            "Reader should reject invalid field characters"
        );
    }

    private static List<MineField> readFields(String input) {
        try (Scanner scanner = new Scanner(input)) {
            MineFieldReader reader = new MineFieldReader(scanner);
            return reader.readFields();
        }
    }

    private static void checkField(
            MineField field,
            String[] expected) {

        check(
            field.numRows() == expected.length,
            "Incorrect number of rows"
        );

        check(
            field.numCols() == expected[0].length(),
            "Incorrect number of columns"
        );

        for (int row = 0; row < expected.length; row++) {
            for (int col = 0; col < expected[row].length(); col++) {
                Square square = field.getSquare(row, col);
                char expectedValue = expected[row].charAt(col);

                if (expectedValue == '*') {
                    check(
                        square.hasMine(),
                        "Expected mine at (" + row + ", " + col + ")"
                    );
                } else {
                    check(
                        !square.hasMine(),
                        "Expected safe square at ("
                            + row + ", " + col + ")"
                    );

                    int expectedCount = expectedValue - '0';

                    check(
                        square.getAdjacentMineCount() == expectedCount,
                        "At (" + row + ", " + col
                            + "), expected " + expectedCount
                            + " adjacent mines but found "
                            + square.getAdjacentMineCount()
                    );
                }
            }
        }
    }

    private static void check(
            boolean condition,
            String message) {

        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void expectException(
            Class<? extends Throwable> expectedType,
            Runnable action,
            String message) {

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
            message + "; expected "
                + expectedType.getSimpleName()
        );
    }
}