package edu.mvcc.jcovey.avoidprojectiles.app;

import edu.mvcc.jcovey.JavaFXWindow;
import java.util.function.Consumer;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Window that allows the user to edit persistent preferences.
 *
 * @author Jason A. Covey
 */
public class PreferencesWindow extends JavaFXWindow {
    private final Stage owner;
    private final UserPreferences preferences;
    private final Consumer<UserPreferences> saveHandler;

    /**
     * Creates the preferences window.
     *
     * @param owner the owning stage
     * @param preferences the current preferences
     * @param saveHandler callback invoked when the user saves
     */
    public PreferencesWindow(Stage owner, UserPreferences preferences, Consumer<UserPreferences> saveHandler) {
        this.owner = owner;
        this.preferences = preferences;
        this.saveHandler = saveHandler;
    }

    @Override
    protected String getStageTitle() {
        return "Preferences";
    }

    @Override
    protected void configureStage(Stage stage) {
        stage.initOwner(owner);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setResizable(false);
    }

    @Override
    protected void configureController(Stage stage, Object controllerInstance) {
        PreferencesWindowController controller = (PreferencesWindowController) controllerInstance;
        controller.configure(preferences, saveHandler);
    }

    @Override
    protected boolean isBlockingWindow() {
        return true;
    }
}
