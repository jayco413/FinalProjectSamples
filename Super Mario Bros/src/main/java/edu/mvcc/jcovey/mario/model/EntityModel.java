package edu.mvcc.jcovey.mario.model;

/**
 * Base model for rectangular world entities.
 *
 * @author Jason A. Covey
 */
public class EntityModel {
    private double x;
    private double y;
    private double width;
    private double height;

    /**
     * Creates an entity model.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the entity width
     * @param height the entity height
     */
    protected EntityModel(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Gets the entity bounds.
     *
     * @return the bounds rectangle
     */
    public PhysicsRect getBounds() {
        return new PhysicsRect(x, y, width, height);
    }

    /**
     * Gets the x coordinate.
     *
     * @return the x coordinate
     */
    public double getX() {
        return x;
    }

    /**
     * Sets the x coordinate.
     *
     * @param x the x coordinate
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Gets the y coordinate.
     *
     * @return the y coordinate
     */
    public double getY() {
        return y;
    }

    /**
     * Sets the y coordinate.
     *
     * @param y the y coordinate
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Gets the width.
     *
     * @return the width
     */
    public double getWidth() {
        return width;
    }

    /**
     * Sets the width.
     *
     * @param width the width
     */
    protected void setWidth(double width) {
        this.width = width;
    }

    /**
     * Gets the height.
     *
     * @return the height
     */
    public double getHeight() {
        return height;
    }

    /**
     * Sets the height.
     *
     * @param height the height
     */
    protected void setHeight(double height) {
        this.height = height;
    }
}
