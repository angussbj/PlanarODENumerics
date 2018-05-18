package angus.planarodenumerics;

import android.app.Activity;
import android.graphics.Color;
import android.os.Message;
import android.view.View;
import android.widget.Button;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

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
    private String[] parameterSymbols;
    private double[] parameterValues;
    private double dt_max;
    private boolean steps_not_time;
    private int max_steps;
    private double max_time;
    private boolean solve_outside_plot_area;
    ZoomableImageView plotView;
    Button stopButton;
    private int jag;
    private double minl;
    private double maxl;

    /**
     * This method creates a runnable object that when started draws a solution curve to the pixels
     * array of the plot object. The curve drawn is the unique curve through (x, y) for the ODE
     * given by dxdt and dydt.
     *
     * @param dxdt          The x component of the ODE that the user input
     * @param dydt          The y component of the ODE that the user input
     * @param x_val         The x co-ordinate of the point the curve goes through
     * @param y_val         The y co-ordinate of the point the curve goes through
     * @param initial_dt    An (except for the sign) random choice of a reasonable time-step to use.
     *                      Choosing the sign chooses the direction in which time will step.
     * @param pix_len       The length of a pixel in (x, y) space - to be used to determine when the
     *                      time step is too short. This method assumes all you want is a good
     *                      looking curve...
     * @param xmn           The left edge of the frame of our graph
     * @param xmx           The right edge of the frame of our graph
     * @param ymn           The bottom edge of the frame of our graph
     * @param ymx           The top edge of the frame of our graph
     * @param color         The colour of the curve to be plotted
     * @param plot          The plot containing the pixel list we'll alter to 'draw' the curve
     * @param paramSymbs    The list of symbols used as parameters in dxdt and/or dydt
     * @param paramVals     The list containing the values assigned to those symbols. (Symbols and
     *                      their corresponding values have the same list index.)
     * @param dt_maximum    A length that will
     */
    DrawCurveToArray (String dxdt, String dydt, double x_val, double y_val,
                      double initial_dt, double pix_len, double xmn, double xmx, double ymn,
                      double ymx, int color, Plot plot, String[] paramSymbs, double[] paramVals,
                      double dt_maximum, int mx_steps, boolean outside, ZoomableImageView v,
                      Button stop, int jaggedness) {
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
        parameterSymbols = paramSymbs;
        parameterValues = paramVals;
        dt_max = dt_maximum;
        steps_not_time = true;
        max_steps = mx_steps;
        solve_outside_plot_area = outside;
        plotView = v;
        stopButton = stop;
        jag = jaggedness;
    }

    /**
     * This method creates a runnable object that when started draws a solution curve to the pixels
     * array of the plot object. The curve drawn is the unique curve through (x, y) for the ODE
     * given by dxdt and dydt.
     *
     * @param dxdt          The x component of the ODE that the user input
     * @param dydt          The y component of the ODE that the user input
     * @param x_val         The x co-ordinate of the point the curve goes through
     * @param y_val         The y co-ordinate of the point the curve goes through
     * @param initial_dt    An (except for the sign) random choice of a reasonable time-step to use.
     *                      Choosing the sign chooses the direction in which time will step.
     * @param pix_len       The length of a pixel in (x, y) space - to be used to determine when the
     *                      time step is too short. This method assumes all you want is a good
     *                      looking curve...
     * @param xmn           The left edge of the frame of our graph
     * @param xmx           The right edge of the frame of our graph
     * @param ymn           The bottom edge of the frame of our graph
     * @param ymx           The top edge of the frame of our graph
     * @param color         The colour of the curve to be plotted
     * @param plot          The plot containing the pixel list we'll alter to 'draw' the curve
     * @param paramSymbs    The list of symbols used as parameters in dxdt and/or dydt
     * @param paramVals     The list containing the values assigned to those symbols. (Symbols and
     *                      their corresponding values have the same list index.)
     * @param dt_maximum    A length that will
     */
    DrawCurveToArray (String dxdt, String dydt, double x_val, double y_val,
                      double initial_dt, double pix_len, double xmn, double xmx, double ymn,
                      double ymx, int color, Plot plot, String[] paramSymbs, double[] paramVals,
                      double dt_maximum, double mx_time, boolean outside, ZoomableImageView v,
                      Button stop, int jag) {
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
        parameterSymbols = paramSymbs;
        parameterValues = paramVals;
        dt_max = dt_maximum;
        steps_not_time = false;
        max_time = mx_time;
        solve_outside_plot_area = outside;
        plotView = v;
        stopButton = stop;
    }

    /**
     * This method runs the draw solution method to draw the solution curve that this thread was
     * created to draw. It also does some set up and notifies the Plot object, plt, when it's done.
     */
    public void run() {
        // Set up min and max lengths
        minl = (jag/6) * pixel_length;
        maxl = jag * pixel_length;
        // Do the calculations and update the array in plt
        if (solve_outside_plot_area) {
            if (steps_not_time) {
                draw_soln_steps_outside();
            } else {
                draw_soln_time_outside();
            }
        } else {
            if (steps_not_time) {
                draw_soln_steps();
            } else {
                draw_soln_time();
            }
        }
        // Make the view draw the updated image
        plotView.redraw_plot_area();
        // Let the plot object know we're done, so it can start another solution thread if it wants
        int running = plt.solutionThreadsRunning.decrementAndGet();
        // Hide the stop button, if we were the last thread running TODO: Check - might not be so threadsafe
        if (running == 0) {
            Message msg = Message.obtain();
            msg.what = plt.THREAD_DONE;
            msg.setTarget(plt.handler);
            msg.sendToTarget();
        }
    }

    /**
     * Draws the solution the thread was created to draw using the Runge-Kutta 4th order method
     * using the RK4_step method below.
     *
     * Note: the direction in time the solution is plotted for is decided by the sign of dt.
     *
     * The following four methods do essentially the same thing but separate the cases where we
     * stop after we reach the maximum number of steps from the cases where we stop after we reach
     * the maximum in-simulation time; and separate the cases where we stop calculations as soon
     * as we reach the edge of the plot area from the cases where we keep calculating in case we
     * come back into the plot area.
     */
    public void draw_soln_steps() {
        if (Eval.eval(x_dot, x, y, parameterSymbols, parameterValues) == 0
                && Eval.eval(y_dot, x, y, parameterSymbols, parameterValues) == 0) {
            return;
        }
        // Draw the solution
        // TODO: improve speed and end conditions...
        int max_step_count = max_steps;
        int step_count;
        double[] X;
        double x_length;
        double y_length;
        boolean careful = false;
        /*
         * The following booleans and all their occurrences deal with 'very' non-linear situations
         * Basically they let us say dt is too big, but when we halve it it's too small, so just
         * use it anyway (or the same statement with dt too small but 2*dt too big).
         */
        boolean increased_dt = false;
        boolean decreased_dt = false;
        step_count = 0;
        while (step_count < max_step_count && plt.keep_calculating) {

            if (careful) {
                X = RK4_step_careful(x, y, dt);
            } else {
                X = RK4_step(x, y, dt);
            }
            if (X[0] == Double.POSITIVE_INFINITY || X[1] == Double.POSITIVE_INFINITY
                    || X[0] != X[0] || X[1] != X[1]) {
                // If you get NaN or infinity, start being careful
                careful = true;
                X = RK4_step_careful(x, y, dt);
            }

            x_length = Math.abs(X[0] - x);
            y_length = Math.abs(X[1] - y);
            if (increased_dt && decreased_dt) {
                /*
                 * This deals with the situation where the algorithm can't decide on an
                 * appropriate dt interval (the aforementioned 'very non-linear' cases).
                 * The solution used is to just run with whatever you've got. This should
                 * be improved in later versions
                 */
                plt.draw_line(x, y, X[0], X[1], col);
                step_count += 1;
                x = X[0];
                y = X[1];
                increased_dt = false;
                decreased_dt = false;
            } else if (x_length < minl && y_length < minl) {
                // This section increases dt if the line to be plotted would be too short
                dt *= 2;
                increased_dt = true;
                if (Math.abs(dt) >= dt_max) {
                    // This condition stops calculations if we're reaching an equilibrium
                    double[] eq_pt = plt.find_equilibrium(x, y);
                    double d = Math.sqrt(Math.pow((x - eq_pt[0]), 2) + Math.pow(y - eq_pt[1], 2));
                    if (d < maxl && plt.source_sink_or_zero(eq_pt[0], eq_pt[1])) {
                        // If we're pretty close to the equilibrium and it's a source or a sink,
                        // we're done - draw the line to the equilibrium and stop calculating
                        plt.draw_line(x, y, eq_pt[0], eq_pt[1], col);
                        break;
                    }
                }
            } else if (x_length > maxl || y_length > maxl) {
                // This section decreases dt if the line to be plotted would be too long
                dt /= 2;
                decreased_dt = true;
            } else if (x > x_max || x < x_min || y > y_max || y < y_min) {
                // This condition stops calculations if the line would leave the plot domain
                break;
            } else {
                // This section draws the line if everything's going to plan
                plt.draw_line(x, y, X[0], X[1], col);
                step_count += 1;
                x = X[0];
                y = X[1];
                increased_dt = false;
                decreased_dt = false;
            }
        }
    }
    public void draw_soln_steps_outside() {
        if (Eval.eval(x_dot, x, y, parameterSymbols, parameterValues) == 0
                && Eval.eval(y_dot, x, y, parameterSymbols, parameterValues) == 0) {
            return;
        }
        // Draw the solution
        // TODO: improve speed and end conditions...
        int max_step_count = max_steps;
        int step_count;
        double[] X;
        double x_length;
        double y_length;
        boolean careful = false;
        /*
         * The following booleans and all their occurrences deal with 'very' non-linear situations
         * Basically they let us say dt is too big, but when we halve it it's too small, so just
         * use it anyway (or the same statement with dt too small but 2*dt too big).
         */
        boolean increased_dt = false;
        boolean decreased_dt = false;
        step_count = 0;
        while (step_count < max_step_count && plt.keep_calculating) {

            if (careful) {
                X = RK4_step_careful(x, y, dt);
            } else {
                X = RK4_step(x, y, dt);
            }
            if (X[0] == Double.POSITIVE_INFINITY || X[1] == Double.POSITIVE_INFINITY
                    || X[0] != X[0] || X[1] != X[1]) {
                // If you get NaN or infinity, start being careful
                careful = true;
                X = RK4_step_careful(x, y, dt);
            }

            x_length = Math.abs(X[0] - x);
            y_length = Math.abs(X[1] - y);
            if (increased_dt && decreased_dt) {
                /*
                 * This deals with the situation where the algorithm can't decide on an
                 * appropriate dt interval (the aforementioned 'very non-linear' cases).
                 * The solution used is to just run with whatever you've got. This should
                 * be improved in later versions
                 */
                plt.draw_line(x, y, X[0], X[1], col);
                step_count += 1;
                x = X[0];
                y = X[1];
                increased_dt = false;
                decreased_dt = false;
            } else if (x_length < minl && y_length < minl) {
                // This section increases dt if the line to be plotted would be too short
                dt *= 2;
                increased_dt = true;
                if (Math.abs(dt) >= dt_max) {
                    // This condition stops calculations if we're reaching an equilibrium
                    double[] eq_pt = plt.find_equilibrium(x, y);
                    double d = Math.sqrt(Math.pow((x - eq_pt[0]), 2) + Math.pow(y - eq_pt[1], 2));
                    if (d < maxl && plt.source_sink_or_zero(eq_pt[0], eq_pt[1])) {
                        // If we're pretty close to the equilibrium and it's a source or a sink,
                        // we're done - draw the line to the equilibrium and stop calculating
                        plt.draw_line(x, y, eq_pt[0], eq_pt[1], col);
                        break;
                    }
                }
            } else if (x_length > maxl || y_length > maxl) {
                // This section decreases dt if the line to be plotted would be too long
                dt /= 2;
                decreased_dt = true;
            } else {
                // TODO: Check you don't have problems when leaving the plot area
                // This section draws the line if everything's going to plan
                plt.draw_line(x, y, X[0], X[1], col);
                step_count += 1;
                x = X[0];
                y = X[1];
                increased_dt = false;
                decreased_dt = false;
            }
        }
    }
    public void draw_soln_time() {
        if (Eval.eval(x_dot, x, y, parameterSymbols, parameterValues) == 0
                && Eval.eval(y_dot, x, y, parameterSymbols, parameterValues) == 0) {
            return;
        }
        // Draw the solution
        double[] X;
        double x_length;
        double y_length;
        boolean careful = false;
        double t = 0;
        double t_max = max_time;
        /*
         * The following booleans and all their occurrences deal with 'very' non-linear situations
         * Basically they let us say dt is too big, but when we halve it it's too small, so just
         * use it anyway (or the same statement with dt too small but 2*dt too big).
         */
        boolean increased_dt = false;
        boolean decreased_dt = false;
        while (t < t_max && plt.keep_calculating) {

            if (careful) {
                X = RK4_step_careful(x, y, dt);
            } else {
                X = RK4_step(x, y, dt);
            }
            if (X[0] == Double.POSITIVE_INFINITY || X[1] == Double.POSITIVE_INFINITY
                    || X[0] != X[0] || X[1] != X[1]) {
                // If you get NaN or infinity, start being careful
                careful = true;
                X = RK4_step_careful(x, y, dt);
            }

            x_length = Math.abs(X[0] - x);
            y_length = Math.abs(X[1] - y);
            if (increased_dt && decreased_dt) {
                /*
                 * This deals with the situation where the algorithm can't decide on an
                 * appropriate dt interval (the aforementioned 'very non-linear' cases).
                 * The solution used is to just run with whatever you've got. This should
                 * be improved in later versions
                 */
                plt.draw_line(x, y, X[0], X[1], col);
                t += Math.abs(dt);                      //TODO: Consider separating cases for speed
                x = X[0];
                y = X[1];
                increased_dt = false;
                decreased_dt = false;
            } else if (x_length < minl && y_length < minl) {
                // This section increases dt if the line to be plotted would be too short
                dt *= 2;
                increased_dt = true;
                if (Math.abs(dt) >= dt_max) {
                    // This condition stops calculations if we're reaching an equilibrium
                    double[] eq_pt = plt.find_equilibrium(x, y);
                    double d = Math.sqrt(Math.pow((x - eq_pt[0]), 2) + Math.pow(y - eq_pt[1], 2));
                    if (d < maxl && plt.source_sink_or_zero(eq_pt[0], eq_pt[1])) {
                        // If we're pretty close to the equilibrium and it's a source or a sink,
                        // we're done - draw the line to the equilibrium and stop calculating
                        plt.draw_line(x, y, eq_pt[0], eq_pt[1], col);
                        break;
                    }
                }
            } else if (x_length > maxl || y_length > maxl) {
                // This section decreases dt if the line to be plotted would be too long
                dt /= 2;
                decreased_dt = true;
            } else if (x > x_max || x < x_min || y > y_max || y < y_min) {
                // This condition stops calculations if the line would leave the plot domain
                break;
            } else {
                // This section draws the line if everything's going to plan
                plt.draw_line(x, y, X[0], X[1], col);
                t += Math.abs(dt);
                x = X[0];
                y = X[1];
                increased_dt = false;
                decreased_dt = false;
            }
        }
    }
    public void draw_soln_time_outside() {
        if (Eval.eval(x_dot, x, y, parameterSymbols, parameterValues) == 0
                && Eval.eval(y_dot, x, y, parameterSymbols, parameterValues) == 0) {
            return;
        }
        // Draw the solution
        double[] X;
        double x_length;
        double y_length;
        boolean careful = false;
        double t = 0;
        double t_max = max_time;
        /*
         * The following booleans and all their occurrences deal with 'very' non-linear situations
         * Basically they let us say dt is too big, but when we halve it it's too small, so just
         * use it anyway (or the same statement with dt too small but 2*dt too big).
         */
        boolean increased_dt = false;
        boolean decreased_dt = false;
        while (t < t_max && plt.keep_calculating) {

            if (careful) {
                X = RK4_step_careful(x, y, dt);
            } else {
                X = RK4_step(x, y, dt);
            }
            if (X[0] == Double.POSITIVE_INFINITY || X[1] == Double.POSITIVE_INFINITY
                    || X[0] != X[0] || X[1] != X[1]) {
                // If you get NaN or infinity, start being careful
                careful = true;
                X = RK4_step_careful(x, y, dt);
            }

            x_length = Math.abs(X[0] - x);
            y_length = Math.abs(X[1] - y);
            if (increased_dt && decreased_dt) {
                /*
                 * This deals with the situation where the algorithm can't decide on an
                 * appropriate dt interval (the aforementioned 'very non-linear' cases).
                 * The solution used is to just run with whatever you've got. This should
                 * be improved in later versions
                 */
                plt.draw_line(x, y, X[0], X[1], col);
                t += Math.abs(dt);                      //TODO: Consider separating cases for speed
                x = X[0];
                y = X[1];
                increased_dt = false;
                decreased_dt = false;
            } else if (x_length < minl && y_length < minl) {
                // This section increases dt if the line to be plotted would be too short
                dt *= 2;
                increased_dt = true;
                if (Math.abs(dt) >= dt_max) {
                    // This condition stops calculations if we're reaching an equilibrium
                    double[] eq_pt = plt.find_equilibrium(x, y);
                    double d = Math.sqrt(Math.pow((x - eq_pt[0]), 2) + Math.pow(y - eq_pt[1], 2));
                    if (d < maxl && plt.source_sink_or_zero(eq_pt[0], eq_pt[1])) {
                        // If we're pretty close to the equilibrium and it's a source or a sink,
                        // we're done - draw the line to the equilibrium and stop calculating
                        plt.draw_line(x, y, eq_pt[0], eq_pt[1], col);
                        break;
                    }
                }
            } else if (x_length > maxl || y_length > maxl) {
                // This section decreases dt if the line to be plotted would be too long
                dt /= 2;
                decreased_dt = true;
            } else {
                // This section draws the line if everything's going to plan
                plt.draw_line(x, y, X[0], X[1], col);
                t += Math.abs(dt);
                x = X[0];
                y = X[1];
                increased_dt = false;
                decreased_dt = false;
            }
        }
    }

    /**
     * This method evaluates x_dot (the string input from the user - the x component of the ODE) at
     * the point (x, y)
     */
    public double fx(double x, double y) {
        double dx = Eval.eval(x_dot, x, y, parameterSymbols, parameterValues);
        return dx;
    }

    /**
     * This method evaluates y_dot (the string input from the user - the y component of the ODE) at
     * the point (x, y)
     */
    public double fy(double x, double y) {
        double dy = Eval.eval(y_dot, x, y, parameterSymbols, parameterValues);
        return dy;
    }

    /**
     * This method uses the Runge-Kutta 4th order method to numerically estimate the x and y values
     * dt after the x and y input values. It avoids returning infinite or NaN values by taking
     * unrealistically short steps because it set any components that would be inf or nan to 0.
     *
     * TODO: Check you don't have problems if everything comes back nan, so you don't go anywhere
     *
     * @param x     the x coordinate of the point before the step
     * @param y     the y coordinate of the point before the step
     * @param dt    the length of the step in terms of time
     * @return      a list containing the x and y coordinates of the point after the step.
     */
    public double[] RK4_step_careful(double x, double y, double dt) {
        double ix1 = dt * fx(x, y) / 2;
        if (Math.abs(ix1) == Double.POSITIVE_INFINITY || ix1 != ix1) { ix1 = 0; }
        double iy1 = dt * fy(x, y) / 2;
        if (Math.abs(iy1) == Double.POSITIVE_INFINITY || iy1 != iy1) { iy1 = 0; }
        double ix2 = dt * fx(x + ix1, y + iy1);
        if (Math.abs(ix2) == Double.POSITIVE_INFINITY || ix2 != ix2) { ix2 = 0; }
        double iy2 = dt * fy(x + ix1, y + iy1);
        if (Math.abs(iy2) == Double.POSITIVE_INFINITY || iy2 != iy2) { iy2 = 0; }
        double ix3 = dt * fx(x + ix2/2, y + iy2/2);
        if (Math.abs(ix3) == Double.POSITIVE_INFINITY || ix3 != ix3) { ix3 = 0; }
        double iy3 = dt * fy(x + ix2/2, y + iy2/2);
        if (Math.abs(iy3) == Double.POSITIVE_INFINITY || iy3 != iy3) { iy3 = 0; }
        double ix4 = dt * fx(x + ix3, y + iy3) / 2;
        if (Math.abs(ix4) == Double.POSITIVE_INFINITY || ix4 != ix4) { ix4 = 0; }
        double iy4 = dt * fy(x + ix3, y + iy3) / 2;
        if (Math.abs(iy4) == Double.POSITIVE_INFINITY || iy4 != iy4) { iy4 = 0; }
        x += (ix1 + ix2 + ix3 + ix4) / 3;
        y += (iy1 + iy2 + iy3 + iy4) / 3;
        return new double[]{x, y};
    }

    /**
     * This method uses the Runge-Kutta 4th order method to numerically estimate the x and y values
     * dt after the x and y input values.
     *
     * @param x     the x coordinate of the point before the step
     * @param y     the y coordinate of the point before the step
     * @param dt    the length of the step in terms of time
     * @return      a list containing the x and y coordinates of the point after the step.
     */
    public double[] RK4_step(double x, double y, double dt) {
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
