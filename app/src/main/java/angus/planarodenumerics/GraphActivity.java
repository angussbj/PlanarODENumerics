package angus.planarodenumerics;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.math.BigDecimal;
import java.math.MathContext;

public class GraphActivity extends AppCompatActivity {

    // Class instance variables
    String dxdt;
    String dydt;
    double xmin;
    double xmax;
    double ymin;
    double ymax;
    int arrow_size;
    ZoomableImageView image;
    boolean zoom;
    boolean solution;
    boolean equilibrium;
    String[] parameterSymbols;
    double[] parameterValues;
    Button zoomButton;
    Button solutionButton;
    Button equilibriumButton;
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
    boolean solve_outside_plot_area;
    boolean forwards_is_steps_not_time;
    boolean backwards_is_steps_not_time;
    double t_max_forwards;
    double t_max_backwards;
    int steps_max_forwards;
    int steps_max_bakcwards;
    Complex[] eigs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        // Set up the action bar
        setSupportActionBar((Toolbar) findViewById(R.id.graph_toolbar));
        ActionBar ab =  getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        // Set up the toolbar buttons
        zoomButton = findViewById(R.id.zoomButton);
        solutionButton = findViewById(R.id.solutionButton);
        equilibriumButton = findViewById(R.id.equilibriumButton);
        solutionButton.setBackgroundColor(0xFF303F9F);              // Set to primary colour dark
        zoom = false;
        solution = true;
        equilibrium = false;

        // Set up the text output fields
        timeView = findViewById(R.id.timeView);
        jEqualsView = findViewById(R.id.jEqualsTextView);
        leftBracketView = findViewById(R.id.jacobainLeftBracketTextView);
        rightBracketView = findViewById(R.id.jacobianRightBracketTextView);
        j11View = findViewById(R.id.j11TextView);
        j12View = findViewById(R.id.j12TextView);
        j21View = findViewById(R.id.j21TextView);
        j22View = findViewById(R.id.j22TextView);
        classificationView = findViewById(R.id.equilibruimClassificaitonTextView);
        dxdtView = findViewById(R.id.dxdtView);
        dydtView = findViewById(R.id.dydtView);
        eigenButton = findViewById(R.id.eigenButton);
        eigenView1 = findViewById(R.id.eigenView1);
        eigenView2 = findViewById(R.id.eigenView2);

        // Get the variables from the main activity
        Intent intent = getIntent();
        dxdt = intent.getStringExtra(MainActivity.EXTRA_DXDT_KEY);
        dydt = intent.getStringExtra(MainActivity.EXTRA_DYDT_KEY);
        parameterSymbols = intent.getStringArrayExtra(MainActivity.EXTRA_PARAMSYMBS_KEY);
        parameterValues = intent.getDoubleArrayExtra(MainActivity.EXTRA_PARAMVALS_KEY);
        xmin = Eval.eval(intent.getStringExtra(MainActivity.EXTRA_XMIN_KEY), 0, 0, parameterSymbols, parameterValues);
        xmax = Eval.eval(intent.getStringExtra(MainActivity.EXTRA_XMAX_KEY), 0, 0, parameterSymbols, parameterValues);
        ymin = Eval.eval(intent.getStringExtra(MainActivity.EXTRA_YMIN_KEY), 0, 0, parameterSymbols, parameterValues);
        ymax = Eval.eval(intent.getStringExtra(MainActivity.EXTRA_YMAX_KEY), 0, 0, parameterSymbols, parameterValues);
        arrow_size = Integer.parseInt(intent.getStringExtra(MainActivity.EXTRA_ARROWSIZE_KEY));
        solve_outside_plot_area = intent.getBooleanExtra(MainActivity.EXTRA_OUTSIDE_KEY, false);
        String t_max_f = intent.getStringExtra(MainActivity.EXTRA_TIMEMAX_KEY);
        String t_max_b = intent.getStringExtra(MainActivity.EXTRA_TIMEMIN_KEY);
        forwards_is_steps_not_time = t_max_f.equals("");
        backwards_is_steps_not_time = t_max_b.equals("");
        if (forwards_is_steps_not_time) {
            steps_max_forwards = Integer.parseInt(intent.getStringExtra(MainActivity.EXTRA_STEPMAX_KEY));
            t_max_forwards = 0;
        } else {
            t_max_forwards = Eval.eval(t_max_f, 0, 0, parameterSymbols, parameterValues);
            steps_max_forwards = 0;
        }
        if (backwards_is_steps_not_time) {
            steps_max_bakcwards = Integer.parseInt(intent.getStringExtra(MainActivity.EXTRA_STEPMIN_KEY));
            t_max_backwards = 0;
        } else {
            t_max_backwards = Eval.eval(t_max_b, 0, 0, parameterSymbols, parameterValues);
            steps_max_bakcwards = 0;

        }
        // Write the equations up the top, so people know what graph they're looking at
        dxdtView.setText("dx/dt = " + dxdt);
        dydtView.setText("dy/dt = " + dydt);

        // Find the view we'll show the graph in
        image = findViewById(R.id.graphView);
        //image.setOnTouchListener(onTouchListener);

        // Give the graph view the info it needs to draw solutions
        image.setSolutionDrawingVariables(forwards_is_steps_not_time,
                backwards_is_steps_not_time, steps_max_forwards, steps_max_bakcwards,
                t_max_forwards, t_max_backwards, solve_outside_plot_area);
        image.setSolutions(true);

        // Give the graph view access to the text views
        image.setViews(eigenButton, timeView, dxdtView, dydtView, jEqualsView, leftBracketView,
                rightBracketView, j11View, j12View, j21View, j22View, classificationView,
                eigenView1, eigenView2);
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
                    dydt.replace(" ", ""), w, h, 80, arrow_size,
                    parameterSymbols, parameterValues));
            image.plt.draw_vector_field();
            image.redraw_plot_area();
        }
    }

    private void update_text_views() {
        timeView.setText(image.timeString);
        j11View.setText(image.jStrings[0]);
        j12View.setText(image.jStrings[1]);
        j21View.setText(image.jStrings[2]);
        j22View.setText(image.jStrings[3]);
        classificationView.setText(image.classificationString);
        eigenView1.setText(image.e1String);
        eigenView2.setText(image.e2String);
        if (image.tVisible) {
            timeView.setVisibility(View.VISIBLE);
        } else {
            timeView.setVisibility(View.INVISIBLE);
        }
        if (image.dxdtVisible) {
            dxdtView.setVisibility(View.VISIBLE);
            dydtView.setVisibility(View.VISIBLE);
        } else {
            dxdtView.setVisibility(View.INVISIBLE);
            dydtView.setVisibility(View.INVISIBLE);
        }
        if (image.jVisible) {
            jEqualsView.setVisibility(View.VISIBLE);
            leftBracketView.setVisibility(View.VISIBLE);
            rightBracketView.setVisibility(View.VISIBLE);
            j11View.setVisibility(View.VISIBLE);
            j12View.setVisibility(View.VISIBLE);
            j21View.setVisibility(View.VISIBLE);
            j22View.setVisibility(View.VISIBLE);
        } else {
            jEqualsView.setVisibility(View.INVISIBLE);
            leftBracketView.setVisibility(View.INVISIBLE);
            rightBracketView.setVisibility(View.INVISIBLE);
            j11View.setVisibility(View.INVISIBLE);
            j12View.setVisibility(View.INVISIBLE);
            j21View.setVisibility(View.INVISIBLE);
            j22View.setVisibility(View.INVISIBLE);
        }
        if (image.cVisible) {
            classificationView.setVisibility(View.VISIBLE);
        } else {
            classificationView.setVisibility(View.INVISIBLE);
        }
        if (image.eVisible) {
            eigenView1.setVisibility(View.VISIBLE);
            eigenView2.setVisibility(View.VISIBLE);
        } else {
            eigenView1.setVisibility(View.INVISIBLE);
            eigenView2.setVisibility(View.INVISIBLE);
        }
        if (image.ebVisible) {
            eigenButton.setVisibility(View.VISIBLE);
        } else {
            eigenButton.setVisibility(View.INVISIBLE);
        }
    }

    public void show_eigenstuff(View view) {
        System.out.println();
        if (eigenView1.getVisibility() == View.VISIBLE) {
            timeView.setVisibility(View.VISIBLE);
            j11View.setVisibility(View.VISIBLE);
            j12View.setVisibility(View.VISIBLE);
            j21View.setVisibility(View.VISIBLE);
            j22View.setVisibility(View.VISIBLE);
            leftBracketView.setVisibility(View.VISIBLE);
            rightBracketView.setVisibility(View.VISIBLE);
            jEqualsView.setVisibility(View.VISIBLE);
            classificationView.setVisibility(View.VISIBLE);
            eigenView1.setVisibility(View.INVISIBLE);
            eigenView2.setVisibility(View.INVISIBLE);
        } else {
            timeView.setVisibility(View.INVISIBLE);
            j11View.setVisibility(View.INVISIBLE);
            j12View.setVisibility(View.INVISIBLE);
            j21View.setVisibility(View.INVISIBLE);
            j22View.setVisibility(View.INVISIBLE);
            leftBracketView.setVisibility(View.INVISIBLE);
            rightBracketView.setVisibility(View.INVISIBLE);
            jEqualsView.setVisibility(View.INVISIBLE);
            classificationView.setVisibility(View.INVISIBLE);
            eigenView1.setVisibility(View.VISIBLE);
            eigenView2.setVisibility(View.VISIBLE);
        }
    }

    public void selectZoom(View view) {

        // set booleans so other methods know how to behave
        image.setZoom(true);
        image.setSolutions(false);
        image.setEquilibria(false);

        // set appearance so user knows how the methods will behave
        zoomButton.setBackgroundColor(0xFF303F9F);                  // Set to primary colour dark
        solutionButton.setBackgroundColor(0xFF3F51B5);              // Set to primary colour light
        equilibriumButton.setBackgroundColor(0xFF3F51B5);           // Set to primary colour light

        image.tVisible = true;
        image.jVisible = false;
        image.cVisible = false;
        image.ebVisible = false;
        image.eVisible = false;
        image.dxdtVisible = true;
        update_text_views();
    }
    public void selectSolution(View view) {

        // set booleans so other methods know how to behave
        image.setZoom(false);
        image.setSolutions(true);
        image.setEquilibria(false);

        // set appearance so user knows how the methods will behave
        zoomButton.setBackgroundColor(0xFF3F51B5);                  // Set to primary colour light
        solutionButton.setBackgroundColor(0xFF303F9F);              // Set to primary colour dark
        equilibriumButton.setBackgroundColor(0xFF3F51B5);           // Set to primary colour light

        image.tVisible = true;
        image.jVisible = false;
        image.cVisible = false;
        image.ebVisible = false;
        image.eVisible = false;
        image.dxdtVisible = true;
        update_text_views();
    }
    public void selectEquilibrium(View view) {

        // set booleans so other methods know how to behave
        image.setZoom(false);
        image.setSolutions(false);
        image.setEquilibria(true);

        // set appearance so user knows how the methods will behave
        zoomButton.setBackgroundColor(0xFF3F51B5);                  // Set to primary colour light
        solutionButton.setBackgroundColor(0xFF3F51B5);              // Set to primary colour light
        equilibriumButton.setBackgroundColor(0xFF303F9F);           // Set to primary colour dark
    }
}
