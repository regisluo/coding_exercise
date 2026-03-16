package practice.leetcode.seventyfive75.arraystring;

public class String_Common_Divisor_1071 {
    /**
     * Find the greatest common divisor of two strings.
     * If there is no such string, return an empty string "".
     * the idea is to find the gcd of the lengths of the two strings,
     * then check if the substring of that length can divide both strings.
     */
    public String gcdOfStrings(String str1, String str2) {
        // edge cases
        if (str1 == null || str2 == null) {
            return "";
        }
        if (str1.length() == 0 || str2.length() == 0) {
            return "";
        }

        // get the length of the two strings
        int l1 = str1.length();
        int l2 = str2.length();

        // find the gcd of the two lengths
        int gcdLen = gcd(l1, l2);

        // get the candidate substring
        String candidate = str1.substring(0, gcdLen);

        // check if candidate can divide both strings
        if (canDivide(str1, candidate) && canDivide(str2, candidate)) {
            return candidate;
        } else {
            return "";
        }

    }

    /**
     * find the gcd of two integers.
     * gcd means the greatest common divisor.
     * use the Euclidean algorithm.
     * the idea is that gcd(a, b) = gcd(b, a % b)
     * until b == 0, then gcd(a, 0) = a.
     */
    private int gcd(int a, int b) {
        if (b == 0) {
            return a;
        }
        return gcd(b, a % b);
    }

    // canDivide
    private boolean canDivide(String str, String candidate) {
        int strLen = str.length();
        int candLen = candidate.length();
        if (strLen % candLen != 0) {
            return false;
        }
        int times = strLen / candLen;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++) {
            sb.append(candidate);
        }
        return sb.toString().equals(str);
    }
}
