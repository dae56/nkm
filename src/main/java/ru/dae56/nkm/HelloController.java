package ru.dae56.nkm;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class HelloController {
    static ContextMenu currentContextMenu;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane AnchorPane;

    @FXML
    private HBox BottomHBox;

    @FXML
    private ScrollPane FrontScrollPane;

    @FXML
    private BorderPane MainBorder;

    @FXML
    void initialize() {

        assert AnchorPane != null : "fx:id=\"AnchorPane\" was not injected: check your FXML file 'Main-view.fxml'.";
        assert BottomHBox != null : "fx:id=\"BottomHBox\" was not injected: check your FXML file 'Main-view.fxml'.";
        assert FrontScrollPane != null : "fx:id=\"FrontScrollPane\" was not injected: check your FXML file 'Main-view.fxml'.";
        assert MainBorder != null : "fx:id=\"MainBorder\" was not injected: check your FXML file 'Main-view.fxml'.";

        AnchorPane.setOnMouseClicked(event -> {

            if (event.getButton() == MouseButton.SECONDARY && event.getTarget().equals(AnchorPane)) {
                ContextMenu contextMenu = new ContextMenu();

                MenuItem menuItemAdd = new MenuItem("Add Node");

                menuItemAdd.setOnAction(e -> {Node.addNode(AnchorPane, 40, 3, Color.GREEN, Color.BLACK, event.getX(), event.getY());});

                contextMenu.getItems().addAll(menuItemAdd);

                if (currentContextMenu != null) {
                    currentContextMenu.hide();
                }
                currentContextMenu = contextMenu;
                contextMenu.show(AnchorPane, event.getScreenX() + 5, event.getScreenY() + 5);
            } else if ((event.getButton() == MouseButton.PRIMARY || event.getButton() == MouseButton.MIDDLE) && event.getTarget().equals(AnchorPane)) {
                if (currentContextMenu != null) {currentContextMenu.hide();}
            }
        });
    }

    static class Node {
        static double offsetX, offsetY;
        static int CurrentId = 0;

        static void addNode(AnchorPane AnchorPane, int radius, int strokeWidth, Color colorFill, Color colorStroke, double coordinateMouseX, double coordinateMouseY) {
            Circle circle = new Circle();
            circle.setId(String.valueOf(CurrentId));
            CurrentId ++;
            circle.setRadius(radius);
            circle.setFill(colorFill);
            circle.setStroke(colorStroke);
            circle.setStrokeWidth(strokeWidth);

            Text text = new Text("V" + CurrentId);
            text.setFont(Font.font("Arial", (double) radius/1.5));
            text.setFill(Color.WHITE);

            StackPane circleContainer = new StackPane();
            circleContainer.setLayoutX(coordinateMouseX);
            circleContainer.setLayoutY(coordinateMouseY);

            circleContainer.getChildren().addAll(circle, text);

            initHover(circleContainer, strokeWidth);
            initDragAndDrop(circleContainer);
            initContextMenu(AnchorPane, circleContainer);

            AnchorPane.getChildren().add(circleContainer);
        }

        static void initDragAndDrop(StackPane circleContainer) {
            circleContainer.setOnMousePressed(event -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    if (currentContextMenu != null) {currentContextMenu.hide();}
                    offsetX = event.getX();
                    offsetY = event.getY();
                    circleContainer.toFront();
                }
            });
            circleContainer.setOnMouseDragged(event -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    if (currentContextMenu != null) {currentContextMenu.hide();}
                    circleContainer.setLayoutX(circleContainer.getLayoutX() + event.getX() - offsetX);
                    circleContainer.setLayoutY(circleContainer.getLayoutY() + event.getY() - offsetY);
                }
            });
        }

        static void initHover(StackPane circleContainer, int strokeWidth) {
            circleContainer.setOnMouseEntered(e -> {
                ((Circle) circleContainer.getChildren().getFirst()).setStrokeWidth(strokeWidth * 2.5);
            });
            circleContainer.setOnMouseExited(e -> {
                ((Circle) circleContainer.getChildren().getFirst()).setStrokeWidth(strokeWidth);
            });
        }

        static void initContextMenu(AnchorPane AnchorPane, StackPane circleContainer) {
            circleContainer.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.SECONDARY && (event.getTarget().equals(circleContainer.getChildren().getFirst()) || event.getTarget().equals(circleContainer.getChildren().get(1)))) {
                    ContextMenu contextMenu = new ContextMenu();

                    MenuItem delete = new MenuItem("Delete");
                    MenuItem addLinkage = new MenuItem("Add Linkage");

                    delete.setOnAction(e -> {deleteNode(AnchorPane, circleContainer);});
                    addLinkage.setOnAction(e -> {addLinkage(AnchorPane, circleContainer);});

                    contextMenu.getItems().addAll(delete);

                    if (currentContextMenu != null) {currentContextMenu.hide();}
                    currentContextMenu = contextMenu;
                    contextMenu.show(circleContainer, event.getScreenX(), event.getScreenY());
                }
            });
        }

        static void deleteNode(AnchorPane AnchorPane, StackPane circleContainer) {
            AnchorPane.getChildren().remove(circleContainer);
        }

        static void addLinkage(AnchorPane AnchorPane, StackPane circleContainerFrom) {

        }
    }
}
