package Archived;/*
Copyright (c) <2013>, Intel Corporation All Rights Reserved.
 
The source code, information and material ("Material") contained herein is owned by Intel Corporation or its suppliers or licensors, and title to such Material remains with Intel Corporation
or its suppliers or licensors. The Material contains proprietary information of Intel or its suppliers and licensors. The Material is protected by worldwide copyright laws and treaty provisions. 
No part of the Material may be used, copied, reproduced, modified, published, uploaded, posted, transmitted, distributed or disclosed in any way without Intel's prior express written permission. 
No license under any patent, copyright or other intellectual property rights in the Material is granted to or conferred upon you, either expressly, by implication, inducement, estoppel or otherwise. 
Any license under such intellectual property rights must be express and approved by Intel in writing.
 
Unless otherwise agreed by Intel in writing, you may not remove or alter this notice or any other notice embedded in Materials by Intel or Intel’s suppliers or licensors in any way.
*/

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.SwipeEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.io.IOException;
import java.util.logging.Level;

public class MovableElementController extends Pane implements ISelectableItem {

	private ISelectableItemContainer m_container;
	private Timeline timeline;
	private Label timerLabel = new Label(), splitTimerLabel = new Label();
	private DoubleProperty timeSeconds = new SimpleDoubleProperty(),
			splitTimeSeconds = new SimpleDoubleProperty(),
			lastSplit = new SimpleDoubleProperty();
	private Duration time = Duration.ZERO, splitTime = Duration.ZERO;
	public boolean isBracedPosition = false;


	@FXML
	private Pane dragPane;


	public MovableElementController(ISelectableItemContainer container){

		super();
		m_container = container;

		FXMLLoader loader = new FXMLLoader(getClass().getResource("MovableElement.fxml"));
		loader.setRoot(this);
		loader.setController(this);

		try {
			loader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	@Override
	public void onUnselected() {
		getStyleClass().clear();
		getStyleClass().add("mainFxmlClassUnselected");
	}

	@Override
	public void onSelected() {
		getStyleClass().clear();
		getStyleClass().add("mainFxmlClassSelected");
	}

	private boolean moveInProgress = false;
	private int touchPointId;
	private Point2D prevPos;


	public void onTouchPressed(TouchEvent t) {
		if (moveInProgress == false) {
			if (m_container.getRegisterredItem() != MovableElementController.this) {
				m_container.unregisterItem();
				m_container.registerItem(MovableElementController.this);
			}

			moveInProgress = true;
			touchPointId = t.getTouchPoint().getId();

			prevPos = new Point2D(t.getTouchPoint().getSceneX(),
				t.getTouchPoint().getSceneY());
			System.out.println("TOUCH BEGIN " + t.toString());
		}
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


			InteractionLogger.log(Level.INFO, "5th finger pressed ID:" + t.getTouchPoint().getId() + "\n" +
					"Location:" + t.getTouchPoint().getSceneX() + "," + t.getTouchPoint().getSceneY());
			InteractionLogger.log(Level.INFO, "Time of tap:" + lastSplit);
			if (lastSplit.getValue() < 0.5 && !isBracedPosition) {
				isBracedPosition = true;
				InteractionLogger.log(Level.INFO, "Braced is " + isBracedPosition);
				getStyleClass().clear();
				getStyleClass().add("mainFxmlClassBracedSelected");
			} else if (lastSplit.getValue() > 0.5) {
				isBracedPosition = false;
			}
		}

		else {
			isBracedPosition = false;
			getStyleClass().clear();
			getStyleClass().add("mainFxmlClassSelected");
		}



		t.consume();
	}


	public void onTouchMoved(TouchEvent t) {
		if (moveInProgress == true && t.getTouchPoint().getId() == touchPointId) {
			//this part should be oprimized in a praoduction code but here in order to present the steps i took a more verbose approach 
			Point2D currPos = new Point2D(t.getTouchPoint().getSceneX(), t.getTouchPoint().getSceneY());
			double[] translationVector = new double[2];
			translationVector[0] = currPos.getX() - prevPos.getX();
			translationVector[1] = currPos.getY() - prevPos.getY();

			//i used this instead of setTranslate* because we don't care about the original position of the object and aggregating translation
			//will require having another variable
			setTranslateX(getTranslateX() + translationVector[0]);
			setTranslateY(getTranslateY() + translationVector[1]);

			prevPos = currPos;

		}
		t.consume();
	}

	public void onTouchReleased(TouchEvent t) {
		if (t.getTouchPoint().getId() == touchPointId) {
			moveInProgress = false;
			System.err.println("TOUCH RELEASED " + t.toString());
		}

		t.consume();
	}

	@FXML
	public void onScroll(ScrollEvent t) {
		t.consume();
	}


	@FXML
	public void onSwipe(SwipeEvent t) {
		t.consume();
	}

	@Override
	public Node getCorrespondingNode() {
		return this;
	}
}
