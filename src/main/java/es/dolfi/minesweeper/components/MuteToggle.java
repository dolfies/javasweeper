package es.dolfi.minesweeper.components;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

import es.dolfi.minesweeper.Game;
import es.dolfi.minesweeper.enums.GameState;
import es.dolfi.minesweeper.util.SoundManager;
import es.dolfi.minesweeper.util.SpriteSheet;

/**
 * Represents a mute toggle for the game
 */
public class MuteToggle extends JComponent {
    public static final SpriteSheet minesweeps = SpriteSheet.load("spritresheet.png", 32);
    private final Game game;

    /**
     * Create a mute toggle
     *
     * @param game The game
     */
    public MuteToggle(Game game) {
        this.game = game;

        this.setPreferredSize(new Dimension(32, 32));
        this.setVisible(true);
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                toggle();

                // Hack for menu music
                if (!isMuted() && game.getGameState() == GameState.MENU) {
                    SoundManager soundManager = game.getSoundManager();
                    SoundManager.Sound sound = soundManager.get("shipbg");
                    sound.play(-1);
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        BufferedImage sprite;
        if (this.isMuted()) {
            sprite = minesweeps.getSprite(7, 1);
        } else {
            sprite = minesweeps.getSprite(6, 1);
        }

        g.drawImage(sprite, 0, 0, 32, 32, null);
    }

    /**
     * Check if the game is muted
     *
     * @return True if the game is muted
     */
    public boolean isMuted() {
        SoundManager soundManager = this.game.getSoundManager();
        return soundManager.isMuted();
    }

    /**
     * Toggle the mute status of the game
     */
    public void toggle() {
        SoundManager soundManager = this.game.getSoundManager();
        soundManager.setMuted(!soundManager.isMuted());
        this.repaint();
    }
}
