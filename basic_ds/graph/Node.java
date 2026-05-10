package basic_ds.graph;

import java.util.ArrayList;
import java.util.List;

public class Node {
    public int value;
    public int in;
    public int out;
    public List<Node> next;
    public List<Edge> edges;

    public Node(int v) {
        this.value = v;
        this.in = 0;
        this.out = 0;
        this.next = new ArrayList<>();
        this.edges = new ArrayList<>();
    }
}
