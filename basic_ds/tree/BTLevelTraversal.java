package basic_ds.tree;

import java.util.LinkedList;
import java.util.Queue;

public class BTLevelTraversal {

    public void levelTraversal(BinaryTreeNode root) {
        if (root == null) {
            return;
        }
        Queue<BinaryTreeNode> queue = new LinkedList<>();
        queue.offer(root); // add node to the queue first
        while (!queue.isEmpty()) {
            BinaryTreeNode node = queue.poll();
            node.visit();
            if (node.left != null) { // has left child add to queue
                queue.offer(node.left);
            }
            if (node.right != null) { // has right child add to queue
                queue.offer(node.right);
            }
        }
    }
}
