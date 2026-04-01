package edu.mvcc.jcovey.mario.view;

import edu.mvcc.jcovey.mario.model.EnemyModel;
import edu.mvcc.jcovey.mario.model.FireballModel;
import edu.mvcc.jcovey.mario.model.GameModel;
import edu.mvcc.jcovey.mario.model.MarioModel;
import edu.mvcc.jcovey.mario.view.SpriteAtlas.MarioPose;
import java.util.HashMap;
import java.util.Map;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

final class EntityRenderer {
    private final RenderAssets assets;
    private final Map<Image, Image> invertedImageCache = new HashMap<>();

    EntityRenderer(RenderAssets assets) {
        this.assets = assets;
    }

    void render(GraphicsContext graphics, GameModel gameModel, double cameraX) {
        drawFireballs(graphics, gameModel, cameraX);
        drawEnemies(graphics, gameModel, cameraX);
        drawMario(graphics, gameModel.getMario(), cameraX);
    }

    private void drawFireballs(GraphicsContext graphics, GameModel gameModel, double cameraX) {
        GoodAsset fireballAsset = assets.goodAssets().get("FI");
        for (FireballModel fireball : gameModel.getFireballs()) {
            if (!fireball.isActive()) {
                continue;
            }
            if (fireballAsset != null) {
                graphics.drawImage(fireballAsset.getImage(), fireball.getX() - cameraX, fireball.getY(), fireball.getWidth(), fireball.getHeight());
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
            } else if (enemy.isFireDefeated()) {
                drawFireDefeatedEnemy(graphics, enemy, cameraX);
            } else if (!assets.atlas().hasEnemySheet()) {
                drawEnemyPlaceholder(graphics, enemy, cameraX);
            } else {
                Rectangle2D viewport = assets.atlas().getEnemyViewport(enemy);
                graphics.drawImage(
                    assets.atlas().getEnemySheet(),
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
    }

    private void drawBumpDefeatedEnemy(GraphicsContext graphics, EnemyModel enemy, double cameraX) {
        double progress = enemy.getDefeatProgress();
        double alpha = progress < 0.7 ? 1.0 : Math.max(0.0, 1.0 - ((progress - 0.7) / 0.3));
        graphics.setGlobalAlpha(alpha);
        if (assets.atlas().hasEnemySheet()) {
            Rectangle2D viewport = assets.atlas().getEnemyViewport(enemy);
            graphics.drawImage(
                assets.atlas().getEnemySheet(),
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
        graphics.setGlobalAlpha(Math.max(0.0, 1.0 - progress));
        if (assets.atlas().hasEnemySheet()) {
            Rectangle2D viewport = assets.atlas().getEnemyViewport(enemy);
            graphics.drawImage(
                assets.atlas().getEnemySheet(),
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
        MarioPose marioPose = assets.atlas().getMarioPose(mario);
        if (marioPose != null) {
            drawMarioPoseImage(graphics, marioPose, mario, cameraX);
        } else if (!assets.atlas().hasMarioSheet()) {
            drawMarioPlaceholder(graphics, mario, cameraX);
        } else {
            drawMarioSprite(graphics, assets.atlas().getMarioSheet(), mario, cameraX, 32.0, mario.isSuperForm() ? 64.0 : mario.getHeight());
        }
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
        double targetX = squarePose ? slotX + (mario.isFacingRight() ? Math.max(0.0, slotWidth - drawWidth) : 0.0) : slotX;
        double targetY = squarePose ? slotY + Math.max(0.0, slotHeight - drawHeight) : slotY;
        graphics.setImageSmoothing(true);
        if (mario.isFacingRight()) {
            graphics.drawImage(poseImage, targetX, targetY, drawWidth, drawHeight);
        } else {
            graphics.save();
            graphics.translate(targetX + drawWidth, targetY);
            graphics.scale(-1.0, 1.0);
            graphics.drawImage(poseImage, 0.0, 0.0, drawWidth, drawHeight);
            graphics.restore();
        }
        graphics.setImageSmoothing(false);
    }

    private void drawMarioSprite(GraphicsContext graphics, Image spriteSheet, MarioModel mario, double cameraX, double drawWidth, double drawHeight) {
        Image starVariant = getStarManPoseImage(spriteSheet, mario);
        Rectangle2D viewport;
        if (!mario.isAlive()) {
            viewport = new Rectangle2D(0.0, 8.0, 16.0, 16.0);
        } else if (!mario.isOnGround()) {
            viewport = new Rectangle2D(56.0, 8.0, 16.0, 16.0);
        } else if (Math.abs(mario.getVelocityX()) < 10.0) {
            viewport = new Rectangle2D(0.0, 8.0, 16.0, 16.0);
        } else {
            int frame = ((int) (mario.getAnimationTime() * 10.0)) % 3;
            viewport = frame == 0 ? new Rectangle2D(20.0, 8.0, 16.0, 16.0)
                : frame == 1 ? new Rectangle2D(38.0, 8.0, 16.0, 16.0)
                : new Rectangle2D(0.0, 8.0, 16.0, 16.0);
        }
        double targetX = mario.getX() - cameraX;
        double targetY = mario.getFeetY() - drawHeight;
        if (mario.isFacingRight()) {
            graphics.drawImage(starVariant, viewport.getMinX(), viewport.getMinY(), viewport.getWidth(), viewport.getHeight(), targetX, targetY, drawWidth, drawHeight);
        } else {
            graphics.save();
            graphics.translate(targetX + drawWidth, targetY);
            graphics.scale(-1.0, 1.0);
            graphics.drawImage(starVariant, viewport.getMinX(), viewport.getMinY(), viewport.getWidth(), viewport.getHeight(), 0.0, 0.0, drawWidth, drawHeight);
            graphics.restore();
        }
    }

    private Image getStarManPoseImage(Image baseImage, MarioModel mario) {
        if (!mario.hasStarPower()) {
            return baseImage;
        }
        double normalized = Math.max(0.0, Math.min(1.0, mario.getStarPowerTimer() / 10.0));
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
                inverted.getPixelWriter().setColor(x, y, new Color(1.0 - color.getRed(), 1.0 - color.getGreen(), 1.0 - color.getBlue(), color.getOpacity()));
            }
        }
        return inverted;
    }

    private void drawEnemyPlaceholder(GraphicsContext graphics, EnemyModel enemy, double cameraX) {
        Color fill = "parakoopa".equals(enemy.getType()) || "koopa".equals(enemy.getType()) ? Color.web("#6cc04a") : Color.web("#8b4a20");
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
}
