package practice.tixi;

import java.util.Arrays;

/**
 * Merge two sorted arrays
 * 1. recursive:
 *
 * 2. non-recursive:
 *
 */
public class TX006_MergeSort {
    public void mergeSortRec(int[] arr) {
        if (arr==null || arr.length==1)
            return;
        process(arr, 0, arr.length-1);
    }

    /**
     * Sort the arr given index from L to R
     */
    public void process(int arr[], int L, int R){
        if(L==R) return;
        int M = L+((R-L)/2); // find the middle
        process(arr, L, M); // sort the left part
        process(arr, M+1, R); // sort the right part
        merge(arr, L, M, R); // merge the left and right
    }
    // merge two sorted array: L->M, and M+1->R
    public static void merge(int arr[], int L, int M, int R) {
        int[] help = new int[R-L+1];
        int i=L, j=M+1;
        int h=0; // index for help array

        while(i<=M && j<=R) {
            help[h++] = arr[i]<arr[j]?arr[i++]:arr[j++];
        }

        // either i or j over the scope
        // copy the reminding elements, either in left or right part into help array
        if (i<=M) {
            help[h++] = arr[i++];
        }
        if (j<=R) {
            help[h++] = arr[j++];
        }
        // copy elements in help back to arr
        for(int k=0;k<help.length;k++) {
            arr[L+k] = help[k];
        }

    }

    // nonRec still reuse the same merge function; thus
    // the key point here is to find correct L, M and R
    public static void mergeSortNonRec(int[] arr){
        // base case
        if (arr==null || arr.length==1)
            return;
        int n = arr.length;

        // a step is defined from 1 to n;
        for(int step=1; step<n; step*=2){
            for(int left=0;left<n;left+=2*step) {
                int M = Math.min(left+step-1, n-1);
                int R = Math.min(left+2*step-1, n-1);
                merge(arr, left, M, R);
            }
        }
    }

    public static void main(String[] args) {
        int[] array = {38, 27, 43, 3, 9, 82, 10};
        System.out.println("Original Array: " + Arrays.toString(array));

        mergeSortNonRec(array);

        System.out.println("Sorted Array (Non-Recursive): " + Arrays.toString(array));
    }
}
