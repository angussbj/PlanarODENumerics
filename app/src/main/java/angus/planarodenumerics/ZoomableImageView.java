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

public class ZoomableImageView extends AppCompatImageView {

    private float X;
    private float Y;
    private float initFocusX;
    private float initFocusY;
    private float scaleFactor = 1f;
    private float lastTouchX;
    private float lastTouchY;
    private ScaleGestureDetector scaleDetector;
    private Bitmap bmpPlot;
    private Bitmap bmpBackground;
    private int activePointerId = -1;
    private boolean zoom;
    private boolean solutions;
    private boolean equilibria;
    public Plot plt;
    private Activity host;
    boolean forwards_is_steps_not_time;
    boolean backwards_is_steps_not_time;
    int steps_max_forwards;
    int steps_max_backwards;
    double t_max_forwards;
    double t_max_backwards;
    boolean solve_outside_plot_area;

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

    /**
     * Gives the defaults null and 0 if attrs and defStyle are not specified.
     */
    public ZoomableImageView(Context context) {
        this(context, null, 0);
    }

    public ZoomableImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomableImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        X = 0f;
        Y = 0f;

        scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    public void setSolutionDrawingVariables(boolean forwards_is_steps, boolean backwards_is_steps,
                                            int steps_max_f, int steps_max_b, double t_max_f,
                                            double t_max_b, boolean solve_outside) {
        forwards_is_steps_not_time = forwards_is_steps;
        backwards_is_steps_not_time = backwards_is_steps;
        steps_max_forwards = steps_max_f;
        steps_max_backwards = steps_max_b;
        t_max_forwards = t_max_f;
        t_max_backwards = t_max_b;
        solve_outside_plot_area = solve_outside;
    }

    public void setViews(Button eigen, TextView time, TextView dxdt, TextView dydt, TextView jeq,
                         TextView leftB, TextView rightB, TextView j11, TextView j12, TextView j21,
                         TextView j22, TextView classif, TextView e1, TextView e2) {
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
    }

    public void setPlot(Plot plot) {
        plt = plot;
        bmpBackground = plt.getBackground();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (bmpPlot != null) {
            Paint paint = new Paint();
            canvas.drawBitmap(bmpBackground, 0, 0, paint);
                //TODO: try new ColorDrawable(Color.parseColor("#000000"))
            canvas.save();
            canvas.translate(X, Y);
            canvas.scale(scaleFactor, scaleFactor);
            canvas.drawBitmap(bmpPlot, plt.border, 0, paint);
            canvas.restore();
            canvas.drawBitmap(plt.getNewXAxis(X, scaleFactor), 0, plt.y_pixels, paint);
            canvas.drawBitmap(plt.getNewYAxis(Y, scaleFactor), 0, 0, paint);
        }
    }

    public void setZoom(boolean z) {
        zoom = z;
    }
    public void setSolutions(boolean s) {
        solutions = s;
    }
    public void setEquilibria(boolean e) {
        equilibria = e;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (zoom) {
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
                    plt.resetBoundsTranslatedScaled(X, Y, scaleFactor);
                    X = 0f;
                    Y = 0f;
                    scaleFactor = 1f;
                    plt.make_black();
                    plt.draw_vector_field();
                    this.redraw_plot_area();
                    activePointerId = -1;
                    break;
                }
            }
        } else if (solutions) {
            int i = (int) event.getX();
            int j = (int) event.getY();
            if (event.getAction() == MotionEvent.ACTION_UP) {

                long t = System.nanoTime(); // for timing the computation

                // draw solution and get bitmap
                plt.draw_soln(i, j, forwards_is_steps_not_time,
                        backwards_is_steps_not_time, steps_max_forwards, steps_max_backwards,
                        t_max_forwards, t_max_backwards, solve_outside_plot_area);

                // Outputs
                redraw_plot_area();
                timeView.setText(Double.toString(((double) (System.nanoTime() - t) / 1000000000)) + " s");
                return true;
            }
        } else if (equilibria) {
            int i = (int) event.getX();
            int j = (int) event.getY();
            if (event.getAction() == MotionEvent.ACTION_UP) {

                // Find the equilibruim point
                double[] eq_pt = plt.find_equilibrium(i, j);
                double x = eq_pt[0];
                double y = eq_pt[1];

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
                eigenView1.setVisibility(View.INVISIBLE);
                eigenView2.setVisibility(View.INVISIBLE);

                // Determine the type of equilibrium point and display it
                classificationView.setText(plt.equilibrium_classification(x, y));

                // Draw a little white square to mark the equilibrium point
                if (x > plt.x_min && x < plt.x_max && y > plt.y_min && y < plt.y_max) {
                    plt.draw_point(eq_pt[0], eq_pt[1], 4, Color.MAGENTA);
                }
                redraw_plot_area();

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
        }
        return true;
    }

    public void redraw_plot_area() {
        bmpPlot = plt.getBitmapPlotArea();
        invalidate();
    }

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

