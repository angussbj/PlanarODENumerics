package angus.planarodenumerics;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GraphActivity extends AppCompatActivity {

    String dxdt;
    String dydt;
    double xmin;
    double xmax;
    double ymin;
    double ymax;
    int arrow_size;
    int max_steps;
    boolean do_forwards;
    boolean do_backwards;
    ZoomableImageView image;
    boolean zoom;
    boolean solution;
    boolean coord;
    Button zoomButton;
    Button solutionButton;
    Button coordButton;
    TextView timeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        // Set up the toolbar buttons
        zoomButton = findViewById(R.id.zoomButton);
        solutionButton = findViewById(R.id.solutionButton);
        coordButton = findViewById(R.id.coordButton);
        solutionButton.setBackgroundColor(0xFF303F9F);              // Set to primary colour dark
        zoom = false;
        solution = true;
        coord = false;

        // Set up the other text output
        timeView = findViewById(R.id.timeView);

        // Get the variables from the main activity
        Intent intent = getIntent();
        dxdt = intent.getStringExtra(MainActivity.EXTRA_DXDT_KEY);
        dydt = intent.getStringExtra(MainActivity.EXTRA_DYDT_KEY);
        xmin = Double.parseDouble(intent.getStringExtra(MainActivity.EXTRA_XMIN_KEY));
        xmax = Double.parseDouble(intent.getStringExtra(MainActivity.EXTRA_XMAX_KEY));
        ymin = Double.parseDouble(intent.getStringExtra(MainActivity.EXTRA_YMIN_KEY));
        ymax = Double.parseDouble(intent.getStringExtra(MainActivity.EXTRA_YMAX_KEY));
        arrow_size = Integer.parseInt(intent.getStringExtra(MainActivity.EXTRA_ARROWSIZE_KEY));
        max_steps = Integer.parseInt(intent.getStringExtra(MainActivity.EXTRA_MAXSTEPS_KEY));
        do_forwards = intent.getBooleanExtra(MainActivity.EXTRA_FORWARDS_KEY, true);
        do_backwards = intent.getBooleanExtra(MainActivity.EXTRA_BACKWARDS_KEY, true);

        // Write the equations up the top, so people know what graph they're looking at
        TextView dxdtView = findViewById(R.id.dxdtView);
        TextView dydtView = findViewById(R.id.dydtView);
        // TODO: make this nicer
        dxdtView.setText("dx/dt = " + dxdt);
        dydtView.setText("dy/dt = " + dydt);

        // Find the view we'll show the graph in
        image = findViewById(R.id.graphView);
        image.setOnTouchListener(onTouchListener);
    }

    // This also happens when the activity is created, but later than onCreate, so we can work out
    // the dimensions of the image view
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            // Find width and height of the view
            int w = image.getWidth();
            int h = image.getHeight();

            // Create Plot object for plot and use it
            image.setPlot(new Plot(xmin, xmax, ymin, ymax, dxdt.replace(" ", ""),
                    dydt.replace(" ", ""), w, h, 80, max_steps, arrow_size));
            image.plt.draw_vector_field();
            image.plt.draw_axes(Color.WHITE);
            image.setImageBitmap(image.plt.getBitmap());
        }
    }

    View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if (solution) {
                int i = (int) event.getX();
                int j = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {

                    long t = System.nanoTime(); // for timing the computation

                    // get location of image on screen to adjust
                    // TODO: turn this method into a listener so you don't have to do this
                    int[] location = new int[2];
                    image.getLocationOnScreen(location);

                    // draw solution and get bitmap
                    image.plt.draw_soln(i, j, do_forwards, do_backwards);

                    // Outputs
                    image.setImageBitmap(image.plt.getBitmap());
                    String message = Double.toString(((double) (System.nanoTime() - t) / 1000000000)) + " s";
                    timeView.setText(message);
                    return true;
                }
            } else if (coord) {
                int i = (int) event.getX();
                int j = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    image.plt.draw_soln(i, j, false, false);
                    image.setImageBitmap(image.plt.getBitmap());
                }
                timeView.setText(image.plt.real_coords_string(i, j));
            }
            return false;
        }
    };

    public void selectZoom(View view) {

        // set booleans so other methods know how to behave
        image.setZoom(true);
        solution = false;
        coord = false;

        // set appearance so user knows how the methods will behave
        zoomButton.setBackgroundColor(0xFF303F9F);                  // Set to primary colour dark
        solutionButton.setBackgroundColor(0xFF3F51B5);              // Set to primary colour light
        coordButton.setBackgroundColor(0xFF3F51B5);                 // Set to primary colour light
    }

    public void selectSolution(View view) {

        // set booleans so other methods know how to behave
        image.setZoom(false);
        solution = true;
        coord = false;

        // set appearance so user knows how the methods will behave
        zoomButton.setBackgroundColor(0xFF3F51B5);                  // Set to primary colour light
        solutionButton.setBackgroundColor(0xFF303F9F);              // Set to primary colour dark
        coordButton.setBackgroundColor(0xFF3F51B5);                 // Set to primary colour light
    }

    public void selectCoord(View view) {

        // set booleans so other methods know how to behave
        image.setZoom(false);
        solution = false;
        coord = true;

        // set appearance so user knows how the methods will behave
        zoomButton.setBackgroundColor(0xFF3F51B5);                  // Set to primary colour light
        solutionButton.setBackgroundColor(0xFF3F51B5);              // Set to primary colour light
        coordButton.setBackgroundColor(0xFF303F9F);                 // Set to primary colour dark
    }
}
