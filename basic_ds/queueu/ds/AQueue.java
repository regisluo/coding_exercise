package basic_ds.queueu.ds;

public class AQueue {
    int[] data;
    int size;
    int length;
    int head = -1;
    int rear = 0;

    public AQueue(int length) {
        this.length = length;
        this.size = 0;
        this.head = -1;
        this.data = new int[this.length];
    }

    public void enQueue(int v) throws Exception {
        if (this.size == this.length) {
            throw new Exception("Queue is full");
        }
        this.data[this.rear] = v;
        this.rear = (this.rear + 1) % this.length;
        this.size++;
    }

    public int deQueue() throws Exception {
        if (this.size == 0) {
            throw new Exception("Queue is empty");
        }
        int ans = this.data[this.head];
        this.size--;
        this.head = (this.head + 1) % this.length;
        return ans;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public int size() {
        return this.size;
    }

    public boolean isFull(){
        return this.size == this.length;
    }
}
