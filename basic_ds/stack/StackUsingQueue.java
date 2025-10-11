package basic_ds.stack;

import java.util.ArrayDeque;
import java.util.Queue;

public class StackUsingQueue<T> {
    Queue<T> dataQueue = new ArrayDeque<>();
    Queue<T> helpQueue = new ArrayDeque<>();

    public void push(T v) {
        dataQueue.offer(v);
    }

    public T pop() throws Exception {
        if (dataQueue.isEmpty())
            throw new Exception("Stack is empty");
        while (dataQueue.size() > 1) {
            helpQueue.offer(dataQueue.poll());
        }
        T ret = dataQueue.poll();
        // swap the two queues
        Queue<T> tmp = dataQueue;
        dataQueue = helpQueue;
        helpQueue = tmp;
        return ret;
    }

    public T peek() throws Exception {
        if (dataQueue.isEmpty())
            throw new Exception("Stack is empty");
        while (dataQueue.size() > 1) {
            helpQueue.offer(dataQueue.poll());
        }
        T ret = dataQueue.poll();
        helpQueue.offer(ret);
        // swap the two queues
        Queue<T> tmp = dataQueue;
        dataQueue = helpQueue;
        helpQueue = tmp;
        return ret;
    }

}
