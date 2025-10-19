package ru.dae56.nkm;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.InvalidationListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class HelloController {
    static ContextMenu currentContextMenu;

    static StackPane nodeFrom = null; // Для хранения первого узла при создании связи

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
                if (event.getButton() == MouseButton.SECONDARY) {
                    ContextMenu contextMenu = new ContextMenu();

                    MenuItem delete = new MenuItem("Delete");
                    MenuItem addLinkageItem = new MenuItem("Add Linkage");

                    delete.setOnAction(e -> deleteNode(AnchorPane, circleContainer));

                    addLinkageItem.setOnAction(e -> {
                        nodeFrom = circleContainer;
                        ((Circle) nodeFrom.getChildren().get(0)).setStroke(Color.RED);
                    });

                    contextMenu.getItems().addAll(delete, addLinkageItem);

                    if (currentContextMenu != null) { currentContextMenu.hide(); }
                    currentContextMenu = contextMenu;
                    contextMenu.show(circleContainer, event.getScreenX(), event.getScreenY());
                }
                else if (event.getButton() == MouseButton.PRIMARY) {
                    // Если выбран первый узел, создаем связь к этому узлу
                    if (nodeFrom != null && nodeFrom != circleContainer) {
                        addLinkage(AnchorPane, nodeFrom, circleContainer);

                        // Сброс выделения
                        ((Circle) nodeFrom.getChildren().get(0)).setStroke(Color.BLACK);
                        nodeFrom = null;
                    }
                }
            });
        }

        static void deleteNode(AnchorPane AnchorPane, StackPane circleContainer) {
            AnchorPane.getChildren().remove(circleContainer);
        }

        static void addLinkage(AnchorPane AnchorPane, StackPane nodeFrom, StackPane nodeTo) {
            TextInputDialog dialog = new TextInputDialog("0.5");
            dialog.setTitle("Set Link Weight");
            dialog.setHeaderText("Enter weight for this link (-1 to 1):");
            dialog.setContentText("Weight:");

            dialog.showAndWait().ifPresent(input -> {
                double weight;
                try {
                    weight = Double.parseDouble(input);
                    if (weight < -1 || weight > 1) throw new NumberFormatException();
                } catch (NumberFormatException e) {
                    System.out.println("Invalid weight. Must be between -1 and 1.");
                    return;
                }
                new Link(AnchorPane, nodeFrom, nodeTo, weight);
            });
        }
    }

    public static class Link {
        private final StackPane from;
        private final StackPane to;
        private final Line line;
        private final Polygon arrow;
        private final Text weightText;
        private final double weight;

        public Link(AnchorPane parent, StackPane from, StackPane to, double weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;

            line = new Line();
            line.setStroke(Color.BLACK);
            line.setStrokeWidth(2);
            if (weight < 0) {
                line.getStrokeDashArray().addAll(10.0, 5.0);
            }

            arrow = new Polygon();
            arrow.setFill(Color.BLACK);
            weightText = new Text(String.valueOf(weight));
            weightText.setFill(Color.BLUE);
            // Добавляем в контейнер до подписки, чтобы сразу было видно
            parent.getChildren().addAll(line, arrow, weightText);
            // Подписываемся на изменение координат обоих узлов
            InvalidationListener updater = obs -> updatePositions();
            from.layoutXProperty().addListener(updater);
            from.layoutYProperty().addListener(updater);
            to.layoutXProperty().addListener(updater);
            to.layoutYProperty().addListener(updater);
            // Первоначальное обновление
            updatePositions();
        }

        private void updatePositions() {
            Circle circleFrom = (Circle) from.getChildren().get(0);
            Circle circleTo = (Circle) to.getChildren().get(0);

            double rFrom = circleFrom.getRadius();
            double rTo = circleTo.getRadius();

            double x1 = from.getLayoutX() + rFrom;
            double y1 = from.getLayoutY() + rFrom;
            double x2 = to.getLayoutX() + rTo;
            double y2 = to.getLayoutY() + rTo;

            // Вычисляем угол
            double angle = Math.atan2(y2 - y1, x2 - x1);
            // Смещаем начало и конец на радиусы, чтобы линия касалась краёв
            double startX = x1 + Math.cos(angle) * rFrom;
            double startY = y1 + Math.sin(angle) * rFrom;
            double endX = x2 - Math.cos(angle) * rTo;
            double endY = y2 - Math.sin(angle) * rTo;

            line.setStartX(startX);
            line.setStartY(startY);
            line.setEndX(endX);
            line.setEndY(endY);
            // Рисуем стрелку
            double arrowLength = 10;
            double arrowWidth = 7;
            arrow.getPoints().setAll(
                    endX, endY,
                    endX - arrowLength * Math.cos(angle - Math.PI / 6), endY - arrowLength * Math.sin(angle - Math.PI / 6),
                    endX - arrowLength * Math.cos(angle + Math.PI / 6), endY - arrowLength * Math.sin(angle + Math.PI / 6)
            );
            // Текст веса — по центру линии
            double textX = (startX + endX) / 2;
            double textY = (startY + endY) / 2 - 10;
            weightText.setX(textX);
            weightText.setY(textY);
        }

        public double getWeight() {
            return weight;
        }
    }
}