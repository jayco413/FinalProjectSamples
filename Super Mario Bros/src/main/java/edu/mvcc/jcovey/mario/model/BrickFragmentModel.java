package edu.mvcc.jcovey.mario.model;

public class BrickFragmentModel {
    private final int sourceColumn;
    private final int sourceRow;
    private double x;
    private double y;
    private double velocityX;
    private double velocityY;
    private double lifetime;

    public BrickFragmentModel(double x, double y, double velocityX, double velocityY, int sourceColumn, int sourceRow) {
        this.sourceColumn = sourceColumn;
        this.sourceRow = sourceRow;
        this.x = x;
        this.y = y;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        lifetime = 0.5;
    }

    public void update(double deltaSeconds) {
        x += velocityX * deltaSeconds;
        y += velocityY * deltaSeconds;
        velocityY += 720.0 * deltaSeconds;
        lifetime -= deltaSeconds;
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

    public int getSourceColumn() {
        return sourceColumn;
    }

    public int getSourceRow() {
        return sourceRow;
    }
}
