package practice.leetcode.seventyfive75;

public class RemoveElement_27 {
    public int removeElement(int[] nums, int val) {
        int i=0, j=nums.length-1;
        // move all val to the 'end' of the array by switching elements
        while (i<=j) {
            // find the element whose value is val
            while (nums[i] != val) i++;
            // find the element whose value is not val
            while (nums[j] == val) j--;

            // swap the values
            int tmp = nums[i];
            nums[i] = nums[j];
            nums[j] = tmp;
        }

        int ret = i<j?i:j;



        return ret;
    }

    // sort the num array from index L to R
    private void mergeSort(int[] num, int L, int R) {

    }
}
