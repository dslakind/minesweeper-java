package minesweeper;

import java.util.Objects;

/** Formats a minefield as the numbered solution used by the console program. */
public class SolutionFormatter {
    /** Creates a solution formatter. */
    public SolutionFormatter() {
    }

    /**
     * Produces a labeled solution in which mines are asterisks and safe squares
     * contain their adjacent-mine counts.
     *
     * @param field field to format
     * @param fieldNum one-based field number used in the heading
     * @return formatted field, ending with a newline
     * @throws NullPointerException if {@code field} is {@code null}
     * @throws IllegalArgumentException if {@code fieldNum} is less than one
     */
    public static String format(MineField field, int fieldNum) {
        Objects.requireNonNull(field, "Field cannot be null");

        if (fieldNum < 1) {
            throw new IllegalArgumentException(
                "Field number must be at least 1"
            );
        }
        
        StringBuilder result = new StringBuilder("Field #" + fieldNum + ":\n");
        
        for (int row = 0; row < field.numRows(); row++) {
            for (int col = 0; col < field.numCols(); col++) {
                Square square = field.getSquare(row, col); 
                if (square.hasMine()) {
                    result.append("*");
                } else {
                    result.append(square.getAdjacentMineCount());
                }
            }
            result.append("\n");
        }

        return result.toString();
    }
}
