package edu.mvcc.jcovey.mario.model;

public class BlockModel {
    private final double x;
    private final double y;
    private final double width;
    private final double height;
    private final String type;
    private final boolean containsMushroom;
    private final boolean containsOneUp;
    private final boolean containsStar;
    private int remainingCoins;
    private boolean used;
    private boolean visible;
    private double bumpTimer;
    private double renderOffsetY;

    public BlockModel(
        double x,
        double y,
        String type,
        int remainingCoins,
        boolean containsMushroom,
        boolean containsOneUp,
        boolean containsStar,
        boolean visible
    ) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.remainingCoins = remainingCoins;
        this.containsMushroom = containsMushroom;
        this.containsOneUp = containsOneUp;
        this.containsStar = containsStar;
        this.visible = visible;
        width = 32.0;
        height = 32.0;
        used = false;
        bumpTimer = 0.0;
        renderOffsetY = 0.0;
    }

    public PhysicsRect getBounds() {
        return new PhysicsRect(x, y, width, height);
    }

    public boolean canCollide() {
        return visible;
    }

    public boolean shouldSpawnMushroom() {
        return containsMushroom && !used;
    }

    public boolean shouldSpawnOneUp() {
        return containsOneUp && !used;
    }

    public boolean shouldSpawnStar() {
        return containsStar && !used;
    }

    public boolean shouldAwardCoin() {
        return remainingCoins > 0 && !used;
    }

    public boolean isRevealableHiddenBlock() {
        return !visible && ("hidden".equals(type) || "hidden1up".equals(type));
    }

    public void registerHit() {
        if (remainingCoins > 0) {
            remainingCoins--;
        }
        if ("question".equals(type) || "hidden".equals(type) || "hidden1up".equals(type) || "starbrick".equals(type) || "oneupbrick".equals(type)) {
            used = true;
        }
        if (remainingCoins == 0 && (
            "question".equals(type)
                || "hidden".equals(type)
                || "hidden1up".equals(type)
                || "coinbrick".equals(type)
                || "starbrick".equals(type)
                || "oneupbrick".equals(type)
        )) {
            used = true;
        }
        if ("hidden".equals(type) || "hidden1up".equals(type)) {
            visible = true;
        }
    }

    public void breakBlock() {
        used = true;
        visible = false;
        remainingCoins = 0;
    }

    public void startBump() {
        bumpTimer = 0.18;
    }

    public void update(double deltaSeconds) {
        if (bumpTimer <= 0.0) {
            renderOffsetY = 0.0;
            return;
        }

        bumpTimer = Math.max(0.0, bumpTimer - deltaSeconds);
        double normalized = bumpTimer / 0.18;
        if (normalized > 0.5) {
            renderOffsetY = -8.0 * (1.0 - ((normalized - 0.5) / 0.5));
        } else {
            renderOffsetY = -8.0 * (normalized / 0.5);
        }
    }

    public boolean isUsed() {
        return used;
    }

    public boolean isVisible() {
        return visible;
    }

    public String getType() {
        return type;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getRenderOffsetY() {
        return renderOffsetY;
    }
}
