package practice.tixi.binarytree;

import basic_ds.tree.BinaryTreeNode;

/**
 * Recursive solution:
 * use the left-child + right-sibling representation, where the
 * first child becomes the left node, and siblings are chained
 * using the right pointer. This preserves structure while fitting
 * into a binary tree.
 */
public class NaryTreeEncode {

    public BinaryTreeNode encode(NaryTreeNode root) {
        if (root == null)
            return null;
        BinaryTreeNode binaryTreeRoot = new BinaryTreeNode(root.value);
        if (root.children != null && !root.children.isEmpty()) {
            // get the first child and set to BT's left
            binaryTreeRoot.left = encode(root.children.get(0));
            BinaryTreeNode cur = binaryTreeRoot.left;
            for (int i = 1; i < root.children.size(); i++) {
                // update cur's right which is the encoded value of each child from index 1
                cur.right = encode(root.children.get(i));
                cur = cur.right;
            }
        }
        return binaryTreeRoot;
    }

    public NaryTreeNode decode(BinaryTreeNode root) {
        if (root == null) {
            return null;
        }
        NaryTreeNode ans = new NaryTreeNode(root.value);
        BinaryTreeNode cur = root.left;
        while (cur != null) {
            ans.children.add(decode(cur));
            cur = cur.right;
        }
        return ans;
    }


}
