package edu.mvcc.jcovey.avoidprojectiles.app;

import edu.mvcc.jcovey.JavaFXWindow;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Window that explains the game rules and objective.
 *
 * <p>AI-assisted documentation updates reviewed and integrated by Jason A. Covey.</p>
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
            Reach 100 points in as little time as possible.

            Core rules:
            Move Mario around the playfield to avoid incoming projectiles.
            Bullet Bills travel from right to left. Each one that leaves the screen
            without touching Mario awards one point.
            Starmen grant temporary invincibility, letting Mario ignore Bullet Bills.
            Mini Mushrooms temporarily shrink Mario, making him a smaller target.

            Winning and losing:
            Reaching 100 points ends the run and shows the completion time.
            Fast runs can enter the top-10 high-score table with player initials.
            A hit from a Bullet Bill does not end the game immediately, but it breaks
            the survival streak by forcing the player to continue under pressure.
            """
        );
    }

    @Override
    protected boolean isBlockingWindow() {
        return true;
    }
}
