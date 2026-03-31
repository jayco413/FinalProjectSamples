package edu.mvcc.jcovey.mario.model;

/**
 * Base model for moving entities.
 *
 * @author Jason A. Covey
 */
public class DynamicEntityModel extends EntityModel {
    private double velocityX;
    private double velocityY;

    /**
     * Creates a moving entity.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width
     * @param height the height
     */
    protected DynamicEntityModel(double x, double y, double width, double height) {
        super(x, y, width, height);
    }

    /**
     * Gets horizontal velocity.
     *
     * @return the horizontal velocity
     */
    public double getVelocityX() {
        return velocityX;
    }

    /**
     * Sets horizontal velocity.
     *
     * @param velocityX the horizontal velocity
     */
    public void setVelocityX(double velocityX) {
        this.velocityX = velocityX;
    }

    /**
     * Gets vertical velocity.
     *
     * @return the vertical velocity
     */
    public double getVelocityY() {
        return velocityY;
    }

    /**
     * Sets vertical velocity.
     *
     * @param velocityY the vertical velocity
     */
    public void setVelocityY(double velocityY) {
        this.velocityY = velocityY;
    }
}
