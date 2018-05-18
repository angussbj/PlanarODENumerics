package angus.planarodenumerics;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * This class is a specialised imageView that displays the graph in the graphActivity. It allows the
 * user to pan and zoom the graph and makes sure the axes make sense at the same time. It also
 * controls the drawing of solutions and finding of equilibria through the onTouchEvent method.
 */
public class ZoomableImageView extends AppCompatImageView {

    private float X;
    private float Y;
    private float initFocusX;
    private float initFocusY;
    private float scaleFactor;
    private float lastTouchX;
    private float lastTouchY;
    private ScaleGestureDetector scaleDetector;
    private Bitmap bmpPlot;
    private Bitmap bmpBackground;
    private int activePointerId = -1;
    private boolean solutions;
    boolean equilibria;
    public Plot plt;
    boolean forwards_is_steps_not_time;
    boolean backwards_is_steps_not_time;
    int steps_max_forwards;
    int steps_max_backwards;
    double t_max_forwards;
    double t_max_backwards;
    boolean solve_outside_plot_area;
    int jag;

    Button eigenButton;
    TextView timeView;
    TextView dxdtView;
    TextView dydtView;
    TextView jEqualsView;
    TextView leftBracketView;
    TextView rightBracketView;
    TextView j11View;
    TextView j12View;
    TextView j21View;
    TextView j22View;
    TextView classificationView;
    TextView eigenView1;
    TextView eigenView2;
    TextView tapInstructionTV;

    /**
     * Gives the defaults null and 0 if attrs and defStyle are not specified.
     */
    public ZoomableImageView(Context context) {
        this(context, null, 0);
    }

    /**
     * Gives default 0 if defStyle not specified.
     */
    public ZoomableImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Sets up the imageview, the variables that will store dispacement and scaling, and the scale
     * detector.
     */
    public ZoomableImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        X = 0f;
        Y = 0f;
        scaleFactor = 1f;

        scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    /**
     * This method allows this object to receive all the data it needs to draw solution curves.
     *
     * @param forwards_is_steps     A boolean that tells you whether the forwards direction is
     *                              limited by a measurement in steps or time
     * @param backwards_is_steps    A boolean that tells you whether the backwards direction is
     *                              limited by a measurement in steps or time
     * @param steps_max_f           The quantity of this measurement
     * @param steps_max_b           The quantity of this measurement
     * @param t_max_f               The quantity of this measurement
     * @param t_max_b               The quantity of this measurement
     * @param solve_outside         Whether solutions should be calculated outside the plot area
     * @param jaggedness            The max length of straight section allowed to make up a curve
     */
    public void setSolutionDrawingVariables(boolean forwards_is_steps, boolean backwards_is_steps,
                                            int steps_max_f, int steps_max_b, double t_max_f,
                                            double t_max_b, boolean solve_outside, int jaggedness) {
        forwards_is_steps_not_time = forwards_is_steps;
        backwards_is_steps_not_time = backwards_is_steps;
        steps_max_forwards = steps_max_f;
        steps_max_backwards = steps_max_b;
        t_max_forwards = t_max_f;
        t_max_backwards = t_max_b;
        solve_outside_plot_area = solve_outside;
        jag = jaggedness;
    }

    /**
     * This method allows this object to set, show, and hide views in the graph activity to give
     * output based on on touch events.
     */
    public void setViews(Button eigen, TextView time, TextView dxdt, TextView dydt, TextView jeq,
                         TextView leftB, TextView rightB, TextView j11, TextView j12, TextView j21,
                         TextView j22, TextView classif, TextView e1, TextView e2, TextView tap) {
        eigenButton = eigen;
        timeView = time;
        dxdtView = dxdt;
        dydtView = dydt;
        jEqualsView = jeq;
        leftBracketView = leftB;
        rightBracketView = rightB;
        j11View = j11;
        j12View = j12;
        j21View = j21;
        j22View = j22;
        classificationView = classif;
        eigenView1 = e1;
        eigenView2 = e2;
        tapInstructionTV = tap;
    }

    /**
     * This lets this view interact with what its showing.
     *
     * @param plot  The plot being viewed in this view
     */
    public void setPlot(Plot plot) {
        plt = plot;
        bmpBackground = plt.getBackground();
    }

    /**
     * This method describes how to draw the plot area as its being dragged around, how to draw the
     * axes, and ensures any area not covered by either thing being drawn is black.
     *
     * @param canvas    The drawing canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        if (bmpPlot != null) {
            Paint paint = new Paint();
            // Black background
            canvas.drawBitmap(bmpBackground, 0, 0, paint);
                //TODO: try new ColorDrawable(Color.parseColor("#000000"))
            // Plot area
            canvas.save();
            canvas.translate(X, Y);
            canvas.scale(scaleFactor, scaleFactor);
            canvas.drawBitmap(bmpPlot, plt.border, 0, paint);
            canvas.restore();
            // Axes
            canvas.drawBitmap(plt.getNewXAxis(X, scaleFactor), 0, plt.y_pixels, paint);
            canvas.drawBitmap(plt.getNewYAxis(Y, scaleFactor), 0, 0, paint);
        }
    }

    /**
     * The following two methods allow changing mode between solution plotting (solutions = true,
     * equilibria = false) and equilibrium finding (solutions = false, equilibria = true).
     *
     * TODO: Combine them?
     */
    public void setSolutions(boolean s) {
        solutions = s;
    }
    public void setEquilibria(boolean e) {
        equilibria = e;
    }

    /**
     * This method deals with the behaviour when the user touches the graph.
     *
     * @param event     The touch event.
     * @return          'True' to indicate that the event has been consumed, and does not need to be
     *                  dealt with further.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleDetector.onTouchEvent(event);      // give scaleDetector access to this information
        int action = event.getAction();
        switch (action) {

            case MotionEvent.ACTION_POINTER_DOWN: {
                int pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                // Apparently this gives this index of the pointer that came down. TODO: understand
                lastTouchX = event.getX();
                lastTouchY = event.getY();
                activePointerId = event.getPointerId(pointerIndex);
            }

            case MotionEvent.ACTION_DOWN: {
                lastTouchX = event.getX();
                lastTouchY = event.getY();
                activePointerId = event.getPointerId(0);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                if (event.getPointerCount() == 1) {
                    int pointerIndex = event.findPointerIndex(activePointerId);
                    float x = event.getX(pointerIndex);
                    float y = event.getY(pointerIndex);
                    float dx = x - lastTouchX;
                    float dy = y - lastTouchY;
                    X += dx;
                    Y += dy;
                    invalidate();
                    lastTouchX = x;
                    lastTouchY = y;
                }
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                int pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                // Apparently this gives this index of the pointer that left the screen. TODO: understand
                int pointerId = event.getPointerId(pointerIndex);
                if (pointerId == activePointerId) {
                    int newPointerIndex = ((pointerIndex == 0) ? 1 : 0);
                    lastTouchX = event.getX(newPointerIndex);
                    lastTouchY = event.getY(newPointerIndex);
                    activePointerId = event.getPointerId(newPointerIndex);
                }
                break;
            }

            case MotionEvent.ACTION_CANCEL:      // The control 'falls through' to the next case
            case MotionEvent.ACTION_UP: {
                if (scaleFactor != 1) {
                    // We zoomed, so redraw the plot area from scratch
                    plt.resetBoundsTranslatedScaled(X, Y, scaleFactor);
                    X = 0f;
                    Y = 0f;
                    scaleFactor = 1f;
                    plt.make_black();
                    plt.draw_vector_field();
                    this.redraw_plot_area();
                    activePointerId = -1;

                } else if (Math.abs(X) > 12 || Math.abs(Y) > 12) {
                    // We panned but didn't zoom, so redraw the plot area then bring things that are
                    // coloured across by translating them (the curves and the equilibria).
                    plt.resetBoundsTranslatedScaled(X, Y, scaleFactor);
                    int pix[] = plt.pixels.clone();
                    plt.make_black();
                    plt.draw_vector_field();
                    int imageWidth = plt.x_pixels + plt.border;
                    for (int n = 0; n < pix.length; n++) {
                        if (!isGrey(pix[n])) {
                            int i = n % imageWidth;
                            int j = n / imageWidth;
                            int i2 = (int) X + i;
                            int j2 = (int) Y + j;
                            if (i2 < imageWidth && i2 > plt.border && j2 < plt.y_pixels && j2 > 0) {
                                int m = i2 + j2 * imageWidth;
                                if (m > 0 && m < pix.length) {
                                    plt.pixels[m] = pix[n];
                                }
                            }
                        }
                    }
                    this.redraw_plot_area();
                    activePointerId = -1;
                    // Reset variables for the next event
                    X = 0f;
                    Y = 0f;
                    scaleFactor = 1f;

                } else if (solutions) {
                    // The plot was touched but not dragged or zoomed, we're in solution mode, so
                    // draw a solution curve
                    int i = (int) event.getX();
                    int j = (int) event.getY();
                    if (event.getAction() == MotionEvent.ACTION_UP) {

                        // draw solution and get bitmap
                        plt.draw_soln(i, j, forwards_is_steps_not_time,
                                backwards_is_steps_not_time, steps_max_forwards, steps_max_backwards,
                                t_max_forwards, t_max_backwards, solve_outside_plot_area, jag);

                        // Outputs
                        redraw_plot_area();
                        return true;
                    }

                } else if (equilibria) {
                    // The plot was touched but not dragged or zoomed, we're in equilibrium mode, so
                    // find an equilibrium point
                    int i = (int) event.getX();
                    int j = (int) event.getY();

                    // Find the equilibruim point
                    double[] eq_pt = plt.find_equilibrium(i, j);
                    double x = eq_pt[0];
                    double y = eq_pt[1];

                    if (Double.isNaN(x)) {
                        timeView.setText("(x, y) = (-, -)");
                        j11View.setText("-");
                        j12View.setText("-");
                        j21View.setText("-");
                        j22View.setText("-");
                        classificationView.setText("No equilibrium found");
                        eigenView1.setText("-");
                        eigenView2.setText("-");
                    } else {

                        // Display the coordinates in the top right view
                        MathContext mathContext = new MathContext(4);
                        BigDecimal out_x = new BigDecimal(x, mathContext);
                        BigDecimal out_y = new BigDecimal(y, mathContext);
                        timeView.setText("(x, y) = (" + out_x.toString() + ", " + out_y.toString() + ")");

                        // Find and display the jacobian
                        String[] js = plt.jacobianString(x, y, 4);
                        j11View.setText(js[0]);
                        j12View.setText(js[1]);
                        j21View.setText(js[2]);
                        j22View.setText(js[3]);

                        // Determine the type of equilibrium point and display it
                        classificationView.setText(plt.equilibrium_classification(x, y));

                        // Draw a little magenta square to mark the equilibrium point
                        if (x > plt.x_min && x < plt.x_max && y > plt.y_min && y < plt.y_max) {
                            plt.draw_point(eq_pt[0], eq_pt[1], 4, Color.MAGENTA);
                        }
                        redraw_plot_area();

                        // Calculate the eigenvalues and eigenvectors and set the strings
                        Complex[] eigs = plt.eigJ(x, y);
                        int p = 4; // precision with which to display eigen outputs
                        if (eigs[4].equals(0) && eigs[5].equals(0)) {
                            eigenView1.setText("λ = " + eigs[0].toString(p) + ", v = (" + eigs[2].toString(p) + ", " + eigs[3].toString(p) + ")");
                            eigenView2.setText("");
                        } else if (eigs[0].equals(eigs[1])) {
                            eigenView1.setText("λ = " + eigs[0].toString(p) + ", v1 = (" + eigs[2].toString(p) + ", " + eigs[3].toString(p) + ")");
                            eigenView2.setText("and v2 = (" + eigs[4].toString(p) + ", " + eigs[5].toString(p) + ")");
                        } else {
                            eigenView1.setText("λ1 = " + eigs[0].toString(p) + ", v1 = (" + eigs[2].toString(p) + ", " + eigs[3].toString(p) + ")");
                            eigenView2.setText("λ2 = " + eigs[1].toString(p) + ", v2 = (" + eigs[4].toString(p) + ", " + eigs[5].toString(p) + ")");
                        }

                    }

                    // Make the text views we're using visible, and the ones that overlap with them invisible
                    timeView.setVisibility(View.VISIBLE);
                    dxdtView.setVisibility(View.INVISIBLE);
                    dydtView.setVisibility(View.INVISIBLE);
                    j11View.setVisibility(View.VISIBLE);
                    j12View.setVisibility(View.VISIBLE);
                    j21View.setVisibility(View.VISIBLE);
                    j22View.setVisibility(View.VISIBLE);
                    leftBracketView.setVisibility(View.VISIBLE);
                    rightBracketView.setVisibility(View.VISIBLE);
                    jEqualsView.setVisibility(View.VISIBLE);
                    classificationView.setVisibility(View.VISIBLE);
                    eigenButton.setVisibility(View.VISIBLE);
                    tapInstructionTV.setVisibility(View.VISIBLE);
                    eigenView1.setVisibility(View.INVISIBLE);
                    eigenView2.setVisibility(View.INVISIBLE);
                }
            }
        }
        return true;
    }

    /**
     * This method checks wether a colour is grey.
     *
     * @param c     The colour
     * @return      Returns true if it's grey or false if it's not.
     */
    private boolean isGrey(int c) {
        int r = Color.red(c);
        int g = Color.green(c);
        int b = Color.blue(c);
        if (r == g && g == b) {
            return true;
        }
        return false;
    }

    /**
     * This method redraws the plot area by requesting a new vector field from the plot object and
     * asking the view to do onDraw.
     */
    public void redraw_plot_area() {
        bmpPlot = plt.getBitmapPlotArea();
        invalidate();
    }

    /**
     * This listener adjusts the X, Y and scaleFactor variables when the user zooms
     */
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            initFocusX = detector.getFocusX();
            initFocusY = detector.getFocusY();
            return true;
        }

        // TODO: consider re-drawing the vector field as dragging happens?
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            // when scaling happens, adjust the variables that keep track of it and redraw image
            scaleFactor *= detector.getScaleFactor();
            X = detector.getFocusX() - scaleFactor * initFocusX;
            Y = detector.getFocusY() - scaleFactor * initFocusY;
            invalidate();
            return true;
        }
    }
}

