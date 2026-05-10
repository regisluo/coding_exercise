package basic_ds.graph;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Generate into Graph instance per a input Matrix
 */
public class GraphGenerator {
    /**
     * @param matrix: an N*3 matrix, [weight, fromValue, toValue]
     * @return
     */
    public Graph generateGraph(int[][] matrix) {
        Graph graph = new Graph();
        HashSet<Edge> edges = new HashSet<>();
        HashMap<Integer, Node> nodes = new HashMap<>();

        int N = matrix.length;
        for (int i = 0; i < N; i++) {
            Integer weight = matrix[i][0];
            Integer from = matrix[i][1];
            Integer to = matrix[i][2];

            Node fromNode = new Node(from);
            Node toNode = new Node(to);
            Edge edge = new Edge(weight, fromNode, toNode);

            if (!graph.nodes.containsKey(from))
                graph.nodes.put(from, fromNode);
            if (!graph.nodes.containsKey(to))
                graph.nodes.put(to, toNode);

            fromNode.out++;
            fromNode.next.add(toNode);
            fromNode.edges.add(edge);
            toNode.in++;

            graph.edges.add(edge);
        }
        return graph;
    }
}
