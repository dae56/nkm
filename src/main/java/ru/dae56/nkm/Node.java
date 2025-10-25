package ru.dae56.nkm;

import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.util.ArrayList;


public class Node {
    static double offsetX, offsetY;

    public static void addNode(ScrollPane window, AnchorPane anchorPane, String nameNode) throws Exception {
        for (Object o : anchorPane.getChildren()) {
            if (o instanceof StackPane) {
                if (nameNode.equals(((StackPane) o).getId())) {
                    throw new Exception("Node already exists.");
                }
            }
        }

        Text text = new Text(nameNode);
        text.getStyleClass().add("adaptive-text");
        text.setWrappingWidth(150); // Устанавливаем ширину для переноса

        // Ждем применения стилей и рассчитываем размер текста
        text.applyCss();

        // Получаем реальные размеры текста после переноса
        double textWidth = text.getLayoutBounds().getWidth();

        // Добавляем отступы для толстого контура
        double circleRadius = Math.min(textWidth + 20, 170);

        Circle circle = new Circle(circleRadius / 2);
        circle.getStyleClass().add("adaptive-circle");

        StackPane circleContainer = new StackPane();
        circleContainer.getChildren().addAll(circle, text);
        circleContainer.getStyleClass().add("adaptive-container");

        circleContainer.setLayoutX(anchorPane.getHeight() * window.getHvalue() - circleRadius / 2);
        circleContainer.setLayoutY(anchorPane.getWidth() * window.getVvalue() - circleRadius / 2);
        circleContainer.setId(nameNode);
        anchorPane.getChildren().add(circleContainer);

        initDragAndDrop(circleContainer);
        initHover(circleContainer, 10);
    }

    static void removeNode(AnchorPane anchorPane, String nameNode) throws Exception {
        boolean flag = true;
        for (Object o : anchorPane.getChildren()) {
            if (o instanceof StackPane) {
                if (nameNode.equals(((StackPane) o).getId())) {
                    anchorPane.getChildren().remove((StackPane) o);
                    flag = false;
                }
            }
        }
        if (flag) {
            throw new Exception("Node does not exists!");
        }
    }

    static void initDragAndDrop(StackPane circleContainer) {
        circleContainer.setOnMousePressed(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                offsetX = event.getX();
                offsetY = event.getY();
                circleContainer.toFront();
            }
        });
        circleContainer.setOnMouseDragged(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
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
}
