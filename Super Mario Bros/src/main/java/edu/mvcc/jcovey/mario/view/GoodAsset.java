package edu.mvcc.jcovey.mario.view;

import javafx.scene.image.Image;

public class GoodAsset {
    private final String code;
    private final Image image;
    private final int widthUnits;
    private final int heightUnits;

    public GoodAsset(String code, Image image, int widthUnits, int heightUnits) {
        this.code = code;
        this.image = image;
        this.widthUnits = widthUnits;
        this.heightUnits = heightUnits;
    }

    public String getCode() {
        return code;
    }

    public Image getImage() {
        return image;
    }

    public int getWidthUnits() {
        return widthUnits;
    }

    public int getHeightUnits() {
        return heightUnits;
    }
}
