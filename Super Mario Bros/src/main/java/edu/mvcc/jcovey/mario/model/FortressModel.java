package edu.mvcc.jcovey.mario.model;

public class FortressModel {
    private final double x;
    private final double y;
    private final int widthInTiles;
    private final int heightInTiles;
    private final double doorwayX;
    private final double doorwayY;
    private final int doorwayWidthInTiles;
    private final int doorwayHeightInTiles;

    public FortressModel(
        double x,
        double y,
        int widthInTiles,
        int heightInTiles,
        double doorwayX,
        double doorwayY,
        int doorwayWidthInTiles,
        int doorwayHeightInTiles
    ) {
        this.x = x;
        this.y = y;
        this.widthInTiles = widthInTiles;
        this.heightInTiles = heightInTiles;
        this.doorwayX = doorwayX;
        this.doorwayY = doorwayY;
        this.doorwayWidthInTiles = doorwayWidthInTiles;
        this.doorwayHeightInTiles = doorwayHeightInTiles;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return widthInTiles * GameConstants.TILE_SIZE;
    }

    public double getHeight() {
        return heightInTiles * GameConstants.TILE_SIZE;
    }

    public double getDoorwayCenterX() {
        return doorwayX + ((doorwayWidthInTiles * GameConstants.TILE_SIZE) / 2.0);
    }

    public PhysicsRect getDoorwayZone() {
        return new PhysicsRect(
            doorwayX,
            doorwayY,
            doorwayWidthInTiles * GameConstants.TILE_SIZE,
            doorwayHeightInTiles * GameConstants.TILE_SIZE
        );
    }
}
