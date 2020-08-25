/*
Copyright (c) <2013>, Intel Corporation All Rights Reserved.
 
The source code, information and material ("Material") contained herein is owned by Intel Corporation or its suppliers or licensors, and title to such Material remains with Intel Corporation
or its suppliers or licensors. The Material contains proprietary information of Intel or its suppliers and licensors. The Material is protected by worldwide copyright laws and treaty provisions. 
No part of the Material may be used, copied, reproduced, modified, published, uploaded, posted, transmitted, distributed or disclosed in any way without Intel's prior express written permission. 
No license under any patent, copyright or other intellectual property rights in the Material is granted to or conferred upon you, either expressly, by implication, inducement, estoppel or otherwise. 
Any license under such intellectual property rights must be express and approved by Intel in writing.
 
Unless otherwise agreed by Intel in writing, you may not remove or alter this notice or any other notice embedded in Materials by Intel or Intel’s suppliers or licensors in any way.
*/
package jfxgestureexample2;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class TouchPaneController implements Initializable, ISelectableItemContainer {

	@FXML public Button randomizePaneBtn;
	@FXML private Pane touchPane;
	@FXML private HBox buttons;
	@FXML ToggleButton setScrollBtn;
	@FXML ToggleButton setSwipeBtn;
	@FXML ToggleGroup gestureSelectionGroup;
	@FXML private GridPane gridPane;
	@FXML public Pane Top_L_Pane;
	@FXML public Pane Top_M_Pane;
	@FXML public Pane Top_R_Pane;
	@FXML public Pane Center_L_Pane;
	@FXML public Pane Center_M_Pane;
	@FXML public Pane Center_R_Pane;
	@FXML public Pane Bottom_L_Pane;
	@FXML public Pane Bottom_M_Pane;
	@FXML public Pane Bottom_R_Pane;

	private ArrayList<Pane> paneArrayList = new ArrayList<Pane>();

	public enum GestureSelection {

		SWIPE, SCROLL
	}


	private ISelectableItem currentSelection;
	private GestureSelection selectedGesture = GestureSelection.SCROLL;

	@FXML
	private void handleResetScene(ActionEvent event) {

		touchPane.getChildren().clear();
		touchPane.getChildren().add(buttons);
	}

	@FXML
	private void handleAddElement(ActionEvent event) throws IOException {

		MovableElementController elem = new MovableElementController(this);
		elem.setTranslateX(touchPane.getWidth() / 2.0);
		elem.setTranslateY(touchPane.getHeight() / 2.0);
		touchPane.getChildren().remove(buttons);
		touchPane.getChildren().add(elem);
		touchPane.getChildren().add(buttons);

		Pane redPane = paneArrayList.get(0);

		System.out.println("Elem coords: " + elem.dragPane.getLayoutX() + ", " + elem.getLayoutY());


			if (elem.isBracedPosition &&
					(elem.dragPane.getLayoutX() - redPane.getLayoutX() < 50 ||
							elem.dragPane.getLayoutX() - redPane.getLayoutX()> -50)
					&& (elem.dragPane.getLayoutY() - redPane.getLayoutY() < 50 ||
					elem.dragPane.getLayoutY() - redPane.getLayoutY() > -50)) {
				System.out.println("element is dragged");
				redPane.setStyle("-fx-background-color: green");
			}

			redPane.setOnDragOver(new EventHandler<DragEvent>() {
				@Override
				public void handle(DragEvent event) {
					/* data is dragged over the target */
					/* accept it only if it is not dragged from the same node
					 * and if it has a string data */
					if (event.getGestureSource() != currentSelection) {
						event.acceptTransferModes(TransferMode.MOVE);
						System.out.println("drag detected");
					}
					event.consume();
				}
			});
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		setScrollBtn.setUserData(GestureSelection.SCROLL);
		setSwipeBtn.setUserData(GestureSelection.SWIPE);
		gridPane.setStyle("-fx-background-color: aqua");

		paneArrayList.add(Top_L_Pane);
		paneArrayList.add(Top_M_Pane);
		paneArrayList.add(Top_R_Pane);
		paneArrayList.add(Center_L_Pane);
		paneArrayList.add(Center_M_Pane);
		paneArrayList.add(Center_R_Pane);
		paneArrayList.add(Bottom_L_Pane);
		paneArrayList.add(Bottom_M_Pane);
		paneArrayList.add(Bottom_R_Pane);

		System.out.println("Original List : \n" + paneArrayList);


		randomizePaneBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Collections.shuffle(paneArrayList, new Random());
				System.out.println("\nShuffled List with Random() : \n"
						+ paneArrayList);
				paneArrayList.get(0).setStyle("-fx-background-color: red;");
				System.out.println("Red pane coords: " + paneArrayList.get(0).getLayoutX() + ", " + paneArrayList.get(0).getLayoutY());
				paneArrayList.remove(0);

			}
		});



		System.out.println(currentSelection);





		gestureSelectionGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			@Override
			public void changed(ObservableValue<? extends Toggle> ov, Toggle oldValue, Toggle newValue) {
				selectedGesture = (GestureSelection) newValue.getUserData();
			}
		});
	}

	@Override
	public void registerItem(ISelectableItem rect) {
		if (rect == null) {
			throw new NullPointerException();
		}

		if (!touchPane.getChildren().contains(rect)) {
			throw new Error("Trying to register an element that is not a child of this pane");
		} else {

			Node rectNode = rect.getCorrespondingNode();

			currentSelection = rect;
			currentSelection.onSelected();

			//pushing to the end of the list to make it appear on the top
			touchPane.getChildren().remove(rectNode);
			touchPane.getChildren().remove(buttons);
			touchPane.getChildren().add(rectNode);
			touchPane.getChildren().add(buttons);
		}
	}

	@Override
	public boolean isRegisterred() {
		return (currentSelection != null);
	}

	@Override
	public ISelectableItem getRegisterredItem() {
		return currentSelection;
	}

	@Override
	public void unregisterItem() {
		if (currentSelection != null) {
			currentSelection.onUnselected();
		}
		currentSelection = null;
	}


	@FXML
	public void onScroll(ScrollEvent t) {
		if (selectedGesture == GestureSelection.SCROLL && currentSelection != null) {
			Node selNode = currentSelection.getCorrespondingNode();
			selNode.setTranslateX(selNode.getTranslateX() + (t.getDeltaX() / 10.0));
			selNode.setTranslateY(selNode.getTranslateY() + (t.getDeltaY() / 10.0));
		}

		t.consume();
	}

	@FXML
	public void onZoom(ZoomEvent t) {
		if (currentSelection != null) {
			Node selNode = currentSelection.getCorrespondingNode();
			selNode.setScaleX(selNode.getScaleX() * t.getZoomFactor());
			selNode.setScaleY(selNode.getScaleY() * t.getZoomFactor());
		}

		t.consume();
	}

	@FXML
	public void onRotate(RotateEvent t) {
		if (currentSelection != null) {
			Node selNode = currentSelection.getCorrespondingNode();
			selNode.setRotate(selNode.getRotate() + t.getAngle());
		}

		t.consume();
	}

	@FXML
	public void onSwipe(SwipeEvent t) {
		if (selectedGesture == GestureSelection.SWIPE && currentSelection != null) {
			Node selNode = currentSelection.getCorrespondingNode();
			TranslateTransition transition = new TranslateTransition(Duration.millis(1000), selNode);
			if (t.getEventType() == SwipeEvent.SWIPE_DOWN) {
				transition.setByY(100);
			} else if (t.getEventType() == SwipeEvent.SWIPE_UP) {
				transition.setByY(-100);
			} else if (t.getEventType() == SwipeEvent.SWIPE_LEFT) {
				transition.setByX(-100);
			} else if (t.getEventType() == SwipeEvent.SWIPE_RIGHT) {
				transition.setByX(100);
			}
			transition.play();

		}
		t.consume();
	}
}
