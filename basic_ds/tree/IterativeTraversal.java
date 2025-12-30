package basic_ds.tree;

import java.util.ArrayDeque;
import java.util.Deque;

public class IterativeTraversal {

    // using a stack to implement pre-order traversal
    public void preOrderIterative(BinaryTreeNode root) {
        if (root != null) {
            Deque<BinaryTreeNode> stack = new ArrayDeque<>();
            stack.push(root); // push head first
            while (!stack.isEmpty()) {
                BinaryTreeNode node = stack.pop();
                node.visit();
                if (node.right != null) stack.push(node.right);
                if (node.left != null) stack.push(node.left);
            }
        }
    }

    // l, node, r
    // each of the node will be visited once so time complexity is O(n)
    public void inOrder(BinaryTreeNode root) {
        BinaryTreeNode cur = root;
        Deque<BinaryTreeNode> stack = new ArrayDeque<>();

        //for each of sub-tree, apply the same logic
        while (cur != null || !stack.isEmpty()) {
            // find the bottom-left node
            while (cur != null) {
                stack.push(cur);
                cur = cur.left;
            }

            // visit the left node
            cur = stack.pop();
            cur.visit();

            // go to the right-subTree
            cur = cur.right;
        }
    }

    /**
     * Post order is: left-right-root, the reverse order is: root-right-left, it is similar as preOrder which is
     * root-left-right.
     * <p>
     * So, the solution can be based on preOrder, and save the processing sequency into a stack (root-right-left),
     * after all nodes are precessed, pop the stack elements which will be left-right-root which is postOrder.
     */
    public void postOrder(BinaryTreeNode root) {
        // firstly, let's implement the preOrder
        if (root != null) {
            Deque<BinaryTreeNode> tempStack = new ArrayDeque();
            Deque<BinaryTreeNode> resultStack = new ArrayDeque();
            tempStack.push(root); // push head first
            while (!tempStack.isEmpty()) {
                BinaryTreeNode node = tempStack.pop();
                resultStack.push(node);
                tempStack.push(node.left);
                tempStack.push(node.right);
            }

            while (!resultStack.isEmpty()) {
                resultStack.pop().visit();
            }
        }
    }
}
