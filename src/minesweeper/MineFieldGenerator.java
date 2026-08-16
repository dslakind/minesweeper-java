package minesweeper;

import java.util.Objects;
import java.util.Random;

/** Creates randomized Minesweeper fields with a requested size and mine count. */
public class MineFieldGenerator {

    private final Random rand;

    /** Creates a generator backed by a new pseudorandom number generator. */
    public MineFieldGenerator() {
        this(new Random());
    }

    /**
     * Creates a generator backed by the supplied random source.
     *
     * @param random random source used to choose mine positions
     * @throws NullPointerException if {@code random} is {@code null}
     */
    public MineFieldGenerator(Random random) {
        rand = Objects.requireNonNull(
            random, 
            "MineFieldGenerator requires a non-null Random object"
        );
    }

    /**
     * Generates a field containing distinct, randomly positioned mines.
     *
     * @param rows number of rows; must be positive
     * @param columns number of columns; must be positive
     * @param mineCount number of mines; must be nonnegative and smaller than
     *        the number of squares
     * @return newly generated minefield
     * @throws IllegalArgumentException if the dimensions or mine count are invalid
     */
    public MineField generate(int rows, int columns, int mineCount) {
        // Validate before multiplying the dimensions or allocating the field.
        if (rows <= 0 || columns <= 0) {
            throw new IllegalArgumentException(
                "MineField cannot have " + rows + " rows and " 
                + columns + " columns."
            );
        }

        // Leave at least one safe square so every generated game is playable.
        if (mineCount < 0 || mineCount >= rows * columns) {
            throw new IllegalArgumentException(
                "MineField cannot have " + mineCount + " mines for a field with " +
                rows + " rows and " + columns + " columns."
            );
        }


        char[][] chars = new char[rows][columns];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                chars[r][c] = '.';
            }
        }

        // The shuffled positions guarantee that mine locations are distinct.
        int[] randomPositions = getRandomMinePositions(rows, columns);

        // Place mines at the requested prefix of the shuffled positions.
        for (int i = 0; i < mineCount; i++) {
            int pos = randomPositions[i];
            int row = pos / columns;
            int col = pos % columns;
            chars[row][col] = '*';
        }

        // Convert the mutable construction matrix into MineField input rows.
        String[] rowStrings = new String[rows];
        for (int r = 0; r < rows; r++) {
            rowStrings[r] = new String(chars[r]);
        }

        return new MineField(rowStrings);
    }

    /**
     * Returns every field position in uniformly shuffled order.
     *
     * @param rows number of field rows
     * @param columns number of field columns
     * @return shuffled linear indexes for all field positions
     */
    private int[] getRandomMinePositions(int rows, int columns) {
        int[] positions = new int[rows * columns];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                positions[r * columns + c] = r * columns + c;
            }
        }

        // Apply an in-place Fisher-Yates shuffle.
        for (int i = positions.length - 1; i > 0; i--) {
            int index = rand.nextInt(i + 1);

            int temp = positions[i];
            positions[i] = positions[index];
            positions[index] = temp;
        }

        return positions;
    }

    /**
     * Generates a field using a predefined difficulty configuration.
     *
     * @param difficulty dimensions and mine count to use
     * @return newly generated minefield
     * @throws NullPointerException if {@code difficulty} is {@code null}
     */
    public MineField generate(Difficulty difficulty) {
        Objects.requireNonNull(
            difficulty, 
            "Difficulty level cannot be null."
        );
        
        return generate(
            difficulty.getRows(), 
            difficulty.getColumns(), 
            difficulty.getMineCount()
        );
    }

}
