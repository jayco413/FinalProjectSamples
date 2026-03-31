package edu.mvcc.jcovey.mario.model;

public class PipeModel {
    private final double x;
    private final double y;
    private final int heightInTiles;
    private final String warpId;
    private final String orientation;

    public PipeModel(double x, double y, int heightInTiles, String warpId, String orientation) {
        this.x = x;
        this.y = y;
        this.heightInTiles = heightInTiles;
        this.warpId = warpId;
        this.orientation = orientation;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getHeightInTiles() {
        return heightInTiles;
    }

    public String getWarpId() {
        return warpId;
    }

    public String getOrientation() {
        return orientation;
    }

    public PhysicsRect getTopBounds() {
        return new PhysicsRect(x, y, GameConstants.TILE_SIZE * 2.0, GameConstants.TILE_SIZE);
    }

    public double getCenterX() {
        return x + GameConstants.TILE_SIZE;
    }
}
