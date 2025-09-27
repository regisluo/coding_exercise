package practice.leet.arranAndString;

public class MergeSortedArray_88 {
    public void merge(int[] nums1, int m, int[] nums2, int n) {
        // edge cases
        if (nums1 == null || nums2 == null || n == 0) {
            return;
        }
        int i = m - 1; // i starting from last element of nums1
        int j = n - 1; // j starting from last element of nums2
        int k = m + n - 1; // k starting from the end of nums1
        while (i >= 0 && j >= 0) {
            // populate array nums1 from end to start
            nums1[k--] = nums1[i] > nums2[j] ? nums1[i--] : nums2[j--];
        }
        while (j >= 0) {
            nums1[k--] = nums2[j--];
        }
    }

    public void printArray(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
    }
}
