package basic_ds.list;

import basic_ds.list.ds.SingleNode;
import basic_ds.list.ds.SinglyLinkedList;

/**
 * return the middle of the list
 */
public class FindMiddleOfList {
    public static SingleNode findMiddle(SingleNode head) {
        if (head == null) {
            return null;
        } else {
            // fast and slow pointer
            SingleNode fast = head;
            SingleNode slow = head;
            while (fast.next!=null && fast.next.next != null) {
                fast = fast.next.next;
                slow = slow.next;
            }
            // here the fast is in the last node
            // and slow is in the middle
            return slow;
        }
    }

    public static void main(String[] args) {
        SinglyLinkedList list = new SinglyLinkedList();
        SingleNode head = list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        list.add(6);
        list.add(7);
        list.add(8);
        list.add(9);
        System.out.println(findMiddle(head).value);

    }
}
