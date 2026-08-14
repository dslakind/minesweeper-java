package minesweeper;

import java.util.Objects;

public class Game {
    private final MineField field;
    private GameStatus status;

    public Game(MineField initField) {
        field = Objects.requireNonNull(initField, "Game requires a MineField");
        status = GameStatus.IN_PROGRESS;
    }

    public void revealSquare(int row, int col) {
        Square square = field.getSquare(row, col);

        if (status != GameStatus.IN_PROGRESS) {
            return; // do nothing
        }

        if (square.getState() == SquareState.FLAGGED 
            || square.getState() == SquareState.REVEALED) {
            return; // do nothing
        }

        if (square.hasMine()) {
            square.reveal();
            status = GameStatus.LOST;
            return; // game over
        } 
        
        field.revealRegion(row, col);
        
        if (field.allSafeSquaresRevealed()) {
            status = GameStatus.WON;
            return; // game over
        }
    }

    public void toggleFlag(int row, int col) {
        Square square = getSquare(row, col);
        if(status == GameStatus.IN_PROGRESS) {
            square.toggleFlag();
        }
    }

    public GameStatus getStatus() {
        return status;
    }

    public int numRows() {
        return field.numRows();
    }

    public int numCols() {
        return field.numCols();
    }

    public Square getSquare(int row, int col) {
        return field.getSquare(row, col);
    }
}
