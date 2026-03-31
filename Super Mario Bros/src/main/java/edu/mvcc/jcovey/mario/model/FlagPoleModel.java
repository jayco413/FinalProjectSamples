package edu.mvcc.jcovey.mario.model;

public class FlagPoleModel {
    private final double x;
    private final double topY;
    private final double bottomY;
    private double flagY;
    private boolean triggered;

    public FlagPoleModel(double x, double topY, double bottomY) {
        this.x = x;
        this.topY = topY;
        this.bottomY = bottomY;
        this.flagY = topY;
        this.triggered = false;
    }

    public double getX() {
        return x;
    }

    public double getTopY() {
        return topY;
    }

    public double getBottomY() {
        return bottomY;
    }

    public double getFlagY() {
        return flagY;
    }

    public void resetFlag() {
        flagY = topY;
        triggered = false;
    }

    public void trigger(double marioY) {
        triggered = true;
        flagY = clampFlagY(marioY);
    }

    public void followMario(double marioY) {
        if (!triggered) {
            return;
        }
        flagY = clampFlagY(Math.max(flagY, marioY));
    }

    public boolean isTriggered() {
        return triggered;
    }

    public PhysicsRect getZone() {
        return new PhysicsRect(x, topY, GameConstants.TILE_SIZE, bottomY - topY);
    }

    private double clampFlagY(double value) {
        return Math.max(topY, Math.min(bottomY - GameConstants.TILE_SIZE, value));
    }
}
