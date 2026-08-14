package minesweeper;

import java.util.Objects;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Represents a rectangular Minesweeper field and its mine-adjacency data.
 */
public class MineField {
    private final Square[][] squares;

    /**
     * Creates a field from rows containing {@code '*'} for mines and
     * {@code '.'} for safe squares.
     *
     * @param rows rectangular textual representation of the field
     * @throws NullPointerException if the array or any row is {@code null}
     * @throws IllegalArgumentException if the field is empty, non-rectangular,
     *         or contains an unsupported character
     */
    public MineField(String[] rows) {
        // validate that the matrix is nonempty and rectangular
        Objects.requireNonNull(rows, "MineField requires a String[] to process");
        int index = 0;
        if (rows.length == 0) {
            throw new IllegalArgumentException("MineField must contain at least one row");
        }
        Objects.requireNonNull(rows[0], "MineField rows cannot be null");

        int len = rows[0].length();

        if (len == 0) {
            throw new IllegalArgumentException("MineField rows cannot be empty");
        }

        do {
            String line = Objects.requireNonNull(
                rows[index], "MineField rows cannot be null"
            );
            if (line.length() != len) {
                 throw new IllegalArgumentException(
                    "Each row must have the same length"
                );
            }
            index++;
        } while (index < rows.length); 

        squares = new Square[rows.length][len];

        // initialize squares 
        for (int row = 0; row < rows.length; row++) {
            String line = rows[row];
            for (int col = 0; col < len; col++) {
                char symbol = line.charAt(col);
                if (symbol == '*') {
                    squares[row][col] = new Square(true);
                } else if (symbol != '.'){
                    throw new IllegalArgumentException(
                        "Invalid symbol at (" + row + ", " + col + "): " + symbol
                    );

                } else {
                    squares[row][col] = new Square(false);
                }
            }
        }

        // update squares with number of adjacent mines
        calculateAdjacentMines();
    }

    /**
     * Returns the height of this field.
     *
     * @return number of rows in this field
     */
    public int numRows() {
        return squares.length;
    }

    /**
     * Returns the width of this field.
     *
     * @return number of columns in this field
     */
    public int numCols() {
        return squares[0].length;
    }

    /**
     * Returns a square by its zero-based coordinates.
     *
     * @param row row index
     * @param col column index
     * @return square at the requested position
     * @throws IndexOutOfBoundsException if the coordinates are outside the field
     */
    public Square getSquare(int row, int col) {
        if (!isValidSquare(row, col)) {
            throw new IndexOutOfBoundsException(
                "Invalid square position: (" + row + ", " + col + ")"
            );
        }
        return squares[row][col];
    }

    /**
     * Determines whether coordinates identify a square in this field.
     *
     * @param row row index to test
     * @param col column index to test
     * @return {@code true} when the coordinates are within the field
     */
    public boolean isValidSquare(int row, int col) {
        return row >= 0 && row < numRows() && 
            col >= 0 && col < numCols();
    }

    private boolean isEligibleForRegionReveal(int row, int col){
        // Are the coordinates valid?
        if (!isValidSquare(row, col)) {
            return false;
        } 

        // Is the square already revealed?
        // Is the square flagged?
        // Does it contain a mine?
        return 
            getSquare(row, col).getState() != SquareState.REVEALED &&
            getSquare(row, col).getState() != SquareState.FLAGGED &&
            !getSquare(row, col).hasMine();
    }

    /**
     * Reveals a safe square and expands through adjacent empty squares. Numbered
     * boundary squares are revealed, while mines and flagged squares remain hidden.
     *
     * @param row row at which to begin revealing
     * @param col column at which to begin revealing
     * @throws IndexOutOfBoundsException if the coordinates are outside the field
     */
    public void revealRegion(int row, int col) {
        if (!isValidSquare(row, col)) {
            throw new IndexOutOfBoundsException(
                "Invalid square position: (" + row + ", " + col + ")"
            );
        }

        if (!isEligibleForRegionReveal(row, col)) {
            return;
        }

        // The queue contains coordinates waiting to be processed.
        Queue<Position> positionsToProcess = new ArrayDeque<>();
        
        // A boolean[][] to record which positions have already been placed in the queue
        boolean[][] scheduledPositions = new boolean[numRows()][numCols()];

        positionsToProcess.offer(new Position(row, col));
        scheduledPositions[row][col] = true;

        while (!positionsToProcess.isEmpty()) {

            Position currPosition = positionsToProcess.poll();
            int currRow = currPosition.getRow();
            int currCol = currPosition.getCol();
            
            if (!isEligibleForRegionReveal(currRow, currCol)) {
                continue;
            }

            Square currSquare = getSquare(currRow, currCol);
            currSquare.reveal();

            // Examining neighbors
            if (currSquare.getAdjacentMineCount() == 0) {
                for(int r = currRow - 1; r <= currRow + 1; r++) {
                    for(int c = currCol - 1; c <= currCol + 1; c++) {
                        if (r != currRow || c != currCol) {
                            schedulePositionIfEligible(
                                r, 
                                c, 
                                positionsToProcess, 
                                scheduledPositions
                            );
                        }
                    }
                }
            }
        }
    }

    private void schedulePositionIfEligible(
        int row, int col, 
        Queue<Position> positionsToProcess, 
        boolean[][] scheduledPositions
    ) {
        if (isEligibleForRegionReveal(row, col) 
            && !scheduledPositions[row][col]) {
            positionsToProcess.offer(new Position(row, col));
            scheduledPositions[row][col] = true;
        } 

    }

    /**
     * Reports whether all non-mine squares have been revealed.
     *
     * @return {@code true} if no safe hidden or flagged squares remain
     */
    public boolean allSafeSquaresRevealed() {
        for (Square[] row : squares) {
            for (Square square : row) {
                if (!square.hasMine() && square.getState() != SquareState.REVEALED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void calculateAdjacentMines() {
        for (int row = 0; row < numRows(); row++) {
            for (int col = 0; col < numCols(); col++) {
                calculateAdjacentMinesHelper(row, col);
            }
        }
    }

    private void calculateAdjacentMinesHelper(int row, int col) {
        int result = 0;
        for (int r = row - 1; r <= row + 1; r++) {
            for (int c = col - 1; c <= col + 1; c++) {
                if (isValidSquare(r, c) && (r != row || c != col) && squares[r][c].hasMine()) {
                    result++;
                }
            }
        }
        squares[row][col].setAdjacentMineCount(result);
    }


    /** Immutable row and column used by the region-reveal traversal. */
    private static class Position {

        private final int row;
        private final int col; 

        Position(int initRow, int initCol) {
            row = initRow;
            col = initCol;
        }

        int getRow() {
            return row;
        }

        int getCol() {
            return col;
        }

        @Override
        public String toString() {
            return "Position [row=" + row + ", col=" + col + "]";
        }
        
    }

}
