package minesweeper;

import java.util.Objects;

/**
 * Coordinates player actions and tracks the status of a Minesweeper game.
 */
public class Game {
    private final MineField field;
    private GameStatus status;

    /**
     * Creates an in-progress game using the supplied minefield.
     *
     * @param initField field on which the game is played
     * @throws NullPointerException if {@code initField} is {@code null}
     */
    public Game(MineField initField) {
        field = Objects.requireNonNull(initField, "Game requires a MineField");
        status = GameStatus.IN_PROGRESS;
    }

    /**
     * Reveals a square and, when appropriate, its connected safe region.
     * Revealing a mine loses the game; revealing every safe square wins it.
     * This method has no effect after the game ends or when the target square
     * is already revealed or flagged.
     *
     * @param row zero-based row of the square
     * @param col zero-based column of the square
     * @throws IndexOutOfBoundsException if the coordinates are outside the field
     */
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

    /**
     * Adds or removes a flag from a square while the game is in progress.
     *
     * @param row zero-based row of the square
     * @param col zero-based column of the square
     * @throws IndexOutOfBoundsException if the coordinates are outside the field
     */
    public void toggleFlag(int row, int col) {
        Square square = getSquare(row, col);
        if(status == GameStatus.IN_PROGRESS) {
            square.toggleFlag();
        }
    }

    /**
     * Returns the current outcome of the game.
     *
     * @return current game status
     */
    public GameStatus getStatus() {
        return status;
    }

    /**
     * Returns the number of rows in the field.
     *
     * @return row count
     */
    public int numRows() {
        return field.numRows();
    }

    /**
     * Returns the number of columns in the field.
     *
     * @return column count
     */
    public int numCols() {
        return field.numCols();
    }

    /**
     * Returns the square at the specified coordinates.
     *
     * @param row zero-based row
     * @param col zero-based column
     * @return square at the requested position
     * @throws IndexOutOfBoundsException if the coordinates are outside the field
     */
    public Square getSquare(int row, int col) {
        return field.getSquare(row, col);
    }

    /** @return the number of remaining mines  */
    public int numRemainingMines() {
        return field.numMines() - field.numFlags();
    }
}
