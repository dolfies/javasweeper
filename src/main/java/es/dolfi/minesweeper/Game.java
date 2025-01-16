package es.dolfi.minesweeper;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;

import com.formdev.flatlaf.FlatLightLaf;

import es.dolfi.minesweeper.enums.*;
import es.dolfi.minesweeper.screens.*;
import es.dolfi.minesweeper.util.*;

/**
 * Main application window, responsible for displaying the game
 *
 * @version 0.1
 */
public class Game extends JFrame {
    public static final ImageIcon icon = new ImageIcon("res/bomb.png");
    private GameState state;
    private Screen screen;
    private Difficulty difficulty = Difficulty.EASY;
    private final SoundManager soundManager = new SoundManager();

    private JMenuBar menuBar = new JMenuBar();
    private JMenu gameMenu = new JMenu("Game");
    private JMenu helpMenu = new JMenu("Help");
    private JMenuItem newGame, exit, help, debug;

    public static void main(String[] args) {
        // Don't block the main thread
        SwingUtilities.invokeLater(() -> {
            setLookAndFeel();
            Game game = new Game();
            game.switchState(GameState.MENU);
            game.setVisible(true);
        });
    }

    /**
     * Set the UI theme of the game
     */
    private static void setLookAndFeel() {
        try {
            FlatLightLaf.setup();
            UIManager.put("TextComponent.arc", 5);
        } catch (Exception e) {
            // Look and feel isn't vital
            System.out.println("[GAME] Failed to initialize look and feel");
        }
    }

    /**
     * Create a new game instance
     */
    public Game() {
        super("JavaSweeper");
        this.setSize(600, 400);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setIconImage(icon.getImage());

        // Cleanup resources on close
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                close();
            }
        });

        // Simple menu bar
        this.newGame = new JMenuItem("New Game");
        this.newGame.addActionListener(e -> this.switchState(GameState.MENU));
        this.exit = new JMenuItem("Exit");
        this.exit.addActionListener(e -> this.close());
        this.gameMenu.add(this.newGame);
        this.gameMenu.add(this.exit);

        this.help = new JMenuItem("How to Play");
        this.help.addActionListener(e -> {
            Help help = new Help();
            help.setVisible(true);
        });
        this.debug = new JMenuItem("Debug Console");
        this.debug.addActionListener(e -> {
            DebugConsole debugConsole = new DebugConsole(this);
            debugConsole.setVisible(true);
        });
        this.helpMenu.add(this.help);
        this.helpMenu.add(this.debug);

        this.menuBar.add(this.gameMenu);
        this.menuBar.add(this.helpMenu);
        this.setJMenuBar(this.menuBar);
    }

    /**
     * Close the game and cleanup resources
     */
    public void close() {
        this.soundManager.close();
        System.exit(0);
    }

    /**
     * Get the current game state
     *
     * @return The current game state
     */
    public GameState getGameState() {
        return this.state;
    }

    /**
     * Switch the current game state
     *
     * @param state The new game state
     */
    public void switchState(GameState state) {
        if (this.state == state) {
            return;
        }
        if (this.screen != null) {
            this.remove(this.screen);
        }

        switch (state) {
            case MENU:
                this.screen = new TitleScreen(this);
                break;
            case PLAYING:
                this.screen = new GameBoard(this);
                break;
            case WON:
                this.screen = new WinScreen(this);
                break;
            case LOST:
                this.screen = new LoseScreen(this);
                break;
        }

        this.add(this.screen);
        this.state = state;
        this.pack();
        this.repaint();
    }

    /**
     * Get the current game screen
     *
     * @return The current game screen
     */
    public Screen getScreen() {
        return this.screen;
    }

    /**
     * Get the current game difficulty
     *
     * @return The current game difficulty
     */
    public Difficulty getDifficulty() {
        return this.difficulty;
    }

    /**
     * Set the current game difficulty
     * This should not be changed during runtime
     *
     * @param difficulty The new game difficulty
     */
    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    /**
     * Get the game sound manager
     *
     * @return The game sound manager
     */
    public SoundManager getSoundManager() {
        return this.soundManager;
    }
}
