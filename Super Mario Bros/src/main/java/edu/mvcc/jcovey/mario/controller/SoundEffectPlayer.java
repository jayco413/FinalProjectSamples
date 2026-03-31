package edu.mvcc.jcovey.mario.controller;

import javafx.scene.media.AudioClip;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SoundEffectPlayer {
    private final Map<String, AudioClip> clips = new HashMap<>();
    private final Map<String, Double> durations = new HashMap<>();
    private boolean enabled = true;

    public void initialize() {
        Path root = Path.of("assets", "sound_effects");
        load(root, "1up", "1up.wav");
        load(root, "bump", "bump.wav");
        load(root, "brick", "brick.wav");
        load(root, "coin", "coin.wav");
        load(root, "death", "death.wav");
        load(root, "fireball", "fireball.wav");
        load(root, "firework", "billfirework.wav");
        load(root, "flagpole", "flagpole.wav");
        load(root, "gameover", "gameover.wav");
        load(root, "item", "item.wav");
        load(root, "jump_big", "jump.wav");
        load(root, "jump_small", "jumpsmall.wav");
        load(root, "kickkill", "kickkill.wav");
        load(root, "pipepowerdown", "pipepowerdown.wav");
        load(root, "powerup", "powerup.wav");
        load(root, "stomp", "stompswim.wav");
    }

    public void playAll(List<String> effectKeys) {
        if (!enabled) {
            return;
        }
        for (String effectKey : effectKeys) {
            AudioClip clip = clips.get(effectKey);
            if (clip != null) {
                clip.play();
            }
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public double getDurationSeconds(String effectKey) {
        return durations.getOrDefault(effectKey, 0.0);
    }

    private void load(Path root, String key, String filename) {
        Path path = root.resolve(filename);
        if (!Files.exists(path)) {
            return;
        }
        clips.put(key, new AudioClip(path.toUri().toString()));
        durations.put(key, readDurationSeconds(path));
    }

    private double readDurationSeconds(Path path) {
        try (AudioInputStream stream = AudioSystem.getAudioInputStream(path.toFile())) {
            AudioFormat format = stream.getFormat();
            if (format.getFrameRate() <= 0.0f) {
                return 0.0;
            }
            return stream.getFrameLength() / format.getFrameRate();
        } catch (Exception exception) {
            return 0.0;
        }
    }
}
