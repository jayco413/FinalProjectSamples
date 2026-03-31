package edu.mvcc.jcovey;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Base class for JavaFX windows defined by FXML files whose names match the
 * concrete subclass names.
 *
 * @author Jason A. Covey
 */
public abstract class JavaFXWindow extends Application {
    private Object controller;

    /**
     * Gets the FXML controller created for this window.
     *
     * @return the controller instance
     */
    public Object getController() {
        return controller;
    }

    /**
     * Gets the stage title.
     *
     * @return the stage title
     */
    protected abstract String getStageTitle();

    /**
     * Allows subclasses to configure the stage before it is shown.
     *
     * @param stage the stage being prepared
     */
    protected void configureStage(Stage stage) {
    }

    /**
     * Allows subclasses to pass state to the controller before showing.
     *
     * @param stage the stage being prepared
     * @param controllerInstance the loaded controller
     */
    protected void configureController(Stage stage, Object controllerInstance) {
    }

    /**
     * Indicates whether the window should block until closed.
     *
     * @return true for modal behavior; false otherwise
     */
    protected boolean isBlockingWindow() {
        return false;
    }

    private Parent loadFXML() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(getClass().getSimpleName() + ".fxml"));
        Parent parent = loader.load();
        controller = loader.getController();
        return parent;
    }

    /**
     * Opens a new window at the requested screen position.
     *
     * @param screenX the desired x coordinate
     * @param screenY the desired y coordinate
     * @throws IOException if the FXML cannot be loaded
     */
    public void openNewWindow(double screenX, double screenY) throws IOException {
        Stage stage = new Stage();
        stage.setX(screenX);
        stage.setY(screenY);
        start(stage);
    }

    /**
     * Launches the subclass as the startup JavaFX application.
     */
    public void runAsStartUpWindow() {
        launch(getClass());
    }

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = loadFXML();
        Scene scene = new Scene(root);
        stage.setTitle(getStageTitle());
        stage.setScene(scene);
        configureController(stage, controller);
        configureStage(stage);
        stage.sizeToScene();
        if (isBlockingWindow()) {
            stage.showAndWait();
        } else {
            stage.show();
        }
    }
}
