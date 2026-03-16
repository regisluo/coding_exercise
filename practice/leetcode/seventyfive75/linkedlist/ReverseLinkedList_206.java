package practice.leetcode.seventyfive75.linkedlist;

import basic_ds.list.ds.SingleNode;

/**
 * Given the head of a singly linked list, reverse the list,
 * and return the reversed list.
 */
public class ReverseLinkedList_206 {
    /**
     * 3 pointer solution: pre, cur and next (this can be a temp pointer)
     */
    public SingleNode reverseList(SingleNode head) {
        SingleNode pre = null;
        SingleNode cur = head;
        while (cur != null) {
            SingleNode temp = cur.next; // remember the node after cur
            // change the pointer
            cur.next = pre;
            pre = cur;
            cur = temp;
        }
        return pre;
    }
}
