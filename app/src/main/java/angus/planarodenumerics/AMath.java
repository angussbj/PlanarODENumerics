package angus.planarodenumerics;

public final class AMath {
    public static double log2(double x){
        // TODO: think about clever bitshift implementations and stuff
        return Math.log(x) / Math.log(2);
    }
}
