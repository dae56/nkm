package ru.dae56.nkm.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import ru.dae56.nkm.graphElements.GraphLink;
import ru.dae56.nkm.graphElements.GraphNode;
import ru.dae56.nkm.memento.GraphMemento;
import ru.dae56.nkm.memento.GraphStorage;
import ru.dae56.nkm.utils.GraphAnalyzer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;


import java.util.*;

public class HelloController {

    @FXML
    private AnchorPane AnchorPane;

    @FXML
    private Button btnAnalyzeCycles;

    @FXML
    private Button btnSaveGraph;

    @FXML
    private Button btnRestoreGraph;

    @FXML
    private Button btnClearGraph;


    private static ContextMenu currentContextMenu;
    private static GraphNode nodeFrom = null;

    private static final List<GraphNode> allNodes = new ArrayList<>();
    private static final List<GraphLink> allLinks = new ArrayList<>();
    public static List<GraphLink> getAllLinks() { return allLinks; }
    public static GraphNode getNodeFrom() { return nodeFrom; }
    public static void setNodeFrom(GraphNode node) { nodeFrom = node; }

    @FXML
    void initialize() {
        AnchorPane.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY && event.getTarget() == AnchorPane) {
                hideCurrentContextMenu();
                ContextMenu menu = new ContextMenu();
                MenuItem addNode = new MenuItem("Добавить узел");
                addNode.setOnAction(e -> {
                    GraphNode node = new GraphNode(AnchorPane, event.getX(), event.getY(), 40, 3, Color.GREEN, Color.BLACK);
                    allNodes.add(node);
                });
                menu.getItems().add(addNode);
                setCurrentContextMenu(menu);
                menu.show(AnchorPane, event.getScreenX() + 5, event.getScreenY() + 5);
                event.consume();
            } else {
                hideCurrentContextMenu();
            }
        });

        btnAnalyzeCycles.setOnAction(e -> {
            GraphAnalyzer analyzer = new GraphAnalyzer(AnchorPane);
            analyzer.analyzeCycles();
        });
        btnSaveGraph.setOnAction(e -> {
            if (allNodes.isEmpty() && allLinks.isEmpty()) {
                showInfo("Нельзя сохранить пустой граф.");
                return;
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Сохранить граф");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("JSON файлы (*.json)", "*.json")
            );

            File file = fileChooser.showSaveDialog(new Stage());
            if (file != null) {
                GraphMemento memento = saveState();
                GraphStorage.saveToFile(memento, file.getAbsolutePath());
                showInfo("Граф сохранён: " + file.getAbsolutePath());
            }
        });

        btnRestoreGraph.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Загрузить граф");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("JSON файлы (*.json)", "*.json")
            );

            File file = fileChooser.showOpenDialog(new Stage());
            if (file != null) {
                GraphMemento memento = GraphStorage.loadFromFile(file.getAbsolutePath());
                if (memento != null) {
                    restoreState(memento);
                    showInfo("Граф восстановлен: " + file.getAbsolutePath());
                } else {
                    showInfo("Не удалось загрузить граф.");
                }
            }
        });


        btnClearGraph.setOnAction(e -> {
            allNodes.clear();
            allLinks.clear();
            AnchorPane.getChildren().clear();
            GraphNode.resetIdCounter(); // ← сбрасываем счётчик
            showInfo("Граф очищен.");
        });

        AnchorPane.setScaleX(1.0);
        AnchorPane.setScaleY(1.0);

        AnchorPane.setOnScroll(event -> {
            double delta = event.getDeltaY();
            double scaleFactor = (delta > 0) ? 1.1 : 0.9;

            double newScale = AnchorPane.getScaleX() * scaleFactor;

            // Ограничим масштаб от 0.2 до 5.0
            if (newScale >= 0.2 && newScale <= 5.0) {
                AnchorPane.setScaleX(newScale);
                AnchorPane.setScaleY(newScale);
            }
        });



    }

    public static void hideCurrentContextMenu() {
        if (currentContextMenu != null) currentContextMenu.hide();
    }

    public static void setCurrentContextMenu(ContextMenu menu) {
        currentContextMenu = menu;
    }

    public static void addLinkage(AnchorPane parent, GraphNode from, GraphNode to) {
        for (GraphLink link : allLinks) {
            if (link.getFrom() == from && link.getTo() == to) {
                showInfo("Связь между этими узлами уже существует.");
                return;
            }
        }
        TextInputDialog dialog = new TextInputDialog("0.5");
        dialog.setTitle("Вес связи");
        dialog.setHeaderText("Введите вес (-1 до 1):");
        dialog.showAndWait().ifPresent(input -> {
            try {
                double weight = Double.parseDouble(input);
                if (weight < -1 || weight > 1) throw new NumberFormatException();
                GraphLink link = new GraphLink(parent, from, to, weight);
                allLinks.add(link);
            } catch (NumberFormatException ignored) {
                showInfo("Неверный формат веса.");
            }
        });
    }


    private GraphMemento saveState() {
        List<GraphMemento.NodeState> nodeStates = new ArrayList<>();
        List<GraphMemento.LinkState> linkStates = new ArrayList<>();

        for (GraphNode node : allNodes) {
            var pane = node.getStackPane();
            String label = ((Text) pane.getChildren().get(1)).getText();
            nodeStates.add(new GraphMemento.NodeState(node.getId(), pane.getLayoutX(), pane.getLayoutY(), label));
        }

        for (GraphLink link : allLinks) {
            linkStates.add(new GraphMemento.LinkState(link.getFrom().getId(), link.getTo().getId(), link.getWeight()));
        }

        return new GraphMemento(nodeStates, linkStates);
    }

    private void restoreState(GraphMemento memento) {
        allNodes.clear();
        allLinks.clear();
        AnchorPane.getChildren().clear();

        Map<String, GraphNode> nodeMap = new HashMap<>();
        for (GraphMemento.NodeState ns : memento.getNodes()) {
            GraphNode node = new GraphNode(AnchorPane, ns.x, ns.y, 40, 3, Color.GREEN, Color.BLACK);
            node.getStackPane().setId(ns.id);
            ((Text) node.getStackPane().getChildren().get(1)).setText(ns.label);
            allNodes.add(node);
            nodeMap.put(ns.id, node);
        }

        for (GraphMemento.LinkState ls : memento.getLinks()) {
            GraphNode from = nodeMap.get(ls.fromId);
            GraphNode to = nodeMap.get(ls.toId);
            GraphLink link = new GraphLink(AnchorPane, from, to, ls.weight);
            allLinks.add(link);
        }
    }

    private static void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
