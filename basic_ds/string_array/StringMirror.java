package basic_ds.string_array;

/**
 * given two strings, decide if one is a mirror of the other.
 * Exp: s1="abc", s2="cba", true;
 * s1="acd", s2="adc", false;
 */
public class StringMirror {
    /**
     * Two-Pointer Technique:
     * one pointer form the starting of s1; the other pointer from the end of s2.
     * A straightforward way: loop s1 from 0->length-1; loop s2 from length-1->0, and
     * compare each of the character to make sure they are the same
     */
    public static boolean isMirror1(String s1, String s2) {
        // base case
        if (s1 == null || s2 == null || s1.length() != s2.length()) {
            return false;
        }
        int length = s1.length(); // the two string have the same length
        char[] chars1 = s1.toCharArray();
        char[] chars2 = s2.toCharArray();

        for (int i = 0, j = length - 1; i < length; i++, j--) {
            if (chars1[i] != chars2[j]) {
                return false;
            }
        }

        return true;
    }

    /**
     * reverse a string.
     * reverse s2, if equals to s1 return true; otherwise return false;
     */
    public static boolean isMirror2(String s1, String s2) {
        // edge case
        if (s1 == null || s2 == null || s1.length() != s2.length()) {
            return false;
        }
        // reverse s2
        char[] char2 = s2.toCharArray();
        int left = 0, right = char2.length - 1;
        char temp;
        while (left < right) {
            temp = char2[left];
            char2[left] = char2[right];
            char2[right] = temp;
            left++;
            right--;
        }
//        System.out.println("Reversed: "+String.valueOf(char2));
        return s1.equals(String.valueOf(char2));
    }

    public static void main(String[] args) {
        System.out.println(isMirror1("ac", "ca") == true ? "pass" : "fail");
        System.out.println(isMirror1("abc", "cbad") == false ? "pass" : "fail");

        System.out.println(isMirror2("ac", "ca") == true ? "pass" : "fail");
        System.out.println(isMirror2("abc", "cbad") == false ? "pass" : "fail");
    }
}
