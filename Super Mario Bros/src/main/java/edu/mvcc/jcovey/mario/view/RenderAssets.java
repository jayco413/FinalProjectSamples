package edu.mvcc.jcovey.mario.view;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

final class RenderAssets {
    private final SpriteAtlas atlas = new SpriteAtlas();
    private final GoodAssetCatalog goodAssets = new GoodAssetCatalog();
    private Image repeatingBackground;
    private Image undergroundLevelBackground;
    private Image undergroundBackground;
    private Image rotatedHorizontalPipe;
    private Image mushroomBodyLeft;
    private Image mushroomBodyMiddle;
    private Image mushroomBodyRight;
    private String hudFontFamily = "";

    void load() {
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
        mushroomBodyLeft = loadImage("tiles", "JL_mushroom_body_left_1x1.png");
        mushroomBodyMiddle = loadImage("tiles", "JM_mushroom_body_middle_1x1.png");
        mushroomBodyRight = loadImage("tiles", "JR_mushroom_body_right_1x1.png");
        GoodAsset pipeAsset = goodAssets.get("TP");
        if (pipeAsset != null) {
            rotatedHorizontalPipe = createRotatedHorizontalPipe(pipeAsset.getImage());
        }
        Font hudFont = loadHudFont();
        hudFontFamily = hudFont != null ? hudFont.getFamily() : "";
    }

    SpriteAtlas atlas() {
        return atlas;
    }

    GoodAssetCatalog goodAssets() {
        return goodAssets;
    }

    Image repeatingBackground() {
        return repeatingBackground;
    }

    Image undergroundLevelBackground() {
        return undergroundLevelBackground;
    }

    Image undergroundBackground() {
        return undergroundBackground;
    }

    Image rotatedHorizontalPipe() {
        return rotatedHorizontalPipe;
    }

    Image mushroomBodyLeft() {
        return mushroomBodyLeft;
    }

    Image mushroomBodyMiddle() {
        return mushroomBodyMiddle;
    }

    Image mushroomBodyRight() {
        return mushroomBodyRight;
    }

    String hudFontFamily() {
        return hudFontFamily;
    }

    private Font loadHudFont() {
        Path fontPath = Path.of("assets", "fonts", "hud-font.otf");
        try (InputStream stream = Files.newInputStream(fontPath)) {
            return Font.loadFont(stream, 18.0);
        } catch (IOException exception) {
            return null;
        }
    }

    private Image loadImage(String folder, String fileName) {
        return new Image(Path.of("assets", "game_art", folder, fileName).toUri().toString());
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
}
