package edu.mvcc.jcovey.avoidprojectiles.app;

import edu.mvcc.jcovey.JavaFXWindow;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Window that displays the current top 10 completion times.
 *
 * <p>AI-assisted implementation reviewed and integrated by Jason A. Covey.</p>
 *
 * @author Jason A. Covey
 */
public class HighScoresWindow extends JavaFXWindow {
    private final Stage owner;
    private final String highScoreText;

    /**
     * Creates the high-scores window.
     *
     * @param owner the owning stage
     * @param highScoreText the formatted leaderboard body text
     */
    public HighScoresWindow(Stage owner, String highScoreText) {
        this.owner = owner;
        this.highScoreText = highScoreText;
    }

    @Override
    protected String getStageTitle() {
        return "High Scores";
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
            "High Scores",
            """
            Fastest runs to 100 points:

            %s
            """.formatted(highScoreText)
        );
    }

    @Override
    protected boolean isBlockingWindow() {
        return true;
    }
}
