package edu.mvcc.jcovey.AvoidProjectiles;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * A class that encapsulates the functionality of creating and displaying
 * a new window in JavaFX. Usage of this class requires the filename of
 * the FXML to be the same as the name of the concrete class which inherits
 * this abstract class.
 * 
 * @author Jason A. Covey
 */
public abstract class JavaFXWindow extends Application {
	
	private Object controller = null;
	
	/**
	 * Gets the controller instance which must be cast
	 * into its proper type by the caller.
	 * 
	 * @return the controller instance
	 */
    public Object getController() {
    	return controller;
    }

    /**
     * Sets the title of the window
     * @return the title of the window
     */
    protected abstract String getStageTitle();
    
    private Parent loadFXML() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
        		JavaFXWindow.class.getResource(
        				this.getClass().getSimpleName() + ".fxml"));
        Parent p = fxmlLoader.load();
        controller = fxmlLoader.getController();
        return p;
    }
    
    /**
     * Opens a new window at a specified position
     * 
     * @param screenX the x coordinate on the screen where the window should appear
     * @param screenY the y coordinate on the screen where the window should appear
     * @throws IOException
     */
    public void openNewWindow(double screenX, double screenY) throws IOException {
        Stage s = new Stage();
        s.setX(screenX);
        s.setY(screenY);
        start(s);
	}

    /**
     * Launches the window as the application start up window
     */
	public void runAsStartUpWindow() {
    	launch(this.getClass());
    }

	/**
	 * Start method inherited from Application class
	 */
	@Override
    public void start(Stage stage) throws IOException {
        startWindow(stage, getStageTitle());
    }

	private void startWindow(Stage stage, String title) throws IOException {
    	Scene scene = new Scene(loadFXML());
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }
}