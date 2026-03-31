package edu.mvcc.jcovey.avoidprojectiles.app;

import edu.mvcc.jcovey.JavaFXWindow;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Window that presents required course and team metadata.
 *
 * @author Jason A. Covey
 */
public class AboutWindow extends JavaFXWindow {
    private final Stage owner;

    /**
     * Creates the about window.
     *
     * @param owner the owning stage
     */
    public AboutWindow(Stage owner) {
        this.owner = owner;
    }

    @Override
    protected String getStageTitle() {
        return "About";
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
            "About",
            """
            Name of the game: Avoid Projectiles
            Team name: Team Professor
            Team members: Jason A. Covey
            Course: CI245 Java Programming
            Assignment: Final Project
            Term: Spring 2026

            This version restructures the original game into an MVC JavaFX project
            with a required menu-based startup window, persistent preferences,
            and dedicated help forms while preserving the original gameplay loop.
            """
        );
    }

    @Override
    protected boolean isBlockingWindow() {
        return true;
    }
}
