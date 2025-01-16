package es.dolfi.minesweeper.enums;

import java.awt.Dimension;

/**
 * Represents the difficulty of the game, with size-related utils
 */
public enum Difficulty {
    // Presets taken off Google Minesweeper
    EASY(10, 8, 10),
    MEDIUM(18, 14, 40),
    HARD(24, 20, 99);

    final private int width;
    final private int height;
    final private int mines;

    /**
     * Create a new difficulty
     *
     * @param width  The width of the board
     * @param height The height of the board
     * @param mines  The number of mines on the board
     */
    private Difficulty(int width, int height, int mines) {
        this.width = width;
        this.height = height;
        this.mines = mines;
    }

    /**
     * Get the width of the board (number of columns)
     *
     * @return The width
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * Get the height of the board (number of rows)
     *
     * @return The height
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * Get the total number of cells on the board
     *
     * @return The number of cells
     */
    public int getCells() {
        return this.width * this.height;
    }

    /**
     * Get the total number of mines on the board
     *
     * @return The number of mines
     */
    public int getMines() {
        return this.mines;
    }

    /**
     * Get the size of an individual cell (in pixels)
     *
     * @return The size
     */
    public int getCellSize() {
        // Cell size depends on the amount of squares
        int squares = this.width * this.height;
        if (squares <= 200) {
            return 45;
        } else if (squares <= 400) {
            return 30;
        } else {
            return 25;
        }
    }

    /**
     * Get the dimensions of the game, including padding
     *
     * @return The dimensions
     */
    public Dimension getDimensions() {
        // We increase the size of the screen depending on the amount of squares
        return new Dimension(this.width * this.getCellSize() + 100, this.height * this.getCellSize() + 175);
    }

    /**
     * Get the name of the difficulty, with the first letter capitalized
     */
    @Override
    public String toString() {
        return this.name().charAt(0) + this.name().substring(1).toLowerCase();
    }

    /**
     * Get the names of all the difficulties
     *
     * @return The names
     */
    public static String[] getNames() {
        Difficulty[] difficulties = values();
        String[] names = new String[difficulties.length];
        for (int i = 0; i < difficulties.length; i++) {
            names[i] = difficulties[i].toString();
        }
        return names;
    }
}
