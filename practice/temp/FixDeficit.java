package practice.temp;

import java.util.*;

public class FixDeficit {
    private int minMove1(int[] A) {
        int minMove = 0;
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        long runningPrefix = 0;

        for (int a : A) {
            if (a < 0) {
                minHeap.offer(a); // put negative into the heap
            }
            runningPrefix += a;
            if (runningPrefix < 0) {// fix the deficit on the way
                int worst = minHeap.poll();
                runningPrefix -= worst;
                minMove++;
            }

        }

        return minMove;
    }

    public int minMove2(int[] A) {
        int minMove = 0;
        // firstly save all negatives
        List<Integer> negatives = new ArrayList<>();
        long minPrefix = 0; // record the min prefix
        long runningPrefix = 0;

        for (int a : A) {
            negatives.add(a);
            runningPrefix += a;
            minPrefix = Math.min(runningPrefix, Long.MAX_VALUE);
        }
        if (minPrefix >= 0)
            return minMove;// there is no need to move
        // sort the negatives decending
        negatives.sort((a, b) -> Long.compare(Math.abs(b), Math.abs(a)));
        long deficit = -minPrefix;
        for (int neg : negatives) {
            deficit += neg;
            minMove++;
            if (deficit >= 0)
                break;
        }

        return minMove;
    }
}
