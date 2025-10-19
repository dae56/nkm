package ru.dae56.nkm.memento;
import java.util.List;

public class GraphMemento {

    public static class NodeState {
        public final String id;
        public final double x, y;
        public final String label;

        public NodeState(String id, double x, double y, String label) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.label = label;
        }
    }

    public static class LinkState {
        public final String fromId;
        public final String toId;
        public final double weight;

        public LinkState(String fromId, String toId, double weight) {
            this.fromId = fromId;
            this.toId = toId;
            this.weight = weight;
        }
    }

    private final List<NodeState> nodes;
    private final List<LinkState> links;

    public GraphMemento(List<NodeState> nodes, List<LinkState> links) {
        this.nodes = nodes;
        this.links = links;
    }

    public GraphMemento() {
        this.nodes = null;
        this.links = null;
    }

    public List<NodeState> getNodes() { return nodes; }
    public List<LinkState> getLinks() { return links; }
}
