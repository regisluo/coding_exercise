package basic_ds.tree;

import java.util.LinkedList;
import java.util.Queue;

public class BTMaxWidth {

    public int maxWidth(BinaryTreeNode root) {
        if (root == null) {
            return 0;
        }
        // based on level-traversal
        Queue<BinaryTreeNode> queue = new LinkedList<>();
        queue.offer(root);
        int maxWidth = 0;
        int curWidth = 0; // record width of current level
        // last node of current level
        BinaryTreeNode curEnd = root;

        // last node of next level
        BinaryTreeNode nextEnd = null;

        while (!queue.isEmpty()) {
            BinaryTreeNode cur = queue.poll();
            curWidth++;

            if (cur.left != null) {
                queue.offer(cur.left);
                nextEnd = cur.left;
            }
            if (cur.right != null) {
                queue.offer(cur.right);
                nextEnd = cur.right;
            }
            if (cur == curEnd) {
                maxWidth = maxWidth > curWidth ? maxWidth : curWidth;
                curWidth = 0;
                curEnd = nextEnd;
            }
        }
        return maxWidth;
    }
}
