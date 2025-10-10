package basic_ds;

import java.util.ArrayDeque;
import java.util.Deque;

public class Temp {
    public static void main(String[] args) {
        Deque<Integer> deque = new ArrayDeque<>();

// Add to front
        deque.addFirst(1);
        deque.offerFirst(2);

// Add to rear
        deque.addLast(3);
        deque.offerLast(4);

        System.out.println(deque);

// Remove from front
        Integer first = deque.removeFirst(); // or pollFirst()

// Remove from rear
        Integer last = deque.removeLast(); // or pollLast()

// Peek at both ends
        Integer peekFirst = deque.peekFirst();
        Integer peekLast = deque.peekLast();
    }
}
