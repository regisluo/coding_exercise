package practice.tixi.binarytree;

import basic_ds.tree.BinaryTreeNode;

import java.util.LinkedList;
import java.util.Queue;

/**
 * max nodes of a BT is the max of level width;
 * level width is the number of nodes in this level
 */
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

    /**
     * Based on level-traversal, the max width is the biggest
     * queue size of a level
     */
    public int maxWidth2(BinaryTreeNode root) {
        if (root == null) {
            return 0;
        }
        Queue<BinaryTreeNode> queue = new LinkedList<>();
        queue.offer(root);
        int size = 0;
        BinaryTreeNode cur = null;
        int level = 1;
        int max = 0;
        while (!queue.isEmpty()) {
            size = queue.size();
            // process the current level by polling all nodes
            for (int i = 0; i < size; i++) {
                cur = queue.poll();
            }
            System.out.println(String.format("Level: %s, width:", level++, size));
            max = Math.max(max, size);
            // put left and right
            if (cur.left != null) queue.offer(cur.left);
            if (cur.right != null) queue.offer(cur.right);
        }
        return max;
    }
}
