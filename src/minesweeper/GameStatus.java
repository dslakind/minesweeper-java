package minesweeper;

/** Describes whether a Minesweeper game is active, won, or lost. */
enum GameStatus {
    /** The player can continue revealing and flagging squares. */
    IN_PROGRESS,
    /** Every safe square has been revealed. */
    WON,
    /** The player has revealed a mine. */
    LOST;
}
