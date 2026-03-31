package edu.mvcc.jcovey.AvoidProjectiles;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class ImageViewScroller {
    
    private ImageView originalImageView;
    private ImageView copiedImageView;

    public ImageViewScroller(ImageView original) {
        this.originalImageView = original;
        
        // Creating deep copy
        this.copiedImageView = new ImageView(original.getImage());
        
        // Ensure copiedImageView has the same z-index as originalImageView
        Pane parentPane = (Pane) original.getParent();
        ObservableList<Node> children = parentPane.getChildren();
        int originalIndex = children.indexOf(originalImageView);
        children.add(originalIndex + 1, copiedImageView); // Add right after the original

        resetCopyPosition();
    }

    public void scroll(double value) {
        originalImageView.setLayoutX(originalImageView.getLayoutX() - value);
        copiedImageView.setLayoutX(copiedImageView.getLayoutX() - value);

        if (originalImageView.getLayoutX() + originalImageView.getLayoutBounds().getWidth() <= 0) {
            originalImageView.setLayoutX(copiedImageView.getLayoutX() + copiedImageView.getLayoutBounds().getWidth());
        }
        
        if (copiedImageView.getLayoutX() + copiedImageView.getLayoutBounds().getWidth() <= 0) {
            copiedImageView.setLayoutX(originalImageView.getLayoutX() + originalImageView.getLayoutBounds().getWidth());
        }
    }

    private void resetCopyPosition() {
        copiedImageView.setLayoutX(originalImageView.getLayoutX() + originalImageView.getLayoutBounds().getWidth());
    }
}