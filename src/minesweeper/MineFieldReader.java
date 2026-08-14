package minesweeper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class MineFieldReader {
    private final Scanner source;

    public MineFieldReader(Scanner sourceScanner) {
        source = Objects.requireNonNull(
            sourceScanner, "MineFieldReader requires a Scanner"
        );
    }    

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
