package edu.mvcc.jcovey.mario.model;

/**
 * Model representing a star power-up.
 *
 * @author Jason A. Covey
 */
public class StarModel extends DynamicEntityModel {
    private boolean active;

    public StarModel(double x, double y) {
        super(x, y, 28.0, 28.0);
        setVelocityX(GameConstants.STAR_SPEED);
        setVelocityY(GameConstants.STAR_BOUNCE_SPEED);
        active = true;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
