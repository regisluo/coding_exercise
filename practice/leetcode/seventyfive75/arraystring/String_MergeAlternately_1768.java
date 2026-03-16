package practice.leetcode.seventyfive75.arraystring;

public class String_MergeAlternately_1768 {
    public String mergeAlternately(String word1, String word2) {
        // edge cases
        if (word1 == null) {
            if (word2 == null) {
                return null;
            } else {
                return word2;
            }
        } else {
            if (word2 == null) {
                return word1;
            }
        }

        // get the length
        int l1 = word1.length();
        int l2 = word2.length();

        int min = l1 <= l2 ? l1 : l2;
        StringBuilder sb = new StringBuilder();
        char[] ch1 = word1.toCharArray();
        char[] ch2 = word2.toCharArray();

        for (int i = 0; i < min; i++) {
            sb.append(ch1[i]).append(ch2[i]);
        }

        if (l1 == l2) {
            return sb.toString();
        } else {
            if (l1 > l2) {
                sb.append(word1.substring(min, l1));
            } else {
                sb.append(word2.substring(min, l2));
            }
            return sb.toString();
        }


    }
}
