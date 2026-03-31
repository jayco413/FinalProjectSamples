package edu.mvcc.jcovey.AvoidProjectiles;

public class AvoidProjectilesWindow extends JavaFXWindow {

	@Override
	protected String getStageTitle() {
		return "Avoid Projectiles Game";
	}

	public static void main(String[] args) {
		AvoidProjectilesWindow hww = new AvoidProjectilesWindow();
		hww.runAsStartUpWindow();
	}
}
