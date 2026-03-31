package edu.mvcc.jcovey.mario.model;

public class FloatingCoinModel {
    private double x;
    private double y;
    private double velocityY;
    private double lifetime;
    private boolean sparkling;
    private double sparkleTimer;

    public FloatingCoinModel(double x, double y) {
        this.x = x;
        this.y = y;
        velocityY = -180.0;
        lifetime = 0.55;
        sparkling = false;
        sparkleTimer = 0.0;
    }

    public void update(double deltaSeconds) {
        if (sparkling) {
            sparkleTimer -= deltaSeconds;
            lifetime -= deltaSeconds;
            return;
        }

        y += velocityY * deltaSeconds;
        velocityY += 420.0 * deltaSeconds;
        if (velocityY >= 0.0) {
            sparkling = true;
            sparkleTimer = 0.12;
            lifetime = Math.min(lifetime, sparkleTimer);
        } else {
            lifetime -= deltaSeconds;
        }
    }

    public boolean isExpired() {
        return lifetime <= 0.0;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public boolean isSparkling() {
        return sparkling;
    }

    public double getSparkleTimer() {
        return sparkleTimer;
    }
}
