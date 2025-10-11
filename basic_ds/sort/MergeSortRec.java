package basic_ds.sort;

public class MergeSortRec {
    public void mergeSort(int[] arr){
        if(arr == null || arr.length < 2){
            return;
        }
        process(arr,0,arr.length-1);
    }
    // recursion
    private void process(int[] arr, int L, int R){
        // base case: arr length is 1
        if(L == R) {
            return;
        }
        int mid = L + ((R - L) >> 1);
        process(arr,L,mid);
        process(arr, mid+1, R);
        merge(arr, L, mid, R);
    }

    public static void merge(int[] arr, int L, int mid, int R) {
        int[] help = new int[R - L + 1];
        int i = 0;
        int p1 = L;
        int p2 = mid + 1;
        while (p1 <= mid && p2 <= R) {
            help[i++] = arr[p1] <= arr[p2] ? arr[p1++] : arr[p2++];
        }
        while (p1 <= mid) {
            help[i++] = arr[p1++];
        }
        while (p2 <= R) {
            help[i++] = arr[p2++];
        }
        for (i = 0; i < help.length; i++) {
            arr[L + i] = help[i];
        }
    }

    // moving from a recursive version to an iterative one,
    // we have to think in the opposite way of the recursion
    public static void mergeSortNonRec(int[] arr) {
        if (arr == null || arr.length < 2) {
            return;
        }
        int n = arr.length;
        for (int step = 1; step < n; step *= 2) {
            for (int left = 0; left < n; left += 2 * step) {
                int mid = Math.min(left + step - 1, n-1);
                int right = Math.min(left + 2 * step - 1, n - 1);
                merge(arr, left, mid, right);
            }
        }
    }
}
