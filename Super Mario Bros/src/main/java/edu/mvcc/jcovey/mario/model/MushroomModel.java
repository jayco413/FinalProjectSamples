package edu.mvcc.jcovey.mario.model;

/**
 * Model representing mushroom-like collectibles.
 *
 * @author Jason A. Covey
 */
public class MushroomModel extends DynamicEntityModel {
    private final String type;
    private boolean active;

    public MushroomModel(double x, double y, String type) {
        super(x, y, 28.0, 28.0);
        this.type = type;
        setVelocityX("fireflower".equals(type) ? 0.0 : GameConstants.MUSHROOM_SPEED);
        setVelocityY(0.0);
        active = true;
    }

    public String getType() {
        return type;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
