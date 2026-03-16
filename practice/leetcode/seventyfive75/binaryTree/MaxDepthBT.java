package practice.leetcode.seventyfive75.binaryTree;

import com.sun.source.tree.ParenthesizedTree;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

public class MaxDepthBT {
    /**
     * recursive solution: maxDepth = maxLeft + maxRight + 1
     */
    public int maxDepth1(TreeNode root) {
        if (root == null)
            return 0;
        int maxLeft = maxDepth1(root.left);
        int maxRight = maxDepth1(root.right);
        return Math.max(maxLeft, maxRight) + 1;
    }

    // based on level traversal
    public int maxDepth2(TreeNode root) {
        if (root == null)
            return 0;
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        int ans = 0;
        while (!queue.isEmpty()) {
            int size = queue.size(); //the size of current level
            for (int i = 0; i < size; i++) {
                TreeNode cur = queue.poll();
                if (cur.left != null) queue.offer(cur.left);
                if (cur.right != null) queue.offer(cur.right);
            }
            ans++;// all nodes are processed for current level

        }
        return ans;
    }

    // based on DFS
    public int maxDepth3(TreeNode root) {
        if (root == null) {
            return 0;
        }
        Deque<Pair<TreeNode, Integer>> stack = new ArrayDeque<>();
        stack.push(new Pair<>(root, 1));
        int max = 0;
        while (!stack.isEmpty()) {
            Pair<TreeNode, Integer> cur = stack.pop();
            max = Math.max(max, cur.depth);

            if (cur.node.right != null)
                stack.push(new Pair(cur.node.right, max + 1));
            if (cur.node.left != null)
                stack.push(new Pair<>(cur.node.left, max + 1));
        }
        return max;
    }

    static class Pair<TreeNode, Integer> {
        TreeNode node;
        int depth;

        public Pair(TreeNode node, int depth) {
            this.node = node;
            this.depth = depth;
        }
    }

    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode() {
        }

        TreeNode(int val) {
            this.val = val;
        }

        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }
}

