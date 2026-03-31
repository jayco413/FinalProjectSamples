package edu.mvcc.jcovey.AvoidProjectiles;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.util.Duration;

/**
 * Abstract class for managing timeline-based animations in JavaFX.
 * Subclasses need to provide concrete implementations for the methods
 * {@link #handleTimerIteration()} and {@link #initializeConcrete()}.
 * 
 * @author Jason A. Covey
 */
public abstract class TimelineAnimationController {

    /**
     * Initializes the animation. This method gets automatically called when the 
     * FXML document associated with this class is loaded.
     * It initializes the concrete part of the subclass and then creates and 
     * starts the timeline animation which calls {@link #handleTimerIteration()} 
     * at every tick.
     */
    @FXML
    void initialize() {
        // Initialize the concrete part as defined by subclasses
        initializeConcrete();

        // Create a new Timeline animation with a duration of 10ms for each tick
        Timeline timelineAnimation = new Timeline(new KeyFrame(Duration.millis(10),
            new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {
                    handleTimerIteration();
                }

            }));

        // Set the cycle count to indefinite, meaning the timeline will loop forever
        timelineAnimation.setCycleCount(Timeline.INDEFINITE);

        // Start the animation
        timelineAnimation.play();
    }

    /**
     * This method gets called at every tick of the Timeline animation. Subclasses
     * need to provide a concrete implementation detailing what needs to happen 
     * at each tick.
     */
    protected abstract void handleTimerIteration();

    /**
     * This method should initialize any concrete parts that the subclass needs. 
     * It's called at the beginning of the {@link #initialize()} method.
     */
    protected abstract void initializeConcrete();
}
