import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;

import java.util.*;

public class KruskalVisualizer {
    static class EdgeData {
        String node1, node2;
        int weight;

        EdgeData(String n1, String n2, int w) {
            this.node1 = n1;
            this.node2 = n2;
            this.weight = w;
        }
    }

    static class DisjointSet {
        private final Map<String, String> parent = new HashMap<>();

        public void makeSet(String node) {
            parent.put(node, node);
        }

        public String find(String node) {
            if (!parent.get(node).equals(node)) {
                parent.put(node, find(parent.get(node)));
            }
            return parent.get(node);
        }

        public void union(String node1, String node2) {
            String root1 = find(node1);
            String root2 = find(node2);
            if (!root1.equals(root2)) {
                parent.put(root1, root2);
            }
        }
    }

    public static void main(String[] args) {
        // Enable GraphStream display
        System.setProperty("org.graphstream.ui", "swing");

        Graph graph = new SingleGraph("Kruskal's MST");
        graph.setAttribute("ui.stylesheet", styleSheet());
        graph.setAutoCreate(true);
        graph.setStrict(false);

        // Define edges: a, b, weight
        List<EdgeData> edges = Arrays.asList(
                new EdgeData("A", "B", 4),
                new EdgeData("A", "C", 2),
                new EdgeData("B", "C", 5),
                new EdgeData("B", "D", 10),
                new EdgeData("C", "E", 3),
                new EdgeData("E", "D", 4),
                new EdgeData("D", "F", 11)
        );

        // Add all nodes
        Set<String> nodes = new HashSet<>();
        for (EdgeData edge : edges) {
            nodes.add(edge.node1);
            nodes.add(edge.node2);
        }
        for (String node : nodes) {
            Node n = graph.addNode(node);
            n.setAttribute("ui.label", node);
        }

        // Sort edges by weight
        edges.sort(Comparator.comparingInt(e -> e.weight));

        DisjointSet ds = new DisjointSet();
        for (String node : nodes) {
            ds.makeSet(node);
        }

        int count = 0;
        for (EdgeData edge : edges) {
            String root1 = ds.find(edge.node1);
            String root2 = ds.find(edge.node2);
            String edgeId = edge.node1 + "-" + edge.node2;

            // Add the edge visually first (gray), update color later if in MST
            Edge e = graph.addEdge(edgeId, edge.node1, edge.node2);
            e.setAttribute("ui.label", String.valueOf(edge.weight));
            e.setAttribute("ui.class", "default");

            if (!root1.equals(root2)) {
                ds.union(edge.node1, edge.node2);
                e.setAttribute("ui.class", "mst"); // green edge
                count++;
            }

            if (count == nodes.size() - 1) break;
        }

        graph.display();
    }

    private static String styleSheet() {
        return "graph {"
                + "padding: 50px;"
                + "fill-color: #1e1e2f;"  // background color
                + "}"
                + "node {"
                + "size: 25px;"
                + "fill-color: red;"
                + "text-color: white;"
                + "text-size: 18px;"
                + "text-style: bold;"
                + "stroke-mode: plain;"
                + "stroke-color: black;"
                + "}"
                + "edge.default {"
                + "fill-color: gray;"
                + "text-color: yellow;"
                + "text-size: 16px;"
                + "text-style: bold;"
                + "}"
                + "edge.mst {"
                + "fill-color: limegreen;"
                + "size: 3px;"
                + "}";
    }
}
