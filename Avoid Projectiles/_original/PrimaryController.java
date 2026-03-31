package edu.mvcc.jcovey.AvoidProjectiles;

import java.text.NumberFormat;
import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.event.ActionEvent;

/**
 * Represents the primary controller for the Avoid Projectiles game.
 * This controller manages the game's state, user inputs, and visual updates.
 * 
 * @author Jason A. Covey
 */
public class PrimaryController extends TimelineAnimationController {

    /**
     * Enum representing the different game modes/states.
     */
    private enum GameMode {
        NOT_PLAYING,
        PLAYING
    }

    private ImageViewScroller backgroundScroller;

    @FXML
    private Button btnStartGame;
    private GameMode gameMode = GameMode.NOT_PLAYING;
    
    @FXML
    private ImageView ivBackground, ivMario;
    private KeyHandler keyHandler;

    @FXML
    private Label lblScore;
    private double marioWidth;

    @FXML
    private Pane pnBackground;

    // List to store projectiles appearing in the game.
    private ArrayList<Projectile> projectiles = new ArrayList<>();

    // List to store projectiles that are currently affecting Mario.
    private ArrayList<Projectile> projectilesInEffect = new ArrayList<>();
    private int score = 0;
    private static final NumberFormat scoreFormat = NumberFormat.getNumberInstance();

    /**
     * Adds new projectiles to the game at specified conditions.
     */
    private void addNewProjectiles() {
        if (projectiles.size() < (score <= 30 ? 3 : score / 10)) {
            Projectile p = Projectile.getRandomProjectile();
            if (p != null) {
                p.setUp(300, 500, 501, 10, 1);
                pnBackground.getChildren().add(p);
                projectiles.add(p);
            }
        }
    }

    /**
     * Handles game logic during active gameplay.
     */
    private void gamePlay() {
        backgroundScroller.scroll(2);
        keyHandler.performKeyEffects();

        for (Projectile p : new ArrayList<>(projectilesInEffect)) {
            p.iterateCollisionEffect(projectilesInEffect, ivMario);
        }
        
        addNewProjectiles();
        handleExistingProjectiles();
        handleScore();
    }

    /**
     * Handles logic related to existing projectiles.
     * Removes projectiles that are off-screen or intersecting with Mario.
     */
    private void handleExistingProjectiles() {
        ArrayList<Projectile> projectilesToRemove = new ArrayList<>();
        for (Projectile p : new ArrayList<>(projectiles)) {
            p.setLayoutX(p.getLayoutX() - p.getSpeed());
            if (p.getLayoutX() < 0 || p.getBoundsInParent().intersects(ivMario.getBoundsInParent())) {
                if (p.getLayoutX() < 0) score += p.getScoreIfOffScreen();
                else p.beginCollisionEffect(projectilesInEffect, ivMario);
                projectilesToRemove.add(p);
            }
        }
        projectiles.removeAll(projectilesToRemove);
        pnBackground.getChildren().removeAll(projectilesToRemove);
    }

    /**
     * Updates the game score, and checks for game-end conditions.
     */
    private void handleScore() {
        score = Math.max(score, 0);
        lblScore.setText(scoreFormat.format(score));
        if (score >= 100) {
            resetGame();
        }
    }

    /**
     * Resets the game to its initial state.
     */
    private void resetGame() {
        Projectile.stopAllSounds();
        gameMode = GameMode.NOT_PLAYING;
        btnStartGame.setVisible(true);
        ivMario.setFitWidth(marioWidth);
        projectiles.clear();
        projectilesInEffect.clear();

        ArrayList<Node> thingsWeDontWant = new ArrayList<>();
        pnBackground.getChildren().forEach(node -> {
            if (node instanceof Projectile) {
                thingsWeDontWant.add(node);
            }
        });
        pnBackground.getChildren().removeAll(thingsWeDontWant);

        score = 0;
        lblScore.setText("000000");
        ivMario.setLayoutX(100);
        ivMario.setLayoutY(250);
    }

    /**
     * Handles actions that occur at every timer iteration.
     */
    @Override
    protected void handleTimerIteration() {
        ivMario.setVisible(gameMode == GameMode.PLAYING);
        if (gameMode == GameMode.PLAYING) {
            gamePlay();
        }
    }

    /**
     * Initializes game components and settings.
     */
    @Override
    protected void initializeConcrete() {
        keyHandler = new MarioKeyHandler(pnBackground, ivMario);
        marioWidth = ivMario.getFitWidth();
        backgroundScroller = new ImageViewScroller(ivBackground);

        scoreFormat.setMinimumIntegerDigits(6);
        scoreFormat.setGroupingUsed(false);
    }

    /**
     * Starts the game when the start button is pressed.
     * @param event The associated ActionEvent triggered by pressing the button.
     */
    @FXML
    void btnStartGame_OnAction(ActionEvent event) {
        gameMode = GameMode.PLAYING;
        btnStartGame.setVisible(false);
    }
}
