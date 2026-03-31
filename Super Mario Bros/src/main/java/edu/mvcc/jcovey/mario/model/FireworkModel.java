package edu.mvcc.jcovey.mario.model;

public class FireworkModel {
    private final double x;
    private final double y;
    private double ageSeconds;

    public FireworkModel(double x, double y) {
        this.x = x;
        this.y = y;
        this.ageSeconds = 0.0;
    }

    public void update(double deltaSeconds) {
        ageSeconds += deltaSeconds;
    }

    public boolean isExpired() {
        return ageSeconds >= 0.8;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getAgeSeconds() {
        return ageSeconds;
    }
}
