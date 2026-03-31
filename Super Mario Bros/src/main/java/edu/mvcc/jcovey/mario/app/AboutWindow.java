package edu.mvcc.jcovey.mario.app;

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
            Name of the game: Super Mario Bros. 1-1 JavaFX Recreation
            Team name: Team Professor
            Team members: Jason A. Covey
            Course: CI245 Java Programming
            Assignment: Final Project
            Term: Spring 2026

            This JavaFX project recreates key systems from the original Super Mario Bros.
            using an MVC structure with model classes for rules and state, controller classes
            for input and coordination, and JavaFX/FXML view classes for presentation.
            """
        );
    }

    @Override
    protected boolean isBlockingWindow() {
        return true;
    }
}
