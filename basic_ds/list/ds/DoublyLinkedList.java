package basic_ds.list.ds;

/**
 * Doubly linked list implementation.
 * Normally it has a <b>head</b> pointer and a size variable.
 * But if needed, it can also have a <b>tail<b/> pointer.
 * It supports add, delete, size, display operations.
 */
public class DoublyLinkedList {
    private DoubleNode head;
    public int size;

    // construct
    public DoublyLinkedList() {
        this.head = null;
        this.size = 0;
    }

    public int size() {
        return this.size;
    }

    public void add(int v){
        if (this.head == null) {
            this.head = new DoubleNode(v);
        } else {
            // find the last node
            DoubleNode cur = this.head;
            while (cur.next != null) {
                cur= cur.next;
            }
            // create a new node
            DoubleNode node = new DoubleNode(v);
            node.pre = cur;
            cur.next = node;
        }
        this.size ++;
    }
    
    public void delete(int v) {
        if (this.head !=null) {
            if (this.head.value == v) {
                // delete head
                if (this.size == 1) {// only one node
                    this.head = null;
                } else {
                    DoubleNode cur = head.next;
                    cur.pre = null;
                    this.head = cur;
                }
                this.size --;
            } else {
                // find the node with value of v
                DoubleNode cur = this.head;
                while (cur != null && cur.value != v) {
                    cur = cur.next;
                }
                if (cur != null) { // find the node
                    if (cur.next == null) { // last node
                        cur.pre.next = null;
                    } else {
                        cur.next.pre = cur.pre;
                        cur.pre.next= cur.next;
                    }
                    cur.pre = null;
                    cur.next = null;
                    cur = null;
                }
            }
        } else {
            // empty list
            System.out.println("empty list");
        }

    }

    public void display(){
        DoubleNode cur = this.head;
        while (cur!=null) {
            System.out.print(cur.value + " -> ");
            cur = cur.next;
        }
        System.out.println(" null");
    }

    public static void main(String[] args) {
        DoublyLinkedList list = new DoublyLinkedList();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.display();

        list.add(5);
        list.add(6);
        list.display();

        list.delete(2);
        list.display();

        list.delete(3);
        list.delete(6);
        list.display();
        list.delete(1);
        list.display();
    }
}
