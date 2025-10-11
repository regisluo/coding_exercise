package basic_ds.search;

/**
 * Binary Search implementation on a sorted array.
 * normally, the array is sorted in ascending order, but it doesn't have to be able to
 * use binary search. One example use BS on a non-sorted array is to find the
 * local minimum value in an unsorted array.
 */
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

    // find the local minimum value in an unsorted array
    public int findLocalMin(int[] arr) {
        if (arr == null || arr.length == 0) return -1;
        int n = arr.length;
        if (n == 1 || arr[0] < arr[1]) return 0;
        if (arr[n - 1] < arr[n - 2]) return n - 1;
        // binary search in the rest of the array
        int left = 1, right = n - 2;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (arr[mid] > arr[mid - 1]) {
                right = mid - 1;
            } else if (arr[mid] > arr[mid + 1]) {
                left = mid + 1;
            } else {
                return mid; // local minimum found
            }
        }
        return left; // or right, both are the same here
    }
}
