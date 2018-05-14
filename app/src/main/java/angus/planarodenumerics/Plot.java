package angus.planarodenumerics;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Random;

public class Plot {
    private int x_pixels;
    public int y_pixels;
    private Bitmap bmp;
    private double x_scale;
    private double y_scale;
    double x_min;
    double x_max;
    double y_min;
    double y_max;
    private String x_dot;
    private String y_dot;
    private int[] pixels;
    public int border;
    private double[] shortest_vector_drawn = new double[]{0, 0, Double.POSITIVE_INFINITY};
    private int arrow_size;
    private final Bitmap.Config conf = Bitmap.Config.ARGB_8888;
    String[] parameterSymbols;
    double[] parameterValues;
    double pixel_length;
    double dt_max;

    // TODO: Minimise uses of borders - make the only get plot function the one called by 'onDraw'.

    /**
     * This method creates an instance of the Plot class. Within this object most of the work of
     * turning equations into images is done.
     *
     * @param xmn           The left end of the x-axis
     * @param xmx           The right end of the x-axis
     * @param ymn           The bottom end of the y-axis
     * @param ymx           The top end of the y-axis
     * @param xd            The derivative of x with respect to time: dx/dt, xdot, xd, dxdt are all
     *                      synonyms. This is the first of the two strings the user enters to
     *                      describe the ODE they are interested in.
     * @param yd            The derivative of y with respect to time: dy/dt, ydot, yd, dydt are all
     *                      synonyms. This is the second of the two strings the user enters to
     *                      describe the ODE they are interested in.
     * @param xpx           The width of the desired output in pixels
     * @param ypx           The height of the desired output in pixels
     * @param bdr           The width/height of the borders at the left and bottom that are used to
     *                      draw the axes.
     * @param arr_size      The target side length of the square into which the arrows drawn for the
     *                      vector field must fit. The actual square may be larger so that an
     *                      integer number of squares fit in the plot area. Measured in pixels.
     * @param paramSymbs    The list of strings that are names of parameters the user is using to
     *                      in their ODE equations (xd, yd) or their bounds (xmn, xmx, ymn, ymx).
     * @param paramVals     The list of values (doubles) assigned to the parameters. Corresponding
     *                      pairs of symbols and values have the same string index.
     */
    Plot(double xmn, double xmx, double ymn, double ymx, String xd, String yd, int xpx, int ypx,
         int bdr, int arr_size, String[] paramSymbs, double[] paramVals) {
        border = bdr;
        x_pixels = xpx - border;
        y_pixels = ypx - border;
        bmp = Bitmap.createBitmap(x_pixels + border, y_pixels + border, conf);
        pixels = new int[(x_pixels + border) * (y_pixels + border)];
        make_black();
        x_max = xmx;
        y_max = ymx;
        x_min = xmn;
        y_min = ymn;
        x_scale = x_pixels / (x_max - x_min);
        y_scale = y_pixels / (y_max - y_min);
        x_dot = xd;
        y_dot = yd;
        arrow_size = arr_size;
        parameterSymbols = paramSymbs;
        parameterValues = paramVals;
        pixel_length = real_coords(1, 0)[0] - real_coords(0,0)[0];
    }

    /**
     * This method is used when the graph is dragged/zoomed and then released to set the relevent
     * variables to their new values before redrawing the graph.
     *
     * @param X             The horizontal pixel coordinate of the the left side of the previous
     *                      graph as it appears scaled within the boundaries of the new graph we're
     *                      about to draw.
     * @param Y             The vertical pixel coordinate of the the top edge of the previous
     *                      graph as it appears scaled within the boundaries of the new graph we're
     *                      about to draw.
     * @param scaleFactor   The ratio by which the image has been scaled. More precisely,
     *                      width the previous graph has been scaled down to in pixels
     *                                         / width of the new graph (i.e. image width in pixels)
     */
    public void resetBoundsTranslatedScaled(float X, float Y, float scaleFactor) {
        double old_x_width = x_max - x_min;
        double old_y_height = y_max - y_min;
        x_scale *= scaleFactor;
        y_scale *= scaleFactor;
        x_min -= X / x_scale;
        x_max = x_min + old_x_width / scaleFactor;
        y_max += Y / y_scale;
        y_min = y_max - old_y_height / scaleFactor;
    }

    /**
     * Sets the colour of a pixel in the array of pixels associated with the Plot object. (This is
     * the array that becomes the bitmap of the plot area (not the axes).)
     *
     * @param i         The horizontal pixel coordinate of the pixel to be set (measured from the
     *                  left).
     * @param j         The vertical pixel coordinate of the pixel to be set (measured from the
     *                  top).
     * @param colour    The colour the pixel should be set to.
     */
    private void setPixel(int i, int j, int colour) {
        pixels[i + j * (x_pixels + border)] = colour;
    }

    /**
     * The pixel coordinates (i and j, horizontal and vertical, from the left and the top
     * (respectively)) of the given point (given described by its (x, y) coordinates). Note, this
     * method rounds to the nearest integer pixel position.
     *
     * @param x     The horizontal mathematical coordinate of the point, measured left to right from
     *              the origin.
     * @param y     The vertical mathematical coordinate of the point, measured bottom to top, from
     *              the origin.
     * @return      An array containing the integers that are the (i, j) pixel coordinates of the
     *              point.
     */
    private int[] pixel_coords(double x, double y) {
        int i = (int) ((x - x_min) * x_scale) + border;
        int j = (int) ((y_max - y) * y_scale);
        return new int[]{i, j};
    }

    /**
     * The pixel coordinate (i, horizontal, measured from the left) of the given point (given
     * described by its x coordinate). Note, this method rounds to the nearest integer pixel
     * position.
     *
     * @param x     The horizontal mathematical coordinate of the point, measured left to right from
     *              the origin.
     * @return      The integer i pixel coordinate of the point.
     */
    private int pixel_coord_i(double x) {
        int i = (int) ((x - x_min) * x_scale) + border;
        return i;
    }

    /**
     * The pixel coordinate (j, vertical, measured from the top) of the given point (given
     * described by its y coordinate). Note, this method rounds to the nearest integer pixel
     * position.
     *
     * @param y     The vertical mathematical coordinate of the point, measured left to right from
     *              the origin.
     * @return      The integer j pixel coordinate of the point.
     */
    private int pixel_coord_j(double y) {
        int j = (int) ((y_max - y) * y_scale);
        return j;
    }

    /**
     * The inverse of pixel_coords, this method takes in pixel coordinates (i, j) and outputs
     * mathematical coordinates (x, y) of a point.
     *
     * @param i     The horizontal pixel coordinate of a point.
     * @param j     The vertical pixel coordinate of a point.
     * @return      Both mathematical (x, y) coordinates of that point.
     */
    private double[] real_coords(int i, int j) {
        double x = (i - border) / x_scale + x_min;
        double y = y_max - j / y_scale;
        return new double[]{x, y};
    }

    /**
     * This method tells you whether the pixel in question is in the plotting area of the graph.
     *
     * @param i     The horizontal pixel coordinate of the pixel.
     * @param j     The vertical pixel coordinate of the pixel.
     * @return      true if it is in the plotting area, otherwise false.
     */
    private boolean in_frame(int i, int j) {
        if (i < border) {return false;}
        if (i >= x_pixels + border) {return false;}
        if (j < 0) {return false;}
        if (j >= y_pixels) {return false;}
        return true;
    }

    // TODO: Consider rewriting to not care which end is which
    /**
     * This method draws a line of a given colour to the array associated with the plot object. This
     * gives a line in the plotting area. The method uses the setPixel method. This version of the
     * method uses (x, y) coordinates while the other uses pixels ((i, j) coordinates).
     *
     * @param start_x   The x coordinate of the start of the line.
     * @param start_y   The y coordinate of the start of the line.
     * @param end_x     The x coordinate of the end of the line.
     * @param end_y     The y coordinate of the end of the line.
     * @param colour    The colour of the line.
     */
    void draw_line(double start_x, double start_y, double end_x, double end_y, int colour) {
        int[] start_coords = pixel_coords(start_x, start_y);
        int[] end_coords = pixel_coords(end_x, end_y);
        int start_i = start_coords[0];
        int start_j = start_coords[1];
        int end_i = end_coords[0];
        int end_j = end_coords[1];
        int x = end_i - start_i;
        int y = end_j - start_j;
        if (Math.abs(x) >= Math.abs(y)) {
            if (x > 0) {
                for (int i = 0; i < x; i++) {
                    int j = (int) ((double)y * i / x);
                    if (in_frame(start_i + i, start_j + j)) {
                        setPixel(start_i + i, start_j + j, colour);
                    }
                }
            } else {
                for (int i = 0; i > x; i--) {
                    int j = (int) ((double)y * i / x);
                    if (in_frame(start_i + i, start_j + j)) {
                        setPixel(start_i + i, start_j + j, colour);
                    }
                }
            }
        } else {
            if (y > 0) {
                for (int j = 0; j < y; j++) {
                    int i = (int) ((double)x * j / y);
                    if (in_frame(start_i + i, start_j + j)) {
                        setPixel(start_i + i, start_j + j, colour);
                    }
                }
            } else {
                for (int j = 0; j > y; j--) {
                    int i = (int) ((double)x * j / y);
                    if (in_frame(start_i + i, start_j + j)) {
                        setPixel(start_i + i, start_j + j, colour);
                    }
                }
            }
        }
    }

    /**
     * This method draws a line of a given colour to the array associated with the plot object. This
     * gives a line in the plotting area. The method uses the setPixel method. This version of the
     * method uses pixels ((i, j) coordinates) while the other uses (x, y) coordinates.
     *
     * @param start_i   The i coordinate of the start of the line.
     * @param start_j   The j coordinate of the start of the line.
     * @param end_i     The i coordinate of the end of the line.
     * @param end_j     The j coordinate of the end of the line.
     * @param colour    The colour of the line.
     */
    void draw_line(int start_i, int start_j, int end_i, int end_j, int colour) {
        int x = end_i - start_i;
        int y = end_j - start_j;
        if (Math.abs(x) >= Math.abs(y)) {
            if (x > 0) {
                for (int i = 0; i < x; i++) {
                    int j = (int) ((double)y * i / x);
                    if (in_frame(start_i + i, start_j + j)) {
                        setPixel(start_i + i, start_j + j, colour);
                    }
                }
            } else {
                for (int i = 0; i > x; i--) {
                    int j = (int) ((double)y * i / x);
                    if (in_frame(start_i + i, start_j + j)) {
                        setPixel(start_i + i, start_j + j, colour);
                    }
                }
            }
        } else {
            if (y > 0) {
                for (int j = 0; j < y; j++) {
                    int i = (int) ((double)x * j / y);
                    if (in_frame(start_i + i, start_j + j)) {
                        setPixel(start_i + i, start_j + j, colour);
                    }
                }
            } else {
                for (int j = 0; j > y; j--) {
                    int i = (int) ((double)x * j / y);
                    if (in_frame(start_i + i, start_j + j)) {
                        setPixel(start_i + i, start_j + j, colour);
                    }
                }
            }
        }
    }

    /**
     * This method draws an arrow of horizontal length x and vertical length y, centred on the point
     * (x_position, y_position). The arrow's colour is the colour given. This method uses both draw
     * line methods.
     *
     * @param x_position    The position of the centre of the arrow, horizontally
     * @param y_position    The position of the centre of the arrow, vertically
     * @param x             The length of the arrow, horizontally
     * @param y             The length of the arrow, vertically
     * @param colour        The colour of the arrow
     */
    private void draw_arrow(double x_position, double y_position, double x, double y, int colour) {

        // in order to centre the arrows on the given coordinates:
        x_position -= x/2;
        y_position -= y/2;
        double arrow_head_angle = 30;

        // draw the main line
        draw_line(x_position, y_position, x_position + x, y_position + y, colour);

        // calculate the arrow head start and end coordinates
        int start_i = pixel_coord_i(x_position);
        int start_j = pixel_coord_j(y_position);
        double i = x * x_scale;      // These are lengths not coordinates, so don't use pixel_coords
        double j = - y * y_scale;    // These are lengths not coordinates, so don't use pixel_coords
        int end_i = (int) Math.round((start_i + i));
        int end_j = (int) Math.round((start_j + j));
        int wing1_end_i = (int) Math.round(end_i + AMath.cos(180 - arrow_head_angle) * i/3 - AMath.sin(180 - arrow_head_angle) * j/3);
        int wing1_end_j = (int) Math.round(end_j + AMath.sin(180 - arrow_head_angle) * i/3 + AMath.cos(180 - arrow_head_angle) * j/3);
        int wing2_end_i = (int) Math.round(end_i + AMath.cos(180 + arrow_head_angle) * i/3 - AMath.sin(180 + arrow_head_angle) * j/3);
        int wing2_end_j = (int) Math.round(end_j + AMath.sin(180 + arrow_head_angle) * i/3 + AMath.cos(180 + arrow_head_angle) * j/3);

        // draw the lines for the arrow head
        draw_line(end_i, end_j, wing1_end_i, wing1_end_j, colour);
        draw_line(end_i, end_j, wing2_end_i, wing2_end_j, colour);
    }

    /**  returns a list of n values, each value in the centre of one of n equal length disjoint
     *   intervals between a and b
     *
     *   NB: floating point error will mess with long arrays
     *
     * @param start the beginning of the sequence of intervals
     * @param end   the end of the sequence of intervals
     * @param n     the number of intervals
     * @return      the list of points each of which is in the middle of one of the intervals
     */
    private double[] m_linspace(double start, double end, int n) {
        double interval_length = (end - start) / n;
        double current = start + interval_length / 2;
        double[] output = new double[n];
        for (int i = 0; i < n; i++) {
            output[i] = current;
            current += interval_length;
        }
        return output;
    }

    /**
     * This method draws the vector field described by the objects attributes, x_dot and y_dot which
     * are the differential equation in question. The method uses the draw_arrow attribute to draw
     * arrows to the list of pixels that will become the bitmap of the plot area.
     *
     * This method also sets the bottom decile length which is used as a guide for when the
     * draw_soln method is near an equilibrium point.
     */
    void draw_vector_field() {
        // TODO: Add big arrows mode for people with poor vision
        double[] x_samples = m_linspace(x_min, x_max, x_pixels / arrow_size);
        double[] y_samples = m_linspace(y_min, y_max, y_pixels / arrow_size);
        double max_arrow_length = 0;
        double[][] arrows = new double[x_samples.length * y_samples.length][4]; //TODO: shouldn't this be 5???
        double[] lengths = new double[x_samples.length * y_samples.length];
        for (int i = 0; i < x_samples.length; i++) {
            for (int j = 0; j < y_samples.length; j++) {
                double x = x_samples[i];
                double y = y_samples[j];
                // TODO: check whether your axes are the wrong way around
                double dx = Eval.eval(x_dot, x, y, parameterSymbols, parameterValues);
                double dy = Eval.eval(y_dot, x, y, parameterSymbols, parameterValues);
                double l = Math.sqrt(dx*dx + dy*dy);
                if (l > max_arrow_length) {max_arrow_length = l;}
                if (l < shortest_vector_drawn[2]) { shortest_vector_drawn = new double[]{x, y, l}; }
                    // shortest vector is used when drawing curves
                arrows[i + j * x_samples.length] = new double[]{x, y, dx, dy, l};
                lengths[i + j * x_samples.length] = l;
            }
        }
        double max_display_dx = x_samples[1] - x_samples[0];
        double max_display_dy = y_samples[1] - y_samples[0];
        for (int i = 0; i < arrows.length; i++) {
            // display scaling is so that you can still see the direction of 'very short' arrows
            double display_length = 4 * arrows[i][4] / 5 + max_arrow_length / 5;
            double display_scaling = display_length / arrows[i][4];
            double dx = display_scaling * max_display_dx * arrows[i][2] / max_arrow_length;
            double dy = display_scaling * max_display_dy * arrows[i][3] / max_arrow_length;
            int greyness = (int) (127 + Math.round(128 * arrows[i][4] / max_arrow_length));
            draw_arrow(arrows[i][0], arrows[i][1], dx, dy, Color.rgb(greyness, greyness, greyness));
        }
        double typical_slow_velocity = AMath.bottom_percentile(lengths);
        dt_max = 4 * pixel_length / typical_slow_velocity;
    }

    /**
     * This method creates up to two DrawCurveToArray runnables and runs them on separate threads.
     * They draw curves on the pixel array 'pixels' which will become the bitmap of the plot area.
     */
    void draw_soln(int i, int j, boolean forwards_is_steps, boolean backwards_is_steps,
                   int max_steps_forwards, int max_steps_backwards, double max_t_forwards,
                   double max_t_backwards, boolean solve_outside_plot_area) {

        // Choose a random colour (that's not too dark)
        Random rnd = new Random();
        int r = 64 + rnd.nextInt(192);
        int g = 64 + rnd.nextInt(192);
        int b = 64 + rnd.nextInt(192);
        int col = Color.rgb(r, g, b);
        // Draw a box at the tap location
        for (int k = -2; k < 3; k++) {
            for (int l = -2; l < 3; l++) {
                setPixel(i + k, j + l, col);
            }
        }

        // Set up the threads and start coordinates, then draw the solution
        Thread ft;
        Thread bt;
        double[] X = real_coords(i, j);
        double x = X[0];
        double y = X[1];
        if (forwards_is_steps) {
            ft = drawingThread(x, y, col, max_steps_forwards, 0.125, solve_outside_plot_area);
            ft.start();
        } else {
            ft = drawingThread(x, y, col, max_t_forwards, 0.125, solve_outside_plot_area);
            ft.start();
        }
        if (backwards_is_steps) {
            bt = drawingThread(x, y, col, max_steps_backwards, -0.125, solve_outside_plot_area);
            bt.start();
        } else {
            bt = drawingThread(x, y, col, max_t_backwards, -0.125, solve_outside_plot_area);
            bt.start();
        }
        try {
            ft.join();
            bt.join();
        } catch (InterruptedException e) {
            System.err.println("Interrupted");
        }
    }

    private Thread drawingThread(double x, double y, int color, int max_steps, double dt,
                                 boolean outside) {
        DrawCurveToArray fwds = new DrawCurveToArray(x_dot, y_dot, x, y, dt,
                pixel_length, x_min, x_max, y_min, y_max, color, this, parameterSymbols,
                parameterValues, dt_max, max_steps, outside);
        return new Thread(fwds);
    }

    private Thread drawingThread(double x, double y, int color, double max_time, double dt,
                                 boolean outside) {
        DrawCurveToArray fwds = new DrawCurveToArray(x_dot, y_dot, x, y, dt,
                pixel_length, x_min, x_max, y_min, y_max, color, this, parameterSymbols,
                parameterValues, dt_max, max_time, outside);
        return new Thread(fwds);
    }

    /**
     * This method draws a square on the plot area to denote a point at the location (x, y) in the
     * colour given. It simply translates the coordinates to (i, j) coordinates and passes them to
     * the other draw_point method.
     *
     * @param x         The x-coordinate of the point to be drawn
     * @param y         The y-coordinate of the point to be drawn
     * @param radius    The 'radius' - (edge_length - 1) / 2 - of the square drawn
     * @param color     The colour of the square
     */
    void draw_point(double x, double y, int radius, int color) {
        int i = pixel_coord_i(x);
        int j = pixel_coord_j(y);
        draw_point(i, j, radius, color);
    }

    /**
     * This method draws a square on the plot area to denote a point at the location (x, y) in the
     * colour given. It uses the setPixel method to do so.
     *
     * @param i         The i-coordinate of the point to be drawn
     * @param j         The j-coordinate of the point to be drawn
     * @param radius    The 'radius' - (edge_length - 1) / 2 - of the square drawn
     * @param color     The colour of the square
     */
    void draw_point(int i, int j, int radius, int color) {
        for (int k = i - radius; k < i + radius + 1; k++) {
            for (int l = j - radius; l < j + radius + 1; l++) {
                setPixel(k, l, color);
            }
        }
    }

    double[] find_equilibrium(double x, double y) {
        return find_equilibrium(pixel_coord_i(x), pixel_coord_j(y));
    }

    double[] find_equilibrium(int i, int j) {
        double delta_x = Double.POSITIVE_INFINITY;
        double delta_y = Double.POSITIVE_INFINITY;
        double[] coords = real_coords(i, j);
        double x = coords[0];
        double y = coords[1];
        double j11;
        double j12;
        double j21;
        double j22;
        double fx;
        double fy;
        double detJ;
        int count = 0;
        int max_count = 20;
        while ((Math.abs(delta_x) > pixel_length / 2 || Math.abs(delta_y) > pixel_length / 2) &&  count < max_count) {
            // Newton's method in 2D, inverting the Jacobian - consider solving directly without inverting
            j11 = ddx_x(x, y);
            j12 = ddy_x(x, y);
            j21 = ddx_y(x, y);
            j22 = ddy_y(x, y);
            fx = Eval.eval(x_dot, x, y, parameterSymbols, parameterValues);
            fy = Eval.eval(y_dot, x, y, parameterSymbols, parameterValues);
            detJ = j11 * j22 - j12 * j21;
            if (detJ == 0) {
                // TODO: deal with this case more carefully
                break;
            }
            delta_x = -(j22*fx - j12*fy) / detJ;
            delta_y = -(j11*fy - j21*fx) / detJ;
            x += delta_x;
            y += delta_y;
            count += 1;
        }
        return new double[]{x, y};
    }

    String equilibrium_classification(double x, double y) {
        double j11 = ddx_x(x, y);
        double j22 = ddy_y(x, y);
        double det = j11 * j22 - ddy_x(x, y) * ddx_y(x, y);
        if (det < 0) { return "saddle"; }
        double tr = j11 + j22;
        if (det == 0) {
            if (tr > 0) { return "degenerate unstable equilibrium"; }
            if (tr < 0) { return "degenerate stable equilibrium"; }
            return "degenerate parallel flow";
        }
        if (tr > 0) {
            if (det < (tr*tr / 4)) { return "unstable node"; }
            if (det > (tr*tr / 4)) { return "unstable focus"; }
            return "unstable improper node"; // tr > 0, det = tr^2 / 4
        }
        if (tr < 0) {
            if (det < (tr*tr / 4)) { return "stable node"; }
            if (det > (tr*tr / 4)) { return "stable focus"; }
            return "stable improper node"; // tr < 0, det = tr^2 / 4
        }
        return "centre"; // tr == 0, det > 0
    }

    /**
     * Jacobian components: ddx_y is the y component of the derivative with respect to x of
     * (x_dot, y_dot) at (x, y) - ie the derivative with respect to x of y_dot at (x, y). The
     * following four methods give the entries of the Jacobian matrix in the order j11, j12, j21,
     * j22
     *
     * The fifth method returns an array of these four values (in that order) written as strings to
     * the precision given.
     *
     * The sixth method returns the determinant and is used by DrawCurveToArray to check whether
     * it's approaching the kind of equilibrium it should stop at (a source or a sink - detJ >
     */
    double ddx_x(double x, double y) {
        double h = pixel_length / 512;
        return (Eval.eval(x_dot, x + h, y, parameterSymbols, parameterValues)
                - Eval.eval(x_dot, x, y, parameterSymbols, parameterValues)) / h;
    }
    double ddy_x(double x, double y) {
        double h = pixel_length / 512;
        return (Eval.eval(x_dot, x, y + h, parameterSymbols, parameterValues)
                - Eval.eval(x_dot, x, y, parameterSymbols, parameterValues)) / h;
    }
    double ddx_y(double x, double y) {
        double h = pixel_length / 512;
        return (Eval.eval(y_dot, x + h, y, parameterSymbols, parameterValues)
                - Eval.eval(y_dot, x, y, parameterSymbols, parameterValues)) / h;
    }
    double ddy_y(double x, double y) {
        double h = pixel_length / 512;
        return (Eval.eval(y_dot, x, y + h, parameterSymbols, parameterValues)
                - Eval.eval(y_dot, x, y, parameterSymbols, parameterValues)) / h;
    }
    String[] jacobianString(double x, double y, int precision) {
        MathContext mathContext = new MathContext(precision);
        String s11 = new BigDecimal(ddx_x(x, y), mathContext).toString();
        String s12 = new BigDecimal(ddy_x(x, y), mathContext).toString();
        String s21 = new BigDecimal(ddx_y(x, y), mathContext).toString();
        String s22 = new BigDecimal(ddy_y(x, y), mathContext).toString();
        return new String[]{s11, s12, s21, s22};
    }
    double detJ(double x, double y) {
        return ddx_x(x, y) * ddy_y(x, y) - ddy_x(x, y) * ddx_y(x, y);
    }

    /**
     * Returns the eigenvalues and eigenvectors of the Jacobian of the ODE at the point (x, y) in a
     * array [first eigenvalue, second eigenvalue, first eigenvector first component, first
     * eigenvector second component, second eigenvector first component, second eigenvector second
     * component]
     *
     * @param x     The x coordinate we're interested in
     * @param y     The y coordinate we're interested in
     * @return      An array containing the eigenvalues, then the eigenvectors component by
     *              component (6 element array in total, as described above).
     */
    Complex[] eigJ(double x, double y) {
        // Outputs
        Complex l1;
        Complex l2;
        Complex x1;
        Complex y1;
        Complex x2;
        Complex y2;

        // Jacobian matrix, J = [[a, b], [c, d]]
        Complex a = new Complex(ddx_x(x, y));
        Complex b = new Complex(ddy_x(x, y));
        Complex c = new Complex(ddx_y(x, y));
        Complex d = new Complex(ddy_y(x, y));

        // lambda 1 and 2 the eigenvalues of J.
        l1 = (a.plus(d).plus(a.plus(d).squared().minus(a.times(d).minus(b.times(c)).times(4)).sqrt())).div(2);
        l2 = (a.plus(d).minus(a.plus(d).squared().minus(a.times(d).minus(b.times(c)).times(4)).sqrt())).div(2);
        // These use the quadratic formula. The real version of the same thing is here:
        // double l1 = (a + d + Math.sqrt(Math.pow(a + d, 2) - 4*(a*d - b*c)))/2;
        // double l2 = (a + d - Math.sqrt(Math.pow(a + d, 2) - 4*(a*d - b*c)))/2;

        if (l1.equals(l2)) {
            if (a.minus(l1).modulus() < Math.pow(10, -12) && b.modulus() < Math.pow(10, -12)
                    && c.modulus() < Math.pow(10, -12) && d.minus(l1).modulus() < Math.pow(10, -12)) {
                // In this case the matrix is of the form λI, so all vectors are eigenvectors
                x1 = new Complex(1);
                y1 = new Complex(0);
                x2 = new Complex(0);
                y2 = new Complex(1);
                System.out.println("A");
            } else {
                // Set x2 as the zero vector to signify there is only one eigenvector
                x2 = new Complex(0);
                y2 = new Complex(0);
                if (a.minus(l1).modulus() < Math.pow(10, -12)) {
                    x1 = new Complex(1);
                    y1 = new Complex(0);
                } else if (b.modulus() < Math.pow(10, -12)) {
                    x1 = new Complex(0);
                    y1 = new Complex(1);
                } else {
                    // If x1 = 1, then (since (a - l1) * x1 + b * y1 = 0).
                    y1 = l1.minus(a).div(b);                    // (l1 - e) / b
                    // And normalize.
                    x1 = y1.squared().plus(1).sqrt().recip();   // 1 / sqrt(y1^2 + 1)
                    y1 = y1.div(y1.squared().plus(1).sqrt());   // y1 / sqrt(y1^2 + 1)
                }
            }
        } else {
            // For l1,
            if (a.minus(l1).modulus() < Math.pow(10, -12)) {
                x1 = new Complex(1);
                y1 = new Complex(0);
            } else if (b.modulus() < Math.pow(10, -12)) {
                x1 = new Complex(0);
                y1 = new Complex(1);
            } else {
                // If x1 = 1, then (since (a - l1) * x1 + b * y1 = 0).
                y1 = l1.minus(a).div(b);                    // (l1 - e) / b
                // And normalize.
                x1 = y1.squared().plus(1).sqrt().recip();   // 1 / sqrt(y1^2 + 1)
                y1 = y1.div(y1.squared().plus(1).sqrt());   // y1 / sqrt(y1^2 + 1)
            }

            // And we do the same for l2,
            if (a.minus(l2).modulus() < Math.pow(10, -12)) {
                x2 = new Complex(1);
                y2 = new Complex(0);
            } else if (b.modulus() < Math.pow(10, -12)) {
                x2 = new Complex(0);
                y2 = new Complex(1);
            } else {
                // If x2 = 1, then (since (a - l2) * x2 + b * y2 = 0).
                y2 = l2.minus(a).div(b);                    // (l2 - e) / b
                // And normalize.
                x2 = y2.squared().plus(1).sqrt().recip();   // 1 / sqrt(y2^2 + 1)
                y2 = y2.div(y2.squared().plus(1).sqrt());   // y2 / sqrt(y2^2 + 1)
            }
        }
        return new Complex[]{l1, l2, x1, y1, x2, y2};
    }

    /**
     * This sets all the pixels in the pixels array (the one used for the plot area) to black.
     */
    void make_black() {
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = Color.BLACK;
        }
    }

    /**
     * This method gives you the bitmap made from the pixels array after the borders have been cut
     * off. This bitmap shows the plot area.
     *
     * @return  The bitmap of the plot area.
     */
    Bitmap getBitmapPlotArea() {
        bmp.setPixels(pixels, 0, x_pixels + border, 0,0,
                x_pixels + border, y_pixels + border);
        Bitmap plotAreaBitmap = Bitmap.createBitmap(bmp, border, 0, x_pixels, y_pixels);
        return plotAreaBitmap;
    }

    /**
     * This method returns the x axis based of the current scale and position that the image of the
     * plot area is in. This is designed to happen repeatedly as the image is dragged around and
     * rescaled by the users fingers.
     *
     * @param X             The pixel coordinate of the left edge of the old image being dragged
     *                      around
     * @param scaleFactor   The scale the old image has shrunk or grown to (size of old image
     *                      divided by size it is when not being dragged around)
     * @return              A bitmap, dimensions (x_pixels + border) * border, containing the x-axis
     */
    Bitmap getNewXAxis(float X, float scaleFactor) {
        /*
        * Rescale x_scale, x_min and x_max to the temp_ versions so the axes match the current
        * scale and position of the graph that could be currently being dragged around.
        */
        double old_x_width = x_max - x_min;
        double temp_x_scale = x_scale * scaleFactor;
        double temp_x_min = x_min - X / temp_x_scale;
        double temp_x_max = temp_x_min + old_x_width / scaleFactor;

        // TODO: make this something users can change
        int color = Color.WHITE;

        // Create new bitmap to draw to
        Bitmap output = Bitmap.createBitmap(x_pixels + border, border, conf);
        int[] px = new int[(x_pixels + border) * border];
        for (int i = 0; i < px.length; i++) { px[i] = Color.BLACK; }

        //Axis edge line
        for (int i = border; i < border + x_pixels; i++) { px[i] = color; }

        // Axis ticks
        double x_diff = temp_x_max - temp_x_min;
        double x_mark_spacing = Math.pow(2, (int) Math.round(AMath.log2((double) 90 * x_diff / x_pixels)));
        double first_x_mark = temp_x_min - temp_x_min % x_mark_spacing;
        while (first_x_mark <= temp_x_min) { first_x_mark += x_mark_spacing; }
        int i;
        for (double x = first_x_mark; x < temp_x_max ; x += x_mark_spacing) {
            i = (int) ((x - temp_x_min) * temp_x_scale) + border;
            for (int j = 0; j < 15; j++) {
                px[i + j * (x_pixels + border)] = color;
            }
        }

        // Axis numbers
        output.setPixels(px, 0, x_pixels + border, 0,0, x_pixels + border, border);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setTextSize(25);
        String s;
        for (double x = first_x_mark; x < temp_x_max; x += x_mark_spacing) {
            s = Double.toString(x);
            i = (int) ((x - temp_x_min) * temp_x_scale) + border;
            if (s.length() > 7) {
                // This if statement deals with strings that won't fit in the margin
                MathContext mathContext = new MathContext(2);
                BigDecimal bigDecimal = new BigDecimal(x, mathContext);
                s = bigDecimal.toString();
                if (s.length() > 7) { paint.setTextSize(175 / s.length()); }
                canvas.drawText(s, i - 6 * s.length(), 3 * border / 5, paint);
                paint.setTextSize(25);
            } else {
                canvas.drawText(s, i - 6 * s.length(), 3 * border / 5, paint);
            }
        }

        return output;
    }

    /**
     * This method returns the y axis based of the current scale and position that the image of the
     * plot area is in. This is designed to happen repeatedly as the image is dragged around and
     * rescaled by the users fingers.
     *
     * @param Y             The pixel coordinate of the top edge of the old image being dragged
     *                      around
     * @param scaleFactor   The scale the old image has shrunk or grown to (size of old image
     *                      divided by size it is when not being dragged around)
     * @return              A bitmap, dimensions border * (y_pixels + border), containing the y-axis
     */
    Bitmap getNewYAxis(float Y, float scaleFactor) {
        /*
         * Rescale y_scale, y_min and y_max to the temp_ versions so the axes match the current
         * scale and position of the graph that could be currently being dragged around.
         */
        double old_y_height = y_max - y_min;
        double temp_y_scale = y_scale * scaleFactor;
        double temp_y_max = y_max + Y / temp_y_scale;
        double temp_y_min = temp_y_max - old_y_height / scaleFactor;

        // TODO: Make this accessible to the user
        int color = Color.WHITE;

        // Create new bitmap to draw to
        Bitmap output = Bitmap.createBitmap(border, y_pixels + border, conf);
        int[] px = new int[border * (y_pixels + border)];
        for (int i = 0; i < px.length; i++) { px[i] = Color.BLACK; }

        //Axis edge line
        for (int i = border - 1; i < border * y_pixels; i += border) { px[i] = color; }

        // Axis ticks
        double y_diff = temp_y_max - temp_y_min;
        double y_mark_spacing = Math.pow(2, (int) Math.round(AMath.log2((double) 90 * y_diff / y_pixels)));
        double first_y_mark = temp_y_min - temp_y_min % y_mark_spacing;
        while (first_y_mark <= temp_y_min) { first_y_mark += y_mark_spacing; }
        int j;
        for (double y = first_y_mark; y < temp_y_max ; y += y_mark_spacing) {
            j = (int) ((temp_y_max - y) * temp_y_scale);
            for (int i = border - 15; i < border; i++) {
                px[i + j * border] = color;
            }
        }

        // Axis numbers
        output.setPixels(px, 0, border, 0,0, border, y_pixels + border);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setTextSize(25);
        String s;
        for (double y = first_y_mark; y < temp_y_max; y += y_mark_spacing) {
            s = Double.toString(y);
            j = (int) ((temp_y_max - y) * temp_y_scale);
            if (s.length() > 5) {
                // This if statement deals with strings that won't fit in the margin
                MathContext mathContext = new MathContext(2);
                BigDecimal bigDecimal = new BigDecimal(y, mathContext);
                s = bigDecimal.toString();
                if (s.length() > 5) { paint.setTextSize(125 / s.length()); }
                canvas.drawText(s, Math.max(border - 20 - 11 * s.length(), 0), j + 8, paint);
                paint.setTextSize(25);
            } else {
                canvas.drawText(s, border - 20 - 11 * s.length(), j + 8, paint);
            }
        }

        return output;
    }

    /**
     * This method returns a blank black image the size of the whole plot area and axes to be drawn
     * behind it all, so as the plot area is moved around the background is black.
     *
     * @return  Blank black bitmap dimensions (x_pixels + border) * (y_pixels + border)
     */
    Bitmap getBackground() {
        int[] px = new int[(x_pixels + border) * (y_pixels + border)];
        for (int i = 0; i < px.length; i++) { px[i] = Color.BLACK; }
        return Bitmap.createBitmap(px, x_pixels + border, y_pixels + border, conf);
    }
}
