package angus.planarodenumerics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class ZoomableImageView extends AppCompatImageView {
    private float X;
    private float Y;
    private float initFocusX;
    private float initFocusY;
    private float scaleFactor = 1f;
    private float lastTouchX;
    private float lastTouchY;
    private ScaleGestureDetector scaleDetector;
    private Bitmap bmp;
    private int activePointerId = -1;
    private boolean zoom;
    public Plot plt;

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
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        bmp = Bitmap.createBitmap(1, 1, conf);
        bmp.setPixel(0, 0, Color.rgb(250, 250,250));

        scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    @Override
    public void setImageBitmap(Bitmap bitmap) {
        super.setImageBitmap(bitmap);
        bmp = bitmap;
    }

    public void setPlot(Plot plot) {
        plt = plot;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        canvas.save();
        canvas.translate(X, Y);
        canvas.scale(scaleFactor, scaleFactor);
        canvas.drawBitmap(bmp, 0,0, paint);
        canvas.restore();
    }

    public void setZoom(boolean z) {
        zoom = z;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (zoom) {
            scaleDetector.onTouchEvent(event);      // give scaleDetector access to this information
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN: {
                    float x = event.getX();
                    float y = event.getY();
                    lastTouchX = x;
                    lastTouchY = y;
                    activePointerId = event.getPointerId(0);
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    if (!scaleDetector.isInProgress()) {
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
                    // TODO: fix how the app crashes when you pass control back and forth between fingers
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
                    plt.draw_axes(Color.WHITE);
                    bmp = plt.getBitmap();
                    invalidate();
                    activePointerId = -1;
                    break;
                }
            }
        }
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            initFocusX = detector.getFocusX();
            initFocusY = detector.getFocusY();
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            X = detector.getFocusX() - scaleFactor * initFocusX;
            Y = detector.getFocusY() - scaleFactor * initFocusY;
            invalidate();
            return true;
        }
    }
}

