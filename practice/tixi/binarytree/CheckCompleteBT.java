package practice.tixi.binarytree;

import basic_ds.tree.BinaryTreeNode;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 是否完全二叉树?
 * A Complete Binary tree is a full BT apart from the last level nodes;
 * The nodes on the last level are from left to right.
 */
public class CheckCompleteBT {
    public boolean isCBT_LevelTraversal(BinaryTreeNode root) {
        // if a null is seen, all after should be null;
        // let's first do a level traversal
        if (root == null) {
            return false;
        }
        Queue<BinaryTreeNode> que = new LinkedList<>();
        que.offer(root);
        boolean meetNull = false;
        while (!que.isEmpty()) {
            BinaryTreeNode cur = que.poll();
            // add the left and right into queue regardless null or not
            if (cur != null) {
                if (meetNull)
                    return false;
                else {
                    que.offer(cur.left);
                    que.offer(cur.right);
                }
            } else {
                meetNull = true;
            }
        }
        return true;
    }

    // a given index i, its left is 2*i, right is 2*i+1
    public boolean isCBT_index_based(BinaryTreeNode root) {
        if (root == null) {
            return false;
        }
        Queue<Info> que = new LinkedList<>();
        que.offer(new Info(root, 0));
        int index = 0;
        int nodeCount = 0;
        while (!que.isEmpty()) {
            Info cur = que.poll();
            nodeCount++;
            index = cur.index;
            if (cur.node != null && cur.node.left != null) que.offer(new Info(cur.node.left, 2 * index));
            if (cur.node != null && cur.node.right != null) que.offer(new Info(cur.node.right, 2 * index + 1));
        }
        if (index > nodeCount)
            return false;
        else
            return true;
    }

    private static class Info {
        BinaryTreeNode node;
        int index;

        public Info(BinaryTreeNode node, int index) {
            this.node = node;
            this.index = index;
        }
    }

    // 二叉树递归套路
    public boolean isCBTtaolu(BinaryTreeNode root) {
        Info2 info2 = process(root);
        if (info2 != null)
            return info2.isCBT;
        else
            return false;
    }

    public Info2 process(BinaryTreeNode node) {
        if (node == null) {
            return null;
        }
        Info2 linfo = process(node.left);
        Info2 rinfo = process(node.right);
        // update below for the current node
        boolean isCBT = false;
        if (linfo != null && rinfo != null &&
                linfo.isCBT && rinfo.isCBT && ((linfo.height == rinfo.height) || (linfo.height == rinfo.height + 1)))
            isCBT = true;
        int height = Math.max(linfo.height, rinfo.height) + 1;
        return new Info2(isCBT, height);

    }

    private static class Info2 {
        boolean isCBT;
        int height;

        public Info2(boolean iscbt, int h) {
            this.isCBT = iscbt;
            this.height = h;
        }
    }
}
