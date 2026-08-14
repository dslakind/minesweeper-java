package minesweeper;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/** Command-line application that reads and solves text-encoded minefields. */
public class MinesweeperApplication {
    private List<MineField> fields;

    /** Creates an application with no fields loaded. */
    public MinesweeperApplication() {
        this.fields = new ArrayList<MineField>();
    }

    /**
     * Reads all fields from an input source and writes their formatted solutions.
     *
     * @param source scanner supplying encoded fields
     * @param output destination for formatted solutions
     * @throws NullPointerException if either argument is {@code null}
     */
    void run(Scanner source, PrintStream output) {
        Objects.requireNonNull(source, "Input source cannot be null");
        Objects.requireNonNull(output, "Output stream cannot be null");
        MineFieldReader reader = new MineFieldReader(source);
        fields = reader.readFields();
        outputFields(output);
    }

    private void outputFields(PrintStream output) {
        Objects.requireNonNull(output, "Output stream cannot be null");
        for (int i = 0; i < fields.size(); i++) {
            output.print(SolutionFormatter.format(fields.get(i), i + 1));

            if (i < fields.size() - 1) {
                output.println();
            }
        }
    }

    /**
     * Starts the command-line application using standard input and output.
     *
     * @param args command-line arguments; currently ignored
     */
    public static void main(String[] args) {
        Scanner source = new Scanner(System.in);
        
        MinesweeperApplication minesweeper = new MinesweeperApplication();
        minesweeper.run(source, System.out);

        
    }
}
