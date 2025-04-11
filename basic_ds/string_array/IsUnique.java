package basic_ds.string_array;

public class IsUnique {
    public static void main(String[] args) {
        System.out.println(isUnique("abc12"));
        System.out.println(isUnique("abca"));
    }

    public static boolean isUnique(String str) {
        char[] sc = str.toCharArray();
        boolean[] chars = new boolean[128];
        for (char c:sc) {
            if (chars[c]) {
                return false;
            } else {
                chars[c] = true;
            }
        }
        return true;
    }
}