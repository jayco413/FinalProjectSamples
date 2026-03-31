package edu.mvcc.jcovey.avoidprojectiles.controller;

import java.util.HashMap;
import java.util.Map;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * Shared sound-effect playback service.
 *
 * @author Jason A. Covey
 */
public class SoundEffectPlayer {
    private static final Map<String, MediaPlayer> MEDIA_PLAYERS = new HashMap<>();

    /**
     * Plays a cached sound effect.
     *
     * @param soundResource resource path beginning with {@code /}
     */
    public void play(String soundResource) {
        MediaPlayer player = MEDIA_PLAYERS.computeIfAbsent(soundResource, path -> {
            MediaPlayer mediaPlayer = new MediaPlayer(new Media(getClass().getResource(path).toExternalForm()));
            mediaPlayer.setOnEndOfMedia(mediaPlayer::stop);
            return mediaPlayer;
        });

        player.stop();
        player.play();
    }

    /**
     * Stops a cached sound effect if it is active.
     *
     * @param soundResource resource path beginning with {@code /}
     */
    public void stop(String soundResource) {
        MediaPlayer player = MEDIA_PLAYERS.get(soundResource);
        if (player != null) {
            player.stop();
        }
    }

    /**
     * Stops all active sound effects.
     */
    public static void stopAllSounds() {
        for (MediaPlayer player : MEDIA_PLAYERS.values()) {
            player.stop();
        }
    }
}
