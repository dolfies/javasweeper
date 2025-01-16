package es.dolfi.minesweeper.screens;

import java.awt.*;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import es.dolfi.minesweeper.Game;

/**
 * Game screen interface
 */
public class Screen extends JPanel {
    protected Component[] components = new Component[] {};

    /**
     * Create a new screen
     *
     * @param game The game
     */
    public Screen(Game game) {
        super();
        this.setLayout(null);
        this.setSize(game.getSize());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        final Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int width = this.getWidth();
        int height = this.getHeight();
        Color color1 = new Color(0xCCCCCC);
        Color color2 = new Color(0x6666FF);
        GradientPaint gp = new GradientPaint(0, 0, color1, width, height, color2);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, width, height);
    }

    @Override
    public Dimension getPreferredSize() {
        return this.getGame().getSize();
    }

    /**
     * Add all defined components to the screen
     */
    protected void addComponents() {
        for (Component component : this.components) {
            this.add(component);
        }
    }

    /**
     * Get the current game instance
     *
     * @return The game
     */
    public Game getGame() {
        return (Game) SwingUtilities.getWindowAncestor(this);
    }
}
