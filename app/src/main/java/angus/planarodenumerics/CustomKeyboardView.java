package angus.planarodenumerics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;

import java.util.List;

/**
 * This class extends the KeyboardView class to allow customising the onDraw method. The only thing
 * it changes is the appearance of the keyboard on the screen.
 *
 * At May 17, 2018, this method
 *  - draws "DONE"-labeled key with a blue circle and a tick instead of the text
 *  - draws multi-character labels at size 40
 *  - draws single character labels at size 60 (or 65 for math-italic x and y)
 *  - draws "f()", "123", "Aa", "CAPS", "CLR", and the backspace character in grey
 *  - draws all other key lables in black
 *  - does not draw any other features (key outlines can be added by un-commenting the relevant
 *    section)
 */
public class CustomKeyboardView extends KeyboardView {
    public CustomKeyboardView(Context context) {
        this(context, null, 0);
    }

    public CustomKeyboardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomKeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onDraw(Canvas canvas) {

        // Set up
        Paint paint = new Paint();
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setAntiAlias(true);
        String s;
        List<Keyboard.Key> keys = getKeyboard().getKeys();

        // Draw each key
        for(Keyboard.Key key : keys) {

            // s is the string we want to show on the key (these are determined in the layout file)
            s = key.label.toString();

            /**
             * Get the text size right for each key. There are three sizes we draw with:
             *      1. Labels with more than one character at size 40
             *      2. Labels with just one character at size 60
             *      3. The math-italic x and y which look weird at 60, so we draw at 65
             */
            if (s.length() > 1) {
                paint.setTextSize(40);
            } else {
                paint.setTextSize(60);
            }
            if (s.equals("\uD835\uDC65") || s.equals("\uD835\uDC66")) {
                paint.setTextSize(65);      // fancy x and y size
            }

            if (s.equals("DONE")) {
                // This statement draws the done key with a blue circle and a tick
                paint.setColor(Color.rgb(70, 136, 241));     // Light blue
                paint.setColor(0xffff4081);
                canvas.drawCircle(key.x + key.width/2, key.y + key.height/2, Math.min(key.width/3, key.height/2), paint);
                paint.setColor(Color.WHITE);
                canvas.drawText("✓", key.x + key.width/2, key.y + key.height/2 + 15, paint);
            } else if (s.equals("f()") || s.equals("Aa") || s.equals("123") || s.equals("CAPS") || s.equals("\u232B") || s.equals("CLR")) {
                // This statement draws other special keys in a lighter grey color
                paint.setColor(Color.rgb(90, 90, 90));
                canvas.drawText(s, key.x + key.width/2, key.y + key.height/2 + 15, paint);
            } else {
                // All other keys are drawn in black
                paint.setColor(Color.BLACK);
                canvas.drawText(s, key.x + key.width/2, key.y + key.height/2 + 15, paint);
            }

            /*
            // draw box around the key
            canvas.drawLine(key.x                   ,  key.y                    , key.x + key.width, key.y, paint);
            canvas.drawLine(key.x                   ,  key.y + key.height, key.x + key.width, key.y + key.height, paint);
            canvas.drawLine(key.x                   ,  key.y                    , key.x                   , key.y + key.height, paint);
            canvas.drawLine(key.x + key.width,  key.y                    , key.x + key.width, key.y + key.height, paint);
            */
        }
    }
}
