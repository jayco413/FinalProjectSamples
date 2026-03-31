package edu.mvcc.jcovey.avoidprojectiles.view;

import edu.mvcc.jcovey.avoidprojectiles.model.GameModel;
import edu.mvcc.jcovey.avoidprojectiles.model.GamePhase;
import edu.mvcc.jcovey.avoidprojectiles.model.PlayerModel;
import edu.mvcc.jcovey.avoidprojectiles.model.ProjectileKind;
import edu.mvcc.jcovey.avoidprojectiles.model.ProjectileModel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Color;

/**
 * Presentation-only renderer that synchronizes JavaFX nodes from model state.
 *
 * <p>AI-assisted ready-screen leaderboard presentation reviewed and integrated
 * by Jason A. Covey.</p>
 *
 * @author Jason A. Covey
 */
public class GameRenderer {
    private static final String ASSET_ROOT = "/edu/mvcc/jcovey/avoidprojectiles/assets/images/";
    private static final double BASE_WIDTH = 800.0;

    private final ImageView backgroundPrimary;
    private final ImageView backgroundSecondary;
    private final Pane projectileLayer;
    private final ImageView marioImageView;
    private final Canvas overlayCanvas;
    private final Label scoreLabel;
    private final Label statusLabel;
    private final Button startButton;
    private final Label startMessageLabel;
    private final Label highScoresTitleLabel;
    private final Label highScoresLabel;
    private final Map<Integer, ImageView> projectileViews;
    private final Map<ProjectileKind, Image> projectileImages;
    private final Map<Image, Image> invertedImageCache;
    private final Image marioBaseImage;
    private double renderScale;
    private String highScoreSummary;

    /**
     * Creates the renderer.
     *
     * @param backgroundPrimary first scrolling background
     * @param backgroundSecondary second scrolling background
     * @param projectileLayer layer for moving projectile nodes
     * @param marioImageView mario display node
     * @param overlayCanvas canvas used for HUD accents
     * @param scoreLabel score display node
     * @param statusLabel status display node
     * @param startButton overlay button shown before play
     * @param startMessageLabel ready-screen objective label
     * @param highScoresTitleLabel ready-screen leaderboard heading
     * @param highScoresLabel ready-screen leaderboard body
     */
    public GameRenderer(
        ImageView backgroundPrimary,
        ImageView backgroundSecondary,
        Pane projectileLayer,
        ImageView marioImageView,
        Canvas overlayCanvas,
        Label scoreLabel,
        Label statusLabel,
        Button startButton,
        Label startMessageLabel,
        Label highScoresTitleLabel,
        Label highScoresLabel
    ) {
        this.backgroundPrimary = backgroundPrimary;
        this.backgroundSecondary = backgroundSecondary;
        this.projectileLayer = projectileLayer;
        this.marioImageView = marioImageView;
        this.overlayCanvas = overlayCanvas;
        this.scoreLabel = scoreLabel;
        this.statusLabel = statusLabel;
        this.startButton = startButton;
        this.startMessageLabel = startMessageLabel;
        this.highScoresTitleLabel = highScoresTitleLabel;
        this.highScoresLabel = highScoresLabel;
        projectileViews = new HashMap<>();
        projectileImages = new HashMap<>();
        invertedImageCache = new HashMap<>();
        marioBaseImage = marioImageView.getImage();
        renderScale = 1.0;
        highScoreSummary = "No recorded times yet.\nFinish a 100-point run to set the pace.";
    }

    /**
     * Updates the scale used for gameplay rendering.
     *
     * @param renderScale the viewport scale
     */
    public void setRenderScale(double renderScale) {
        this.renderScale = renderScale;
    }

    /**
     * Updates the start-screen leaderboard text.
     *
     * @param highScoreSummary formatted leaderboard body
     */
    public void setHighScoreSummary(String highScoreSummary) {
        this.highScoreSummary = highScoreSummary;
    }

    /**
     * Renders the current model state.
     *
     * @param gameModel the model to render
     */
    public void render(GameModel gameModel) {
        renderBackground(gameModel);
        renderPlayer(gameModel.getPlayer(), gameModel.getPhase());
        renderProjectiles(gameModel.getProjectiles());
        renderOverlay(gameModel);
        scoreLabel.setText(gameModel.getFormattedScore());
        statusLabel.setText(gameModel.getStatusText());
        boolean ready = gameModel.getPhase() == GamePhase.READY;
        startButton.setVisible(ready);
        startMessageLabel.setVisible(ready);
        highScoresTitleLabel.setVisible(ready);
        highScoresLabel.setVisible(ready);
        highScoresLabel.setText(highScoreSummary);
    }

    private void renderBackground(GameModel gameModel) {
        double offset = gameModel.getBackgroundOffset() * renderScale;
        backgroundPrimary.setLayoutX(-offset);
        backgroundSecondary.setLayoutX(backgroundPrimary.getLayoutX() + BASE_WIDTH * renderScale);
    }

    private void renderPlayer(PlayerModel player, GamePhase phase) {
        marioImageView.setVisible(phase == GamePhase.PLAYING);
        marioImageView.setLayoutX(player.getX() * renderScale);
        marioImageView.setLayoutY(player.getY() * renderScale);
        marioImageView.setFitWidth(player.getWidth() * renderScale);
        marioImageView.setImage(getStarmanPoseImage(marioBaseImage, player));
    }

    private void renderProjectiles(Iterable<ProjectileModel> projectiles) {
        Set<Integer> activeIds = new HashSet<>();
        for (ProjectileModel projectile : projectiles) {
            activeIds.add(projectile.getId());
            ImageView projectileView = projectileViews.computeIfAbsent(projectile.getId(), id -> createProjectileView(projectile.getKind()));
            projectileView.setLayoutX(projectile.getX() * renderScale);
            projectileView.setLayoutY(projectile.getY() * renderScale);
            projectileView.setFitWidth(projectile.getWidth() * renderScale);
        }

        projectileViews.entrySet().removeIf(entry -> {
            if (activeIds.contains(entry.getKey())) {
                return false;
            }
            projectileLayer.getChildren().remove(entry.getValue());
            return true;
        });
    }

    private ImageView createProjectileView(ProjectileKind kind) {
        ImageView imageView = new ImageView(projectileImages.computeIfAbsent(kind, this::loadProjectileImage));
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(kind.getWidth() * renderScale);
        projectileLayer.getChildren().add(imageView);
        return imageView;
    }

    private Image loadProjectileImage(ProjectileKind kind) {
        return new Image(getClass().getResource(ASSET_ROOT + kind.getImageName()).toExternalForm());
    }

    private Image getStarmanPoseImage(Image baseImage, PlayerModel player) {
        if (!player.isInvincible()) {
            return baseImage;
        }
        double normalized = Math.max(0.0, Math.min(1.0, player.getInvincibilityTicksRemaining() / 1000.0));
        double flashPeriod = 8.0 + ((1.0 - normalized) * 28.0);
        long flashBucket = (long) Math.floor(player.getInvincibilityTicksRemaining() / flashPeriod);
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

    private void renderOverlay(GameModel gameModel) {
        GraphicsContext graphics = overlayCanvas.getGraphicsContext2D();
        graphics.clearRect(0.0, 0.0, overlayCanvas.getWidth(), overlayCanvas.getHeight());

        graphics.setStroke(Color.rgb(255, 255, 255, 0.35));
        graphics.setLineWidth(3.0);
        graphics.strokeRoundRect(10.0, 10.0, overlayCanvas.getWidth() - 20.0, overlayCanvas.getHeight() - 20.0, 18.0, 18.0);

        Color indicatorColor = gameModel.getPlayer().isInvincible()
            ? Color.GOLD
            : Color.rgb(255, 255, 255, 0.22);
        graphics.setFill(indicatorColor);
        graphics.fillOval(18.0, 18.0, 18.0, 18.0);
    }
}
