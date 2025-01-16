package es.dolfi.minesweeper;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Displays gameplay instructions
 */
public class Help extends JFrame {
    private JTextArea text;
    private JScrollPane scroll;

    /**
     * Create a new help window
     */
    public Help() {
        super("Help");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(400, 300);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setIconImage(Game.icon.getImage());

        text = new JTextArea("Minesweeper is a game of logic and luck.\n\n" +
                "The goal of the game is to uncover all the cells on the board that are not mines.\n\n" +
                "When the game starts, you will see a board of covered cells. Left click on a cell to uncover it.\n\n" +
                "If you uncover a mine, the game is over and you lose. Otherwise, the cell will show a number. " +
                "The number represents the number of mines that are directly touching that cell.\n\n" +
                "If you think a cell is a mine, you can right click on it to place a flag. " +
                "This will prevent you from left clicking on that cell.\n\n" +
                "If you think you have flagged all surrounding mines of a cell, " +
                "you can middle click on it to uncover all surrounding cells.\n\n" +
                "When you have uncovered all the cells that are not mines, you win!\n\n" +
                "If you want to start a new game, click on Game -> New Game in the menu bar. " +
                "From there, you can adjust the difficulty to your choosing.\n\n" +
                "Good luck!");
        text.setEditable(false);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        this.add(text);

        scroll = new JScrollPane(text);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        this.add(scroll);
    }
}
