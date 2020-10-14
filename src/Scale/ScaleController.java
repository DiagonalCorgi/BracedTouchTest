package Scale;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.TouchEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.shape.Arc;
import javafx.util.Duration;

import java.text.DecimalFormat;
import java.util.logging.Level;

public class ScaleController extends Arc {



    public Button rBtn;
    public Label sourceScale;
    public Label targetScale;
    private Timeline timeline;
    private Label timerLabel = new Label(), splitTimerLabel = new Label();
    private DoubleProperty timeSeconds = new SimpleDoubleProperty(),
            splitTimeSeconds = new SimpleDoubleProperty(),
            lastSplit = new SimpleDoubleProperty();
    private Duration time = Duration.ZERO, splitTime = Duration.ZERO;
    public boolean isBracedPosition = false;

    DecimalFormat df = new DecimalFormat("#.0");


    @FXML
    public Arc source;

    @FXML
    public Arc target;
    

    private boolean moveInProgress = false;
    private int touchPointId;
    private Point2D prevPos;

    /**
     * Initiates the braced detection engine, tests for the touch point counts and
     * sets the timer for braced detection.
     * @param t
     */

    public void onTouchPressed(TouchEvent t) {

        moveInProgress = true;
        touchPointId = t.getTouchPoint().getId();

        if (t.getTouchCount() < 5) {
            isBracedPosition = false;
        }
        else if (t.getTouchCount() >= 4) {
            if (timeline != null) {
                splitTime = Duration.ZERO;
                splitTimeSeconds.set(splitTime.toSeconds());
            } else {
                timeline = new Timeline(
                        new KeyFrame(Duration.millis(100),
                                new EventHandler<ActionEvent>() {
                                    @Override
                                    public void handle(ActionEvent t) {
                                        Duration duration = ((KeyFrame)t.getSource()).getTime();
                                        time = time.add(duration);
                                        splitTime = splitTime.add(duration);
                                        timeSeconds.set(time.toSeconds());
                                        splitTimeSeconds.set(splitTime.toSeconds());
                                        lastSplit.set(splitTime.toSeconds());
                                    }
                                })
                );
                timeline.setCycleCount(Timeline.INDEFINITE);
                timeline.play();
            }
            ScaleLogger.log(Level.INFO, "5th Finger Pressed ");
            ScaleLogger.log(Level.INFO, "Time of tap:" + lastSplit);
            if (lastSplit.getValue() < 0.5 && !isBracedPosition && t.getTouchCount() >= 5) {
                isBracedPosition = true;
                getStyleClass().clear();
                getStyleClass().add("mainFxmlClassBracedSelected");
            } else if (lastSplit.getValue() > 0.5) {
                isBracedPosition = false;
            }
            ScaleLogger.log(Level.INFO, "Braced is " + isBracedPosition);
        }

        t.consume();

        if (isBracedPosition == true) {
            source.setStyle("-fx-fill: green");
        } else if (!isBracedPosition) {
            source.setStyle("-fx-fill: blue");
        }


    }

    /**
     * Handler for the button. Will randomize the scale and output to log.
     * @param E
     */
     public void handle(ActionEvent E) {
         double max = 1.5;
         double min = 0.5;
         double scale = Math.random() * ((max - min) + 1) + min;
        target.setScaleX(scale);
        target.setScaleY(scale);
        targetScale.setStyle("-fx-fill: red");
        double target_scale = target.getScaleX();
        targetScale.setText(String.valueOf(df.format(target_scale)));
        ScaleLogger.log(Level.INFO, "Scale of target is " + target.getScaleX());

    }
    /**
     * The scale event. will scale the arc if the user is in a braced position.
     * Also changes the target's colour to green upon success.
     * @param t the Event itself.
     */

    public void setOnZoom(ZoomEvent t) {
         if (isBracedPosition) {
             source.setScaleX(source.getScaleX() * t.getZoomFactor());
             source.setScaleY(source.getScaleY() * t.getZoomFactor());
             sourceScale.setText(String.valueOf(df.format(source.getScaleX())));
             double comparison = source.getScaleX() - target.getScaleX();
             if (
                     comparison > -0.05 &&
                             comparison < 0.05 &&
                             isBracedPosition)
             {
                 ScaleLogger.log(Level.INFO, "SUCCESS");
                 target.setStyle("-fx-fill: green");
             } else {
                 target.setStyle("-fx-fill: red");
             }
             t.consume();
         }
     }

}



