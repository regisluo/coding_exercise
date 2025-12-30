package basic_ds.tree;

import java.util.List;

public class NaryTreeNode {
    int value;
    List<NaryTreeNode> children;

    NaryTreeNode(int value) {
        this.value = value;
    }

    public void visit() {
    }
}
