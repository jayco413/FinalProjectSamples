package edu.mvcc.jcovey.AvoidProjectiles;

import java.util.ArrayList;
import java.util.Stack;

import javafx.scene.image.ImageView;

public class MiniMushroom extends Projectile {

	private double previousWidth;
	private static Stack<MiniMushroom> inEffect = new Stack<>();
	int timer;
	
	public MiniMushroom() {
		super();
	}

	@Override
	protected double getProjectileWidth() {
		// TODO Auto-generated method stub
		return 30;
	}

	@Override
	protected int getScoreIfCollides(ArrayList<Projectile> projectilesInEffect) {
		// TODO Auto-generated method stub
		return 5;
	}

	@Override
	protected int getScoreIfOffScreen() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected String getImagePath() {
		return "minimushroom.png";
	}

	@Override
	protected String getSoundPath() {
		return "smb_pipe.mp3";
	}

	@Override
	protected boolean shouldInitiateEffects(ArrayList<Projectile> projectilesInEffect) {
		return true;
	}

	@Override
	protected void beginCollisionEffectConcrete(ArrayList<Projectile> projectilesInEffect, ImageView ivMario) {
		timer = 1200;
		previousWidth = ivMario.getFitWidth();
		ivMario.setFitWidth(ivMario.getFitWidth() * 0.9);
		inEffect.push(this);
	}

	@Override
	protected boolean shouldPlaySound(ArrayList<Projectile> projectilesInEffect) {
		return true;
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
		MiniMushroom popShroom = inEffect.pop();
		ivMario.setFitWidth(popShroom.getPreviousWidth());
	}

	private double getPreviousWidth() {
		return previousWidth;
	}

}
