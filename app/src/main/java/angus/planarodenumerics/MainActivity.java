package angus.planarodenumerics;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_DXDT_KEY = "angus.planarodenumerics.EXTRA_DXDT";
    public static final String EXTRA_DYDT_KEY = "angus.planarodenumerics.EXTRA_DYDT";
    public static final String EXTRA_XMIN_KEY = "angus.planarodenumerics.EXTRA_XMIN";
    public static final String EXTRA_XMAX_KEY = "angus.planarodenumerics.EXTRA_XMAX";
    public static final String EXTRA_YMIN_KEY = "angus.planarodenumerics.EXTRA_YMIN";
    public static final String EXTRA_YMAX_KEY = "angus.planarodenumerics.EXTRA_YMAX";
    public static final String EXTRA_ARROWSIZE_KEY = "angus.planarodenumerics.EXTRA_ARROWSIZE";
    public static final String EXTRA_PARAMSYMBS_KEY = "angus.planarodenumerics.EXTRA_PARAMSYMBS";
    public static final String EXTRA_PARAMVALS_KEY = "angus.planarodenumerics.EXTRA_PARAMVALS";
    public static final String EXTRA_STEPMAX_KEY = "angus.planarodenumerics.EXTRA_STEPMAX";
    public static final String EXTRA_STEPMIN_KEY = "angus.planarodenumerics.EXTRA_STEPMIN";
    public static final String EXTRA_TIMEMAX_KEY = "angus.planarodenumerics.EXTRA_TIMEMAX";
    public static final String EXTRA_TIMEMIN_KEY = "angus.planarodenumerics.EXTRA_TIMEMIN";
    public static final String EXTRA_OUTSIDE_KEY = "angus.planarodenumerics.EXTRA_OUTSIDE";
    public static final String EXTRA_JAG_KEY = "angus.planarodenumerics.EXTRA_JAG";

    public static final String PREF_DXDT_KEY = "angus.planarodenumerics.PREF_DXDT";
    public static final String PREF_DYDT_KEY = "angus.planarodenumerics.PREF_DYDT";
    public static final String PREF_XMIN_KEY = "angus.planarodenumerics.PREF_XMIN";
    public static final String PREF_XMAX_KEY = "angus.planarodenumerics.PREF_XMAX";
    public static final String PREF_YMIN_KEY = "angus.planarodenumerics.PREF_YMIN";
    public static final String PREF_YMAX_KEY = "angus.planarodenumerics.PREF_YMAX";
    public static final String PREF_ARROWSIZE_KEY = "angus.planarodenumerics.PREF_ARROWSIZE";
    public static final String PREF_PARAM1S_KEY = "angus.planarodenumerics.PREF_PARAM1S";
    public static final String PREF_PARAM2S_KEY = "angus.planarodenumerics.PREF_PARAM2S";
    public static final String PREF_PARAM3S_KEY = "angus.planarodenumerics.PREF_PARAM3S";
    public static final String PREF_PARAM4S_KEY = "angus.planarodenumerics.PREF_PARAM4S";
    public static final String PREF_PARAM5S_KEY = "angus.planarodenumerics.PREF_PARAM5S";
    public static final String PREF_PARAM6S_KEY = "angus.planarodenumerics.PREF_PARAM6S";
    public static final String PREF_PARAM1V_KEY = "angus.planarodenumerics.PREF_PARAM1V";
    public static final String PREF_PARAM2V_KEY = "angus.planarodenumerics.PREF_PARAM2V";
    public static final String PREF_PARAM3V_KEY = "angus.planarodenumerics.PREF_PARAM3V";
    public static final String PREF_PARAM4V_KEY = "angus.planarodenumerics.PREF_PARAM4V";
    public static final String PREF_PARAM5V_KEY = "angus.planarodenumerics.PREF_PARAM5V";
    public static final String PREF_PARAM6V_KEY = "angus.planarodenumerics.PREF_PARAM6V";
    public static final String PREF_STEPMAX_KEY = "angus.planarodenumerics.PREF_STEPMAX";
    public static final String PREF_STEPMIN_KEY = "angus.planarodenumerics.PREF_STEPMIN";
    public static final String PREF_TIMEMAX_KEY = "angus.planarodenumerics.PREF_TIMEMAX";
    public static final String PREF_TIMEMIN_KEY = "angus.planarodenumerics.PREF_TIMEMIN";
    public static final String PREF_OUTSIDE_KEY = "angus.planarodenumerics.PREF_OUTSIDE";
    public static final String PREF_JAG_KEY = "angus.planarodenumerics.PREF_JAG";

    EditText dxdtEditText;
    EditText dydtEditText;
    EditText xminEditText;
    EditText xmaxEditText;
    EditText yminEditText;
    EditText ymaxEditText;
    EditText param1SymbolET;
    EditText param2SymbolET;
    EditText param3SymbolET;
    EditText param4SymbolET;
    EditText param5SymbolET;
    EditText param6SymbolET;
    EditText param1ValueET;
    EditText param2ValueET;
    EditText param3ValueET;
    EditText param4ValueET;
    EditText param5ValueET;
    EditText param6ValueET;
    EditText arrowSizeEditText;
    EditText stepMaxET;
    EditText stepMinET;
    EditText timeMaxET;
    EditText timeMinET;
    EditText jagET;
    Switch   outsideSw;
    CustomKeyboard k_eqn_xyab;
    CustomKeyboard k_int;
    CustomKeyboard k_abc;
    Button   graphButton;
    View errorView;
    TextView errorMessageTV;

    /**
     * This method identifies all the input fields for use in other methods, and tells them each
     * what keyboard they get to use (as well as setting up the keyboards).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.main_toolbar));

        // Set up all the input field objects
        dxdtEditText = findViewById(R.id.dxdtEditText);
        dydtEditText = findViewById(R.id.dydtEditText);
        xminEditText = findViewById(R.id.xminEditText);
        xmaxEditText = findViewById(R.id.xmaxEditText);
        yminEditText = findViewById(R.id.yminEditText);
        ymaxEditText = findViewById(R.id.ymaxEditText);
        param1SymbolET = findViewById(R.id.parameter1SymbolEditText);
        param2SymbolET = findViewById(R.id.parameter2SymbolEditText);
        param3SymbolET = findViewById(R.id.parameter3SymbolEditText);
        param4SymbolET = findViewById(R.id.parameter4SymbolEditText);
        param5SymbolET = findViewById(R.id.parameter5SymbolEditText);
        param6SymbolET = findViewById(R.id.parameter6SymbolEditText);
        param1ValueET = findViewById(R.id.parameter1ValueEditText);
        param2ValueET = findViewById(R.id.parameter2ValueEditText);
        param3ValueET = findViewById(R.id.parameter3ValueEditText);
        param4ValueET = findViewById(R.id.parameter4ValueEditText);
        param5ValueET = findViewById(R.id.parameter5ValueEditText);
        param6ValueET = findViewById(R.id.parameter6ValueEditText);
        arrowSizeEditText = findViewById(R.id.arrowSizeEditText);
        stepMaxET = findViewById(R.id.maxPositiveStepsEditText);
        stepMinET = findViewById(R.id.maxNegativeStepsEditText);
        timeMaxET = findViewById(R.id.maxPositiveTimeEditText);
        timeMinET = findViewById(R.id.maxNegativeTimeEditText);
        jagET = findViewById(R.id.jaggednessEditText);
        outsideSw = findViewById(R.id.allowOffScreenSolutionCurvesSwitch);
        errorView = findViewById(R.id.errorScrimFrame);
        errorMessageTV = findViewById(R.id.errorMessageTV);

        // Set up the keyboards for different types of input field
        k_eqn_xyab = new CustomKeyboard(this, R.id.keyboardview, R.layout.keyboard);
        k_int = new CustomKeyboard(this, R.id.keyboardview, R.layout.keyboard_int);
        k_abc = new CustomKeyboard(this, R.id.keyboardview, R.layout.keyboard_lower_letters_only);
        k_eqn_xyab.registerEditText(dxdtEditText);
        k_eqn_xyab.registerEditText(dydtEditText);
        k_eqn_xyab.registerEditText(xminEditText);
        k_eqn_xyab.registerEditText(xmaxEditText);
        k_eqn_xyab.registerEditText(yminEditText);
        k_eqn_xyab.registerEditText(ymaxEditText);
        k_abc.registerEditText(param1SymbolET);
        k_abc.registerEditText(param2SymbolET);
        k_abc.registerEditText(param3SymbolET);
        k_abc.registerEditText(param4SymbolET);
        k_abc.registerEditText(param5SymbolET);
        k_abc.registerEditText(param6SymbolET);
        k_eqn_xyab.registerEditText(param1ValueET);
        k_eqn_xyab.registerEditText(param2ValueET);
        k_eqn_xyab.registerEditText(param3ValueET);
        k_eqn_xyab.registerEditText(param4ValueET);
        k_eqn_xyab.registerEditText(param5ValueET);
        k_eqn_xyab.registerEditText(param6ValueET);
        k_int.registerEditText(arrowSizeEditText);
        k_int.registerEditText(jagET);
        k_int.registerEditText(stepMaxET, timeMaxET);    //   The second argument is the edit text
        k_int.registerEditText(stepMinET, timeMinET);    // with which this edit text is mutually
        k_eqn_xyab.registerEditText(timeMaxET, stepMaxET); // exclusive.
        k_eqn_xyab.registerEditText(timeMinET, stepMinET); // (here too)
    }

    /**
     * This method restores all the text in the input fields from the saved state, and hides the
     * keyboard.
     */
    @Override
    protected void onResume() {
        super.onResume();

        // Get previous values for input fields
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        String dxdt = sharedPref.getString(PREF_DXDT_KEY, "(x+1)*y*(x-2) + (y-1)*x*(y+2)*(y-2)");
        String dydt = sharedPref.getString(PREF_DYDT_KEY, "(y-1)*x*(y+2)*(y-2) - (x+1)*y*(x-2)");
        String xmin = sharedPref.getString(PREF_XMIN_KEY, "-6");
        String xmax = sharedPref.getString(PREF_XMAX_KEY, "6");
        String ymin = sharedPref.getString(PREF_YMIN_KEY, "-8");
        String ymax = sharedPref.getString(PREF_YMAX_KEY, "12");
        String param1Symbol = sharedPref.getString(PREF_PARAM1S_KEY, "a");
        String param2Symbol = sharedPref.getString(PREF_PARAM2S_KEY, "b");
        String param3Symbol = sharedPref.getString(PREF_PARAM3S_KEY, "c");
        String param4Symbol = sharedPref.getString(PREF_PARAM4S_KEY, "d");
        String param5Symbol = sharedPref.getString(PREF_PARAM5S_KEY, "f");
        String param6Symbol = sharedPref.getString(PREF_PARAM6S_KEY, "g");
        String param1Value = sharedPref.getString(PREF_PARAM1V_KEY, "");
        String param2Value = sharedPref.getString(PREF_PARAM2V_KEY, "");
        String param3Value = sharedPref.getString(PREF_PARAM3V_KEY, "");
        String param4Value = sharedPref.getString(PREF_PARAM4V_KEY, "");
        String param5Value = sharedPref.getString(PREF_PARAM5V_KEY, "");
        String param6Value = sharedPref.getString(PREF_PARAM6V_KEY, "");
        String arrow_size = sharedPref.getString(PREF_ARROWSIZE_KEY, "60");
        String stepMax = sharedPref.getString(PREF_STEPMAX_KEY, "700");
        String stepMin = sharedPref.getString(PREF_STEPMIN_KEY, "700");
        String timeMax = sharedPref.getString(PREF_TIMEMAX_KEY, "");
        String timeMin = sharedPref.getString(PREF_TIMEMIN_KEY, "");
        String jag = sharedPref.getString(PREF_JAG_KEY, "4");
        Boolean outside = sharedPref.getBoolean(PREF_OUTSIDE_KEY, false);

        // Set the values
        dxdtEditText.setText(dxdt);
        dydtEditText.setText(dydt);
        xminEditText.setText(xmin);
        xmaxEditText.setText(xmax);
        yminEditText.setText(ymin);
        ymaxEditText.setText(ymax);
        param1SymbolET.setText(param1Symbol);
        param2SymbolET.setText(param2Symbol);
        param3SymbolET.setText(param3Symbol);
        param4SymbolET.setText(param4Symbol);
        param5SymbolET.setText(param5Symbol);
        param6SymbolET.setText(param6Symbol);
        param1ValueET.setText(param1Value);
        param2ValueET.setText(param2Value);
        param3ValueET.setText(param3Value);
        param4ValueET.setText(param4Value);
        param5ValueET.setText(param5Value);
        param6ValueET.setText(param6Value);
        arrowSizeEditText.setText(arrow_size);
        stepMaxET.setText(stepMax);
        stepMinET.setText(stepMin);
        timeMaxET.setText(timeMax);
        timeMinET.setText(timeMin);
        jagET.setText(jag);
        outsideSw.setChecked(outside);

        // Hide the keyboard by moving focus to the random button that has no other purpose
        findViewById(R.id.focus_on_me).requestFocus();
    }

    /**
     * This method saves all the text in the input fields when the activity is paused
     */
    @Override
    protected void onPause() {
        super.onPause();

        // Get strings (or booleans) from each input field
        String dxdt = dxdtEditText.getText().toString();
        String dydt = dydtEditText.getText().toString();
        String xmin = xminEditText.getText().toString();
        String xmax = xmaxEditText.getText().toString();
        String ymin = yminEditText.getText().toString();
        String ymax = ymaxEditText.getText().toString();
        String param1Symbol = param1SymbolET.getText().toString();
        String param2Symbol = param2SymbolET.getText().toString();
        String param3Symbol = param3SymbolET.getText().toString();
        String param4Symbol = param4SymbolET.getText().toString();
        String param5Symbol = param5SymbolET.getText().toString();
        String param6Symbol = param6SymbolET.getText().toString();
        String param1Value = param1ValueET.getText().toString();
        String param2Value = param2ValueET.getText().toString();
        String param3Value = param3ValueET.getText().toString();
        String param4Value = param4ValueET.getText().toString();
        String param5Value = param5ValueET.getText().toString();
        String param6Value = param6ValueET.getText().toString();
        String arrow_size = arrowSizeEditText.getText().toString();
        String stepMax = stepMaxET.getText().toString();
        String stepMin = stepMinET.getText().toString();
        String timeMax = timeMaxET.getText().toString();
        String timeMin = timeMinET.getText().toString();
        String jag = jagET.getText().toString();
        Boolean outside = outsideSw.isChecked();

        // Store these values for when we come back to this activity (they'll be used by onStart)
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(PREF_DXDT_KEY, dxdt);
        editor.putString(PREF_DYDT_KEY, dydt);
        editor.putString(PREF_XMIN_KEY, xmin);
        editor.putString(PREF_XMAX_KEY, xmax);
        editor.putString(PREF_YMIN_KEY, ymin);
        editor.putString(PREF_YMAX_KEY, ymax);
        editor.putString(PREF_PARAM1S_KEY, param1Symbol);
        editor.putString(PREF_PARAM2S_KEY, param2Symbol);
        editor.putString(PREF_PARAM3S_KEY, param3Symbol);
        editor.putString(PREF_PARAM4S_KEY, param4Symbol);
        editor.putString(PREF_PARAM5S_KEY, param5Symbol);
        editor.putString(PREF_PARAM6S_KEY, param6Symbol);
        editor.putString(PREF_PARAM1V_KEY, param1Value);
        editor.putString(PREF_PARAM2V_KEY, param2Value);
        editor.putString(PREF_PARAM3V_KEY, param3Value);
        editor.putString(PREF_PARAM4V_KEY, param4Value);
        editor.putString(PREF_PARAM5V_KEY, param5Value);
        editor.putString(PREF_PARAM6V_KEY, param6Value);
        editor.putString(PREF_ARROWSIZE_KEY, arrow_size);
        editor.putString(PREF_STEPMAX_KEY, stepMax);
        editor.putString(PREF_STEPMIN_KEY, stepMin);
        editor.putString(PREF_TIMEMAX_KEY, timeMax);
        editor.putString(PREF_TIMEMIN_KEY, timeMin);
        editor.putString(PREF_JAG_KEY, jag);
        editor.putBoolean(PREF_OUTSIDE_KEY, outside);
        editor.commit();
    }

    /**
     * This method creates the options menu - ie makes the help icon appear.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // This sets up the options on the app bar - the only option being the 'help' section
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * This method defines the behaviour of the help icon - opening the help activity.
     *
     * @param item  The menu item we're defining the behaviour of - currently there's only one in
     *              this activity
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // This opens the help menu when the only option item - the help option item - is selected
        startActivity(new Intent(this, HelpActivity.class));
        return true;
    }

    /**
     * This method checks for errors in the input fields, then if everything looks good, it makes
     * the graph activity appear, and passes it all the necessary information to show the graph and
     * do all the cool stuff it does.
     */
    public void graph(View view) {

        // Check that either steps and time aren't being used together for the same direction
        if (stepMaxET.hasFocus() && !stepMaxET.getText().equals("")) {
            timeMaxET.setText("");
        }
        if (stepMinET.hasFocus() && !stepMinET.getText().equals("")) {
            timeMinET.setText("");
        }
        if (timeMaxET.hasFocus() && !timeMaxET.getText().equals("")) {
            stepMaxET.setText("");
        }
        if (timeMinET.hasFocus() && !timeMinET.getText().equals("")) {
            stepMinET.setText("");
        }

        // Get strings (or booleans) from each input field
        String dxdt = dxdtEditText.getText().toString();
        String dydt = dydtEditText.getText().toString();
        String xmin = xminEditText.getText().toString();
        String xmax = xmaxEditText.getText().toString();
        String ymin = yminEditText.getText().toString();
        String ymax = ymaxEditText.getText().toString();
        String param1Symbol = param1SymbolET.getText().toString();
        String param2Symbol = param2SymbolET.getText().toString();
        String param3Symbol = param3SymbolET.getText().toString();
        String param4Symbol = param4SymbolET.getText().toString();
        String param5Symbol = param5SymbolET.getText().toString();
        String param6Symbol = param6SymbolET.getText().toString();
        String param1Value = param1ValueET.getText().toString();
        String param2Value = param2ValueET.getText().toString();
        String param3Value = param3ValueET.getText().toString();
        String param4Value = param4ValueET.getText().toString();
        String param5Value = param5ValueET.getText().toString();
        String param6Value = param6ValueET.getText().toString();
        String arrow_size = arrowSizeEditText.getText().toString();
        String stepMax = stepMaxET.getText().toString();
        String stepMin = stepMinET.getText().toString();
        String timeMax = timeMaxET.getText().toString();
        String timeMin = timeMinET.getText().toString();
        String jag = jagET.getText().toString();
        Boolean outside = outsideSw.isChecked();

        // Create intent that will be used below to start the GraphActivity activity
        Intent intent = new Intent(this, GraphActivity.class);

        // Turn the parameters into lists for easier handling before passing to intent
        String[] parameterSymbols = new String[]{param1Symbol, param2Symbol, param3Symbol,
                param4Symbol, param5Symbol, param6Symbol};
        double[] parameterValues = new double[]{Eval.eval(param1Value), Eval.eval(param2Value),
                Eval.eval(param3Value), Eval.eval(param4Value), Eval.eval(param5Value),
                Eval.eval(param6Value)};

        // Check for matched brackets in all text fields
        ArrayList<String> bfields = new ArrayList<String>(); // fields where brackets are mismatched
        if (!checkBrackets(dxdt)) { bfields.add("dx/dt"); }
        if (!checkBrackets(dydt)) { bfields.add("dy/dt"); }
        if (!checkBrackets(xmin)) { bfields.add("x_min"); }
        if (!checkBrackets(xmax)) { bfields.add("x_max"); }
        if (!checkBrackets(ymin)) { bfields.add("y_min"); }
        if (!checkBrackets(ymax)) { bfields.add("y_max"); }
        if (!checkBrackets(param1Value)) { bfields.add("parameter " + param1Symbol); }
        if (!checkBrackets(param2Value)) { bfields.add("parameter " + param2Symbol); }
        if (!checkBrackets(param3Value)) { bfields.add("parameter " + param3Symbol); }
        if (!checkBrackets(param4Value)) { bfields.add("parameter " + param4Symbol); }
        if (!checkBrackets(param5Value)) { bfields.add("parameter " + param5Symbol); }
        if (!checkBrackets(param6Value)) { bfields.add("parameter " + param6Symbol); }
        if (!checkBrackets(timeMax)) { bfields.add("the forwards time field"); }
        if (!checkBrackets(timeMin)) { bfields.add("the backwards time field"); }
        String[] bracket_mismatched_fields = bfields.toArray(new String[0]);

        if (bracket_mismatched_fields.length != 0) {
            String message = "There are mismatched brackets in ";
            message += toNaturalString(bracket_mismatched_fields);
            errorMessageTV.setText(message);
            showError();
            return;
        }

        // The following will only run if all brackets are paired correctly

        // Check that Eval.eval can parse the strings
        String unparsed = "";
        String u;
        u = toNaturalString(Eval.evalTest(dxdt, parameterSymbols).toArray(new String[0]));
        if (!u.equals("")) {unparsed += "\n - " + u + " in " + "dx/dt"; }
        u = toNaturalString(Eval.evalTest(dydt, parameterSymbols).toArray(new String[0]));
        if (!u.equals("")) {unparsed += "\n - " + u + " in " + "dy/dt"; }
        u = toNaturalString(Eval.evalTest(xmin, parameterSymbols).toArray(new String[0]));
        if (!u.equals("")) {unparsed += "\n - " + u + " in " + "x_min"; }
        u = toNaturalString(Eval.evalTest(xmax, parameterSymbols).toArray(new String[0]));
        if (!u.equals("")) {unparsed += "\n - " + u + " in " + "x_max"; }
        u = toNaturalString(Eval.evalTest(ymin, parameterSymbols).toArray(new String[0]));
        if (!u.equals("")) {unparsed += "\n - " + u + " in " + "y_min"; }
        u = toNaturalString(Eval.evalTest(ymax, parameterSymbols).toArray(new String[0]));
        if (!u.equals("")) {unparsed += "\n - " + u + " in " + "y_max"; }
        if (param1Value.equals("")) { u = ""; } else { u = toNaturalString(Eval.evalTest(param1Value).toArray(new String[0])); }
        if (!u.equals("")) {unparsed += "\n - " + u + " in " + "parameter " + param1Symbol; }
        if (param2Value.equals("")) { u = ""; } else { u = toNaturalString(Eval.evalTest(param2Value).toArray(new String[0])); }
        if (!u.equals("")) {unparsed += "\n - " + u + " in " + "parameter " + param2Symbol; }
        if (param3Value.equals("")) { u = ""; } else { u = toNaturalString(Eval.evalTest(param3Value).toArray(new String[0])); }
        if (!u.equals("")) {unparsed += "\n - " + u + " in " + "parameter " + param3Symbol; }
        if (param4Value.equals("")) { u = ""; } else { u = toNaturalString(Eval.evalTest(param4Value).toArray(new String[0])); }
        if (!u.equals("")) {unparsed += "\n - " + u + " in " + "parameter " + param4Symbol; }
        if (param5Value.equals("")) { u = ""; } else { u = toNaturalString(Eval.evalTest(param5Value).toArray(new String[0])); }
        if (!u.equals("")) {unparsed += "\n - " + u + " in " + "parameter " + param5Symbol; }
        if (param6Value.equals("")) { u = ""; } else { u = toNaturalString(Eval.evalTest(param6Value).toArray(new String[0])); }
        if (!u.equals("")) {unparsed += "\n - " + u + " in " + "parameter " + param6Symbol; }
        if (timeMax.equals("")) { u = ""; } else { u = toNaturalString(Eval.evalTest(timeMax).toArray(new String[0])); }
        if (!u.equals("")) {unparsed += "\n - " + u + " in " + "the forwards time field"; }
        if (timeMin.equals("")) { u = ""; } else { u = toNaturalString(Eval.evalTest(timeMin).toArray(new String[0])); }
        if (!u.equals("")) {unparsed += "\n - " + u + " in " + "the forwards time field"; }
        // (The integer cases)
        if (arrow_size.equals("")) { u = ""; } else { try { Integer.parseInt(arrow_size); u = ""; } catch (NumberFormatException e) {u = arrow_size; } }
        if (!u.equals("")) {unparsed += "\n - " + u + "in " + "the arrow size field (this should be an integer)"; }
        if (stepMax.equals("")) { u = ""; } else { try { Integer.parseInt(stepMax); u = ""; } catch (NumberFormatException e) {u = stepMax; } }
        if (!u.equals("")) {unparsed += "\n - " + u + "in " + "the forwards steps field (this should be an integer)"; }
        if (stepMin.equals("")) { u = ""; } else { try { Integer.parseInt(stepMin); u = ""; } catch (NumberFormatException e) {u = stepMin; } }
        if (!u.equals("")) {unparsed += "\n - " + u + "in " + "the backwards steps field (this should be an integer)"; }
        if (stepMin.equals("")) { u = ""; } else { try { Integer.parseInt(jag); u = ""; } catch (NumberFormatException e) {u = jag; } }
        if (!u.equals("")) {unparsed += "\n - " + u + "in " + "the jaggedness field (this should be an integer)"; }

        if (unparsed.length() != 0) {
            errorMessageTV.setText("The following expressions were not understood:" + unparsed);
            showError();
            return;
        }

        // The following will only run if all input syntax makes sense

        // Add these strings (and booleans) to the intent as extras
        intent.putExtra(EXTRA_DXDT_KEY, dxdt);
        intent.putExtra(EXTRA_DYDT_KEY, dydt);
        intent.putExtra(EXTRA_XMIN_KEY, xmin);
        intent.putExtra(EXTRA_XMAX_KEY, xmax);
        intent.putExtra(EXTRA_YMIN_KEY, ymin);
        intent.putExtra(EXTRA_YMAX_KEY, ymax);
        intent.putExtra(EXTRA_PARAMSYMBS_KEY, parameterSymbols);
        intent.putExtra(EXTRA_PARAMVALS_KEY, parameterValues);
        intent.putExtra(EXTRA_ARROWSIZE_KEY, arrow_size);
        intent.putExtra(EXTRA_STEPMAX_KEY, stepMax);
        intent.putExtra(EXTRA_STEPMIN_KEY, stepMin);
        intent.putExtra(EXTRA_TIMEMAX_KEY, timeMax);
        intent.putExtra(EXTRA_TIMEMIN_KEY, timeMin);
        intent.putExtra(EXTRA_JAG_KEY, jag);
        intent.putExtra(EXTRA_OUTSIDE_KEY, outside);

        // Start the GraphActivity activity
        startActivity(intent);
    }

    // EXAMPLES: The following methods are called by button presses, and set the input fields to
    // pre-determined values to show an example ODE.
    public void restoreDefaults(View view) {
        // Set the values
        dxdtEditText.setText("(x+1)*y*(x-2) + (y-1)*x*(y+2)*(y-2)");
        dydtEditText.setText("(y-1)*x*(y+2)*(y-2) - (x+1)*y*(x-2)");
        xminEditText.setText("-3");
        xmaxEditText.setText("3");
        yminEditText.setText("-4");
        ymaxEditText.setText("4");
        param1SymbolET.setText("a");
        param2SymbolET.setText("b");
        param3SymbolET.setText("c");
        param4SymbolET.setText("d");
        param5SymbolET.setText("f");
        param6SymbolET.setText("g");
        param1ValueET.setText("");
        param2ValueET.setText("");
        param3ValueET.setText("");
        param4ValueET.setText("");
        param5ValueET.setText("");
        param6ValueET.setText("");
        arrowSizeEditText.setText("60");
        stepMaxET.setText("700");
        stepMinET.setText("700");
        timeMaxET.setText("");
        timeMinET.setText("");
        jagET.setText("4");
        outsideSw.setChecked(false);
    }
    public void examplePendulum(View view) {
        // Set the values
        dxdtEditText.setText("y");
        dydtEditText.setText("- g * sin(x) / l");
        xminEditText.setText("-4");
        xmaxEditText.setText("4");
        yminEditText.setText("-12");
        ymaxEditText.setText("12");
        param1SymbolET.setText("l");
        param2SymbolET.setText("g");
        param3SymbolET.setText("");
        param4SymbolET.setText("");
        param5SymbolET.setText("");
        param6SymbolET.setText("");
        param1ValueET.setText("1");
        param2ValueET.setText("9.8");
        param3ValueET.setText("");
        param4ValueET.setText("");
        param5ValueET.setText("");
        param6ValueET.setText("");
        arrowSizeEditText.setText("60");
        stepMaxET.setText("700");
        stepMinET.setText("700");
        timeMaxET.setText("");
        timeMinET.setText("");
        outsideSw.setChecked(false);
    }
    public void examplePredatorPrey(View view) {
        // Set the values
        dxdtEditText.setText("x*(1 - x) - a*x*y/(x + c)");
        dydtEditText.setText("b*y*(1 - y/x)");
        xminEditText.setText("0");
        xmaxEditText.setText("1");
        yminEditText.setText("0");
        ymaxEditText.setText("2/3");
        param1SymbolET.setText("a");
        param2SymbolET.setText("b");
        param3SymbolET.setText("c");
        param4SymbolET.setText("");
        param5SymbolET.setText("");
        param6SymbolET.setText("");
        param1ValueET.setText("1");
        param2ValueET.setText("0.2");
        param3ValueET.setText("0.05");
        param4ValueET.setText("");
        param5ValueET.setText("");
        param6ValueET.setText("");
        arrowSizeEditText.setText("60");
        stepMaxET.setText("1000");
        stepMinET.setText("0");
        timeMaxET.setText("");
        timeMinET.setText("");
        outsideSw.setChecked(false);
    }
    public void exampleLinear(View view) {
        // Set the values
        dxdtEditText.setText("a*x + b*y");
        dydtEditText.setText("c*x + d*y");
        xminEditText.setText("-2");
        xmaxEditText.setText("2");
        yminEditText.setText("-3");
        ymaxEditText.setText("3");
        param1SymbolET.setText("a");
        param2SymbolET.setText("b");
        param3SymbolET.setText("c");
        param4SymbolET.setText("d");
        param5SymbolET.setText("");
        param6SymbolET.setText("");
        param1ValueET.setText("1/2");
        param2ValueET.setText("-1");
        param3ValueET.setText("1");
        param4ValueET.setText("-1");
        param5ValueET.setText("");
        param6ValueET.setText("");
        arrowSizeEditText.setText("60");
        stepMaxET.setText("700");
        stepMinET.setText("700");
        timeMaxET.setText("");
        timeMinET.setText("");
        outsideSw.setChecked(false);
    }

    /**
     * This method takes an array of strings and returns a string formatted as is natural to english
     * with no brackets, commas between elements, an and before the final element if there are two
     * or more elements and the oxford comma if there are three or more elements
     *
     * @param a     A list of strings that would make sense if listed in english with comma
     *              separation
     * @return      A natural english listing of the strings
     */
    private String toNaturalString(String[] a){
        if (a.length == 0) { return ""; }
        if (a.length == 1) { return a[0]; }
        if (a.length == 2) { return a[0] + " and " + a[1]; }
        String output = "";
        for (int i = 0; i < a.length - 1; i++) {
            output += a[i] + ", ";
        }
        output += "and " + a[a.length - 1];
        return output;
    }

    /**
     * This method checks that the brackets in the string form a meaningful pattern. (Technically
     * parentheses.
     *
     * @param a     A string to check the brackets of
     * @return      True if the brackets make sense, and false otherwise.
     */
    private boolean checkBrackets(String a) {
        int level = 0;
        for (int i = 0; i < a.length(); i++) {
            if (a.charAt(i) == '(') {
                level++;
            } else if (a.charAt(i) == ')') {
                level--;
            }
            if (level < 0) { return false; }
        }
        if (level == 0) { return true; }
        return false;
    }

    /**
     * Makes the error window pop up or disappear. The show method should only be called after
     * setting the text in the error window.
     *
     * TODO: turn this into showError(String title, String body, String Response1, String Response2,
     * todo  _?_ responseAction1, _?_ responseAction2)
     */
    private void showError() {
        errorView.setVisibility(View.VISIBLE);
        dxdtEditText.setEnabled(false);
        dydtEditText.setEnabled(false);
        xminEditText.setEnabled(false);
        xmaxEditText.setEnabled(false);
        yminEditText.setEnabled(false);
        ymaxEditText.setEnabled(false);
        param1SymbolET.setEnabled(false);
        param2SymbolET.setEnabled(false);
        param3SymbolET.setEnabled(false);
        param4SymbolET.setEnabled(false);
        param5SymbolET.setEnabled(false);
        param6SymbolET.setEnabled(false);
        param1ValueET.setEnabled(false);
        param2ValueET.setEnabled(false);
        param3ValueET.setEnabled(false);
        param4ValueET.setEnabled(false);
        param5ValueET.setEnabled(false);
        param6ValueET.setEnabled(false);
        arrowSizeEditText.setEnabled(false);
        stepMaxET.setEnabled(false);
        stepMinET.setEnabled(false);
        timeMaxET.setEnabled(false);
        timeMinET.setEnabled(false);
        outsideSw.setEnabled(false);
        k_eqn_xyab.enabled = false;
        k_int.enabled = false;
        k_abc.enabled = false;
    }
    public void hideError(View view) {
        errorView.setVisibility(View.GONE);
        dxdtEditText.setEnabled(true);
        dydtEditText.setEnabled(true);
        xminEditText.setEnabled(true);
        xmaxEditText.setEnabled(true);
        yminEditText.setEnabled(true);
        ymaxEditText.setEnabled(true);
        param1SymbolET.setEnabled(true);
        param2SymbolET.setEnabled(true);
        param3SymbolET.setEnabled(true);
        param4SymbolET.setEnabled(true);
        param5SymbolET.setEnabled(true);
        param6SymbolET.setEnabled(true);
        param1ValueET.setEnabled(true);
        param2ValueET.setEnabled(true);
        param3ValueET.setEnabled(true);
        param4ValueET.setEnabled(true);
        param5ValueET.setEnabled(true);
        param6ValueET.setEnabled(true);
        arrowSizeEditText.setEnabled(true);
        stepMaxET.setEnabled(true);
        stepMinET.setEnabled(true);
        timeMaxET.setEnabled(true);
        timeMinET.setEnabled(true);
        outsideSw.setEnabled(true);
        k_eqn_xyab.enabled = true;
        k_int.enabled = true;
        k_abc.enabled = true;
    }

    // TODO: Export figure options
}
