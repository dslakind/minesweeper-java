package minesweeper;

/**
 * Represents one square in a Minesweeper field.
 * A square records whether it contains a mine, how many neighboring mines it
 * has, and whether it is hidden, revealed, or flagged.
 */
public class Square {

    private int adjacentMineCount;
    private boolean hasMine;
    private SquareState state;

    /**
     * Creates a hidden square.
     *
     * @param initMine whether the square contains a mine
     */
    public Square(boolean initMine) {
        hasMine = initMine;
        state = SquareState.HIDDEN;
        adjacentMineCount = 0; 
    }

    /**
     * Returns the square's current visibility or flag state.
     *
     * @return current square state
     */
    public SquareState getState() {
        return state;
    }

    /**
     * Records the number of mines neighboring this square.
     *
     * @param adjMineCount number of adjacent mines
     */
    protected void setAdjacentMineCount(int adjMineCount) {
        this.adjacentMineCount = adjMineCount;
    }
    
    /**
     * Returns the number of mines in the eight neighboring positions.
     *
     * @return adjacent mine count
     */
    public int getAdjacentMineCount() {
        return adjacentMineCount;
    }

    /**
     * Reports whether this square contains a mine.
     *
     * @return {@code true} if this square contains a mine
     */
    public boolean hasMine() {
        return hasMine;
    }

    /**
     * Switches a hidden square to flagged, or a flagged square to hidden.
     * Revealed squares are unchanged.
     */
    public void toggleFlag() {
        if(state != SquareState.REVEALED) {
            state = (state == SquareState.FLAGGED) ? 
                SquareState.HIDDEN : SquareState.FLAGGED;
        }
    }

    /**
     * Reveals this square if it is currently hidden.
     *
     * @return {@code true} if the state changed to revealed
     */
    public boolean reveal() {
        if(state == SquareState.HIDDEN) {
            state = SquareState.REVEALED;
            return true;
        }

        return false;
    }

    /**
     * Returns a diagnostic representation of this square.
     *
     * @return square state and, when visible, its mine data
     */
    @Override
    public String toString() {
        if (state == SquareState.HIDDEN) {
            return "Square [state=" + state + "]";
        }
        return "Square [adjacentMineCount=" + adjacentMineCount + ", hasMine=" + hasMine + ", state=" + state + "]";
    }
}
