package edu.mvcc.jcovey.mario.view;

import edu.mvcc.jcovey.mario.model.FireworkModel;
import edu.mvcc.jcovey.mario.model.GameConstants;
import edu.mvcc.jcovey.mario.model.GameModel;
import java.util.Locale;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;

final class OverlayRenderer {
    private final RenderAssets assets;

    OverlayRenderer(RenderAssets assets) {
        this.assets = assets;
    }

    void render(GraphicsContext graphics, GameModel gameModel, double cameraX) {
        drawFireworks(graphics, gameModel, cameraX);
        drawHud(graphics, gameModel);
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

    private void drawHud(GraphicsContext graphics, GameModel gameModel) {
        graphics.setFill(Color.color(0.0, 0.0, 0.0, 0.25));
        graphics.fillRect(0.0, 0.0, GameConstants.VIEWPORT_WIDTH, GameConstants.HUD_ROWS * GameConstants.TILE_SIZE);

        graphics.setFill(Color.WHITE);
        graphics.setFontSmoothingType(FontSmoothingType.GRAY);
        graphics.setFont(!assets.hudFontFamily().isBlank() ? Font.font(assets.hudFontFamily(), 16.0) : Font.font(16.0));

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
        GoodAsset coinAsset = assets.goodAssets().get("CO");
        double iconSize = 16.0;
        double iconY = baselineY - iconSize + 1.0;
        if (coinAsset != null) {
            graphics.drawImage(coinAsset.getImage(), x, iconY, iconSize, iconSize);
        }
        graphics.fillText("x" + gameModel.getCoinText(), x + 18.0, baselineY);
    }
}
