package basic_ds.stack.ds;

import basic_ds.queueu.ds.ArrayQueue;

import java.util.ArrayDeque;
import java.util.Queue;

public class StackUsingQueue<T> {
    Queue<T> dataQueue = new ArrayDeque<>();
    Queue<T> helpQueue = new ArrayDeque<>();

    public void push(T v) {
        // always push new element into helpQueue
        helpQueue.offer(v);
        // copy all elements in dataQueue into helpQueue
        while (!dataQueue.isEmpty()) {
            helpQueue.offer(dataQueue.poll());
        }
        // swap the two queues
        Queue<T> tmp = dataQueue;
        dataQueue = helpQueue;
        helpQueue = tmp;
    }

    public T pop() throws Exception {
        if (dataQueue.isEmpty())
            throw new Exception("Stack is empty");
        return this.dataQueue.poll();
    }

    public T top() throws Exception {
        if (dataQueue.isEmpty())
            throw new Exception("Stack is empty");
        return this.dataQueue.peek();
    }

    public boolean isEmpty() {
        return this.dataQueue.isEmpty();
    }

}
