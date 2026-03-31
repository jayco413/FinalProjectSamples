package edu.mvcc.jcovey.AvoidProjectiles;

import java.util.ArrayList;

import javafx.scene.image.ImageView;

public class Starman extends Projectile {
	
	public int timer;

	public Starman() {
		super();
	}

	@Override
	protected double getProjectileWidth() {
		return 30;
	}

	@Override
	protected int getScoreIfCollides(ArrayList<Projectile> projectilesInEffect) {
		return 10;
	}

	@Override
	protected int getScoreIfOffScreen() {
		return 0;
	}
	
	@Override
	public int getSpeed() {
		return 7;
	}

	@Override
	protected String getImagePath() {
		return "starman.png";
	}

	@Override
	protected String getSoundPath() {
		return "starmantheme.mp3";
	}

	@Override
	protected void beginCollisionEffectConcrete(ArrayList<Projectile> projectilesInEffect, ImageView ivMario) {
		timer = 1000;
	}

	@Override
	protected boolean shouldPlaySound(ArrayList<Projectile> projectilesInEffect) {
		return true;
	}

	@Override
	protected boolean shouldInitiateEffects(ArrayList<Projectile> projectilesInEffect) {
		return !isMarioInvincible(projectilesInEffect);
	}

	@Override
	protected boolean isEffectExpired() {
		return timer <= 0;
	}

	@Override
	protected void iterateCollisionEffectConcrete(ArrayList<Projectile> projectilesInEffect, ImageView ivMario) {
		timer--;
	}

	@Override
	protected void endCollisionEffectConcrete(ArrayList<Projectile> projectilesInEffect, ImageView ivMario) {
		// NONE
	}


}
