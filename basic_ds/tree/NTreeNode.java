package basic_ds.tree;

import java.util.List;

public class NTreeNode {
    int value;
    List<NTreeNode> children;

    public NTreeNode(int value, List<NTreeNode> children) {
        this.value = value;
        this.children = children;
    }
}
