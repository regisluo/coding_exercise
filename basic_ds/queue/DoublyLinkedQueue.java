package basic_ds.queue;

import basic_ds.list.ds.DoubleNode;

public class DoublyLinkedQueue {
    DoubleNode head;
    DoubleNode tail;
    int size;

    public void enqueue(int value) {
        DoubleNode node = new DoubleNode(value);
        if (this.size == 0) {
            this.head = node;
            this.tail = node;
        } else  {
            this.tail.next = node;
            node.pre = this.tail;
            this.tail = node;
        }
        this.size ++;
    }

    public int dequeue() throws Exception {
        if (this.size == 0) {
            throw new Exception("Queue is empty");
        }
        int value = this.head.value;
        this.head = this.head.next;
        if (this.head == null) {
            this.tail = null;
        } else {
            this.head.pre = null;
        }
        this.size--;
        return value;
    }

    public int peek() throws Exception {
        if (this.size == 0) {
            throw new Exception("Queue is empty");
        }
        return this.head.value;
    }

    public int size() {
        return this.size;
    }

    public boolean isEmpty(){
        return this.size == 0;
    }
}
