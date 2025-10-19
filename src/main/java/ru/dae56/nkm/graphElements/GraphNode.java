package ru.dae56.nkm.graphElements;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import ru.dae56.nkm.controllers.HelloController;

public class GraphNode {
    private static int currentId = 0;
    private static double offsetX, offsetY;

    private final StackPane container;

    public GraphNode(AnchorPane parent, double x, double y, int radius, int strokeWidth, Color fill, Color stroke) {
        Circle circle = new Circle(radius, fill);
        circle.setStroke(stroke);
        circle.setStrokeWidth(strokeWidth);

        Text text = new Text("V" + (currentId + 1));
        text.setFont(Font.font("Arial", radius / 1.5));
        text.setFill(Color.WHITE);

        container = new StackPane();
        container.setLayoutX(x);
        container.setLayoutY(y);
        container.getChildren().addAll(circle, text);
        container.setId("V" + (currentId + 1));
        currentId++;

        initHover(circle, strokeWidth);
        initDragAndDrop();
        initContextMenu(parent);

        parent.getChildren().add(container);
    }

    public StackPane getStackPane() { return container; }
    public double getRadius() { return ((Circle) container.getChildren().get(0)).getRadius(); }
    public String getId() { return container.getId(); }

    private void initHover(Circle circle, int strokeWidth) {
        container.setOnMouseEntered(e -> circle.setStrokeWidth(strokeWidth * 2.5));
        container.setOnMouseExited(e -> circle.setStrokeWidth(strokeWidth));
    }

    private void initDragAndDrop() {
        container.setOnMousePressed(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                HelloController.hideCurrentContextMenu();
                offsetX = event.getX();
                offsetY = event.getY();
                container.toFront();
            }
        });
        container.setOnMouseDragged(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                HelloController.hideCurrentContextMenu();
                container.setLayoutX(container.getLayoutX() + event.getX() - offsetX);
                container.setLayoutY(container.getLayoutY() + event.getY() - offsetY);
            }
        });
    }

    private void initContextMenu(AnchorPane parent) {
        container.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                HelloController.hideCurrentContextMenu();

                ContextMenu contextMenu = new ContextMenu();

                MenuItem delete = new MenuItem("Удалить");
                delete.setOnAction(e -> {
                    HelloController.getAllLinks().removeIf(link -> {
                        if (link.getFrom() == this || link.getTo() == this) {
                            parent.getChildren().removeAll(
                                    link.getLine(),
                                    link.getArrow(),
                                    link.getWeightText()
                            );
                            return true;
                        }
                        return false;
                    });
                    parent.getChildren().remove(container);
                });

                MenuItem addLink = new MenuItem("Добавить связь");
                addLink.setOnAction(e -> HelloController.setNodeFrom(this));

                contextMenu.getItems().addAll(delete, addLink);
                HelloController.setCurrentContextMenu(contextMenu);
                contextMenu.show(container, event.getScreenX(), event.getScreenY());

                event.consume();
            }
            else if (event.getButton() == MouseButton.PRIMARY) {
                GraphNode fromNode = HelloController.getNodeFrom();
                if (fromNode != null && fromNode != this) {
                    HelloController.addLinkage(parent, fromNode, this);
                    HelloController.setNodeFrom(null);
                }
            }
        });
    }
    public static void resetIdCounter() {
        currentId = 0;
    }

}
