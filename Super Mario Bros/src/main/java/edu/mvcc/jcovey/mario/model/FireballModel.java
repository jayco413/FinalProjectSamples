package edu.mvcc.jcovey.mario.model;

/**
 * Model representing a fireball projectile.
 *
 * @author Jason A. Covey
 */
public class FireballModel extends DynamicEntityModel {
    private boolean active;

    public FireballModel(double x, double y, boolean facingRight) {
        super(x, y, 16.0, 16.0);
        setVelocityX(facingRight ? GameConstants.FIREBALL_SPEED : -GameConstants.FIREBALL_SPEED);
        setVelocityY(-120.0);
        active = true;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
