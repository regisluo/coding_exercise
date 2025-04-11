package basic_ds.list;

import basic_ds.list.ds.SingleNode;
import basic_ds.list.ds.SinglyLinkedList;

/**
 * 结论1：一个有环的链表，快指针和慢指针一定会在环上相遇
 * 结论2：一个指针从head开始，另一个指针从刚才相遇的地方开始，一路next，那么两个指针相遇的地方就是section node
 */
public class TX017_FindFirstSectionNodeFromList {
    //two-pointer technique

    /**
     * 1. check loop first
     * 2. if neigh of them looping, normal case
     * 3. if either of them looping, no intersection
     * 4. if both of them looping,
     */
    public static SingleNode getFirstSectionNode(SingleNode h1, SingleNode h2) {
        if (h1 == null || h2 == null)
            return null;
        else {
            SingleNode loopA = getLoop(h1);
            SingleNode loopB = getLoop(h2);
            if (loopA == null && loopB == null) { // both not looping
                return getFirstSectionNoLoop(h1, h2);
            }

            if ((loopA == null && loopB != null) || (loopA != null && loopB == null)) {// either
                return null;
            }
            if (loopA != null && loopB != null) { // both looping
                if (loopA == loopB) { // Case 1: Same loop entry point → They must have intersected before or at loopA
                    int lenA = getLoopLength(h1);
                    int lenB = getLoopLength(h2);
                    SingleNode curA = h1;
                    SingleNode curB = h2;
                    if (lenA > lenB) { // if lenA bigger, h1 move lenA-lenB steps
                        for (int i = 0; i < lenA - lenB; i++) {
                            curA = curA.next;
                        }
                    } else {
                        for (int i = 0; i < lenB - lenA; i++) {
                            curB = curB.next;
                        }
                    }
                    // now, curA and curB are same far away from the interaction point
                    while (curA != null && curB != null) {
                        if (curA == curB)
                            return curA; // find the intersection point
                        curA = curA.next;
                        curB = curB.next;
                    }
                } else {
                    SingleNode cur = loopA.next;
                    while (cur != loopA) {
                        if (cur == loopB) {
                            return cur;
                        }
                        cur = cur.next;
                    }
                }

            }
            return null;
        }
    }

    public static int getLoopLength(SingleNode head) {
        SingleNode loopNode = getLoop(head);
        if (null != loopNode) { // has loop
            SingleNode cur = head;
            int len = 0;
            boolean meetLoopingPoint = false;
            while (true) {
                len++;
                if (cur == loopNode) {
                    if (!meetLoopingPoint)
                        meetLoopingPoint = true; // first meet
                    else
                        return len-1;
                }
                cur = cur.next;
            }
        }
        return 0;
    }

    /**
     * 结论1：一个有环的链表，快指针和慢指针一定会在环上相遇!!!
     * 结论2：一个指针从head开始，另一个指针从刚才相遇的地方开始，一路next，那么两个指针相遇的地方就是section node
     */
    // return the first node of the loop, return null if no loop
    public static SingleNode getLoop(SingleNode head) {
        if (head == null)
            return null;
        SingleNode slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) { // circle detected.
                slow = head; // slow move back to the head
                while (slow != fast) { // move slow and fast one step by one step
                    slow = slow.next;
                    fast = fast.next;
                }
                return slow;
            }
        }
        return null;
    }


    // return first intersection node if both the list no loop
    public static SingleNode getFirstSectionNoLoop(SingleNode h1, SingleNode h2) {
        return null;
    }

    // return first intersection node if either or both of the lists with loop
    public static SingleNode getFirstSectionNodeWithLoop(SingleNode h1, SingleNode h2) {
        return null;
    }

    public static void main(String[] args) {
        SinglyLinkedList list = new SinglyLinkedList();
        SingleNode n1 = list.add(1);
        SingleNode n2 = list.add(2);
        SingleNode n3 = list.add(3);
        SingleNode n4 = list.add(4);
        SingleNode n5 = list.add(5);
        SingleNode n6 = list.add(6);
        SingleNode n7 = list.add(7);
        SingleNode n8 = list.add(8);
        SingleNode n9 = list.add(9);
        n9.next = n5;
        System.out.println("len 1: " + getLoopLength(n1));
        System.out.println(getLoop(n1).value);

        SinglyLinkedList list2 = new SinglyLinkedList();
        SingleNode n10 = list2.add(10);
        SingleNode n11 = list2.add(11);
        SingleNode n12 = list2.add(12);

        n12.next = n7;
//        n12.next = n4;
        System.out.println("len 2: " + getLoopLength(n10));

        System.out.println(getLoop(n10).value);

        System.out.println(getFirstSectionNode(n1, n10).value);

    }
}
