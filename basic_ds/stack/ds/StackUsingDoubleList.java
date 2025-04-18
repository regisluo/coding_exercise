package basic_ds.stack.ds;

import basic_ds.list.ds.DoubleNode;

public class StackUsingDoubleList {
    DoubleNode top;
    int size;

    public StackUsingDoubleList(){
        this.top = null;
        this.size = 0;
    }

    public int pop() throws Exception {
        if (this.size==0){
            throw new Exception("Stack is empty");
        }
        int top = 0;

        return top;
    }

}
