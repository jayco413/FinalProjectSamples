package edu.mvcc.jcovey.avoidprojectiles.app;

import edu.mvcc.jcovey.JavaFXWindow;
import javafx.stage.Stage;

/**
 * Startup window for the Avoid Projectiles final project.
 *
 * @author Jason A. Covey
 */
public class AvoidProjectilesApp extends JavaFXWindow {
    @Override
    protected String getStageTitle() {
        return "Avoid Projectiles";
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
        new AvoidProjectilesApp().runAsStartUpWindow();
    }
}
