package angus.planarodenumerics;

import java.util.Arrays;

public final class AMath {
    public static double log2(double x){
        // TODO: think about clever bitshift implementations and stuff
        return Math.log(x) / Math.log(2);
    }

    // This method is not currently used.
    /**
     * This method gets the median of a list of doubles. It should only be used if the array is not
     * ordered and the current order of the array will not be needed. THIS METHOD SORTS ARRAYS as
     * well as giving the median.
     */
    public static double median_unordered_to_ordered(double[] xs) {
        Arrays.sort(xs);
        if (xs.length % 2 == 0) {
            return (xs[xs.length/2 - 1] + xs[xs.length/2]) / 2;
        } else {
            return xs[xs.length/2];
        }
    }
}
