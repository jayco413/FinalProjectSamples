package edu.mvcc.jcovey.mario.model;

public class PlatformModel {
    private final double x;
    private final double width;
    private final double height;
    private final boolean movesDown;
    private final double wrapTopY;
    private final double wrapBottomY;
    private double y;
    private double previousY;
    private double velocityY;

    public PlatformModel(double x, double startY, boolean movesDown) {
        this.x = x;
        this.movesDown = movesDown;
        this.width = GameConstants.TILE_SIZE * 3.0;
        this.height = GameConstants.TILE_SIZE * 0.5;
        this.wrapTopY = (GameConstants.HUD_ROWS * GameConstants.TILE_SIZE) - GameConstants.TILE_SIZE;
        this.wrapBottomY = GameConstants.VIEWPORT_HEIGHT;
        this.y = startY;
        this.previousY = y;
        this.velocityY = movesDown ? 65.0 : -65.0;
    }

    public void update(double deltaSeconds) {
        previousY = y;
        y += velocityY * deltaSeconds;
        if (movesDown && y >= wrapBottomY) {
            y = wrapTopY;
            previousY = y;
        } else if (!movesDown && y + height <= wrapTopY) {
            y = wrapBottomY;
            previousY = y;
        }
    }

    public PhysicsRect getBounds() {
        return new PhysicsRect(x, y, width, height);
    }

    public double getDeltaY() {
        return y - previousY;
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
}
