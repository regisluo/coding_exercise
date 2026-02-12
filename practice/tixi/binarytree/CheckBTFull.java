package practice.tixi.binarytree;

import basic_ds.tree.BinaryTreeNode;

import java.util.LinkedList;
import java.util.Queue;

public class CheckBTFull {
    /**
     * check if a BT full or not. Non-recursive way.
     * the solution is based on analysis that:
     * 1. if a node has right child but on left, then false;
     * 2. if meet a leaf node, then all nodes after should be leaf nodes; if not, then false;
     */
    public boolean isBTFull(BinaryTreeNode head) {
        if (head == null)
            return true;
        boolean ans = true;
        Queue<BinaryTreeNode> queue = new LinkedList<>();
        queue.offer(head);
        boolean firstLeaf = false;
        while (!queue.isEmpty()) {
            BinaryTreeNode node = queue.poll();
            // check logic
            if (node.right != null && node.left == null) {
                ans = false;
            } else {
                // if encounter leaf before, and either the left or right child is not null, return false
                if (firstLeaf && (node.right != null || node.left != null))
                    ans = false;
            }
            if (node.left != null)
                queue.offer(node.left);
            if (node.right != null)
                queue.offer(node.right);

            if (node.left == null && node.right == null && firstLeaf == false) {
                // first leaf node
                firstLeaf = true;
            }
        }
        return ans;
    }

}
