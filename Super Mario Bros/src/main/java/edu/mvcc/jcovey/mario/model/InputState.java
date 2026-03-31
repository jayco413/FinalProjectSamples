package edu.mvcc.jcovey.mario.model;

public class InputState {
    private boolean leftPressed;
    private boolean rightPressed;
    private boolean downPressed;
    private boolean jumpPressed;
    private boolean runPressed;
    private boolean jumpConsumed;
    private boolean runConsumed;

    public boolean isLeftPressed() {
        return leftPressed;
    }

    public void setLeftPressed(boolean leftPressed) {
        this.leftPressed = leftPressed;
    }

    public boolean isRightPressed() {
        return rightPressed;
    }

    public void setRightPressed(boolean rightPressed) {
        this.rightPressed = rightPressed;
    }

    public boolean isJumpPressed() {
        return jumpPressed;
    }

    public boolean isDownPressed() {
        return downPressed;
    }

    public void setDownPressed(boolean downPressed) {
        this.downPressed = downPressed;
    }

    public void setJumpPressed(boolean jumpPressed) {
        this.jumpPressed = jumpPressed;
        if (!jumpPressed) {
            jumpConsumed = false;
        }
    }

    public boolean isRunPressed() {
        return runPressed;
    }

    public void setRunPressed(boolean runPressed) {
        this.runPressed = runPressed;
        if (!runPressed) {
            runConsumed = false;
        }
    }

    public boolean consumeJumpPress() {
        if (!jumpPressed || jumpConsumed) {
            return false;
        }
        jumpConsumed = true;
        return true;
    }

    public boolean consumeRunPress() {
        if (!runPressed || runConsumed) {
            return false;
        }
        runConsumed = true;
        return true;
    }
}
