package es.dolfi.minesweeper.screens;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.JButton;
import javax.swing.Timer;

import es.dolfi.minesweeper.Game;
import es.dolfi.minesweeper.enums.*;
import es.dolfi.minesweeper.util.SpriteSheet;

/**
 * Lose screen
 */
public class LoseScreen extends Screen {
    private final JButton playButton;
    public static final SpriteSheet minesweeps = SpriteSheet.load("spritresheet.png", 32);

    private Timer animator;
    private Timer boatShaker;
    private int spriteRow = 2;
    private int spriteCol = 7;
    private int boatY = 125;
    private int boatX = 230;

    /**
     * Create a new lose screen
     *
     * @param game The game
     */
    public LoseScreen(Game game) {
        super(game);
        game.setSize(600, 400);

        this.playButton = new JButton("New Game");
        this.playButton.addActionListener(e -> game.switchState(GameState.MENU));
        this.playButton.setPreferredSize(new Dimension(300, 100));
        this.playButton.setBounds(200, 175, 200, 100);
        this.playButton.setBackground(Color.BLACK);
        this.playButton.setForeground(Color.WHITE);
        this.playButton.setBorderPainted(false);

        Font font = this.playButton.getFont();
        this.playButton.setFont(new Font(font.getName(), Font.BOLD, 24));

        this.components = new Component[] { this.playButton };
        this.animate();
        this.boatShaker();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.RED);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        g.setColor(Color.WHITE);

        // Make a massive sad face
        g.setColor(Color.BLACK);
        g.fillOval(70, 155, 450, 150);

        final Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        if ((this.spriteRow == 3 && this.spriteCol <= 5) || this.spriteRow == 2) {
            BufferedImage sprite = minesweeps.getSprite(1, 2);
            g2d.drawImage(sprite, this.boatX, this.boatY, 128, 128, null);
        }

        if ((this.spriteRow >= 3 && this.spriteRow <= 4) && !(this.spriteRow == 4 && this.spriteCol == 4)) {
            BufferedImage sprite = minesweeps.getSprite(this.spriteCol, this.spriteRow);
            g2d.drawImage(sprite, 228, 130, 132, 132, null);
        }
        if (this.spriteRow >= 4 && this.spriteCol >= 4) {
            this.addComponents();
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            g.drawString("You Lose!", 175, 100);
        }
    }

    /**
     * Display the losing animation
     */
    private void animate() {
        this.animator = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (spriteCol < 7) {
                    spriteCol++;
                } else if (spriteCol == 7) {
                    spriteRow++;
                    spriteCol = 0;
                }
                if (spriteRow >= 4 && spriteCol >= 5) {
                    spriteCol = 4;
                    animator.stop();
                } else {
                    repaint();
                }
            }
        });
        this.animator.setInitialDelay(400);
        this.animator.start();
    }

    private void boatShaker() {
        this.boatShaker = new Timer(200, new ActionListener() {
            double boatShake = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                boatY += (int) (3 * Math.sin(boatShake));
                boatShake++;
                if (spriteRow >= 4 && spriteCol >= 4) {
                    boatShaker.stop();
                } else {
                    repaint();
                }
            }
        });
        this.boatShaker.setInitialDelay(0);
        this.boatShaker.start();
    }
}
