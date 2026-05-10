package basic_ds.graph;

public class Edge {
    private Node from;
    private Node to;
    private int weight;

    public Edge(int weight, Node f, Node t) {
        this.weight = weight;
        this.from = f;
        this.to = t;
    }
}
