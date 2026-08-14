package minesweeper;

/**
 * Represents a Square in the Minesweeper game board
 * <p>
 * This class handles the character that should be used 
 * when displaying the minecraft board. It can display
 * a '.' for a hidden cell, a '*' for a bomb, or an integer
 * [0-8] for the number of adjacent bombs. 
 * 
 * </p>
 * 
 * @author David Lakind
 * @version 0.0
 * @since 08102026
 */

public class Square
{

    private int adjacentMineCount;
    private boolean hasMine;
    private SquareState state;

    public Square(boolean initMine) {
        hasMine = initMine;
        state = SquareState.HIDDEN;
        adjacentMineCount = 0; 
    }

    public SquareState getState() {
        return state;
    }

    protected void setAdjacentMineCount(int adjMineCount) {
        this.adjacentMineCount = adjMineCount;
    }
    
    public int getAdjacentMineCount() {
        return adjacentMineCount;
    }

    public boolean hasMine() {
        return hasMine;
    }

    public void toggleFlag() {
        if(state != SquareState.REVEALED) {
            state = (state == SquareState.FLAGGED) ? 
                SquareState.HIDDEN : SquareState.FLAGGED;
        }
    }

    public boolean reveal() {
        if(state == SquareState.HIDDEN) {
            state = SquareState.REVEALED;
            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        if (state == SquareState.HIDDEN) {
            return "Square [state=" + state + "]";
        }
        return "Square [adjacentMineCount=" + adjacentMineCount + ", hasMine=" + hasMine + ", state=" + state + "]";
    }
    
    
}