package edu.mvcc.jcovey.mario.view;

import edu.mvcc.jcovey.mario.model.GameConstants;
import edu.mvcc.jcovey.mario.model.GameModel;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class GameRenderer {
    private final RenderAssets assets = new RenderAssets();
    private final WorldRenderer worldRenderer = new WorldRenderer(assets);
    private final EntityRenderer entityRenderer = new EntityRenderer(assets);
    private final OverlayRenderer overlayRenderer = new OverlayRenderer(assets);

    public void initialize() {
        assets.load();
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
        worldRenderer.render(graphics, gameModel, cameraX);
        entityRenderer.render(graphics, gameModel, cameraX);
        overlayRenderer.render(graphics, gameModel, cameraX);
        graphics.restore();
    }
}
