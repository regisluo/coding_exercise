// filepath: /Users/ttfl/vprojects/coding_exercise/basic_ds/tree/java_tree.md

# Java Tree Data Structure - Interview Guide

## Table of Contents
1. [Basic Concepts](#basic-concepts)
2. [Java SDK - Tree Implementations](#java-sdk-tree-implementations)
3. [Common Tree Algorithms](#common-tree-algorithms)
4. [Important Interview Questions](#important-interview-questions)
5. [Tips & Patterns](#tips--patterns)

---

## Basic Concepts

### 1. Tree Terminology

**Core Definitions:**
- **Node**: Basic unit containing data and references to children
- **Root**: Top node with no parent
- **Leaf**: Node with no children
- **Parent/Child**: Direct connection relationship
- **Siblings**: Nodes with same parent
- **Edge**: Connection between two nodes
- **Path**: Sequence of nodes connected by edges
- **Height**: Longest path from node to any leaf (leaf height = 0)
- **Depth**: Distance from root to node (root depth = 0)
- **Level**: Depth + 1
- **Subtree**: Tree formed by node and all its descendants
- **Ancestor**: All nodes on path from node to root
- **Descendant**: All nodes in subtree rooted at node

### 2. Tree Types

#### Binary Tree
Each node has **at most 2 children** (left and right)

**Properties:**
- Max nodes at level L: 2^L
- Max nodes in tree of height h: 2^(h+1) - 1
- Min height for n nodes: log₂(n+1) - 1

**Special Binary Trees:**

1. **Full Binary Tree**: Every node has 0 or 2 children
2. **Complete Binary Tree**: All levels filled except possibly last, filled left to right
3. **Perfect Binary Tree**: All internal nodes have 2 children, all leaves at same level
4. **Balanced Binary Tree**: Height difference between left and right subtrees ≤ 1 for all nodes
5. **Skewed Binary Tree**: All nodes have only left or only right child

#### Binary Search Tree (BST)
Binary tree with ordering property:
- All values in left subtree < node value
- All values in right subtree > node value
- No duplicates (typically)
- Inorder traversal gives sorted sequence

**Operations (average case):**
- Search: O(log n)
- Insert: O(log n)
- Delete: O(log n)
- Worst case (skewed): O(n)

#### Balanced Trees

1. **AVL Tree**: Self-balancing BST
   - Balance factor = height(left) - height(right) ∈ {-1, 0, 1}
   - Operations: O(log n) guaranteed

2. **Red-Black Tree**: Self-balancing BST with color properties
   - Used in TreeMap, TreeSet
   - Operations: O(log n) guaranteed

#### N-ary Tree
Each node can have **N children**

#### Other Important Trees

1. **Trie (Prefix Tree)**: For string operations
2. **Segment Tree**: For range queries
3. **Heap**: Complete binary tree (Min/Max heap)
4. **B-Tree**: Multi-way tree for databases

---

## Java SDK - Tree Implementations

### 1. TreeNode Class (Custom Implementation)

```java
// Binary Tree Node
class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    
    TreeNode() {}
    
    TreeNode(int val) {
        this.val = val;
    }
    
    TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }
}
```

```java
// N-ary Tree Node
class Node {
    public int val;
    public List<Node> children;
    
    public Node() {
        children = new ArrayList<>();
    }
    
    public Node(int val) {
        this.val = val;
        children = new ArrayList<>();
    }
    
    public Node(int val, List<Node> children) {
        this.val = val;
        this.children = children;
    }
}
```

```java
// Tree Node with Parent Reference
class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    TreeNode parent;
    
    TreeNode(int val) {
        this.val = val;
    }
}
```

### 2. Java Collections Framework - Tree-Based Classes

#### TreeSet (Sorted Set)
Based on **Red-Black Tree**

```java
import java.util.TreeSet;

TreeSet<Integer> set = new TreeSet<>();

// Basic Operations - O(log n)
set.add(5);           // Add element
set.remove(5);        // Remove element
set.contains(5);      // Check existence
set.size();           // Get size
set.isEmpty();        // Check if empty
set.clear();          // Remove all

// Range Operations
set.first();          // Smallest element
set.last();           // Largest element
set.lower(5);         // Largest element < 5
set.floor(5);         // Largest element <= 5
set.higher(5);        // Smallest element > 5
set.ceiling(5);       // Smallest element >= 5

// Range Views
set.headSet(5);       // Elements < 5
set.tailSet(5);       // Elements >= 5
set.subSet(3, 7);     // Elements >= 3 and < 7

// Iteration (sorted order)
for (Integer num : set) {
    System.out.println(num);
}
```

#### TreeMap (Sorted Map)
Based on **Red-Black Tree**

```java
import java.util.TreeMap;

TreeMap<Integer, String> map = new TreeMap<>();

// Basic Operations - O(log n)
map.put(1, "one");
map.get(1);
map.remove(1);
map.containsKey(1);
map.containsValue("one");

// Navigation
map.firstKey();           // Smallest key
map.lastKey();            // Largest key
map.lowerKey(5);          // Largest key < 5
map.floorKey(5);          // Largest key <= 5
map.higherKey(5);         // Smallest key > 5
map.ceilingKey(5);        // Smallest key >= 5

// Entry Operations
map.firstEntry();         // Entry with smallest key
map.lastEntry();          // Entry with largest key
map.lowerEntry(5);
map.floorEntry(5);
map.higherEntry(5);
map.ceilingEntry(5);

// Range Views
map.headMap(5);           // Keys < 5
map.tailMap(5);           // Keys >= 5
map.subMap(3, 7);         // Keys >= 3 and < 7
```

#### PriorityQueue (Heap)
Based on **Binary Heap** (Complete Binary Tree)

```java
import java.util.PriorityQueue;
import java.util.Comparator;

// Min Heap (default)
PriorityQueue<Integer> minHeap = new PriorityQueue<>();

// Max Heap
PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
// Or: new PriorityQueue<>((a, b) -> b - a);

// Operations
minHeap.offer(5);      // Add - O(log n)
minHeap.add(5);        // Add - O(log n)
minHeap.poll();        // Remove and return min - O(log n)
minHeap.peek();        // View min - O(1)
minHeap.remove(5);     // Remove specific - O(n)
minHeap.size();        // Size - O(1)
minHeap.isEmpty();     // Check empty - O(1)

// Custom Comparator
PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[0] - b[0]);
```

### 3. Common Data Structures Used with Trees

```java
import java.util.Stack;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Deque;
import java.util.ArrayDeque;

// Stack - for DFS, iterative traversals
Stack<TreeNode> stack = new Stack<>();
stack.push(node);
TreeNode top = stack.pop();
TreeNode peek = stack.peek();

// Queue - for BFS, level order
Queue<TreeNode> queue = new LinkedList<>();
queue.offer(node);
TreeNode front = queue.poll();
TreeNode peek = queue.peek();

// Deque - for flexible operations
Deque<TreeNode> deque = new ArrayDeque<>();
deque.offerFirst(node);   // Add to front
deque.offerLast(node);    // Add to back
deque.pollFirst();        // Remove from front
deque.pollLast();         // Remove from back
```

---

## Common Tree Algorithms

### 1. Tree Traversals

#### Preorder (Root → Left → Right)
```java
// Recursive
public void preorder(TreeNode root, List<Integer> result) {
    if (root == null) return;
    result.add(root.val);
    preorder(root.left, result);
    preorder(root.right, result);
}

// Iterative
public List<Integer> preorderIterative(TreeNode root) {
    List<Integer> result = new ArrayList<>();
    if (root == null) return result;
    
    Stack<TreeNode> stack = new Stack<>();
    stack.push(root);
    
    while (!stack.isEmpty()) {
        TreeNode node = stack.pop();
        result.add(node.val);
        
        if (node.right != null) stack.push(node.right);
        if (node.left != null) stack.push(node.left);
    }
    
    return result;
}
```
**Use Cases**: Copy tree, prefix expressions, tree serialization

#### Inorder (Left → Root → Right)
```java
// Recursive
public void inorder(TreeNode root, List<Integer> result) {
    if (root == null) return;
    inorder(root.left, result);
    result.add(root.val);
    inorder(root.right, result);
}

// Iterative
public List<Integer> inorderIterative(TreeNode root) {
    List<Integer> result = new ArrayList<>();
    Stack<TreeNode> stack = new Stack<>();
    TreeNode curr = root;
    
    while (curr != null || !stack.isEmpty()) {
        while (curr != null) {
            stack.push(curr);
            curr = curr.left;
        }
        curr = stack.pop();
        result.add(curr.val);
        curr = curr.right;
    }
    
    return result;
}
```
**Use Cases**: BST sorted order, infix expressions

#### Postorder (Left → Right → Root)
```java
// Recursive
public void postorder(TreeNode root, List<Integer> result) {
    if (root == null) return;
    postorder(root.left, result);
    postorder(root.right, result);
    result.add(root.val);
}

// Iterative (Two Stacks)
public List<Integer> postorderIterative(TreeNode root) {
    List<Integer> result = new ArrayList<>();
    if (root == null) return result;
    
    Stack<TreeNode> stack1 = new Stack<>();
    Stack<TreeNode> stack2 = new Stack<>();
    
    stack1.push(root);
    while (!stack1.isEmpty()) {
        TreeNode node = stack1.pop();
        stack2.push(node);
        
        if (node.left != null) stack1.push(node.left);
        if (node.right != null) stack1.push(node.right);
    }
    
    while (!stack2.isEmpty()) {
        result.add(stack2.pop().val);
    }
    
    return result;
}
```
**Use Cases**: Delete tree, postfix expressions, tree height

#### Level Order (BFS)
```java
public List<List<Integer>> levelOrder(TreeNode root) {
    List<List<Integer>> result = new ArrayList<>();
    if (root == null) return result;
    
    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);
    
    while (!queue.isEmpty()) {
        int size = queue.size();
        List<Integer> level = new ArrayList<>();
        
        for (int i = 0; i < size; i++) {
            TreeNode node = queue.poll();
            level.add(node.val);
            
            if (node.left != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }
        
        result.add(level);
    }
    
    return result;
}
```
**Use Cases**: Level-wise operations, shortest path, minimum depth

### 2. Essential Tree Operations

#### Height/Depth of Tree
```java
public int maxDepth(TreeNode root) {
    if (root == null) return 0;
    return 1 + Math.max(maxDepth(root.left), maxDepth(root.right));
}
// Time: O(n), Space: O(h)
```

#### Check if Balanced
```java
public boolean isBalanced(TreeNode root) {
    return checkHeight(root) != -1;
}

private int checkHeight(TreeNode node) {
    if (node == null) return 0;
    
    int leftHeight = checkHeight(node.left);
    if (leftHeight == -1) return -1;
    
    int rightHeight = checkHeight(node.right);
    if (rightHeight == -1) return -1;
    
    if (Math.abs(leftHeight - rightHeight) > 1) return -1;
    
    return 1 + Math.max(leftHeight, rightHeight);
}
// Time: O(n), Space: O(h)
```

#### Lowest Common Ancestor (LCA)
```java
public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
    if (root == null || root == p || root == q) return root;
    
    TreeNode left = lowestCommonAncestor(root.left, p, q);
    TreeNode right = lowestCommonAncestor(root.right, p, q);
    
    if (left != null && right != null) return root;
    return left != null ? left : right;
}
// Time: O(n), Space: O(h)
```

#### Validate BST
```java
public boolean isValidBST(TreeNode root) {
    return validate(root, null, null);
}

private boolean validate(TreeNode node, Integer min, Integer max) {
    if (node == null) return true;
    
    if ((min != null && node.val <= min) || 
        (max != null && node.val >= max)) {
        return false;
    }
    
    return validate(node.left, min, node.val) && 
           validate(node.right, node.val, max);
}
// Time: O(n), Space: O(h)
```

---

## Important Interview Questions

### Easy Level

#### 1. Maximum Depth of Binary Tree (LeetCode 104)
**Problem**: Find the maximum depth of a binary tree.

```java
public int maxDepth(TreeNode root) {
    if (root == null) return 0;
    return 1 + Math.max(maxDepth(root.left), maxDepth(root.right));
}
```
**Time**: O(n), **Space**: O(h)

#### 2. Same Tree (LeetCode 100)
**Problem**: Check if two trees are identical.

```java
public boolean isSameTree(TreeNode p, TreeNode q) {
    if (p == null && q == null) return true;
    if (p == null || q == null) return false;
    
    return p.val == q.val && 
           isSameTree(p.left, q.left) && 
           isSameTree(p.right, q.right);
}
```
**Time**: O(n), **Space**: O(h)

#### 3. Invert Binary Tree (LeetCode 226)
**Problem**: Invert a binary tree (mirror image).

```java
public TreeNode invertTree(TreeNode root) {
    if (root == null) return null;
    
    TreeNode temp = root.left;
    root.left = invertTree(root.right);
    root.right = invertTree(temp);
    
    return root;
}
```
**Time**: O(n), **Space**: O(h)

#### 4. Symmetric Tree (LeetCode 101)
**Problem**: Check if tree is mirror of itself.

```java
public boolean isSymmetric(TreeNode root) {
    return isMirror(root, root);
}

private boolean isMirror(TreeNode t1, TreeNode t2) {
    if (t1 == null && t2 == null) return true;
    if (t1 == null || t2 == null) return false;
    
    return t1.val == t2.val && 
           isMirror(t1.left, t2.right) && 
           isMirror(t1.right, t2.left);
}
```
**Time**: O(n), **Space**: O(h)

#### 5. Minimum Depth of Binary Tree (LeetCode 111)
**Problem**: Find minimum depth from root to leaf.

```java
public int minDepth(TreeNode root) {
    if (root == null) return 0;
    
    // If leaf node
    if (root.left == null && root.right == null) return 1;
    
    // If left subtree is null, recur for right
    if (root.left == null) return minDepth(root.right) + 1;
    
    // If right subtree is null, recur for left
    if (root.right == null) return minDepth(root.left) + 1;
    
    return Math.min(minDepth(root.left), minDepth(root.right)) + 1;
}
```
**Time**: O(n), **Space**: O(h)

#### 6. Path Sum (LeetCode 112)
**Problem**: Check if root-to-leaf path with given sum exists.

```java
public boolean hasPathSum(TreeNode root, int targetSum) {
    if (root == null) return false;
    
    if (root.left == null && root.right == null) {
        return targetSum == root.val;
    }
    
    return hasPathSum(root.left, targetSum - root.val) || 
           hasPathSum(root.right, targetSum - root.val);
}
```
**Time**: O(n), **Space**: O(h)

#### 7. Merge Two Binary Trees (LeetCode 617)
**Problem**: Merge two trees by summing overlapping nodes.

```java
public TreeNode mergeTrees(TreeNode root1, TreeNode root2) {
    if (root1 == null) return root2;
    if (root2 == null) return root1;
    
    TreeNode merged = new TreeNode(root1.val + root2.val);
    merged.left = mergeTrees(root1.left, root2.left);
    merged.right = mergeTrees(root1.right, root2.right);
    
    return merged;
}
```
**Time**: O(min(m, n)), **Space**: O(min(m, n))

---

### Medium Level

#### 8. Binary Tree Level Order Traversal (LeetCode 102)
**Problem**: Return level order traversal as list of lists.

```java
public List<List<Integer>> levelOrder(TreeNode root) {
    List<List<Integer>> result = new ArrayList<>();
    if (root == null) return result;
    
    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);
    
    while (!queue.isEmpty()) {
        int size = queue.size();
        List<Integer> level = new ArrayList<>();
        
        for (int i = 0; i < size; i++) {
            TreeNode node = queue.poll();
            level.add(node.val);
            
            if (node.left != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }
        
        result.add(level);
    }
    
    return result;
}
```
**Time**: O(n), **Space**: O(w) - w is max width

#### 9. Validate Binary Search Tree (LeetCode 98)
**Problem**: Determine if tree is valid BST.

```java
public boolean isValidBST(TreeNode root) {
    return validate(root, null, null);
}

private boolean validate(TreeNode node, Integer min, Integer max) {
    if (node == null) return true;
    
    if ((min != null && node.val <= min) || 
        (max != null && node.val >= max)) {
        return false;
    }
    
    return validate(node.left, min, node.val) && 
           validate(node.right, node.val, max);
}
```
**Time**: O(n), **Space**: O(h)

#### 10. Binary Tree Right Side View (LeetCode 199)
**Problem**: Return values visible from right side.

```java
public List<Integer> rightSideView(TreeNode root) {
    List<Integer> result = new ArrayList<>();
    if (root == null) return result;
    
    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);
    
    while (!queue.isEmpty()) {
        int size = queue.size();
        for (int i = 0; i < size; i++) {
            TreeNode node = queue.poll();
            if (i == size - 1) result.add(node.val);
            
            if (node.left != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }
    }
    
    return result;
}
```
**Time**: O(n), **Space**: O(w)

#### 11. Kth Smallest Element in BST (LeetCode 230)
**Problem**: Find kth smallest element in BST.

```java
private int count = 0;
private int result = 0;

public int kthSmallest(TreeNode root, int k) {
    inorder(root, k);
    return result;
}

private void inorder(TreeNode node, int k) {
    if (node == null) return;
    
    inorder(node.left, k);
    
    count++;
    if (count == k) {
        result = node.val;
        return;
    }
    
    inorder(node.right, k);
}
```
**Time**: O(n), **Space**: O(h)

#### 12. Lowest Common Ancestor of BST (LeetCode 235)
**Problem**: Find LCA in BST.

```java
public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
    if (p.val < root.val && q.val < root.val) {
        return lowestCommonAncestor(root.left, p, q);
    } else if (p.val > root.val && q.val > root.val) {
        return lowestCommonAncestor(root.right, p, q);
    }
    return root;
}
```
**Time**: O(h), **Space**: O(h)

#### 13. Path Sum II (LeetCode 113)
**Problem**: Find all root-to-leaf paths with given sum.

```java
public List<List<Integer>> pathSum(TreeNode root, int targetSum) {
    List<List<Integer>> result = new ArrayList<>();
    dfs(root, targetSum, new ArrayList<>(), result);
    return result;
}

private void dfs(TreeNode node, int remaining, List<Integer> path, 
                 List<List<Integer>> result) {
    if (node == null) return;
    
    path.add(node.val);
    
    if (node.left == null && node.right == null && remaining == node.val) {
        result.add(new ArrayList<>(path));
    }
    
    dfs(node.left, remaining - node.val, path, result);
    dfs(node.right, remaining - node.val, path, result);
    
    path.remove(path.size() - 1); // Backtrack
}
```
**Time**: O(n²), **Space**: O(h)

#### 14. Construct Binary Tree from Preorder and Inorder (LeetCode 105)
**Problem**: Build tree from preorder and inorder arrays.

```java
private int preIndex = 0;
private Map<Integer, Integer> inorderMap = new HashMap<>();

public TreeNode buildTree(int[] preorder, int[] inorder) {
    for (int i = 0; i < inorder.length; i++) {
        inorderMap.put(inorder[i], i);
    }
    return build(preorder, 0, inorder.length - 1);
}

private TreeNode build(int[] preorder, int left, int right) {
    if (left > right) return null;
    
    int val = preorder[preIndex++];
    TreeNode node = new TreeNode(val);
    
    int index = inorderMap.get(val);
    node.left = build(preorder, left, index - 1);
    node.right = build(preorder, index + 1, right);
    
    return node;
}
```
**Time**: O(n), **Space**: O(n)

#### 15. Diameter of Binary Tree (LeetCode 543)
**Problem**: Find length of longest path between any two nodes.

```java
private int diameter = 0;

public int diameterOfBinaryTree(TreeNode root) {
    height(root);
    return diameter;
}

private int height(TreeNode node) {
    if (node == null) return 0;
    
    int leftHeight = height(node.left);
    int rightHeight = height(node.right);
    
    diameter = Math.max(diameter, leftHeight + rightHeight);
    
    return 1 + Math.max(leftHeight, rightHeight);
}
```
**Time**: O(n), **Space**: O(h)

#### 16. Serialize and Deserialize Binary Tree (LeetCode 297)
**Problem**: Encode tree to string and decode string to tree.

```java
public class Codec {
    public String serialize(TreeNode root) {
        if (root == null) return "#";
        return root.val + "," + serialize(root.left) + "," + serialize(root.right);
    }
    
    public TreeNode deserialize(String data) {
        Queue<String> queue = new LinkedList<>(Arrays.asList(data.split(",")));
        return buildTree(queue);
    }
    
    private TreeNode buildTree(Queue<String> queue) {
        String val = queue.poll();
        if (val.equals("#")) return null;
        
        TreeNode node = new TreeNode(Integer.parseInt(val));
        node.left = buildTree(queue);
        node.right = buildTree(queue);
        return node;
    }
}
```
**Time**: O(n), **Space**: O(n)

#### 17. Flatten Binary Tree to Linked List (LeetCode 114)
**Problem**: Flatten tree to linked list in-place (preorder).

```java
public void flatten(TreeNode root) {
    if (root == null) return;
    
    flatten(root.left);
    flatten(root.right);
    
    TreeNode rightSubtree = root.right;
    root.right = root.left;
    root.left = null;
    
    TreeNode curr = root;
    while (curr.right != null) {
        curr = curr.right;
    }
    curr.right = rightSubtree;
}
```
**Time**: O(n), **Space**: O(h)

---

### Hard Level

#### 18. Binary Tree Maximum Path Sum (LeetCode 124)
**Problem**: Find maximum path sum (path can start/end anywhere).

```java
private int maxSum = Integer.MIN_VALUE;

public int maxPathSum(TreeNode root) {
    maxGain(root);
    return maxSum;
}

private int maxGain(TreeNode node) {
    if (node == null) return 0;
    
    // Max gain from left and right subtrees (ignore negative)
    int leftGain = Math.max(maxGain(node.left), 0);
    int rightGain = Math.max(maxGain(node.right), 0);
    
    // Path sum going through current node
    int pathSum = node.val + leftGain + rightGain;
    maxSum = Math.max(maxSum, pathSum);
    
    // Return max gain if continue with parent
    return node.val + Math.max(leftGain, rightGain);
}
```
**Time**: O(n), **Space**: O(h)

#### 19. Vertical Order Traversal (LeetCode 987)
**Problem**: Return vertical order traversal.

```java
public List<List<Integer>> verticalTraversal(TreeNode root) {
    List<List<Integer>> result = new ArrayList<>();
    if (root == null) return result;
    
    // Map: column -> (row -> list of values)
    TreeMap<Integer, TreeMap<Integer, List<Integer>>> map = new TreeMap<>();
    
    dfs(root, 0, 0, map);
    
    for (TreeMap<Integer, List<Integer>> colMap : map.values()) {
        List<Integer> col = new ArrayList<>();
        for (List<Integer> values : colMap.values()) {
            Collections.sort(values);
            col.addAll(values);
        }
        result.add(col);
    }
    
    return result;
}

private void dfs(TreeNode node, int row, int col, 
                 TreeMap<Integer, TreeMap<Integer, List<Integer>>> map) {
    if (node == null) return;
    
    map.putIfAbsent(col, new TreeMap<>());
    map.get(col).putIfAbsent(row, new ArrayList<>());
    map.get(col).get(row).add(node.val);
    
    dfs(node.left, row + 1, col - 1, map);
    dfs(node.right, row + 1, col + 1, map);
}
```
**Time**: O(n log n), **Space**: O(n)

#### 20. Binary Tree Cameras (LeetCode 968)
**Problem**: Minimum cameras needed to monitor all nodes.

```java
private int cameras = 0;

public int minCameraCover(TreeNode root) {
    return dfs(root) == 0 ? cameras + 1 : cameras;
}

// Returns: 0 = needs camera, 1 = has camera, 2 = covered
private int dfs(TreeNode node) {
    if (node == null) return 2; // null is covered
    
    int left = dfs(node.left);
    int right = dfs(node.right);
    
    // If any child needs camera, place camera here
    if (left == 0 || right == 0) {
        cameras++;
        return 1;
    }
    
    // If any child has camera, this is covered
    if (left == 1 || right == 1) {
        return 2;
    }
    
    // Both children covered but no camera, this needs camera
    return 0;
}
```
**Time**: O(n), **Space**: O(h)

---

## Tips & Patterns

### Common Triggers

1. **Use Recursion**: Most tree problems have recursive solutions
2. **BST Property**: Inorder traversal gives sorted order
3. **Level-by-level**: Use BFS with Queue
4. **Path/Sum Problems**: DFS with backtracking
5. **Two Trees**: Often need helper function comparing nodes
6. **Parent-Child Info**: May need to pass info down or return up

### Common Patterns

#### 1. Top-Down Approach (Preorder-like)
Pass information from parent to children
```java
void topDown(TreeNode node, int parentInfo) {
    if (node == null) return;
    // Use parentInfo to compute current
    topDown(node.left, newInfo);
    topDown(node.right, newInfo);
}
```

#### 2. Bottom-Up Approach (Postorder-like)
Return information from children to parent
```java
int bottomUp(TreeNode node) {
    if (node == null) return baseValue;
    int left = bottomUp(node.left);
    int right = bottomUp(node.right);
    return compute(left, right, node.val);
}
```

#### 3. Divide and Conquer
Solve for subtrees, combine results
```java
Result divideConquer(TreeNode node) {
    if (node == null) return baseCase;
    Result left = divideConquer(node.left);
    Result right = divideConquer(node.right);
    return merge(left, right, node);
}
```

#### 4. Global Variable Pattern
Track global state across recursion
```java
private int globalResult;

public int solve(TreeNode root) {
    globalResult = initialValue;
    helper(root);
    return globalResult;
}
```

### Edge Cases to Consider

1. **Empty tree**: `root == null`
2. **Single node**: No children
3. **Skewed tree**: All left or all right children
4. **Complete/Perfect tree**: Fully balanced
5. **Duplicate values**: How to handle?
6. **Integer overflow**: Use long if needed
7. **Leaf nodes**: Special handling often needed

### Time Complexity Cheatsheet

| Operation | Binary Tree | BST (balanced) | BST (skewed) |
|-----------|-------------|----------------|--------------|
| Search    | O(n)        | O(log n)       | O(n)         |
| Insert    | O(n)        | O(log n)       | O(n)         |
| Delete    | O(n)        | O(log n)       | O(n)         |
| Traversal | O(n)        | O(n)           | O(n)         |
| Height    | O(n)        | O(n)           | O(n)         |

### Space Complexity Cheatsheet

| Approach | Space Complexity |
|----------|------------------|
| Recursive DFS | O(h) - call stack |
| Iterative with Stack | O(h) |
| BFS with Queue | O(w) - max width |
| Morris Traversal | O(1) |

### Interview Strategy

1. **Clarify Requirements**:
   - Node value type and range?
   - Duplicates allowed?
   - Tree balanced or can be skewed?
   - Can modify tree structure?

2. **Draw Examples**:
   - Start with simple cases (2-3 nodes)
   - Consider edge cases
   - Walk through algorithm step-by-step

3. **Choose Approach**:
   - Recursive vs iterative?
   - DFS vs BFS?
   - Need additional data structures?

4. **Code**:
   - Handle base cases first
   - Clear variable names
   - Add comments for complex logic

5. **Test**:
   - Empty tree
   - Single node
   - Provided examples
   - Edge cases

6. **Optimize**:
   - Can we reduce time/space?
   - Is there a better traversal order?
   - Can we avoid recursion if needed?

### Common Mistakes to Avoid

- ❌ Forgetting null checks
- ❌ Modifying tree structure incorrectly
- ❌ Not handling leaf nodes properly
- ❌ Infinite recursion (missing base case)
- ❌ Not restoring state in backtracking
- ❌ Comparing object references instead of values
- ❌ Off-by-one errors in level/height calculations
- ❌ Not considering skewed trees in complexity analysis

### Quick Reference

**When to use each traversal:**
- **Preorder**: Copy tree, prefix expressions, serialize
- **Inorder**: BST sorted order, validate BST
- **Postorder**: Delete tree, postfix expressions, calculate height
- **Level Order**: Shortest path, minimum depth, level-wise operations

**Common helper functions:**
```java
// Check if leaf
boolean isLeaf(TreeNode node) {
    return node != null && node.left == null && node.right == null;
}

// Count nodes
int countNodes(TreeNode root) {
    if (root == null) return 0;
    return 1 + countNodes(root.left) + countNodes(root.right);
}

// Find max value
int findMax(TreeNode root) {
    if (root == null) return Integer.MIN_VALUE;
    return Math.max(root.val, Math.max(findMax(root.left), findMax(root.right)));
}
```

---

**Remember**: Practice is key! Start with easy problems, understand the patterns, then tackle medium and hard problems. Focus on understanding why solutions work, not just memorizing code.

