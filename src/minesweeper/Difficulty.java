package minesweeper;

/**
 * Defines the board dimensions and mine count for each supported game level.
 */
public enum Difficulty {
    /** A 9-by-9 field containing 10 mines. */
    BEGINNER(9, 9, 10),
    /** A 16-by-16 field containing 40 mines. */
    INTERMEDIATE(16, 16, 40), 
    /** A 16-by-30 field containing 99 mines. */
    EXPERT(16, 30, 99);

    private final int rows;
    private final int columns;
    private final int mineCount;

    /**
     * Creates a difficulty configuration.
     *
     * @param rows field height
     * @param columns field width
     * @param mines number of mines
     */
    private Difficulty(int rows, int columns, int mines) {
        this.rows = rows;
        this.columns = columns;
        this.mineCount = mines;
    }

    /**
     * Returns the configured field height.
     *
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * Returns the configured field width.
     *
     * @return number of columns
     */
    public int getColumns() {
        return columns;
    }

    /**
     * Returns the configured number of mines.
     *
     * @return number of mines
     */
    public int getMineCount() {
        return mineCount;
    }

    /**
     * Returns the difficulty name displayed in the user interface.
     *
     * @return title-cased difficulty name
     */
    @Override
    public String toString() {
        switch (this) {
            case BEGINNER:
                return "Beginner";
            case INTERMEDIATE:
                return "Intermediate";
            case EXPERT:
                return "Expert";
            default:
                return "Beginner";
        }

    }
    
}
