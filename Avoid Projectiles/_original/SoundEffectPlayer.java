package edu.mvcc.jcovey.AvoidProjectiles;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.HashMap;
import java.util.Map;

/**
 * The SoundEffectPlayer class provides functionality to play and stop sound effects.
 * It ensures unique MediaPlayer instances per sound path to efficiently manage 
 * media resources.
 * 
 * <p>
 * Requires a new dependency added to pom.xml:
 * <pre>
 * {@code
 * <dependency>
 *     <groupId>org.openjfx</groupId>
 *     <artifactId>javafx-media</artifactId>
 *     <version>13</version>
 * </dependency>
 * }
 * </pre>
 * </p>
 * 
 * @author jay
 */
public class SoundEffectPlayer {

    /** Represents the media object for sound. */
    private Media soundMedia;
    
    /** Represents the media player to play the sound. */
    private MediaPlayer soundPlayer;

    /**
     * A static map that maintains a unique MediaPlayer instance for each 
     * sound path. This optimizes resource usage.
     */
    private static final Map<String, MediaPlayer> mediaPlayerMap = 
        new HashMap<>();

    /**
     * Constructor initializes a SoundEffectPlayer with a given path.
     * 
     * @param path Path to the sound file.
     */
    public SoundEffectPlayer(String path) {
        setSoundPath(path);
    }

    /**
     * Plays the sound effect. It checks if a sound is already playing 
     * before playing to prevent overlaps.
     */
    public void play() {
        if (soundPlayer != null && 
            soundPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
            soundPlayer.play();
        }
    }

    /**
     * Stops the sound effect if it's playing.
     */
    public void stop() {
        if (soundPlayer != null && 
            soundPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            soundPlayer.stop();
        }
    }

    /**
     * Private method to set the sound path. It checks if the sound path
     * is already associated with an existing MediaPlayer. If not, it
     * creates a new MediaPlayer for that path.
     * 
     * @param path Path to the sound file.
     */
    private void setSoundPath(String path) {
        if (!mediaPlayerMap.containsKey(path)) {
            soundMedia = new Media(path);
            soundPlayer = new MediaPlayer(soundMedia);

            // Stop the sound once it's done playing
            soundPlayer.setOnEndOfMedia(new Runnable() {
                @Override
                public void run() {
                    soundPlayer.stop();
                }
            });

            // Store the MediaPlayer in the map
            mediaPlayerMap.put(path, soundPlayer);
        } else {
            soundPlayer = mediaPlayerMap.get(path);
        }
    }

    /**
     * Static method to stop all active sounds from all instances 
     * of SoundEffectPlayer.
     */
    public static void stopAllSounds() {
        for (MediaPlayer mediaPlayer : mediaPlayerMap.values()) {
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                mediaPlayer.stop();
            }
        }
    }
}
