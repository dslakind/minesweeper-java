package minesweeper;

public enum Difficulty {
    BEGINNER(9, 9, 10),
    INTERMEDIATE(16, 16, 40), 
    EXPERT(16, 30, 99);

    private final int rows;
    private final int columns;
    private final int mineCount;

    private Difficulty(int rows, int columns, int mines) {
        this.rows = rows;
        this.columns = columns;
        this.mineCount = mines;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public int getMineCount() {
        return mineCount;
    }

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
