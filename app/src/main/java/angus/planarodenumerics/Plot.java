package angus.planarodenumerics;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays; // For debugging
import java.util.Random;

public class Plot {
    private int x_pixels;
    private int y_pixels;
    private Bitmap bmp;
    private double x_scale;
    private double y_scale;
    private double x_min;
    private double x_max;
    private double y_min;
    private double y_max;
    private String x_dot;
    private String y_dot;
    private int[] pixels;
    private int border;

    public Plot(double xmn, double xmx, double ymn, double ymx, String xd, String yd, int xpx, int ypx, int bdr) {
        border = bdr;
        x_pixels = xpx - border;
        y_pixels = ypx - border;
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        bmp = Bitmap.createBitmap(x_pixels + border, y_pixels + border, conf);
        pixels = new int[(x_pixels + border) * (y_pixels + border)];
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = Color.BLACK;
        }
        x_max = xmx;
        y_max = ymx;
        x_min = xmn;
        y_min = ymn;
        x_scale = x_pixels / (x_max - x_min);
        y_scale = y_pixels / (y_min - y_max);   // note that this scales in reverse so y can have up
                                                // as positive even though pixels don't
        x_dot = xd;
        y_dot = yd;
    }

    public Plot(double xmn, double xmx, double ymn, double ymx, String xd, String yd, int bdr, Bitmap bitmap) {
        border = bdr;
        bmp = bitmap;
        x_pixels = bmp.getWidth() - border;
        y_pixels = bmp.getHeight() - border;
        pixels = new int[(x_pixels + border) * (y_pixels + border)];
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        x_max = xmx;
        y_max = ymx;
        x_min = xmn;
        y_min = ymn;
        x_scale = x_pixels / (x_max - x_min);
        y_scale = y_pixels / (y_min - y_max);   // note that this scales in reverse so y can have up
        // as positive even though pixels don't
        x_dot = xd;
        y_dot = yd;
    }

    private double sin(double n) {
        return Math.sin(n * Math.PI / 180);
    }

    private double cos(double n) {
        return Math.cos(n * Math.PI / 180);
    }

    private void setPixel(int i, int j, int colour) {
        pixels[i + j * (x_pixels + border)] = colour;
    }

    private int[] pixel_coords(double x, double y) {
        int i = (int) ((x - x_min) * x_scale) + border;
        int j = (int) (y_pixels + (y - y_min) * y_scale);
        return new int[]{i, j};
    }

    private int pixel_coord_i(double x) {
        int i = (int) ((x - x_min) * x_scale) + border;
        return i;
    }

    private int pixel_coord_j(double y) {
        int j = (int) (y_pixels + (y - y_min) * y_scale);
        return j;
    }

    private double[] real_coords(int i, int j) {
        double x = (double) (i - border) / x_scale + x_min;
        double y = (double) j / y_scale - y_min;
        return new double[]{x, y};
    }

    private boolean in_frame(int i, int j) {
        if (i < border) {return false;}
        if (i >= x_pixels + border) {return false;}
        if (j < 0) {return false;}
        if (j >= y_pixels) {return false;}
        return true;
    }

    // TODO: Consider rewriting to not care which end is which
    private void draw_line(double start_x, double start_y, double end_x, double end_y, int colour, boolean frame_override) {
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
                    if (in_frame(start_i + i, start_j + j) || frame_override) {
                        setPixel(start_i + i, start_j + j, colour);
                    }
                }
            } else {
                for (int i = 0; i > x; i--) {
                    int j = (int) ((double)y * i / x);
                    if (in_frame(start_i + i, start_j + j) || frame_override) {
                        setPixel(start_i + i, start_j + j, colour);
                    }
                }
            }
        } else {
            if (y > 0) {
                for (int j = 0; j < y; j++) {
                    int i = (int) ((double)x * j / y);
                    if (in_frame(start_i + i, start_j + j) || frame_override) {
                        setPixel(start_i + i, start_j + j, colour);
                    }
                }
            } else {
                for (int j = 0; j > y; j--) {
                    int i = (int) ((double)x * j / y);
                    if (in_frame(start_i + i, start_j + j) || frame_override) {
                        setPixel(start_i + i, start_j + j, colour);
                    }
                }
            }
        }
    }

    private void draw_arrow(double start_x, double start_y, double x, double y, int colour) {
        // in order to centre the arrows on the given coordinates:
        start_x -= x/2;
        start_y -= y/2;
        double arrow_head_angle = 30;
        draw_line(start_x, start_y, start_x + x, start_y + y, colour, false);
        double wing1_end_x = start_x + x + cos(180 - arrow_head_angle) * x/3 - sin(180 - arrow_head_angle) * y/3;
        double wing1_end_y = start_y + y + sin(180 - arrow_head_angle) * x/3 + cos(180 - arrow_head_angle) * y/3;
        double wing2_end_x = start_x + x + cos(180 + arrow_head_angle) * x/3 - sin(180 + arrow_head_angle) * y/3;
        double wing2_end_y = start_y + y + sin(180 + arrow_head_angle) * x/3 + cos(180 + arrow_head_angle) * y/3;
        draw_line(start_x + x, start_y + y, wing1_end_x, wing1_end_y, colour, false);
        draw_line(start_x + x, start_y + y, wing2_end_x, wing2_end_y, colour, false);
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

    public void draw_vector_field() {
        // TODO: Add big arrows mode for people with poor vision
        double[] x_samples = m_linspace(x_min, x_max, x_pixels / 45);
        double[] y_samples = m_linspace(y_min, y_max, y_pixels / 45);
        double max_arrow_length = 0;
        double[][] arrows = new double[x_samples.length * y_samples.length][4];
        for (int i = 0; i < x_samples.length; i++) {
            for (int j = 0; j < y_samples.length; j++) {
                double x = x_samples[i];
                double y = y_samples[j];
                // TODO: check whether your axes are the wrong way around
                double dx = Eval.eval(x_dot, x, y);
                double dy = Eval.eval(y_dot, x, y);
                double l = Math.sqrt(dx*dx + dy*dy);
                if (l > max_arrow_length) {max_arrow_length = l;}
                arrows[i + j * x_samples.length] = new double[]{x, y, dx, dy};
            }
        }
        double max_display_dx = x_samples[1] - x_samples[0];
        double max_display_dy = y_samples[1] - y_samples[0];
        for (int i = 0; i < arrows.length; i++) {
            double dx = max_display_dx * arrows[i][2] / max_arrow_length;
            double dy = max_display_dy * arrows[i][3] / max_arrow_length;
            draw_arrow(arrows[i][0], arrows[i][1], dx, dy, Color.WHITE);
        }
    }


    // The following methods draw solution curves for given ICs using RK4

    // This is the public method that does the drawing and stuff.
    public void draw_soln(int i, int j) {

        // Choose a random colour
        Random rnd = new Random();
        int r = rnd.nextInt(256);
        int g = rnd.nextInt(256);
        int b = rnd.nextInt(256);
        int col = Color.rgb(r, g, b);

        // Draw a box at the tap location
        for (int k = -2; k < 3; k++) {
            for (int l = -2; l < 3; l++) {
                setPixel(i + k, j + l, col);
            }
        }

        // Draw the solution
        // TODO: make these variables accessible to the user
        double T = 8;
        double dt = 0.1;
        boolean forwards = true;
        boolean backwards = true;
        double x;
        double y;
        double[] X;
        double x_length;
        double y_length;
        double t;
        double pixel_length = real_coords(1, 0)[0] - real_coords(0,0)[0];
        // The following booleans and all their occurrences deal with 'very' non-linear situations
        // Basically they let us say dt is too big, but when we halve it it's too small, so just
        // use it anyway (or the same statement with dt too small but 2*dt too big).
        boolean increased_dt = false;
        boolean decreased_dt = false;
        if (forwards) {
            X = real_coords(i, j);
            x = X[0];
            y = X[1];
            t = 0;
            // TODO: Sometimes jumps away from equilibria....
            while (t < T) {
                X = RK4_step(x, y, dt);
                x_length = Math.abs(X[0] - x);
                y_length = Math.abs(X[1] - y);
                if (increased_dt && decreased_dt) {
                    draw_line(x, y, X[0], X[1], col, false);
                    t += dt;
                    x = X[0];
                    y = X[1];
                    increased_dt = false;
                    decreased_dt = false;
                    //System.out.println("A"); // debugging
                } else if (x_length < pixel_length && y_length < pixel_length) {
                    if (dt > 1) {
                        break;
                    } else {
                        dt *= 2;
                        increased_dt = true;
                        //System.out.println("B"); // debugging
                    }
                } else if (x_length > 4 * pixel_length || y_length > 4 * pixel_length) {
                    // TODO: Test if multiplying by 4 is significantly faster than by 5
                    dt /= 2;
                    decreased_dt = true;
                    //System.out.println("C"); // debugging
                } else if (X[0] > x_max || X[0] < x_min || X[1] > y_max || X[1] < y_min) {
                    break;
                } else {
                    draw_line(x, y, X[0], X[1], col, false);
                    t += dt;
                    x = X[0];
                    y = X[1];
                    increased_dt = false;
                    decreased_dt = false;
                    //System.out.println("D"); // debugging
                }
            }
        }
        increased_dt = false;
        decreased_dt = false;
        if (backwards) {
            X = real_coords(i, j);
            x = X[0];
            y = X[1];
            t = 0;
            while (t > -T) {
                X = RK4_step(x, y, -dt);
                x_length = Math.abs(X[0] - x);
                y_length = Math.abs(X[1] - y);
                if (increased_dt && decreased_dt) {
                    draw_line(x, y, X[0], X[1], col, false);
                    t -= dt;
                    x = X[0];
                    y = X[1];
                    increased_dt = false;
                    decreased_dt = false;
                    //System.out.println("E"); // debugging
                } else if (x_length < pixel_length && y_length < pixel_length) {
                    if (dt > 1) {
                        break;
                    } else {
                        dt *= 2;
                        increased_dt = true;
                        //System.out.println("F"); // debugging
                    }
                } else if (x_length > 4 * pixel_length || y_length > 4 * pixel_length) {
                    dt /= 2;
                    decreased_dt = true;
                    //System.out.println("G"); // debugging
                } else if (X[0] > x_max || X[0] < x_min || X[1] > y_max || X[1] < y_min) {
                    break;
                } else {
                    draw_line(x, y, X[0], X[1], col, false);
                    t -= dt;
                    x = X[0];
                    y = X[1];
                    increased_dt = false;
                    decreased_dt = false;
                    //System.out.println("H"); // debugging
                }
            }
        }
//        if (backwards) {
//            X = real_coords(i, j);
//            t = 0;
//            while (t > -T) {
//                x = X[0];
//                y = X[1];
//                X = RK4_step(x, y, -dt);
//                draw_line(x, y, X[0], X[1], col, false);
//                t -= dt;
//            }
//        }
        // For use when debug string is in use (also change return type to String)
        //return debug;
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

    // This is the public method that adds the x and y axes and scales
    public void draw_axes(int color) {

        // Set up tick spacings and tick positions
        double x_diff = x_max - x_min;
        double y_diff = y_max - y_min;
        double x_mark_spacing = Math.pow(2, (int) Math.round(AMath.log2((double) 90 * x_diff / x_pixels)));
        double y_mark_spacing = Math.pow(2, (int) Math.round(AMath.log2((double) 90 * y_diff / y_pixels)));
        double first_x_mark = x_min + x_mark_spacing - x_min % x_mark_spacing;
        double first_y_mark = y_min + y_mark_spacing - y_min % x_mark_spacing;

        // Axis ticks
        for (double x = first_x_mark; x < x_max ; x += x_mark_spacing) {
            draw_line(x, y_min, x, y_min + 15 / y_scale, color, true);
        }
        for (double y = first_y_mark; y < y_max ; y += y_mark_spacing) {
            draw_line(x_min, y, x_min - 15 / x_scale, y, color, true);
        }

        // Plot area border
        draw_line(x_min, y_min, x_max, y_min, color, true);
        draw_line(x_min, y_min, x_min, y_max, color, true);

        // Write numbers next to ticks
        bmp.setPixels(pixels, 0, x_pixels + border, 0,0, x_pixels + border, y_pixels + border);
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setTextSize(25);
        String s;
        for (double x = first_x_mark; x < x_max; x += x_mark_spacing) {
            s = Double.toString(x);
            // This if statement deals with strings that won't fit in the margin
            if (s.length() > 7) {
                MathContext mathContext = new MathContext(2);
                BigDecimal bigDecimal = new BigDecimal(x, mathContext);
                s = bigDecimal.toString();
                if (s.length() > 7) { paint.setTextSize(175 / s.length()); }
                canvas.drawText(s, pixel_coord_i(x) - 6 * s.length(), bmp.getHeight() - 2 * border / 5, paint);
                paint.setTextSize(25);
            } else {
                canvas.drawText(s, pixel_coord_i(x) - 6 * s.length(), bmp.getHeight() - 2 * border / 5, paint);
            }
            System.out.println(s);
        }
        for (double y = first_y_mark; y < y_max; y += y_mark_spacing) {
            s = Double.toString(y);
            if (s.length() > 5) {
                MathContext mathContext = new MathContext(2);
                BigDecimal bigDecimal = new BigDecimal(y, mathContext);
                s = bigDecimal.toString();
                if (s.length() > 5) { paint.setTextSize(125 / s.length()); }
                canvas.drawText(s, Math.max(border - 20 - 11 * s.length(), 0), pixel_coord_j(y) + 8, paint);
                paint.setTextSize(25);
            } else {
                canvas.drawText(s, border - 20 - 11 * s.length(), pixel_coord_j(y) + 8, paint);
            }
            System.out.println(s);
        }
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
    }

    public Bitmap giveBitmap() {
        bmp.setPixels(pixels, 0, x_pixels + border, 0,0, x_pixels + border, y_pixels + border);
        return bmp;
    }

}
