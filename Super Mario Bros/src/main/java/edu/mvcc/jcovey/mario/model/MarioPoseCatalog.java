package edu.mvcc.jcovey.mario.model;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 * Provides pose-aware Mario collision metrics based on exported pose PNGs.
 */
final class MarioPoseCatalog {
    private static final double SMALL_SLOT_WIDTH = 32.0;
    private static final double SMALL_SLOT_HEIGHT = 32.0;
    private static final double TALL_SLOT_WIDTH = 32.0;
    private static final double TALL_SLOT_HEIGHT = 64.0;
    private static final double TALL_COLLISION_INSET_LEFT = 0.5;
    private static final double TALL_COLLISION_WIDTH = 31.0;

    private static final Map<String, PoseMetrics> POSE_METRICS = Map.of(
        "small_idle", loadSquarePoseMetrics(Path.of("assets", "game_art", "mario", "small", "small_idle.png")),
        "small_run1", loadSquarePoseMetrics(Path.of("assets", "game_art", "mario", "small", "small_run1.png")),
        "small_run2", loadSquarePoseMetrics(Path.of("assets", "game_art", "mario", "small", "small_run2.png")),
        "small_run3", loadSquarePoseMetrics(Path.of("assets", "game_art", "mario", "small", "small_run3.png")),
        "small_jump", loadSquarePoseMetrics(Path.of("assets", "game_art", "mario", "small", "small_jump.png")),
        "small_skid", loadSquarePoseMetrics(Path.of("assets", "game_art", "mario", "small", "small_skid.png")),
        "big_crouch", loadSquarePoseMetrics(Path.of("assets", "game_art", "mario", "big", "big_crouch.png")),
        "fire_crouch", loadSquarePoseMetrics(Path.of("assets", "game_art", "mario", "fire", "fire_crouch.png"))
    );

    private MarioPoseCatalog() {
    }

    static double getSlotWidth(MarioModel mario) {
        return mario.isSuperForm() ? TALL_SLOT_WIDTH : SMALL_SLOT_WIDTH;
    }

    static double getSlotHeight(MarioModel mario) {
        return mario.isSuperForm() ? TALL_SLOT_HEIGHT : SMALL_SLOT_HEIGHT;
    }

    static double getCollisionInsetLeft(MarioModel mario) {
        if (!usesSquareCollisionPose(mario)) {
            return TALL_COLLISION_INSET_LEFT;
        }
        PoseMetrics pose = getCurrentSquarePoseMetrics(mario);
        double collisionWidth = SMALL_SLOT_WIDTH * pose.widthFraction();
        return mario.isFacingRight() ? Math.max(0.0, SMALL_SLOT_WIDTH - collisionWidth) : 0.0;
    }

    static double getCollisionInsetTop(MarioModel mario) {
        if (!usesSquareCollisionPose(mario)) {
            return 0.0;
        }
        PoseMetrics pose = getCurrentSquarePoseMetrics(mario);
        double collisionHeight = SMALL_SLOT_HEIGHT * pose.heightFraction();
        return Math.max(0.0, SMALL_SLOT_HEIGHT - collisionHeight);
    }

    static double getCollisionWidth(MarioModel mario) {
        if (!usesSquareCollisionPose(mario)) {
            return TALL_COLLISION_WIDTH;
        }
        return SMALL_SLOT_WIDTH * getCurrentSquarePoseMetrics(mario).widthFraction();
    }

    static double getCollisionHeight(MarioModel mario) {
        if (!usesSquareCollisionPose(mario)) {
            return TALL_SLOT_HEIGHT;
        }
        return SMALL_SLOT_HEIGHT * getCurrentSquarePoseMetrics(mario).heightFraction();
    }

    private static boolean usesSquareCollisionPose(MarioModel mario) {
        return !mario.isSuperForm() || mario.isCrouching();
    }

    private static PoseMetrics getCurrentSquarePoseMetrics(MarioModel mario) {
        if (!mario.isSuperForm()) {
            return POSE_METRICS.get(getSmallPoseKey(mario));
        }
        return POSE_METRICS.get(mario.isFireForm() ? "fire_crouch" : "big_crouch");
    }

    private static String getSmallPoseKey(MarioModel mario) {
        if (!mario.isAlive() || !mario.isOnGround()) {
            return "small_jump";
        }
        if (mario.isSkidding()) {
            return "small_skid";
        }
        if (Math.abs(mario.getVelocityX()) < 10.0) {
            return "small_idle";
        }
        int frame = ((int) (mario.getAnimationTime() * 10.0)) % 3;
        if (frame == 0) {
            return "small_run1";
        }
        if (frame == 1) {
            return "small_run2";
        }
        return "small_run3";
    }

    private static PoseMetrics loadSquarePoseMetrics(Path path) {
        try {
            BufferedImage image = ImageIO.read(path.toFile());
            if (image == null) {
                throw new IllegalStateException("Failed to load Mario pose image: " + path);
            }
            double maxSide = Math.max(image.getWidth(), image.getHeight());
            return new PoseMetrics(image.getWidth() / maxSide, image.getHeight() / maxSide);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to load Mario pose image: " + path, exception);
        }
    }

    private record PoseMetrics(double widthFraction, double heightFraction) {
    }
}
