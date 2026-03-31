package edu.mvcc.jcovey.avoidprojectiles.app;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller for the modal initials-entry form.
 *
 * <p>AI-assisted implementation reviewed and integrated by Jason A. Covey.</p>
 *
 * @author Jason A. Covey
 */
public class HighScoreEntryWindowController {
    @FXML
    private Label timeLabel;

    @FXML
    private TextField initialsField;

    @FXML
    private Label validationLabel;

    private String submittedInitials = "";

    /**
     * Populates the qualifying run time shown to the player.
     *
     * @param formattedTime human-readable completion time
     */
    public void configure(String formattedTime) {
        timeLabel.setText("Top 10 time: " + formattedTime);
        initialsField.setText("");
        validationLabel.setText("");
        initialsField.requestFocus();
    }

    /**
     * Gets the initials submitted by the player.
     *
     * @return sanitized initials, or an empty string when cancelled
     */
    public String getSubmittedInitials() {
        return submittedInitials;
    }

    @FXML
    private void handleSave() {
        String sanitized = HighScoreStore.sanitizeInitials(initialsField.getText());
        if (sanitized.isBlank()) {
            validationLabel.setText("Enter 1 to 3 letters or digits.");
            return;
        }
        submittedInitials = sanitized;
        closeWindow();
    }

    @FXML
    private void handleCancel() {
        submittedInitials = "";
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) initialsField.getScene().getWindow();
        stage.close();
    }
}
