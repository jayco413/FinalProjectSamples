package edu.mvcc.jcovey.mario.controller;

import edu.mvcc.jcovey.JavaFXWindow;
import edu.mvcc.jcovey.mario.app.AboutWindow;
import edu.mvcc.jcovey.mario.app.ControlsWindow;
import edu.mvcc.jcovey.mario.app.LevelIntegrityService;
import edu.mvcc.jcovey.mario.app.PreferencesWindow;
import edu.mvcc.jcovey.mario.app.RulesWindow;
import edu.mvcc.jcovey.mario.app.UserPreferences;
import edu.mvcc.jcovey.mario.model.GameConstants;
import edu.mvcc.jcovey.mario.model.GameModel;
import edu.mvcc.jcovey.mario.model.InputState;
import edu.mvcc.jcovey.mario.view.GameRenderer;
import java.io.IOException;
import java.nio.file.Path;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Main controller for the startup window and gameplay loop.
 *
 * @author Jason A. Covey
 */
public class GameController {
    private static final double SCREEN_USAGE_RATIO = 0.8;
    private static final double MIN_RENDER_SCALE = 0.25;

    @FXML
    private MenuItem exitMenuItem;

    @FXML
    private MenuItem preferencesMenuItem;

    @FXML
    private MenuItem controlsMenuItem;

    @FXML
    private MenuItem rulesMenuItem;

    @FXML
    private MenuItem aboutMenuItem;

    @FXML
    private StackPane gamePane;

    @FXML
    private Canvas gameCanvas;

    @FXML
    private Label statusLabel;

    @FXML
    private Label integrityLabel;

    private final InputState inputState = new InputState();
    private final GameModel gameModel = new GameModel();
    private final GameRenderer renderer = new GameRenderer();
    private final SoundEffectPlayer soundEffectPlayer = new SoundEffectPlayer();
    private final BackgroundMusicPlayer backgroundMusicPlayer = new BackgroundMusicPlayer();
    private UserPreferences userPreferences;
    private AnimationTimer timer;
    private long lastTick;
    private Path lastIntegrityPath;

    /**
     * Initializes the JavaFX controller and starts the game loop.
     */
    @FXML
    public void initialize() {
        gamePane.setFocusTraversable(true);
        gamePane.setOnKeyPressed(event -> handlePressed(event.getCode()));
        gamePane.setOnKeyReleased(event -> handleReleased(event.getCode()));
        gamePane.setOnMouseClicked(event -> gamePane.requestFocus());

        userPreferences = UserPreferences.load();
        renderer.initialize();
        soundEffectPlayer.initialize();
        backgroundMusicPlayer.initialize();
        gameModel.setDeathRespawnDelaySeconds(soundEffectPlayer.getDurationSeconds("death"));
        installMenuHandlers();
        applyPreferences(true);
        refreshIntegrityLabel();
        renderFrame();
        updateStatusBar();
        startLoop();
        gamePane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(event -> handlePressed(event.getCode()));
                newScene.setOnKeyReleased(event -> handleReleased(event.getCode()));
                resizeForPreferences();
                gamePane.requestFocus();
            }
        });
    }

    private void startLoop() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastTick == 0L) {
                    lastTick = now;
                    return;
                }

                double deltaSeconds = Math.min((now - lastTick) / 1_000_000_000.0, 1.0 / 30.0);
                lastTick = now;
                gameModel.update(deltaSeconds, inputState);
                soundEffectPlayer.playAll(gameModel.drainSoundEffects());
                syncBackgroundMusic();
                gameModel.setLevelClearMusicFinished(backgroundMusicPlayer.isLevelClearTrackFinished());
                syncIntegrityLabel();
                renderFrame();
                updateStatusBar();
            }
        };
        timer.start();
    }

    private void renderFrame() {
        renderer.render(gameCanvas, gameModel);
    }

    private void handlePressed(KeyCode keyCode) {
        if (keyCode == KeyCode.LEFT) {
            inputState.setLeftPressed(true);
        } else if (keyCode == KeyCode.RIGHT) {
            inputState.setRightPressed(true);
        } else if (keyCode == KeyCode.DOWN) {
            inputState.setDownPressed(true);
        } else if (keyCode == KeyCode.Z || keyCode == KeyCode.SPACE || keyCode == KeyCode.UP) {
            inputState.setJumpPressed(true);
        } else if (keyCode == KeyCode.X || keyCode == KeyCode.SHIFT) {
            inputState.setRunPressed(true);
        } else if (keyCode == KeyCode.R) {
            gameModel.reset();
            soundEffectPlayer.playAll(gameModel.drainSoundEffects());
            syncBackgroundMusic();
            refreshIntegrityLabel();
        } else if (handleCourseShortcut(keyCode)) {
            soundEffectPlayer.playAll(gameModel.drainSoundEffects());
            syncBackgroundMusic();
            refreshIntegrityLabel();
        }
    }

    private boolean handleCourseShortcut(KeyCode keyCode) {
        int shortcutDigit;
        if (keyCode == KeyCode.DIGIT1) {
            shortcutDigit = 1;
        } else if (keyCode == KeyCode.DIGIT2) {
            shortcutDigit = 2;
        } else if (keyCode == KeyCode.DIGIT3) {
            shortcutDigit = 3;
        } else {
            return false;
        }
        return gameModel.loadCourseByShortcut(shortcutDigit);
    }

    private void handleReleased(KeyCode keyCode) {
        if (keyCode == KeyCode.LEFT) {
            inputState.setLeftPressed(false);
        } else if (keyCode == KeyCode.RIGHT) {
            inputState.setRightPressed(false);
        } else if (keyCode == KeyCode.DOWN) {
            inputState.setDownPressed(false);
        } else if (keyCode == KeyCode.Z || keyCode == KeyCode.SPACE || keyCode == KeyCode.UP) {
            inputState.setJumpPressed(false);
        } else if (keyCode == KeyCode.X || keyCode == KeyCode.SHIFT) {
            inputState.setRunPressed(false);
        }
    }

    private void syncBackgroundMusic() {
        if (!gameModel.getMario().isAlive() && !gameModel.isGameOver()) {
            backgroundMusicPlayer.stop();
            return;
        }
        backgroundMusicPlayer.sync(
            gameModel.getLevel().getAreaId(),
            gameModel.getMario().hasStarPower(),
            gameModel.isCourseClearActive() || gameModel.isLevelComplete(),
            gameModel.isGameOver()
        );
    }

    private void installMenuHandlers() {
        exitMenuItem.setOnAction(event -> closeCurrentWindow());
        preferencesMenuItem.setOnAction(event -> showPreferencesWindow());
        controlsMenuItem.setOnAction(event -> openModalWindow(new ControlsWindow(getStage())));
        rulesMenuItem.setOnAction(event -> openModalWindow(new RulesWindow(getStage())));
        aboutMenuItem.setOnAction(event -> openModalWindow(new AboutWindow(getStage())));
    }

    private void closeCurrentWindow() {
        backgroundMusicPlayer.stop();
        getStage().close();
    }

    private void showPreferencesWindow() {
        openModalWindow(new PreferencesWindow(getStage(), userPreferences.copy(), this::savePreferences));
    }

    private void savePreferences(UserPreferences updatedPreferences) {
        boolean startWorldChanged = !userPreferences.getStartWorld().equals(updatedPreferences.getStartWorld());
        userPreferences = updatedPreferences;
        userPreferences.save();
        applyPreferences(false);
        if (startWorldChanged) {
            gameModel.loadCourseByWorldText(userPreferences.getStartWorld());
            soundEffectPlayer.playAll(gameModel.drainSoundEffects());
            refreshIntegrityLabel();
        }
    }

    private void applyPreferences(boolean initialLoad) {
        backgroundMusicPlayer.setEnabled(userPreferences.isMusicEnabled());
        soundEffectPlayer.setEnabled(userPreferences.isSoundEnabled());
        resizeForPreferences();
        if (initialLoad) {
            gameModel.loadCourseByWorldText(userPreferences.getStartWorld());
        }
        syncBackgroundMusic();
        renderFrame();
        updateStatusBar();
    }

    private void resizeForPreferences() {
        Stage stage = getStageIfAvailable();
        if (stage == null) {
            applyViewportScale(1.0);
            return;
        }

        applyViewportScale(1.0);
        stage.sizeToScene();

        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        double nonPlayfieldWidth = Math.max(0.0, stage.getWidth() - GameConstants.VIEWPORT_WIDTH);
        double nonPlayfieldHeight = Math.max(0.0, stage.getHeight() - GameConstants.VIEWPORT_HEIGHT);
        double maxStageWidth = visualBounds.getWidth() * SCREEN_USAGE_RATIO;
        double maxStageHeight = visualBounds.getHeight() * SCREEN_USAGE_RATIO;
        double widthScale = Math.max(MIN_RENDER_SCALE, (maxStageWidth - nonPlayfieldWidth) / GameConstants.VIEWPORT_WIDTH);
        double heightScale = Math.max(MIN_RENDER_SCALE, (maxStageHeight - nonPlayfieldHeight) / GameConstants.VIEWPORT_HEIGHT);
        double scale = Math.min(widthScale, heightScale);

        applyViewportScale(scale);
        stage.sizeToScene();
        clampStageToScreen(stage, visualBounds);
    }

    private void applyViewportScale(double scale) {
        gameCanvas.setWidth(GameConstants.VIEWPORT_WIDTH * scale);
        gameCanvas.setHeight(GameConstants.VIEWPORT_HEIGHT * scale);
        gamePane.setPrefWidth(gameCanvas.getWidth());
        gamePane.setPrefHeight(gameCanvas.getHeight());
    }

    private void clampStageToScreen(Stage stage, Rectangle2D visualBounds) {
        double maxX = Math.max(visualBounds.getMinX(), visualBounds.getMaxX() - stage.getWidth());
        double maxY = Math.max(visualBounds.getMinY(), visualBounds.getMaxY() - stage.getHeight());
        double clampedX = Math.max(visualBounds.getMinX(), Math.min(stage.getX(), maxX));
        double clampedY = Math.max(visualBounds.getMinY(), Math.min(stage.getY(), maxY));
        stage.setX(clampedX);
        stage.setY(clampedY);
    }

    private void updateStatusBar() {
        statusLabel.setText(
            "Status: " + gameModel.getBannerText()
                + " | World: " + gameModel.getWorldText()
                + " | Lives: " + gameModel.getLivesText()
                + " | Score: " + gameModel.getScoreText()
        );
    }

    private void refreshIntegrityLabel() {
        lastIntegrityPath = gameModel.getLevel().getLevelPath();
        integrityLabel.setText("Level SHA-256: loading...");
        LevelIntegrityService.computeDigestLabel(lastIntegrityPath)
            .thenAccept(digest -> Platform.runLater(() ->
                integrityLabel.setText("Level SHA-256: " + digest)
            ))
            .exceptionally(exception -> {
                Platform.runLater(() -> integrityLabel.setText("Level SHA-256: unavailable"));
                return null;
            });
    }

    private void syncIntegrityLabel() {
        Path currentPath = gameModel.getLevel().getLevelPath();
        if (lastIntegrityPath == null || !lastIntegrityPath.equals(currentPath)) {
            refreshIntegrityLabel();
        }
    }

    private void openModalWindow(JavaFXWindow window) {
        stopLoop();
        try {
            Stage owner = getStage();
            window.openNewWindow(owner.getX() + 50.0, owner.getY() + 50.0);
        } catch (IOException exception) {
            integrityLabel.setText("Window error: " + exception.getMessage());
        } finally {
            resumeLoop();
            gamePane.requestFocus();
        }
    }

    private void stopLoop() {
        if (timer != null) {
            timer.stop();
        }
        lastTick = 0L;
    }

    private void resumeLoop() {
        if (timer != null) {
            timer.start();
        }
        lastTick = 0L;
    }

    private Stage getStage() {
        return (Stage) gamePane.getScene().getWindow();
    }

    private Stage getStageIfAvailable() {
        if (gamePane.getScene() == null || gamePane.getScene().getWindow() == null) {
            return null;
        }
        return (Stage) gamePane.getScene().getWindow();
    }
}
