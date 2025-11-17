# Java List Data Structure - Interview Guide

## Table of Contents
1. [Basic Concepts](#basic-concepts)
2. [Custom List Implementations](#custom-list-implementations)
3. [Java SDK - List Interface](#java-sdk-list-interface)
4. [Common List Algorithms](#common-list-algorithms)
5. [Important Interview Questions](#important-interview-questions)
6. [Tips & Patterns](#tips--patterns)

---

## Basic Concepts

### 1. List Fundamentals

**Definition**: A linear data structure where elements are stored sequentially. Each element (node) contains:
- **Data**: The actual value
- **Reference/Pointer**: Link to next element (and previous in doubly linked list)

### 2. List Types

#### Singly Linked List
Each node has:
- `data`: Value stored
- `next`: Reference to next node

**Advantages**:
- Dynamic size
- Efficient insertion/deletion at beginning: O(1)
- No memory waste (no pre-allocation)

**Disadvantages**:
- No random access (must traverse): O(n)
- Extra memory for pointers
- Not cache-friendly

#### Doubly Linked List
Each node has:
- `data`: Value stored
- `next`: Reference to next node
- `prev`: Reference to previous node

**Advantages**:
- Bidirectional traversal
- Efficient deletion (if node reference known): O(1)
- Can traverse backwards

**Disadvantages**:
- Extra memory for prev pointer
- More complex operations

#### Circular Linked List
Last node points back to first node (can be singly or doubly)

**Use Cases**:
- Round-robin scheduling
- Music/video playlists
- Browser history (circular buffer)

### 3. Time Complexity Comparison

| Operation | Array | Singly Linked List | Doubly Linked List |
|-----------|-------|-------------------|-------------------|
| Access by index | O(1) | O(n) | O(n) |
| Search | O(n) | O(n) | O(n) |
| Insert at beginning | O(n) | O(1) | O(1) |
| Insert at end | O(1) | O(n)* | O(1)** |
| Insert at middle | O(n) | O(n) | O(n) |
| Delete at beginning | O(n) | O(1) | O(1) |
| Delete at end | O(1) | O(n) | O(1)** |
| Delete at middle | O(n) | O(n) | O(n) |

\* O(1) if tail pointer maintained  
\** With tail pointer

---

## Custom List Implementations

### 1. Singly Linked List Node

```java
class ListNode {
    int val;
    ListNode next;
    
    ListNode() {}
    
    ListNode(int val) {
        this.val = val;
    }
    
    ListNode(int val, ListNode next) {
        this.val = val;
        this.next = next;
    }
}
```

### 2. Singly Linked List Implementation

```java
class SinglyLinkedList {
    private ListNode head;
    private int size;
    
    public SinglyLinkedList() {
        this.head = null;
        this.size = 0;
    }
    
    // Insert at beginning - O(1)
    public void insertFirst(int val) {
        ListNode newNode = new ListNode(val);
        newNode.next = head;
        head = newNode;
        size++;
    }
    
    // Insert at end - O(n)
    public void insertLast(int val) {
        ListNode newNode = new ListNode(val);
        
        if (head == null) {
            head = newNode;
            size++;
            return;
        }
        
        ListNode curr = head;
        while (curr.next != null) {
            curr = curr.next;
        }
        curr.next = newNode;
        size++;
    }
    
    // Insert at position - O(n)
    public void insertAt(int val, int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }
        
        if (index == 0) {
            insertFirst(val);
            return;
        }
        
        ListNode newNode = new ListNode(val);
        ListNode curr = head;
        
        for (int i = 0; i < index - 1; i++) {
            curr = curr.next;
        }
        
        newNode.next = curr.next;
        curr.next = newNode;
        size++;
    }
    
    // Delete first - O(1)
    public void deleteFirst() {
        if (head == null) {
            throw new NoSuchElementException();
        }
        head = head.next;
        size--;
    }
    
    // Delete last - O(n)
    public void deleteLast() {
        if (head == null) {
            throw new NoSuchElementException();
        }
        
        if (head.next == null) {
            head = null;
            size--;
            return;
        }
        
        ListNode curr = head;
        while (curr.next.next != null) {
            curr = curr.next;
        }
        curr.next = null;
        size--;
    }
    
    // Search - O(n)
    public boolean contains(int val) {
        ListNode curr = head;
        while (curr != null) {
            if (curr.val == val) return true;
            curr = curr.next;
        }
        return false;
    }
    
    // Get by index - O(n)
    public int get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        
        ListNode curr = head;
        for (int i = 0; i < index; i++) {
            curr = curr.next;
        }
        return curr.val;
    }
    
    // Print list - O(n)
    public void printList() {
        ListNode curr = head;
        while (curr != null) {
            System.out.print(curr.val + " -> ");
            curr = curr.next;
        }
        System.out.println("null");
    }
    
    public int size() {
        return size;
    }
    
    public boolean isEmpty() {
        return head == null;
    }
}
```

### 3. Doubly Linked List Node

```java
class DoublyListNode {
    int val;
    DoublyListNode prev;
    DoublyListNode next;
    
    DoublyListNode(int val) {
        this.val = val;
        this.prev = null;
        this.next = null;
    }
}
```

### 4. Doubly Linked List Implementation

```java
class DoublyLinkedList {
    private DoublyListNode head;
    private DoublyListNode tail;
    private int size;
    
    public DoublyLinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }
    
    // Insert at beginning - O(1)
    public void insertFirst(int val) {
        DoublyListNode newNode = new DoublyListNode(val);
        
        if (head == null) {
            head = tail = newNode;
        } else {
            newNode.next = head;
            head.prev = newNode;
            head = newNode;
        }
        size++;
    }
    
    // Insert at end - O(1)
    public void insertLast(int val) {
        DoublyListNode newNode = new DoublyListNode(val);
        
        if (tail == null) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        size++;
    }
    
    // Delete first - O(1)
    public void deleteFirst() {
        if (head == null) {
            throw new NoSuchElementException();
        }
        
        if (head == tail) {
            head = tail = null;
        } else {
            head = head.next;
            head.prev = null;
        }
        size--;
    }
    
    // Delete last - O(1)
    public void deleteLast() {
        if (tail == null) {
            throw new NoSuchElementException();
        }
        
        if (head == tail) {
            head = tail = null;
        } else {
            tail = tail.prev;
            tail.next = null;
        }
        size--;
    }
    
    // Print forward - O(n)
    public void printForward() {
        DoublyListNode curr = head;
        while (curr != null) {
            System.out.print(curr.val + " <-> ");
            curr = curr.next;
        }
        System.out.println("null");
    }
    
    // Print backward - O(n)
    public void printBackward() {
        DoublyListNode curr = tail;
        while (curr != null) {
            System.out.print(curr.val + " <-> ");
            curr = curr.prev;
        }
        System.out.println("null");
    }
}
```

---

## Java SDK - List Interface

### 1. List Interface Hierarchy

```
Collection (interface)
    ↓
List (interface)
    ↓
    ├── ArrayList (class)
    ├── LinkedList (class)
    ├── Vector (class)
    └── Stack (class)
```

### 2. ArrayList

**Backed by**: Dynamic array  
**Best for**: Random access, iteration

```java
import java.util.ArrayList;
import java.util.List;

List<Integer> list = new ArrayList<>();

// Add elements - O(1) amortized
list.add(10);              // Append
list.add(0, 5);            // Insert at index - O(n)

// Access - O(1)
int val = list.get(0);

// Update - O(1)
list.set(0, 15);

// Remove - O(n)
list.remove(0);            // By index
list.remove(Integer.valueOf(10));  // By value

// Search - O(n)
boolean exists = list.contains(10);
int index = list.indexOf(10);      // First occurrence
int lastIdx = list.lastIndexOf(10); // Last occurrence

// Size operations
int size = list.size();
boolean empty = list.isEmpty();

// Clear - O(n)
list.clear();

// Bulk operations
list.addAll(Arrays.asList(1, 2, 3));
list.removeAll(Arrays.asList(1, 2));
list.retainAll(Arrays.asList(3));

// Convert to array
Integer[] arr = list.toArray(new Integer[0]);

// Iteration
for (int num : list) {
    System.out.println(num);
}

// Using Iterator
Iterator<Integer> it = list.iterator();
while (it.hasNext()) {
    System.out.println(it.next());
}

// Stream operations (Java 8+)
list.stream().filter(x -> x > 5).forEach(System.out::println);
```

**Key Methods**:
- `add(E e)`: O(1) amortized
- `add(int index, E e)`: O(n)
- `get(int index)`: O(1)
- `set(int index, E e)`: O(1)
- `remove(int index)`: O(n)
- `contains(Object o)`: O(n)
- `size()`: O(1)

### 3. LinkedList

**Backed by**: Doubly linked list  
**Best for**: Frequent insertions/deletions at ends

```java
import java.util.LinkedList;

LinkedList<Integer> list = new LinkedList<>();

// Add operations
list.add(10);              // Append - O(1)
list.addFirst(5);          // Add at beginning - O(1)
list.addLast(15);          // Add at end - O(1)
list.add(1, 7);            // Insert at index - O(n)

// Access - O(n)
int val = list.get(0);
int first = list.getFirst();  // O(1)
int last = list.getLast();    // O(1)

// Remove - O(1) for ends, O(n) for middle
list.removeFirst();
list.removeLast();
list.remove(1);            // By index

// Peek operations (don't remove)
int peekFirst = list.peekFirst();  // O(1)
int peekLast = list.peekLast();    // O(1)

// Poll operations (remove and return)
Integer pollFirst = list.pollFirst();  // O(1)
Integer pollLast = list.pollLast();    // O(1)

// Can be used as Queue
list.offer(20);            // Add to end
list.poll();               // Remove from beginning

// Can be used as Stack
list.push(25);             // Add to beginning
list.pop();                // Remove from beginning

// Can be used as Deque
list.offerFirst(30);
list.offerLast(35);
```

**Key Methods**:
- `addFirst(E e)`: O(1)
- `addLast(E e)`: O(1)
- `getFirst()`: O(1)
- `getLast()`: O(1)
- `removeFirst()`: O(1)
- `removeLast()`: O(1)
- `get(int index)`: O(n)

### 4. ArrayList vs LinkedList

| Feature | ArrayList | LinkedList |
|---------|-----------|------------|
| **Underlying Structure** | Dynamic array | Doubly linked list |
| **Random access** | O(1) | O(n) |
| **Insert/Delete at beginning** | O(n) | O(1) |
| **Insert/Delete at end** | O(1) | O(1) |
| **Memory overhead** | Less | More (pointers) |
| **Cache performance** | Better | Worse |
| **Best use case** | Frequent access | Frequent insert/delete |

### 5. Common List Utilities

```java
import java.util.Collections;
import java.util.Arrays;

List<Integer> list = new ArrayList<>(Arrays.asList(3, 1, 4, 1, 5));

// Sort - O(n log n)
Collections.sort(list);
Collections.sort(list, Collections.reverseOrder());

// Reverse - O(n)
Collections.reverse(list);

// Shuffle - O(n)
Collections.shuffle(list);

// Binary search (requires sorted list) - O(log n)
int index = Collections.binarySearch(list, 3);

// Min/Max - O(n)
int min = Collections.min(list);
int max = Collections.max(list);

// Frequency - O(n)
int freq = Collections.frequency(list, 1);

// Fill - O(n)
Collections.fill(list, 0);

// Copy - O(n)
List<Integer> dest = new ArrayList<>(Collections.nCopies(list.size(), 0));
Collections.copy(dest, list);

// Rotate - O(n)
Collections.rotate(list, 2);  // Rotate right by 2

// Swap
Collections.swap(list, 0, 1);
```

---

## Common List Algorithms

### 1. Reverse a Linked List

```java
// Iterative - O(n) time, O(1) space
public ListNode reverseList(ListNode head) {
    ListNode prev = null;
    ListNode curr = head;
    
    while (curr != null) {
        ListNode next = curr.next;
        curr.next = prev;
        prev = curr;
        curr = next;
    }
    
    return prev;
}

// Recursive - O(n) time, O(n) space
public ListNode reverseListRecursive(ListNode head) {
    if (head == null || head.next == null) {
        return head;
    }
    
    ListNode newHead = reverseListRecursive(head.next);
    head.next.next = head;
    head.next = null;
    
    return newHead;
}
```

### 2. Detect Cycle (Floyd's Algorithm)

```java
// Two pointers - O(n) time, O(1) space
public boolean hasCycle(ListNode head) {
    if (head == null) return false;
    
    ListNode slow = head;
    ListNode fast = head;
    
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
        
        if (slow == fast) return true;
    }
    
    return false;
}
```

### 3. Find Middle of List

```java
// Two pointers - O(n) time, O(1) space
public ListNode findMiddle(ListNode head) {
    if (head == null) return null;
    
    ListNode slow = head;
    ListNode fast = head;
    
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
    }
    
    return slow;
}
```

### 4. Merge Two Sorted Lists

```java
// Iterative - O(m + n) time, O(1) space
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

---

## Important Interview Questions

### Easy Level

#### 1. Reverse Linked List (LeetCode 206)
**Problem**: Reverse a singly linked list.

```java
/**
 * Iterative approach using three pointers
 * Time: O(n) - visit each node once
 * Space: O(1) - only use constant extra space
 */
public ListNode reverseList(ListNode head) {
    ListNode prev = null;
    ListNode curr = head;
    
    while (curr != null) {
        ListNode nextTemp = curr.next;  // Save next
        curr.next = prev;               // Reverse link
        prev = curr;                    // Move prev forward
        curr = nextTemp;                // Move curr forward
    }
    
    return prev;  // prev is new head
}
```

#### 2. Merge Two Sorted Lists (LeetCode 21)
**Problem**: Merge two sorted linked lists.

```java
/**
 * Use dummy node to simplify edge cases
 * Time: O(m + n) - visit all nodes from both lists
 * Space: O(1) - no extra space (reuse existing nodes)
 */
public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
    ListNode dummy = new ListNode(0);
    ListNode curr = dummy;
    
    // Compare and link smaller node
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
    
    // Attach remaining nodes
    curr.next = (l1 != null) ? l1 : l2;
    
    return dummy.next;
}
```

#### 3. Delete Node in Linked List (LeetCode 237)
**Problem**: Delete node given only access to that node (not head).

```java
/**
 * Copy next node's value, then delete next node
 * Time: O(1) - constant operations
 * Space: O(1) - no extra space
 */
public void deleteNode(ListNode node) {
    // Copy next node's value to current node
    node.val = node.next.val;
    
    // Delete next node
    node.next = node.next.next;
}
```

#### 4. Middle of Linked List (LeetCode 876)
**Problem**: Find the middle node of a linked list.

```java
/**
 * Floyd's slow-fast pointer technique
 * Time: O(n) - traverse list once
 * Space: O(1) - only two pointers
 */
public ListNode middleNode(ListNode head) {
    ListNode slow = head;
    ListNode fast = head;
    
    // Fast moves 2x speed, when fast reaches end, slow is at middle
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
    }
    
    return slow;
}
```

#### 5. Linked List Cycle (LeetCode 141)
**Problem**: Detect if linked list has a cycle.

```java
/**
 * Floyd's Cycle Detection (Tortoise and Hare)
 * Time: O(n) - in worst case visit all nodes
 * Space: O(1) - only two pointers
 */
public boolean hasCycle(ListNode head) {
    if (head == null || head.next == null) return false;
    
    ListNode slow = head;
    ListNode fast = head;
    
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
        
        // If they meet, cycle exists
        if (slow == fast) return true;
    }
    
    return false;
}
```

#### 6. Remove Duplicates from Sorted List (LeetCode 83)
**Problem**: Remove duplicates from sorted linked list.

```java
/**
 * One pass, skip duplicate nodes
 * Time: O(n) - traverse list once
 * Space: O(1) - no extra space
 */
public ListNode deleteDuplicates(ListNode head) {
    if (head == null) return null;
    
    ListNode curr = head;
    
    while (curr != null && curr.next != null) {
        if (curr.val == curr.next.val) {
            // Skip duplicate
            curr.next = curr.next.next;
        } else {
            curr = curr.next;
        }
    }
    
    return head;
}
```

#### 7. Palindrome Linked List (LeetCode 234)
**Problem**: Check if linked list is palindrome.

```java
/**
 * Find middle, reverse second half, compare
 * Time: O(n) - three passes (find middle, reverse, compare)
 * Space: O(1) - only pointers
 */
public boolean isPalindrome(ListNode head) {
    if (head == null || head.next == null) return true;
    
    // Find middle
    ListNode slow = head, fast = head;
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
    }
    
    // Reverse second half
    ListNode secondHalf = reverseList(slow);
    
    // Compare both halves
    ListNode p1 = head, p2 = secondHalf;
    while (p2 != null) {
        if (p1.val != p2.val) return false;
        p1 = p1.next;
        p2 = p2.next;
    }
    
    return true;
}

private ListNode reverseList(ListNode head) {
    ListNode prev = null;
    while (head != null) {
        ListNode next = head.next;
        head.next = prev;
        prev = head;
        head = next;
    }
    return prev;
}
```

---

### Medium Level

#### 8. Remove Nth Node From End (LeetCode 19)
**Problem**: Remove the nth node from the end.

```java
/**
 * Two pointers with gap of n
 * Time: O(n) - one pass
 * Space: O(1) - only pointers
 */
public ListNode removeNthFromEnd(ListNode head, int n) {
    ListNode dummy = new ListNode(0);
    dummy.next = head;
    
    ListNode first = dummy;
    ListNode second = dummy;
    
    // Move first n+1 steps ahead
    for (int i = 0; i <= n; i++) {
        first = first.next;
    }
    
    // Move both until first reaches end
    while (first != null) {
        first = first.next;
        second = second.next;
    }
    
    // Remove nth node
    second.next = second.next.next;
    
    return dummy.next;
}
```

#### 9. Add Two Numbers (LeetCode 2)
**Problem**: Add two numbers represented by linked lists (digits in reverse order).

```java
/**
 * Simulate addition with carry
 * Time: O(max(m, n)) - process longer list
 * Space: O(max(m, n)) - result list size
 */
public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
    ListNode dummy = new ListNode(0);
    ListNode curr = dummy;
    int carry = 0;
    
    // Process both lists and carry
    while (l1 != null || l2 != null || carry != 0) {
        int sum = carry;
        
        if (l1 != null) {
            sum += l1.val;
            l1 = l1.next;
        }
        
        if (l2 != null) {
            sum += l2.val;
            l2 = l2.next;
        }
        
        carry = sum / 10;
        curr.next = new ListNode(sum % 10);
        curr = curr.next;
    }
    
    return dummy.next;
}
```

#### 10. Reorder List (LeetCode 143)
**Problem**: Reorder L0→L1→…→Ln-1→Ln to L0→Ln→L1→Ln-1→L2→Ln-2→…

```java
/**
 * Find middle, reverse second half, merge alternately
 * Time: O(n) - three passes
 * Space: O(1) - only pointers
 */
public void reorderList(ListNode head) {
    if (head == null || head.next == null) return;
    
    // Step 1: Find middle
    ListNode slow = head, fast = head;
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
    }
    
    // Step 2: Reverse second half
    ListNode second = reverseList(slow.next);
    slow.next = null;  // Split list
    
    // Step 3: Merge two halves
    ListNode first = head;
    while (second != null) {
        ListNode temp1 = first.next;
        ListNode temp2 = second.next;
        
        first.next = second;
        second.next = temp1;
        
        first = temp1;
        second = temp2;
    }
}

private ListNode reverseList(ListNode head) {
    ListNode prev = null;
    while (head != null) {
        ListNode next = head.next;
        head.next = prev;
        prev = head;
        head = next;
    }
    return prev;
}
```

#### 11. Linked List Cycle II (LeetCode 142)
**Problem**: Return the node where cycle begins, or null if no cycle.

```java
/**
 * Floyd's algorithm, then find cycle start
 * Time: O(n) - two passes
 * Space: O(1) - only pointers
 */
public ListNode detectCycle(ListNode head) {
    if (head == null) return null;
    
    // Detect cycle
    ListNode slow = head, fast = head;
    boolean hasCycle = false;
    
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
        
        if (slow == fast) {
            hasCycle = true;
            break;
        }
    }
    
    if (!hasCycle) return null;
    
    // Find cycle start: move one pointer to head, advance both at same pace
    slow = head;
    while (slow != fast) {
        slow = slow.next;
        fast = fast.next;
    }
    
    return slow;
}
```

#### 12. Intersection of Two Linked Lists (LeetCode 160)
**Problem**: Find the node where two lists intersect.

```java
/**
 * Two pointers, switch to other list when reaching end
 * Time: O(m + n) - each pointer traverses both lists
 * Space: O(1) - only two pointers
 */
public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
    if (headA == null || headB == null) return null;
    
    ListNode pA = headA;
    ListNode pB = headB;
    
    // When reaching end, switch to other list
    // They will meet at intersection or both become null
    while (pA != pB) {
        pA = (pA == null) ? headB : pA.next;
        pB = (pB == null) ? headA : pB.next;
    }
    
    return pA;  // Could be intersection or null
}
```

#### 13. Remove Duplicates from Sorted List II (LeetCode 82)
**Problem**: Remove all nodes that have duplicate values.

```java
/**
 * Use dummy node, skip all duplicates
 * Time: O(n) - one pass
 * Space: O(1) - only pointers
 */
public ListNode deleteDuplicates(ListNode head) {
    ListNode dummy = new ListNode(0);
    dummy.next = head;
    ListNode prev = dummy;
    
    while (head != null) {
        // Check if current is start of duplicates
        if (head.next != null && head.val == head.next.val) {
            // Skip all nodes with same value
            while (head.next != null && head.val == head.next.val) {
                head = head.next;
            }
            prev.next = head.next;  // Skip duplicates
        } else {
            prev = prev.next;
        }
        head = head.next;
    }
    
    return dummy.next;
}
```

#### 14. Swap Nodes in Pairs (LeetCode 24)
**Problem**: Swap every two adjacent nodes.

```java
/**
 * Iterative swapping with dummy node
 * Time: O(n) - one pass
 * Space: O(1) - only pointers
 */
public ListNode swapPairs(ListNode head) {
    ListNode dummy = new ListNode(0);
    dummy.next = head;
    ListNode prev = dummy;
    
    while (head != null && head.next != null) {
        // Nodes to swap
        ListNode first = head;
        ListNode second = head.next;
        
        // Swap
        prev.next = second;
        first.next = second.next;
        second.next = first;
        
        // Move to next pair
        prev = first;
        head = first.next;
    }
    
    return dummy.next;
}
```

#### 15. Partition List (LeetCode 86)
**Problem**: Partition list so nodes < x come before nodes >= x.

```java
/**
 * Two separate lists, then merge
 * Time: O(n) - one pass
 * Space: O(1) - reuse existing nodes
 */
public ListNode partition(ListNode head, int x) {
    ListNode beforeDummy = new ListNode(0);
    ListNode afterDummy = new ListNode(0);
    
    ListNode before = beforeDummy;
    ListNode after = afterDummy;
    
    // Partition into two lists
    while (head != null) {
        if (head.val < x) {
            before.next = head;
            before = before.next;
        } else {
            after.next = head;
            after = after.next;
        }
        head = head.next;
    }
    
    after.next = null;  // Important: prevent cycle
    before.next = afterDummy.next;  // Connect two lists
    
    return beforeDummy.next;
}
```

#### 16. Copy List with Random Pointer (LeetCode 138)
**Problem**: Deep copy linked list with random pointer.

```java
/**
 * Use HashMap to track original -> copy mapping
 * Time: O(n) - two passes
 * Space: O(n) - HashMap storage
 */
class Node {
    int val;
    Node next;
    Node random;
    
    public Node(int val) {
        this.val = val;
        this.next = null;
        this.random = null;
    }
}

public Node copyRandomList(Node head) {
    if (head == null) return null;
    
    Map<Node, Node> map = new HashMap<>();
    
    // First pass: create all nodes
    Node curr = head;
    while (curr != null) {
        map.put(curr, new Node(curr.val));
        curr = curr.next;
    }
    
    // Second pass: set next and random pointers
    curr = head;
    while (curr != null) {
        Node copy = map.get(curr);
        copy.next = map.get(curr.next);
        copy.random = map.get(curr.random);
        curr = curr.next;
    }
    
    return map.get(head);
}

/**
 * Alternative: O(1) space using interleaving
 * Time: O(n), Space: O(1)
 */
public Node copyRandomListOptimized(Node head) {
    if (head == null) return null;
    
    // Step 1: Create copy nodes interleaved with original
    Node curr = head;
    while (curr != null) {
        Node copy = new Node(curr.val);
        copy.next = curr.next;
        curr.next = copy;
        curr = copy.next;
    }
    
    // Step 2: Set random pointers for copies
    curr = head;
    while (curr != null) {
        if (curr.random != null) {
            curr.next.random = curr.random.next;
        }
        curr = curr.next.next;
    }
    
    // Step 3: Separate original and copy lists
    curr = head;
    Node copyHead = head.next;
    Node copyCurr = copyHead;
    
    while (curr != null) {
        curr.next = curr.next.next;
        if (copyCurr.next != null) {
            copyCurr.next = copyCurr.next.next;
        }
        curr = curr.next;
        copyCurr = copyCurr.next;
    }
    
    return copyHead;
}
```

---

### Hard Level

#### 17. Reverse Nodes in k-Group (LeetCode 25)
**Problem**: Reverse nodes in groups of k.

```java
/**
 * Reverse in groups, connect reversed groups
 * Time: O(n) - visit each node constant times
 * Space: O(1) - only pointers
 */
public ListNode reverseKGroup(ListNode head, int k) {
    if (head == null || k == 1) return head;
    
    ListNode dummy = new ListNode(0);
    dummy.next = head;
    
    ListNode prevGroupEnd = dummy;
    ListNode curr = head;
    
    while (true) {
        // Check if k nodes remain
        ListNode kthNode = prevGroupEnd;
        for (int i = 0; i < k; i++) {
            kthNode = kthNode.next;
            if (kthNode == null) return dummy.next;
        }
        
        ListNode nextGroupStart = kthNode.next;
        
        // Reverse k nodes
        ListNode prev = nextGroupStart;
        curr = prevGroupEnd.next;
        
        for (int i = 0; i < k; i++) {
            ListNode next = curr.next;
            curr.next = prev;
            prev = curr;
            curr = next;
        }
        
        // Connect with previous group
        ListNode groupStart = prevGroupEnd.next;
        prevGroupEnd.next = kthNode;
        prevGroupEnd = groupStart;
    }
}
```

#### 18. Merge k Sorted Lists (LeetCode 23)
**Problem**: Merge k sorted linked lists.

```java
/**
 * Use Min Heap (PriorityQueue) to track smallest elements
 * Time: O(N log k) - N total nodes, heap size k
 * Space: O(k) - heap size
 */
public ListNode mergeKLists(ListNode[] lists) {
    if (lists == null || lists.length == 0) return null;
    
    // Min heap based on node values
    PriorityQueue<ListNode> pq = new PriorityQueue<>((a, b) -> a.val - b.val);
    
    // Add first node of each list to heap
    for (ListNode list : lists) {
        if (list != null) {
            pq.offer(list);
        }
    }
    
    ListNode dummy = new ListNode(0);
    ListNode curr = dummy;
    
    // Extract min and add its next to heap
    while (!pq.isEmpty()) {
        ListNode minNode = pq.poll();
        curr.next = minNode;
        curr = curr.next;
        
        if (minNode.next != null) {
            pq.offer(minNode.next);
        }
    }
    
    return dummy.next;
}

/**
 * Alternative: Divide and Conquer (Merge sort style)
 * Time: O(N log k)
 * Space: O(log k) - recursion stack
 */
public ListNode mergeKListsDivideConquer(ListNode[] lists) {
    if (lists == null || lists.length == 0) return null;
    return mergeHelper(lists, 0, lists.length - 1);
}

private ListNode mergeHelper(ListNode[] lists, int left, int right) {
    if (left == right) return lists[left];
    
    int mid = left + (right - left) / 2;
    ListNode l1 = mergeHelper(lists, left, mid);
    ListNode l2 = mergeHelper(lists, mid + 1, right);
    
    return mergeTwoLists(l1, l2);
}

private ListNode mergeTwoLists(ListNode l1, ListNode l2) {
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

#### 19. Sort List (LeetCode 148)
**Problem**: Sort a linked list in O(n log n) time and O(1) space.

```java
/**
 * Merge sort (bottom-up for O(1) space)
 * Time: O(n log n) - merge sort
 * Space: O(1) - iterative, no recursion
 */
public ListNode sortList(ListNode head) {
    if (head == null || head.next == null) return head;
    
    // Get list length
    int length = 0;
    ListNode curr = head;
    while (curr != null) {
        length++;
        curr = curr.next;
    }
    
    ListNode dummy = new ListNode(0);
    dummy.next = head;
    
    // Merge sublists of increasing sizes
    for (int size = 1; size < length; size *= 2) {
        ListNode prev = dummy;
        curr = dummy.next;
        
        while (curr != null) {
            // Get first sublist
            ListNode left = curr;
            ListNode right = split(left, size);
            curr = split(right, size);
            
            // Merge and connect
            prev = merge(left, right, prev);
        }
    }
    
    return dummy.next;
}

private ListNode split(ListNode head, int size) {
    if (head == null) return null;
    
    for (int i = 1; i < size && head.next != null; i++) {
        head = head.next;
    }
    
    ListNode next = head.next;
    head.next = null;
    return next;
}

private ListNode merge(ListNode l1, ListNode l2, ListNode prev) {
    ListNode curr = prev;
    
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
    
    while (curr.next != null) {
        curr = curr.next;
    }
    
    return curr;
}
```

---

## Tips & Patterns

### Common Techniques

#### 1. Dummy Node
Use dummy node to simplify edge cases (empty list, operations at head)

```java
ListNode dummy = new ListNode(0);
dummy.next = head;
// Operate on dummy.next
return dummy.next;
```

#### 2. Two Pointers

**Fast & Slow (Floyd's Algorithm)**:
- Find middle: slow moves 1, fast moves 2
- Detect cycle: if they meet, cycle exists
- Find cycle start: reset one to head, move both at same pace

**Gap of n**:
- Remove nth from end: maintain gap of n+1
- Find kth from end: maintain gap of k

**Different speeds**:
- Merge: compare values, advance smaller

#### 3. Reverse List Pattern

```java
ListNode prev = null;
while (curr != null) {
    ListNode next = curr.next;
    curr.next = prev;
    prev = curr;
    curr = next;
}
return prev;
```

#### 4. Split List

```java
// Find middle and split
ListNode slow = head, fast = head, prev = null;
while (fast != null && fast.next != null) {
    prev = slow;
    slow = slow.next;
    fast = fast.next.next;
}
prev.next = null;  // Split
```

### Common Patterns

1. **Reversal Problems**: Use three pointers (prev, curr, next)
2. **Cycle Detection**: Fast & slow pointers
3. **Finding Middle**: Fast & slow pointers
4. **Merge Problems**: Two pointers, dummy node
5. **k-Group Operations**: Count k, process, repeat
6. **Copy with Extra Pointers**: HashMap or interleaving
7. **Sorting**: Merge sort (O(n log n))
8. **Remove/Delete**: Dummy node, careful pointer manipulation

### Edge Cases

1. **Empty list**: `head == null`
2. **Single node**: `head.next == null`
3. **Two nodes**: Both branches in conditions
4. **Odd/even length**: Affects middle finding
5. **Cycle**: Check for null before dereferencing
6. **Duplicates**: Consecutive duplicates

### Interview Tips

1. **Draw it out**: Visualize with 3-4 nodes
2. **Use dummy node**: Simplifies head operations
3. **Check pointers**: Before accessing `.next`
4. **Track previous**: Often needed for modifications
5. **Test edge cases**: Empty, single, two nodes
6. **Explain complexity**: Time and space

### Time Complexity Patterns

- **Traversal**: O(n)
- **Two pointers (one pass)**: O(n)
- **Sorting**: O(n log n)
- **Nested loops**: O(n²)
- **With heap**: O(n log k)

### Space Complexity Patterns

- **Pointers only**: O(1)
- **Recursion**: O(n) for call stack
- **HashMap**: O(n)
- **Heap**: O(k)

### Common Mistakes

- ❌ Not checking for null before accessing `.next`
- ❌ Losing head reference
- ❌ Creating cycles when modifying pointers
- ❌ Not handling empty list
- ❌ Off-by-one errors in gap calculations
- ❌ Modifying list while iterating without care

---

**Key Takeaways**:
1. Master the dummy node technique
2. Practice two-pointer patterns extensively
3. Always visualize with small examples
4. Check edge cases: null, single node, two nodes
5. Understand when to use ArrayList vs LinkedList
6. Know time/space complexity for all operations

