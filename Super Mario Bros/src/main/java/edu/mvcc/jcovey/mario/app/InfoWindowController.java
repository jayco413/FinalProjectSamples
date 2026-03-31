package edu.mvcc.jcovey.mario.app;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/**
 * Controller for read-only informational windows.
 *
 * @author Jason A. Covey
 */
public class InfoWindowController {
    @FXML
    private Label headingLabel;

    @FXML
    private TextArea bodyTextArea;

    /**
     * Populates the informational window.
     *
     * @param title the heading text
     * @param body the body text
     */
    public void configure(String title, String body) {
        headingLabel.setText(title);
        bodyTextArea.setText(body);
        bodyTextArea.positionCaret(0);
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) bodyTextArea.getScene().getWindow();
        stage.close();
    }
}
