package practice.temp;

import basic_ds.tree.BinaryTreeNode;

import java.util.LinkedList;
import java.util.Queue;

public class BTMaxL {
    public int maxWidth(BinaryTreeNode root) {
        if (root == null) {
            return 0;
        }
        int maxWid = 0;
        // we can use BFS to find the max width. One question is how
        // we can know if the level is changed or not.
        // using a queue as help to do the level traversal
        Queue<BinaryTreeNode> queue = new LinkedList<>();
        queue.offer(root);
        int levelWidth = 0;
        while (!queue.isEmpty()) {
            levelWidth = queue.size(); // width of this level
            for (int i = 0; i < levelWidth; i++) { // process this level
                BinaryTreeNode node = queue.poll();
                if (node.left != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }
            // here, this level just finish processing
            maxWid = maxWid > levelWidth ? maxWid : levelWidth;
        }
        return maxWid;
    }
    
}
