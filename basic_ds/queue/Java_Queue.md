# Queue Data Structure - Interview Guide

## Queue Basics
**FIFO (First In, First Out)** principle
**Core Operations:** `offer()/add()`, `poll()/remove()`, `peek()/element()`, `isEmpty()`, `size()`

---

## When to Use Queue - Key Triggers

### 1. **Level-Order/BFS Traversal**
- Binary tree level-order traversal
- Graph BFS algorithms
- Shortest path problems

### 2. **Sliding Window Problems**
- Maximum in sliding window
- Moving average calculations
- First unique character in stream

### 3. **Task Scheduling**
- Process scheduling algorithms
- Rate limiting implementations
- Cache eviction policies

### 4. **Stream Processing**
- Real-time data processing
- Buffer management
- Producer-consumer patterns

### 5. **State Management**
- Undo/Redo operations (with deque)
- Game state tracking
- Event processing systems

---

## Common Interview Patterns & Solutions

### 1. Binary Tree Level-Order Traversal

#### **Basic Level-Order Traversal**
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

#### **Zigzag Level-Order Traversal**
```java
public List<List<Integer>> zigzagLevelOrder(TreeNode root) {
    List<List<Integer>> result = new ArrayList<>();
    if (root == null) return result;
    
    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);
    boolean leftToRight = true;
    
    while (!queue.isEmpty()) {
        int levelSize = queue.size();
        List<Integer> currentLevel = new ArrayList<>();
        
        for (int i = 0; i < levelSize; i++) {
            TreeNode node = queue.poll();
            
            if (leftToRight) {
                currentLevel.add(node.val);
            } else {
                currentLevel.add(0, node.val); // Add at beginning
            }
            
            if (node.left != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }
        
        result.add(currentLevel);
        leftToRight = !leftToRight;
    }
    return result;
}
```

### 2. Sliding Window with Queue

#### **Sliding Window Maximum**
```java
public int[] maxSlidingWindow(int[] nums, int k) {
    Deque<Integer> deque = new ArrayDeque<>(); // Store indices
    int[] result = new int[nums.length - k + 1];
    
    for (int i = 0; i < nums.length; i++) {
        // Remove indices outside current window
        while (!deque.isEmpty() && deque.peekFirst() < i - k + 1) {
            deque.pollFirst();
        }
        
        // Remove smaller elements from back
        while (!deque.isEmpty() && nums[deque.peekLast()] < nums[i]) {
            deque.pollLast();
        }
        
        deque.offerLast(i);
        
        // Record maximum for current window
        if (i >= k - 1) {
            result[i - k + 1] = nums[deque.peekFirst()];
        }
    }
    return result;
}
```

#### **Moving Average from Data Stream**
```java
class MovingAverage {
    private Queue<Integer> queue;
    private int size;
    private double sum;
    
    public MovingAverage(int size) {
        this.queue = new LinkedList<>();
        this.size = size;
        this.sum = 0.0;
    }
    
    public double next(int val) {
        queue.offer(val);
        sum += val;
        
        if (queue.size() > size) {
            sum -= queue.poll();
        }
        
        return sum / queue.size();
    }
}
```

### 3. Graph BFS Problems

#### **01 Matrix (Shortest Distance)**
```java
public int[][] updateMatrix(int[][] mat) {
    int m = mat.length, n = mat[0].length;
    int[][] result = new int[m][n];
    Queue<int[]> queue = new LinkedList<>();
    
    // Initialize: add all 0s to queue, mark 1s as unvisited
    for (int i = 0; i < m; i++) {
        for (int j = 0; j < n; j++) {
            if (mat[i][j] == 0) {
                result[i][j] = 0;
                queue.offer(new int[]{i, j});
            } else {
                result[i][j] = Integer.MAX_VALUE;
            }
        }
    }
    
    int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
    
    while (!queue.isEmpty()) {
        int[] cell = queue.poll();
        int row = cell[0], col = cell[1];
        
        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            
            if (newRow >= 0 && newRow < m && newCol >= 0 && newCol < n) {
                if (result[newRow][newCol] > result[row][col] + 1) {
                    result[newRow][newCol] = result[row][col] + 1;
                    queue.offer(new int[]{newRow, newCol});
                }
            }
        }
    }
    return result;
}
```

#### **Word Ladder**
```java
public int ladderLength(String beginWord, String endWord, List<String> wordList) {
    Set<String> wordSet = new HashSet<>(wordList);
    if (!wordSet.contains(endWord)) return 0;
    
    Queue<String> queue = new LinkedList<>();
    Set<String> visited = new HashSet<>();
    
    queue.offer(beginWord);
    visited.add(beginWord);
    int level = 1;
    
    while (!queue.isEmpty()) {
        int size = queue.size();
        
        for (int i = 0; i < size; i++) {
            String word = queue.poll();
            
            if (word.equals(endWord)) return level;
            
            // Try all possible one-character changes
            char[] chars = word.toCharArray();
            for (int j = 0; j < chars.length; j++) {
                char originalChar = chars[j];
                
                for (char c = 'a'; c <= 'z'; c++) {
                    if (c == originalChar) continue;
                    
                    chars[j] = c;
                    String newWord = new String(chars);
                    
                    if (wordSet.contains(newWord) && !visited.contains(newWord)) {
                        queue.offer(newWord);
                        visited.add(newWord);
                    }
                }
                chars[j] = originalChar; // Restore
            }
        }
        level++;
    }
    return 0;
}
```

### 4. Queue Implementation Problems

#### **Implement Queue using Stacks**
```java
class MyQueue {
    private Stack<Integer> input;
    private Stack<Integer> output;
    
    public MyQueue() {
        input = new Stack<>();
        output = new Stack<>();
    }
    
    public void push(int x) {
        input.push(x);
    }
    
    public int pop() {
        peek(); // Ensure output stack has elements
        return output.pop();
    }
    
    public int peek() {
        if (output.isEmpty()) {
            while (!input.isEmpty()) {
                output.push(input.pop());
            }
        }
        return output.peek();
    }
    
    public boolean empty() {
        return input.isEmpty() && output.isEmpty();
    }
}
```

#### **Circular Queue**
```java
class MyCircularQueue {
    private int[] queue;
    private int head;
    private int tail;
    private int size;
    private int capacity;
    
    public MyCircularQueue(int k) {
        this.capacity = k;
        this.queue = new int[k];
        this.head = 0;
        this.tail = -1;
        this.size = 0;
    }
    
    public boolean enQueue(int value) {
        if (isFull()) return false;
        
        tail = (tail + 1) % capacity;
        queue[tail] = value;
        size++;
        return true;
    }
    
    public boolean deQueue() {
        if (isEmpty()) return false;
        
        head = (head + 1) % capacity;
        size--;
        return true;
    }
    
    public int Front() {
        return isEmpty() ? -1 : queue[head];
    }
    
    public int Rear() {
        return isEmpty() ? -1 : queue[tail];
    }
    
    public boolean isEmpty() {
        return size == 0;
    }
    
    public boolean isFull() {
        return size == capacity;
    }
}
```

### 5. Priority Queue Problems

#### **K Closest Points to Origin**
```java
public int[][] kClosest(int[][] points, int k) {
    PriorityQueue<int[]> maxHeap = new PriorityQueue<>(
        (a, b) -> (b[0] * b[0] + b[1] * b[1]) - (a[0] * a[0] + a[1] * a[1])
    );
    
    for (int[] point : points) {
        maxHeap.offer(point);
        if (maxHeap.size() > k) {
            maxHeap.poll();
        }
    }
    
    int[][] result = new int[k][2];
    for (int i = 0; i < k; i++) {
        result[i] = maxHeap.poll();
    }
    return result;
}
```

#### **Merge K Sorted Lists**
```java
public ListNode mergeKLists(ListNode[] lists) {
    PriorityQueue<ListNode> minHeap = new PriorityQueue<>(
        (a, b) -> a.val - b.val
    );
    
    // Add all non-null heads to heap
    for (ListNode head : lists) {
        if (head != null) {
            minHeap.offer(head);
        }
    }
    
    ListNode dummy = new ListNode(0);
    ListNode current = dummy;
    
    while (!minHeap.isEmpty()) {
        ListNode node = minHeap.poll();
        current.next = node;
        current = current.next;
        
        if (node.next != null) {
            minHeap.offer(node.next);
        }
    }
    
    return dummy.next;
}
```

### 6. Deque (Double-ended Queue) Problems

#### **Palindrome Check**
```java
public boolean isPalindrome(String s) {
    Deque<Character> deque = new ArrayDeque<>();
    
    // Add only alphanumeric characters
    for (char c : s.toCharArray()) {
        if (Character.isLetterOrDigit(c)) {
            deque.offerLast(Character.toLowerCase(c));
        }
    }
    
    // Check palindrome by comparing front and back
    while (deque.size() > 1) {
        if (deque.pollFirst() != deque.pollLast()) {
            return false;
        }
    }
    return true;
}
```

---

## Queue Implementations in Java

### 1. **Interface Hierarchy**
```java
// Queue Interface
Queue<Integer> queue = new LinkedList<>();  // Most common
Queue<Integer> queue = new ArrayDeque<>();  // Better performance

// Deque Interface (extends Queue)
Deque<Integer> deque = new ArrayDeque<>();   // Preferred
Deque<Integer> deque = new LinkedList<>();   // Also works

// PriorityQueue (special case)
PriorityQueue<Integer> pq = new PriorityQueue<>(); // Min heap by default
```

### 2. **Method Comparison**
| Operation | Throws Exception | Returns Special Value |
|-----------|------------------|----------------------|
| Insert | `add(e)` | `offer(e)` |
| Remove | `remove()` | `poll()` |
| Examine | `element()` | `peek()` |

---

## Interview Tips & Best Practices

### 1. **Pattern Recognition**
```java
// Queue is often the answer when you see:
- Level-by-level processing (BFS)
- Sliding window problems
- First-come-first-serve scenarios
- Stream processing with buffering
- Finding shortest path/minimum steps
```

### 2. **Common Mistakes to Avoid**
```java
// ❌ Not checking if queue is empty before poll/peek
if (!queue.isEmpty()) {
    queue.poll();
}

// ❌ Using LinkedList when ArrayDeque is better
Deque<Integer> deque = new ArrayDeque<>(); // Preferred
Deque<Integer> deque = new LinkedList<>(); // Slower

// ❌ Forgetting to track level size in BFS
int levelSize = queue.size(); // Capture before processing
for (int i = 0; i < levelSize; i++) {
    // Process current level
}
```

### 3. **Optimization Techniques**
```java
// Use ArrayDeque for better performance
Deque<Integer> deque = new ArrayDeque<>();

// Pre-allocate capacity if size is known
Queue<Integer> queue = new LinkedList<>(); // Or with capacity

// Use indices instead of objects when possible
Queue<Integer> indices = new LinkedList<>(); // More memory efficient
```

### 4. **Space-Time Complexity Analysis**

| Implementation | Add | Remove | Peek | Space |
|----------------|-----|--------|------|-------|
| LinkedList | O(1) | O(1) | O(1) | O(n) |
| ArrayDeque | O(1) amortized | O(1) | O(1) | O(n) |
| PriorityQueue | O(log n) | O(log n) | O(1) | O(n) |

**Typical Interview Complexities:**
- BFS problems: O(V + E) time, O(V) space
- Sliding window: O(n) time, O(k) space
- Level-order traversal: O(n) time, O(w) space (w = max width)

### 5. **Problem-Solving Strategy**
```
1. Identify if FIFO processing is needed
2. Consider if level-by-level processing applies
3. For sliding window, think about what to track
4. For graph problems, consider BFS vs DFS
5. Determine if priority ordering is needed
6. Handle edge cases (empty queue, single element)
```

### 6. **Common Problem Categories**

#### **Easy Level**
- Implement Queue using Stacks
- Moving Average from Data Stream
- First Unique Character in String

#### **Medium Level**
- Binary Tree Level Order Traversal
- 01 Matrix
- Sliding Window Maximum
- Perfect Squares

#### **Hard Level**
- Word Ladder
- Sliding Window Median
- Shortest Path in Binary Matrix
- Serialize and Deserialize Binary Tree

### 7. **Queue vs Other Data Structures**

| Use Queue When | Use Stack When | Use Priority Queue When |
|----------------|----------------|------------------------|
| BFS traversal | DFS traversal | Need ordered processing |
| Level processing | Backtracking | Top K problems |
| Task scheduling | Expression evaluation | Merge operations |
| Buffer management | Undo operations | Dijkstra's algorithm |

### 8. **Code Template**
```java
public returnType queueProblem(inputType input) {
    // 1. Initialize queue and result structures
    Queue<DataType> queue = new LinkedList<>();
    // or Deque<DataType> queue = new ArrayDeque<>();
    
    // 2. Add initial elements to queue
    queue.offer(initialElement);
    
    // 3. Process while queue is not empty
    while (!queue.isEmpty()) {
        // For level-order processing
        int levelSize = queue.size();
        
        for (int i = 0; i < levelSize; i++) {
            DataType current = queue.poll();
            
            // Process current element
            processElement(current);
            
            // Add next elements to queue
            addNextElements(queue, current);
        }
    }
    
    return result;
}
```

This comprehensive guide covers the most important queue patterns and techniques for coding interviews. Master these patterns and you'll be well-prepared for queue-related interview questions.
