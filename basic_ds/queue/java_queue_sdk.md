# Java Queue SDK - Interview Reference

## Queue Overview

### Definition
A **Queue** is a linear data structure that follows the **FIFO (First In, First Out)** principle. The first element added to the queue is the first one to be removed.
#### In all, there are 4 most often usage:
- Queue<Integer> queue = new ArrayDeque<>();
- Deque<Integer> deque = new ArrayDeque<>();
- PriorityQueue<Integer> minHeap = new PriorityQueue<>();
- PriorityQueue<Integer> maxHeap = new PriorityQueue<>((a,b)->b-a);
### Real-World Analogy
Think of a line at a coffee shop - the first person in line is the first person served.

---

## Java Queue Implementations

### 1. LinkedList (Implements Queue)
```java
import java.util.LinkedList;
import java.util.Queue;

Queue<Integer> queue = new LinkedList<>();
```

### 2. ArrayDeque (Recommended)
```java
import java.util.ArrayDeque;
import java.util.Deque;

// As Queue
Queue<Integer> queue = new ArrayDeque<>();
// As Deque (double-ended queue)
Deque<Integer> deque = new ArrayDeque<>();
```

### 3. PriorityQueue (Heap-based)
```java
import java.util.PriorityQueue;

// Min heap by default
PriorityQueue<Integer> pq = new PriorityQueue<>();
// Max heap with custom comparator
PriorityQueue<Integer> maxHeap = new PriorityQueue<>((a, b) -> b - a);
```

### 4. Custom Array-based Implementation
```java
class ArrayQueue<T> {
    private Object[] array;
    private int front;
    private int rear;
    private int size;
    private int capacity;
    
    public ArrayQueue(int capacity) {
        this.capacity = capacity;
        this.array = new Object[capacity];
        this.front = 0;
        this.rear = -1;
        this.size = 0;
    }
    
    public boolean offer(T item) {
        if (isFull()) {
            return false;
        }
        rear = (rear + 1) % capacity;
        array[rear] = item;
        size++;
        return true;
    }
    
    @SuppressWarnings("unchecked")
    public T poll() {
        if (isEmpty()) {
            return null;
        }
        T item = (T) array[front];
        array[front] = null; // Help GC
        front = (front + 1) % capacity;
        size--;
        return item;
    }
    
    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) {
            return null;
        }
        return (T) array[front];
    }
    
    public boolean isEmpty() {
        return size == 0;
    }
    
    public boolean isFull() {
        return size == capacity;
    }
    
    public int size() {
        return size;
    }
}
```

### 5. Custom Linked List Implementation
```java
class LinkedQueue<T> {
    private Node<T> front;
    private Node<T> rear;
    private int size;
    
    private static class Node<T> {
        T data;
        Node<T> next;
        
        Node(T data) {
            this.data = data;
        }
    }
    
    public boolean offer(T item) {
        Node<T> newNode = new Node<>(item);
        
        if (isEmpty()) {
            front = rear = newNode;
        } else {
            rear.next = newNode;
            rear = newNode;
        }
        size++;
        return true;
    }
    
    public T poll() {
        if (isEmpty()) {
            return null;
        }
        
        T data = front.data;
        front = front.next;
        
        if (front == null) {
            rear = null; // Queue is now empty
        }
        
        size--;
        return data;
    }
    
    public T peek() {
        return isEmpty() ? null : front.data;
    }
    
    public boolean isEmpty() {
        return front == null;
    }
    
    public int size() {
        return size;
    }
}
```

---

## Core Queue Operations

### Basic Operations with Examples

#### 1. Offer/Add Operation (Enqueue)
```java
Queue<String> queue = new ArrayDeque<>();

// Add elements to rear of queue
queue.offer("First");
queue.offer("Second");
queue.offer("Third");
// Queue: [First, Second, Third] (front to rear)

System.out.println("Queue after offers: " + queue);
// Output: [First, Second, Third]

// Alternative method (throws exception on failure)
queue.add("Fourth");
```

#### 2. Poll/Remove Operation (Dequeue)
```java
// Remove and return front element
String frontElement = queue.poll(); // Returns "First"
System.out.println("Polled: " + frontElement);
// Queue: [Second, Third, Fourth]

// Alternative method (throws exception if empty)
String second = queue.remove(); // Returns "Second"
// Queue: [Third, Fourth]
```

#### 3. Peek/Element Operation
```java
// View front element without removing
String front = queue.peek(); // Returns "Third"
System.out.println("Front element: " + front);
// Queue still: [Third, Fourth]

// Alternative method (throws exception if empty)
String frontAlt = queue.element(); // Same as peek() but throws exception
```

#### 4. Check if Empty
```java
boolean empty = queue.isEmpty();
System.out.println("Is empty: " + empty); // false

queue.poll(); // Remove "Third"
queue.poll(); // Remove "Fourth"
System.out.println("Is empty: " + queue.isEmpty()); // true
```

#### 5. Size Operation
```java
queue.offer("A");
queue.offer("B");
queue.offer("C");

int size = queue.size();
System.out.println("Queue size: " + size); // 3
```

---

## Method Comparison Table

| Operation | Throws Exception | Returns Special Value |
|-----------|------------------|----------------------|
| **Insert** | `add(e)` | `offer(e)` |
| **Remove** | `remove()` | `poll()` |
| **Examine** | `element()` | `peek()` |

### Implementation Comparison
| Method | LinkedList | ArrayDeque | PriorityQueue |
|--------|------------|------------|---------------|
| **Offer** | `offer(e)` | `offer(e)` | `offer(e)` |
| **Poll** | `poll()` | `poll()` | `poll()` |
| **Peek** | `peek()` | `peek()` | `peek()` |
| **Size** | `size()` | `size()` | `size()` |

---

## Deque (Double-ended Queue) Operations

### Additional Deque Methods
```java
Deque<Integer> deque = new ArrayDeque<>();

// Add to front
deque.addFirst(1);
deque.offerFirst(2);

// Add to rear
deque.addLast(3);
deque.offerLast(4);

// Remove from front
Integer first = deque.removeFirst(); // or pollFirst()

// Remove from rear
Integer last = deque.removeLast(); // or pollLast()

// Peek at both ends
Integer peekFirst = deque.peekFirst();
Integer peekLast = deque.peekLast();
```

---

## Priority Queue Examples

### 1. Min Heap (Default)
```java
PriorityQueue<Integer> minHeap = new PriorityQueue<>();
minHeap.offer(5);
minHeap.offer(2);
minHeap.offer(8);
minHeap.offer(1);

while (!minHeap.isEmpty()) {
    System.out.println(minHeap.poll()); // Output: 1, 2, 5, 8
}
```

### 2. Max Heap with Comparator
```java
PriorityQueue<Integer> maxHeap = new PriorityQueue<>((a, b) -> b - a);
maxHeap.offer(5);
maxHeap.offer(2);
maxHeap.offer(8);
maxHeap.offer(1);

while (!maxHeap.isEmpty()) {
    System.out.println(maxHeap.poll()); // Output: 8, 5, 2, 1
}
```

### 3. Custom Objects with Priority
```java
class Task implements Comparable<Task> {
    String name;
    int priority;
    
    Task(String name, int priority) {
        this.name = name;
        this.priority = priority;
    }
    
    @Override
    public int compareTo(Task other) {
        return Integer.compare(this.priority, other.priority);
    }
    
    @Override
    public String toString() {
        return name + "(" + priority + ")";
    }
}

PriorityQueue<Task> taskQueue = new PriorityQueue<>();
taskQueue.offer(new Task("Low", 3));
taskQueue.offer(new Task("High", 1));
taskQueue.offer(new Task("Medium", 2));

while (!taskQueue.isEmpty()) {
    System.out.println(taskQueue.poll()); // High(1), Medium(2), Low(3)
}
```

---

## Interview-Specific Examples

### 1. Basic Queue Usage Pattern
```java
public void demonstrateBFS() {
    Queue<Integer> queue = new ArrayDeque<>();
    
    // BFS-like processing
    queue.offer(1);
    
    while (!queue.isEmpty()) {
        int current = queue.poll();
        System.out.println("Processing: " + current);
        
        // Add neighbors/children
        if (current < 5) {
            queue.offer(current * 2);
            queue.offer(current * 2 + 1);
        }
    }
}
```

### 2. Level-Order Tree Traversal
```java
public List<List<Integer>> levelOrder(TreeNode root) {
    List<List<Integer>> result = new ArrayList<>();
    if (root == null) return result;
    
    Queue<TreeNode> queue = new ArrayDeque<>();
    queue.offer(root);
    
    while (!queue.isEmpty()) {
        int levelSize = queue.size();
        List<Integer> currentLevel = new ArrayList<>();
        
        for (int i = 0; i < levelSize; i++) {
            TreeNode node = queue.poll();
            currentLevel.add(node.val);
            
            if (node.left != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }
        
        result.add(currentLevel);
    }
    
    return result;
}
```

### 3. Sliding Window Maximum with Deque
```java
public int[] maxSlidingWindow(int[] nums, int k) {
    Deque<Integer> deque = new ArrayDeque<>(); // Store indices
    int[] result = new int[nums.length - k + 1];
    
    for (int i = 0; i < nums.length; i++) {
        // Remove indices outside window
        while (!deque.isEmpty() && deque.peekFirst() < i - k + 1) {
            deque.pollFirst();
        }
        
        // Remove smaller elements from rear
        while (!deque.isEmpty() && nums[deque.peekLast()] < nums[i]) {
            deque.pollLast();
        }
        
        deque.offerLast(i);
        
        if (i >= k - 1) {
            result[i - k + 1] = nums[deque.peekFirst()];
        }
    }
    
    return result;
}
```

---

## Performance Characteristics

### Time Complexity
| Operation | LinkedList | ArrayDeque | PriorityQueue |
|-----------|------------|------------|---------------|
| Offer | O(1) | O(1) amortized | O(log n) |
| Poll | O(1) | O(1) | O(log n) |
| Peek | O(1) | O(1) | O(1) |
| Size | O(1) | O(1) | O(1) |
| Contains | O(n) | O(n) | O(n) |

### Space Complexity
- **LinkedList**: O(n) - each element has node overhead
- **ArrayDeque**: O(n) - more memory efficient than LinkedList
- **PriorityQueue**: O(n) - array-based heap implementation

---

## Common Interview Scenarios

### 1. When to Use Different Queue Types
```java
// Regular Queue (FIFO):
// - BFS traversal
// - Level-order processing
// - Task scheduling

// Deque (Double-ended):
// - Sliding window problems
// - Palindrome checking
// - Undo/Redo operations

// Priority Queue:
// - Dijkstra's algorithm
// - Top K problems
// - Merge K sorted lists
```

### 2. Queue vs Other Data Structures
```java
// Use Queue when:
// - FIFO processing needed
// - BFS traversal
// - Level-by-level processing
// - Buffering operations

// Don't use Queue when:
// - Need random access (use Array/List)
// - LIFO access needed (use Stack)
// - Need frequent middle insertions/deletions
```

### 3. Common Pitfalls in Interviews
```java
public void commonMistakes() {
    Queue<Integer> queue = new ArrayDeque<>();
    
    // ❌ Not checking if empty before poll/peek
    // Integer value = queue.poll(); // Could return null
    
    // ❌ Using element() instead of peek() without null check
    // Integer front = queue.element(); // Throws exception if empty
    
    // ❌ Forgetting level size in BFS
    // while (!queue.isEmpty()) {
    //     TreeNode node = queue.poll(); // Wrong - processes all levels together
    // }
    
    // ✅ Correct patterns
    if (!queue.isEmpty()) {
        Integer value = queue.peek();
        // Process safely
    }
    
    // ✅ Correct BFS level processing
    int levelSize = queue.size();
    for (int i = 0; i < levelSize; i++) {
        // Process current level only
    }
}
```

---

## Interview Code Templates

### Template 1: BFS Traversal
```java
public void bfsTraversal(Node start) {
    Queue<Node> queue = new ArrayDeque<>();
    Set<Node> visited = new HashSet<>();
    
    queue.offer(start);
    visited.add(start);
    
    while (!queue.isEmpty()) {
        Node current = queue.poll();
        
        // Process current node
        processNode(current);
        
        // Add unvisited neighbors
        for (Node neighbor : current.getNeighbors()) {
            if (!visited.contains(neighbor)) {
                queue.offer(neighbor);
                visited.add(neighbor);
            }
        }
    }
}
```

### Template 2: Level-Order Processing
```java
public List<List<T>> levelOrderProcessing(Node root) {
    List<List<T>> result = new ArrayList<>();
    if (root == null) return result;
    
    Queue<Node> queue = new ArrayDeque<>();
    queue.offer(root);
    
    while (!queue.isEmpty()) {
        int levelSize = queue.size();
        List<T> currentLevel = new ArrayList<>();
        
        for (int i = 0; i < levelSize; i++) {
            Node node = queue.poll();
            currentLevel.add(node.getValue());
            
            // Add children for next level
            addChildren(queue, node);
        }
        
        result.add(currentLevel);
    }
    
    return result;
}
```

### Template 3: Priority Queue for Top K Problems
```java
public List<T> topKElements(T[] elements, int k) {
    PriorityQueue<T> minHeap = new PriorityQueue<>();
    
    for (T element : elements) {
        minHeap.offer(element);
        
        if (minHeap.size() > k) {
            minHeap.poll(); // Remove smallest
        }
    }
    
    return new ArrayList<>(minHeap);
}
```

---

## Best Practices for Interviews

### 1. Choose the Right Implementation
```java
// For general queue operations
Queue<Integer> queue = new ArrayDeque<>(); // ✅ Preferred

// For double-ended operations
Deque<Integer> deque = new ArrayDeque<>(); // ✅ Best performance

// For priority-based processing
PriorityQueue<Integer> pq = new PriorityQueue<>(); // ✅ When order matters

// Avoid LinkedList for performance reasons unless specifically needed
Queue<Integer> slow = new LinkedList<>(); // ❌ Slower than ArrayDeque
```

### 2. Always Handle Empty Queue
```java
// Safe polling
Integer value = queue.isEmpty() ? null : queue.poll();

// Or check before operations
if (!queue.isEmpty()) {
    processElement(queue.poll());
}
```

### 3. Level-Order Processing Pattern
```java
// Always capture level size before inner loop
int levelSize = queue.size(); // ✅ Correct
for (int i = 0; i < levelSize; i++) {
    // Process current level
}

// Don't use queue.size() in loop condition
for (int i = 0; i < queue.size(); i++) { // ❌ Wrong - size changes
}
```

### 4. Common Interview Questions to Practice
```java
// Easy:
// - Implement Queue using Stacks
// - Moving Average from Data Stream
// - First Unique Character in String

// Medium:
// - Binary Tree Level Order Traversal
// - Perfect Squares (BFS)
// - Sliding Window Maximum

// Hard:
// - Word Ladder
// - Shortest Path in Binary Matrix
// - Merge K Sorted Lists (Priority Queue)
```

This comprehensive reference covers all essential Queue concepts and implementations needed for coding interviews, with practical examples and common patterns.
