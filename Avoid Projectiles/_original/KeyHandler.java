package edu.mvcc.jcovey.AvoidProjectiles;

import java.util.ArrayList;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

/**
 * The KeyHandler class provides functionality to manage and track the active 
 * key states for a given scene. It offers event listening for both key press 
 * and key release actions.
 * 
 * Subclasses are required to define the exact nature of how keys affect the 
 * scene by implementing the abstract methods.
 * 
 * @author Jason A. Covey
 */
public abstract class KeyHandler {

    /** Scene where key events are detected. */
    private Scene scene = null;
    
    /** List of active keys that are currently pressed. */
    private ArrayList<KeyCode> activeKeys;

    /**
     * Sets the specified key as active by adding it to the activeKeys list.
     * 
     * @param kc KeyCode to be marked as active.
     */
    private void setKeyActive(KeyCode kc) {
        if (!activeKeys.contains(kc)) {
            activeKeys.add(kc);
        }
    }

    /**
     * Marks the specified key as inactive by removing it from the activeKeys list.
     * 
     * @param kc KeyCode to be marked as inactive.
     */
    private void setKeyInactive(KeyCode kc) {
        activeKeys.remove(kc);
    }

    /**
     * Determines if the specified key is currently active.
     * 
     * @param kc KeyCode to be checked.
     * @return true if the key is active, false otherwise.
     */
    public boolean isKeyActive(KeyCode kc) {
        return activeKeys.contains(kc);
    }

    /**
     * Determines if both specified keys are currently active.
     * 
     * @param kc1 First KeyCode to be checked.
     * @param kc2 Second KeyCode to be checked.
     * @return true if both keys are active, false otherwise.
     */
    public boolean isKeyComboActive(KeyCode kc1, KeyCode kc2) {
        return isKeyActive(kc1) && isKeyActive(kc2);
    }

    /**
     * Determines if all three specified keys are currently active.
     * 
     * @param kc1 First KeyCode to be checked.
     * @param kc2 Second KeyCode to be checked.
     * @param kc3 Third KeyCode to be checked.
     * @return true if all three keys are active, false otherwise.
     */
    public boolean isKeyComboActive(KeyCode kc1, KeyCode kc2, KeyCode kc3) {
        return isKeyComboActive(kc1, kc2) && isKeyActive(kc3);
    }

    /**
     * Attaches event handlers to the scene for key press and key release.
     * This also triggers the effects that correspond to the active keys.
     */
    public void performKeyEffects() {
        if (scene == null) {
            // Fetch the scene using the abstract method.
            scene = getScene();

            // Initialize active keys list.
            activeKeys = new ArrayList<KeyCode>();

            // Attach event listener for key press.
            scene.setOnKeyPressed(e -> {
                setKeyActive(e.getCode());
            });
            
            // Attach event listener for key release.
            scene.setOnKeyReleased(e -> {
                setKeyInactive(e.getCode());
            });
        }
        // Trigger the effects related to the active keys.
        performKeyEffectsConcrete();
    }

    /**
     * Concrete effects to be executed depending on the active keys.
     * This is an abstract method, so subclasses must provide the actual 
     * implementation detailing how active keys affect the scene.
     */
    protected abstract void performKeyEffectsConcrete();

    /**
     * Fetches the scene associated with this key handler.
     * Subclasses are responsible for providing the exact scene instance.
     * 
     * @return Scene where key events are detected.
     */
    protected abstract Scene getScene();
}
