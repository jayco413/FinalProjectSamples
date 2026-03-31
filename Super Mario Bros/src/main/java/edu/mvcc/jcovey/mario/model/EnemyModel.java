package edu.mvcc.jcovey.mario.model;

/**
 * Model representing an enemy.
 *
 * @author Jason A. Covey
 */
public class EnemyModel extends DynamicEntityModel {
    private static final double BUMP_DEFEAT_DURATION = 0.6;
    private static final double FIRE_DEFEAT_DURATION = 0.45;

    private final String type;
    private double x;
    private double y;
    private double width;
    private double height;
    private double velocityX;
    private double velocityY;
    private boolean alive;
    private boolean activated;
    private boolean flattened;
    private double flattenedTimer;
    private double defeatTimer;
    private boolean onGround;
    private double parakoopaJumpCooldown;
    private String defeatMode;

    public EnemyModel(String type, double x, double y) {
        super(x, y, 28.0, 28.0);
        this.type = type;
        setVelocityX(-GameConstants.ENEMY_SPEED);
        setVelocityY(0.0);
        alive = true;
        activated = false;
        flattened = false;
        flattenedTimer = 0.0;
        defeatTimer = 0.0;
        onGround = false;
        parakoopaJumpCooldown = 0.9;
        defeatMode = "";
    }

    public String getType() {
        return type;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public boolean isFlattened() {
        return flattened;
    }

    public boolean isInDefeatAnimation() {
        return !defeatMode.isEmpty();
    }

    public boolean isBumpDefeated() {
        return "bump".equals(defeatMode);
    }

    public boolean isFireDefeated() {
        return "fire".equals(defeatMode);
    }

    public double getDefeatTimer() {
        return defeatTimer;
    }

    public double getDefeatProgress() {
        double duration = isBumpDefeated() ? BUMP_DEFEAT_DURATION : FIRE_DEFEAT_DURATION;
        if (duration <= 0.0) {
            return 1.0;
        }
        return 1.0 - Math.max(0.0, Math.min(1.0, defeatTimer / duration));
    }

    public boolean isParakoopa() {
        return "parakoopa".equals(type);
    }

    public void flatten() {
        flattened = true;
        flattenedTimer = 0.45;
        setHeight(18.0);
        setVelocityX(0.0);
        setVelocityY(0.0);
    }

    public void knockDownToKoopa() {
        setWidth(28.0);
        setHeight(28.0);
        setVelocityY(0.0);
        onGround = false;
        parakoopaJumpCooldown = 0.0;
    }

    public void defeatByBump() {
        defeatMode = "bump";
        defeatTimer = BUMP_DEFEAT_DURATION;
        setVelocityX(0.0);
        setVelocityY(-360.0);
        onGround = false;
    }

    public void defeatByFireball() {
        defeatMode = "fire";
        defeatTimer = FIRE_DEFEAT_DURATION;
        setVelocityX(0.0);
        setVelocityY(0.0);
        onGround = false;
    }

    public void tickFlattened(double deltaSeconds) {
        flattenedTimer -= deltaSeconds;
        if (flattenedTimer <= 0.0) {
            alive = false;
        }
    }

    public void updateDefeatAnimation(double deltaSeconds) {
        if (!isInDefeatAnimation()) {
            return;
        }
        if (isBumpDefeated()) {
            setVelocityY(Math.min(getVelocityY() + (GameConstants.GRAVITY * deltaSeconds), GameConstants.TERMINAL_VELOCITY));
            setY(getY() + (getVelocityY() * deltaSeconds));
        }
        defeatTimer -= deltaSeconds;
        if (defeatTimer <= 0.0) {
            alive = false;
            defeatMode = "";
            defeatTimer = 0.0;
        }
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public void updateParakoopa(double deltaSeconds) {
        if (!isParakoopa() || flattened || isInDefeatAnimation() || !alive || !activated) {
            return;
        }

        parakoopaJumpCooldown = Math.max(0.0, parakoopaJumpCooldown - deltaSeconds);
        if (onGround && parakoopaJumpCooldown <= 0.0) {
            setVelocityY(-360.0);
            onGround = false;
            parakoopaJumpCooldown = 1.05;
        }
    }
}
