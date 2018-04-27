package angus.planarodenumerics;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;

public class Graph extends AppCompatActivity {

    String dxdt;
    String dydt;
    double xmin;
    double xmax;
    double ymin;
    double ymax;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        // Get the variables from the main activity
        Intent intent = getIntent();
        dxdt = intent.getStringExtra(MainActivity.EXTRA_DXDT_KEY);
        dydt = intent.getStringExtra(MainActivity.EXTRA_DYDT_KEY);
        xmin = Double.parseDouble(intent.getStringExtra(MainActivity.EXTRA_XMIN_KEY));
        xmax = Double.parseDouble(intent.getStringExtra(MainActivity.EXTRA_XMAX_KEY));
        ymin = Double.parseDouble(intent.getStringExtra(MainActivity.EXTRA_YMIN_KEY));
        ymax = Double.parseDouble(intent.getStringExtra(MainActivity.EXTRA_YMAX_KEY));

        // Write the equations up the top, so people know what graph they're looking at
        TextView dxdtView = findViewById(R.id.dxdtView);
        TextView dydtView = findViewById(R.id.dydtView);
        // TODO: make this nicer
        dxdtView.setText("dx/dt = " + dxdt);
        dydtView.setText("dy/dt = " + dydt);
    }

    // This also happens when the activity is created, but after we can work out the dimensions of
    // the image view
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            // Find the view we'll show the graph in
            ImageView image = (ImageView) findViewById(R.id.graphView);

            // Find width and height of the view
            int w = image.getWidth();
            int h = image.getHeight();

            // Create Plot object for plot and use it
            Plot plt = new Plot(xmin, xmax, ymin, ymax, dxdt, dydt, w, h, 80);
            plt.draw_vector_field();
            plt.draw_axes(Color.WHITE);
            image.setImageBitmap(plt.giveBitmap());
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int i = (int) event.getX();
        int j = (int) event.getY();
        if (event.getAction() == MotionEvent.ACTION_UP) {

            long t = System.nanoTime(); // for timing for the sake of debug

            // Get currently displayed bitmap
            ImageView image = (ImageView) findViewById(R.id.graphView);
            Bitmap bmp = ((BitmapDrawable) image.getDrawable()).getBitmap();

            // Get the distance of the image from the top of the screen
            int[] location = new int[2];
            image.getLocationOnScreen(location);
            int top_of_image_j = location[1];
            if (j < top_of_image_j || j > top_of_image_j + image.getHeight()) {return false;}
            // Create Plot object for plot and draw solution (Commented out lines allow debug messages)
            // TODO: make the co-ordinates work when in landscape mode OR lock rotation
            Plot plt = new Plot(xmin, xmax, ymin, ymax, dxdt, dydt, 80, bmp);
            plt.draw_soln(i, j - top_of_image_j);
            //String message = plt.draw_soln(i, j - delta_i - 140);

            // Outputs
            image.setImageBitmap(plt.giveBitmap());
            String message = Double.toString( ((double) (System.nanoTime() - t) / 1000000000)) + " s";
            TextView textView = findViewById(R.id.timeView);
            textView.setText(message);
            return true;
        }
        return false;
    }
}
