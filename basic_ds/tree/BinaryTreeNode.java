package basic_ds.tree;

public class BinaryTreeNode {
    public int value;
    public BinaryTreeNode left;
    public BinaryTreeNode right;
    public BinaryTreeNode parent; //optional with Parent Reference

    public BinaryTreeNode(int value) {
        this.value = value;
        this.left = null;
        this.right = null;
        this.parent = null;
    }

    // Getters and Setters
    public int value() {
        return value;
    }

    public BinaryTreeNode getLeft() {
        return left;
    }
    public void setLeft(BinaryTreeNode left) {
        this.left = left;
    }


    public BinaryTreeNode getRight() {
        return right;
    }
    public void setRight(BinaryTreeNode right) {
        this.right = right;
    }

    public void visit(){
        System.out.println(this.value);
    }

}
