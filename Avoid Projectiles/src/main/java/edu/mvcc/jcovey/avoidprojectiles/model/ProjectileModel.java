package edu.mvcc.jcovey.avoidprojectiles.model;

/**
 * Model for a projectile or power-up moving across the playfield.
 *
 * @author Jason A. Covey
 */
public class ProjectileModel {
    private final int id;
    private final ProjectileKind kind;
    private double x;
    private double y;
    private double speed;

    /**
     * Creates a projectile model.
     *
     * @param id unique identifier
     * @param kind projectile type
     * @param x initial x position
     * @param y initial y position
     * @param speed horizontal speed
     */
    public ProjectileModel(int id, ProjectileKind kind, double x, double y, double speed) {
        this.id = id;
        this.kind = kind;
        this.x = x;
        this.y = y;
        this.speed = speed;
    }

    /**
     * Moves the projectile one frame to the left.
     */
    public void advance() {
        x -= speed;
    }

    /**
     * Gets the projectile identifier.
     *
     * @return unique id
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the projectile kind.
     *
     * @return the kind
     */
    public ProjectileKind getKind() {
        return kind;
    }

    /**
     * Gets the projectile width.
     *
     * @return width in pixels
     */
    public double getWidth() {
        return kind.getWidth();
    }

    /**
     * Gets the projectile height.
     *
     * @return height in pixels
     */
    public double getHeight() {
        return kind.getWidth();
    }

    /**
     * Gets the projectile x position.
     *
     * @return the x position
     */
    public double getX() {
        return x;
    }

    /**
     * Gets the projectile y position.
     *
     * @return the y position
     */
    public double getY() {
        return y;
    }
}
