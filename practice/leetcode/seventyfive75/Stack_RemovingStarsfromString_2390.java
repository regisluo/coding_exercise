package practice.leetcode.seventyfive75;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Stack;

/**
 * You are given a string s, which contains stars *.
 *
 * In one operation, you can:
 *
 * Choose a star in s.
 * Remove the closest non-star character to its left, as well as remove the star itself.
 * Return the string after all stars have been removed.
 */

public class Stack_RemovingStarsfromString_2390 {
    public String removeStars(String s) {
        StringBuilder sb= new StringBuilder();
        for(var i:s.toCharArray())
            if(i=='*') sb.deleteCharAt(sb.length() - 1);
            else sb.append(i);
        return sb.toString();
    }

    public static void main(String[] args) {
        Stack_RemovingStarsfromString_2390 obj = new Stack_RemovingStarsfromString_2390();
        String s = "leet**cod*e";
        System.out.println(obj.removeStars(s));
    }
}
