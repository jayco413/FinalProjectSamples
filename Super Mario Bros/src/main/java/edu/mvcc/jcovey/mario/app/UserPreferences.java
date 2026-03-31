package edu.mvcc.jcovey.mario.app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Persistent user-configurable settings for the game.
 *
 * @author Jason A. Covey
 */
public class UserPreferences {
    private static final Path PREFERENCES_PATH = resolvePreferencesPath();
    private boolean musicEnabled;
    private boolean soundEnabled;
    private double windowScale;
    private String startWorld;

    /**
     * Creates the default preferences.
     */
    public UserPreferences() {
        musicEnabled = true;
        soundEnabled = true;
        windowScale = 2.0;
        startWorld = "1-1";
    }

    /**
     * Loads preferences from a JSON file in the project directory.
     *
     * @return the loaded preferences
     */
    public static UserPreferences load() {
        UserPreferences preferences = new UserPreferences();
        if (!Files.exists(PREFERENCES_PATH)) {
            return preferences;
        }
        try {
            String json = Files.readString(PREFERENCES_PATH);
            preferences.musicEnabled = parseBoolean(json, "musicEnabled", true);
            preferences.soundEnabled = parseBoolean(json, "soundEnabled", true);
            preferences.windowScale = clampScale(parseDouble(json, "windowScale", 2.0));
            preferences.startWorld = normalizeWorld(parseString(json, "startWorld", "1-1"));
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
              "musicEnabled": %s,
              "soundEnabled": %s,
              "windowScale": %s,
              "startWorld": "%s"
            }
            """.formatted(
            musicEnabled,
            soundEnabled,
            formatScale(clampScale(windowScale)),
            escapeJson(normalizeWorld(startWorld))
        );
        try {
            Files.writeString(PREFERENCES_PATH, json);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to save preferences to " + PREFERENCES_PATH, exception);
        }
    }

    /**
     * Creates a copy of this preferences object.
     *
     * @return a copy
     */
    public UserPreferences copy() {
        UserPreferences copy = new UserPreferences();
        copy.musicEnabled = musicEnabled;
        copy.soundEnabled = soundEnabled;
        copy.windowScale = windowScale;
        copy.startWorld = startWorld;
        return copy;
    }

    /**
     * Gets the window scale factor.
     *
     * @return the scale factor
     */
    public double getWindowScale() {
        return windowScale;
    }

    /**
     * Sets the window scale factor.
     *
     * @param windowScale the scale factor
     */
    public void setWindowScale(double windowScale) {
        this.windowScale = clampScale(windowScale);
    }

    /**
     * Checks whether music playback is enabled.
     *
     * @return true when music is enabled
     */
    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    /**
     * Enables or disables music playback.
     *
     * @param musicEnabled true to enable music
     */
    public void setMusicEnabled(boolean musicEnabled) {
        this.musicEnabled = musicEnabled;
    }

    /**
     * Checks whether sound effects are enabled.
     *
     * @return true when sound effects are enabled
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
     * Gets the configured startup world.
     *
     * @return the startup world id
     */
    public String getStartWorld() {
        return startWorld;
    }

    /**
     * Sets the configured startup world.
     *
     * @param startWorld the startup world id
     */
    public void setStartWorld(String startWorld) {
        this.startWorld = normalizeWorld(startWorld);
    }

    private static double clampScale(double scale) {
        if (scale <= 1.0) {
            return 1.0;
        }
        if (scale >= 3.0) {
            return 3.0;
        }
        return scale;
    }

    private static String normalizeWorld(String world) {
        if ("1-2".equals(world)) {
            return "1-2";
        }
        return "1-1";
    }

    private static Path resolvePreferencesPath() {
        Path directory = Path.of("").toAbsolutePath();
        while (directory != null) {
            if (Files.exists(directory.resolve("Start-SuperMario.ps1"))
                || Files.exists(directory.resolve("Start-SuperMario.sh"))) {
                return directory.resolve("user-preferences.json");
            }
            directory = directory.getParent();
        }
        return Path.of("user-preferences.json").toAbsolutePath();
    }

    private static boolean parseBoolean(String json, String key, boolean defaultValue) {
        Matcher matcher = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*(true|false)").matcher(json);
        if (!matcher.find()) {
            return defaultValue;
        }
        return Boolean.parseBoolean(matcher.group(1));
    }

    private static double parseDouble(String json, String key, double defaultValue) {
        Matcher matcher = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*(-?\\d+(?:\\.\\d+)?)").matcher(json);
        if (!matcher.find()) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(matcher.group(1));
        } catch (NumberFormatException exception) {
            return defaultValue;
        }
    }

    private static String parseString(String json, String key, String defaultValue) {
        Matcher matcher = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*\"((?:\\\\.|[^\\\\\"])*)\"").matcher(json);
        if (!matcher.find()) {
            return defaultValue;
        }
        return unescapeJson(matcher.group(1));
    }

    private static String formatScale(double scale) {
        if (Math.rint(scale) == scale) {
            return Integer.toString((int) scale);
        }
        return Double.toString(scale);
    }

    private static String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static String unescapeJson(String value) {
        return value.replace("\\\"", "\"").replace("\\\\", "\\");
    }
}
