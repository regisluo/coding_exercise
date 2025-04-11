package basic_ds.list.ds;

/**
 * It is a class of singly linked list. It is built upon the singlenode.
 * As a singly linked list, we only need a Head, and then create some operations.
 * 单链表操作最常见的方法就是，设置一个cur=head开始遍历。最常用的判断就是：
 * while(cur!=null): 遍历所有节点，跳出循环，cur=null。
 * while(cur.next!=null): 跳出循环，cur在最后一个结点。
 *
 * 上面两个条件，根据情况，灵活处理
 */
public class SinglyLinkedList {
    public SingleNode head;
    private int size;

    // constructor
    public SinglyLinkedList() {
        head = null;
        this.size = 0;
    }

    public int size(){
        return this.size;
    }

    public SingleNode add(int v) {
        SingleNode n = new SingleNode(v);
        if (this.head == null) {
            head = n;
        } else {
            SingleNode cur = head;
            while (cur.next!=null) {
                cur = cur.next;
            }
            cur.next = n;
        }
        this.size++;
        return n;
    }

    public void remove(int v) {
        if(this.head == null)
            return; // empty list
        SingleNode cur = this.head;
        SingleNode pre = cur;
        while (cur!=null) {
            if (cur.value == v) {
                if(cur == head) { // remove head
                    pre = pre.next;
                    head = pre;
                } else if (cur.next ==null) {// last node
                    pre.next = null;
                    cur = null;
                } else {//general case
                    pre.next = cur.next;
                    cur = null;
                }
                this.size --;
                return;
            }
            pre = cur;
            cur = cur.next;
        }
    }

    public void display(){
        SingleNode cur = this.head;
        while (cur!=null) {
            System.out.print(cur.value + " -> ");
            cur = cur.next;
        }
        System.out.println(" null");
    }

    public static void main(String[] args) {
        SinglyLinkedList list = new SinglyLinkedList();
        list.add(2);
        list.add(3);
        list.add(5);
        list.add(6);
        list.display();

        list.remove(6);
        list.display();
    }

}
