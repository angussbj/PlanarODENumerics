package angus.planarodenumerics;

import java.util.Arrays;

public final class AMath {
    /**
     * The base 2 logarithm function (slightly shitily implemented at the moment)
     *
     * @param x     The number we want the logarithm of
     * @return      The base 2 logarithm of that number
     */
    public static double log2(double x){
        // TODO: think about clever bitshift implementations and stuff
        return Math.log(x) / Math.log(2);
    }

    /**
     * The sine of an angle n measured in degrees.
     *
     * @param n     angle in degrees
     * @return      sine of that angle
     */
    public static double sin(double n) {
        return Math.sin(n * Math.PI / 180);
    }

    /**
     * The cosine of an angle n measured in degrees.
     *
     * @param n     angle in degrees
     * @return      cosine of that angle
     */
    public static double cos(double n) {
        return Math.cos(n * Math.PI / 180);
    }

    /**
     * This method gets the (approcimate) bottom decile of a list of doubles. It should only be used if the array
     * is not ordered and the current order of the array will not be needed. THIS METHOD SORTS
     * ARRAYS as well as giving the median.
     *
     * @param xs    The list of values that we find the bottom decile of
     * @return      The value for the (approximate) bottom decile
     */
    public static double bottom_percentile(double[] xs) {
        Arrays.sort(xs);
        return xs[xs.length/100];
    }
}
