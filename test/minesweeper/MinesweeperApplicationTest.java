package minesweeper;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

/** Exercises console input handling, formatting, and validation failures. */
public class MinesweeperApplicationTest {

    /**
     * Runs all {@link MinesweeperApplication} tests.
     *
     * @param args command-line arguments; ignored
     */
    public static void main(String[] args) {
        testSampleInput();
        testSingleField();
        testImmediateTerminator();
        testNullSource();
        testNullOutput();
        testMalformedField();

        System.out.println("All MinesweeperApplication tests passed.");
    }

    /** Verifies output for the complete sample containing two fields. */
    private static void testSampleInput() {
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

        checkEquals(
            expected,
            actual,
            "Sample input output is incorrect"
        );
    }

    /** Verifies formatting of a single field without an extra blank line. */
    private static void testSingleField() {
        String input =
            "1 1\n"
            + ".\n"
            + "0 0";

        String expected =
            "Field #1:\n"
            + "0\n";

        String actual = runApplication(input);

        checkEquals(
            expected,
            actual,
            "Single-field output is incorrect"
        );

        check(
            !actual.endsWith("\n\n"),
            "A blank line should not follow the final field"
        );
    }

    /** Verifies that an immediate terminator produces no output. */
    private static void testImmediateTerminator() {
        String actual = runApplication("0 0");

        checkEquals(
            "",
            actual,
            "An immediate terminator should produce no output"
        );
    }

    /** Verifies rejection of a null input scanner. */
    private static void testNullSource() {
        ByteArrayOutputStream capturedOutput =
            new ByteArrayOutputStream();

        try (PrintStream output = new PrintStream(capturedOutput)) {
            MinesweeperApplication application =
                new MinesweeperApplication();

            expectException(
                NullPointerException.class,
                () -> application.run(null, output),
                "run() should reject a null input source"
            );
        }
    }

    /** Verifies rejection of a null output stream. */
    private static void testNullOutput() {
        try (Scanner source = new Scanner("0 0")) {
            MinesweeperApplication application =
                new MinesweeperApplication();

            expectException(
                NullPointerException.class,
                () -> application.run(source, null),
                "run() should reject a null output stream"
            );
        }
    }

    /** Verifies propagation of malformed-field errors. */
    private static void testMalformedField() {
        String input =
            "2 3\n"
            + "...\n"
            + "..\n"
            + "0 0";

        expectException(
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

    /**
     * Fails the test run when a condition is false.
     *
     * @param condition condition that must hold
     * @param message failure message
     */
    private static void check(
            boolean condition,
            String message) {

        if (!condition) {
            throw new AssertionError(message);
        }
    }

    /**
     * Checks two strings for exact equality.
     *
     * @param expected expected text
     * @param actual actual text
     * @param message failure message
     */
    private static void checkEquals(
            String expected,
            String actual,
            String message) {

        if (!expected.equals(actual)) {
            throw new AssertionError(
                message
                    + "\nExpected:\n"
                    + showWhitespace(expected)
                    + "\nActual:\n"
                    + showWhitespace(actual)
            );
        }
    }

    /**
     * Makes spaces and line endings visible in assertion messages.
     *
     * @param text text to annotate
     * @return text with visible whitespace markers
     */
    private static String showWhitespace(String text) {
        return text
            .replace(" ", "·")
            .replace("\r", "\\r")
            .replace("\n", "\\n\n");
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
            String message) {

        try {
            action.run();
        } catch (Throwable exception) {
            if (expectedType.isInstance(exception)) {
                return;
            }

            throw new AssertionError(
                message
                    + "; expected "
                    + expectedType.getSimpleName()
                    + " but received "
                    + exception.getClass().getSimpleName(),
                exception
            );
        }

        throw new AssertionError(
            message
                + "; expected "
                + expectedType.getSimpleName()
                + " but no exception was thrown"
        );
    }
}
