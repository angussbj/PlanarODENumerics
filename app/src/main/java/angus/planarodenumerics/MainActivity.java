package angus.planarodenumerics;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_DXDT_KEY = "angus.planarodenumerics.EXTRA_DXDT";
    public static final String EXTRA_DYDT_KEY = "angus.planarodenumerics.EXTRA_DYDT";
    public static final String EXTRA_XMIN_KEY = "angus.planarodenumerics.EXTRA_XMIN";
    public static final String EXTRA_XMAX_KEY = "angus.planarodenumerics.EXTRA_XMAX";
    public static final String EXTRA_YMIN_KEY = "angus.planarodenumerics.EXTRA_YMIN";
    public static final String EXTRA_YMAX_KEY = "angus.planarodenumerics.EXTRA_YMAX";
    public static final String EXTRA_ARROWSIZE_KEY = "angus.planarodenumerics.EXTRA_ARROWSIZE";
    public static final String EXTRA_MAXSTEPS_KEY = "angus.planarodenumerics.EXTRA_MAXSTEPS";
    public static final String EXTRA_FORWARDS_KEY = "angus.planarodenumerics.EXTRA_FORWARDS";
    public static final String EXTRA_BACKWARDS_KEY = "angus.planarodenumerics.EXTRA_BACKWARDS";

    public static final String PREF_DXDT_KEY = "angus.planarodenumerics.PREF_DXDT";
    public static final String PREF_DYDT_KEY = "angus.planarodenumerics.PREF_DYDT";
    public static final String PREF_XMIN_KEY = "angus.planarodenumerics.PREF_XMIN";
    public static final String PREF_XMAX_KEY = "angus.planarodenumerics.PREF_XMAX";
    public static final String PREF_YMIN_KEY = "angus.planarodenumerics.PREF_YMIN";
    public static final String PREF_YMAX_KEY = "angus.planarodenumerics.PREF_YMAX";
    public static final String PREF_ARROWSIZE_KEY = "angus.planarodenumerics.PREF_ARROWSIZE";
    public static final String PREF_MAXSTEPS_KEY = "angus.planarodenumerics.PREF_MAXSTEPS";
    public static final String PREF_FORWARDS_KEY = "angus.planarodenumerics.PREF_FORWARDS";
    public static final String PREF_BACKWARDS_KEY = "angus.planarodenumerics.PREF_BACKWARDS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Get previous values for input fields
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        String dxdt = sharedPref.getString(PREF_DXDT_KEY, "2*x - y + 3*(x^2-y^2) + 2*x*y");
        String dydt = sharedPref.getString(PREF_DYDT_KEY, "x - 3*y - 3*(x^2-y^2) + 3*x*y");
        String xmin = sharedPref.getString(PREF_XMIN_KEY, "-2");
        String xmax = sharedPref.getString(PREF_XMAX_KEY, "2");
        String ymin = sharedPref.getString(PREF_YMIN_KEY, "-3");
        String ymax = sharedPref.getString(PREF_YMAX_KEY, "3");
        String arrow_size = sharedPref.getString(PREF_ARROWSIZE_KEY, "-2");
        String max_steps = sharedPref.getString(PREF_MAXSTEPS_KEY, "2");
        boolean do_forwards = sharedPref.getBoolean(PREF_FORWARDS_KEY, true);
        boolean do_backwards = sharedPref.getBoolean(PREF_BACKWARDS_KEY, true);

        // Create EditText objects for each input field
        EditText dxdtEditText = findViewById(R.id.dxdtEditText);
        EditText dydtEditText = findViewById(R.id.dydtEditText);
        EditText xminEditText = findViewById(R.id.xminEditText);
        EditText xmaxEditText = findViewById(R.id.xmaxEditText);
        EditText yminEditText = findViewById(R.id.yminEditText);
        EditText ymaxEditText = findViewById(R.id.ymaxEditText);
        EditText arrowSizeEditText = findViewById(R.id.arrowSizeEditText);
        EditText maxStepsEditText = findViewById(R.id.maxStepsEditText);
        Switch plotForwardsSwitch = findViewById(R.id.plotForwardsSwitch);
        Switch plotBackwardsSwitch = findViewById(R.id.plotBackwardsSwitch);

        // Set the values
        dxdtEditText.setText(dxdt);
        dydtEditText.setText(dydt);
        xminEditText.setText(xmin);
        xmaxEditText.setText(xmax);
        yminEditText.setText(ymin);
        ymaxEditText.setText(ymax);
        arrowSizeEditText.setText(arrow_size);
        maxStepsEditText.setText(max_steps);
        plotForwardsSwitch.setChecked(do_forwards);
        plotBackwardsSwitch.setChecked(do_backwards);
    }

    public void graph(View view) {
        // Create EditText objects for each input field
        EditText dxdtEditText = findViewById(R.id.dxdtEditText);
        EditText dydtEditText = findViewById(R.id.dydtEditText);
        EditText xminEditText = findViewById(R.id.xminEditText);
        EditText xmaxEditText = findViewById(R.id.xmaxEditText);
        EditText yminEditText = findViewById(R.id.yminEditText);
        EditText ymaxEditText = findViewById(R.id.ymaxEditText);
        EditText arrowSizeEditText = findViewById(R.id.arrowSizeEditText);
        EditText maxStepsEditText = findViewById(R.id.maxStepsEditText);
        Switch plotForwardsSwitch = findViewById(R.id.plotForwardsSwitch);
        Switch plotBackwardsSwitch = findViewById(R.id.plotBackwardsSwitch);

        // Get strings from each input field
        String dxdt = dxdtEditText.getText().toString();
        String dydt = dydtEditText.getText().toString();
        String xmin = xminEditText.getText().toString();
        String xmax = xmaxEditText.getText().toString();
        String ymin = yminEditText.getText().toString();
        String ymax = ymaxEditText.getText().toString();
        String arrow_size = arrowSizeEditText.getText().toString();
        String max_steps = maxStepsEditText.getText().toString();
        boolean do_forwards = plotForwardsSwitch.isChecked();
        boolean do_backwards = plotBackwardsSwitch.isChecked();

        // To store these values for when we come back to this activity (they'll be used by onStart)
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(PREF_DXDT_KEY, dxdt);
        editor.putString(PREF_DYDT_KEY, dydt);
        editor.putString(PREF_XMIN_KEY, xmin);
        editor.putString(PREF_XMAX_KEY, xmax);
        editor.putString(PREF_YMIN_KEY, ymin);
        editor.putString(PREF_YMAX_KEY, ymax);
        editor.putString(PREF_ARROWSIZE_KEY, arrow_size);
        editor.putString(PREF_MAXSTEPS_KEY, max_steps);
        editor.putBoolean(PREF_FORWARDS_KEY, do_forwards);
        editor.putBoolean(PREF_BACKWARDS_KEY, do_backwards);
        editor.commit();

        // Create intent
        Intent intent = new Intent(this, GraphActivity.class);

        // Add these strings to the intent as extras
        intent.putExtra(EXTRA_DXDT_KEY, dxdt);
        intent.putExtra(EXTRA_DYDT_KEY, dydt);
        intent.putExtra(EXTRA_XMIN_KEY, xmin);
        intent.putExtra(EXTRA_XMAX_KEY, xmax);
        intent.putExtra(EXTRA_YMIN_KEY, ymin);
        intent.putExtra(EXTRA_YMAX_KEY, ymax);
        intent.putExtra(EXTRA_ARROWSIZE_KEY, arrow_size);
        intent.putExtra(EXTRA_MAXSTEPS_KEY, max_steps);
        intent.putExtra(EXTRA_FORWARDS_KEY, do_forwards);
        intent.putExtra(EXTRA_BACKWARDS_KEY, do_backwards);

        // Start the GraphActivity activity
        startActivity(intent);
    }

    public void restoreDefaults(View view) {
        // Create EditText objects for each input field
        EditText dxdtEditText = findViewById(R.id.dxdtEditText);
        EditText dydtEditText = findViewById(R.id.dydtEditText);
        EditText xminEditText = findViewById(R.id.xminEditText);
        EditText xmaxEditText = findViewById(R.id.xmaxEditText);
        EditText yminEditText = findViewById(R.id.yminEditText);
        EditText ymaxEditText = findViewById(R.id.ymaxEditText);
        EditText arrowSizeEditText = findViewById(R.id.arrowSizeEditText);
        EditText maxStepsEditText = findViewById(R.id.maxStepsEditText);
        Switch plotForwardsSwitch = findViewById(R.id.plotForwardsSwitch);
        Switch plotBackwardsSwitch = findViewById(R.id.plotBackwardsSwitch);

        // Set the values
        dxdtEditText.setText("2*x - y + 3*(x^2-y^2) + 2*x*y");
        dydtEditText.setText("x - 3*y - 3*(x^2-y^2) + 3*x*y");
        xminEditText.setText("-2");
        xmaxEditText.setText("2");
        yminEditText.setText("-3");
        ymaxEditText.setText("3");
        arrowSizeEditText.setText("60");
        maxStepsEditText.setText("700");
        plotForwardsSwitch.setChecked(true);
        plotBackwardsSwitch.setChecked(true);
    }


}

// TODO: Export figure options