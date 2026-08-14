package minesweeper;

import java.util.Objects;

public class SolutionFormatter {
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
