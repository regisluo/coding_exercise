# Linked List Data Structures - Interview Guide

## Single Linked List
**Structure**: `value + next`
**Head**: First node reference

## Double Linked List  
**Structure**: `prev + value + next`
**Head/Tail**: First and last node references

---

## Common Interview Patterns & Solutions

### 1. Two Pointer Technique (Runner Pattern)

#### **Fast & Slow Pointers**
```java
// Find middle of list
public ListNode findMiddle(ListNode head) {
    ListNode slow = head, fast = head;
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
    }
    return slow;
}

// Detect cycle
public boolean hasCycle(ListNode head) {
    ListNode slow = head, fast = head;
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
        if (slow == fast) return true;
    }
    return false;
}
```

#### **Two Pointers with Gap**
```java
// Remove nth node from end
public ListNode removeNthFromEnd(ListNode head, int n) {
    ListNode dummy = new ListNode(0);
    dummy.next = head;
    ListNode first = dummy, second = dummy;
    
    // Move first n+1 steps ahead
    for (int i = 0; i <= n; i++) {
        first = first.next;
    }
    
    // Move both until first reaches end
    while (first != null) {
        first = first.next;
        second = second.next;
    }
    
    second.next = second.next.next;
    return dummy.next;
}
```

### 2. Reversal Patterns

#### **Iterative Reversal**
```java
public ListNode reverseList(ListNode head) {
    ListNode prev = null, curr = head;
    while (curr != null) {
        ListNode nextTemp = curr.next;
        curr.next = prev;
        prev = curr;
        curr = nextTemp;
    }
    return prev;
}
```

#### **Recursive Reversal**
```java
public ListNode reverseListRecursive(ListNode head) {
    if (head == null || head.next == null) return head;
    
    ListNode newHead = reverseListRecursive(head.next);
    head.next.next = head;
    head.next = null;
    return newHead;
}
```

#### **Reverse in Groups**
```java
// Reverse nodes in k-group
public ListNode reverseKGroup(ListNode head, int k) {
    ListNode curr = head;
    int count = 0;
    
    // Check if we have k nodes
    while (curr != null && count < k) {
        curr = curr.next;
        count++;
    }
    
    if (count == k) {
        curr = reverseKGroup(curr, k); // Reverse next group
        
        // Reverse current group
        while (count-- > 0) {
            ListNode tmp = head.next;
            head.next = curr;
            curr = head;
            head = tmp;
        }
        head = curr;
    }
    return head;
}
```

### 3. Merging Patterns

#### **Merge Two Sorted Lists**
```java
public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
    ListNode dummy = new ListNode(0);
    ListNode curr = dummy;
    
    while (l1 != null && l2 != null) {
        if (l1.val <= l2.val) {
            curr.next = l1;
            l1 = l1.next;
        } else {
            curr.next = l2;
            l2 = l2.next;
        }
        curr = curr.next;
    }
    
    curr.next = (l1 != null) ? l1 : l2;
    return dummy.next;
}
```

#### **Merge K Sorted Lists (Divide & Conquer)**
```java
public ListNode mergeKLists(ListNode[] lists) {
    if (lists == null || lists.length == 0) return null;
    return mergeHelper(lists, 0, lists.length - 1);
}

private ListNode mergeHelper(ListNode[] lists, int start, int end) {
    if (start == end) return lists[start];
    if (start + 1 == end) return mergeTwoLists(lists[start], lists[end]);
    
    int mid = start + (end - start) / 2;
    ListNode left = mergeHelper(lists, start, mid);
    ListNode right = mergeHelper(lists, mid + 1, end);
    return mergeTwoLists(left, right);
}
```

### 4. Cycle Detection & Handling

#### **Find Cycle Start**
```java
public ListNode detectCycle(ListNode head) {
    ListNode slow = head, fast = head;
    
    // Phase 1: Detect if cycle exists
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
        if (slow == fast) break;
    }
    
    if (fast == null || fast.next == null) return null; // No cycle
    
    // Phase 2: Find cycle start
    slow = head;
    while (slow != fast) {
        slow = slow.next;
        fast = fast.next;
    }
    return slow;
}
```

### 5. Palindrome Check

#### **Using Stack (O(n) space)**
```java
public boolean isPalindrome(ListNode head) {
    Stack<Integer> stack = new Stack<>();
    ListNode curr = head;
    
    // Push all values to stack
    while (curr != null) {
        stack.push(curr.val);
        curr = curr.next;
    }
    
    // Compare with original list
    curr = head;
    while (curr != null) {
        if (curr.val != stack.pop()) return false;
        curr = curr.next;
    }
    return true;
}
```

#### **O(1) Space Solution**
```java
public boolean isPalindrome(ListNode head) {
    if (head == null || head.next == null) return true;
    
    // Find middle
    ListNode slow = head, fast = head;
    while (fast.next != null && fast.next.next != null) {
        slow = slow.next;
        fast = fast.next.next;
    }
    
    // Reverse second half
    ListNode secondHalf = reverseList(slow.next);
    
    // Compare first and second half
    ListNode firstHalf = head;
    while (secondHalf != null) {
        if (firstHalf.val != secondHalf.val) return false;
        firstHalf = firstHalf.next;
        secondHalf = secondHalf.next;
    }
    return true;
}
```

### 6. Intersection Problems

#### **Find Intersection of Two Lists**
```java
public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
    if (headA == null || headB == null) return null;
    
    ListNode pA = headA, pB = headB;
    
    // When pA reaches end, redirect to headB
    // When pB reaches end, redirect to headA
    // They will meet at intersection or both become null
    while (pA != pB) {
        pA = (pA == null) ? headB : pA.next;
        pB = (pB == null) ? headA : pB.next;
    }
    return pA;
}
```

### 7. Sorting Lists

#### **Merge Sort for Linked List**
```java
public ListNode sortList(ListNode head) {
    if (head == null || head.next == null) return head;
    
    // Find middle and split
    ListNode mid = findMiddle(head);
    ListNode secondHalf = mid.next;
    mid.next = null;
    
    // Recursively sort both halves
    ListNode left = sortList(head);
    ListNode right = sortList(secondHalf);
    
    return mergeTwoLists(left, right);
}
```

---

## Double Linked List Specific Patterns

### 1. LRU Cache Implementation
```java
class LRUCache {
    class DLLNode {
        int key, value;
        DLLNode prev, next;
        DLLNode(int k, int v) { key = k; value = v; }
    }
    
    private Map<Integer, DLLNode> cache;
    private DLLNode head, tail;
    private int capacity;
    
    public LRUCache(int capacity) {
        this.capacity = capacity;
        cache = new HashMap<>();
        
        // Dummy head and tail
        head = new DLLNode(0, 0);
        tail = new DLLNode(0, 0);
        head.next = tail;
        tail.prev = head;
    }
    
    public int get(int key) {
        DLLNode node = cache.get(key);
        if (node == null) return -1;
        
        // Move to head (most recently used)
        moveToHead(node);
        return node.value;
    }
    
    public void put(int key, int value) {
        DLLNode node = cache.get(key);
        
        if (node == null) {
            DLLNode newNode = new DLLNode(key, value);
            cache.put(key, newNode);
            addToHead(newNode);
            
            if (cache.size() > capacity) {
                DLLNode tail = removeTail();
                cache.remove(tail.key);
            }
        } else {
            node.value = value;
            moveToHead(node);
        }
    }
    
    private void addToHead(DLLNode node) {
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
    }
    
    private void removeNode(DLLNode node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }
    
    private void moveToHead(DLLNode node) {
        removeNode(node);
        addToHead(node);
    }
    
    private DLLNode removeTail() {
        DLLNode lastNode = tail.prev;
        removeNode(lastNode);
        return lastNode;
    }
}
```

---

## Essential Interview Tips & Best Practices

### 1. Edge Cases to Always Consider
```java
// Always check these cases:
- Empty list (head == null)
- Single node list
- Two node list
- Cycle in list
- Very long list (performance)
```

### 2. Common Mistakes to Avoid
```java
// ❌ Forgetting to update pointers
node.next = newNode; // Missing: newNode.next = node.next

// ❌ Null pointer access
while (curr.next != null) // Should be: while (curr != null)

// ❌ Losing reference to original head
head = head.next; // Use dummy node instead

// ❌ Not handling cycle properly
// Always use Floyd's algorithm for cycle detection
```

### 3. Dummy Node Pattern
```java
// Use dummy node to simplify edge cases
ListNode dummy = new ListNode(0);
dummy.next = head;
// Work with dummy.next instead of head
// Return dummy.next at the end
```

### 4. Time & Space Complexity Reference

| Operation | Singly Linked List | Doubly Linked List |
|-----------|-------------------|-------------------|
| Access | O(n) | O(n) |
| Search | O(n) | O(n) |
| Insertion | O(1) at head, O(n) elsewhere | O(1) anywhere if node given |
| Deletion | O(1) at head, O(n) elsewhere | O(1) anywhere if node given |
| Space | O(1) per node | O(1) per node |

### 5. Problem-Solving Strategy
```
1. Clarify requirements and constraints
2. Consider edge cases (null, single node, cycles)
3. Choose appropriate pattern (two pointers, reversal, etc.)
4. Draw out examples on paper
5. Code step by step
6. Test with edge cases
7. Analyze time/space complexity
```

### 6. Common Problem Categories

#### **Easy Level**
- Remove duplicates from sorted list
- Merge two sorted lists
- Reverse linked list
- Find middle element

#### **Medium Level**
- Remove nth node from end
- Detect cycle and find cycle start
- Intersection of two linked lists
- Sort linked list
- Palindrome check

#### **Hard Level**
- Merge k sorted lists
- Reverse nodes in k-group
- LRU Cache implementation
- Copy list with random pointer

### 7. Code Template for List Problems
```java
public ListNode solution(ListNode head) {
    // 1. Handle edge cases
    if (head == null) return null;
    
    // 2. Use dummy node if needed
    ListNode dummy = new ListNode(0);
    dummy.next = head;
    
    // 3. Initialize pointers
    ListNode slow = head, fast = head;
    
    // 4. Main logic with appropriate pattern
    while (/* condition */) {
        // Update pointers
    }
    
    // 5. Return result
    return dummy.next; // or appropriate return value
}
```

This comprehensive guide covers the most frequent linked list patterns and solutions encountered in coding interviews. Practice these patterns and understand the underlying principles rather than memorizing solutions.
