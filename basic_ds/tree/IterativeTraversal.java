package basic_ds.tree;

import com.sun.source.tree.BinaryTree;
import java.util.ArrayDeque;
import java.util.Deque;

public class IterativeTraversal {

    // using a stack to implement pre-order traversal
    public void preOrderIterative(BinaryTreeNode root) {
        if (root != null) {
            Deque<BinaryTreeNode> stack = new ArrayDeque<>();
            stack.push(root);
            while (!stack.isEmpty()) {
                BinaryTreeNode node = stack.pop();
                node.visit();
                stack.push(node.right);
                stack.push(node.left);
            }
        }
    }

    public void inOrder(BinaryTreeNode root) {
        Deque<BinaryTreeNode> stack = new ArrayDeque<>();
        BinaryTreeNode cur = root;
        while (cur != null) {
            // find the bottom-left node
            if (cur != null || !stack.isEmpty()) {
                stack.push(cur);
                cur = cur.left;
            } else {
                cur = stack.pop();
                cur.visit();
                cur = cur.right;
            }
        }
    }

    /**
     * Post order is: left-right-root, the reverse order is: root-right-left, it is similar as preOrder which is
     * root-left-right.
     *
     * So, the solution can be based on preOrder, and save the processing sequency into a stack (root-right-left),
     * after all nodes are precessed, pop the stack elements which will be left-right-root which is postOrder.
     */
    public void postOrder(BinaryTreeNode root) {
        // firstly, let's implement the preOrder
        if (root != null) {
            Deque<BinaryTreeNode> tempStack = new ArrayDeque();
            Deque<BinaryTreeNode> resultStack = new ArrayDeque();
            tempStack.push(root);
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
