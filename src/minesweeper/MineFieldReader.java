package minesweeper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/** Reads one or more text-encoded minefields from a {@link Scanner}. */
public class MineFieldReader {
    private final Scanner source;

    /**
     * Creates a reader that consumes data from the supplied scanner.
     *
     * @param sourceScanner scanner containing field dimensions and rows
     * @throws NullPointerException if {@code sourceScanner} is {@code null}
     */
    public MineFieldReader(Scanner sourceScanner) {
        source = Objects.requireNonNull(
            sourceScanner, "MineFieldReader requires a Scanner"
        );
    }    

    /**
     * Reads fields until the input ends or a {@code 0 0} terminator is found.
     * Dimensions must be between 1 and 100, and every row must have the declared
     * width.
     *
     * @return fields in their input order
     * @throws IllegalStateException if the input is malformed or incomplete
     * @throws IllegalArgumentException if a field contains an invalid symbol
     */
    public List<MineField> readFields() {
        List<MineField> fields = new ArrayList<>();

        // read the next dimension line, with number of rows and cols
        while (source.hasNext()) {
            if (!source.hasNextInt()) {
                throw new IllegalStateException(
                    "Expected the number of field rows"
                );
            }
            int rows = source.nextInt();

            if (!source.hasNextInt()) {
                throw new IllegalStateException(
                    "Expected the number of field columns"
                );
            }
            int cols = source.nextInt();

             // Process dimensions and field...
            if (rows == 0 && cols == 0) {
                break;
            } else if (rows == 0) {
                throw new IllegalStateException(
                    "Field must contain at least one row"
                );
            } else if (cols == 0) {
                throw new IllegalStateException(
                    "Field rows cannot be length zero columns"
                );
            } else if (rows < 1 || rows > 100 || cols < 1 || cols > 100) {
                throw new IllegalStateException(
                    "Field dimensions must be between 1 and 100"
                );
            }
            source.nextLine(); // consume the remainder of the dimension line

            // construct the new field using lines. 
            fields.add(readField(rows, cols));
        }
        
        return fields;
    }

    private MineField readField(int rows, int cols) {
        // read the field data
        String[] lines = new String[rows];

        for (int i = 0; i < rows; i++) {
            if (!source.hasNextLine()) {
                throw new IllegalStateException("incorrect number of lines in source");
            }
            lines[i] = source.nextLine();
            
            if (lines[i].length() != cols) {
                throw new IllegalStateException(
                    "Row " + i + " contains " + lines[i].length()
                        + " characters; expected " + cols
                );
            }
        }

        // construct the new field using lines. 
        return new MineField(lines);

    }
}
