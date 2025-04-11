package basic_ds.stack.ds;

public class AStack {
    int[] data;
    int size;
    int length;
    int top = -1; // index pointing to the top element of the stack

    public AStack(int length) {
        this.length = length;
        // init the store of data
        this.data = new int[this.length];
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
    public void add(int v) throws Exception {
        if(this.size == this.length) {
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
