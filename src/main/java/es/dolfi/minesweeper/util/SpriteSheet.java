package es.dolfi.minesweeper.util;

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/**
 * Provides an interface for selecting sprites from a sprite sheet
 */
public class SpriteSheet {
    private final BufferedImage sheet;
    private final int tileSize;

    /**
     * Create a new sprite sheet
     *
     * @param file The sprite sheet file
     * @param size The size of each sprite
     */
    public SpriteSheet(File file, int size) {
        this.tileSize = size;
        try {
            this.sheet = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load spritesheet");
        }
    }

    /**
     * Load a sprite sheet from a file
     *
     * @param filename The sprite sheet file name
     * @param size     The size of each sprite
     * @return The sprite sheet
     */
    public static SpriteSheet load(String filename, int size) {
        return new SpriteSheet(new File("res/" + filename), size);
    }

    /**
     * Get a sprite from the sprite sheet
     *
     * @param xGrid The x grid position of the sprite
     * @param yGrid The y grid position of the sprite
     * @return The sprite
     */
    public BufferedImage getSprite(int xGrid, int yGrid) {
        return sheet.getSubimage(xGrid * this.tileSize, yGrid * this.tileSize, this.tileSize, this.tileSize);
    }
}
