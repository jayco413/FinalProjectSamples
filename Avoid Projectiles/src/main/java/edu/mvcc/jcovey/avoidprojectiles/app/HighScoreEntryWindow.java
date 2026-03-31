package edu.mvcc.jcovey.avoidprojectiles.app;

import edu.mvcc.jcovey.JavaFXWindow;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Modal form for entering initials after a top-10 run.
 *
 * <p>AI-assisted implementation reviewed and integrated by Jason A. Covey.</p>
 *
 * @author Jason A. Covey
 */
public class HighScoreEntryWindow extends JavaFXWindow {
    private final Stage owner;
    private final double elapsedSeconds;
    private HighScoreEntryWindowController controller;

    /**
     * Creates the initials-entry window.
     *
     * @param owner the owning stage
     * @param elapsedSeconds the qualifying completion time
     */
    public HighScoreEntryWindow(Stage owner, double elapsedSeconds) {
        this.owner = owner;
        this.elapsedSeconds = elapsedSeconds;
    }

    @Override
    protected String getStageTitle() {
        return "New High Score";
    }

    @Override
    protected void configureStage(Stage stage) {
        stage.initOwner(owner);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setResizable(false);
    }

    @Override
    protected void configureController(Stage stage, Object controllerInstance) {
        controller = (HighScoreEntryWindowController) controllerInstance;
        controller.configure(HighScoreStore.formatForDisplay(elapsedSeconds));
    }

    @Override
    protected boolean isBlockingWindow() {
        return true;
    }

    /**
     * Gets the submitted initials after the modal dialog closes.
     *
     * @return sanitized initials, or an empty string when cancelled
     */
    public String getSubmittedInitials() {
        if (controller == null) {
            return "";
        }
        return controller.getSubmittedInitials();
    }
}
