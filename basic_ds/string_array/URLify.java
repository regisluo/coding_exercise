package basic_ds.string_array;
/**
 * Write a method to replace all spaces in a string with '%20  You may assume that
 * the string has sufficient space at the end to hold the additional characters, and
 * that you are given the "true" length of the string. (Note: if implementing in Java,
 * please use a character array so that you can perform this operation in place.)
 * EXAMPLE
 * Input: "Mr John Smith     ", 13; Note that there are many extra spaces from the end
 * of the string, and the 13 is the 'true' length other than the string's length
 * Output: "Mr%20John%20Smith"
 *
 * 这个题目的重点就是：
 * 1. 将space替换成%20以后，新string变长了，因此新string的index变化了
 * 2. 这个题目算法的核心就是计算出新string的长度，然后将旧string的一个一个的字符‘拷贝’到新的index。
 *    在拷贝的过程中，如果是space，就用%20代替
 * 3. 这个题目还一个重点就是, 没有使用额外的空间, 就在原数组上完成拷贝, 即前面的字符拷贝到后面的位置
 */
public class URLify {
    public static String REP = "%20";

    public static String urlify(char[] chars, int trueLength){
        if (chars == null || chars.length == 0) {
            return null;
        }
        // the true length is used to count the SPACES inside the string, other
        // than EXTRA spaces after the string
        int spaceCount = 0;
        for(int i=0;i<trueLength;i++) {
            if (chars[i] == ' ') {
                spaceCount ++;
            }
        }

        int newLength = trueLength + 2*spaceCount;
        int index = newLength-1;
        for(int i=trueLength-1;i>-1;i--) {
            if (chars[i]!=' ') {
                chars[index--] = chars[i];
            } else {
                chars[index--] = '0';
                chars[index--] = '2';
                chars[index--] = '%';
            }
        }
        return String.valueOf(chars);
    }

    public static void main(String[] args) {
        String input = "Mr John Smith    "; // Extra spaces to accommodate '%20'
        int trueLength = 13;

        char[] charArray = input.toCharArray();
        String s = urlify(charArray, trueLength);

        System.out.println("Output: " + s);
    }
}
