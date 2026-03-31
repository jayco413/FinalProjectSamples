package edu.mvcc.jcovey.mario.app;

import edu.mvcc.jcovey.JavaFXWindow;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Window that documents the game controls.
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
            Left Arrow: move Mario left
            Right Arrow: move Mario right
            Down Arrow: enter vertical pipes
            Z, Space, or Up Arrow: jump
            X or Shift: run
            R: restart the current run
            1: start from World 1-1
            2: start from World 1-2

            Menus:
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
