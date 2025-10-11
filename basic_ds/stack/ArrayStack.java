package basic_ds.stack;

public class ArrayStack {
    int[] data;
    int size;
    int capacity;
    int top = -1; // index pointing to the top element of the stack

    public ArrayStack(int capacity) {
        this.capacity = capacity;
        // init the store of data
        this.data = new int[this.capacity];
        this.size = 0; // initial count of elements in the stack
    }
    // return the top
    public int pop() throws Exception {
        if (this.size == 0) {
            throw new Exception("Empty stack");
        }
        this.size--;
        int topData = this.data[this.top];
        this.top--;
        return topData;
    }

    public int peek() throws Exception {
        if (this.size == 0) {
            throw new Exception("Empty stack");
        }
        return this.data[this.top];
    }

    // add element v into the Stack, return
    public void push(int v) throws Exception {
        if(this.size == this.capacity) {
            throw new Exception("Stack is full");
        }
        this.top++;
        this.size++;
        this.data[top] = v;
    }

    public int size(){
        return this.size;
    }

    public boolean isEmpty(){
        return this.size == 0;
    }
}
