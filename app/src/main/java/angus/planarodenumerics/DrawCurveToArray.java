package angus.planarodenumerics;

import android.graphics.Color;
import java.util.Random;

public class DrawCurveToArray implements Runnable {

    private String x_dot;
    private String y_dot;
    private double x;
    private double y;
    private double x_min;
    private double x_max;
    private double y_min;
    private double y_max;
    private double dt;
    private double pixel_length;
    private int col;
    private Plot plt;

    DrawCurveToArray (String dxdt, String dydt, double x_val, double y_val,
                      double initial_dt, double pix_len, double xmn, double xmx, double ymn,
                      double ymx, int color, Plot plot) {
        x_dot = dxdt;
        y_dot = dydt;
        x = x_val;
        y = y_val;
        x_min = xmn;
        x_max = xmx;
        y_min = ymn;
        y_max = ymx;
        dt = initial_dt;
        pixel_length = pix_len;
        col = color;
        plt = plot;
    }

    public void run() {
        draw_soln();
    }

    public void draw_soln() {
        // Draw the solution
        // TODO: make these variables accessible to the user
        // TODO: improve speed and end conditions...
        //double T = 8; // An alternative to step_count
        int max_step_count = plt.max_steps;
        int step_count;
        boolean forwards = true;
        boolean backwards = true;
        double[] X;
        double x_length;
        double y_length;
        //double t;
        /*
         * The following booleans and all their occurrences deal with 'very' non-linear situations
         * Basically they let us say dt is too big, but when we halve it it's too small, so just
         * use it anyway (or the same statement with dt too small but 2*dt too big).
         */
        boolean increased_dt = false;
        boolean decreased_dt = false;
        //t = 0;
        step_count = 0;
        // Sometimes jumps away from equilibria, so...
        // TODO: Need to set a max_dt = pixel_length / [speed 3 px away from eq.] ish
        while (step_count < max_step_count) {
            X = RK4_step(x, y, dt);
            x_length = Math.abs(X[0] - x);
            y_length = Math.abs(X[1] - y);
            if (increased_dt && decreased_dt) {
                /*
                 * This deals with the situation where the algorithm can't decide on an
                 * appropriate dt interval (the aforementioned 'very non-linear' cases).
                 * The solution used is to just run with whatever you've got. This should
                 * be improved in later versions
                 */
                plt.draw_line(x, y, X[0], X[1], col, false);
                //t += dt;
                step_count += 1;
                x = X[0];
                y = X[1];
                increased_dt = false;
                decreased_dt = false;
            } else if (x_length < pixel_length && y_length < pixel_length) {
                // This section increases dt if the line to be plotted would be too short
                if (Math.abs(dt) >= 0.5) {
                    // This condition stops calculations if we're reaching an equilibrium
                    break;
                } else {
                    dt *= 2;
                    increased_dt = true;
                }
            } else if (x_length > 4 * pixel_length || y_length > 4 * pixel_length) {
                // This section decreases dt if the line to be plotted would be too long
                dt /= 2;
                decreased_dt = true;
            } else if (X[0] > x_max || X[0] < x_min || X[1] > y_max || X[1] < y_min) {
                // This condition stops calculations if the line would leave the plot domain
                break;
            } else {
                // This section draws the line if everything's going to plan
                plt.draw_line(x, y, X[0], X[1], col, false);
                //t += dt;
                step_count += 1;
                x = X[0];
                y = X[1];
                increased_dt = false;
                decreased_dt = false;
            }
        }
    }


    public double fx(double x, double y) {
        double dx = Eval.eval(x_dot, x, y);
        return dx;
    }

    public double fy(double x, double y) {
        double dy = Eval.eval(y_dot, x, y);
        return dy;
    }

    public double[] RK4_step(double x, double y, double dt) {
        // TODO: See if there's a nice maths library that makes this neater
        // TODO: At least stop doubling up on the calculations
        double ix1 = dt * fx(x, y) / 2;
        double iy1 = dt * fy(x, y) / 2;
        double ix2 = dt * fx(x + ix1, y + iy1);
        double iy2 = dt * fy(x + ix1, y + iy1);
        double ix3 = dt * fx(x + ix2/2, y + iy2/2);
        double iy3 = dt * fy(x + ix2/2, y + iy2/2);
        double ix4 = dt * fx(x + ix3, y + iy3) / 2;
        double iy4 = dt * fy(x + ix3, y + iy3) / 2;
        x += (ix1 + ix2 + ix3 + ix4) / 3;
        y += (iy1 + iy2 + iy3 + iy4) / 3;
        return new double[]{x, y};
    }
}
