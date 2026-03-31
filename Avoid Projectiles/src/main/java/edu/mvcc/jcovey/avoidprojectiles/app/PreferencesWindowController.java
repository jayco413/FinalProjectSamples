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
    private double savedWindowScale;

    /**
     * Initializes the preference form.
     *
     * @param preferences the current preferences
     * @param saveHandler callback invoked when the user saves
     */
    public void configure(UserPreferences preferences, Consumer<UserPreferences> saveHandler) {
        this.saveHandler = saveHandler;
        savedWindowScale = preferences.getWindowScale();
        scaleComboBox.setItems(FXCollections.observableArrayList("Automatic (80% of screen)"));
        difficultyComboBox.setItems(FXCollections.observableArrayList("Relaxed", "Standard", "Chaotic"));
        playerSpeedComboBox.setItems(FXCollections.observableArrayList("Slow", "Normal", "Fast"));

        soundCheckBox.setSelected(preferences.isSoundEnabled());
        scaleComboBox.setValue("Automatic (80% of screen)");
        scaleComboBox.setDisable(true);
        difficultyComboBox.setValue(preferences.getDifficulty());
        playerSpeedComboBox.setValue(preferences.getPlayerSpeed());
    }

    @FXML
    private void handleSave() {
        UserPreferences preferences = new UserPreferences();
        preferences.setSoundEnabled(soundCheckBox.isSelected());
        preferences.setWindowScale(savedWindowScale);
        preferences.setDifficulty(difficultyComboBox.getValue());
        preferences.setPlayerSpeed(playerSpeedComboBox.getValue());
        saveHandler.accept(preferences);
        closeWindow();
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) soundCheckBox.getScene().getWindow();
        stage.close();
    }
}
