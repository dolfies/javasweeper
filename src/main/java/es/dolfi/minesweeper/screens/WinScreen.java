package es.dolfi.minesweeper.screens;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.Timer;

import es.dolfi.minesweeper.Game;
import es.dolfi.minesweeper.enums.GameState;
import es.dolfi.minesweeper.util.SpriteSheet;

/**
 * Win screen
 */
public class WinScreen extends Screen {
    private final JButton playButton;
    public static final SpriteSheet minesweeps = SpriteSheet.load("spritresheet.png", 32);

    private Timer boatShaker;
    private int boatY = 145;
    private int boatX = 230;

    /**
     * Create a new win screen
     *
     * @param game The game
     */
    public WinScreen(Game game) {
        super(game);
        game.setSize(600, 400);

        this.playButton = new JButton("New Game");
        this.playButton.addActionListener(e -> game.switchState(GameState.MENU));
        this.playButton.setPreferredSize(new Dimension(200, 50));
        this.playButton.setBounds(195, 200, 200, 50);
        this.playButton.setBackground(Color.WHITE);
        this.playButton.setForeground(Color.BLACK);
        this.playButton.setBorderPainted(false);

        Font font = this.playButton.getFont();
        this.playButton.setFont(new Font(font.getName(), Font.BOLD, 24));

        this.components = new Component[] { this.playButton };
        this.boatShaker();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.GREEN);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        // Make a massive happy face
        g.setColor(Color.WHITE);
        g.fillOval(70, 155, 450, 150);

        final Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        BufferedImage sprite = minesweeps.getSprite(1, 2);
        g2d.drawImage(sprite, this.boatX, this.boatY, 128, 128, null);

        if (this.boatX < -128) {
            this.boatShaker.stop();
        }
        if (this.boatX < 195 - 128) {
            this.addComponents();

            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            g.drawString("You Win!", 190, 100);
        }
    }

    private void boatShaker() {
        this.boatShaker = new Timer(100, new ActionListener() {
            double boatShake = 0;
            double boatSpeed = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                boatY = boatY + (int) (3 * Math.sin(boatShake));
                if (boatSpeed < 10) {
                    boatSpeed += 0.3;
                }
                boatX -= boatSpeed;
                boatShake++;
                repaint();
            }
        });
        this.boatShaker.setInitialDelay(0);
        this.boatShaker.start();
    }
}
