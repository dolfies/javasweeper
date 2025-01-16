package es.dolfi.minesweeper;

import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

import es.dolfi.minesweeper.components.Cell;
import es.dolfi.minesweeper.enums.*;
import es.dolfi.minesweeper.screens.GameBoard;

/**
 * Debug console for the game
 */
public class DebugConsole extends JFrame {
    private Game game;
    private JTextField console = new JTextField();
    private JTextArea output = new JTextArea();
    private JScrollPane scroll = new JScrollPane(this.output);

    /**
     * Create a new debug console
     *
     * @param game The game instance
     */
    public DebugConsole(Game game) {
        super("Debug Console");
        this.game = game;

        this.setLayout(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(600, 400);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setIconImage(Game.icon.getImage());
        this.getContentPane().setBackground(Color.BLACK);

        // Input console
        this.console.setBounds(50, 110, 490, 50);
        this.console.setBackground(Color.BLACK);
        this.console.setForeground(Color.WHITE);
        this.console.setCaretColor(Color.WHITE);
        this.console.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.WHITE),
                BorderFactory.createEmptyBorder(5, 7, 5, 7)));
        this.console.setFont(new Font("Consolas", Font.BOLD, 18));

        // Execute command when enter is pressed
        this.console.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "execute");
        this.console.getActionMap().put("execute", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String command = console.getText();
                console.setText("");
                execute(command);
            }
        });

        // Output console
        this.output.setBounds(50, 160, 490, 180);
        this.output.setBackground(Color.BLACK);
        this.output.setForeground(Color.WHITE);
        this.output.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.WHITE),
                BorderFactory.createEmptyBorder(5, 7, 5, 7)));
        this.output.setFont(new Font("Consolas", Font.PLAIN, 16));
        this.output.setEditable(false);
        this.output.setLineWrap(true);
        this.output.setWrapStyleWord(true);
        this.output.setFocusable(false);
        this.scroll.setBounds(50, 160, 490, 180);
        this.scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        this.add(this.console);
        this.add(this.scroll);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        g.drawString("Debug Console", 135, 100);
        // Draw a > before the console
        g.drawString(">", 20, 180);
    }

    /**
     * Execute a command
     *
     * @param command The command to execute
     */
    public void execute(String command) {
        command = command.toLowerCase();

        if (command.equals("exit")) {
            System.exit(0);
        } else if (command.equals("close")) {
            this.setVisible(false);
        } else if (command.equals("reveal")) {
            if (this.game.getGameState() != GameState.PLAYING) {
                this.output.setText("You can only reveal mines while playing");
                return;
            }

            GameBoard board = (GameBoard) this.game.getScreen();
            for (Cell cell : board.getMines()) {
                cell.setRevealed(true, false);
            }

            this.output.setText("Revealed all mines");
        } else if (command.equals("win")) {
            this.game.switchState(GameState.PLAYING);
            GameBoard board = (GameBoard) this.game.getScreen();
            board.generateMines(null);
            board.win();

            this.output.setText("Played win animation");
        } else if (command.equals("lose")) {
            this.game.switchState(GameState.PLAYING);
            GameBoard board = (GameBoard) this.game.getScreen();
            board.generateMines(null);
            board.lose();

            this.output.setText("Played lose animation");
        } else if (command.equals("state get")) {
            this.output.setText("Current game state: " + this.game.getGameState());
        } else if (command.startsWith("state set")) {
            try {
                String[] args = command.split(" ");
                GameState state = GameState.valueOf(args[2].toUpperCase());
                this.game.switchState(state);

                this.output.setText("Set game state to: " + state);
            } catch (Exception e) {
                this.output.setText("Invalid state");
            }
        } else if (command.equals("difficulty get")) {
            this.output.setText("Current game difficulty: " + this.game.getDifficulty());
        } else if (command.startsWith("difficulty set")) {
            try {
                String[] args = command.split(" ");
                Difficulty difficulty = Difficulty.valueOf(args[2].toUpperCase());
                this.game.setDifficulty(difficulty);

                this.output.setText("Set game difficulty to: " + difficulty);
            } catch (Exception e) {
                this.output.setText("Invalid difficulty");
            }
        } else if (command.equals("mute")) {
            this.game.getSoundManager().setMuted(true);
            this.output.setText("Muted the game");
        } else if (command.equals("unmute")) {
            this.game.getSoundManager().setMuted(false);
            this.output.setText("Unmuted the game");
        } else if (command.equals("help")) {
            this.output.setText("Available commands:\n" +
                    "reveal - Reveal all mines without losing\n" +
                    "win - Play the win animation\n" +
                    "lose - Play the lose animation\n" +
                    "state get - Get the game state\n" +
                    "state set <state> - Set the game state\n" +
                    "difficulty get - Get the game difficulty\n" +
                    "difficulty set <difficulty> - Set the game difficulty\n" +
                    "mute - Mute the game\n" +
                    "unmute - Unmute the game\n" +
                    "help - Show this help message\n" +
                    "close - Close the debug console\n" +
                    "exit - Exit the game\n");
        } else {
            this.output.setText("Unknown command: " + command);
        }
    }
}
