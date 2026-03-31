package edu.mvcc.jcovey.AvoidProjectiles;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.security.SecureRandom;
import java.util.ArrayList;

public abstract class Projectile extends ImageView {

    private int speed;
    
    private static final SecureRandom random = new SecureRandom();
    private SoundEffectPlayer soundEffectPlayer;
	
    public Projectile() {

        setImage(new Image(getResourcePath(getImagePath(), true)));
        soundEffectPlayer = new SoundEffectPlayer(getResourcePath(getSoundPath(), false));
        
        setPreserveRatio(true);
        setPickOnBounds(true);
        setFitWidth(getProjectileWidth());
    }
    
    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
   
    private String getResourcePath(String localPath, boolean isImage) {
        if (isImage) {
            localPath = "resources/images/" + localPath;
        } else {
            localPath = "resources/media/" + localPath;
        }
        return Projectile.class.getResource(localPath).toString();
    }

    protected abstract String getImagePath();

    protected abstract double getProjectileWidth();

    protected abstract int getScoreIfCollides(ArrayList<Projectile> projectilesInEffect);

    protected abstract int getScoreIfOffScreen();
    
    protected abstract String getSoundPath();

    // Static method to stop all media players
    public static void stopAllSounds() {
        SoundEffectPlayer.stopAllSounds();
    }

	public void beginCollisionEffect(ArrayList<Projectile> projectilesInEffect, ImageView ivMario) {		
		if (shouldInitiateEffects(projectilesInEffect)) {
			soundEffectPlayer.play();
			beginCollisionEffectConcrete(projectilesInEffect, ivMario);
			projectilesInEffect.add(this);
		}
	}

	protected abstract boolean shouldInitiateEffects(ArrayList<Projectile> projectilesInEffect);

	protected abstract void beginCollisionEffectConcrete(ArrayList<Projectile> projectilesInEffect, ImageView ivMario);

	protected abstract boolean shouldPlaySound(ArrayList<Projectile> projectilesInEffect);
	
	protected boolean isMarioInvincible(ArrayList<Projectile> projectilesInEffect) {
		boolean invincible = false;
		
		for (Projectile p : projectilesInEffect) {
			if (p instanceof Starman) {
				invincible = true;
				break;
			}
		}
		return invincible;
	}

	protected abstract boolean isEffectExpired();

	public void iterateCollisionEffect(ArrayList<Projectile> projectilesInEffect, ImageView ivMario) {
		if (isEffectExpired()) {
			soundEffectPlayer.stop();
			endCollisionEffectConcrete(projectilesInEffect, ivMario);
			projectilesInEffect.remove(this);
		} else {
			iterateCollisionEffectConcrete(projectilesInEffect, ivMario);
		}
	}

	protected abstract void iterateCollisionEffectConcrete(ArrayList<Projectile> projectilesInEffect, ImageView ivMario);

	protected abstract void endCollisionEffectConcrete(ArrayList<Projectile> projectilesInEffect, ImageView ivMario);

	public static Projectile getRandomProjectile() {
		// 30% chance of anything at all
		if (random.nextInt(10) < 3) {
			// 10% chance of starman
			if (random.nextInt(10) == 1) {
				return new Starman();
			} else  if (random.nextInt(10) < 2) {
				return new MiniMushroom();
			} else {
				return new Bullet();
			}
		}
		return null;
	}

	public void setUp(int xRand, int xAdder, int yRand, int speedRand, int speedAdder) {
		setLayoutX(random.nextInt(xRand) + xAdder);
		setLayoutY(random.nextInt(yRand));
		setSpeed(random.nextInt(speedRand) + speedAdder);
	}
}
