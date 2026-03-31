package edu.mvcc.jcovey.mario.app;

import edu.mvcc.jcovey.JavaFXWindow;
import javafx.stage.Stage;

/**
 * Startup window for the Mario project.
 *
 * @author Jason A. Covey
 */
public class SuperMarioApp extends JavaFXWindow {
    @Override
    protected String getStageTitle() {
        return "Super Mario Bros. 1-1";
    }

    @Override
    protected void configureStage(Stage stage) {
        stage.setResizable(false);
    }

    /**
     * Launches the JavaFX application.
     *
     * @param args ignored command-line arguments
     */
    public static void main(String[] args) {
        new SuperMarioApp().runAsStartUpWindow();
    }
}
