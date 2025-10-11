# Java Stack SDK - Interview Reference

## Stack Overview

### Definition
A **Stack** is a linear data structure that follows the **LIFO (Last In, First Out)** principle. The last element added to the stack is the first one to be removed.

### Real-World Analogy
Think of a stack of plates - you can only add or remove plates from the top.

---

## Java Stack Implementations

### 1. Built-in Stack Class (Legacy)
```java
import java.util.Stack;

Stack<Integer> stack = new Stack<>();
```

### 2. ArrayDeque (Recommended)
```java
import java.util.ArrayDeque;
import java.util.Deque;

Deque<Integer> stack = new ArrayDeque<>();
```

### 3. Custom Array-based Implementation
```java
class ArrayStack<T> {
    private Object[] array;
    private int top;
    private int capacity;
    
    public ArrayStack(int capacity) {
        this.capacity = capacity;
        this.array = new Object[capacity];
        this.top = -1;
    }
    
    public void push(T item) {
        if (isFull()) {
            throw new RuntimeException("Stack overflow");
        }
        array[++top] = item;
    }
    
    @SuppressWarnings("unchecked")
    public T pop() {
        if (isEmpty()) {
            throw new RuntimeException("Stack underflow");
        }
        T item = (T) array[top];
        array[top--] = null; // Help GC
        return item;
    }
    
    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) {
            throw new RuntimeException("Stack is empty");
        }
        return (T) array[top];
    }
    
    public boolean isEmpty() {
        return top == -1;
    }
    
    public boolean isFull() {
        return top == capacity - 1;
    }
    
    public int size() {
        return top + 1;
    }
}
```

### 4. Custom Linked List Implementation
```java
class LinkedStack<T> {
    private Node<T> head;
    private int size;
    
    private static class Node<T> {
        T data;
        Node<T> next;
        
        Node(T data) {
            this.data = data;
        }
    }
    
    public void push(T item) {
        Node<T> newNode = new Node<>(item);
        newNode.next = head;
        head = newNode;
        size++;
    }
    
    public T pop() {
        if (isEmpty()) {
            throw new RuntimeException("Stack underflow");
        }
        T data = head.data;
        head = head.next;
        size--;
        return data;
    }
    
    public T peek() {
        if (isEmpty()) {
            throw new RuntimeException("Stack is empty");
        }
        return head.data;
    }
    
    public boolean isEmpty() {
        return head == null;
    }
    
    public int size() {
        return size;
    }
}
```

---

## Core Stack Operations

### Basic Operations with Examples

#### 1. Push Operation
```java
Deque<Integer> stack = new ArrayDeque<>();

// Add elements to top of stack
stack.push(10);
stack.push(20);
stack.push(30);
// Stack: [30, 20, 10] (top to bottom)

System.out.println("Stack after pushes: " + stack);
// Output: [30, 20, 10]
```

#### 2. Pop Operation
```java
// Remove and return top element
int topElement = stack.pop(); // Returns 30
System.out.println("Popped: " + topElement);
// Stack: [20, 10]

// Pop again
int secondElement = stack.pop(); // Returns 20
// Stack: [10]
```

#### 3. Peek/Top Operation
```java
// View top element without removing
int top = stack.peek(); // Returns 10
System.out.println("Top element: " + top);
// Stack still: [10]

// Alternative method names
int topAlt = stack.peekFirst(); // Same as peek()
```

#### 4. Check if Empty
```java
boolean empty = stack.isEmpty();
System.out.println("Is empty: " + empty); // false

stack.pop(); // Remove last element
System.out.println("Is empty: " + stack.isEmpty()); // true
```

#### 5. Size Operation
```java
stack.push(5);
stack.push(15);
stack.push(25);

int size = stack.size();
System.out.println("Stack size: " + size); // 3
```

---

## Method Comparison Table

| Method | Stack Class | ArrayDeque | Description |
|--------|-------------|------------|-------------|
| **Push** | `push(e)` | `push(e)` | Add element to top |
| **Pop** | `pop()` | `pop()` | Remove top element |
| **Peek** | `peek()` | `peek()` / `peekFirst()` | View top element |
| **Empty Check** | `isEmpty()` | `isEmpty()` | Check if empty |
| **Size** | `size()` | `size()` | Get number of elements |
| **Search** | `search(e)` | Not available | Find element position |

---

## Interview-Specific Examples

### 1. Basic Stack Usage Pattern
```java
public void demonstrateStackOperations() {
    Deque<String> stack = new ArrayDeque<>();
    
    // Push operations
    stack.push("First");
    stack.push("Second");
    stack.push("Third");
    
    // Process all elements
    while (!stack.isEmpty()) {
        String current = stack.pop();
        System.out.println("Processing: " + current);
    }
    // Output: Third, Second, First
}
```

### 2. Stack with Error Handling
```java
public class SafeStack<T> {
    private Deque<T> stack = new ArrayDeque<>();
    
    public boolean push(T item) {
        try {
            stack.push(item);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public T pop() {
        if (stack.isEmpty()) {
            return null; // Or throw custom exception
        }
        return stack.pop();
    }
    
    public T peek() {
        return stack.isEmpty() ? null : stack.peek();
    }
    
    public boolean isEmpty() {
        return stack.isEmpty();
    }
    
    public int size() {
        return stack.size();
    }
}
```

### 3. Stack Iteration (Interview Tip)
```java
public void iterateStack() {
    Deque<Integer> stack = new ArrayDeque<>();
    stack.push(1);
    stack.push(2);
    stack.push(3);
    
    // Method 1: Destructive iteration
    while (!stack.isEmpty()) {
        System.out.println(stack.pop());
    }
    
    // Method 2: Non-destructive iteration (Java 8+)
    stack.stream().forEach(System.out::println);
    
    // Method 3: Convert to array for processing
    Integer[] array = stack.toArray(new Integer[0]);
    for (Integer element : array) {
        System.out.println(element);
    }
}
```

---

## Performance Characteristics

### Time Complexity
| Operation | Array-based | Linked List | Built-in Stack/ArrayDeque |
|-----------|-------------|-------------|---------------------------|
| Push | O(1) | O(1) | O(1) amortized |
| Pop | O(1) | O(1) | O(1) |
| Peek | O(1) | O(1) | O(1) |
| isEmpty | O(1) | O(1) | O(1) |
| Size | O(1) | O(1) | O(1) |
| Search | O(n) | O(n) | O(n) |

### Space Complexity
- **Array-based**: O(n) where n is capacity
- **Linked List**: O(n) where n is number of elements
- **Built-in**: O(n) where n is number of elements

---

## Common Interview Scenarios

### 1. When to Use Stack
```java
// Pattern recognition for interviews:
// 1. Balanced parentheses problems
// 2. Expression evaluation
// 3. Function call simulation
// 4. Undo operations
// 5. Browser back button
// 6. DFS traversal (iterative)
```

### 2. Stack vs Other Data Structures
```java
// Use Stack when:
// - LIFO access pattern needed
// - Tracking nested structures
// - Implementing recursion iteratively
// - Temporary storage with recent access

// Don't use Stack when:
// - Need random access (use Array/List)
// - FIFO access needed (use Queue)
// - Need to access middle elements frequently
```

### 3. Common Pitfalls in Interviews
```java
public void commonMistakes() {
    Deque<Integer> stack = new ArrayDeque<>();
    
    // ❌ Not checking if empty before pop/peek
    // if (!stack.isEmpty()) {
    //     stack.pop();
    // }
    
    // ❌ Using Stack class instead of ArrayDeque
    // Stack<Integer> oldWay = new Stack<>(); // Avoid this
    
    // ❌ Not handling stack overflow in custom implementation
    // Always check capacity in array-based implementations
    
    // ✅ Correct patterns
    if (!stack.isEmpty()) {
        Integer top = stack.peek();
        // Process top element
    }
}
```

---

## Interview Code Templates

### Template 1: Basic Stack Problem
```java
public ReturnType solveProblem(InputType input) {
    Deque<DataType> stack = new ArrayDeque<>();
    
    // Initialize result
    ReturnType result = new ReturnType();
    
    // Process input
    for (Element element : input) {
        // Decide when to pop
        while (!stack.isEmpty() && condition(stack.peek(), element)) {
            DataType popped = stack.pop();
            // Process popped element
            updateResult(result, popped);
        }
        
        // Decide when to push
        if (shouldPush(element)) {
            stack.push(processElement(element));
        }
    }
    
    // Process remaining elements
    while (!stack.isEmpty()) {
        updateResult(result, stack.pop());
    }
    
    return result;
}
```

### Template 2: Parentheses/Bracket Problems
```java
public boolean isValid(String input) {
    Deque<Character> stack = new ArrayDeque<>();
    Map<Character, Character> pairs = Map.of(')', '(', '}', '{', ']', '[');
    
    for (char c : input.toCharArray()) {
        if (pairs.containsKey(c)) {
            // Closing bracket
            if (stack.isEmpty() || stack.pop() != pairs.get(c)) {
                return false;
            }
        } else {
            // Opening bracket
            stack.push(c);
        }
    }
    
    return stack.isEmpty();
}
```

---

## Best Practices for Interviews

### 1. Choose the Right Implementation
```java
// For interviews, prefer ArrayDeque over Stack class
Deque<Integer> stack = new ArrayDeque<>(); // ✅ Preferred
Stack<Integer> stack = new Stack<>();      // ❌ Legacy

// Reason: ArrayDeque is faster and not synchronized
```

### 2. Always Check for Empty Stack
```java
// Before pop() or peek()
if (!stack.isEmpty()) {
    stack.pop();
}

// Or handle gracefully
Integer result = stack.isEmpty() ? null : stack.pop();
```

### 3. Consider Space Complexity
```java
// For large datasets, be mindful of stack growth
// Consider iterative solutions over recursive ones
// to avoid stack overflow
```

### 4. Common Interview Questions to Practice
```java
// Easy:
// - Valid Parentheses
// - Implement Stack using Queues
// - Min Stack

// Medium:
// - Daily Temperatures
// - Next Greater Element
// - Decode String

// Hard:
// - Basic Calculator
// - Largest Rectangle in Histogram
// - Maximal Rectangle
```

This reference covers all essential Stack concepts and implementations needed for coding interviews, with practical examples and common patterns.
