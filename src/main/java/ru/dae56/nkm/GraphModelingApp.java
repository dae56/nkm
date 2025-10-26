package ru.dae56.nkm;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;

public class GraphModelingApp extends Application {
    private GraphModel graphModel = new GraphModel();
    private GraphVisualization graphVisualization = new GraphVisualization();
    private ObservableList<String> nodeList = FXCollections.observableArrayList();
    private List<XYChart.Series<Number, Number>> impulseSeries = new ArrayList<>();

    // UI components
    private ListView<String> nodesListView;
    private TextArea cyclesTextArea;
    private LineChart<Number, Number> impulseChart;
    private Pane graphPane;
    private ComboBox<String> fromNodeCombo, toNodeCombo;
    private TextField nodeNameField, weightField, impulseStepsField;
    private TableView<ImpulseInput> impulseInputTable;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Graph Modeling and Impulse Analysis");

        // Create main layout
        BorderPane mainLayout = new BorderPane();
        mainLayout.getStyleClass().add("root");

        // Create menu bar
        MenuBar menuBar = createMenuBar();

        // Create left panel for graph operations
        VBox leftPanel = createLeftPanel();

        // Create center panel for graph visualization
        VBox centerPanel = createCenterPanel();

        // Create right panel for analysis
        VBox rightPanel = createRightPanel();

        mainLayout.setTop(menuBar);
        mainLayout.setLeft(leftPanel);
        mainLayout.setCenter(centerPanel);
        mainLayout.setRight(rightPanel);

        Scene scene = new Scene(mainLayout, 1600, 900);

        // Load CSS from file
        String stylesheet = getClass().getResource("styles.css").toExternalForm();
        scene.getStylesheets().add(stylesheet);

        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();

        updateNodeLists();
        updateGraphVisualization();
        refreshImpulseTable();
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        menuBar.getStyleClass().add("menu-bar-style");

        // File menu
        Menu fileMenu = new Menu("File");
        fileMenu.getStyleClass().add("menu-style");

        MenuItem newItem = new MenuItem("New Graph");
        MenuItem saveItem = new MenuItem("Save Graph");
        MenuItem loadItem = new MenuItem("Load Graph");
        MenuItem exitItem = new MenuItem("Exit");

        // Apply styles to menu items
        newItem.getStyleClass().add("menu-item-style");
        saveItem.getStyleClass().add("menu-item-style");
        loadItem.getStyleClass().add("menu-item-style");
        exitItem.getStyleClass().add("menu-item-style");

        newItem.setOnAction(e -> newGraph());
        saveItem.setOnAction(e -> saveGraph());
        loadItem.setOnAction(e -> loadGraph());
        exitItem.setOnAction(e -> System.exit(0));

        fileMenu.getItems().addAll(newItem, saveItem, loadItem, new SeparatorMenuItem(), exitItem);
        menuBar.getMenus().add(fileMenu);

        return menuBar;
    }

    private VBox createLeftPanel() {
        VBox leftPanel = new VBox(15);
        leftPanel.getStyleClass().add("vbox-style");
        leftPanel.setPadding(new Insets(20));

        // Node management section
        HBox nodeHeader = new HBox();
        nodeHeader.setAlignment(Pos.CENTER_LEFT);
        Label nodeLabel = new Label("Node Management");
        nodeLabel.getStyleClass().add("section-label");
        nodeHeader.getChildren().add(nodeLabel);

        HBox nodeInputBox = new HBox(10);
        nodeInputBox.setAlignment(Pos.CENTER);
        nodeNameField = new TextField();
        nodeNameField.setPromptText("Node name");
        nodeNameField.getStyleClass().add("text-field-style");
        nodeNameField.setPrefWidth(120);

        Button addNodeBtn = new Button("Add Node");
        addNodeBtn.getStyleClass().add("button-style");
        addNodeBtn.setOnAction(e -> addNode());

        nodeInputBox.getChildren().addAll(nodeNameField, addNodeBtn);

        nodesListView = new ListView<>(nodeList);
        nodesListView.getStyleClass().add("list-view-style");
        nodesListView.setPrefHeight(150);

        Button removeNodeBtn = new Button("Remove Selected Node");
        removeNodeBtn.getStyleClass().add("button-style");
        removeNodeBtn.setOnAction(e -> removeNode());

        // Connection management section
        HBox connectionHeader = new HBox();
        connectionHeader.setAlignment(Pos.CENTER_LEFT);
        Label connectionLabel = new Label("Connection Management");
        connectionLabel.getStyleClass().add("section-label");
        connectionHeader.getChildren().add(connectionLabel);

        fromNodeCombo = new ComboBox<>();
        toNodeCombo = new ComboBox<>();
        weightField = new TextField();
        weightField.setPromptText("Weight (-1 to 1)");

        // Apply styles
        fromNodeCombo.getStyleClass().add("combo-box-style");
        toNodeCombo.getStyleClass().add("combo-box-style");
        weightField.getStyleClass().add("text-field-style");

        Button addConnectionBtn = new Button("Add Connection");
        addConnectionBtn.getStyleClass().add("button-style");
        addConnectionBtn.setOnAction(e -> addConnection());

        Button removeConnectionBtn = new Button("Remove Connection");
        removeConnectionBtn.getStyleClass().add("button-style");
        removeConnectionBtn.setOnAction(e -> removeConnection());

        VBox connectionBox = new VBox(8);
        connectionBox.getChildren().addAll(
                new Label("From:"), fromNodeCombo,
                new Label("To:"), toNodeCombo,
                new Label("Weight:"), weightField,
                addConnectionBtn, removeConnectionBtn
        );

        // Style labels
        for (var node : connectionBox.getChildren()) {
            if (node instanceof Label) {
                ((Label) node).setTextFill(Color.WHITE);
                ((Label) node).setFont(Font.font("System", FontWeight.BOLD, 12));
            }
        }

        leftPanel.getChildren().addAll(
                nodeHeader, nodeInputBox, nodesListView, removeNodeBtn,
                new Separator(), connectionHeader, connectionBox
        );

        return leftPanel;
    }

    private VBox createCenterPanel() {
        VBox centerPanel = new VBox(15);
        centerPanel.setPadding(new Insets(20));
        centerPanel.setAlignment(Pos.TOP_CENTER);

        HBox centerHeader = new HBox();
        centerHeader.setAlignment(Pos.CENTER);
        centerHeader.getStyleClass().add("hbox-style");
        Label graphLabel = new Label("Graph Visualization");
        graphLabel.getStyleClass().add("section-label");
        centerHeader.getChildren().add(graphLabel);

        graphPane = new Pane();
        graphPane.getStyleClass().add("graph-pane-style");
        graphPane.setPrefSize(600, 400);

        Button refreshGraphBtn = new Button("Refresh Visualization");
        refreshGraphBtn.getStyleClass().add("button-style");
        refreshGraphBtn.setOnAction(e -> updateGraphVisualization());

        centerPanel.getChildren().addAll(centerHeader, graphPane, refreshGraphBtn);

        return centerPanel;
    }

    private VBox createRightPanel() {
        VBox rightPanel = new VBox(15);
        rightPanel.getStyleClass().add("vbox-style");
        rightPanel.setPadding(new Insets(20));

        // Structural analysis section
        HBox analysisHeader = new HBox();
        analysisHeader.setAlignment(Pos.CENTER_LEFT);
        Label analysisLabel = new Label("Structural Analysis");
        analysisLabel.getStyleClass().add("section-label");
        analysisHeader.getChildren().add(analysisLabel);

        Button findCyclesBtn = new Button("Find Cycles");
        findCyclesBtn.getStyleClass().add("button-style");
        findCyclesBtn.setOnAction(e -> findCycles());

        cyclesTextArea = new TextArea();
        cyclesTextArea.getStyleClass().add("text-area-style");
        cyclesTextArea.setPrefHeight(150);
        cyclesTextArea.setEditable(false);

        // Impulse modeling section
        HBox impulseHeader = new HBox();
        impulseHeader.setAlignment(Pos.CENTER_LEFT);
        Label impulseLabel = new Label("Impulse Modeling");
        impulseLabel.getStyleClass().add("section-label");
        impulseHeader.getChildren().add(impulseLabel);

        // Impulse input table
        Label impulseTableLabel = new Label("Set initial impulse values:");
        impulseTableLabel.setTextFill(Color.WHITE);
        impulseTableLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

        impulseInputTable = new TableView<>();
        impulseInputTable.getStyleClass().add("table-view-style");
        setupImpulseInputTable();

        HBox impulseInputBox = new HBox(10);
        impulseInputBox.setAlignment(Pos.CENTER);
        impulseStepsField = new TextField();
        impulseStepsField.setPromptText("Simulation steps");
        impulseStepsField.setText("10");
        impulseStepsField.getStyleClass().add("text-field-style");
        impulseStepsField.setPrefWidth(100);

        Button runImpulseBtn = new Button("Run Impulse Simulation");
        runImpulseBtn.getStyleClass().add("button-style");
        runImpulseBtn.setOnAction(e -> runImpulseSimulation());

        Button refreshImpulseTableBtn = new Button("Refresh Table");
        refreshImpulseTableBtn.getStyleClass().add("button-style");
        refreshImpulseTableBtn.setOnAction(e -> refreshImpulseTable());

        impulseInputBox.getChildren().addAll(impulseStepsField, runImpulseBtn, refreshImpulseTableBtn);

        // Impulse chart
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Time Step");
        yAxis.setLabel("Impulse Value");

        impulseChart = new LineChart<>(xAxis, yAxis);
        impulseChart.getStyleClass().add("chart-style");
        impulseChart.setPrefHeight(300);
        impulseChart.setTitle("Impulse Propagation");
        impulseChart.setLegendVisible(true);

        rightPanel.getChildren().addAll(
                analysisHeader, findCyclesBtn, cyclesTextArea,
                new Separator(), impulseHeader, impulseTableLabel,
                impulseInputTable, impulseInputBox, impulseChart
        );

        return rightPanel;
    }

    private void setupImpulseInputTable() {
        TableColumn<ImpulseInput, String> nodeCol = new TableColumn<>("Node");
        nodeCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNodeName()));

        TableColumn<ImpulseInput, String> valueCol = new TableColumn<>("Initial Impulse");
        valueCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                String.format("%.3f", cellData.getValue().getImpulseValue())));

        // Make value column editable
        valueCol.setCellFactory(column -> new TableCell<ImpulseInput, String>() {
            private final TextField textField = new TextField();

            {
                textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
                    if (!newVal) {
                        updateValueFromTextField();
                    }
                });

                textField.setOnAction(e -> updateValueFromTextField());
            }

            private void updateValueFromTextField() {
                try {
                    double value = Double.parseDouble(textField.getText());
                    ImpulseInput item = getTableView().getItems().get(getIndex());
                    item.setImpulseValue(value);
                    commitEdit(String.format("%.3f", value));
                    getTableView().refresh();
                } catch (NumberFormatException e) {
                    ImpulseInput item = getTableView().getItems().get(getIndex());
                    commitEdit(String.format("%.3f", item.getImpulseValue()));
                }
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    if (item != null) {
                        textField.setText(item);
                    }
                    setGraphic(textField);
                }
            }
        });

        impulseInputTable.getColumns().addAll(nodeCol, valueCol);
        impulseInputTable.setPrefHeight(150);
        impulseInputTable.setEditable(true);
    }

    private void refreshImpulseTable() {
        ObservableList<ImpulseInput> impulseInputs = FXCollections.observableArrayList();
        for (String nodeName : graphModel.getNodes().keySet()) {
            impulseInputs.add(new ImpulseInput(nodeName, 0.0));
        }
        impulseInputTable.setItems(impulseInputs);
    }

    private void newGraph() {
        graphModel = new GraphModel();
        updateNodeLists();
        updateGraphVisualization();
        refreshImpulseTable();
        cyclesTextArea.clear();
        impulseChart.getData().clear();
    }

    private void saveGraph() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Graph");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Graph Files (*.graph)", "*.graph"));

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(graphModel);
                showAlert("Success", "Graph saved successfully!");
            } catch (IOException e) {
                showAlert("Error", "Failed to save graph: " + e.getMessage());
            }
        }
    }

    private void loadGraph() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Graph");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Graph Files (*.graph)", "*.graph"));

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                graphModel = (GraphModel) ois.readObject();
                updateNodeLists();
                updateGraphVisualization();
                refreshImpulseTable();
                cyclesTextArea.clear();
                impulseChart.getData().clear();
                showAlert("Success", "Graph loaded successfully!");
            } catch (IOException | ClassNotFoundException e) {
                showAlert("Error", "Failed to load graph: " + e.getMessage());
            }
        }
    }

    private void addNode() {
        String nodeName = nodeNameField.getText().trim();
        if (!nodeName.isEmpty()) {
            if (graphModel.addNode(nodeName)) {
                updateNodeLists();
                updateGraphVisualization();
                refreshImpulseTable();
                nodeNameField.clear();
            } else {
                showAlert("Error", "Node with this name already exists!");
            }
        }
    }

    private void removeNode() {
        String selectedNode = nodesListView.getSelectionModel().getSelectedItem();
        if (selectedNode != null) {
            graphModel.removeNode(selectedNode);
            updateNodeLists();
            updateGraphVisualization();
            refreshImpulseTable();
        }
    }

    private void addConnection() {
        String fromNode = fromNodeCombo.getValue();
        String toNode = toNodeCombo.getValue();
        String weightText = weightField.getText();

        if (fromNode != null && toNode != null && !weightText.isEmpty()) {
            try {
                double weight = Double.parseDouble(weightText);
                if (graphModel.addConnection(fromNode, toNode, weight)) {
                    updateGraphVisualization();
                    weightField.clear();
                } else {
                    showAlert("Error", "Cannot create connection! Check for mutual connections or invalid weight.");
                }
            } catch (NumberFormatException e) {
                showAlert("Error", "Please enter a valid weight between -1 and 1");
            }
        }
    }

    private void removeConnection() {
        String fromNode = fromNodeCombo.getValue();
        String toNode = toNodeCombo.getValue();

        if (fromNode != null && toNode != null) {
            graphModel.removeConnection(fromNode, toNode);
            updateGraphVisualization();
        }
    }

    private void findCycles() {
        CycleAnalysisResult cycleResult = graphModel.findAllCyclesWithAnalysis();
        cyclesTextArea.clear();

        if (cycleResult.isEmpty()) {
            cyclesTextArea.setText("No cycles found in the graph.");
        } else {
            StringBuilder sb = new StringBuilder();

            // Положительные циклы
            if (!cycleResult.getPositiveCycles().isEmpty()) {
                sb.append("=== POSITIVE CYCLES ===\n");
                sb.append("Found ").append(cycleResult.getPositiveCycles().size()).append(" positive cycle(s):\n\n");
                for (int i = 0; i < cycleResult.getPositiveCycles().size(); i++) {
                    CycleInfo cycle = cycleResult.getPositiveCycles().get(i);
                    sb.append("Positive Cycle ").append(i + 1).append(": ");
                    sb.append(String.join(" -> ", cycle.getNodes()));
                    if (!cycle.getNodes().isEmpty()) {
                        sb.append(" -> ").append(cycle.getNodes().get(0));
                    }
                    sb.append(" (Total weight: ").append(String.format("%.3f", cycle.getTotalWeight())).append(")");
                    sb.append("\n");
                }
                sb.append("\n");
            }

            // Отрицательные циклы
            if (!cycleResult.getNegativeCycles().isEmpty()) {
                sb.append("=== NEGATIVE CYCLES ===\n");
                sb.append("Found ").append(cycleResult.getNegativeCycles().size()).append(" negative cycle(s):\n\n");
                for (int i = 0; i < cycleResult.getNegativeCycles().size(); i++) {
                    CycleInfo cycle = cycleResult.getNegativeCycles().get(i);
                    sb.append("Negative Cycle ").append(i + 1).append(": ");
                    sb.append(String.join(" -> ", cycle.getNodes()));
                    if (!cycle.getNodes().isEmpty()) {
                        sb.append(" -> ").append(cycle.getNodes().get(0));
                    }
                    sb.append(" (Total weight: ").append(String.format("%.3f", cycle.getTotalWeight())).append(")");
                    sb.append("\n");
                }
                sb.append("\n");
            }

            // Нейтральные циклы
            if (!cycleResult.getNeutralCycles().isEmpty()) {
                sb.append("=== NEUTRAL CYCLES ===\n");
                sb.append("Found ").append(cycleResult.getNeutralCycles().size()).append(" neutral cycle(s):\n\n");
                for (int i = 0; i < cycleResult.getNeutralCycles().size(); i++) {
                    CycleInfo cycle = cycleResult.getNeutralCycles().get(i);
                    sb.append("Neutral Cycle ").append(i + 1).append(": ");
                    sb.append(String.join(" -> ", cycle.getNodes()));
                    if (!cycle.getNodes().isEmpty()) {
                        sb.append(" -> ").append(cycle.getNodes().get(0));
                    }
                    sb.append(" (Total weight: ").append(String.format("%.3f", cycle.getTotalWeight())).append(")");
                    sb.append("\n");
                }
                sb.append("\n");
            }

            // Сводная статистика
            sb.append("=== SUMMARY ===\n");
            sb.append("Total cycles: ").append(cycleResult.getAllCycles().size()).append("\n");
            sb.append("Positive cycles: ").append(cycleResult.getPositiveCycles().size()).append("\n");
            sb.append("Negative cycles: ").append(cycleResult.getNegativeCycles().size()).append("\n");
            sb.append("Neutral cycles: ").append(cycleResult.getNeutralCycles().size()).append("\n");

            cyclesTextArea.setText(sb.toString());
        }
    }

    private void runImpulseSimulation() {
        try {
            int steps = Integer.parseInt(impulseStepsField.getText());

            Map<String, Double> initialImpulses = new HashMap<>();
            for (ImpulseInput input : impulseInputTable.getItems()) {
                if (Math.abs(input.getImpulseValue()) > 0.0001) {
                    initialImpulses.put(input.getNodeName(), input.getImpulseValue());
                }
            }

            if (initialImpulses.isEmpty()) {
                showAlert("Error", "Please set at least one non-zero initial impulse value");
                return;
            }

            Map<String, double[]> impulseResults = graphModel.runImpulseSimulation(initialImpulses, steps);
            updateImpulseChart(impulseResults, steps);

        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid number of simulation steps");
        }
    }

    private void updateImpulseChart(Map<String, double[]> impulseResults, int steps) {
        impulseChart.getData().clear();
        impulseSeries.clear();

        for (Map.Entry<String, double[]> entry : impulseResults.entrySet()) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(entry.getKey());

            double[] values = entry.getValue();
            for (int i = 0; i < steps; i++) {
                series.getData().add(new XYChart.Data<>(i, values[i]));
            }

            impulseChart.getData().add(series);
            impulseSeries.add(series);
        }
    }

    private void updateNodeLists() {
        nodeList.setAll(graphModel.getNodes().keySet());
        fromNodeCombo.setItems(nodeList);
        toNodeCombo.setItems(nodeList);
    }

    private void updateGraphVisualization() {
        graphPane.getChildren().clear();
        graphVisualization.drawGraph(graphPane, graphModel);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

// Класс для хранения импульсных входов
class ImpulseInput {
    private final String nodeName;
    private double impulseValue;

    public ImpulseInput(String nodeName, double impulseValue) {
        this.nodeName = nodeName;
        this.impulseValue = impulseValue;
    }

    public String getNodeName() { return nodeName; }
    public double getImpulseValue() { return impulseValue; }
    public void setImpulseValue(double value) { this.impulseValue = value; }
}

// Класс для хранения информации о цикле
class CycleInfo {
    private final List<String> nodes;
    private final double totalWeight;

    public CycleInfo(List<String> nodes, double totalWeight) {
        this.nodes = nodes;
        this.totalWeight = totalWeight;
    }

    public List<String> getNodes() { return nodes; }
    public double getTotalWeight() { return totalWeight; }
    public boolean isPositive() { return totalWeight > 0; }
    public boolean isNegative() { return totalWeight < 0; }
    public boolean isNeutral() { return totalWeight == 0; }
}

// Класс для хранения результатов анализа циклов
class CycleAnalysisResult {
    private final List<CycleInfo> positiveCycles;
    private final List<CycleInfo> negativeCycles;
    private final List<CycleInfo> neutralCycles;

    public CycleAnalysisResult(List<CycleInfo> positiveCycles, List<CycleInfo> negativeCycles, List<CycleInfo> neutralCycles) {
        this.positiveCycles = positiveCycles;
        this.negativeCycles = negativeCycles;
        this.neutralCycles = neutralCycles;
    }

    public List<CycleInfo> getPositiveCycles() { return positiveCycles; }
    public List<CycleInfo> getNegativeCycles() { return negativeCycles; }
    public List<CycleInfo> getNeutralCycles() { return neutralCycles; }
    public List<CycleInfo> getAllCycles() {
        List<CycleInfo> all = new ArrayList<>();
        all.addAll(positiveCycles);
        all.addAll(negativeCycles);
        all.addAll(neutralCycles);
        return all;
    }
    public boolean isEmpty() { return getAllCycles().isEmpty(); }
}

// GraphNode class
class GraphNode implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private Map<String, Double> connections;
    private transient double impulseValue;

    public GraphNode(String name) {
        this.name = name;
        this.connections = new HashMap<>();
        this.impulseValue = 0.0;
    }

    public String getName() { return name; }
    public Map<String, Double> getConnections() { return connections; }
    public double getImpulseValue() { return impulseValue; }
    public void setImpulseValue(double value) { this.impulseValue = value; }

    public boolean addConnection(String targetNode, double weight) {
        if (weight >= -1 && weight <= 1) {
            connections.put(targetNode, weight);
            return true;
        }
        return false;
    }

    public boolean removeConnection(String targetNode) {
        return connections.remove(targetNode) != null;
    }

    public void resetImpulse() {
        impulseValue = 0.0;
    }
}

// GraphModel class
class GraphModel implements Serializable {
    private static final long serialVersionUID = 1L;
    private Map<String, GraphNode> nodes = new HashMap<>();

    public boolean addNode(String name) {
        if (nodes.containsKey(name)) {
            return false;
        }
        nodes.put(name, new GraphNode(name));
        return true;
    }

    public boolean removeNode(String name) {
        if (!nodes.containsKey(name)) {
            return false;
        }

        for (GraphNode node : nodes.values()) {
            node.removeConnection(name);
        }

        nodes.remove(name);
        return true;
    }

    public boolean addConnection(String fromNode, String toNode, double weight) {
        if (!nodes.containsKey(fromNode) || !nodes.containsKey(toNode) ||
                fromNode.equals(toNode) || weight < -1 || weight > 1) {
            return false;
        }

        if (nodes.get(toNode).getConnections().containsKey(fromNode)) {
            return false;
        }

        return nodes.get(fromNode).addConnection(toNode, weight);
    }

    public boolean removeConnection(String fromNode, String toNode) {
        if (nodes.containsKey(fromNode)) {
            return nodes.get(fromNode).removeConnection(toNode);
        }
        return false;
    }

    public Map<String, GraphNode> getNodes() {
        return nodes;
    }

    public List<List<String>> findAllCycles() {
        List<List<String>> allCycles = new ArrayList<>();
        Map<String, List<String>> adjacencyList = buildAdjacencyList();
        List<String> nodeNames = new ArrayList<>(nodes.keySet());
        Collections.sort(nodeNames);

        JohnsonCycleFinder cycleFinder = new JohnsonCycleFinder(adjacencyList, nodeNames);
        return cycleFinder.findElementaryCycles();
    }

    public CycleAnalysisResult findAllCyclesWithAnalysis() {
        List<List<String>> allCycles = findAllCycles();
        List<CycleInfo> positiveCycles = new ArrayList<>();
        List<CycleInfo> negativeCycles = new ArrayList<>();
        List<CycleInfo> neutralCycles = new ArrayList<>();

        for (List<String> cycle : allCycles) {
            double totalWeight = calculateCycleWeight(cycle);
            CycleInfo cycleInfo = new CycleInfo(cycle, totalWeight);

            if (cycleInfo.isPositive()) {
                positiveCycles.add(cycleInfo);
            } else if (cycleInfo.isNegative()) {
                negativeCycles.add(cycleInfo);
            } else {
                neutralCycles.add(cycleInfo);
            }
        }

        return new CycleAnalysisResult(positiveCycles, negativeCycles, neutralCycles);
    }

    private double calculateCycleWeight(List<String> cycle) {
        if (cycle.size() < 2) return 0.0;

        double totalWeight = 0.0;
        // Проходим по всем связям в цикле
        for (int i = 0; i < cycle.size(); i++) {
            String fromNode = cycle.get(i);
            String toNode = cycle.get((i + 1) % cycle.size()); // Замыкаем цикл

            GraphNode node = nodes.get(fromNode);
            if (node != null && node.getConnections().containsKey(toNode)) {
                totalWeight += node.getConnections().get(toNode);
            }
        }
        return totalWeight;
    }

    private Map<String, List<String>> buildAdjacencyList() {
        Map<String, List<String>> adjacencyList = new HashMap<>();
        for (String nodeName : nodes.keySet()) {
            GraphNode node = nodes.get(nodeName);
            List<String> neighbors = new ArrayList<>(node.getConnections().keySet());
            Collections.sort(neighbors);
            adjacencyList.put(nodeName, neighbors);
        }
        return adjacencyList;
    }

    public Map<String, double[]> runImpulseSimulation(Map<String, Double> initialImpulses, int steps) {
        // Reset all impulse values
        for (GraphNode node : nodes.values()) {
            node.resetImpulse();
        }

        // Apply initial impulses
        for (Map.Entry<String, Double> entry : initialImpulses.entrySet()) {
            if (nodes.containsKey(entry.getKey())) {
                nodes.get(entry.getKey()).setImpulseValue(entry.getValue());
            }
        }

        // Store results
        Map<String, double[]> results = new HashMap<>();
        for (String nodeName : nodes.keySet()) {
            results.put(nodeName, new double[steps]);
        }

        // Run simulation
        for (int step = 0; step < steps; step++) {
            // Store current values and record them
            Map<String, Double> currentValues = new HashMap<>();
            for (Map.Entry<String, GraphNode> entry : nodes.entrySet()) {
                double value = entry.getValue().getImpulseValue();
                currentValues.put(entry.getKey(), value);
                results.get(entry.getKey())[step] = value;
            }

            // Calculate next values
            for (GraphNode node : nodes.values()) {
                double newValue = 0.0;
                // Sum inputs from all connected nodes
                for (Map.Entry<String, GraphNode> sourceEntry : nodes.entrySet()) {
                    GraphNode sourceNode = sourceEntry.getValue();
                    Double weight = sourceNode.getConnections().get(node.getName());
                    if (weight != null) {
                        newValue += currentValues.get(sourceEntry.getKey()) * weight;
                    }
                }
                // Apply damping to prevent infinite growth
                newValue *= 0.95;
                node.setImpulseValue(Math.max(-1.0, Math.min(1.0, newValue)));
            }
        }

        return results;
    }
}

// JohnsonCycleFinder class
class JohnsonCycleFinder {
    private Map<String, List<String>> graph;
    private List<String> nodes;
    private Set<String> blocked;
    private Map<String, Set<String>> blockMap;
    private List<String> stack;
    private List<List<String>> cycles;

    public JohnsonCycleFinder(Map<String, List<String>> graph, List<String> nodes) {
        this.graph = graph;
        this.nodes = nodes;
    }

    public List<List<String>> findElementaryCycles() {
        cycles = new ArrayList<>();
        blocked = new HashSet<>();
        blockMap = new HashMap<>();
        stack = new ArrayList<>();

        // Initialize blockMap
        for (String node : nodes) {
            blockMap.put(node, new HashSet<>());
        }

        // Run Johnson's algorithm for each strongly connected component
        int startIndex = 0;
        while (startIndex < nodes.size()) {
            String startNode = nodes.get(startIndex);

            // Get subgraph reachable from startNode
            Map<String, List<String>> subgraph = getSubgraph(startNode);
            if (!subgraph.isEmpty()) {
                // Find cycles in this component
                findCyclesInComponent(startNode, startNode, subgraph);
                startIndex++;
            } else {
                startIndex++;
            }
        }

        return cycles;
    }

    private Map<String, List<String>> getSubgraph(String startNode) {
        Map<String, List<String>> subgraph = new HashMap<>();
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();

        queue.add(startNode);
        visited.add(startNode);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            List<String> neighbors = graph.get(current);
            if (neighbors != null) {
                List<String> reachableNeighbors = new ArrayList<>();
                for (String neighbor : neighbors) {
                    if (nodes.indexOf(neighbor) >= nodes.indexOf(startNode)) {
                        reachableNeighbors.add(neighbor);
                        if (!visited.contains(neighbor)) {
                            visited.add(neighbor);
                            queue.add(neighbor);
                        }
                    }
                }
                subgraph.put(current, reachableNeighbors);
            }
        }

        return subgraph;
    }

    private boolean findCyclesInComponent(String start, String current, Map<String, List<String>> component) {
        boolean foundCycle = false;
        stack.add(current);
        blocked.add(current);

        List<String> neighbors = component.get(current);
        if (neighbors != null) {
            for (String neighbor : neighbors) {
                if (neighbor.equals(start)) {
                    // Cycle found
                    List<String> cycle = new ArrayList<>(stack);
                    cycles.add(cycle);
                    foundCycle = true;
                } else if (!blocked.contains(neighbor)) {
                    boolean gotCycle = findCyclesInComponent(start, neighbor, component);
                    foundCycle = foundCycle || gotCycle;
                }
            }
        }

        if (foundCycle) {
            unblock(current);
        } else {
            for (String neighbor : neighbors) {
                if (neighbors != null) {
                    Set<String> blockSet = blockMap.get(neighbor);
                    if (blockSet != null) {
                        blockSet.add(current);
                    }
                }
            }
        }

        stack.remove(stack.size() - 1);
        return foundCycle;
    }

    private void unblock(String node) {
        blocked.remove(node);
        Set<String> blockSet = blockMap.get(node);
        if (blockSet != null) {
            for (String blockedNode : new HashSet<>(blockSet)) {
                if (blocked.contains(blockedNode)) {
                    unblock(blockedNode);
                }
            }
            blockSet.clear();
        }
    }
}

// GraphVisualization class
class GraphVisualization {
    private static final double NODE_RADIUS = 25;
    private static final double CIRCLE_RADIUS = 180;

    public void drawGraph(Pane pane, GraphModel graphModel) {
        Map<String, GraphNode> nodes = graphModel.getNodes();
        if (nodes.isEmpty()) return;

        Map<String, Point> positions = calculateNodePositions(nodes.keySet());
        drawConnections(pane, nodes, positions);
        drawNodes(pane, nodes, positions);
    }

    private Map<String, Point> calculateNodePositions(Set<String> nodeNames) {
        Map<String, Point> positions = new HashMap<>();
        List<String> nodesList = new ArrayList<>(nodeNames);
        int nodeCount = nodesList.size();

        for (int i = 0; i < nodeCount; i++) {
            double angle = 2 * Math.PI * i / nodeCount;
            double x = CIRCLE_RADIUS * Math.cos(angle) + 300;
            double y = CIRCLE_RADIUS * Math.sin(angle) + 200;
            positions.put(nodesList.get(i), new Point(x, y));
        }

        return positions;
    }

    private void drawConnections(Pane pane, Map<String, GraphNode> nodes, Map<String, Point> positions) {
        for (Map.Entry<String, GraphNode> entry : nodes.entrySet()) {
            String fromNode = entry.getKey();
            Point fromPos = positions.get(fromNode);

            for (Map.Entry<String, Double> connection : entry.getValue().getConnections().entrySet()) {
                String toNode = connection.getKey();
                double weight = connection.getValue();
                Point toPos = positions.get(toNode);

                drawConnection(pane, fromPos, toPos, weight, fromNode + "->" + toNode);
            }
        }
    }

    private void drawConnection(Pane pane, Point from, Point to, double weight, String label) {
        double dx = to.x - from.x;
        double dy = to.y - from.y;
        double length = Math.sqrt(dx * dx + dy * dy);

        dx /= length;
        dy /= length;

        double startX = from.x + dx * NODE_RADIUS;
        double startY = from.y + dy * NODE_RADIUS;
        double endX = to.x - dx * NODE_RADIUS;
        double endY = to.y - dy * NODE_RADIUS;

        Line line = new Line(startX, startY, endX, endY);

        // Применяем стили в зависимости от веса связи
        if (weight > 0) {
            line.getStyleClass().add("connection-positive");
            line.setStrokeWidth(Math.abs(weight) * 4 + 2);
        } else if (weight < 0) {
            line.getStyleClass().add("connection-negative");
            line.setStrokeWidth(Math.abs(weight) * 4 + 2);
        } else {
            line.getStyleClass().add("connection-neutral");
        }

        drawArrowHead(pane, endX, endY, dx, dy);

        // Стилизованная подпись веса
        Text weightText = new Text((startX + endX) / 2, (startY + endY) / 2 - 8,
                String.format("%.2f", weight));
        weightText.getStyleClass().add("weight-label");

        // Фон для текста
        javafx.scene.shape.Rectangle textBackground = new javafx.scene.shape.Rectangle();
        textBackground.getStyleClass().add("weight-label-background");
        textBackground.setWidth(weightText.getLayoutBounds().getWidth() + 10);
        textBackground.setHeight(weightText.getLayoutBounds().getHeight() + 6);
        textBackground.setX(weightText.getX() - 5);
        textBackground.setY(weightText.getY() - weightText.getLayoutBounds().getHeight() + 3);

        pane.getChildren().addAll(line, textBackground, weightText);
    }

    private void drawArrowHead(Pane pane, double x, double y, double dx, double dy) {
        double arrowLength = 15;
        double arrowWidth = 8;

        double perpX = -dy;
        double perpY = dx;

        double x1 = x - dx * arrowLength + perpX * arrowWidth;
        double y1 = y - dy * arrowLength + perpY * arrowWidth;
        double x2 = x - dx * arrowLength - perpX * arrowWidth;
        double y2 = y - dy * arrowLength - perpY * arrowWidth;

        Line arrow1 = new Line(x, y, x1, y1);
        Line arrow2 = new Line(x, y, x2, y2);

        arrow1.getStyleClass().add("connection-arrow");
        arrow2.getStyleClass().add("connection-arrow");

        pane.getChildren().addAll(arrow1, arrow2);
    }

    private void drawNodes(Pane pane, Map<String, GraphNode> nodes, Map<String, Point> positions) {
        for (Map.Entry<String, GraphNode> entry : nodes.entrySet()) {
            String nodeName = entry.getKey();
            Point pos = positions.get(nodeName);

            // Стилизованные узлы
            Circle circle = new Circle(pos.x, pos.y, NODE_RADIUS);
            circle.getStyleClass().add("graph-node");

            // Стилизованная подпись узла
            Text label = new Text(pos.x - 8, pos.y + 5, nodeName);
            label.getStyleClass().add("graph-node-label");

            // Добавляем обработчик hover эффекта
            circle.setOnMouseEntered(e -> {
                circle.setScaleX(1.1);
                circle.setScaleY(1.1);
            });

            circle.setOnMouseExited(e -> {
                circle.setScaleX(1.0);
                circle.setScaleY(1.0);
            });

            pane.getChildren().addAll(circle, label);
        }
    }

    private static class Point {
        double x, y;
        Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
}