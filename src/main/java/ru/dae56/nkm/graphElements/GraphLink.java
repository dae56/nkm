package ru.dae56.nkm.graphElements;

import javafx.beans.InvalidationListener;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import ru.dae56.nkm.controllers.HelloController;

public class GraphLink {
    private final GraphNode from;
    private final GraphNode to;
    private final Line line;
    private final Polygon arrow;
    private final Text weightText;
    private final double weight;
    private final AnchorPane parent;

    public Line getLine() {
        return line;
    }

    public Polygon getArrow() {
        return arrow;
    }

    public Text getWeightText() {
        return weightText;
    }


    public GraphLink(AnchorPane parent, GraphNode from, GraphNode to, double weight) {
        this.parent = parent;
        this.from = from;
        this.to = to;
        this.weight = weight;

        line = new Line();
        line.setStroke(Color.BLACK);
        line.setStrokeWidth(2);
        if (weight < 0) line.getStrokeDashArray().addAll(10.0, 5.0);

        arrow = new Polygon();
        arrow.setFill(Color.BLACK);

        weightText = new Text(String.valueOf(weight));
        weightText.setFill(Color.BLUE);
        weightText.setFont(Font.font("Arial", 18));

        parent.getChildren().addAll(line, arrow, weightText);
        line.toFront();
        arrow.toFront();
        weightText.toFront();

        initContextMenu();

        InvalidationListener updater = obs -> updatePositions();
        from.getStackPane().layoutXProperty().addListener(updater);
        from.getStackPane().layoutYProperty().addListener(updater);
        to.getStackPane().layoutXProperty().addListener(updater);
        to.getStackPane().layoutYProperty().addListener(updater);

        updatePositions();
    }

    private void initContextMenu() {
        line.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                showContextMenu(event.getScreenX(), event.getScreenY(), line);
                event.consume();
            }
        });

        arrow.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                showContextMenu(event.getScreenX(), event.getScreenY(), arrow);
                event.consume();
            }
        });

        weightText.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                showContextMenu(event.getScreenX(), event.getScreenY(), weightText);
                event.consume();
            }
        });
    }

    private void showContextMenu(double screenX, double screenY, javafx.scene.Node targetNode) {
        HelloController.hideCurrentContextMenu();

        ContextMenu menu = new ContextMenu();
        MenuItem delete = new MenuItem("Удалить связь");
        delete.setOnAction(e -> {
            deleteLink();
            HelloController.hideCurrentContextMenu();
        });

        menu.getItems().add(delete);
        HelloController.setCurrentContextMenu(menu);
        menu.show(targetNode, screenX, screenY);
    }

    private void deleteLink() {
        parent.getChildren().removeAll(line, arrow, weightText);
        HelloController.getAllLinks().remove(this);
    }

    private void updatePositions() {
        double rFrom = from.getRadius();
        double rTo = to.getRadius();

        double x1 = from.getStackPane().getLayoutX() + rFrom;
        double y1 = from.getStackPane().getLayoutY() + rFrom;
        double x2 = to.getStackPane().getLayoutX() + rTo;
        double y2 = to.getStackPane().getLayoutY() + rTo;

        double angle = Math.atan2(y2 - y1, x2 - x1);

        double startX = x1 + Math.cos(angle) * rFrom;
        double startY = y1 + Math.sin(angle) * rFrom;
        double endX = x2 - Math.cos(angle) * rTo;
        double endY = y2 - Math.sin(angle) * rTo;

        line.setStartX(startX);
        line.setStartY(startY);
        line.setEndX(endX);
        line.setEndY(endY);

        double arrowLength = 10;
        arrow.getPoints().setAll(
                endX, endY,
                endX - arrowLength * Math.cos(angle - Math.PI / 6), endY - arrowLength * Math.sin(angle - Math.PI / 6),
                endX - arrowLength * Math.cos(angle + Math.PI / 6), endY - arrowLength * Math.sin(angle + Math.PI / 6)
        );

        weightText.setX((startX + endX) / 2);
        weightText.setY((startY + endY) / 2 - 10);
    }

    public double getWeight() {
        return weight;
    }

    public GraphNode getFrom() {
        return from;
    }

    public GraphNode getTo() {
        return to;
    }
}
