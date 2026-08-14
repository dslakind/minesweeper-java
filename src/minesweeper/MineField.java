package minesweeper;

import java.util.Objects;
import java.util.ArrayDeque;
import java.util.Queue;


public class MineField {
    private final Square[][] squares;

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

    public int numRows() {
        return squares.length;
    }

    public int numCols() {
        return squares[0].length;
    }

    public Square getSquare(int row, int col) {
        if (!isValidSquare(row, col)) {
            throw new IndexOutOfBoundsException(
                "Invalid square position: (" + row + ", " + col + ")"
            );
        }
        return squares[row][col];
    }

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

