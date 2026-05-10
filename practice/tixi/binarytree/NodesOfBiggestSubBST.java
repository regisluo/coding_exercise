package practice.tixi.binarytree;

import basic_ds.tree.BinaryTreeNode;

// 返回最大BST子树的节点个数. 即整棵树可能不是BST, 但子树可能是, 而且有多个;
// 返回最多的子树节点数.
public class NodesOfBiggestSubBST {

    public int maxNodesOfSubBST(BinaryTreeNode root) {
        Info info = process(root);
        if (info == null) return 0;
        else return info.maxNodes;
    }

    private Info process(BinaryTreeNode x) {
        // base case
        if (x == null) {
            return null;
        }
        Info linfo = process(x.left);
        Info rinfo = process(x.right);
        boolean isBST = true;

        int max = 0;
        int min = 0;
        int maxNodes;


        if (linfo != null) {
            max = Math.max(linfo.max, x.value);
            min = Math.min(linfo.min, x.value);
            isBST = linfo.isBST && linfo.max <= x.value;
        }
        if (rinfo != null) {
            max = Math.max(rinfo.max, x.value);
            min = Math.min(rinfo.min, x.value);
            isBST = rinfo.isBST && rinfo.min > x.value;
        }
        // p1,p2: they are two cases that X not involved as BST
        // if X is not bst, thus the maxNodes comes from max(p1, p2)
        int p1 = -1;
        if (linfo != null) {
            p1 = linfo.maxNodes;
        }

        int p2 = -1;
        if (rinfo != null) {
            p2 = rinfo.maxNodes;
        }

        // X is BST root
        int p3 = -1;
        if (linfo != null && rinfo != null && linfo.isBST && rinfo.isBST) {
            p3 = linfo.maxNodes + rinfo.maxNodes + 1;
        }

        maxNodes = Math.max(Math.max(p1, p2), p3);
        return new Info(isBST, max, min, maxNodes);
    }

    static class Info {
        boolean isBST;
        int max;
        int min;
        int maxNodes;

        public Info(boolean isBST, int max, int min, int maxNodes) {
            this.isBST = isBST;
            this.max = max;
            this.min = min;
            this.maxNodes = maxNodes;
        }
    }
}
