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
    /*
    public CustomKeyboardView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    */

    @Override
    public void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setAntiAlias(true);
        String s;
        List<Keyboard.Key> keys = getKeyboard().getKeys();
        for(Keyboard.Key key : keys) {
            s = key.label.toString();
            if (s.length() > 1) {
                paint.setTextSize(40);
            } else {
                paint.setTextSize(60);
            }
            if (s.equals("\uD835\uDC65") || s.equals("\uD835\uDC66")) {
                paint.setTextSize(65);      // fancy x and y size
            }
            if (s.equals("DONE")) {
                paint.setColor(Color.rgb(70, 136, 241));     // THe lighter blue colour
                paint.setColor(0xffff4081);
                canvas.drawCircle(key.x + key.width/2, key.y + key.height/2, Math.min(key.width/3, key.height/2), paint);
                paint.setColor(Color.WHITE);
                //Bitmap bmp = BitmapFactory.decodeFile("draw")
                canvas.drawText("✓", key.x + key.width/2, key.y + key.height/2 + 15, paint);
            } else if (s.equals("f()") || s.equals("Aa") || s.equals("123") || s.equals("CAPS") || s.equals("\u232B") || s.equals("CLR")) {
                paint.setColor(Color.rgb(90, 90, 90));
                canvas.drawText(s, key.x + key.width/2, key.y + key.height/2 + 15, paint);
            } else {
                paint.setColor(Color.BLACK);
                canvas.drawText(s, key.x + key.width/2, key.y + key.height/2 + 15, paint);
            }
            /*
            // draw box around the key (for debugging?)
            canvas.drawLine(key.x                   ,  key.y                    , key.x + key.width, key.y, paint);
            canvas.drawLine(key.x                   ,  key.y + key.height, key.x + key.width, key.y + key.height, paint);
            canvas.drawLine(key.x                   ,  key.y                    , key.x                   , key.y + key.height, paint);
            canvas.drawLine(key.x + key.width,  key.y                    , key.x + key.width, key.y + key.height, paint);
            */
        }
    }
}
