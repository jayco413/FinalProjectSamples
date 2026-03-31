package edu.mvcc.jcovey.AvoidProjectiles;

import java.util.ArrayList;

import javafx.scene.image.ImageView;

public class Bullet extends Projectile {

	public Bullet() {
		super();
	}

	@Override
	protected double getProjectileWidth() {
		return 30;
	}

	@Override
	protected int getScoreIfCollides(ArrayList<Projectile> projectilesInEffect) {
		return isMarioInvincible(projectilesInEffect) ? 0 : -1;
	}

	@Override
	protected int getScoreIfOffScreen() {
		return 1;
	}

	@Override
	protected String getImagePath() {
		return "bulletbill.png";
	}

	@Override
	protected String getSoundPath() {
		return "whoa.mp3";
	}

	@Override
	protected void beginCollisionEffectConcrete(ArrayList<Projectile> projectilesInEffect, ImageView ivMario) {
		// EMPTY
	}

	@Override
	protected boolean shouldPlaySound(ArrayList<Projectile> projectilesInEffect) {
		return !isMarioInvincible(projectilesInEffect);
	}

	@Override
	protected boolean shouldInitiateEffects(ArrayList<Projectile> projectilesInEffect) {
		return !isMarioInvincible(projectilesInEffect);
	}

	@Override
	protected boolean isEffectExpired() {
		return true;
	}

	@Override
	protected void iterateCollisionEffectConcrete(ArrayList<Projectile> projectilesInEffect, ImageView ivMario) {
		// EMPTY
	}

	@Override
	protected void endCollisionEffectConcrete(ArrayList<Projectile> projectilesInEffect, ImageView ivMario) {
		// EMPTY
	}


}