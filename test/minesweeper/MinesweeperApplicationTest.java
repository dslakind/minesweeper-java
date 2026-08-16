package minesweeper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

/** Exercises console input handling, formatting, and validation failures. */
public class MinesweeperApplicationTest {


    /** Verifies output for the complete sample containing two fields. */
    @Test
    void testSampleInput() {
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
            + "0 0";  // Deliberately no final newline

        String expected =
            "Field #1:\n"
            + "*100\n"
            + "2210\n"
            + "1*10\n"
            + "1110\n"
            + "\n"
            + "Field #2:\n"
            + "**100\n"
            + "33200\n"
            + "1*100\n";

        String actual = runApplication(input);

        assertEquals(
            expected,
            actual,
            "Sample input output is incorrect"
        );
    }

    /** Verifies formatting of a single field without an extra blank line. */
    @Test
    void testSingleField() {
        String input =
            "1 1\n"
            + ".\n"
            + "0 0";

        String expected =
            "Field #1:\n"
            + "0\n";

        String actual = runApplication(input);

        assertEquals(
            expected,
            actual,
            "Single-field output is incorrect"
        );

        assertTrue(
            !actual.endsWith("\n\n"),
            "A blank line should not follow the final field"
        );
    }

    /** Verifies that an immediate terminator produces no output. */
    @Test
    void testImmediateTerminator() {
        String actual = runApplication("0 0");

        assertEquals(
            "",
            actual,
            "An immediate terminator should produce no output"
        );
    }

    /** Verifies rejection of a null input scanner. */
    @Test
    void testNullSource() {
        ByteArrayOutputStream capturedOutput =
            new ByteArrayOutputStream();

        try (PrintStream output = new PrintStream(capturedOutput)) {
            MinesweeperApplication application =
                new MinesweeperApplication();

            assertThrows(
                NullPointerException.class,
                () -> application.run(null, output),
                "run() should reject a null input source"
            );
        }
    }

    /** Verifies rejection of a null output stream. */
    @Test
    void testNullOutput() {
        try (Scanner source = new Scanner("0 0")) {
            MinesweeperApplication application =
                new MinesweeperApplication();

            assertThrows(
                NullPointerException.class,
                () -> application.run(source, null),
                "run() should reject a null output stream"
            );
        }
    }

    /** Verifies propagation of malformed-field errors. */
    @Test
    void testMalformedField() {
        String input =
            "2 3\n"
            + "...\n"
            + "..\n"
            + "0 0";

        assertThrows(
            IllegalStateException.class,
            () -> runApplication(input),
            "Application should reject a row with the wrong length"
        );
    }

    /**
     * Runs the console application with string-backed input and captures output.
     *
     * @param input encoded application input
     * @return text written by the application
     */
    private static String runApplication(String input) {
        ByteArrayOutputStream capturedOutput =
            new ByteArrayOutputStream();

        try (
            Scanner source = new Scanner(input);
            PrintStream output = new PrintStream(capturedOutput)
        ) {
            MinesweeperApplication application =
                new MinesweeperApplication();

            application.run(source, output);
            output.flush();

            return capturedOutput.toString();
        }
    }
}
