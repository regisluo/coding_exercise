package practice.leetcode.seventyfive75.binaryTree;

public class LeafSimilarTrees_872 {
    /**
     * 二叉树套路解题: 递归
     */
    public boolean leafSimilar(TreeNode root1, TreeNode root2) {
        return travel(root1).equals(travel(root2));
    }

    public String travel(TreeNode root) {
        if (root == null) return "";
        if (root.left == null && root.right == null) {
            return root.val + "-";
        }
        return travel(root.left) + travel(root.right);
    }

    public class TreeNode {
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
