package edu.mvcc.jcovey.mario.model;

public class CameraModel {
    private double x;

    public void reset() {
        x = 0.0;
    }

    public void update(double targetCenterX, double worldWidth) {
        double desiredX = targetCenterX - (GameConstants.VIEWPORT_WIDTH / 2.0);
        x = Math.max(0.0, Math.min(desiredX, worldWidth - GameConstants.VIEWPORT_WIDTH));
    }

    public double getX() {
        return x;
    }
}
