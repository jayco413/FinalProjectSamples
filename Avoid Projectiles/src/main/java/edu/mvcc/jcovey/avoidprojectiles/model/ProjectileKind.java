package edu.mvcc.jcovey.avoidprojectiles.model;

/**
 * Types of moving objects that can appear in the game.
 *
 * @author Jason A. Covey
 */
public enum ProjectileKind {
    BULLET("bulletbill.png", "whoa.mp3", 30.0),
    STARMAN("starman.png", "starmantheme.mp3", 30.0),
    MINI_MUSHROOM("minimushroom.png", "smb_pipe.mp3", 30.0);

    private final String imageName;
    private final String soundName;
    private final double width;

    ProjectileKind(String imageName, String soundName, double width) {
        this.imageName = imageName;
        this.soundName = soundName;
        this.width = width;
    }

    /**
     * Gets the image filename for the projectile.
     *
     * @return the image filename
     */
    public String getImageName() {
        return imageName;
    }

    /**
     * Gets the sound filename for the projectile.
     *
     * @return the sound filename
     */
    public String getSoundName() {
        return soundName;
    }

    /**
     * Gets the projectile width.
     *
     * @return width in pixels
     */
    public double getWidth() {
        return width;
    }
}
