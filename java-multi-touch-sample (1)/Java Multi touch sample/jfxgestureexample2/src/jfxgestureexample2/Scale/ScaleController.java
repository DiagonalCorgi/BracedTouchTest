package jfxgestureexample2.Scale;

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
import javafx.scene.input.ZoomEvent;
import javafx.scene.shape.Arc;
import javafx.util.Duration;
import jfxgestureexample2.InteractionLogger;

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
            InteractionLogger.log(Level.INFO, "5th Finger Pressed ");
            InteractionLogger.log(Level.INFO, "Time of tap:" + lastSplit);
            if (lastSplit.getValue() < 0.5 && !isBracedPosition) {
                isBracedPosition = true;
                getStyleClass().clear();
                getStyleClass().add("mainFxmlClassBracedSelected");
            } else if (lastSplit.getValue() > 0.5) {
                isBracedPosition = false;
            }
            InteractionLogger.log(Level.INFO, "Braced is " + isBracedPosition);
        }

        t.consume();

        if (isBracedPosition == true) {
            source.setStyle("-fx-fill: green");
        } else if (!isBracedPosition) {
            source.setStyle("-fx-fill: blue");
        }


    }

     public void handle(ActionEvent E) {
         double max = 3;
         double min = 0.5;
         double scale = Math.random() * ((max - min) + 1) + min;
        target.setScaleX(scale);
        target.setScaleY(scale);
        double target_scale = target.getScaleX();
        targetScale.setText(String.valueOf(df.format(target_scale)));
        InteractionLogger.log(Level.INFO, "Scale of target is " + target.getScaleX());
    }


    public void setOnZoom(ZoomEvent t) {
        source.setScaleX(source.getScaleX() * t.getZoomFactor());
        source.setScaleY(source.getScaleY() * t.getZoomFactor());
        sourceScale.setText(String.valueOf(df.format(source.getScaleX())));
        double comparison = source.getScaleX() - target.getScaleX();
        if (
                comparison > -0.05 &&
                comparison < 0.05 &&
                isBracedPosition)
        {
            target.setStyle("-fx-fill: green");
        } else {
            target.setStyle("-fx-fill: red");
        }
        t.consume();
    }


}
