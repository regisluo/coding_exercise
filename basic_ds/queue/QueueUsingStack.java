package basic_ds.queue;

import java.util.Stack;

public class QueueUsingStack<T> {
    Stack<T> inStack = new Stack<>();
    Stack<T> outStack = new Stack<>();
    int size;

    public void enqueue(T t){
       inStack.push(t);
       this.size++;
    }

    public T dequeue() throws Exception{
       moveInNeed();
       this.size--;
       return outStack.pop();
    }

    private void moveInNeed() {
        if (outStack.isEmpty()){
            while (!inStack.isEmpty()){
                outStack.push(inStack.pop());
            }
        }
    }

    public T peek() throws Exception {
        moveInNeed();
        return outStack.peek();
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public static void main(String[] args) throws Exception {
        QueueUsingStack<Integer> queue = new QueueUsingStack<>();
        queue.enqueue(1);
        queue.enqueue(2);
        queue.enqueue(3);

        System.out.println(queue.dequeue()); // 1
        System.out.println(queue.peek());    // 2
        System.out.println(queue.dequeue()); // 2
    }
}
