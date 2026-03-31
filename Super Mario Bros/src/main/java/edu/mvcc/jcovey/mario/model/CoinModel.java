package edu.mvcc.jcovey.mario.model;

public class CoinModel {
    private final double x;
    private final double y;
    private final double width;
    private final double height;
    private boolean collected;

    public CoinModel(double x, double y) {
        this.x = x;
        this.y = y;
        width = GameConstants.TILE_SIZE;
        height = GameConstants.TILE_SIZE;
        collected = false;
    }

    public PhysicsRect getBounds() {
        return new PhysicsRect(x, y, width, height);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }
}
