package es.dolfi.minesweeper.util;

import java.util.Timer;
import java.util.TimerTask;

import es.dolfi.minesweeper.enums.Difficulty;
import es.dolfi.minesweeper.screens.GameBoard;

/**
 * A thread for keeping track of the time
 */
public class TimerComponent {
    private Timer timer = new Timer();
    public int time = 0;

    /**
     * Create a new timer
     *
     * @param board      The game board
     * @param difficulty The game difficulty
     */
    public TimerComponent(GameBoard board, Difficulty difficulty) {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                time++;
                board.repaint();
            }
        }, 0, 1000);
    }

    /**
     * Cancel the timer
     */
    public void cancel() {
        timer.cancel();
    }
}
