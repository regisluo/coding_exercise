package practice.tixi;

public class TX002_FindNumberOddTimes {
    public static int solution(int[] arr) {
        int e = 0;
        for (int i:arr) {
            e = e^i;
        }
        return e;
    }

    public static void main(String[] args) {
        int[] arr = {1,1,2,2,3,3,4};
        System.out.println(solution(arr));
    }
}
