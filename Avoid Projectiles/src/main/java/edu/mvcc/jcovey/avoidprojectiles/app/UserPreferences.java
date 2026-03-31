package edu.mvcc.jcovey.avoidprojectiles.app;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Persistent user-configurable settings for the game.
 *
 * @author Jason A. Covey
 */
public class UserPreferences {
    private static final Path STORE_PATH = Paths.get(System.getProperty("user.dir"), "avoid-projectiles-preferences.json");
    private static final String SOUND_ENABLED_KEY = "soundEnabled";
    private static final String WINDOW_SCALE_KEY = "windowScale";
    private static final String DIFFICULTY_KEY = "difficulty";
    private static final String PLAYER_SPEED_KEY = "playerSpeed";

    private boolean soundEnabled;
    private double windowScale;
    private String difficulty;
    private String playerSpeed;

    /**
     * Creates default preferences.
     */
    public UserPreferences() {
        soundEnabled = true;
        windowScale = 1.0;
        difficulty = "Standard";
        playerSpeed = "Normal";
    }

    /**
     * Loads preferences from the system store.
     *
     * @return the loaded preferences
     */
    public static UserPreferences load() {
        UserPreferences preferences = new UserPreferences();
        if (!Files.exists(STORE_PATH)) {
            return preferences;
        }

        try {
            String json = Files.readString(STORE_PATH, StandardCharsets.UTF_8);
            preferences.soundEnabled = parseBoolean(json, SOUND_ENABLED_KEY, true);
            preferences.windowScale = clampScale(parseDouble(json, WINDOW_SCALE_KEY, 1.0));
            preferences.difficulty = normalizeDifficulty(parseString(json, DIFFICULTY_KEY, "Standard"));
            preferences.playerSpeed = normalizePlayerSpeed(parseString(json, PLAYER_SPEED_KEY, "Normal"));
        } catch (IOException exception) {
            return preferences;
        }
        return preferences;
    }

    /**
     * Saves the current preferences.
     */
    public void save() {
        String json = """
            {
              "soundEnabled": %s,
              "windowScale": %s,
              "difficulty": "%s",
              "playerSpeed": "%s"
            }
            """.formatted(
            soundEnabled,
            clampScale(windowScale),
            escapeJson(normalizeDifficulty(difficulty)),
            escapeJson(normalizePlayerSpeed(playerSpeed))
        );

        try {
            Files.writeString(STORE_PATH, json, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            // Ignore save failures and keep the in-memory settings.
        }
    }

    /**
     * Creates a copy of this preferences object.
     *
     * @return a copy
     */
    public UserPreferences copy() {
        UserPreferences copy = new UserPreferences();
        copy.soundEnabled = soundEnabled;
        copy.windowScale = windowScale;
        copy.difficulty = difficulty;
        copy.playerSpeed = playerSpeed;
        return copy;
    }

    /**
     * Checks whether sound effects are enabled.
     *
     * @return true when sound is enabled
     */
    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    /**
     * Enables or disables sound effects.
     *
     * @param soundEnabled true to enable sound effects
     */
    public void setSoundEnabled(boolean soundEnabled) {
        this.soundEnabled = soundEnabled;
    }

    /**
     * Gets the current window scale.
     *
     * @return the scale factor
     */
    public double getWindowScale() {
        return windowScale;
    }

    /**
     * Sets the preferred window scale.
     *
     * @param windowScale the desired scale factor
     */
    public void setWindowScale(double windowScale) {
        this.windowScale = clampScale(windowScale);
    }

    /**
     * Gets the selected difficulty.
     *
     * @return the difficulty label
     */
    public String getDifficulty() {
        return difficulty;
    }

    /**
     * Sets the selected difficulty.
     *
     * @param difficulty the difficulty label
     */
    public void setDifficulty(String difficulty) {
        this.difficulty = normalizeDifficulty(difficulty);
    }

    /**
     * Gets the selected player speed.
     *
     * @return the player speed label
     */
    public String getPlayerSpeed() {
        return playerSpeed;
    }

    /**
     * Sets the selected player speed.
     *
     * @param playerSpeed the player speed label
     */
    public void setPlayerSpeed(String playerSpeed) {
        this.playerSpeed = normalizePlayerSpeed(playerSpeed);
    }

    private static boolean parseBoolean(String json, String key, boolean defaultValue) {
        Matcher matcher = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*(true|false)").matcher(json);
        if (matcher.find()) {
            return Boolean.parseBoolean(matcher.group(1));
        }
        return defaultValue;
    }

    private static double parseDouble(String json, String key, double defaultValue) {
        Matcher matcher = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*([0-9]+(?:\\.[0-9]+)?)").matcher(json);
        if (matcher.find()) {
            return Double.parseDouble(matcher.group(1));
        }
        return defaultValue;
    }

    private static String parseString(String json, String key, String defaultValue) {
        Matcher matcher = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*\"([^\"]*)\"").matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return defaultValue;
    }

    private static String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static double clampScale(double scale) {
        if (scale <= 1.0) {
            return 1.0;
        }
        if (scale >= 1.5) {
            return 1.5;
        }
        return 1.25;
    }

    private static String normalizeDifficulty(String difficulty) {
        if ("Relaxed".equals(difficulty) || "Chaotic".equals(difficulty)) {
            return difficulty;
        }
        return "Standard";
    }

    private static String normalizePlayerSpeed(String playerSpeed) {
        if ("Slow".equals(playerSpeed) || "Fast".equals(playerSpeed)) {
            return playerSpeed;
        }
        return "Normal";
    }
}
