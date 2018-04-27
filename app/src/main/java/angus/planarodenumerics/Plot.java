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
    private double[] shortest_vector_drawn = new double[]{0, 0, Double.POSITIVE_INFINITY};
    public int max_steps;
    private int arrow_size;

    Plot(double xmn, double xmx, double ymn, double ymx, String xd, String yd, int xpx, int ypx,
         int bdr, int max_stps, int arr_size) {
        border = bdr;
        x_pixels = xpx - border;
        y_pixels = ypx - border;
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
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
        max_steps = max_stps;
        arrow_size = arr_size;
    }

    public void resetBoundsTranslated(float X, float Y) {
        double dx = X / x_scale;
        double dy = Y / y_scale;
        x_max -= dx;
        y_max += dy;
        x_min -= dx;
        y_min += dy;
    }

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

    // TODO: move these to AMath
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
        int j = (int) ((y_max - y) * y_scale);
        return new int[]{i, j};
    }

    private int pixel_coord_i(double x) {
        int i = (int) ((x - x_min) * x_scale) + border;
        return i;
    }

    private int pixel_coord_j(double y) {
        int j = (int) ((y_max - y) * y_scale);
        return j;
    }

    private double[] real_coords(int i, int j) {
        double x = (i - border) / x_scale + x_min;
        double y = y_max - j / y_scale;
        return new double[]{x, y};
    }

    public String real_coords_string(int i, int j) {
        double[] X = real_coords(i, j);
        MathContext mathContext = new MathContext(3);
        BigDecimal x = new BigDecimal(X[0], mathContext);
        BigDecimal y = new BigDecimal(X[1], mathContext);
        return "(" + x.toString() + ", " + y.toString() + ")";
    }

    private boolean in_frame(int i, int j) {
        if (i < border) {return false;}
        if (i >= x_pixels + border) {return false;}
        if (j < 0) {return false;}
        if (j >= y_pixels) {return false;}
        return true;
    }

    // TODO: Consider rewriting to not care which end is which
    void draw_line(double start_x, double start_y, double end_x, double end_y, int colour, boolean frame_override) {
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

    void draw_line(int start_i, int start_j, int end_i, int end_j, int colour, boolean frame_override) {
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
        int start_i = pixel_coord_i(start_x);
        int start_j = pixel_coord_j(start_y);
        double i = x * x_scale;
        double j = - y * y_scale;
        int end_i = (int) Math.round((start_i + i));
        int end_j = (int) Math.round((start_j + j));
        int wing1_end_i = (int) Math.round(end_i + cos(180 - arrow_head_angle) * i/3 - sin(180 - arrow_head_angle) * j/3);
        int wing1_end_j = (int) Math.round(end_j + sin(180 - arrow_head_angle) * i/3 + cos(180 - arrow_head_angle) * j/3);
        int wing2_end_i = (int) Math.round(end_i + cos(180 + arrow_head_angle) * i/3 - sin(180 + arrow_head_angle) * j/3);
        int wing2_end_j = (int) Math.round(end_j + sin(180 + arrow_head_angle) * i/3 + cos(180 + arrow_head_angle) * j/3);
        draw_line(end_i, end_j, wing1_end_i, wing1_end_j, colour, false);
        draw_line(end_i, end_j, wing2_end_i, wing2_end_j, colour, false);
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

    void draw_vector_field() {
        // TODO: Add big arrows mode for people with poor vision
        double[] x_samples = m_linspace(x_min, x_max, x_pixels / arrow_size);
        double[] y_samples = m_linspace(y_min, y_max, y_pixels / arrow_size);
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
                if (l < shortest_vector_drawn[2]) { shortest_vector_drawn = new double[]{x, y, l}; }
                    // shortest vector is used when drawing curves
                arrows[i + j * x_samples.length] = new double[]{x, y, dx, dy, l};
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
    }

    // This method creates up to two DrawCurveToArray runnables and runs them on separate threads
    void draw_soln(int i, int j, boolean do_forwards, boolean do_backwards) {

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

        // Draw the solution
        double[] X = real_coords(i, j);
        double x = X[0];
        double y = X[1];
        double pixel_length = real_coords(1, 0)[0] - real_coords(0,0)[0];
        // TODO: get a better short vector - maybe find equilibria
        //double max_dt = 2 * pixel_length / shortest_vector_drawn[2];
        if (do_forwards && do_backwards) {
            DrawCurveToArray fwds = new DrawCurveToArray(x_dot, y_dot, x, y, 0.1,
                    pixel_length, x_min, x_max, y_min, y_max, col, this);
            Thread fwdThread = new Thread(fwds);
            fwdThread.start();
            DrawCurveToArray bwds = new DrawCurveToArray(x_dot, y_dot, x, y, -0.1,
                    pixel_length, x_min, x_max, y_min, y_max, col, this);
            Thread bwdThread = new Thread(bwds);
            bwdThread.start();
            try {
                fwdThread.join();
                bwdThread.join();
            } catch (InterruptedException e) {
                System.err.println("Interrupted");
            }
        } else if (do_forwards) {
            DrawCurveToArray fwds = new DrawCurveToArray(x_dot, y_dot, x, y, 0.1,
                    pixel_length, x_min, x_max, y_min, y_max, col, this);
            Thread fwdThread = new Thread(fwds);
            fwdThread.start();
            try {
                fwdThread.join();
            } catch (InterruptedException e) {
                System.err.println("Interrupted");
            }
        } else if (do_backwards) {
            DrawCurveToArray bwds = new DrawCurveToArray(x_dot, y_dot, x, y, -0.1,
                    pixel_length, x_min, x_max, y_min, y_max, col, this);
            Thread bwdThread = new Thread(bwds);
            bwdThread.start();
            try {
                bwdThread.join();
            } catch (InterruptedException e) {
                System.err.println("Interrupted");
            }
        }
    }

    // This is the public method that adds the x and y axes and scales
    void draw_axes(int color) {
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
        }
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
    }

    void make_black() {
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = Color.BLACK;
        }
    }

    Bitmap getBitmap() {
        bmp.setPixels(pixels, 0, x_pixels + border, 0,0, x_pixels + border, y_pixels + border);
        return bmp;
    }

}
