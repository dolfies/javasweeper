package es.dolfi.minesweeper.screens;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import es.dolfi.minesweeper.Game;
import es.dolfi.minesweeper.components.Cell;
import es.dolfi.minesweeper.enums.*;
import es.dolfi.minesweeper.util.*;

/**
 * Main game screen
 */
public class GameBoard extends Screen {
    private static final Random random = new Random();
    public static final SpriteSheet minesweeps = SpriteSheet.load("spritresheet.png", 32);
    public final Cell[][] board;

    private TimerComponent ticker;
    private Timer animator;

    // Seperate count for performance reasons
    private int flagCount = 0;
    private int revealedCount = 0;

    /**
     * Create a new game board
     *
     * @param game The game instance
     */
    public GameBoard(Game game) {
        super(game);
        this.setLayout(new GridBagLayout());

        // Set size based on difficulty
        Difficulty difficulty = game.getDifficulty();
        board = new Cell[difficulty.getWidth()][difficulty.getHeight()];
        flagCount = difficulty.getMines();
        game.setSize(difficulty.getDimensions());

        // Add all the components
        this.ticker = new TimerComponent(this, difficulty);
        this.generateCells(difficulty);
        this.addMouseListener(new MouseInput(this));
        this.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw a 2px border around the board itself
        Difficulty difficulty = this.getGame().getDifficulty();
        int width = difficulty.getWidth(), height = difficulty.getHeight();
        int cellSize = difficulty.getCellSize();
        int boardWidth = width * cellSize, boardHeight = height * cellSize;
        int windowWidth = this.getWidth(), windowHeight = this.getHeight();

        Color darkGrey = new Color(83, 83, 83);
        g.setColor(darkGrey);
        g.fillRect((windowWidth - boardWidth) / 2 - 2, (windowHeight - boardHeight) / 2 - 2, boardWidth + 4,
                boardHeight + 4);

        // Draw the flag count and timer
        g.setColor(Color.BLACK);
        BufferedImage sprite = minesweeps.getSprite(0, 2);
        g.drawImage(sprite, windowWidth - (windowWidth - boardWidth) / 2 - (windowWidth / 5) + 7,
                windowHeight - (windowHeight - boardHeight) / 2 + 13, 28, 28, null);
        sprite = minesweeps.getSprite(2, 2);
        g.drawImage(sprite, boardWidth / 2 - (this.getWidth() / 5) - 28,
                windowHeight - (windowHeight - boardHeight) / 2 + 13, 28, 28, null);

        g.setFont(new Font("Courier", Font.BOLD, 22));
        g.drawString(Integer.toString(flagCount),
                windowWidth - (windowWidth - boardWidth) / 2 - (windowWidth / 5) + 39,
                windowHeight - (windowHeight - boardHeight) / 2 + 35);
        g.drawString(Integer.toString(this.ticker.time), boardWidth / 2 - (windowWidth / 5) + 4,
                windowHeight - (windowHeight - boardHeight) / 2 + 35);
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        this.ticker.cancel();
        if (this.animator != null) {
            this.animator.stop();
        }
    }

    /**
     * Get a random number between 0 and max
     *
     * @param max The maximum number
     * @return The random number
     */
    public int getRandom(int max) {
        return getRandom(0, max);
    }

    /**
     * Get a random number between min and max
     *
     * @param min The minimum number
     * @param max The maximum number
     * @return The random number
     */
    public int getRandom(int min, int max) {
        return random.nextInt(max - min) + min;
    }

    /**
     * Sleep for a specified number of milliseconds
     *
     * @param millis The number of milliseconds to sleep for
     */
    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fill the board with cells based on the difficulty
     *
     * @param difficulty The difficulty
     */
    public void generateCells(Difficulty difficulty) {
        // Fill the 2D array with Cell objects
        for (int i = 0; i < difficulty.getWidth(); i++) {
            for (int j = 0; j < difficulty.getHeight(); j++) {
                this.board[i][j] = new Cell(this, difficulty, i, j, false);
            }
        }

        System.out.printf("[BOARD] Board filled, %s mode\n", difficulty.toString().toLowerCase());
        this.repaint();
    }

    /**
     * Get the cell at the specified coordinates
     * Returns an Optional<Cell> because a cell may not exist where clicked
     *
     * @param x The x coordinate
     * @param y The y coordinate
     * @return The cell at the specified coordinates, if it exists
     */
    public Optional<Cell> getCell(int x, int y) {
        // This is Optional<T> because a cell may not exist at the specified coordinates
        try {
            return Optional.of(board[x][y]);
        } catch (ArrayIndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }

    /**
     * Generate mines on the board, avoiding the clicked cell
     *
     * @param clicked The cell that was clicked
     */
    public void generateMines(Cell clicked) {
        Difficulty difficulty = this.getGame().getDifficulty();
        int mines = difficulty.getMines();

        // Randomly place mines # of mines on the board
        do {
            int x = this.getRandom(difficulty.getWidth());
            int y = this.getRandom(difficulty.getHeight());
            Cell cell = this.getCell(x, y).get();
            if (!cell.isMine() && (clicked == null || (cell != clicked && !clicked.isNeighbor(cell)))) {
                cell.setMine(true);
                mines--;
            }
        } while (mines > 0);

        System.out.printf("[BOARD] Backfilled mines, %s mode (%d)\n", difficulty.toString().toLowerCase(),
                difficulty.getMines());
    }

    /**
     * Get all the mines on the board
     *
     * @return All the mines
     */
    public ArrayList<Cell> getMines() {
        // Get all the mines on the board
        ArrayList<Cell> mines = new ArrayList<>();
        for (Cell[] row : this.board) {
            for (Cell cell : row) {
                if (cell.isMine()) {
                    mines.add(cell);
                }
            }
        }
        // Randomly shuffle the mines
        Collections.shuffle(mines);
        return mines;
    }

    /**
     * Win the game, play the win animation
     */
    public void win() {
        System.out.println("[BOARD] You win!");
        this.ticker.cancel();

        // As a winning animation, we reveal all the mines, getting faster as we go
        // We must use a timer to do this, otherwise the UI will not update
        // The mines revealed should be random
        Game game = this.getGame();
        SoundManager soundManager = game.getSoundManager();
        this.animator = new Timer(200, new ActionListener() {
            private ArrayList<Cell> mines = getMines();
            private SoundManager.Sound sound = soundManager.get("revealMine");
            private boolean lastCycle = false;

            @Override
            public void actionPerformed(ActionEvent e) {
                // Reveal a random mine
                if (mines.size() > 0) {
                    Cell mine = mines.remove(0);
                    mine.setFlagged(true);
                    mine.setRevealed(true, false);
                    sound.play();
                    animator.setDelay(Math.max(1, animator.getDelay() - 1));
                }

                // If we've reached the end, stop the timer after 50ms
                if (lastCycle) {
                    animator.stop();
                    sleep(400);
                    game.switchState(GameState.WON);
                    sound = soundManager.get("win");
                    sound.play();
                }
                if (mines.size() == 0) {
                    lastCycle = true;
                }
            }
        });
        this.animator.start();
    }

    /**
     * Lose the game, play the lose animation
     */
    public void lose() {
        System.out.println("[BOARD] You lose!");
        this.ticker.cancel();

        // As a losing animation, we reveal all the mines, getting faster as we go
        // We must use a timer to do this, otherwise the UI will not update
        // The mines revealed should be random
        Game game = this.getGame();
        SoundManager soundManager = game.getSoundManager();
        this.animator = new Timer(200, new ActionListener() {
            private ArrayList<Cell> mines = getMines();
            private SoundManager.Sound sound = soundManager.get("revealMine");
            private boolean lastCycle = false;

            @Override
            public void actionPerformed(ActionEvent e) {
                // Reveal a random mine
                if (mines.size() > 0) {
                    Cell mine = mines.remove(0);
                    mine.setRevealed(true, false);
                    sound.play();
                    animator.setDelay(Math.max(1, animator.getDelay() - 1));
                }

                // If we've reached the end, stop the timer after 50ms
                if (lastCycle) {
                    animator.stop();
                    sleep(400);
                    game.switchState(GameState.LOST);
                    sound = soundManager.get("death");
                    sound.play();
                }
                if (mines.size() == 0) {
                    lastCycle = true;
                }
            }
        });
        this.animator.start();
    }

    /**
     * Get the number of flags placed
     *
     * @return The number of flags placed
     */
    public int getFlagCount() {
        return this.flagCount;
    }

    /**
     * Update the number of flags placed
     *
     * @param change The amount to change the flag count by
     */
    public void updateFlagCount(int change) {
        this.flagCount += change;
    }

    /**
     * Get the number of cells revealed
     *
     * @return The number of cells revealed
     */
    public int getRevealedCount() {
        return this.revealedCount;
    }

    /**
     * Increase the number of cells revealed by 1
     */
    public void updateRevealedCount() {
        this.updateRevealedCount(1);
    }

    /**
     * Update the number of cells revealed
     *
     * @param change The amount to change the revealed count by
     */
    public void updateRevealedCount(int change) {
        this.revealedCount += change;
        Difficulty difficulty = this.getGame().getDifficulty();
        int cells = difficulty.getCells() - difficulty.getMines();
        if (this.revealedCount >= cells) {
            this.win();
        }
    }

    /**
     * Handles mouse input
     */
    private class MouseInput implements MouseListener {
        private final GameBoard board;
        private boolean firstClick = false;

        /**
         * Create a new mouse input handler
         *
         * @param board The game board
         */
        public MouseInput(GameBoard board) {
            super();
            this.board = board;
        }

        /**
         * Get the row of the cell at the specified x coordinate
         *
         * @param cord The x coordinate
         * @return The row of the cell
         */
        private int rowOf(int cord) {
            Difficulty difficulty = this.board.getGame().getDifficulty();
            int cellSize = difficulty.getCellSize();
            int gridWidth = difficulty.getWidth() * cellSize;
            int bufferX = (board.getWidth() - gridWidth) / 2;
            return (cord - bufferX) / cellSize;
        }

        /**
         * Get the column of the cell at the specified y coordinate
         *
         * @param cord The y coordinate
         * @return The column of the cell
         */
        private int colOf(int cord) {
            Difficulty difficulty = this.board.getGame().getDifficulty();
            int cellSize = difficulty.getCellSize();
            int gridHeight = difficulty.getHeight() * cellSize;
            int bufferY = (board.getHeight() - gridHeight) / 2;
            return (cord - bufferY) / cellSize;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            SoundManager soundManager = this.board.getGame().getSoundManager();
            int mouseX = this.rowOf(e.getX());
            int mouseY = this.colOf(e.getY());

            if (animator != null) {
                return;
            }

            Optional<Cell> maybeCell = this.board.getCell(mouseX, mouseY);
            if (!maybeCell.isPresent()) {
                return;
            }
            Cell cell = maybeCell.get();

            if (SwingUtilities.isLeftMouseButton(e) && !this.firstClick) {
                this.board.generateMines(cell);
                this.firstClick = true;
            }

            if (SwingUtilities.isRightMouseButton(e) && !cell.isRevealed() && this.firstClick) {
                cell.setFlagged(!cell.isFlagged());
            } else if (SwingUtilities.isLeftMouseButton(e) && !cell.isFlagged() && !cell.isRevealed()) {
                cell.setRevealed(true);
                SoundManager.Sound sound = soundManager.get("click");
                sound.play();
            } else if (SwingUtilities.isMiddleMouseButton(e) && cell.isRevealed()) {
                cell.revealNeighbors();
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }
}
