package edu.mvcc.jcovey.avoidprojectiles.model;

import edu.mvcc.jcovey.avoidprojectiles.app.UserPreferences;
import java.security.SecureRandom;
import java.text.NumberFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Source-of-truth gameplay model for Avoid Projectiles.
 *
 * <p>Original gameplay and asset set by Jason A. Covey. This MVC rewrite was
 * reorganized for final-project compliance while preserving the core features.</p>
 * <p>AI-assisted timing and leaderboard additions reviewed and integrated by
 * Jason A. Covey.</p>
 *
 * @author Jason A. Covey
 */
public class GameModel {
    public static final double WORLD_WIDTH = 800.0;
    public static final double WORLD_HEIGHT = 500.0;
    private static final int INVINCIBILITY_TICKS = 1000;
    private static final int MINI_TICKS = 1200;
    private static final NumberFormat SCORE_FORMAT = NumberFormat.getIntegerInstance(Locale.US);

    private final PlayerModel player;
    private final List<ProjectileModel> projectiles;
    private final Deque<Integer> miniTimers;
    private final Deque<String> queuedSounds;
    private final Deque<String> queuedSoundStops;
    private final Random random;

    private GamePhase phase;
    private double backgroundOffset;
    private int score;
    private int nextProjectileId;
    private long tickCount;
    private long runStartNanos;
    private double elapsedSeconds;
    private String statusText;
    private RunCompletion pendingCompletion;

    /**
     * Creates a fresh game model.
     */
    public GameModel() {
        player = new PlayerModel();
        projectiles = new ArrayList<>();
        miniTimers = new ArrayDeque<>();
        queuedSounds = new ArrayDeque<>();
        queuedSoundStops = new ArrayDeque<>();
        random = new SecureRandom();
        SCORE_FORMAT.setGroupingUsed(false);
        SCORE_FORMAT.setMinimumIntegerDigits(6);
        resetToReadyState("Press Start Game to begin.");
    }

    /**
     * Starts a new run.
     */
    public void startGame() {
        resetToReadyState("Avoid the projectiles.");
        phase = GamePhase.PLAYING;
    }

    /**
     * Advances the game by one frame.
     *
     * @param input the current user input
     * @param preferences active user preferences
     * @param nowNanos current animation clock time
     */
    public void update(InputState input, UserPreferences preferences, long nowNanos) {
        if (phase != GamePhase.PLAYING) {
            return;
        }

        if (runStartNanos <= 0L) {
            runStartNanos = nowNanos;
        }

        boolean wasInvincible = player.isInvincible();
        tickCount++;
        elapsedSeconds = (nowNanos - runStartNanos) / 1_000_000_000.0;
        backgroundOffset = (backgroundOffset + getBackgroundSpeed(preferences)) % WORLD_WIDTH;
        player.move(input, getPlayerSpeed(preferences), WORLD_WIDTH, WORLD_HEIGHT);
        player.tickEffects();
        if (wasInvincible && !player.isInvincible()) {
            queuedSoundStops.addLast(ProjectileKind.STARMAN.getSoundName());
            statusText = "Starman effect ended.";
        }
        tickMiniEffects();
        spawnProjectilesIfNeeded(preferences);
        updateProjectiles();

        if (score >= 100) {
            completeRun();
        }
    }

    /**
     * Returns and clears queued sound requests for the current frame.
     *
     * @return sound file names to play
     */
    public List<String> drainSoundEvents() {
        List<String> sounds = new ArrayList<>(queuedSounds);
        queuedSounds.clear();
        return sounds;
    }

    /**
     * Returns and clears queued sound stop requests for the current frame.
     *
     * @return sound file names to stop
     */
    public List<String> drainStoppedSoundEvents() {
        List<String> sounds = new ArrayList<>(queuedSoundStops);
        queuedSoundStops.clear();
        return sounds;
    }

    /**
     * Gets the player model.
     *
     * @return the player model
     */
    public PlayerModel getPlayer() {
        return player;
    }

    /**
     * Gets the active projectiles.
     *
     * @return active projectile models
     */
    public List<ProjectileModel> getProjectiles() {
        return projectiles;
    }

    /**
     * Gets the current phase of play.
     *
     * @return the game phase
     */
    public GamePhase getPhase() {
        return phase;
    }

    /**
     * Gets the current scrolling background offset.
     *
     * @return background offset in pixels
     */
    public double getBackgroundOffset() {
        return backgroundOffset;
    }

    /**
     * Gets the score formatted for the HUD.
     *
     * @return formatted score text
     */
    public String getFormattedScore() {
        return SCORE_FORMAT.format(score);
    }

    /**
     * Gets the status text shown in the HUD.
     *
     * @return current status text
     */
    public String getStatusText() {
        return statusText;
    }

    /**
     * Gets the current gameplay tick counter.
     *
     * @return elapsed update ticks in the current run
     */
    public long getTickCount() {
        return tickCount;
    }

    /**
     * Gets the elapsed time for the active or most recent run.
     *
     * @return elapsed seconds
     */
    public double getElapsedSeconds() {
        return elapsedSeconds;
    }

    /**
     * Returns and clears a pending run-completion event.
     *
     * @return the completed run snapshot, or {@code null} when no run just finished
     */
    public RunCompletion consumePendingCompletion() {
        RunCompletion completion = pendingCompletion;
        pendingCompletion = null;
        return completion;
    }

    private void tickMiniEffects() {
        int effectCount = miniTimers.size();
        for (int index = 0; index < effectCount; index++) {
            int remaining = miniTimers.removeFirst() - 1;
            if (remaining <= 0) {
                player.removeMiniEffect();
            } else {
                miniTimers.addLast(remaining);
            }
        }
    }

    private void spawnProjectilesIfNeeded(UserPreferences preferences) {
        int maxProjectiles = getMaxProjectileCount(preferences);
        if (projectiles.size() >= maxProjectiles) {
            return;
        }

        if (random.nextInt(10) >= getSpawnThreshold(preferences)) {
            return;
        }

        ProjectileKind kind = rollProjectileKind();
        double x = WORLD_WIDTH + random.nextInt(300) + 200;
        double y = random.nextInt((int) WORLD_HEIGHT);
        double speed = kind == ProjectileKind.STARMAN ? 7.0 : random.nextInt(10) + 1;
        projectiles.add(new ProjectileModel(nextProjectileId++, kind, x, y, speed));
    }

    private ProjectileKind rollProjectileKind() {
        if (random.nextInt(10) == 1) {
            return ProjectileKind.STARMAN;
        }
        if (random.nextInt(10) < 2) {
            return ProjectileKind.MINI_MUSHROOM;
        }
        return ProjectileKind.BULLET;
    }

    private void updateProjectiles() {
        Iterator<ProjectileModel> iterator = projectiles.iterator();
        while (iterator.hasNext()) {
            ProjectileModel projectile = iterator.next();
            projectile.advance();

            if (projectile.getX() + projectile.getWidth() < 0.0) {
                if (projectile.getKind() == ProjectileKind.BULLET) {
                    score++;
                    statusText = "Score increased. Keep surviving.";
                }
                iterator.remove();
                continue;
            }

            if (intersects(projectile)) {
                handleCollision(projectile);
                iterator.remove();
            }
        }
    }

    private boolean intersects(ProjectileModel projectile) {
        return projectile.getX() < player.getX() + player.getWidth()
            && projectile.getX() + projectile.getWidth() > player.getX()
            && projectile.getY() < player.getY() + player.getHeight()
            && projectile.getY() + projectile.getHeight() > player.getY();
    }

    private void handleCollision(ProjectileModel projectile) {
        if (projectile.getKind() == ProjectileKind.BULLET) {
            if (!player.isInvincible()) {
                score--;
                queuedSounds.addLast(projectile.getKind().getSoundName());
                statusText = "Bullet Bill hit Mario. Keep going.";
            } else {
                statusText = "Invincible Mario ignored a Bullet Bill.";
            }
            score = Math.max(score, 0);
            return;
        }

        if (projectile.getKind() == ProjectileKind.STARMAN) {
            if (player.isInvincible()) {
                statusText = "Starman ignored because invincibility is already active.";
                return;
            }
            score += 10;
            queuedSounds.addLast(projectile.getKind().getSoundName());
            player.activateInvincibility(INVINCIBILITY_TICKS);
            statusText = "Starman collected. Mario is invincible.";
        } else {
            score += 5;
            queuedSounds.addLast(projectile.getKind().getSoundName());
            player.applyMiniEffect();
            miniTimers.addLast(MINI_TICKS);
            statusText = "Mini Mushroom collected. Mario shrank.";
        }
    }

    private double getBackgroundSpeed(UserPreferences preferences) {
        return switch (preferences.getDifficulty()) {
            case "Relaxed" -> 1.0;
            case "Chaotic" -> 3.0;
            default -> 2.0;
        };
    }

    private double getPlayerSpeed(UserPreferences preferences) {
        return switch (preferences.getPlayerSpeed()) {
            case "Slow" -> 1.5;
            case "Fast" -> 3.0;
            default -> 2.0;
        };
    }

    private int getMaxProjectileCount(UserPreferences preferences) {
        int base = score <= 30 ? 3 : Math.max(3, score / 10);
        return switch (preferences.getDifficulty()) {
            case "Relaxed" -> Math.max(2, base - 1);
            case "Chaotic" -> base + 2;
            default -> base;
        };
    }

    private int getSpawnThreshold(UserPreferences preferences) {
        return switch (preferences.getDifficulty()) {
            case "Relaxed" -> 2;
            case "Chaotic" -> 4;
            default -> 3;
        };
    }

    private void resetToReadyState(String statusMessage) {
        phase = GamePhase.READY;
        backgroundOffset = 0.0;
        score = 0;
        nextProjectileId = 1;
        tickCount = 0L;
        runStartNanos = -1L;
        elapsedSeconds = 0.0;
        projectiles.clear();
        miniTimers.clear();
        queuedSounds.clear();
        queuedSoundStops.clear();
        pendingCompletion = null;
        player.reset();
        statusText = statusMessage;
    }

    private void completeRun() {
        phase = GamePhase.READY;
        backgroundOffset = 0.0;
        projectiles.clear();
        miniTimers.clear();
        queuedSounds.clear();
        queuedSoundStops.clear();
        queuedSoundStops.addLast(ProjectileKind.STARMAN.getSoundName());
        player.reset();
        pendingCompletion = new RunCompletion(score, elapsedSeconds);
        statusText = "You reached 100 points in " + formatElapsedSeconds(elapsedSeconds) + ".";
    }

    private static String formatElapsedSeconds(double seconds) {
        return String.format("%.2f seconds", seconds);
    }
}
