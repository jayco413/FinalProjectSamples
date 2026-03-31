package edu.mvcc.jcovey.AvoidProjectiles;

import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;

public class MarioKeyHandler extends KeyHandler {

	Pane pnBackground;
	ImageView ivMario;
	
	public MarioKeyHandler(Pane pnBackground, ImageView ivMario) {
		super();

		this.pnBackground = pnBackground;
		this.ivMario = ivMario;
	}

	@Override
	public void performKeyEffectsConcrete() {

		if (isKeyActive(KeyCode.UP)) {
			if (pnBackground.getLayoutY() <= ivMario.getLayoutY()) {
				ivMario.setLayoutY(ivMario.getLayoutY() - 2);
			}
		}
		
		if (isKeyActive(KeyCode.DOWN)) {
			if (pnBackground.getLayoutY() + pnBackground.getHeight() >= ivMario.getLayoutY() + ivMario.getLayoutBounds().getHeight()) {
				ivMario.setLayoutY(ivMario.getLayoutY() + 2);
			}
		} 
		
		if (isKeyActive(KeyCode.LEFT)) {
			if (pnBackground.getLayoutX() <= ivMario.getLayoutX()) {
				ivMario.setLayoutX(ivMario.getLayoutX() - 2);
			}
		} 
		
		if (isKeyActive(KeyCode.RIGHT)) {
			if (pnBackground.getLayoutX() + pnBackground.getWidth() >= ivMario.getLayoutX() + ivMario.getLayoutBounds().getWidth()) {
				ivMario.setLayoutX(ivMario.getLayoutX() + 2);
			}
		}
	}

	@Override
	protected Scene getScene() {
		return ivMario.getScene();
	}
}
