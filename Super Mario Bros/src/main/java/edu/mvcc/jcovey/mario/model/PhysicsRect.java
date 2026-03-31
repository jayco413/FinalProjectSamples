package edu.mvcc.jcovey.mario.model;

public class PhysicsRect {
    private final double x;
    private final double y;
    private final double width;
    private final double height;

    public PhysicsRect(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
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

    public boolean intersects(PhysicsRect other) {
        return x < other.x + other.width
            && x + width > other.x
            && y < other.y + other.height
            && y + height > other.y;
    }
}
