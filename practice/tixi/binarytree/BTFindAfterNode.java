package practice.tixi.binarytree;

import basic_ds.tree.BinaryTreeNode;

/**
 * 找到一个给定二叉树节点的后继节点:
 * 定义就是: 中序遍历中, 该节点的下一个节点.
 * 该Node中有parent指针
 */
public class BTFindAfterNode {

    /**
     * As there is 'parent' pointer, thus for a given node x, it can
     * go to any nodes in the tree. And we do not need to in-order traversal
     * the whole tree, but find out the 'after' node directly.
     * This will get a O(k) time complexity rather than O(n).
     */
    public BinaryTreeNode getAfterNode(BinaryTreeNode root, BinaryTreeNode x) {
        if (root == null || x == null)
            return null;
        BinaryTreeNode afterNode = null;
        // if x has right-sub-tree, then its 'after' node is the bottom-left node of the sub-tree
        if (x.right != null) {
            afterNode = getBottomLeftNode(x.right);
        } else {
            // if x is left child of its parent, then 'after' is its parent
            if (x == x.parent.left) {
                afterNode = x.parent;
            } else {
                // search upwards all the way to a node y where y is left-child of y's parent
                // and the 'after' node is y's parent node
                BinaryTreeNode y = getToppestLeftNode(x);
                afterNode = y.parent;
            }
        }
        return afterNode;
    }

    private BinaryTreeNode getToppestLeftNode(BinaryTreeNode x) {
        if (x == null) {
            return null;
        }
        while (x.parent != null && x.parent.right == x)
            x = x.parent;
        return x;
    }

    private BinaryTreeNode getBottomLeftNode(BinaryTreeNode node) {
        if (node == null) {
            return null;
        }
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }
}
