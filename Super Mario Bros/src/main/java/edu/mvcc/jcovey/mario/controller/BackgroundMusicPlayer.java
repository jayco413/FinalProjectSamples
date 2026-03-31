package edu.mvcc.jcovey.mario.controller;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class BackgroundMusicPlayer {
    private final Map<String, Media> tracks = new HashMap<>();
    private MediaPlayer player;
    private String currentTrackKey = "";
    private boolean enabled = true;
    private boolean levelClearTrackFinished = true;

    public void initialize() {
        Path root = Path.of("assets", "music");
        load(root, "overworld", "01-main-theme-overworld.mp3");
        load(root, "underground", "02-underworld.mp3");
        load(root, "starman", "05-starman.mp3");
        load(root, "levelclear", "06-level-complete.mp3");
        load(root, "gameover", "09-game-over.mp3");
    }

    public void sync(String areaId, boolean starActive, boolean levelComplete, boolean gameOver) {
        if (!enabled) {
            stop();
            return;
        }
        if (gameOver) {
            play("gameover", false);
            return;
        }
        if (levelComplete) {
            play("levelclear", false);
            return;
        }
        if (starActive) {
            play("starman", true);
            return;
        }
        if ("bonus".equals(areaId) || "underground".equals(areaId)) {
            play("underground", true);
            return;
        }
        play("overworld", true);
    }

    public void stop() {
        if (player != null) {
            player.stop();
        }
        currentTrackKey = "";
        levelClearTrackFinished = true;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            stop();
        }
    }

    public boolean isLevelClearTrackFinished() {
        return levelClearTrackFinished;
    }

    private void play(String key, boolean loop) {
        if (key.equals(currentTrackKey) && player != null) {
            return;
        }

        Media media = tracks.get(key);
        if (media == null) {
            return;
        }

        if (player != null) {
            player.stop();
            player.dispose();
        }

        player = new MediaPlayer(media);
        levelClearTrackFinished = !"levelclear".equals(key);
        if (loop) {
            player.setOnEndOfMedia(() -> player.seek(Duration.ZERO));
        } else if ("levelclear".equals(key)) {
            player.setOnEndOfMedia(() -> levelClearTrackFinished = true);
        }
        player.play();
        currentTrackKey = key;
    }

    private void load(Path root, String key, String filename) {
        Path path = root.resolve(filename);
        if (!Files.exists(path)) {
            return;
        }
        tracks.put(key, new Media(path.toUri().toString()));
    }
}
