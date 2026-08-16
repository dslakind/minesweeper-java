package minesweeper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import java.util.Scanner;

/** Exercises parsing of valid, terminated, malformed, and incomplete fields. */
public class MineFieldReaderTest {


    /** Verifies parsing of multiple valid minefields. */
    @Test
    void testValidInput() {
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

        assertTrue(fields.size() == 2, "Expected exactly two fields");

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

    /** Verifies that an immediate terminator produces no fields. */
    @Test
    void testImmediateTerminator() {
        List<MineField> fields = readFields("0 0\n");

        assertTrue(
            fields.isEmpty(),
            "An immediate 0 0 should produce an empty list"
        );
    }

    /** Verifies rejection of a null scanner. */
    @Test
    void testNullScanner() {
        assertThrows(
            NullPointerException.class,
            () -> new MineFieldReader(null),
            "Constructor should reject a null Scanner"
        );
    }

    /** Verifies rejection of missing or nonnumeric dimension headers. */
    @Test
    void testInvalidHeaders() {
        assertThrows(
            IllegalStateException.class,
            () -> readFields("four 4\n"),
            "Reader should reject a noninteger row count"
        );

        assertThrows(
            IllegalStateException.class,
            () -> readFields("4 four\n"),
            "Reader should reject a noninteger column count"
        );

        assertThrows(
            IllegalStateException.class,
            () -> readFields("4"),
            "Reader should reject a missing column count"
        );
    }

    /** Verifies rejection of dimensions outside the permitted range. */
    @Test
    void testInvalidDimensions() {
        assertThrows(
            IllegalStateException.class,
            () -> readFields("0 4\n"),
            "Reader should reject a zero row count"
        );

        assertThrows(
            IllegalStateException.class,
            () -> readFields("4 0\n"),
            "Reader should reject a zero column count"
        );

        assertThrows(
            IllegalStateException.class,
            () -> readFields("-1 4\n"),
            "Reader should reject a negative row count"
        );

        assertThrows(
            IllegalStateException.class,
            () -> readFields("4 -1\n"),
            "Reader should reject a negative column count"
        );

        assertThrows(
            IllegalStateException.class,
            () -> readFields("101 4\n"),
            "Reader should reject more than 100 rows"
        );

        assertThrows(
            IllegalStateException.class,
            () -> readFields("4 101\n"),
            "Reader should reject more than 100 columns"
        );
    }

    /** Verifies rejection of a row whose width does not match its header. */
    @Test
    void testIncorrectRowLength() {
        String input =
            "2 3\n"
            + "...\n"
            + "..\n"
            + "0 0\n";

        assertThrows(
            IllegalStateException.class,
            () -> readFields(input),
            "Reader should reject a row with the wrong length"
        );
    }

    /** Verifies rejection of input that ends before all rows are read. */
    @Test
    void testMissingRows() {
        String input =
            "2 3\n"
            + "...\n";

        assertThrows(
            IllegalStateException.class,
            () -> readFields(input),
            "Reader should reject a field with missing rows"
        );
    }

    /** Verifies rejection of unsupported field characters. */
    @Test
    void testInvalidCharacter() {
        String input =
            "1 3\n"
            + ".X.\n"
            + "0 0\n";

        assertThrows(
            IllegalArgumentException.class,
            () -> readFields(input),
            "Reader should reject invalid field characters"
        );
    }

    /**
     * Parses all minefields in a string.
     *
     * @param input encoded minefield input
     * @return parsed fields
     */
    private static List<MineField> readFields(String input) {
        try (Scanner scanner = new Scanner(input)) {
            MineFieldReader reader = new MineFieldReader(scanner);
            return reader.readFields();
        }
    }

    /**
     * Checks a parsed field against an expected solution representation.
     *
     * @param field parsed field
     * @param expected rows containing mines and adjacency counts
     */
    private static void checkField(
            MineField field,
            String[] expected) {

        assertTrue(
            field.numRows() == expected.length,
            "Incorrect number of rows"
        );

        assertTrue(
            field.numCols() == expected[0].length(),
            "Incorrect number of columns"
        );

        for (int row = 0; row < expected.length; row++) {
            for (int col = 0; col < expected[row].length(); col++) {
                Square square = field.getSquare(row, col);
                char expectedValue = expected[row].charAt(col);

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

                    int expectedCount = expectedValue - '0';

                    assertTrue(
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
}
