package es.dolfi.minesweeper.enums;

/**
 * Represents the current state of the game
 */
public enum GameState {
    MENU,
    PLAYING,
    WON,
    LOST;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
