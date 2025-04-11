package practice.tixi;

public class TX001_SwapTwoNumberNoExtraSpace {
    public static void swap(int a, int b) {
        System.out.println(String.format("Before swapping: a is %d, b is %d", a, b));
        a = a^b;
        b = a^b;
        a = a^b;
        System.out.println(String.format("After swapping: a is %d, b is %d", a, b));
    }

    public static void main(String[] args) {
        int a = 2, b=3;
        swap(a,b);
    }
}
