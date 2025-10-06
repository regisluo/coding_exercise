# Introduction to Queue in Java

## Overview
The Queue interface in Java is part of the Collections Framework and represents a collection designed for holding elements prior to processing. Queues typically order elements in a **FIFO (First-In-First-Out)** manner, though priority queues order elements according to a supplied comparator or natural ordering.

## Queue Interface Methods

### Core Operations
```java
// Adding elements
boolean add(E e)        // Throws exception if fails
boolean offer(E e)      // Returns false if fails

// Removing elements
E remove()              // Throws exception if empty
E poll()                // Returns null if empty

// Examining elements
E element()             // Throws exception if empty
E peek()                // Returns null if empty
```

### Method Comparison
| Operation | Throws Exception | Returns Special Value |
|-----------|------------------|----------------------|
| Insert    | add(e)          | offer(e)             |
| Remove    | remove()        | poll()               |
| Examine   | element()       | peek()               |

## Common Queue Implementations for Coding Interviews

### 1. LinkedList (Most Common)
**Use Case**: General-purpose queue, allows null elements

```java
import java.util.*;

Queue<Integer> queue = new LinkedList<>();
queue.offer(1);
queue.offer(2);
queue.offer(3);

System.out.println(queue.poll()); // Output: 1
System.out.println(queue.peek()); // Output: 2
System.out.println(queue.size()); // Output: 2
```

**Time Complexity**:
- Insertion: O(1)
- Deletion: O(1)
- Access: O(1) for front element

### 2. ArrayDeque (Recommended)
**Use Case**: More efficient than LinkedList, resizable array implementation

```java
Queue<String> queue = new ArrayDeque<>();
queue.offer("first");
queue.offer("second");
queue.offer("third");

while (!queue.isEmpty()) {
    System.out.println(queue.poll());
}
// Output: first, second, third
```

**Advantages**:
- No memory overhead of linked nodes
- Better cache locality
- Faster than LinkedList for most operations

### 3. PriorityQueue (Heap-based)
**Use Case**: When elements need to be processed in priority order

```java
// Natural ordering (min-heap for integers)
PriorityQueue<Integer> minHeap = new PriorityQueue<>();
minHeap.offer(5);
minHeap.offer(2);
minHeap.offer(8);
minHeap.offer(1);

while (!minHeap.isEmpty()) {
    System.out.println(minHeap.poll());
}
// Output: 1, 2, 5, 8

// Custom comparator (max-heap)
PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
maxHeap.offer(5);
maxHeap.offer(2);
maxHeap.offer(8);
maxHeap.offer(1);

while (!maxHeap.isEmpty()) {
    System.out.println(maxHeap.poll());
}
// Output: 8, 5, 2, 1
```

**Time Complexity**:
- Insertion: O(log n)
- Deletion: O(log n)
- Peek: O(1)

## Common Interview Patterns

### 1. BFS (Breadth-First Search)
```java
public void bfs(TreeNode root) {
    if (root == null) return;
    
    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);
    
    while (!queue.isEmpty()) {
        TreeNode current = queue.poll();
        System.out.println(current.val);
        
        if (current.left != null) queue.offer(current.left);
        if (current.right != null) queue.offer(current.right);
    }
}
```

### 2. Level Order Traversal
```java
public List<List<Integer>> levelOrder(TreeNode root) {
    List<List<Integer>> result = new ArrayList<>();
    if (root == null) return result;
    
    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);
    
    while (!queue.isEmpty()) {
        int levelSize = queue.size();
        List<Integer> currentLevel = new ArrayList<>();
        
        for (int i = 0; i < levelSize; i++) {
            TreeNode current = queue.poll();
            currentLevel.add(current.val);
            
            if (current.left != null) queue.offer(current.left);
            if (current.right != null) queue.offer(current.right);
        }
        
        result.add(currentLevel);
    }
    
    return result;
}
```

### 3. Sliding Window Maximum
```java
public int[] maxSlidingWindow(int[] nums, int k) {
    Deque<Integer> deque = new ArrayDeque<>(); // Using Deque as double-ended queue
    int[] result = new int[nums.length - k + 1];
    
    for (int i = 0; i < nums.length; i++) {
        // Remove elements outside window
        while (!deque.isEmpty() && deque.peekFirst() < i - k + 1) {
            deque.pollFirst();
        }
        
        // Remove smaller elements from rear
        while (!deque.isEmpty() && nums[deque.peekLast()] < nums[i]) {
            deque.pollLast();
        }
        
        deque.offerLast(i);
        
        // Add to result when window is complete
        if (i >= k - 1) {
            result[i - k + 1] = nums[deque.peekFirst()];
        }
    }
    
    return result;
}
```

### 4. Top K Elements using PriorityQueue
```java
public int[] topKFrequent(int[] nums, int k) {
    Map<Integer, Integer> frequencyMap = new HashMap<>();
    for (int num : nums) {
        frequencyMap.put(num, frequencyMap.getOrDefault(num, 0) + 1);
    }
    
    // Min-heap based on frequency
    PriorityQueue<Integer> heap = new PriorityQueue<>(
        (a, b) -> frequencyMap.get(a) - frequencyMap.get(b)
    );
    
    for (int num : frequencyMap.keySet()) {
        heap.offer(num);
        if (heap.size() > k) {
            heap.poll();
        }
    }
    
    int[] result = new int[k];
    for (int i = k - 1; i >= 0; i--) {
        result[i] = heap.poll();
    }
    
    return result;
}
```

## Deque (Double-Ended Queue)
The `Deque` interface extends `Queue` and allows insertion and removal at both ends.

```java
Deque<Integer> deque = new ArrayDeque<>();

// Add to front and rear
deque.offerFirst(1);
deque.offerLast(2);
deque.offerFirst(0);

// Deque state: [0, 1, 2]

System.out.println(deque.pollFirst()); // Output: 0
System.out.println(deque.pollLast());  // Output: 2
System.out.println(deque.pollFirst()); // Output: 1
```

**Common Deque Methods**:
- `offerFirst(e)`, `offerLast(e)` - Add elements
- `pollFirst()`, `pollLast()` - Remove elements
- `peekFirst()`, `peekLast()` - Examine elements

## Interview Tips

### 1. Choose the Right Implementation
- **LinkedList**: When you need a simple FIFO queue
- **ArrayDeque**: When you need better performance than LinkedList
- **PriorityQueue**: When you need elements in priority order
- **Deque**: When you need access to both ends

### 2. Common Mistakes to Avoid
```java
// ❌ Wrong: Using poll() without null check
Queue<Integer> queue = new LinkedList<>();
int value = queue.poll(); // NullPointerException if autoboxing

// ✅ Correct: Check for null or use peek() first
if (!queue.isEmpty()) {
    int value = queue.poll();
}

// ❌ Wrong: Using element() on empty queue
int front = queue.element(); // Throws exception if empty

// ✅ Correct: Use peek() for null-safe access
Integer front = queue.peek(); // Returns null if empty
```

### 3. Performance Considerations
| Implementation | Add/Remove | Peek | Space |
|---------------|------------|------|-------|
| LinkedList    | O(1)       | O(1) | O(n)  |
| ArrayDeque    | O(1)*      | O(1) | O(n)  |
| PriorityQueue | O(log n)   | O(1) | O(n)  |

*Amortized time complexity

### 4. Common Interview Questions
1. **Implement Queue using Stacks**
2. **Implement Stack using Queues**
3. **Design Circular Queue**
4. **Find Kth Largest Element** (using PriorityQueue)
5. **Level Order Traversal** (BFS)
6. **Sliding Window Maximum** (using Deque)

## Summary
- Queue follows FIFO principle
- `LinkedList` and `ArrayDeque` are most common implementations
- `PriorityQueue` for heap-based priority ordering
- `Deque` for double-ended queue operations
- Choose implementation based on specific requirements
- Always handle empty queue cases properly
