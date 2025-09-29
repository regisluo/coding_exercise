package basic_ds.search;

public class BinarySearchBase {
    // return the index of target in arr if found, otherwise return -1
    public int binarySearch(int[] arr, int target) {
        if (arr == null || arr.length == 0) return -1;

        int left = 0, right = arr.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2; // to avoid overflow

            if (arr[mid] == target) {
                return mid; // target found
            } else if (arr[mid] < target) {
                left = mid + 1; // search in the right half
            } else {
                right = mid - 1; // search in the left half
            }
        }

        return -1; // target not found
    }

    private int getRandom(int n) {
        return (int)(Math.random() * n);
    }
}
