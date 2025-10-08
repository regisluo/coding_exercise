# String & Array Data Structures - Interview Guide

## Basics
**String**: Immutable sequence of characters in Java
**Array**: Fixed-size container of elements of the same type
**StringBuilder**: Mutable string for efficient concatenation

---

## When to Use String/Array - Key Triggers

### 1. **Two Pointer Technique**
- Palindrome checking
- Target sum problems
- Removing duplicates
- Reversing arrays/strings

### 2. **Sliding Window Problems**
- Substring problems
- Maximum/minimum subarray
- Character frequency analysis
- Longest substring without repeating characters

### 3. **Hash Map/Set Problems**
- Anagram detection
- Character counting
- Finding duplicates
- Intersection/union operations

### 4. **String Manipulation**
- Pattern matching
- String parsing and validation
- Text processing and formatting
- Encoding/decoding problems

### 5. **Array Sorting & Searching**
- Binary search variations
- Merge operations
- Finding missing elements
- Rearrangement problems

---

## Common Interview Patterns & Solutions

### 1. Two Pointer Technique

#### **Valid Palindrome**
```java
public boolean isPalindrome(String s) {
    int left = 0, right = s.length() - 1;
    
    while (left < right) {
        // Skip non-alphanumeric characters
        while (left < right && !Character.isLetterOrDigit(s.charAt(left))) {
            left++;
        }
        while (left < right && !Character.isLetterOrDigit(s.charAt(right))) {
            right--;
        }
        
        // Compare characters (case insensitive)
        if (Character.toLowerCase(s.charAt(left)) != 
            Character.toLowerCase(s.charAt(right))) {
            return false;
        }
        left++;
        right--;
    }
    return true;
}
```

#### **Two Sum (Sorted Array)**
```java
public int[] twoSum(int[] numbers, int target) {
    int left = 0, right = numbers.length - 1;
    
    while (left < right) {
        int sum = numbers[left] + numbers[right];
        if (sum == target) {
            return new int[]{left + 1, right + 1}; // 1-indexed
        } else if (sum < target) {
            left++;
        } else {
            right--;
        }
    }
    return new int[]{-1, -1}; // Not found
}
```

#### **Remove Duplicates from Sorted Array**
```java
public int removeDuplicates(int[] nums) {
    if (nums.length == 0) return 0;
    
    int writeIndex = 1;
    for (int i = 1; i < nums.length; i++) {
        if (nums[i] != nums[i - 1]) {
            nums[writeIndex] = nums[i];
            writeIndex++;
        }
    }
    return writeIndex;
}
```

### 2. Sliding Window Problems

#### **Longest Substring Without Repeating Characters**
```java
public int lengthOfLongestSubstring(String s) {
    Set<Character> seen = new HashSet<>();
    int left = 0, maxLength = 0;
    
    for (int right = 0; right < s.length(); right++) {
        char c = s.charAt(right);
        
        // Shrink window until no duplicates
        while (seen.contains(c)) {
            seen.remove(s.charAt(left));
            left++;
        }
        
        seen.add(c);
        maxLength = Math.max(maxLength, right - left + 1);
    }
    return maxLength;
}
```

#### **Minimum Window Substring**
```java
public String minWindow(String s, String t) {
    Map<Character, Integer> targetCount = new HashMap<>();
    for (char c : t.toCharArray()) {
        targetCount.put(c, targetCount.getOrDefault(c, 0) + 1);
    }
    
    int left = 0, minLen = Integer.MAX_VALUE, minStart = 0;
    int required = targetCount.size(), formed = 0;
    Map<Character, Integer> windowCount = new HashMap<>();
    
    for (int right = 0; right < s.length(); right++) {
        char c = s.charAt(right);
        windowCount.put(c, windowCount.getOrDefault(c, 0) + 1);
        
        if (targetCount.containsKey(c) && 
            windowCount.get(c).intValue() == targetCount.get(c).intValue()) {
            formed++;
        }
        
        // Contract window
        while (left <= right && formed == required) {
            if (right - left + 1 < minLen) {
                minLen = right - left + 1;
                minStart = left;
            }
            
            char leftChar = s.charAt(left);
            windowCount.put(leftChar, windowCount.get(leftChar) - 1);
            if (targetCount.containsKey(leftChar) && 
                windowCount.get(leftChar) < targetCount.get(leftChar)) {
                formed--;
            }
            left++;
        }
    }
    
    return minLen == Integer.MAX_VALUE ? "" : s.substring(minStart, minStart + minLen);
}
```

#### **Maximum Subarray (Kadane's Algorithm)**
```java
public int maxSubArray(int[] nums) {
    int maxSoFar = nums[0];
    int maxEndingHere = nums[0];
    
    for (int i = 1; i < nums.length; i++) {
        maxEndingHere = Math.max(nums[i], maxEndingHere + nums[i]);
        maxSoFar = Math.max(maxSoFar, maxEndingHere);
    }
    return maxSoFar;
}
```

### 3. Hash Map/Set Problems

#### **Group Anagrams**
```java
public List<List<String>> groupAnagrams(String[] strs) {
    Map<String, List<String>> groups = new HashMap<>();
    
    for (String str : strs) {
        char[] chars = str.toCharArray();
        Arrays.sort(chars);
        String key = new String(chars);
        
        groups.computeIfAbsent(key, k -> new ArrayList<>()).add(str);
    }
    
    return new ArrayList<>(groups.values());
}
```

#### **First Unique Character**
```java
public int firstUniqChar(String s) {
    int[] count = new int[26];
    
    // Count frequency
    for (char c : s.toCharArray()) {
        count[c - 'a']++;
    }
    
    // Find first unique
    for (int i = 0; i < s.length(); i++) {
        if (count[s.charAt(i) - 'a'] == 1) {
            return i;
        }
    }
    return -1;
}
```

#### **Intersection of Two Arrays**
```java
public int[] intersection(int[] nums1, int[] nums2) {
    Set<Integer> set1 = new HashSet<>();
    Set<Integer> result = new HashSet<>();
    
    for (int num : nums1) {
        set1.add(num);
    }
    
    for (int num : nums2) {
        if (set1.contains(num)) {
            result.add(num);
        }
    }
    
    return result.stream().mapToInt(Integer::intValue).toArray();
}
```

### 4. String Manipulation

#### **Reverse Words in String**
```java
public String reverseWords(String s) {
    String[] words = s.trim().split("\\s+");
    StringBuilder sb = new StringBuilder();
    
    for (int i = words.length - 1; i >= 0; i--) {
        sb.append(words[i]);
        if (i > 0) sb.append(" ");
    }
    
    return sb.toString();
}
```

#### **String to Integer (atoi)**
```java
public int myAtoi(String s) {
    if (s == null || s.length() == 0) return 0;
    
    int i = 0, sign = 1;
    long result = 0;
    
    // Skip whitespace
    while (i < s.length() && s.charAt(i) == ' ') i++;
    
    // Handle sign
    if (i < s.length() && (s.charAt(i) == '+' || s.charAt(i) == '-')) {
        sign = s.charAt(i) == '-' ? -1 : 1;
        i++;
    }
    
    // Convert digits
    while (i < s.length() && Character.isDigit(s.charAt(i))) {
        result = result * 10 + (s.charAt(i) - '0');
        
        // Check overflow
        if (result * sign > Integer.MAX_VALUE) return Integer.MAX_VALUE;
        if (result * sign < Integer.MIN_VALUE) return Integer.MIN_VALUE;
        
        i++;
    }
    
    return (int) (result * sign);
}
```

#### **Valid Parentheses (String)**
```java
public boolean isValid(String s) {
    Stack<Character> stack = new Stack<>();
    Map<Character, Character> pairs = Map.of(')', '(', '}', '{', ']', '[');
    
    for (char c : s.toCharArray()) {
        if (pairs.containsKey(c)) {
            if (stack.isEmpty() || stack.pop() != pairs.get(c)) {
                return false;
            }
        } else {
            stack.push(c);
        }
    }
    return stack.isEmpty();
}
```

### 5. Array Manipulation

#### **Rotate Array**
```java
public void rotate(int[] nums, int k) {
    k = k % nums.length;
    reverse(nums, 0, nums.length - 1);
    reverse(nums, 0, k - 1);
    reverse(nums, k, nums.length - 1);
}

private void reverse(int[] nums, int start, int end) {
    while (start < end) {
        int temp = nums[start];
        nums[start] = nums[end];
        nums[end] = temp;
        start++;
        end--;
    }
}
```

#### **Product of Array Except Self**
```java
public int[] productExceptSelf(int[] nums) {
    int[] result = new int[nums.length];
    
    // Left products
    result[0] = 1;
    for (int i = 1; i < nums.length; i++) {
        result[i] = result[i - 1] * nums[i - 1];
    }
    
    // Right products
    int rightProduct = 1;
    for (int i = nums.length - 1; i >= 0; i--) {
        result[i] *= rightProduct;
        rightProduct *= nums[i];
    }
    
    return result;
}
```

#### **Find Missing Number**
```java
public int missingNumber(int[] nums) {
    int n = nums.length;
    int expectedSum = n * (n + 1) / 2;
    int actualSum = 0;
    
    for (int num : nums) {
        actualSum += num;
    }
    
    return expectedSum - actualSum;
}
```

### 6. Advanced String Algorithms

#### **KMP Pattern Matching**
```java
public int strStr(String haystack, String needle) {
    if (needle.length() == 0) return 0;
    
    int[] lps = buildLPS(needle);
    int i = 0, j = 0;
    
    while (i < haystack.length()) {
        if (haystack.charAt(i) == needle.charAt(j)) {
            i++;
            j++;
        }
        
        if (j == needle.length()) {
            return i - j;
        } else if (i < haystack.length() && haystack.charAt(i) != needle.charAt(j)) {
            if (j != 0) {
                j = lps[j - 1];
            } else {
                i++;
            }
        }
    }
    return -1;
}

private int[] buildLPS(String pattern) {
    int[] lps = new int[pattern.length()];
    int len = 0, i = 1;
    
    while (i < pattern.length()) {
        if (pattern.charAt(i) == pattern.charAt(len)) {
            len++;
            lps[i] = len;
            i++;
        } else {
            if (len != 0) {
                len = lps[len - 1];
            } else {
                lps[i] = 0;
                i++;
            }
        }
    }
    return lps;
}
```

---

## String vs StringBuilder Performance

### **String Concatenation Comparison**
```java
// ❌ Inefficient - O(n²) time complexity
public String concatenateStrings(String[] strings) {
    String result = "";
    for (String s : strings) {
        result += s; // Creates new string object each time
    }
    return result;
}

// ✅ Efficient - O(n) time complexity
public String concatenateStringsEfficient(String[] strings) {
    StringBuilder sb = new StringBuilder();
    for (String s : strings) {
        sb.append(s);
    }
    return sb.toString();
}
```

---

## Interview Tips & Best Practices

### 1. **Pattern Recognition**
```java
// String/Array problems often involve:
- Two pointers for palindromes, sorted arrays
- Sliding window for substring problems
- Hash maps for frequency counting
- StringBuilder for string manipulation
- Binary search for sorted arrays
```

### 2. **Common Mistakes to Avoid**
```java
// ❌ String concatenation in loops
String result = "";
for (String s : strings) {
    result += s; // O(n²) complexity
}

// ❌ Not handling edge cases
- Empty strings/arrays
- Single element
- All same elements
- Null inputs

// ❌ Index out of bounds
while (i < s.length() && j < t.length()) {
    // Always check bounds in conditions
}
```

### 3. **Optimization Techniques**
```java
// Use appropriate data structures
int[] freq = new int[26]; // For lowercase letters
Set<Character> seen = new HashSet<>(); // For uniqueness
StringBuilder sb = new StringBuilder(); // For string building

// Minimize string operations
char[] chars = s.toCharArray(); // Work with char array
String sorted = new String(chars); // Convert back when needed
```

### 4. **Space-Time Complexity Analysis**

| Operation | String | StringBuilder | Array |
|-----------|--------|---------------|-------|
| Access | O(1) | O(1) | O(1) |
| Append | O(n) | O(1) amortized | N/A |
| Insert | O(n) | O(n) | O(n) |
| Delete | O(n) | O(n) | O(n) |
| Search | O(n) | O(n) | O(n) |

**Common Complexities:**
- Two pointers: O(n) time, O(1) space
- Sliding window: O(n) time, O(k) space
- Hash map solutions: O(n) time, O(n) space

### 5. **Problem-Solving Strategy**
```
1. Identify the pattern (two pointers, sliding window, etc.)
2. Consider edge cases (empty, single element, duplicates)
3. Choose appropriate data structure (array, hash map, set)
4. Optimize for time vs space based on constraints
5. Handle string immutability with StringBuilder when needed
6. Test with multiple examples including edge cases
```

### 6. **Common Problem Categories**

#### **Easy Level**
- Two Sum, Valid Palindrome
- Remove Duplicates, Reverse String
- First Unique Character

#### **Medium Level**
- Longest Substring Without Repeating Characters
- Group Anagrams, Product of Array Except Self
- Rotate Array, String to Integer

#### **Hard Level**
- Minimum Window Substring
- Text Justification
- Regular Expression Matching

### 7. **String vs Array Decision Matrix**

| Use String When | Use Array When | Use StringBuilder When |
|-----------------|----------------|----------------------|
| Immutable text | Numeric data | Building strings |
| Pattern matching | Fixed size data | Multiple concatenations |
| Small datasets | Index-based access | Dynamic text manipulation |
| Read-only operations | Mathematical operations | Parsing and formatting |

### 8. **Code Template**
```java
public returnType stringArrayProblem(String s, int[] nums) {
    // 1. Handle edge cases
    if (s == null || s.length() == 0) return defaultValue;
    
    // 2. Initialize variables
    int left = 0, right = 0;
    Map<Character, Integer> map = new HashMap<>();
    StringBuilder sb = new StringBuilder();
    
    // 3. Main processing logic
    while (right < s.length()) {
        // Expand window/process current character
        char c = s.charAt(right);
        // Update data structures
        
        // Contract window if needed
        while (condition) {
            // Update result
            left++;
        }
        right++;
    }
    
    // 4. Return result
    return result;
}
```

This comprehensive guide covers the most important string and array patterns for coding interviews. Master these patterns and you'll be well-prepared for most string/array interview questions.
