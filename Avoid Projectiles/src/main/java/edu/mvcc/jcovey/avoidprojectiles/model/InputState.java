package edu.mvcc.jcovey.avoidprojectiles.model;

/**
 * Current directional input for the player.
 *
 * @author Jason A. Covey
 */
public class InputState {
    private boolean upPressed;
    private boolean downPressed;
    private boolean leftPressed;
    private boolean rightPressed;

    /**
     * Checks whether upward movement is active.
     *
     * @return true when the up key is pressed
     */
    public boolean isUpPressed() {
        return upPressed;
    }

    /**
     * Updates the upward movement state.
     *
     * @param upPressed true when the up key is pressed
     */
    public void setUpPressed(boolean upPressed) {
        this.upPressed = upPressed;
    }

    /**
     * Checks whether downward movement is active.
     *
     * @return true when the down key is pressed
     */
    public boolean isDownPressed() {
        return downPressed;
    }

    /**
     * Updates the downward movement state.
     *
     * @param downPressed true when the down key is pressed
     */
    public void setDownPressed(boolean downPressed) {
        this.downPressed = downPressed;
    }

    /**
     * Checks whether leftward movement is active.
     *
     * @return true when the left key is pressed
     */
    public boolean isLeftPressed() {
        return leftPressed;
    }

    /**
     * Updates the leftward movement state.
     *
     * @param leftPressed true when the left key is pressed
     */
    public void setLeftPressed(boolean leftPressed) {
        this.leftPressed = leftPressed;
    }

    /**
     * Checks whether rightward movement is active.
     *
     * @return true when the right key is pressed
     */
    public boolean isRightPressed() {
        return rightPressed;
    }

    /**
     * Updates the rightward movement state.
     *
     * @param rightPressed true when the right key is pressed
     */
    public void setRightPressed(boolean rightPressed) {
        this.rightPressed = rightPressed;
    }
}
