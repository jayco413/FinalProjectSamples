package edu.mvcc.jcovey.mario.app;

import edu.mvcc.jcovey.JavaFXWindow;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Window that explains the game rules and objective.
 *
 * @author Jason A. Covey
 */
public class RulesWindow extends JavaFXWindow {
    private final Stage owner;

    /**
     * Creates the rules window.
     *
     * @param owner the owning stage
     */
    public RulesWindow(Stage owner) {
        this.owner = owner;
    }

    @Override
    protected String getStageTitle() {
        return "Rules";
    }

    @Override
    protected void configureStage(Stage stage) {
        stage.initOwner(owner);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setResizable(false);
    }

    @Override
    protected void configureController(Stage stage, Object controllerInstance) {
        InfoWindowController controller = (InfoWindowController) controllerInstance;
        controller.configure(
            "Rules",
            """
            Objective:
            Reach the flagpole at the end of each course and clear both World 1-1 and World 1-2.

            Core rules:
            Collect coins to increase score and earn extra lives every 100 coins.
            Stomp enemies from above or hit them with fireballs when Mario has fire power.
            Hit blocks to reveal coins, mushrooms, stars, and hidden items.
            Power-ups let Mario grow, throw fireballs, or gain temporary star power.
            Enter special pipes to reach bonus or transition areas.
            Touching enemies from the side as small Mario costs a life.
            Falling off the stage or letting the timer reach zero also costs a life.

            Winning and losing:
            Clearing World 1-2 completes the game.
            Losing all lives ends the run with a game over state.
            """
        );
    }

    @Override
    protected boolean isBlockingWindow() {
        return true;
    }
}
