package basic_ds.sort;

import java.util.Arrays;

/**
 * non-rec is a revers way of rec
 */
public class MergeSortNonRec {

    public void mergeSortNonRec(int[] arr) {
        // edge case
        if(arr == null || arr.length <2 ){
            return;
        }
        int n = arr.length;
        for (int step=1;step<n; step *=2) {
            for (int l = 0;l<n;l += step *2) {
                int m = Math.min(l+step-1, n-1);
                int r=Math.min(l+2*step-1,n-1);
                merge(arr,l,m,r);
            }
        }
    }

    private void merge(int[] arr,int l,int m,int r){
        int[] help = new int[r-l+1];
        int i=0; // index for help array
        int p1= l;
        int p2=m+1;
        while(p1<=m && p2<=r){
            help[i++] = arr[p1]<arr[p2]?arr[p1++]:arr[p2++];
        }
        while(p1<=m){
            help[i++] = arr[p1++];
        }
        while(p2<=r){
            help[i++] = arr[p2++];
        }

        // copy help back into arr
        for(int j=0;j<help.length;j++){
            arr[l+j] = help[j];
        }
    }

    public static void main(String[] args) {
        int[] array = {38, 27, 43, 3, 9, 82, 10};
        System.out.println("Original Array: " + Arrays.toString(array));
        MergeSortNonRec m = new MergeSortNonRec();
        m.mergeSortNonRec(array);

        System.out.println("Sorted Array (Non-Recursive): " + Arrays.toString(array));
    }

}
