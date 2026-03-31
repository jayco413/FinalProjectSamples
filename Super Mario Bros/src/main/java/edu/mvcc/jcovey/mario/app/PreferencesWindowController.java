package edu.mvcc.jcovey.mario.app;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import java.util.function.Consumer;
import javafx.stage.Stage;

/**
 * Controller for the persistent preference editor.
 *
 * @author Jason A. Covey
 */
public class PreferencesWindowController {
    @FXML
    private CheckBox musicCheckBox;

    @FXML
    private CheckBox soundCheckBox;

    @FXML
    private ComboBox<String> scaleComboBox;

    @FXML
    private RadioButton world11RadioButton;

    @FXML
    private RadioButton world12RadioButton;

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
        musicCheckBox.setSelected(preferences.isMusicEnabled());
        soundCheckBox.setSelected(preferences.isSoundEnabled());
        scaleComboBox.setValue("Automatic (80% of screen)");
        scaleComboBox.setDisable(true);
        if ("1-2".equals(preferences.getStartWorld())) {
            world12RadioButton.setSelected(true);
        } else {
            world11RadioButton.setSelected(true);
        }
    }

    @FXML
    private void handleSave() {
        UserPreferences preferences = new UserPreferences();
        preferences.setMusicEnabled(musicCheckBox.isSelected());
        preferences.setSoundEnabled(soundCheckBox.isSelected());
        preferences.setWindowScale(savedWindowScale);
        preferences.setStartWorld(world12RadioButton.isSelected() ? "1-2" : "1-1");
        saveHandler.accept(preferences);
        closeWindow();
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) musicCheckBox.getScene().getWindow();
        stage.close();
    }
}
