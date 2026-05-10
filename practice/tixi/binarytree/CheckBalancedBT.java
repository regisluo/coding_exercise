package practice.tixi.binarytree;

import basic_ds.tree.BinaryTreeNode;

// 利用二叉树递归套路解题: 分析定义信息体Info
public class CheckBalancedBT {
    public boolean isBalancedBT(BinaryTreeNode root) {
        return this.process(root).isBalanced;
    }

    public static class Info {
        boolean isBalanced;
        int height;

        public Info(boolean b, int h) {
            this.isBalanced = b;
            this.height = h;
        }
    }

    public Info process(BinaryTreeNode x) {
        Info leftInfo = process(x.left);
        Info rightInfo = process(x.right);

        boolean isBalanced = true;
        if (!leftInfo.isBalanced
                || !rightInfo.isBalanced
                || Math.abs(leftInfo.height - rightInfo.height) > 1)
            isBalanced = false;

        int height = Math.max(leftInfo.height, rightInfo.height) + 1;

        Info info = new Info(isBalanced, height);
        return info;

    }
}
