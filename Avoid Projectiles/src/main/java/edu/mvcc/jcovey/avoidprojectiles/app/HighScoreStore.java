package edu.mvcc.jcovey.avoidprojectiles.app;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Persistent top-10 leaderboard for completed Avoid Projectiles runs.
 *
 * <p>Scores are ranked by fastest completion time to 100 points.</p>
 * <p>AI-assisted implementation reviewed and integrated by Jason A. Covey.</p>
 *
 * @author Jason A. Covey
 */
public class HighScoreStore {
    private static final Path STORE_PATH = Paths.get(System.getProperty("user.dir"), "avoid-projectiles-high-scores.json");
    private static final int MAX_SCORES = 10;
    private static final Comparator<HighScoreEntry> SCORE_ORDER =
        Comparator.comparingDouble(HighScoreEntry::elapsedSeconds)
            .thenComparing(HighScoreEntry::initials);

    private final List<HighScoreEntry> entries;

    /**
     * Creates an empty score store.
     */
    public HighScoreStore() {
        entries = new ArrayList<>();
    }

    /**
     * Loads saved scores from disk.
     *
     * @return the loaded store
     */
    public static HighScoreStore load() {
        HighScoreStore store = new HighScoreStore();
        if (!Files.exists(STORE_PATH)) {
            return store;
        }

        try {
            String json = Files.readString(STORE_PATH, StandardCharsets.UTF_8);
            Matcher matcher = Pattern.compile(
                "\\{\\s*\"initials\"\\s*:\\s*\"((?:\\\\.|[^\\\\\"])*)\"\\s*,\\s*\"elapsedSeconds\"\\s*:\\s*(-?\\d+(?:\\.\\d+)?)\\s*\\}"
            ).matcher(json);
            while (matcher.find()) {
                String initials = sanitizeInitials(unescapeJson(matcher.group(1)));
                double elapsedSeconds = parseElapsedSeconds(matcher.group(2));
                if (!initials.isBlank() && Double.isFinite(elapsedSeconds) && elapsedSeconds > 0.0) {
                    store.entries.add(new HighScoreEntry(initials, elapsedSeconds));
                }
            }
            store.trimAndSort();
        } catch (IOException exception) {
            return store;
        }
        return store;
    }

    /**
     * Saves the current leaderboard to disk.
     */
    public void save() {
        trimAndSort();
        StringBuilder builder = new StringBuilder();
        builder.append("{\n");
        builder.append("  \"scores\": [\n");
        for (int index = 0; index < entries.size(); index++) {
            HighScoreEntry entry = entries.get(index);
            builder.append("    { \"initials\": \"")
                .append(escapeJson(entry.initials()))
                .append("\", \"elapsedSeconds\": ")
                .append(formatElapsedSeconds(entry.elapsedSeconds()))
                .append(" }");
            if (index < entries.size() - 1) {
                builder.append(",");
            }
            builder.append("\n");
        }
        builder.append("  ]\n");
        builder.append("}\n");

        try {
            Files.writeString(STORE_PATH, builder.toString(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            // Ignore save failures and keep the in-memory leaderboard.
        }
    }

    /**
     * Returns the current top scores.
     *
     * @return an immutable copy of the top scores
     */
    public List<HighScoreEntry> getEntries() {
        trimAndSort();
        return List.copyOf(entries);
    }

    /**
     * Checks whether a completion time belongs on the leaderboard.
     *
     * @param elapsedSeconds completion time for a 100-point run
     * @return true when the run belongs in the top 10
     */
    public boolean qualifies(double elapsedSeconds) {
        if (!Double.isFinite(elapsedSeconds) || elapsedSeconds <= 0.0) {
            return false;
        }
        trimAndSort();
        return entries.size() < MAX_SCORES || elapsedSeconds < entries.get(entries.size() - 1).elapsedSeconds();
    }

    /**
     * Inserts a new score and keeps only the top 10 entries.
     *
     * @param initials player initials
     * @param elapsedSeconds completion time
     */
    public void addScore(String initials, double elapsedSeconds) {
        String normalizedInitials = sanitizeInitials(initials);
        if (normalizedInitials.isBlank() || !Double.isFinite(elapsedSeconds) || elapsedSeconds <= 0.0) {
            return;
        }
        entries.add(new HighScoreEntry(normalizedInitials, elapsedSeconds));
        trimAndSort();
    }

    /**
     * Formats the leaderboard for a multi-line UI label.
     *
     * @return a compact summary string
     */
    public String toDisplayString() {
        trimAndSort();
        if (entries.isEmpty()) {
            return "No recorded times yet.\nFinish a 100-point run to set the pace.";
        }

        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < entries.size(); index++) {
            HighScoreEntry entry = entries.get(index);
            builder.append(index + 1)
                .append(". ")
                .append(entry.initials())
                .append(" - ")
                .append(formatForDisplay(entry.elapsedSeconds()));
            if (index < entries.size() - 1) {
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    /**
     * Immutable leaderboard entry.
     *
     * @param initials player initials
     * @param elapsedSeconds completion time
     */
    public record HighScoreEntry(String initials, double elapsedSeconds) {
    }

    /**
     * Formats a run time for status messages and UI output.
     *
     * @param elapsedSeconds completion time in seconds
     * @return human-readable elapsed time
     */
    public static String formatForDisplay(double elapsedSeconds) {
        return String.format("%.2f s", elapsedSeconds);
    }

    /**
     * Normalizes a user-entered initials string to up to three characters.
     *
     * @param initials raw initials
     * @return uppercase initials using letters and digits only
     */
    public static String sanitizeInitials(String initials) {
        if (initials == null) {
            return "";
        }
        String cleaned = initials.toUpperCase().replaceAll("[^A-Z0-9]", "");
        if (cleaned.length() > 3) {
            return cleaned.substring(0, 3);
        }
        return cleaned;
    }

    private void trimAndSort() {
        entries.sort(SCORE_ORDER);
        if (entries.size() > MAX_SCORES) {
            entries.subList(MAX_SCORES, entries.size()).clear();
        }
    }

    private static double parseElapsedSeconds(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException exception) {
            return Double.NaN;
        }
    }

    private static String formatElapsedSeconds(double elapsedSeconds) {
        return String.format("%.3f", elapsedSeconds);
    }

    private static String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static String unescapeJson(String value) {
        return value.replace("\\\"", "\"").replace("\\\\", "\\");
    }
}
