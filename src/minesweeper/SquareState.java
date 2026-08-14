package minesweeper;

/** Describes how a square is currently presented to the player. */
enum SquareState {
    /** The square has not been revealed or flagged. */
    HIDDEN,
    /** The square's contents are visible. */
    REVEALED,
    /** The player has marked the square as a possible mine. */
    FLAGGED;
}
