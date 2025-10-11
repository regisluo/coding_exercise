package basic_ds.stack;

import basic_ds.list.ds.DoubleNode;

/**
 * A Stack implemented using DoubleNode
 */
public class DoublyLinkedStack {
    DoubleNode head;
    DoubleNode tail;
    int size;

    public void push(int value){
        // generate the value node
        DoubleNode node = new DoubleNode(value);
        if (this.size == 0) {
            this.head = node;
            this.tail = node;
        } else {
            this.tail.next = node;
            node.pre = this.tail;
            this.tail = node;
        }
        this.size ++;
    }

    public int pop() throws Exception {
        int value = 0;
        if (this.size == 0) {
            throw new Exception("Stack is empty");
        } else {
            value = this.tail.value; // the value to be returned

            this.tail = this.tail.pre;
            if (tail == null) { // tail is the last node
                head = null;
            } else {
                tail.next = null;
            }
            this.size --;
        }
        return value;
    }

    public int peek() throws Exception {
        if (this.size == 0) {
            throw new Exception("Stack is empty");
        }
        return this.tail.value;
    }


    public boolean isEmpty() {
        return this.size == 0;
    }

    /**
     * return count of element in the stack
     */
    public int size() {
        return size;
    }

}
