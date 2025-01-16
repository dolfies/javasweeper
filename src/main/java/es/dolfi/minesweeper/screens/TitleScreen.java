package es.dolfi.minesweeper.screens;

import java.awt.*;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;

import es.dolfi.minesweeper.Game;
import es.dolfi.minesweeper.components.MuteToggle;
import es.dolfi.minesweeper.enums.*;
import es.dolfi.minesweeper.util.SoundManager;

/**
 * Initial title screen
 */
public class TitleScreen extends Screen {
    private final JComboBox<String> difficultyBox;
    private final JButton playButton;
    private final MuteToggle muteButton;
    private final SoundManager.Sound sound;

    /**
     * Create a new title screen
     *
     * @param game The game
     */
    public TitleScreen(Game game) {
        super(game);
        game.setSize(600, 400);

        this.difficultyBox = new JComboBox<String>(Difficulty.getNames());
        this.difficultyBox.setSelectedItem(game.getDifficulty().toString());
        this.difficultyBox.addActionListener(e -> {
            String difficulty = (String) this.difficultyBox.getSelectedItem();
            game.setDifficulty(Difficulty.valueOf(difficulty.toUpperCase()));
        });
        this.difficultyBox.setPreferredSize(new Dimension(200, 25));
        this.difficultyBox.setBounds(195, 150, 200, 25);

        this.playButton = new JButton("Play");
        this.playButton.addActionListener(e -> game.switchState(GameState.PLAYING));
        this.playButton.setPreferredSize(new Dimension(200, 50));
        this.playButton.setBounds(195, 200, 200, 50);

        this.muteButton = new MuteToggle(game);
        this.muteButton.setBounds(550, 10, 30, 30);

        this.components = new Component[] { this.difficultyBox, this.playButton, this.muteButton };
        this.addComponents();

        SoundManager soundManager = game.getSoundManager();
        this.sound = soundManager.get("shipbg");
        this.sound.play(-1);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        g.drawString("MineSweeper", 140, 100);

        Image funnyGIF = new ImageIcon("res/rotat.gif").getImage();
        g.drawImage(funnyGIF, 260, 275, this);
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        this.sound.stop();
    }
}
