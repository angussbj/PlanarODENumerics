package angus.planarodenumerics;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void changeImage(View view) {
        ImageView image = (ImageView) findViewById(R.id.imageView);
        // Find w = screen width
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int w = displayMetrics.widthPixels;
        // Get strings of equations
        EditText editText3 = (EditText) findViewById(R.id.editText3);
        String xdot = editText3.getText().toString();
        EditText editText4 = (EditText) findViewById(R.id.editText4);
        String ydot = editText4.getText().toString();
        // Create Plot object for plot and use it
        Plot plt = new Plot(-2, 2, -2, 2, xdot, ydot, w, w, 80);
        plt.draw_vector_field();
        plt.draw_axes(Color.WHITE);
        image.setImageBitmap(plt.giveBitmap());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int i = (int) event.getX();
        int j = (int) event.getY();
        if (event.getAction() == MotionEvent.ACTION_UP) {

            long t = System.nanoTime(); // for timing for the sake of debug

            // Get currently displayed bitmap
            ImageView image = (ImageView) findViewById(R.id.imageView);
            Bitmap bmp = ((BitmapDrawable) image.getDrawable()).getBitmap();

            // Get the distance of the image from the top of the screen
            int[] location = new int[2];
            image.getLocationOnScreen(location);
            int delta_i = location[1];
            int top_of_image_j = delta_i + 140; //for some completely unknown reason...
            if (j < top_of_image_j || j > top_of_image_j + image.getHeight()) {return false;}

            // Get strings of equations
            EditText editText3 = (EditText) findViewById(R.id.editText3);
            String xdot = editText3.getText().toString();
            EditText editText4 = (EditText) findViewById(R.id.editText4);
            String ydot = editText4.getText().toString();

            // Create Plot object for plot and draw solution (Commented out lines allow debug messages)
            Plot plt = new Plot(-2, 2, -2, 2, xdot, ydot, 80, bmp);
            plt.draw_soln(i, j - top_of_image_j);
            //String message = plt.draw_soln(i, j - delta_i - 140);

            // Outputs
            image.setImageBitmap(plt.giveBitmap());
            String message = Double.toString( ((double) (System.nanoTime() - t) / 1000000000)) + " s";
            TextView textView = findViewById(R.id.textView4);
            textView.setText(message);
            return true;
        }
        return false;
    }
}
