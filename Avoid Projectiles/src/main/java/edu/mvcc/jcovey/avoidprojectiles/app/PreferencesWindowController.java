package edu.mvcc.jcovey.avoidprojectiles.app;

import java.util.function.Consumer;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

/**
 * Controller for the persistent preference editor.
 *
 * @author Jason A. Covey
 */
public class PreferencesWindowController {
    @FXML
    private CheckBox soundCheckBox;

    @FXML
    private ComboBox<String> scaleComboBox;

    @FXML
    private ComboBox<String> difficultyComboBox;

    @FXML
    private ComboBox<String> playerSpeedComboBox;

    private Consumer<UserPreferences> saveHandler;

    /**
     * Initializes the preference form.
     *
     * @param preferences the current preferences
     * @param saveHandler callback invoked when the user saves
     */
    public void configure(UserPreferences preferences, Consumer<UserPreferences> saveHandler) {
        this.saveHandler = saveHandler;
        scaleComboBox.setItems(FXCollections.observableArrayList("100%", "125%", "150%"));
        difficultyComboBox.setItems(FXCollections.observableArrayList("Relaxed", "Standard", "Chaotic"));
        playerSpeedComboBox.setItems(FXCollections.observableArrayList("Slow", "Normal", "Fast"));

        soundCheckBox.setSelected(preferences.isSoundEnabled());
        scaleComboBox.setValue(toScaleLabel(preferences.getWindowScale()));
        difficultyComboBox.setValue(preferences.getDifficulty());
        playerSpeedComboBox.setValue(preferences.getPlayerSpeed());
    }

    @FXML
    private void handleSave() {
        UserPreferences preferences = new UserPreferences();
        preferences.setSoundEnabled(soundCheckBox.isSelected());
        preferences.setWindowScale(parseScale(scaleComboBox.getValue()));
        preferences.setDifficulty(difficultyComboBox.getValue());
        preferences.setPlayerSpeed(playerSpeedComboBox.getValue());
        saveHandler.accept(preferences);
        closeWindow();
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private double parseScale(String scaleLabel) {
        if ("150%".equals(scaleLabel)) {
            return 1.5;
        }
        if ("125%".equals(scaleLabel)) {
            return 1.25;
        }
        return 1.0;
    }

    private String toScaleLabel(double scale) {
        if (scale >= 1.5) {
            return "150%";
        }
        if (scale >= 1.25) {
            return "125%";
        }
        return "100%";
    }

    private void closeWindow() {
        Stage stage = (Stage) soundCheckBox.getScene().getWindow();
        stage.close();
    }
}
