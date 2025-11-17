package basic_ds.tree;

public class RecursiveTraversal {
    private void visit(BinaryTreeNode node){
        System.out.println(node.value());
    }
    public void preOrder(BinaryTreeNode root){
        if(root!=null){
            visit(root);
            preOrder(root.getLeft());
            preOrder(root.getRight());
        }
    }

    public void inOrder(BinaryTreeNode root){
        if(root!=null){
            inOrder(root.getLeft());
            visit(root);
            inOrder(root.getRight());
        }
    }

    public void postOrder(BinaryTreeNode root){
        if(root!=null){
            postOrder(root.getLeft());
            postOrder(root.getRight());
            visit(root);
        }
    }
}
