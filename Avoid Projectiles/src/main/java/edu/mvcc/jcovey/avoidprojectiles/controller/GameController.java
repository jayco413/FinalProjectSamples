package edu.mvcc.jcovey.avoidprojectiles.controller;

import edu.mvcc.jcovey.JavaFXWindow;
import edu.mvcc.jcovey.avoidprojectiles.app.AboutWindow;
import edu.mvcc.jcovey.avoidprojectiles.app.ControlsWindow;
import edu.mvcc.jcovey.avoidprojectiles.app.HighScoreEntryWindow;
import edu.mvcc.jcovey.avoidprojectiles.app.HighScoreStore;
import edu.mvcc.jcovey.avoidprojectiles.app.HighScoresWindow;
import edu.mvcc.jcovey.avoidprojectiles.app.LevelIntegrityService;
import edu.mvcc.jcovey.avoidprojectiles.app.PreferencesWindow;
import edu.mvcc.jcovey.avoidprojectiles.app.RulesWindow;
import edu.mvcc.jcovey.avoidprojectiles.app.UserPreferences;
import edu.mvcc.jcovey.avoidprojectiles.model.GameModel;
import edu.mvcc.jcovey.avoidprojectiles.model.InputState;
import edu.mvcc.jcovey.avoidprojectiles.model.RunCompletion;
import edu.mvcc.jcovey.avoidprojectiles.view.GameRenderer;
import java.io.IOException;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Controller that connects the JavaFX view to the gameplay model.
 *
 * <p>AI-assisted timing and high-score enhancements reviewed and integrated by
 * Jason A. Covey.</p>
 *
 * @author Jason A. Covey
 */
public class GameController {
    private static final String IMAGE_ROOT = "/edu/mvcc/jcovey/avoidprojectiles/assets/images/";
    private static final String MEDIA_ROOT = "/edu/mvcc/jcovey/avoidprojectiles/assets/media/";
    private static final double BASE_WIDTH = 800.0;
    private static final double BASE_HEIGHT = 500.0;
    private static final double BASE_SCORE_X = 650.0;
    private static final double BASE_SCORE_Y = 18.0;
    private static final double BASE_START_X = 337.0;
    private static final double BASE_START_Y = 224.0;
    private static final double BASE_START_MESSAGE_X = 235.0;
    private static final double BASE_START_MESSAGE_Y = 180.0;
    private static final double BASE_HIGH_SCORES_TITLE_X = 26.0;
    private static final double BASE_HIGH_SCORES_TITLE_Y = 56.0;
    private static final double BASE_HIGH_SCORES_X = 28.0;
    private static final double BASE_HIGH_SCORES_Y = 90.0;
    private static final double BASE_MARIO_X = 100.0;
    private static final double BASE_MARIO_Y = 250.0;

    @FXML
    private StackPane gamePane;

    @FXML
    private Pane playfieldPane;

    @FXML
    private ImageView backgroundPrimary;

    @FXML
    private ImageView backgroundSecondary;

    @FXML
    private Pane projectileLayer;

    @FXML
    private ImageView marioImageView;

    @FXML
    private Canvas overlayCanvas;

    @FXML
    private Label scoreLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Label integrityLabel;

    @FXML
    private Button startGameButton;

    @FXML
    private Label startMessageLabel;

    @FXML
    private Label highScoresTitleLabel;

    @FXML
    private Label highScoresLabel;

    @FXML
    private MenuItem exitMenuItem;

    @FXML
    private MenuItem highScoresMenuItem;

    @FXML
    private MenuItem preferencesMenuItem;

    @FXML
    private MenuItem controlsMenuItem;

    @FXML
    private MenuItem rulesMenuItem;

    @FXML
    private MenuItem aboutMenuItem;

    private final GameModel gameModel;
    private final InputState inputState;
    private final SoundEffectPlayer soundEffectPlayer;
    private final AnimationTimer animationTimer;
    private GameRenderer renderer;
    private UserPreferences preferences;
    private HighScoreStore highScoreStore;
    private boolean highScorePromptScheduled;

    /**
     * Creates the controller.
     */
    public GameController() {
        gameModel = new GameModel();
        inputState = new InputState();
        soundEffectPlayer = new SoundEffectPlayer();
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                gameModel.update(inputState, preferences, now);
                playQueuedSounds();
                handleRunCompletion();
                renderer.render(gameModel);
            }
        };
    }

    @FXML
    private void initialize() {
        preferences = UserPreferences.load();
        highScoreStore = HighScoreStore.load();
        loadStaticImages();
        renderer = new GameRenderer(
            backgroundPrimary,
            backgroundSecondary,
            projectileLayer,
            marioImageView,
            overlayCanvas,
            scoreLabel,
            statusLabel,
            startGameButton,
            startMessageLabel,
            highScoresTitleLabel,
            highScoresLabel
        );
        renderer.setRenderScale(preferences.getWindowScale());
        renderer.setHighScoreSummary(highScoreStore.toDisplayString());
        wireMenuItems();
        LevelIntegrityService.computeDigestAsync().thenAccept(digest ->
            Platform.runLater(() -> integrityLabel.setText("Asset SHA-256: " + digest))
        );
        gamePane.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                installKeyHandlers(newScene);
                Platform.runLater(() -> applyPreferencesToStage((Stage) newScene.getWindow()));
            }
        });
        renderer.render(gameModel);
        animationTimer.start();
    }

    @FXML
    private void handleStartGame() {
        SoundEffectPlayer.stopAllSounds();
        gameModel.startGame();
        renderer.render(gameModel);
        gamePane.requestFocus();
    }

    private void wireMenuItems() {
        exitMenuItem.setOnAction(event -> Platform.exit());
        highScoresMenuItem.setOnAction(event -> openHighScoresWindow());
        preferencesMenuItem.setOnAction(event -> openPreferencesWindow());
        controlsMenuItem.setOnAction(event -> openModalWindow(new ControlsWindow(currentStage())));
        rulesMenuItem.setOnAction(event -> openModalWindow(new RulesWindow(currentStage())));
        aboutMenuItem.setOnAction(event -> openModalWindow(new AboutWindow(currentStage())));
    }

    private void loadStaticImages() {
        Image background = new Image(getClass().getResource(IMAGE_ROOT + "333223.jpg").toExternalForm());
        backgroundPrimary.setImage(background);
        backgroundSecondary.setImage(background);
        marioImageView.setImage(new Image(getClass().getResource(IMAGE_ROOT + "mario.png").toExternalForm()));
    }

    private void installKeyHandlers(Scene scene) {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyPressed);
        scene.addEventFilter(KeyEvent.KEY_RELEASED, this::handleKeyReleased);
    }

    private void handleKeyPressed(KeyEvent event) {
        updateKey(event.getCode(), true);
    }

    private void handleKeyReleased(KeyEvent event) {
        updateKey(event.getCode(), false);
    }

    private void updateKey(KeyCode keyCode, boolean pressed) {
        if (keyCode == KeyCode.UP) {
            inputState.setUpPressed(pressed);
        } else if (keyCode == KeyCode.DOWN) {
            inputState.setDownPressed(pressed);
        } else if (keyCode == KeyCode.LEFT) {
            inputState.setLeftPressed(pressed);
        } else if (keyCode == KeyCode.RIGHT) {
            inputState.setRightPressed(pressed);
        }
    }

    private void playQueuedSounds() {
        for (String soundName : gameModel.drainStoppedSoundEvents()) {
            soundEffectPlayer.stop(MEDIA_ROOT + soundName);
        }

        if (!preferences.isSoundEnabled()) {
            gameModel.drainSoundEvents();
            return;
        }

        for (String soundName : gameModel.drainSoundEvents()) {
            soundEffectPlayer.play(MEDIA_ROOT + soundName);
        }
    }

    private void handleRunCompletion() {
        RunCompletion completion = gameModel.consumePendingCompletion();
        if (completion == null) {
            return;
        }

        SoundEffectPlayer.stopAllSounds();
        if (!highScoreStore.qualifies(completion.elapsedSeconds())) {
            renderer.setHighScoreSummary(highScoreStore.toDisplayString());
            return;
        }

        if (highScorePromptScheduled) {
            return;
        }

        highScorePromptScheduled = true;
        animationTimer.stop();
        Platform.runLater(() -> {
            try {
                String initials = promptForInitials(completion.elapsedSeconds());
                if (!initials.isBlank()) {
                    highScoreStore.addScore(initials, completion.elapsedSeconds());
                    highScoreStore.save();
                }
                renderer.setHighScoreSummary(highScoreStore.toDisplayString());
                renderer.render(gameModel);
            } finally {
                highScorePromptScheduled = false;
                animationTimer.start();
            }
        });
    }

    private String promptForInitials(double elapsedSeconds) {
        HighScoreEntryWindow window = new HighScoreEntryWindow(currentStage(), elapsedSeconds);
        try {
            Stage owner = currentStage();
            window.openNewWindow(owner.getX() + 60.0, owner.getY() + 60.0);
            return window.getSubmittedInitials();
        } catch (IOException exception) {
            statusLabel.setText("Unable to record initials: " + exception.getMessage());
            return "";
        }
    }

    private void openHighScoresWindow() {
        openModalWindow(new HighScoresWindow(currentStage(), highScoreStore.toDisplayString()));
    }

    private void openPreferencesWindow() {
        PreferencesWindow window = new PreferencesWindow(currentStage(), preferences.copy(), updatedPreferences -> {
            preferences = updatedPreferences;
            preferences.save();
            SoundEffectPlayer.stopAllSounds();
            applyPreferencesToStage(currentStage());
        });
        openModalWindow(window);
    }

    private void applyPreferencesToStage(Stage stage) {
        if (stage == null) {
            return;
        }
        double scale = preferences.getWindowScale();
        renderer.setRenderScale(scale);
        double width = BASE_WIDTH * scale;
        double height = BASE_HEIGHT * scale;

        gamePane.setPrefSize(width, height);
        gamePane.setMinSize(width, height);
        gamePane.setMaxSize(width, height);

        playfieldPane.setPrefSize(width, height);
        playfieldPane.setMinSize(width, height);
        playfieldPane.setMaxSize(width, height);

        projectileLayer.setPrefSize(width, height);
        projectileLayer.setMinSize(width, height);
        projectileLayer.setMaxSize(width, height);

        backgroundPrimary.setFitWidth(width);
        backgroundPrimary.setFitHeight(height);
        backgroundSecondary.setFitWidth(width);
        backgroundSecondary.setFitHeight(height);
        overlayCanvas.setWidth(width);
        overlayCanvas.setHeight(height);

        scoreLabel.setLayoutX(BASE_SCORE_X * scale);
        scoreLabel.setLayoutY(BASE_SCORE_Y * scale);
        startMessageLabel.setLayoutX(BASE_START_MESSAGE_X * scale);
        startMessageLabel.setLayoutY(BASE_START_MESSAGE_Y * scale);
        startGameButton.setLayoutX(BASE_START_X * scale);
        startGameButton.setLayoutY(BASE_START_Y * scale);
        highScoresTitleLabel.setLayoutX(BASE_HIGH_SCORES_TITLE_X * scale);
        highScoresTitleLabel.setLayoutY(BASE_HIGH_SCORES_TITLE_Y * scale);
        highScoresLabel.setLayoutX(BASE_HIGH_SCORES_X * scale);
        highScoresLabel.setLayoutY(BASE_HIGH_SCORES_Y * scale);
        highScoresLabel.setPrefWidth(220.0 * scale);

        backgroundPrimary.setLayoutX(0.0);
        backgroundSecondary.setLayoutX(width);
        stage.sizeToScene();
    }

    private Stage currentStage() {
        return (Stage) gamePane.getScene().getWindow();
    }

    private void openModalWindow(JavaFXWindow window) {
        try {
            Stage owner = currentStage();
            window.openNewWindow(owner.getX() + 40.0, owner.getY() + 40.0);
        } catch (IOException exception) {
            statusLabel.setText("Unable to open window: " + exception.getMessage());
        }
    }
}
