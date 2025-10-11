# Stack Data Structure - Interview Guide

## Stack Basics
**LIFO (Last In, First Out)** principle
**Core Operations:** `push()`, `pop()`, `peek()/top()`, `isEmpty()`, `size()`

---

## When to Use Stack - Key Triggers

### 1. **Parentheses/Brackets Problems**
- Balanced parentheses validation
- Expression evaluation
- Nested structures

### 2. **Expression Evaluation**
- Infix to postfix conversion
- Postfix expression evaluation
- Calculator implementations

### 3. **Monotonic Stack Problems**
- Next/Previous greater element
- Largest rectangle in histogram
- Trapping rainwater

### 4. **Backtracking & State Management**
- DFS traversal
- Undo operations
- Path tracking

### 5. **Function Call Simulation**
- Recursion to iteration conversion
- Call stack simulation

---

## Common Interview Patterns & Solutions

### 1. Balanced Parentheses

#### **Valid Parentheses**
```java
public boolean isValid(String s) {
    Stack<Character> stack = new Stack<>();
    Map<Character, Character> mapping = new HashMap<>();
    mapping.put(')', '(');
    mapping.put('}', '{');
    mapping.put(']', '[');
    
    for (char c : s.toCharArray()) {
        if (mapping.containsKey(c)) {
            char topElement = stack.empty() ? '#' : stack.pop();
            if (topElement != mapping.get(c)) {
                return false;
            }
        } else {
            stack.push(c);
        }
    }
    return stack.isEmpty();
}
```

#### **Minimum Remove to Make Valid Parentheses**
```java
public String minRemoveToMakeValid(String s) {
    Stack<Integer> stack = new Stack<>();
    Set<Integer> toRemove = new HashSet<>();
    
    // First pass: mark invalid closing brackets
    for (int i = 0; i < s.length(); i++) {
        if (s.charAt(i) == '(') {
            stack.push(i);
        } else if (s.charAt(i) == ')') {
            if (stack.isEmpty()) {
                toRemove.add(i);
            } else {
                stack.pop();
            }
        }
    }
    
    // Add remaining opening brackets to remove
    while (!stack.isEmpty()) {
        toRemove.add(stack.pop());
    }
    
    // Build result
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < s.length(); i++) {
        if (!toRemove.contains(i)) {
            result.append(s.charAt(i));
        }
    }
    return result.toString();
}
```

### 2. Expression Evaluation

#### **Basic Calculator**
```java
public int calculate(String s) {
    Stack<Integer> stack = new Stack<>();
    int num = 0;
    char sign = '+';
    
    for (int i = 0; i < s.length(); i++) {
        char c = s.charAt(i);
        
        if (Character.isDigit(c)) {
            num = num * 10 + (c - '0');
        }
        
        if (c == '+' || c == '-' || c == '*' || c == '/' || i == s.length() - 1) {
            switch (sign) {
                case '+':
                    stack.push(num);
                    break;
                case '-':
                    stack.push(-num);
                    break;
                case '*':
                    stack.push(stack.pop() * num);
                    break;
                case '/':
                    stack.push(stack.pop() / num);
                    break;
            }
            sign = c;
            num = 0;
        }
    }
    
    int result = 0;
    while (!stack.isEmpty()) {
        result += stack.pop();
    }
    return result;
}
```

#### **Evaluate Reverse Polish Notation**
```java
public int evalRPN(String[] tokens) {
    Stack<Integer> stack = new Stack<>();
    Set<String> operators = new HashSet<>(Arrays.asList("+", "-", "*", "/"));
    
    for (String token : tokens) {
        if (operators.contains(token)) {
            int b = stack.pop();
            int a = stack.pop();
            switch (token) {
                case "+": stack.push(a + b); break;
                case "-": stack.push(a - b); break;
                case "*": stack.push(a * b); break;
                case "/": stack.push(a / b); break;
            }
        } else {
            stack.push(Integer.parseInt(token));
        }
    }
    return stack.pop();
}
```

### 3. Monotonic Stack Problems

#### **Next Greater Element**
```java
public int[] nextGreaterElement(int[] nums1, int[] nums2) {
    Map<Integer, Integer> map = new HashMap<>();
    Stack<Integer> stack = new Stack<>();
    
    // Build next greater mapping for nums2
    for (int num : nums2) {
        while (!stack.isEmpty() && stack.peek() < num) {
            map.put(stack.pop(), num);
        }
        stack.push(num);
    }
    
    // Find next greater for nums1
    int[] result = new int[nums1.length];
    for (int i = 0; i < nums1.length; i++) {
        result[i] = map.getOrDefault(nums1[i], -1);
    }
    return result;
}
```

#### **Daily Temperatures**
```java
public int[] dailyTemperatures(int[] temperatures) {
    int[] result = new int[temperatures.length];
    Stack<Integer> stack = new Stack<>(); // Store indices
    
    for (int i = 0; i < temperatures.length; i++) {
        while (!stack.isEmpty() && 
               temperatures[i] > temperatures[stack.peek()]) {
            int index = stack.pop();
            result[index] = i - index;
        }
        stack.push(i);
    }
    return result;
}
```

#### **Largest Rectangle in Histogram**
```java
public int largestRectangleArea(int[] heights) {
    Stack<Integer> stack = new Stack<>();
    int maxArea = 0;
    
    for (int i = 0; i <= heights.length; i++) {
        int h = (i == heights.length) ? 0 : heights[i];
        
        while (!stack.isEmpty() && h < heights[stack.peek()]) {
            int height = heights[stack.pop()];
            int width = stack.isEmpty() ? i : i - stack.peek() - 1;
            maxArea = Math.max(maxArea, height * width);
        }
        stack.push(i);
    }
    return maxArea;
}
```

### 4. String Manipulation

#### **Remove Duplicate Letters**
```java
public String removeDuplicateLetters(String s) {
    int[] count = new int[26];
    boolean[] inStack = new boolean[26];
    Stack<Character> stack = new Stack<>();
    
    // Count frequency of each character
    for (char c : s.toCharArray()) {
        count[c - 'a']++;
    }
    
    for (char c : s.toCharArray()) {
        count[c - 'a']--;
        
        if (inStack[c - 'a']) continue;
        
        // Remove larger characters that appear later
        while (!stack.isEmpty() && 
               stack.peek() > c && 
               count[stack.peek() - 'a'] > 0) {
            char removed = stack.pop();
            inStack[removed - 'a'] = false;
        }
        
        stack.push(c);
        inStack[c - 'a'] = true;
    }
    
    StringBuilder result = new StringBuilder();
    while (!stack.isEmpty()) {
        result.insert(0, stack.pop());
    }
    return result.toString();
}
```

#### **Decode String**
```java
public String decodeString(String s) {
    Stack<Integer> countStack = new Stack<>();
    Stack<StringBuilder> stringStack = new Stack<>();
    StringBuilder current = new StringBuilder();
    int k = 0;
    
    for (char c : s.toCharArray()) {
        if (Character.isDigit(c)) {
            k = k * 10 + (c - '0');
        } else if (c == '[') {
            countStack.push(k);
            stringStack.push(current);
            current = new StringBuilder();
            k = 0;
        } else if (c == ']') {
            StringBuilder temp = current;
            current = stringStack.pop();
            int count = countStack.pop();
            for (int i = 0; i < count; i++) {
                current.append(temp);
            }
        } else {
            current.append(c);
        }
    }
    return current.toString();
}
```

### 5. Binary Tree Traversal

#### **Iterative Inorder Traversal**
```java
public List<Integer> inorderTraversal(TreeNode root) {
    List<Integer> result = new ArrayList<>();
    Stack<TreeNode> stack = new Stack<>();
    TreeNode current = root;
    
    while (current != null || !stack.isEmpty()) {
        // Go to leftmost node
        while (current != null) {
            stack.push(current);
            current = current.left;
        }
        
        // Process current node
        current = stack.pop();
        result.add(current.val);
        
        // Move to right subtree
        current = current.right;
    }
    return result;
}
```

#### **Iterative Preorder Traversal**
```java
public List<Integer> preorderTraversal(TreeNode root) {
    List<Integer> result = new ArrayList<>();
    if (root == null) return result;
    
    Stack<TreeNode> stack = new Stack<>();
    stack.push(root);
    
    while (!stack.isEmpty()) {
        TreeNode node = stack.pop();
        result.add(node.val);
        
        // Push right first, then left (so left is processed first)
        if (node.right != null) stack.push(node.right);
        if (node.left != null) stack.push(node.left);
    }
    return result;
}
```

---

## Stack Implementation Variations

### 1. Min Stack
```java
class MinStack {
    private Stack<Integer> stack;
    private Stack<Integer> minStack;
    
    public MinStack() {
        stack = new Stack<>();
        minStack = new Stack<>();
    }
    
    public void push(int val) {
        stack.push(val);
        if (minStack.isEmpty() || val <= minStack.peek()) {
            minStack.push(val);
        }
    }
    
    public void pop() {
        if (stack.pop().equals(minStack.peek())) {
            minStack.pop();
        }
    }
    
    public int top() {
        return stack.peek();
    }
    
    public int getMin() {
        return minStack.peek();
    }
}
```

### 2. Stack using Queues
```java
class MyStack {
    private Queue<Integer> q1;
    private Queue<Integer> q2;
    
    public MyStack() {
        q1 = new LinkedList<>();
        q2 = new LinkedList<>();
    }
    
    public void push(int x) {
        q2.offer(x);
        while (!q1.isEmpty()) {
            q2.offer(q1.poll());
        }
        Queue<Integer> temp = q1;
        q1 = q2;
        q2 = temp;
    }
    
    public int pop() {
        return q1.poll();
    }
    
    public int top() {
        return q1.peek();
    }
    
    public boolean empty() {
        return q1.isEmpty();
    }
}
```

---

## Interview Tips & Best Practices

### 1. **Pattern Recognition**
```java
// Stack is often the answer when you see:
- Nested structures (parentheses, HTML tags)
- "Next/Previous greater/smaller" problems
- Expression evaluation
- Backtracking scenarios
- Converting recursion to iteration
```

### 2. **Common Mistakes to Avoid**
```java
// ❌ Not checking if stack is empty before pop/peek
if (!stack.isEmpty()) {
    stack.pop();
}

// ❌ Using Stack instead of Deque in Java
Deque<Integer> stack = new ArrayDeque<>(); // Preferred
Stack<Integer> stack = new Stack<>();      // Avoid

// ❌ Forgetting to handle edge cases
- Empty input
- Single element
- No valid solution exists
```

### 3. **Optimization Techniques**
```java
// Use indices instead of values when possible
Stack<Integer> indices = new Stack<>(); // More flexible

// Combine operations when building result
while (!stack.isEmpty()) {
    result.insert(0, stack.pop()); // For reverse order
}

// Use StringBuilder for string operations
StringBuilder sb = new StringBuilder();
```

### 4. **Space-Time Complexity Analysis**

| Operation | Time | Space |
|-----------|------|-------|
| Push | O(1) | O(1) |
| Pop | O(1) | O(1) |
| Peek | O(1) | O(1) |
| Search | O(n) | O(1) |

**Typical Interview Complexities:**
- Parentheses problems: O(n) time, O(n) space
- Monotonic stack: O(n) time, O(n) space
- Expression evaluation: O(n) time, O(n) space

### 5. **Problem-Solving Strategy**
```
1. Identify if stack pattern applies
2. Determine what to store (values, indices, objects)
3. Consider monotonic stack for next/previous problems
4. Handle edge cases (empty stack, single element)
5. Think about when to push vs when to pop
6. Consider if you need additional data structures
```

### 6. **Common Problem Categories**

#### **Easy Level**
- Valid Parentheses
- Implement Stack using Queues
- Remove Outermost Parentheses

#### **Medium Level**
- Min Stack
- Daily Temperatures
- Next Greater Element
- Decode String
- Remove Duplicate Letters

#### **Hard Level**
- Basic Calculator
- Largest Rectangle in Histogram
- Maximal Rectangle
- Remove Invalid Parentheses

### 7. **Stack vs Other Data Structures**

| Use Stack When | Use Queue When | Use Deque When |
|----------------|----------------|----------------|
| LIFO processing | FIFO processing | Both ends access |
| Backtracking | BFS traversal | Sliding window |
| Function calls | Task scheduling | Double-ended operations |
| Expression eval | Level processing | Palindrome checking |

### 8. **Code Template**
```java
public returnType stackProblem(inputType input) {
    // 1. Initialize stack and result
    Stack<DataType> stack = new Stack<>();
    // or Deque<DataType> stack = new ArrayDeque<>();
    
    // 2. Iterate through input
    for (element : input) {
        // 3. Process based on pattern
        while (!stack.isEmpty() && condition) {
            // Pop and process
            processElement(stack.pop());
        }
        
        // 4. Push current element if needed
        if (shouldPush) {
            stack.push(element);
        }
    }
    
    // 5. Process remaining elements
    while (!stack.isEmpty()) {
        processRemaining(stack.pop());
    }
    
    return result;
}
```

This comprehensive guide covers the most important stack patterns and techniques for coding interviews. Master these patterns and you'll be well-prepared for stack-related interview questions.
