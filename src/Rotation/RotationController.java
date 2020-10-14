package Rotation;

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
import javafx.scene.input.RotateEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.shape.Arc;
import javafx.util.Duration;

import java.text.DecimalFormat;
import java.util.logging.Level;

public class RotationController extends Arc {



    public Button rBtn;
    public Label sourceDegrees;
    public Label targetDegrees;
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

    public void onTouchPressed(TouchEvent t) {

        moveInProgress = true;
        touchPointId = t.getTouchPoint().getId();

        if (t.getTouchCount() >= 5) {
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
            RotationLogger.log(Level.INFO, "5th Finger Pressed ");
            RotationLogger.log(Level.INFO, "Time of tap:" + lastSplit);
            if (lastSplit.getValue() < 0.5 && !isBracedPosition) {
                isBracedPosition = true;
                getStyleClass().clear();
                getStyleClass().add("mainFxmlClassBracedSelected");
            } else if (lastSplit.getValue() > 0.5) {
                isBracedPosition = false;
            }
            RotationLogger.log(Level.INFO, "Braced is " + isBracedPosition);
        }

        t.consume();

        if (isBracedPosition == true) {
            source.setStyle("-fx-fill: green");
        } else if (!isBracedPosition) {
            source.setStyle("-fx-fill: blue");
        }


    }

     public void handle(ActionEvent E) {
         double max = 180.0;
         double min = -180.0;
         double angle = Math.random() * ((max - min) + 1) + min;
        target.setRotate(angle);
        target.setStyle("-fx-fill: red");
        double target_angle = target.getRotate();
        targetDegrees.setText(String.valueOf(df.format(target_angle)));
        RotationLogger.log(Level.INFO, "Angle of target is " + target.getRotate());
    }

    public void setOnRotate(RotateEvent E) {
        source.setOnRotate(new EventHandler<RotateEvent>() {
            @Override public void handle(RotateEvent event) {
                source.setRotate(source.getRotate() + event.getAngle());
                double source_angle = source.getRotate();
                RotationLogger.log(Level.INFO, "Rectangle: Rotate Angle " + (source_angle));
                double angle_checker = source_angle - target.getRotate();
                if (angle_checker < 5 && angle_checker >-5 && isBracedPosition) {
                    RotationLogger.log(Level.INFO, "SUCCESS");
                    target.setStyle("-fx-fill: green");
                }
                else {
                    target.setStyle("-fx-fill: red");
                }
                sourceDegrees.setText(String.valueOf(df.format(source_angle)));
                event.consume();
            }
        });

    }


}
