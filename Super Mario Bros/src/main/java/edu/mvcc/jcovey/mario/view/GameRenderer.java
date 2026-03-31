package edu.mvcc.jcovey.mario.view;

import edu.mvcc.jcovey.mario.model.BlockModel;
import edu.mvcc.jcovey.mario.model.BrickFragmentModel;
import edu.mvcc.jcovey.mario.model.CoinModel;
import edu.mvcc.jcovey.mario.model.EnemyModel;
import edu.mvcc.jcovey.mario.model.FloatingCoinModel;
import edu.mvcc.jcovey.mario.model.FireballModel;
import edu.mvcc.jcovey.mario.model.FireworkModel;
import edu.mvcc.jcovey.mario.model.FlagPoleModel;
import edu.mvcc.jcovey.mario.model.FortressModel;
import edu.mvcc.jcovey.mario.model.GameConstants;
import edu.mvcc.jcovey.mario.model.GameModel;
import edu.mvcc.jcovey.mario.model.MarioModel;
import edu.mvcc.jcovey.mario.model.MushroomModel;
import edu.mvcc.jcovey.mario.model.PlatformModel;
import edu.mvcc.jcovey.mario.model.StarModel;
import edu.mvcc.jcovey.mario.model.TextLevelData;
import edu.mvcc.jcovey.mario.view.SpriteAtlas.MarioPose;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;

public class GameRenderer {
    private final SpriteAtlas atlas = new SpriteAtlas();
    private final GoodAssetCatalog goodAssets = new GoodAssetCatalog();
    private Image repeatingBackground;
    private Image undergroundLevelBackground;
    private Image undergroundBackground;
    private Image rotatedHorizontalPipe;
    private Font hudFont;
    private String hudFontFamily;
    private final Map<Image, Image> invertedImageCache = new HashMap<>();

    public void initialize() {
        goodAssets.load(Path.of("assets", "game_art"));
        repeatingBackground = new Image(
            Path.of("assets", "game_art", "scenery", "light_daytime_background.png").toUri().toString()
        );
        undergroundLevelBackground = new Image(
            Path.of("assets", "game_art", "scenery", "underground_level_background.png").toUri().toString()
        );
        undergroundBackground = new Image(
            Path.of("assets", "game_art", "scenery", "underground_secret_area.png").toUri().toString()
        );
        GoodAsset pipeAsset = goodAssets.get("TP");
        if (pipeAsset != null) {
            rotatedHorizontalPipe = createRotatedHorizontalPipe(pipeAsset.getImage());
        }
        hudFont = loadHudFont();
        hudFontFamily = hudFont != null ? hudFont.getFamily() : "";
    }

    public void render(Canvas canvas, GameModel gameModel) {
        GraphicsContext graphics = canvas.getGraphicsContext2D();
        graphics.setImageSmoothing(false);
        graphics.setFill(Color.web("#5c94fc"));
        graphics.fillRect(0.0, 0.0, canvas.getWidth(), canvas.getHeight());

        double scaleX = canvas.getWidth() / GameConstants.VIEWPORT_WIDTH;
        double scaleY = canvas.getHeight() / GameConstants.VIEWPORT_HEIGHT;
        double cameraX = gameModel.getCamera().getX();

        graphics.save();
        graphics.scale(scaleX, scaleY);
        drawRepeatingBackground(graphics, cameraX, gameModel.getLevel().getAreaId());
        drawPipes(graphics, gameModel.getLevel().getStructure(), cameraX);
        drawStructuralLayer(graphics, gameModel.getLevel().getStructure(), cameraX);
        drawBlocks(graphics, gameModel, cameraX);
        drawPlatforms(graphics, gameModel, cameraX);
        drawCoins(graphics, gameModel, cameraX);
        drawFloatingCoins(graphics, gameModel, cameraX);
        drawBrickFragments(graphics, gameModel, cameraX);
        drawMushrooms(graphics, gameModel, cameraX);
        drawStars(graphics, gameModel, cameraX);
        drawFireballs(graphics, gameModel, cameraX);
        drawEnemies(graphics, gameModel, cameraX);
        drawFlagPole(graphics, gameModel, cameraX);
        drawFortress(graphics, gameModel, cameraX);
        drawFireworks(graphics, gameModel, cameraX);
        drawMario(graphics, gameModel.getMario(), cameraX);
        drawHud(graphics, gameModel);
        graphics.restore();
    }

    private void drawRepeatingBackground(GraphicsContext graphics, double cameraX, String areaId) {
        Image background = repeatingBackground;
        if ("underground".equals(areaId)) {
            background = undergroundLevelBackground;
        } else if ("bonus".equals(areaId)) {
            background = undergroundBackground;
        }
        if (background == null || background.isError()) {
            return;
        }

        double playfieldY = GameConstants.HUD_ROWS * GameConstants.TILE_SIZE;
        double playfieldHeight = GameConstants.VIEWPORT_HEIGHT - playfieldY;
        double tileWidth = background.getWidth() * (playfieldHeight / background.getHeight());
        double startX = -(cameraX % tileWidth);

        for (double drawX = startX - tileWidth; drawX < GameConstants.VIEWPORT_WIDTH + tileWidth; drawX += tileWidth) {
            graphics.drawImage(
                background,
                drawX,
                playfieldY,
                tileWidth,
                playfieldHeight
            );
        }
    }

    private void drawStructuralLayer(GraphicsContext graphics, TextLevelData structure, double cameraX) {
        GoodAsset floor = goodAssets.get("FB");
        for (int row = 0; row < structure.getRowCount(); row++) {
            for (int column = 0; column < structure.getColumnCount(); column++) {
                char cell = structure.getCell(row, column);
                double drawX = (column * GameConstants.TILE_SIZE) - cameraX;
                double drawY = row * GameConstants.TILE_SIZE;
                String code = getStructuralAssetCode(cell);
                if (code.isEmpty()) {
                    continue;
                }

                GoodAsset asset = goodAssets.get(code);
                if (asset != null) {
                    graphics.drawImage(asset.getImage(), drawX, drawY, GameConstants.TILE_SIZE, GameConstants.TILE_SIZE);
                    if (floor != null && row == structure.getRowCount() - 1 && cell == '#') {
                        graphics.drawImage(
                            floor.getImage(),
                            drawX,
                            drawY + GameConstants.TILE_SIZE,
                            GameConstants.TILE_SIZE,
                            GameConstants.TILE_SIZE
                        );
                    }
                }
            }
        }
    }

    private void drawBlocks(GraphicsContext graphics, GameModel gameModel, double cameraX) {
        GoodAsset brick = goodAssets.get("BR");
        GoodAsset question = goodAssets.get("QB");
        GoodAsset used = goodAssets.get("UB");

        for (BlockModel block : gameModel.getLevel().getBlocks()) {
            if (!block.isVisible()) {
                continue;
            }

            double drawX = block.getX() - cameraX;
            double drawY = block.getY() + block.getRenderOffsetY();
            if (("brick".equals(block.getType()) || "oneupbrick".equals(block.getType())) && brick != null) {
                graphics.drawImage(brick.getImage(), drawX, drawY, GameConstants.TILE_SIZE, GameConstants.TILE_SIZE);
            } else if (("coinbrick".equals(block.getType()) || "starbrick".equals(block.getType())) && !block.isUsed() && brick != null) {
                graphics.drawImage(brick.getImage(), drawX, drawY, GameConstants.TILE_SIZE, GameConstants.TILE_SIZE);
            } else if ("question".equals(block.getType()) && !block.isUsed() && question != null) {
                graphics.drawImage(question.getImage(), drawX, drawY, GameConstants.TILE_SIZE, GameConstants.TILE_SIZE);
            } else if (used != null) {
                graphics.drawImage(used.getImage(), drawX, drawY, GameConstants.TILE_SIZE, GameConstants.TILE_SIZE);
            }
        }
    }

    private void drawStars(GraphicsContext graphics, GameModel gameModel, double cameraX) {
        GoodAsset starAsset = goodAssets.get("ST");
        for (StarModel star : gameModel.getStars()) {
            if (!star.isActive()) {
                continue;
            }
            if (starAsset != null) {
                graphics.drawImage(
                    starAsset.getImage(),
                    star.getX() - cameraX,
                    star.getY(),
                    star.getWidth(),
                    star.getHeight()
                );
                continue;
            }
            drawPlaceholderStar(graphics, star, cameraX);
        }
    }

    private void drawPlaceholderStar(GraphicsContext graphics, StarModel star, double cameraX) {
        double centerX = (star.getX() - cameraX) + (star.getWidth() / 2.0);
        double centerY = star.getY() + (star.getHeight() / 2.0);
        double outer = star.getWidth() * 0.48;
        double inner = outer * 0.45;
        double[] xPoints = new double[10];
        double[] yPoints = new double[10];
        for (int index = 0; index < 10; index++) {
            double angle = (-90.0 + (index * 36.0)) * (Math.PI / 180.0);
            double radius = (index % 2 == 0) ? outer : inner;
            xPoints[index] = centerX + (Math.cos(angle) * radius);
            yPoints[index] = centerY + (Math.sin(angle) * radius);
        }
        graphics.setFill(Color.web("#ffd83d"));
        graphics.fillPolygon(xPoints, yPoints, xPoints.length);
        graphics.setStroke(Color.web("#fff7b8"));
        graphics.setLineWidth(1.5);
        graphics.strokePolygon(xPoints, yPoints, xPoints.length);
    }

    private void drawMushrooms(GraphicsContext graphics, GameModel gameModel, double cameraX) {
        GoodAsset mushroomAsset = goodAssets.get("MU");
        GoodAsset oneUpAsset = goodAssets.get("OU");
        GoodAsset fireFlowerAsset = goodAssets.get("FF");
        for (MushroomModel mushroom : gameModel.getMushrooms()) {
            if (!mushroom.isActive()) {
                continue;
            }
            GoodAsset assetToUse;
            if ("oneup".equals(mushroom.getType())) {
                assetToUse = oneUpAsset;
            } else if ("fireflower".equals(mushroom.getType())) {
                assetToUse = fireFlowerAsset;
            } else {
                assetToUse = mushroomAsset;
            }
            if (assetToUse != null) {
                graphics.drawImage(
                    assetToUse.getImage(),
                    mushroom.getX() - cameraX,
                    mushroom.getY(),
                    mushroom.getWidth(),
                    mushroom.getHeight()
                );
            } else {
                graphics.drawImage(
                    atlas.getMushroomSprite(),
                    mushroom.getX() - cameraX,
                    mushroom.getY(),
                    mushroom.getWidth(),
                    mushroom.getHeight()
                );
            }
        }
    }

    private void drawPlatforms(GraphicsContext graphics, GameModel gameModel, double cameraX) {
        GoodAsset platformAsset = goodAssets.get("PL");
        for (PlatformModel platform : gameModel.getLevel().getPlatforms()) {
            double drawX = platform.getX() - cameraX;
            if (platformAsset != null) {
                graphics.drawImage(platformAsset.getImage(), drawX, platform.getY(), platform.getWidth(), platform.getHeight());
            } else {
                graphics.setFill(Color.web("#c47a2c"));
                graphics.fillRect(drawX, platform.getY(), platform.getWidth(), platform.getHeight());
                graphics.setFill(Color.web("#8a4f16"));
                graphics.fillRect(drawX, platform.getY() + platform.getHeight() - 4.0, platform.getWidth(), 4.0);
            }
        }
    }

    private void drawCoins(GraphicsContext graphics, GameModel gameModel, double cameraX) {
        GoodAsset coinAsset = goodAssets.get("CO");
        if (coinAsset == null) {
            return;
        }

        for (CoinModel coin : gameModel.getLevel().getCoins()) {
            if (coin.isCollected()) {
                continue;
            }
            graphics.drawImage(
                coinAsset.getImage(),
                coin.getX() - cameraX,
                coin.getY(),
                coin.getWidth(),
                coin.getHeight()
            );
        }
    }

    private void drawFloatingCoins(GraphicsContext graphics, GameModel gameModel, double cameraX) {
        GoodAsset coinAsset = goodAssets.get("CO");
        for (FloatingCoinModel coin : gameModel.getFloatingCoins()) {
            if (coin.isSparkling()) {
                drawCoinSparkle(graphics, coin, cameraX);
                continue;
            }
            if (coinAsset == null) {
                continue;
            }
            graphics.drawImage(
                coinAsset.getImage(),
                coin.getX() - cameraX,
                coin.getY(),
                GameConstants.TILE_SIZE,
                GameConstants.TILE_SIZE
            );
        }
    }

    private void drawCoinSparkle(GraphicsContext graphics, FloatingCoinModel coin, double cameraX) {
        double centerX = (coin.getX() - cameraX) + (GameConstants.TILE_SIZE / 2.0);
        double centerY = coin.getY() + (GameConstants.TILE_SIZE / 2.0);
        double radius = 10.0 + (coin.getSparkleTimer() * 28.0);
        graphics.setStroke(Color.web("#fff2a8"));
        graphics.setLineWidth(3.0);
        graphics.strokeLine(centerX - radius, centerY, centerX + radius, centerY);
        graphics.strokeLine(centerX, centerY - radius, centerX, centerY + radius);
        graphics.setStroke(Color.web("#fffdf0"));
        graphics.setLineWidth(2.0);
        graphics.strokeLine(centerX - (radius * 0.7), centerY - (radius * 0.7), centerX + (radius * 0.7), centerY + (radius * 0.7));
        graphics.strokeLine(centerX - (radius * 0.7), centerY + (radius * 0.7), centerX + (radius * 0.7), centerY - (radius * 0.7));
    }

    private void drawBrickFragments(GraphicsContext graphics, GameModel gameModel, double cameraX) {
        GoodAsset brickAsset = goodAssets.get("BR");
        if (brickAsset == null) {
            return;
        }

        double sourceHalfWidth = brickAsset.getImage().getWidth() / 2.0;
        double sourceHalfHeight = brickAsset.getImage().getHeight() / 2.0;
        for (BrickFragmentModel fragment : gameModel.getBrickFragments()) {
            graphics.drawImage(
                brickAsset.getImage(),
                fragment.getSourceColumn() * sourceHalfWidth,
                fragment.getSourceRow() * sourceHalfHeight,
                sourceHalfWidth,
                sourceHalfHeight,
                fragment.getX() - cameraX,
                fragment.getY(),
                GameConstants.TILE_SIZE / 2.0,
                GameConstants.TILE_SIZE / 2.0
            );
        }
    }

    private void drawFireballs(GraphicsContext graphics, GameModel gameModel, double cameraX) {
        GoodAsset fireballAsset = goodAssets.get("FI");
        for (FireballModel fireball : gameModel.getFireballs()) {
            if (!fireball.isActive()) {
                continue;
            }
            if (fireballAsset != null) {
                graphics.drawImage(
                    fireballAsset.getImage(),
                    fireball.getX() - cameraX,
                    fireball.getY(),
                    fireball.getWidth(),
                    fireball.getHeight()
                );
            } else {
                graphics.setFill(Color.ORANGE);
                graphics.fillOval(fireball.getX() - cameraX, fireball.getY(), fireball.getWidth(), fireball.getHeight());
            }
        }
    }

    private void drawEnemies(GraphicsContext graphics, GameModel gameModel, double cameraX) {
        for (EnemyModel enemy : gameModel.getLevel().getEnemies()) {
            if (!enemy.isAlive()) {
                continue;
            }
            if (enemy.isBumpDefeated()) {
                drawBumpDefeatedEnemy(graphics, enemy, cameraX);
                continue;
            }
            if (enemy.isFireDefeated()) {
                drawFireDefeatedEnemy(graphics, enemy, cameraX);
                continue;
            }
            if (!atlas.hasEnemySheet()) {
                drawEnemyPlaceholder(graphics, enemy, cameraX);
                continue;
            }
            Rectangle2D viewport = atlas.getEnemyViewport(enemy);
            graphics.drawImage(
                atlas.getEnemySheet(),
                viewport.getMinX(),
                viewport.getMinY(),
                viewport.getWidth(),
                viewport.getHeight(),
                enemy.getX() - cameraX,
                enemy.getY(),
                enemy.getWidth(),
                enemy.getHeight()
            );
        }
    }

    private void drawBumpDefeatedEnemy(GraphicsContext graphics, EnemyModel enemy, double cameraX) {
        double progress = enemy.getDefeatProgress();
        double alpha = progress < 0.7 ? 1.0 : Math.max(0.0, 1.0 - ((progress - 0.7) / 0.3));
        graphics.setGlobalAlpha(alpha);
        if (atlas.hasEnemySheet()) {
            Rectangle2D viewport = atlas.getEnemyViewport(enemy);
            graphics.drawImage(
                atlas.getEnemySheet(),
                viewport.getMinX(),
                viewport.getMinY(),
                viewport.getWidth(),
                viewport.getHeight(),
                enemy.getX() - cameraX,
                enemy.getY(),
                enemy.getWidth(),
                enemy.getHeight()
            );
        } else {
            drawEnemyPlaceholder(graphics, enemy, cameraX);
        }
        graphics.setGlobalAlpha(1.0);
        if (progress >= 0.65) {
            drawTwinkle(graphics, (enemy.getX() - cameraX) + (enemy.getWidth() / 2.0), enemy.getY() + (enemy.getHeight() / 2.0), 8.0 + (progress * 12.0));
        }
    }

    private void drawFireDefeatedEnemy(GraphicsContext graphics, EnemyModel enemy, double cameraX) {
        double progress = enemy.getDefeatProgress();
        double bodyAlpha = Math.max(0.0, 1.0 - progress);
        graphics.setGlobalAlpha(bodyAlpha);
        if (atlas.hasEnemySheet()) {
            Rectangle2D viewport = atlas.getEnemyViewport(enemy);
            graphics.drawImage(
                atlas.getEnemySheet(),
                viewport.getMinX(),
                viewport.getMinY(),
                viewport.getWidth(),
                viewport.getHeight(),
                enemy.getX() - cameraX,
                enemy.getY(),
                enemy.getWidth(),
                enemy.getHeight()
            );
        } else {
            drawEnemyPlaceholder(graphics, enemy, cameraX);
        }
        graphics.setGlobalAlpha(1.0);

        double flameX = enemy.getX() - cameraX;
        double flameY = enemy.getY() - 4.0;
        graphics.setFill(Color.web("#ffcc45", 0.85));
        graphics.fillOval(flameX + 6.0, flameY + 4.0, enemy.getWidth() - 12.0, enemy.getHeight() - 6.0);
        graphics.setFill(Color.web("#ff7a21", 0.8));
        graphics.fillOval(flameX + 2.0, flameY + 8.0, enemy.getWidth() - 4.0, enemy.getHeight() - 2.0);
        graphics.setFill(Color.web("#fff4c9", 0.8));
        graphics.fillOval(flameX + 10.0, flameY + 10.0, enemy.getWidth() - 20.0, enemy.getHeight() - 12.0);
    }

    private void drawTwinkle(GraphicsContext graphics, double centerX, double centerY, double radius) {
        graphics.setStroke(Color.web("#fff6b8"));
        graphics.setLineWidth(2.5);
        graphics.strokeLine(centerX - radius, centerY, centerX + radius, centerY);
        graphics.strokeLine(centerX, centerY - radius, centerX, centerY + radius);
        graphics.setStroke(Color.web("#ffffff"));
        graphics.setLineWidth(1.5);
        graphics.strokeLine(centerX - (radius * 0.7), centerY - (radius * 0.7), centerX + (radius * 0.7), centerY + (radius * 0.7));
        graphics.strokeLine(centerX - (radius * 0.7), centerY + (radius * 0.7), centerX + (radius * 0.7), centerY - (radius * 0.7));
    }

    private void drawMario(GraphicsContext graphics, MarioModel mario, double cameraX) {
        if (mario.isHidden()) {
            return;
        }
        if (mario.isInvincible()) {
            graphics.setGlobalAlpha(0.65);
        }
        MarioPose marioPose = atlas.getMarioPose(mario);
        if (marioPose != null) {
            drawMarioPoseImage(graphics, marioPose, mario, cameraX);
            graphics.setGlobalAlpha(1.0);
            return;
        }
        if (!atlas.hasMarioSheet()) {
            drawMarioPlaceholder(graphics, mario, cameraX);
            graphics.setGlobalAlpha(1.0);
            return;
        }
        drawMarioSprite(
            graphics,
            atlas.getMarioSheet(),
            mario,
            cameraX,
            32.0,
            mario.isSuperForm() ? 64.0 : mario.getHeight()
        );
        graphics.setGlobalAlpha(1.0);
    }

    private void drawMarioPoseImage(GraphicsContext graphics, MarioPose pose, MarioModel mario, double cameraX) {
        Image poseImage = getStarManPoseImage(pose.image(), mario);
        boolean squarePose = pose.squarePose();
        double slotWidth = 32.0;
        double slotHeight = squarePose ? 32.0 : 64.0;
        double drawWidth = slotWidth * pose.widthFraction();
        double drawHeight = slotHeight * pose.heightFraction();
        double slotX = mario.getX() - cameraX;
        double slotY = mario.getFeetY() - slotHeight;
        double targetX = squarePose
            ? slotX + (mario.isFacingRight() ? Math.max(0.0, slotWidth - drawWidth) : 0.0)
            : slotX;
        double targetY = squarePose ? slotY + Math.max(0.0, slotHeight - drawHeight) : slotY;
        graphics.setImageSmoothing(true);
        if (mario.isFacingRight()) {
            graphics.drawImage(poseImage, targetX, targetY, drawWidth, drawHeight);
            graphics.setImageSmoothing(false);
            return;
        }

        graphics.save();
        graphics.translate(targetX + drawWidth, targetY);
        graphics.scale(-1.0, 1.0);
        graphics.drawImage(poseImage, 0.0, 0.0, drawWidth, drawHeight);
        graphics.restore();
        graphics.setImageSmoothing(false);
    }

    private void drawMarioSprite(
        GraphicsContext graphics,
        Image spriteSheet,
        MarioModel mario,
        double cameraX,
        double drawWidth,
        double drawHeight
    ) {
        spriteSheet = getStarManPoseImage(spriteSheet, mario);
        Rectangle2D viewport;
        if (!mario.isAlive()) {
            viewport = new Rectangle2D(0.0, 8.0, 16.0, 16.0);
        } else if (!mario.isOnGround()) {
            viewport = new Rectangle2D(56.0, 8.0, 16.0, 16.0);
        } else if (Math.abs(mario.getVelocityX()) < 10.0) {
            viewport = new Rectangle2D(0.0, 8.0, 16.0, 16.0);
        } else {
            int frame = ((int) (mario.getAnimationTime() * 10.0)) % 3;
            if (frame == 0) {
                viewport = new Rectangle2D(20.0, 8.0, 16.0, 16.0);
            } else if (frame == 1) {
                viewport = new Rectangle2D(38.0, 8.0, 16.0, 16.0);
            } else {
                viewport = new Rectangle2D(0.0, 8.0, 16.0, 16.0);
            }
        }
        double targetX = mario.getX() - cameraX;
        double targetY = mario.getFeetY() - drawHeight;
        if (mario.isFacingRight()) {
            graphics.drawImage(
                spriteSheet,
                viewport.getMinX(),
                viewport.getMinY(),
                viewport.getWidth(),
                viewport.getHeight(),
                targetX,
                targetY,
                drawWidth,
                drawHeight
            );
            return;
        }

        graphics.save();
        graphics.translate(targetX + drawWidth, targetY);
        graphics.scale(-1.0, 1.0);
        graphics.drawImage(
            spriteSheet,
            viewport.getMinX(),
            viewport.getMinY(),
            viewport.getWidth(),
            viewport.getHeight(),
            0.0,
            0.0,
            drawWidth,
            drawHeight
        );
        graphics.restore();
    }

    private Image getStarManPoseImage(Image baseImage, MarioModel mario) {
        if (!mario.hasStarPower()) {
            return baseImage;
        }
        double timer = mario.getStarPowerTimer();
        double normalized = Math.max(0.0, Math.min(1.0, timer / 10.0));
        double flashPeriod = 0.08 + ((1.0 - normalized) * 0.28);
        long flashBucket = (long) Math.floor(mario.getAnimationTime() / flashPeriod);
        if ((flashBucket % 2L) == 0L) {
            return baseImage;
        }
        return invertedImageCache.computeIfAbsent(baseImage, this::createInvertedImage);
    }

    private Image createInvertedImage(Image source) {
        int width = (int) Math.round(source.getWidth());
        int height = (int) Math.round(source.getHeight());
        WritableImage inverted = new WritableImage(width, height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = source.getPixelReader().getColor(x, y);
                inverted.getPixelWriter().setColor(
                    x,
                    y,
                    new Color(1.0 - color.getRed(), 1.0 - color.getGreen(), 1.0 - color.getBlue(), color.getOpacity())
                );
            }
        }
        return inverted;
    }

    private void drawEnemyPlaceholder(GraphicsContext graphics, EnemyModel enemy, double cameraX) {
        Color fill = "parakoopa".equals(enemy.getType()) || "koopa".equals(enemy.getType())
            ? Color.web("#6cc04a")
            : Color.web("#8b4a20");
        graphics.setFill(fill);
        graphics.fillRoundRect(enemy.getX() - cameraX, enemy.getY(), enemy.getWidth(), enemy.getHeight(), 8.0, 8.0);
        graphics.setFill(Color.BLACK);
        graphics.fillOval((enemy.getX() - cameraX) + 4.0, enemy.getY() + 5.0, 4.0, 4.0);
        graphics.fillOval((enemy.getX() - cameraX) + enemy.getWidth() - 8.0, enemy.getY() + 5.0, 4.0, 4.0);
    }

    private void drawMarioPlaceholder(GraphicsContext graphics, MarioModel mario, double cameraX) {
        graphics.setFill(mario.isFireForm() ? Color.web("#f6f3d4") : Color.web("#d33f2f"));
        graphics.fillRoundRect(mario.getX() - cameraX, mario.getY(), 32.0, mario.getHeight(), 8.0, 8.0);
        graphics.setFill(Color.web("#234c9f"));
        graphics.fillRect((mario.getX() - cameraX) + 4.0, mario.getY() + (mario.getHeight() * 0.45), 20.0, mario.getHeight() * 0.5);
        graphics.setFill(Color.web("#f0c39b"));
        graphics.fillOval((mario.getX() - cameraX) + 6.0, mario.getY() + 5.0, 16.0, 12.0);
    }

    private void drawHud(GraphicsContext graphics, GameModel gameModel) {
        graphics.setFill(Color.color(0.0, 0.0, 0.0, 0.25));
        graphics.fillRect(0.0, 0.0, GameConstants.VIEWPORT_WIDTH, GameConstants.HUD_ROWS * GameConstants.TILE_SIZE);

        graphics.setFill(Color.WHITE);
        graphics.setFontSmoothingType(FontSmoothingType.GRAY);
        graphics.setFont(!hudFontFamily.isBlank() ? Font.font(hudFontFamily, 16.0) : Font.font(16.0));

        double topY = 22.0;
        double bottomY = 46.0;
        graphics.fillText(toHudText("MARIO"), 16.0, topY);
        graphics.fillText(toHudText("WORLD"), 214.0, topY);
        graphics.fillText(toHudText("TIME"), 368.0, topY);

        graphics.fillText(gameModel.getScoreText(), 16.0, bottomY);
        drawHudCoinCounter(graphics, gameModel, 124.0, bottomY);
        graphics.fillText(toHudText(gameModel.getWorldText()), 232.0, bottomY);
        graphics.fillText(gameModel.getTimeText(), 384.0, bottomY);
    }

    private String toHudText(String text) {
        return text.toUpperCase(Locale.ROOT);
    }

    private void drawHudCoinCounter(GraphicsContext graphics, GameModel gameModel, double x, double baselineY) {
        GoodAsset coinAsset = goodAssets.get("CO");
        double iconSize = 16.0;
        double iconY = baselineY - iconSize + 1.0;
        if (coinAsset != null) {
            graphics.drawImage(coinAsset.getImage(), x, iconY, iconSize, iconSize);
        }
        graphics.fillText("x" + gameModel.getCoinText(), x + 18.0, baselineY);
    }

    private Font loadHudFont() {
        Path fontPath = Path.of("assets", "fonts", "hud-font.otf");
        try (InputStream stream = Files.newInputStream(fontPath)) {
            return Font.loadFont(stream, 18.0);
        } catch (IOException exception) {
            return null;
        }
    }

    private String getStructuralAssetCode(char cell) {
        if (cell == '#') {
            return "FB";
        }
        if (cell == 'T') {
            return "MT";
        }
        if (cell == 'S') {
            return "MS";
        }
        return "";
    }

    private void drawFlagPole(GraphicsContext graphics, GameModel gameModel, double cameraX) {
        FlagPoleModel flagPole = gameModel.getLevel().getFlagPole();
        if (flagPole == null) {
            return;
        }

        GoodAsset poleBody = goodAssets.get("LP");
        GoodAsset poleCap = goodAssets.get("LC");
        GoodAsset flag = goodAssets.get("FG");
        double drawX = flagPole.getX() - cameraX;

        if (poleCap != null) {
            graphics.drawImage(poleCap.getImage(), drawX, flagPole.getTopY(), GameConstants.TILE_SIZE, GameConstants.TILE_SIZE);
        } else {
            graphics.setFill(Color.web("#d8ffd0"));
            graphics.fillOval(drawX + 8.0, flagPole.getTopY() + 6.0, 16.0, 16.0);
        }

        for (double y = flagPole.getTopY() + GameConstants.TILE_SIZE; y < flagPole.getBottomY(); y += GameConstants.TILE_SIZE) {
            if (poleBody != null) {
                graphics.drawImage(poleBody.getImage(), drawX, y, GameConstants.TILE_SIZE, GameConstants.TILE_SIZE);
            } else {
                graphics.setFill(Color.web("#78d050"));
                graphics.fillRect(drawX + 13.0, y, 6.0, GameConstants.TILE_SIZE);
            }
        }

        double flagX = drawX - GameConstants.TILE_SIZE;
        if (flag != null) {
            graphics.drawImage(flag.getImage(), flagX, flagPole.getFlagY(), GameConstants.TILE_SIZE * 2.0, GameConstants.TILE_SIZE);
        } else {
            graphics.setFill(Color.WHITE);
            graphics.fillPolygon(
                new double[] { flagX + 28.0, flagX + 6.0, flagX + 28.0 },
                new double[] { flagPole.getFlagY() + 6.0, flagPole.getFlagY() + 16.0, flagPole.getFlagY() + 26.0 },
                3
            );
        }
    }

    private void drawFortress(GraphicsContext graphics, GameModel gameModel, double cameraX) {
        FortressModel fortress = gameModel.getLevel().getFortress();
        if (fortress == null) {
            return;
        }

        GoodAsset fortressAsset = goodAssets.get("FO");
        double drawX = fortress.getX() - cameraX;
        if (fortressAsset != null) {
            graphics.drawImage(fortressAsset.getImage(), drawX, fortress.getY(), fortress.getWidth(), fortress.getHeight());
            return;
        }

        graphics.setFill(Color.web("#6b3f1f"));
        graphics.fillRect(drawX, fortress.getY(), fortress.getWidth(), fortress.getHeight());
    }

    private void drawFireworks(GraphicsContext graphics, GameModel gameModel, double cameraX) {
        for (FireworkModel firework : gameModel.getFireworks()) {
            double progress = Math.min(1.0, firework.getAgeSeconds() / 0.8);
            double centerX = firework.getX() - cameraX;
            double centerY = firework.getY();
            double radius = 8.0 + (progress * 26.0);
            graphics.setGlobalAlpha(1.0 - progress);
            graphics.setStroke(Color.web(progress < 0.5 ? "#fff685" : "#ffd0f2"));
            graphics.setLineWidth(2.5);
            graphics.strokeLine(centerX - radius, centerY, centerX + radius, centerY);
            graphics.strokeLine(centerX, centerY - radius, centerX, centerY + radius);
            graphics.strokeLine(centerX - (radius * 0.75), centerY - (radius * 0.75), centerX + (radius * 0.75), centerY + (radius * 0.75));
            graphics.strokeLine(centerX - (radius * 0.75), centerY + (radius * 0.75), centerX + (radius * 0.75), centerY - (radius * 0.75));
            graphics.setGlobalAlpha(1.0);
        }
    }

    private void drawPipes(GraphicsContext graphics, TextLevelData structure, double cameraX) {
        GoodAsset pipeAsset = goodAssets.get("TP");
        if (pipeAsset == null) {
            return;
        }

        Image image = pipeAsset.getImage();
        double sliceHeight = image.getHeight() / 3.0;
        drawVerticalPipes(graphics, structure, image, cameraX, sliceHeight);
        drawSingleColumnPipeShafts(graphics, structure, image, cameraX, sliceHeight);
        drawHorizontalPipes(graphics, structure, image, cameraX, sliceHeight);
    }

    private void drawPipeSegment(
        GraphicsContext graphics,
        Image image,
        double cameraX,
        int column,
        int startRow,
        int segmentIndex,
        double sourceY,
        double sliceHeight
    ) {
        graphics.drawImage(
            image,
            0.0,
            sourceY,
            image.getWidth(),
            sliceHeight,
            (column * GameConstants.TILE_SIZE) - cameraX,
            (startRow + segmentIndex) * GameConstants.TILE_SIZE,
            GameConstants.TILE_SIZE * 2.0,
            GameConstants.TILE_SIZE
        );
    }

    private void drawHorizontalPipeCell(GraphicsContext graphics, int column, int row, double cameraX, int sourceColumn, int sourceRow) {
        if (rotatedHorizontalPipe == null) {
            return;
        }
        double cellWidth = rotatedHorizontalPipe.getWidth() / 3.0;
        double cellHeight = rotatedHorizontalPipe.getHeight() / 2.0;
        graphics.drawImage(
            rotatedHorizontalPipe,
            sourceColumn * cellWidth,
            sourceRow * cellHeight,
            cellWidth,
            cellHeight,
            (column * GameConstants.TILE_SIZE) - cameraX,
            row * GameConstants.TILE_SIZE,
            GameConstants.TILE_SIZE,
            GameConstants.TILE_SIZE
        );
    }

    private void drawVerticalPipes(GraphicsContext graphics, TextLevelData structure, Image image, double cameraX, double sliceHeight) {
        for (int row = 0; row < structure.getRowCount(); row++) {
            for (int column = 0; column < structure.getColumnCount() - 1; column++) {
                String pair = readPair(structure, row, column);
                if (!isVisibleVerticalPipeStart(structure, row, column, pair)) {
                    continue;
                }
                drawVerticalPipeFromRow(graphics, structure, image, cameraX, sliceHeight, row, column, pair);
            }
        }
    }

    private boolean isVisibleVerticalPipeStart(TextLevelData structure, int row, int column, String pair) {
        if ("[]".equals(pair) || "{]".equals(pair)) {
            return true;
        }
        if (!"pP".equals(pair) && !"eE".equals(pair)) {
            return false;
        }
        if (row == 0) {
            return true;
        }
        String abovePair = readPair(structure, row - 1, column);
        return !"[]".equals(abovePair) && !"{]".equals(abovePair) && !"pP".equals(abovePair);
    }

    private void drawVerticalPipeFromRow(
        GraphicsContext graphics,
        TextLevelData structure,
        Image image,
        double cameraX,
        double sliceHeight,
        int startRow,
        int column,
        String startPair
    ) {
        for (int segmentRow = startRow; segmentRow < structure.getRowCount(); segmentRow++) {
            String pair = segmentRow == startRow ? startPair : readPair(structure, segmentRow, column);
            if ("[]".equals(pair) || "{]".equals(pair)) {
                drawPipeSegment(graphics, image, cameraX, column, segmentRow, 0, 0.0, sliceHeight);
                continue;
            }
            if ("pP".equals(pair)) {
                drawPipeSegment(graphics, image, cameraX, column, segmentRow, 0, sliceHeight, sliceHeight);
                continue;
            }
            if ("eE".equals(pair)) {
                drawPipeSegment(graphics, image, cameraX, column, segmentRow, 0, sliceHeight * 2.0, sliceHeight);
            }
            break;
        }
    }

    private void drawSingleColumnPipeShafts(GraphicsContext graphics, TextLevelData structure, Image image, double cameraX, double sliceHeight) {
        double halfWidth = image.getWidth() / 2.0;
        for (int row = 0; row < structure.getRowCount(); row++) {
            for (int column = 0; column < structure.getColumnCount(); column++) {
                if (structure.getCell(row, column) != 'p') {
                    continue;
                }
                if (structure.getCell(row, column + 1) == 'P') {
                    continue;
                }
                if (column > 0) {
                    char leftCell = structure.getCell(row, column - 1);
                    if (leftCell == 'H') {
                        drawHorizontalPipeCell(graphics, column, row, cameraX, 1, 0);
                    } else if (leftCell == 'h') {
                        drawHorizontalPipeCell(graphics, column, row, cameraX, 1, 1);
                    }
                }
                graphics.drawImage(
                    image,
                    0.0,
                    sliceHeight,
                    halfWidth,
                    sliceHeight,
                    (column * GameConstants.TILE_SIZE) - cameraX,
                    row * GameConstants.TILE_SIZE,
                    GameConstants.TILE_SIZE,
                    GameConstants.TILE_SIZE
                );
            }
        }
    }

    private String readPair(TextLevelData structure, int row, int column) {
        return "" + structure.getCell(row, column) + structure.getCell(row, column + 1);
    }

    private void drawHorizontalPipes(GraphicsContext graphics, TextLevelData structure, Image image, double cameraX, double sliceHeight) {
        for (int row = 0; row < structure.getRowCount() - 1; row++) {
            for (int column = 0; column < structure.getColumnCount(); column++) {
                char top = structure.getCell(row, column);
                char bottom = structure.getCell(row + 1, column);
                if ((top != '<' && top != ')') || bottom != '>') {
                    continue;
                }
                drawHorizontalPipeCell(graphics, column, row, cameraX, 0, 0);
                drawHorizontalPipeCell(graphics, column, row + 1, cameraX, 0, 1);
                int segmentColumn = column + 1;
                while (segmentColumn < structure.getColumnCount()) {
                    char segmentTop = structure.getCell(row, segmentColumn);
                    char segmentBottom = structure.getCell(row + 1, segmentColumn);
                    if (segmentTop == 'H' && segmentBottom == 'h') {
                        drawHorizontalPipeCell(graphics, segmentColumn, row, cameraX, 1, 0);
                        drawHorizontalPipeCell(graphics, segmentColumn, row + 1, cameraX, 1, 1);
                    } else if (segmentTop == 'F' && segmentBottom == 'f') {
                        if (structure.getCell(row - 1, segmentColumn) == 'p') {
                            drawSingleColumnPipeCell(graphics, image, cameraX, sliceHeight, segmentColumn, row);
                        }
                        drawHorizontalPipeCell(graphics, segmentColumn, row, cameraX, 2, 0);
                        drawHorizontalPipeCell(graphics, segmentColumn, row + 1, cameraX, 2, 1);
                        break;
                    } else {
                        break;
                    }
                    segmentColumn++;
                }
            }
        }
    }

    private Image createRotatedHorizontalPipe(Image source) {
        Canvas canvas = new Canvas(source.getHeight(), source.getWidth());
        GraphicsContext graphics = canvas.getGraphicsContext2D();
        graphics.setImageSmoothing(false);
        graphics.translate(0.0, source.getWidth());
        graphics.rotate(-90.0);
        graphics.drawImage(source, 0.0, 0.0);
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        WritableImage snapshot = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        return canvas.snapshot(parameters, snapshot);
    }

    private void drawSingleColumnPipeCell(GraphicsContext graphics, Image image, double cameraX, double sliceHeight, int column, int row) {
        double halfWidth = image.getWidth() / 2.0;
        graphics.drawImage(
            image,
            0.0,
            sliceHeight,
            halfWidth,
            sliceHeight,
            (column * GameConstants.TILE_SIZE) - cameraX,
            row * GameConstants.TILE_SIZE,
            GameConstants.TILE_SIZE,
            GameConstants.TILE_SIZE
        );
    }
}
