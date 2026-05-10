package practice.tixi.binarytree;

import basic_ds.tree.BinaryTreeNode;

// 利用二叉树递归套路解题: 分析定义信息体Info
public class CheckBST {

    public boolean isBST(BinaryTreeNode root) {
        Info info = this.process(root);
        if (info != null) {
            return info.isBST;
        } else {
            return false;
        }
    }

    public static class Info {
        boolean isBST;
        // MAX(x.value, left-max, right-max)
        int max; // the max value of the sub-tree rooted of this node
        // MIN(x.value, left-min, right-min)
        int min; // the min value of the sub-tree rooted of this node

        public Info(boolean isBST, int max, int min) {
            this.isBST = isBST;
            this.max = max;
            this.min = min;
        }
    }

    private Info process(BinaryTreeNode x) {
        // base case
        if (x == null)
            return null;
        Info linfo = process(x.left);
        Info rinfo = process(x.right);

        // need to update the 3 parameters
        boolean isBST = true;
        int max = 0;
        int min = 0;

        isBST = true;
        if (linfo != null && !linfo.isBST) {
            isBST = false;
        }

        if (rinfo != null && !rinfo.isBST) {
            isBST = false;
        }
        if (linfo != null && linfo.max >= x.value) {
            isBST = false;
        }
        if (rinfo != null && rinfo.min < x.value) {
            isBST = false;
        }
        // max is the MAX among left max, right max and x.value
        max = x.value;
        min = x.value;

        if (linfo != null) {
            // if linfo, update both min and max with linfo
            min = Math.min(linfo.min, min);
            max = Math.max(linfo.max, max);
        }
        if (rinfo != null) {
            // if rinfo, update both min and max with rinfo
            min = Math.min(rinfo.min, min);
            max = Math.max(rinfo.max, max);
        }

        Info info = new Info(isBST, max, min);
        return info;
    }

}
