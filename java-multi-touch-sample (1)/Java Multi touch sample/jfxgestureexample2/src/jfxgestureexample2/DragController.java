package jfxgestureexample2;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.TouchEvent;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

public class DragController extends Circle {

    public Circle TargetCircleTM;
    public Circle TargetCircleCL;
    public Circle TargetCircleTR;
    public Circle TargetCircleBL;
    public Circle TargetCircleBM;
    public Circle TargetCircleBR;
    public Circle TargetCircleTL;
    public Circle TargetCircleCR;
    public Circle SourceCircle;
    public Button generateTestBtn;

    private Timeline timeline;
    private Label timerLabel = new Label(), splitTimerLabel = new Label();
    private DoubleProperty timeSeconds = new SimpleDoubleProperty(),
            splitTimeSeconds = new SimpleDoubleProperty(),
            lastSplit = new SimpleDoubleProperty();
    private Duration time = Duration.ZERO, splitTime = Duration.ZERO;
    public boolean isBracedPosition = false;

    private boolean moveInProgress = false;
    private int touchPointId;
    private Point2D prevPos;

    int touchId = -1;

    private double newY, newX = 0;
    private List<Circle> targetList = new ArrayList<Circle>();





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
            SourceCircle.setStyle("-fx-fill: green");
        } else if (!isBracedPosition) {
            SourceCircle.setStyle("-fx-fill: blue");
        }

        SourceCircle.setOnTouchPressed(new EventHandler<TouchEvent>() {
            @Override
            public void handle(TouchEvent event) {
                if (touchId == -1) {
                    touchId = event.getTouchPoint().getId();
                    newX = event.getTouchPoint().getSceneX() - SourceCircle.getTranslateX();
                    newY = event.getTouchPoint().getSceneY() - SourceCircle.getTranslateY();
                }
                event.consume();
            }
        });

        generateTestBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                targetList.add(TargetCircleTM);
                targetList.add(TargetCircleCL);
                targetList.add(TargetCircleTR);
                targetList.add(TargetCircleBL);
                targetList.add(TargetCircleBM);
                targetList.add(TargetCircleBR);
                targetList.add(TargetCircleTL);
                targetList.add(TargetCircleCR);

                Collections.shuffle(targetList, new Random());
                System.out.println(targetList.get(0));
                Circle chosen_target = targetList.get(0);
                chosen_target.setStyle("-fx-fill: red");
            }
        });

        SourceCircle.setOnTouchMoved(new EventHandler<TouchEvent>() {
            @Override public void handle(TouchEvent event) {
                Circle chosen_target = targetList.get(0);
                if (event.getTouchPoint().getId() == touchId) {
                    SourceCircle.setTranslateX(event.getTouchPoint().getSceneX() - newX);
                    SourceCircle.setTranslateY(event.getTouchPoint().getSceneY() - newY);
                    Double SourceCoordsX = SourceCircle.getTranslateX() + SourceCircle.getLayoutX();
                    Double SourceCoordsY = SourceCircle.getTranslateY() + SourceCircle.getLayoutY();
                     if ((SourceCoordsX - chosen_target.getLayoutX() < 10 &&
                     SourceCoordsX - chosen_target.getLayoutX() > -10) &&
                     (SourceCoordsY - chosen_target.getLayoutY() < 10 &&
                     SourceCoordsY - chosen_target.getLayoutY() > -10)&& isBracedPosition) {
                     chosen_target.setStyle("-fx-fill: green");
                         System.out.println("Source Coords: " + (SourceCircle.getTranslateX() + SourceCircle.getLayoutX()) + "," + (SourceCircle.getTranslateY() + SourceCircle.getLayoutY()));
                         System.out.println("Target Coords: " +chosen_target.getLayoutX() + "," + chosen_target.getLayoutY());
                     }
                }
                event.consume();
            }
        });


        SourceCircle.setOnTouchReleased(new EventHandler<TouchEvent>() {
            @Override
            public void handle(TouchEvent event) {
                if (event.getTouchPoint().getId() == touchId) {
                    touchId = -1;
                }
                event.consume();
            }
        });



    }




}