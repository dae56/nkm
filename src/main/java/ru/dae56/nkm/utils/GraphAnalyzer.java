package ru.dae56.nkm.utils;

import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import ru.dae56.nkm.graphElements.GraphLink;
import ru.dae56.nkm.controllers.HelloController;

import java.util.*;

public class GraphAnalyzer {

    private final AnchorPane anchorPane;

    public GraphAnalyzer(AnchorPane anchorPane) {
        this.anchorPane = anchorPane;
    }

    public void analyzeCycles() {
        Map<String, List<String>> graph = new HashMap<>();
        Map<String, Map<String, Double>> weights = new HashMap<>();

        // Строим граф и веса
        for (GraphLink link : HelloController.getAllLinks()) {
            String fromId = link.getFrom().getId();
            String toId = link.getTo().getId();
            double w = link.getWeight();

            graph.computeIfAbsent(fromId, k -> new ArrayList<>()).add(toId);
            weights.computeIfAbsent(fromId, k -> new HashMap<>()).put(toId, w);
        }

        Set<List<String>> rawCycles = new HashSet<>();
        for (String nodeId : graph.keySet()) {
            findCycles(nodeId, nodeId, new ArrayList<>(), rawCycles, graph);
        }

        List<String> positiveCycles = new ArrayList<>();
        List<String> negativeCycles = new ArrayList<>();

        for (List<String> cycle : rawCycles) {
            double product = 1.0;
            boolean valid = true;

            for (int i = 0; i < cycle.size() - 1; i++) {
                String from = cycle.get(i);
                String to = cycle.get(i + 1);
                Double w = weights.getOrDefault(from, Collections.emptyMap()).get(to);
                if (w == null) {
                    valid = false;
                    break;
                }
                product *= w;
            }

            if (valid) {
                String cycleStr = String.join("->", cycle);
                if (product > 0) positiveCycles.add(cycleStr);
                else if (product < 0) negativeCycles.add(cycleStr);
            }
        }

        // Формируем вывод
        StringBuilder result = new StringBuilder();
        result.append("Общее количество циклов: ").append(positiveCycles.size() + negativeCycles.size()).append("\n\n");

        result.append("Положительные циклы: ").append(positiveCycles.size()).append("\n");
        for (int i = 0; i < positiveCycles.size(); i++) {
            result.append(i).append(" - ").append(positiveCycles.get(i)).append("\n");
        }

        result.append("\nОтрицательные циклы: ").append(negativeCycles.size()).append("\n");
        for (int i = 0; i < negativeCycles.size(); i++) {
            result.append(i).append(" - ").append(negativeCycles.get(i)).append("\n");
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Анализ циклов");
        alert.setHeaderText("Результаты анализа:");
        alert.setContentText(result.toString());
        alert.setResizable(true);
        alert.getDialogPane().setPrefWidth(600);
        alert.showAndWait();
    }

    private void findCycles(String start, String current, List<String> path,
                            Set<List<String>> allCycles, Map<String, List<String>> graph) {
        path.add(current);

        for (String neighbor : graph.getOrDefault(current, Collections.emptyList())) {
            if (neighbor.equals(start) && path.size() > 1) {
                // Проверяем, что переход current → start существует
                if (graph.getOrDefault(current, Collections.emptyList()).contains(start)) {
                    List<String> cycle = new ArrayList<>(path);
                    cycle.add(start); // замыкаем
                    normalizeCycle(cycle);
                    allCycles.add(cycle);
                }
            } else if (!path.contains(neighbor)) {
                findCycles(start, neighbor, path, allCycles, graph);
            }
        }

        path.remove(path.size() - 1);
    }

    private void normalizeCycle(List<String> cycle) {
        if (cycle.isEmpty()) return;
        int minIndex = 0;
        for (int i = 1; i < cycle.size() - 1; i++) {
            if (cycle.get(i).compareTo(cycle.get(minIndex)) < 0) minIndex = i;
        }
        Collections.rotate(cycle, -minIndex);
    }
}
