package edu.mvcc.jcovey.mario.model;

/**
 * Model representing Mario state.
 *
 * @author Jason A. Covey
 */
public class MarioModel extends DynamicEntityModel {
    private boolean facingRight;
    private boolean onGround;
    private boolean alive;
    private boolean superForm;
    private boolean fireForm;
    private boolean climbingFlag;
    private boolean hidden;
    private boolean skidding;
    private boolean crouching;
    private double animationTime;
    private double invincibilityTimer;
    private double starPowerTimer;

    public MarioModel() {
        super(80.0, 384.0, 32.0, 32.0);
        reset();
    }

    public void reset() {
        setX(80.0);
        setY(384.0);
        setVelocityX(0.0);
        setVelocityY(0.0);
        facingRight = true;
        onGround = false;
        alive = true;
        superForm = false;
        fireForm = false;
        climbingFlag = false;
        hidden = false;
        skidding = false;
        crouching = false;
        animationTime = 0.0;
        invincibilityTimer = 0.0;
        starPowerTimer = 0.0;
        setWidth(MarioPoseCatalog.getSlotWidth(this));
        setHeight(MarioPoseCatalog.getSlotHeight(this));
    }

    public PhysicsRect getCollisionBounds() {
        return new PhysicsRect(
            getX() + getCollisionInsetLeft(),
            getY() + getCollisionInsetTop(),
            getCollisionWidth(),
            getCollisionHeight()
        );
    }

    public double getCenterX() {
        return getCollisionBounds().getX() + (getCollisionWidth() / 2.0);
    }

    public double getFeetY() {
        return getY() + getHeight();
    }

    public void addAnimationTime(double deltaSeconds) {
        animationTime += deltaSeconds;
    }

    public double getAnimationTime() {
        return animationTime;
    }

    public void update(double deltaSeconds) {
        invincibilityTimer = Math.max(0.0, invincibilityTimer - deltaSeconds);
        starPowerTimer = Math.max(0.0, starPowerTimer - deltaSeconds);
    }

    public double getCollisionInsetLeft() {
        return MarioPoseCatalog.getCollisionInsetLeft(this);
    }

    public double getCollisionInsetTop() {
        return MarioPoseCatalog.getCollisionInsetTop(this);
    }

    public double getCollisionWidth() {
        return MarioPoseCatalog.getCollisionWidth(this);
    }

    public double getCollisionHeight() {
        return MarioPoseCatalog.getCollisionHeight(this);
    }

    public boolean isFacingRight() {
        return facingRight;
    }

    public void setFacingRight(boolean facingRight) {
        this.facingRight = facingRight;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public boolean isSuperForm() {
        return superForm;
    }

    public void setSuperForm(boolean superForm) {
        this.superForm = superForm;
        if (!superForm) {
            fireForm = false;
        }
        setWidth(MarioPoseCatalog.getSlotWidth(this));
        setHeight(MarioPoseCatalog.getSlotHeight(this));
    }

    public boolean isFireForm() {
        return fireForm;
    }

    public void setFireForm(boolean fireForm) {
        this.fireForm = fireForm;
        if (fireForm) {
            setSuperForm(true);
        }
    }

    public boolean isInvincible() {
        return invincibilityTimer > 0.0;
    }

    public void startInvincibility(double seconds) {
        invincibilityTimer = seconds;
    }

    public boolean hasStarPower() {
        return starPowerTimer > 0.0;
    }

    public double getStarPowerTimer() {
        return starPowerTimer;
    }

    public void startStarPower(double seconds) {
        starPowerTimer = seconds;
    }

    public boolean isClimbingFlag() {
        return climbingFlag;
    }

    public void setClimbingFlag(boolean climbingFlag) {
        this.climbingFlag = climbingFlag;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isSkidding() {
        return skidding;
    }

    public void setSkidding(boolean skidding) {
        this.skidding = skidding;
    }

    public boolean isCrouching() {
        return crouching;
    }

    public void setCrouching(boolean crouching) {
        this.crouching = crouching;
    }
}
