package practice.tixi;

public class TX000_BinarySearchRelated {
    public static int binarySearch(int[] arr, int target) {
        if (arr==null || arr.length==0)
            return -1;
        else
            return findIn(arr, 0, arr.length-1, target);
    }

    private static int findIn(int[] arr, int L, int R, int target) {
        while(L<=R) {
            int M = L+((R-L)>>1);
            if (arr[M] == target){
                return M;
            } else if (arr[M]>target) {
                R = M-1;
            } else {
                L = M+1;
            }
        }
        return -1;
    }

    /**
     * find the most left position which is greater than N in array
     */
    public static int findLeftMostPosition(int[] arr, int target) {
        int L=0;
        int R = arr.length-1;
        int ans = -1;
        while (L<=R) {
            int M = L+((R-L)>>1);
            if (arr[M] == target){
                ans = M;
                R = M-1;
            } else if (arr[M]>target) {
                R = M-1;
            } else {
                L = M+1;
            }
        }
        return ans;
    }

    /**
     * find the rightmost index which value less than the target
     */
    public static int findRightMostIndex(int[] arr, int target) {
        int l=0, r=arr.length-1;
        int ans = -1;
        while (l<=r) {
            int m = l+((r-l)>>1);
            if (arr[m] == target) {
                ans = m;
                l=m+1;
            } else if (arr[m]>target) {
                l = m+1;
            } else {
                r=m-1;
            }
        }
        return ans;
    }

    public static void main(String[] args) {
        int[] sortedArray = {2, 2, 2 , 4, 5, 7};
        int target = 2;
//        int index = findLeftMostPosition(sortedArray, target);

//        if (index != -1) {
//            System.out.println("Leftmost index of " + target + " is: " + index);
//        } else {
//            System.out.println("Element not found");
//        }

        int index2 = findRightMostIndex(sortedArray, target);
        if (index2 != -1) {
            System.out.println("Rightmost index of " + target + " is: " + index2);
        } else {
            System.out.println("Element not found");
        }
    }
}
