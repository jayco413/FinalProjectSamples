package edu.mvcc.jcovey.avoidprojectiles.model;

/**
 * Model describing Mario's state inside the playfield.
 *
 * @author Jason A. Covey
 */
public class PlayerModel {
    private static final double BASE_WIDTH = 75.0;
    private static final double HEIGHT = 75.0;

    private double x;
    private double y;
    private int miniStackCount;
    private int invincibilityTicksRemaining;

    /**
     * Resets the player to the original location and state.
     */
    public void reset() {
        x = 100.0;
        y = 250.0;
        miniStackCount = 0;
        invincibilityTicksRemaining = 0;
    }

    /**
     * Moves the player within the supplied world bounds.
     *
     * @param input the current input state
     * @param speed movement speed in pixels per frame
     * @param worldWidth playfield width
     * @param worldHeight playfield height
     */
    public void move(InputState input, double speed, double worldWidth, double worldHeight) {
        if (input.isUpPressed()) {
            y = Math.max(0.0, y - speed);
        }
        if (input.isDownPressed()) {
            y = Math.min(worldHeight - getHeight(), y + speed);
        }
        if (input.isLeftPressed()) {
            x = Math.max(0.0, x - speed);
        }
        if (input.isRightPressed()) {
            x = Math.min(worldWidth - getWidth(), x + speed);
        }
    }

    /**
     * Decrements active timed effects.
     */
    public void tickEffects() {
        if (invincibilityTicksRemaining > 0) {
            invincibilityTicksRemaining--;
        }
    }

    /**
     * Enables invincibility for the supplied duration.
     *
     * @param ticks duration in frames
     */
    public void activateInvincibility(int ticks) {
        invincibilityTicksRemaining = Math.max(invincibilityTicksRemaining, ticks);
    }

    /**
     * Applies one mini effect instance.
     */
    public void applyMiniEffect() {
        miniStackCount++;
    }

    /**
     * Removes one mini effect instance if present.
     */
    public void removeMiniEffect() {
        if (miniStackCount > 0) {
            miniStackCount--;
        }
    }

    public boolean isInvincible() {
        return invincibilityTicksRemaining > 0;
    }

    /**
     * Gets the remaining invincibility duration in frames.
     *
     * @return remaining invincibility frames
     */
    public int getInvincibilityTicksRemaining() {
        return invincibilityTicksRemaining;
    }

    /**
     * Gets the player's current rendered width.
     *
     * @return current width in pixels
     */
    public double getWidth() {
        return BASE_WIDTH * Math.pow(0.9, miniStackCount);
    }

    /**
     * Gets the player's current rendered height.
     *
     * @return current height in pixels
     */
    public double getHeight() {
        return HEIGHT;
    }

    /**
     * Gets the player's x position.
     *
     * @return the x position
     */
    public double getX() {
        return x;
    }

    /**
     * Gets the player's y position.
     *
     * @return the y position
     */
    public double getY() {
        return y;
    }
}
