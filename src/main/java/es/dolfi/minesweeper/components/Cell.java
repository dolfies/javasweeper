package es.dolfi.minesweeper.components;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JComponent;

import es.dolfi.minesweeper.enums.Difficulty;
import es.dolfi.minesweeper.screens.GameBoard;
import es.dolfi.minesweeper.util.SoundManager;
import es.dolfi.minesweeper.util.SpriteSheet;

/**
 * Represents a single cell on the game board
 */
public class Cell extends JComponent {
    public static final SpriteSheet minesweeps = SpriteSheet.load("spritresheet.png", 32);
    private final GameBoard board;
    private final int column;
    private final int row;
    private boolean flagged = false;
    private boolean revealed = false;
    private boolean mine;

    /**
     * Create a new cell
     *
     * @param board      The game board
     * @param difficulty The game difficulty
     * @param column     The column of the cell
     * @param row        The row of the cell
     * @param mine       Whether the cell is a mine
     */
    public Cell(GameBoard board, Difficulty difficulty, int column, int row, boolean mine) {
        this.board = board;
        this.column = column;
        this.row = row;
        this.mine = mine;

        int cellSize = difficulty.getCellSize();
        this.setPreferredSize(new Dimension(cellSize, cellSize));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = column;
        constraints.gridy = row;
        constraints.insets = new Insets(0, 0, 0, 0);

        this.board.add(this, constraints);
        this.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Difficulty difficulty = this.board.getGame().getDifficulty();
        int cellSize = difficulty.getCellSize();

        BufferedImage sprite;
        if (this.revealed) {
            if (this.mine) {
                sprite = minesweeps.getSprite(0, 1);
                if (this.flagged) {
                    sprite = minesweeps.getSprite(5, 1);
                }
            } else {
                int surroundingMineCount = this.getSurroundingMineCount();
                if (surroundingMineCount == 0) {
                    sprite = minesweeps.getSprite(1, 1);
                } else {
                    sprite = minesweeps.getSprite(surroundingMineCount - 1, 0);
                }
            }
        } else {
            sprite = minesweeps.getSprite(2, 1);
            if (this.flagged) {
                sprite = minesweeps.getSprite(3, 1);
            }
        }

        g.drawImage(sprite, 0, 0, cellSize, cellSize, null);
    }

    /**
     * Get the number of mines surrounding the cell
     *
     * @return The number of mines
     */
    private int getSurroundingMineCount() {
        Difficulty difficulty = this.board.getGame().getDifficulty();

        // Find the cells that are one above, below, to the left, to the right, diagonal
        int count = 0;
        Cell cell;
        for (int i = Math.max(0, this.column - 1); i <= Math.min(difficulty.getWidth() - 1, this.column + 1); i++) {
            for (int j = Math.max(0, this.row - 1); j <= Math.min(difficulty.getHeight() - 1, this.row + 1); j++) {
                cell = this.board.getCell(i, j).get();
                if (cell.isMine()) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Reveal all surrounding cells that are eligible to be revealed
     */
    private void revealEligibleCells() {
        Difficulty difficulty = this.board.getGame().getDifficulty();

        // Find the cells that are one above, below, to the left, to the right, diagonal
        Cell cell;
        for (int i = Math.max(0, this.column - 1); i <= Math.min(difficulty.getWidth() - 1, this.column + 1); i++) {
            for (int j = Math.max(0, this.row - 1); j <= Math.min(difficulty.getHeight() - 1, this.row + 1); j++) {
                cell = this.board.getCell(i, j).get();
                if (!cell.isRevealed() && !cell.isMine()) {
                    cell.setRevealed(true);
                }
            }
        }
    }

    /**
     * Check if a cell is a neighbor of this cell
     *
     * @param cell The cell to check
     * @return Whether the cell is a neighbor
     */
    public boolean isNeighbor(Cell cell) {
        // Check if the cell is one above, below, to the left, to the right, diagonal
        return Math.abs(this.column - cell.getColumn()) <= 1 && Math.abs(this.row - cell.getRow()) <= 1;
    }

    /**
     * Reveal the neighbors of this cell if the mine count is satisfied
     */
    public void revealNeighbors() {
        Difficulty difficulty = this.board.getGame().getDifficulty();

        // Find the cells that are one above, below, to the left, to the right, diagonal
        ArrayList<Cell> neighbors = new ArrayList<>();
        int flagged = 0;
        Cell cell;
        for (int i = Math.max(0, this.column - 1); i <= Math.min(difficulty.getWidth() - 1, this.column + 1); i++) {
            for (int j = Math.max(0, this.row - 1); j <= Math.min(difficulty.getHeight() - 1, this.row + 1); j++) {
                cell = this.board.getCell(i, j).get();
                if (cell.isFlagged()) {
                    flagged++;
                }
                if (!cell.isRevealed() && !cell.isFlagged()) {
                    neighbors.add(cell);
                }
            }
        }

        // If the mine count is satisfied, reveal the neighbors
        int mineCount = this.getSurroundingMineCount();
        if (neighbors.size() > 0 && flagged == mineCount) {
            for (Cell neighbor : neighbors) {
                neighbor.setRevealed(true);
            }
            SoundManager soundManager = this.board.getGame().getSoundManager();
            SoundManager.Sound sound = soundManager.get("sweep" + this.board.getRandom(1, 5));
            sound.play();
        }
    }

    /**
     * Get the column of the cell on the board
     *
     * @return The column
     */
    public int getColumn() {
        return this.column;
    }

    /**
     * Get the row of the cell on the board
     *
     * @return The row
     */
    public int getRow() {
        return this.row;
    }

    /**
     * Get whether the cell is flagged
     *
     * @return Whether the cell is flagged
     */
    public boolean isFlagged() {
        return this.flagged;
    }

    /**
     * Set whether the cell is flagged
     *
     * @param flagged Whether the cell is flagged
     */
    public void setFlagged(boolean flagged) {
        if (flagged == this.flagged) {
            return;
        }

        this.flagged = flagged;
        this.board.updateFlagCount(flagged ? -1 : 1);
        this.board.repaint();
        this.repaint();
        System.out.printf("[CELL] %slagged (%d, %d)%n", flagged ? "F" : "Unf", this.column, this.row);

        SoundManager soundManager = this.board.getGame().getSoundManager();
        SoundManager.Sound sound = soundManager.get(flagged ? "flag" : "unflag");
        sound.play();
    }

    /**
     * Get whether the cell is revealed
     *
     * @return Whether the cell is revealed
     */
    public boolean isRevealed() {
        return this.revealed;
    }

    /**
     * Set whether the cell is revealed
     *
     * @param revealed Whether the cell is revealed
     */
    public void setRevealed(boolean revealed) {
        this.setRevealed(revealed, true);
    }

    /**
     * Set whether the cell is revealed
     *
     * @param revealed Whether the cell is revealed
     * @param action   Whether to check win/lose status and reveal eligible cells
     */
    public void setRevealed(boolean revealed, boolean action) {
        if (revealed == this.revealed) {
            return;
        }

        this.revealed = revealed;
        this.repaint();
        if (revealed && action) {
            if (this.mine) {
                this.board.lose();
            } else {
                this.board.updateRevealedCount();
            }
        }
        if (this.getSurroundingMineCount() == 0 && action) {
            this.revealEligibleCells();
        }
        System.out.printf("[CELL] %sevealed (%d, %d)%n", revealed ? "R" : "Unr", this.column, this.row);
    }

    /**
     * Get whether the cell is a mine
     *
     * @return Whether the cell is a mine
     */
    public boolean isMine() {
        return this.mine;
    }

    /**
     * Set whether the cell is a mine
     *
     * @param mine Whether the cell is a mine
     */
    public void setMine(boolean mine) {
        this.mine = mine;
    }
}
