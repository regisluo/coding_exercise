package basic_ds.list.ds;

public class DoubleNode {
    public int value;
    public DoubleNode pre;
    public DoubleNode next;

    public DoubleNode(int value) {
        this.value = value;
        this.pre = null;
        this.next = null;
    }
}
