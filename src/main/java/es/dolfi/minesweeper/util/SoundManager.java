package es.dolfi.minesweeper.util;

import java.util.HashMap;
import java.io.File;
import javax.sound.sampled.*;

/**
 * Provides an interface for loading and playing sounds
 */
public class SoundManager {
    private final HashMap<String, Sound> currentSounds = new HashMap<>();
    private boolean muted = false;

    /**
     * Represents a sound
     */
    public class Sound implements LineListener {
        private AudioInputStream stream;
        private Clip clip;
        private boolean playing = false;

        /**
         * Create a new sound
         *
         * @param file The sound file
         */
        public Sound(File file) {
            try {
                this.stream = AudioSystem.getAudioInputStream(file);
                this.clip = AudioSystem.getClip();
                this.clip.open(stream);
                this.clip.addLineListener(this);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to load sound file");
            }
        }

        /**
         * Check if the sound is playing
         *
         * @return Whether the sound is playing
         */
        public boolean isPlaying() {
            return this.playing;
        }

        /**
         * Assert the sound is in a playable state
         */
        private void assertStatus() {
            if (!this.clip.isOpen()) {
                try {
                    this.stream = AudioSystem.getAudioInputStream(this.stream.getFormat(), this.stream);
                    this.clip.open(this.stream);
                    this.clip.addLineListener(this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (this.clip.isRunning()) {
                this.clip.stop();
            }
            this.clip.setFramePosition(0);
        }

        /**
         * Play the sound
         */
        public void play() {
            if (!muted) {
                this.assertStatus();
                this.clip.start();
            }
        }

        /**
         * Play the sound a specified number of times
         *
         * @param loopCount The number of times to play the sound
         */
        public void play(int loopCount) {
            if (!muted) {
                this.assertStatus();
                this.clip.loop(loopCount);
            }
        }

        /**
         * Stop the sound
         */
        public void stop() {
            if (this.clip.isOpen() && this.clip.isActive()) {
                this.clip.stop();
            }
        }

        /**
         * Close the sound file
         */
        public void close() {
            if (this.clip.isOpen()) {
                this.clip.close();
            }
            try {
                this.stream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void update(LineEvent event) {
            if (event.getType() == LineEvent.Type.START) {
                this.playing = true;
            } else if (event.getType() == LineEvent.Type.STOP) {
                this.playing = false;
            }
        }
    }

    /**
     * Get a sound from the sound manager
     *
     * @param filename The sound file name
     * @return The sound
     */
    public Sound get(String filename) {
        Sound sound = this.currentSounds.get(filename);
        if (sound == null) {
            sound = new Sound(new File("res/sound/" + filename + ".wav"));
            this.currentSounds.put(filename, sound);
        }
        return sound;
    }

    /**
     * Close all sound resources
     */
    public void close() {
        for (Sound sound : this.currentSounds.values()) {
            sound.close();
        }
        this.currentSounds.clear();
    }

    /**
     * Get the current mute status
     */
    public boolean isMuted() {
        return this.muted;
    }

    /**
     * Set the mute status
     *
     * @param muted The mute status
     */
    public void setMuted(boolean muted) {
        this.muted = muted;
        if (muted) {
            for (Sound sound : this.currentSounds.values()) {
                sound.stop();
            }
        }
    }
}
