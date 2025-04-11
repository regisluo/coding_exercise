package practice.tixi;

public class TX003_FindTheRightmostOne {
    /**
     * for a given int number for instance, 00010010110, return a
     * string with the rightmost 1 only,    00000000010
     */
    public static String solution(int i) {
        // 00010010110   a
        // 11101101001  ~a
        // 11101101010  ~a+1
        // 00000000010  a&(~a+1)
        int a = i & (~i + 1);
        String s = "";
        // print the integer
        StringBuilder sb = new StringBuilder();
        while (a > 0) {
            sb.insert(0, a % 2); // prepend the remainder (0, 1)
            a /= 2; // right-shift i which means the last binary bit is removed
        }
        s = sb.toString();
        String s2 = Integer.toBinaryString(a);

        return String.format("%10s", s).replace(' ', '0');
    }

    public static void main(String[] args) {
        int i = 1778;
        System.out.println(solution(i));

        int num = 2; // Example number
        String binaryString = String.format("%10s", Integer.toBinaryString(num)).replace(' ', '0');
        System.out.println(binaryString); // Output: 0000000010
    }
}
