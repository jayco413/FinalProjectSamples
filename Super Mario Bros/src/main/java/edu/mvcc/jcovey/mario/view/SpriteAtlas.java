package edu.mvcc.jcovey.mario.view;

import edu.mvcc.jcovey.mario.model.EnemyModel;
import edu.mvcc.jcovey.mario.model.MarioModel;
import java.nio.file.Files;
import java.nio.file.Path;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class SpriteAtlas {
    private final Image background = loadImage("world-1-1-map.png");
    private final Image tilesSheet = loadImage("tiles-sprites.png");
    private final Image marioSheet = loadImage("mario-sprites.png");
    private final Image enemySheet = loadImage("enemy-sprites.png");
    private final Image mushroomSprite = loadImage("magic-mushroom.png");
    private final Image brickBlockSprite = loadImage("brick-block.png");
    private final Image questionBlockSprite = loadImage("question-block.gif");

    private final Image smallIdle = loadGameArtImage(Path.of("mario", "small", "small_idle.png"));
    private final Image smallRun1 = loadGameArtImage(Path.of("mario", "small", "small_run1.png"));
    private final Image smallRun2 = loadGameArtImage(Path.of("mario", "small", "small_run2.png"));
    private final Image smallRun3 = loadGameArtImage(Path.of("mario", "small", "small_run3.png"));
    private final Image smallJump = loadGameArtImage(Path.of("mario", "small", "small_jump.png"));
    private final Image smallSkid = loadGameArtImage(Path.of("mario", "small", "small_skid.png"));

    private final Image bigIdle = loadGameArtImage(Path.of("mario", "big", "big_idle.png"));
    private final Image bigRun1 = loadGameArtImage(Path.of("mario", "big", "big_run1.png"));
    private final Image bigRun2 = loadGameArtImage(Path.of("mario", "big", "big_run2.png"));
    private final Image bigRun3 = loadGameArtImage(Path.of("mario", "big", "big_run3.png"));
    private final Image bigJump = loadGameArtImage(Path.of("mario", "big", "big_jump.png"));
    private final Image bigCrouch = loadGameArtImage(Path.of("mario", "big", "big_crouch.png"));

    private final Image fireIdle = loadGameArtImage(Path.of("mario", "fire", "fire_idle.png"));
    private final Image fireRun1 = loadGameArtImage(Path.of("mario", "fire", "fire_run1.png"));
    private final Image fireRun2 = loadGameArtImage(Path.of("mario", "fire", "fire_run2.png"));
    private final Image fireRun3 = loadGameArtImage(Path.of("mario", "fire", "fire_run3.png"));
    private final Image fireJump = loadGameArtImage(Path.of("mario", "fire", "fire_jump.png"));
    private final Image fireCrouch = loadGameArtImage(Path.of("mario", "fire", "fire_crouch.png"));

    private final MarioPose smallIdlePose = createSquarePose(smallIdle);
    private final MarioPose smallRun1Pose = createSquarePose(smallRun1);
    private final MarioPose smallRun2Pose = createSquarePose(smallRun2);
    private final MarioPose smallRun3Pose = createSquarePose(smallRun3);
    private final MarioPose smallJumpPose = createSquarePose(smallJump);
    private final MarioPose smallSkidPose = createSquarePose(smallSkid);

    private final MarioPose bigIdlePose = createTallPose(bigIdle);
    private final MarioPose bigRun1Pose = createTallPose(bigRun1);
    private final MarioPose bigRun2Pose = createTallPose(bigRun2);
    private final MarioPose bigRun3Pose = createTallPose(bigRun3);
    private final MarioPose bigJumpPose = createTallPose(bigJump);
    private final MarioPose bigCrouchPose = createSquarePose(bigCrouch);

    private final MarioPose fireIdlePose = createTallPose(fireIdle);
    private final MarioPose fireRun1Pose = createTallPose(fireRun1);
    private final MarioPose fireRun2Pose = createTallPose(fireRun2);
    private final MarioPose fireRun3Pose = createTallPose(fireRun3);
    private final MarioPose fireJumpPose = createTallPose(fireJump);
    private final MarioPose fireCrouchPose = createSquarePose(fireCrouch);

    public Image getBackground() {
        return background;
    }

    public Image getMarioSheet() {
        return marioSheet;
    }

    public Image getTilesSheet() {
        return tilesSheet;
    }

    public Image getEnemySheet() {
        return enemySheet;
    }

    public Image getMushroomSprite() {
        return mushroomSprite;
    }

    public Image getBrickBlockSprite() {
        return brickBlockSprite;
    }

    public Image getQuestionBlockSprite() {
        return questionBlockSprite;
    }

    public boolean hasMarioSheet() {
        return marioSheet != null && !marioSheet.isError() && marioSheet.getWidth() > 0.0;
    }

    public boolean hasMarioPoseImages() {
        return hasSmallMarioPoseImages() || hasBigMarioPoseImages() || hasFireMarioPoseImages();
    }

    public boolean hasSmallMarioPoseImages() {
        return isUsable(smallIdle) && isUsable(smallRun1) && isUsable(smallRun2) && isUsable(smallRun3) && isUsable(smallJump) && isUsable(smallSkid);
    }

    public boolean hasBigMarioPoseImages() {
        return isUsable(bigIdle) && isUsable(bigRun1) && isUsable(bigRun2) && isUsable(bigRun3) && isUsable(bigJump) && isUsable(bigCrouch);
    }

    public boolean hasFireMarioPoseImages() {
        return isUsable(fireIdle) && isUsable(fireRun1) && isUsable(fireRun2) && isUsable(fireRun3) && isUsable(fireJump) && isUsable(fireCrouch);
    }

    public boolean hasEnemySheet() {
        return enemySheet != null && !enemySheet.isError() && enemySheet.getWidth() > 0.0;
    }

    public MarioPose getMarioPose(MarioModel mario) {
        if (!mario.isSuperForm() && hasSmallMarioPoseImages()) {
            return getSmallMarioPoseImage(mario);
        }
        if (mario.isFireForm() && hasFireMarioPoseImages()) {
            return getFireMarioPoseImage(mario);
        }
        if (mario.isSuperForm() && hasBigMarioPoseImages()) {
            return getBigMarioPoseImage(mario);
        }
        return null;
    }

    public Rectangle2D getEnemyViewport(EnemyModel enemy) {
        if ("koopa".equals(enemy.getType()) || "parakoopa".equals(enemy.getType())) {
            return new Rectangle2D(74.0, 16.0, 16.0, 16.0);
        }
        if (enemy.isFlattened()) {
            return new Rectangle2D(36.0, 24.0, 16.0, 8.0);
        }
        return new Rectangle2D(0.0, 16.0, 16.0, 16.0);
    }

    private MarioPose getSmallMarioPoseImage(MarioModel mario) {
        if (!mario.isAlive() || !mario.isOnGround()) {
            return smallJumpPose;
        }
        if (mario.isSkidding()) {
            return smallSkidPose;
        }
        if (Math.abs(mario.getVelocityX()) < 10.0) {
            return smallIdlePose;
        }
        return getRunFrame(smallRun1Pose, smallRun2Pose, smallRun3Pose, mario);
    }

    private MarioPose getBigMarioPoseImage(MarioModel mario) {
        if (!mario.isAlive() || !mario.isOnGround()) {
            return bigJumpPose;
        }
        if (mario.isCrouching()) {
            return bigCrouchPose;
        }
        if (mario.isSkidding() || Math.abs(mario.getVelocityX()) < 10.0) {
            return bigIdlePose;
        }
        return getRunFrame(bigRun1Pose, bigRun2Pose, bigRun3Pose, mario);
    }

    private MarioPose getFireMarioPoseImage(MarioModel mario) {
        if (!mario.isAlive() || !mario.isOnGround()) {
            return fireJumpPose;
        }
        if (mario.isCrouching()) {
            return fireCrouchPose;
        }
        if (mario.isSkidding() || Math.abs(mario.getVelocityX()) < 10.0) {
            return fireIdlePose;
        }
        return getRunFrame(fireRun1Pose, fireRun2Pose, fireRun3Pose, mario);
    }

    private MarioPose getRunFrame(MarioPose run1, MarioPose run2, MarioPose run3, MarioModel mario) {
        int frame = ((int) (mario.getAnimationTime() * 10.0)) % 3;
        if (frame == 0) {
            return run1;
        }
        if (frame == 1) {
            return run2;
        }
        return run3;
    }

    private Image loadImage(String fileName) {
        Path filePath = Path.of("assets", fileName);
        if (Files.exists(filePath)) {
            return new Image(filePath.toUri().toString());
        }

        if ("magic-mushroom.png".equals(fileName)) {
            filePath = Path.of("assets", "game_art", "items", "MU_mushroom_1x1.png");
        } else if ("brick-block.png".equals(fileName)) {
            filePath = Path.of("assets", "game_art", "tiles", "BR_brick_block_1x1.png");
        } else if ("question-block.gif".equals(fileName)) {
            filePath = Path.of("assets", "game_art", "tiles", "QB_question_block_1x1.png");
        }
        return new Image(filePath.toUri().toString());
    }

    private Image loadGameArtImage(Path relativePath) {
        Path filePath = Path.of("assets", "game_art").resolve(relativePath);
        return new Image(filePath.toUri().toString());
    }

    private boolean isUsable(Image image) {
        return image != null && !image.isError() && image.getWidth() > 0.0;
    }

    private MarioPose createSquarePose(Image sourceImage) {
        double maxSide = Math.max(sourceImage.getWidth(), sourceImage.getHeight());
        double widthFraction = sourceImage.getWidth() / maxSide;
        double heightFraction = sourceImage.getHeight() / maxSide;
        return new MarioPose(sourceImage, widthFraction, heightFraction, true);
    }

    private MarioPose createTallPose(Image sourceImage) {
        WritableImage normalized = resizeImage(sourceImage, 32, 64);
        return new MarioPose(normalized, 1.0, 1.0, false);
    }

    private WritableImage resizeImage(Image sourceImage, int targetWidth, int targetHeight) {
        WritableImage resized = new WritableImage(targetWidth, targetHeight);
        PixelReader reader = sourceImage.getPixelReader();
        PixelWriter writer = resized.getPixelWriter();
        for (int y = 0; y < targetHeight; y++) {
            for (int x = 0; x < targetWidth; x++) {
                double sourceX = ((x + 0.5) * sourceImage.getWidth() / targetWidth) - 0.5;
                double sourceY = ((y + 0.5) * sourceImage.getHeight() / targetHeight) - 0.5;
                writer.setColor(x, y, sampleBilinear(reader, sourceImage, sourceX, sourceY));
            }
        }
        return resized;
    }

    private javafx.scene.paint.Color sampleBilinear(PixelReader reader, Image sourceImage, double sourceX, double sourceY) {
        int x0 = clamp((int) Math.floor(sourceX), 0, (int) sourceImage.getWidth() - 1);
        int y0 = clamp((int) Math.floor(sourceY), 0, (int) sourceImage.getHeight() - 1);
        int x1 = clamp(x0 + 1, 0, (int) sourceImage.getWidth() - 1);
        int y1 = clamp(y0 + 1, 0, (int) sourceImage.getHeight() - 1);
        double tx = Math.max(0.0, Math.min(1.0, sourceX - Math.floor(sourceX)));
        double ty = Math.max(0.0, Math.min(1.0, sourceY - Math.floor(sourceY)));

        javafx.scene.paint.Color c00 = reader.getColor(x0, y0);
        javafx.scene.paint.Color c10 = reader.getColor(x1, y0);
        javafx.scene.paint.Color c01 = reader.getColor(x0, y1);
        javafx.scene.paint.Color c11 = reader.getColor(x1, y1);

        double redTop = lerp(c00.getRed(), c10.getRed(), tx);
        double greenTop = lerp(c00.getGreen(), c10.getGreen(), tx);
        double blueTop = lerp(c00.getBlue(), c10.getBlue(), tx);
        double alphaTop = lerp(c00.getOpacity(), c10.getOpacity(), tx);

        double redBottom = lerp(c01.getRed(), c11.getRed(), tx);
        double greenBottom = lerp(c01.getGreen(), c11.getGreen(), tx);
        double blueBottom = lerp(c01.getBlue(), c11.getBlue(), tx);
        double alphaBottom = lerp(c01.getOpacity(), c11.getOpacity(), tx);

        return new javafx.scene.paint.Color(
            lerp(redTop, redBottom, ty),
            lerp(greenTop, greenBottom, ty),
            lerp(blueTop, blueBottom, ty),
            lerp(alphaTop, alphaBottom, ty)
        );
    }

    private double lerp(double start, double end, double amount) {
        return start + ((end - start) * amount);
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public record MarioPose(
        Image image,
        double widthFraction,
        double heightFraction,
        boolean squarePose
    ) {
    }
}
