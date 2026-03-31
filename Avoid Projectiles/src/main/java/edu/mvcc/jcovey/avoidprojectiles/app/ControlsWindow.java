package edu.mvcc.jcovey.avoidprojectiles.app;

import edu.mvcc.jcovey.JavaFXWindow;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Window that documents the game controls.
 *
 * <p>AI-assisted documentation updates reviewed and integrated by Jason A. Covey.</p>
 *
 * @author Jason A. Covey
 */
public class ControlsWindow extends JavaFXWindow {
    private final Stage owner;

    /**
     * Creates the controls window.
     *
     * @param owner the owning stage
     */
    public ControlsWindow(Stage owner) {
        this.owner = owner;
    }

    @Override
    protected String getStageTitle() {
        return "Controls";
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
            "Controls",
            """
            Up Arrow: move Mario upward
            Down Arrow: move Mario downward
            Left Arrow: move Mario left
            Right Arrow: move Mario right

            During play:
            Let Bullet Bills pass safely to earn one point each.
            Collect Starmen for temporary invincibility.
            Collect Mini Mushrooms to shrink Mario for a limited time.
            The run ends at 100 points, so finish as quickly as possible.

            Menus:
            File > High Scores opens the saved top-10 leaderboard.
            File > Exit closes the game.
            Edit > Preferences opens the persistent settings window.
            Help > Controls, Rules, and About open the required help screens.
            """
        );
    }

    @Override
    protected boolean isBlockingWindow() {
        return true;
    }
}
