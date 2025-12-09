package basic_ds.tree;
import java.util.LinkedList;
import java.util.Queue;

public class BTSerialize {

    // serialize the BT into a queue using pre-order
    public Queue<String> preserialize(BinaryTreeNode root) {
        Queue<String> ans = new LinkedList<>();
        process(ans, root);
        return ans;
    }

    private void process(Queue<String> queue, BinaryTreeNode node) {
        if (node == null) {
            queue.add("#");
        } else {
            queue.add(String.valueOf(node.value));
            process(queue, node.left);
            process(queue, node.right);
        }
    }
}
