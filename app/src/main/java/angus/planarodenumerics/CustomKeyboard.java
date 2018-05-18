package angus.planarodenumerics;

import android.app.Activity;
import android.graphics.Color;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.Editable;
import android.text.InputType;
import android.text.Layout;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

/**
 * This class determines the behaviours of the keyboards in this app.
 */
public class CustomKeyboard {

    private KeyboardView keyboardView;
    private Activity hostActivity;
    private Keyboard keyboardMain;
    private Keyboard keyboardEqnxyab;
    private Keyboard keyboardFns;
    private Keyboard keyboardLower;
    private Keyboard keyboardUpper;
    private Keyboard keyboardLowerLettersOnly;
    private Keyboard keyboardUpperLettersOnly;
    public boolean enabled;


    // The keyboard listener gives each keycode (given to it by a keypress) an action to perform
    private KeyboardView.OnKeyboardActionListener eqnKeyboardListener1 = new KeyboardView.OnKeyboardActionListener() {
        @Override
        public void onPress(int primaryCode) {

        }

        @Override
        public void onRelease(int primaryCode) {

        }

        /**
         * This is the method where stuff actually happens.
         *
         * @param primaryCode   The code of the key that was pressed to trigger this event if
         *                      multiple keys are pressed
         * @param keyCodes      The codes of all the keys being pressed
         */
        @Override
        public void onKey(int primaryCode, int[] keyCodes) {

            if (!enabled) { return; }

            // Get edit text and put its text in an editable
            View focusCurrent = hostActivity.getWindow().getCurrentFocus();
            EditText edittext = (EditText) focusCurrent;
            Editable editable = edittext.getText();
            int start = edittext.getSelectionStart();

            // Deal with the keypress
            switch (primaryCode) {
                case 1000:          // Delete
                    if (editable != null && start > 0) { editable.delete(start - 1, start); }
                    return;
                case 1001:          // Clear
                    if (editable != null) { editable.clear(); }
                    return;
                case 1002:          // Done
                    check_exclusions();
                    hostActivity.findViewById(R.id.focus_on_me).requestFocus();
                    return;
                case 1003:          // 123
                    keyboardView.setKeyboard(keyboardEqnxyab);
                    return;
                case 1004:          // f()
                    keyboardView.setKeyboard(keyboardFns);
                    return;
                case 1005:          // Aa
                    keyboardView.setKeyboard(keyboardLower);
                    return;
                case 1006:          // Aa SHIFT
                    keyboardView.setKeyboard(keyboardUpper);
                    return;
                case 1007:          // Aa SHIFT
                    keyboardView.setKeyboard(keyboardLowerLettersOnly);
                    return;
                case 1008:          // Aa SHIFT
                    keyboardView.setKeyboard(keyboardUpperLettersOnly);
                    return;
                case 1010:          // x
                    if (needsTimes(editable, start)) {
                        editable.insert(start, "*");
                        start += 1;
                    }
                    editable.insert(start, "x");
                    return;
                case 1011:          // y
                    if (needsTimes(editable, start)) {
                        editable.insert(start, "*");
                        start += 1;
                    }
                    editable.insert(start, "y");
                    return;
                case 40:            // (
                    if (needsTimes(editable, start)) {
                        editable.insert(start, "*");
                        start += 1;
                    }
                    editable.insert(start, "(");
                    return;
                case 1013:          // sin
                    if (needsTimes(editable, start)) {
                        editable.insert(start, "*");
                        start += 1;
                    }
                    editable.insert(start, "sin(");
                    return;
                case 1014:          // cos
                    if (needsTimes(editable, start)) {
                        editable.insert(start, "*");
                        start += 1;
                    }
                    editable.insert(start, "cos(");
                    return;
                case 1015:          // tan
                    if (needsTimes(editable, start)) {
                        editable.insert(start, "*");
                        start += 1;
                    }
                    editable.insert(start, "tan(");
                    return;
                case 1016:          // log
                    if (needsTimes(editable, start)) {
                        editable.insert(start, "*");
                        start += 1;
                    }
                    editable.insert(start, "log(");
                    return;
                case 1017:          // asin
                    if (needsTimes(editable, start)) {
                        editable.insert(start, "*");
                        start += 1;
                    }
                    editable.insert(start, "asin(");
                    return;
                case 1018:          // acos
                    if (needsTimes(editable, start)) {
                        editable.insert(start, "*");
                        start += 1;
                    }
                    editable.insert(start, "acos(");
                    return;
                case 1019:          // atan
                    if (needsTimes(editable, start)) {
                        editable.insert(start, "*");
                        start += 1;
                    }
                    editable.insert(start, "atan(");
                    return;
                case 1020:          // sec
                    if (needsTimes(editable, start)) {
                        editable.insert(start, "*");
                        start += 1;
                    }
                    editable.insert(start, "sec(");
                    return;
                case 1021:          // cosec
                    if (needsTimes(editable, start)) {
                        editable.insert(start, "*");
                        start += 1;
                    }
                    editable.insert(start, "cosec(");
                    return;
                case 1022:          // cot
                    if (needsTimes(editable, start)) {
                        editable.insert(start, "*");
                        start += 1;
                    }
                    editable.insert(start, "cot(");
                    return;
                case 1023:          // sinh
                    if (needsTimes(editable, start)) {
                        editable.insert(start, "*");
                        start += 1;
                    }
                    editable.insert(start, "sinh(");
                    return;
                case 1024:          // cosh
                    if (needsTimes(editable, start)) {
                        editable.insert(start, "*");
                        start += 1;
                    }
                    editable.insert(start, "cosh(");
                    return;
                case 1025:          // tanh
                    if (needsTimes(editable, start)) {
                        editable.insert(start, "*");
                        start += 1;
                    }
                    editable.insert(start, "tanh(");
                    return;
                case 1026:          // e
                    if (needsTimes(editable, start)) {
                        editable.insert(start, "*");
                        start += 1;
                    }
                    editable.insert(start, "e");
                    return;
                case 1027:          // pi
                    if (needsTimes(editable, start)) {
                        editable.insert(start, "*");
                        start += 1;
                    }
                    editable.insert(start, "pi");
                    return;
                case 1028:          // sqrt
                    if (needsTimes(editable, start)) {
                        editable.insert(start, "*");
                        start += 1;
                    }
                    editable.insert(start, "sqrt(");
                    return;
                case 1029:          // abs
                    if (needsTimes(editable, start)) {
                        editable.insert(start, "*");
                        start += 1;
                    }
                    editable.insert(start, "abs(");
                    return;
                case 1030:          // cursor left
                    if(start > 0) { edittext.setSelection(start - 1); }
                    return;
                case 1034:          // parameter a
                    if (needsTimes(editable, start)) {
                        editable.insert(start, "*");
                        start += 1;
                    }
                    editable.insert(start, "a");
                    return;
                case 1035:          // parameter b
                    if (needsTimes(editable, start)) {
                        editable.insert(start, "*");
                        start += 1;
                    }
                    editable.insert(start, "b");
                    return;
                case 43:            // +
                    editable.insert(start, " + ");
                    return;
                case 45:            // -
                    if (needsTimes(editable, start)) {
                        editable.insert(start, " - ");  // The a - b case
                    } else {
                        editable.insert(start, "-");    // The -x case
                    }
                    return;
                default:
                    editable.insert(start, Character.toString((char) primaryCode));
            }
        }

        @Override
        public void onText(CharSequence text) {

        }

        @Override
        public void swipeLeft() {

        }

        @Override
        public void swipeRight() {

        }

        @Override
        public void swipeDown() {

        }

        @Override
        public void swipeUp() {

        }
    };

    /**
     * This method creates an instance of this class for a particular rootLayout like the integer
     * keyboard, the usual maths keyboard, or the qwerty keyboard.
     *
     * @param host              The activity this keyboard will be used in.
     * @param keyboardViewId    The view the keyboard will be shown in
     * @param rootLayoutId      The keyboard that will first pop up when you tap a text field that
     *                          is registered with this keyboard.
     */
    CustomKeyboard(Activity host, int keyboardViewId, int rootLayoutId) {
        hostActivity = host;
        keyboardView = host.findViewById(keyboardViewId);
        keyboardView.setOnKeyboardActionListener(eqnKeyboardListener1);
        keyboardMain = new Keyboard(host, rootLayoutId);

        // Set up all the keyboard formats that we might use
        keyboardEqnxyab = new Keyboard(host, R.layout.keyboard);
        keyboardFns = new Keyboard(host, R.layout.keyboard_more_functions);
        keyboardLower = new Keyboard(host, R.layout.keyboard_lower);
        keyboardUpper = new Keyboard(host, R.layout.keyboard_upper);
        keyboardLowerLettersOnly = new Keyboard(host, R.layout.keyboard_lower_letters_only);
        keyboardUpperLettersOnly = new Keyboard(host, R.layout.keyboard_upper_letters_only);

        enabled = true;
    }

    /**
     * This method sets the keyboard to the root keyboard and makes it appear on the screen
     *
     * @param view  The view from which to hide the usual keyboard
     */
    private void showKeyboard(View view) {
        // Hide the usual keyboard
        if (view != null) {
            ((InputMethodManager) hostActivity.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        // Set and show the usual keyboard
        keyboardView.setKeyboard(keyboardMain);
        keyboardView.setVisibility(View.VISIBLE);
        keyboardView.setEnabled(true);

        // Make sure the cursor is visible
        EditText et = (EditText) hostActivity.getWindow().getCurrentFocus();
        if (et != null) { et.setCursorVisible(true); }
    }

    /**
     * Hides the keyboard and cursor TODO: throw focus to the focusonme button so that doesn't need to happen wherever it's currently happening?
     */
    public void hideKeyboard() {
        keyboardView.setVisibility(View.GONE);
        keyboardView.setEnabled(false);
        EditText et = (EditText) hostActivity.getWindow().getCurrentFocus();
        if (et != null) { et.setCursorVisible(false); }
    }

    /**
     * This method tells a keypress whether it should type "x" (false) or "*x" (true) when the x key
     * (or another key that indicates the beginning of an expression) is pressed. This is how the
     * user can type "2xy" and have the text field show "2*x*y" which Eval.eval can understand.
     *
     * @param e         The editable containing the text in the text field being edited
     * @param cursor    The cursors position in the editable
     * @return          True if "*" should precede the symobol typed for eval to be able to
     *                  understand it and false otherwise.
     */
    private boolean needsTimes(Editable e, int cursor) {
        if (cursor == 0) { return false; }
        String s = e.toString();
        s = s.substring(0, cursor);
        if (s.length() == 0) { return false; }
        String a = s.substring(s.length() - 1);
        while (a.equals(" ")) {
            s = s.substring(0, s.length() - 1);
            if (s.length() == 0) { return false; }
            a = s.substring(s.length() - 1);
        }
        if (a.equals("(")) { return false; }
        if (a.equals("+")) { return false; }
        if (a.equals("-")) { return false; }
        if (a.equals("*")) { return false; }
        if (a.equals("/")) { return false; }
        if (a.equals("^")) { return false; }
        if (s.length() == 1) { return true; }
        if (s.substring(s.length() - 2).equals("ln")) { return false; }
        if (s.length() == 2) { return true; }
        a = s.substring(s.length() - 3);
        if (a.equals("sin")) { return false; }
        if (a.equals("cos")) { return false; }
        if (a.equals("tan")) { return false; }
        if (a.equals("log")) { return false; }
        if (a.equals("sec")) { return false; }
        if (a.equals("cot")) { return false; }
        if (a.equals("abs")) { return false; }
        if (s.length() == 3) { return true; }
        a = s.substring(s.length() - 4);
        if (a.equals("sqrt")) { return false; }
        if (a.equals("sinh")) { return false; }
        if (a.equals("cosh")) { return false; }
        if (a.equals("tanh")) { return false; }
        if (a.equals("asin")) { return false; }
        if (a.equals("acos")) { return false; }
        if (a.equals("atan")) { return false; }
        if (s.length() == 4) { return true; }
        if (s.substring(s.length() - 5).equals("cosec")) { return false; }
        return true;
    }

    /**
     * This method is called when the done button is pressed. There are some pairs of text fields
     * where only one should have text at any point in time. If the user has just filled one such
     * text field, this method empties the other.
     */
    public void check_exclusions() {
        int focusId = hostActivity.getWindow().getCurrentFocus().getId();
        if (focusId == R.id.maxNegativeStepsEditText){
            if (!((EditText) hostActivity.findViewById(focusId)).getText().toString().equals("")) {
                ((EditText) hostActivity.findViewById(R.id.maxNegativeTimeEditText)).setText("");
            }
        }
        if (focusId == R.id.maxNegativeTimeEditText){
            if (!((EditText) hostActivity.findViewById(focusId)).getText().toString().equals("")) {
                ((EditText) hostActivity.findViewById(R.id.maxNegativeStepsEditText)).setText("");
            }
        }
        if (focusId == R.id.maxPositiveStepsEditText){
            if (!((EditText) hostActivity.findViewById(focusId)).getText().toString().equals("")) {
                ((EditText) hostActivity.findViewById(R.id.maxPositiveTimeEditText)).setText("");
            }
        }
        if (focusId == R.id.maxPositiveTimeEditText){
            if (!((EditText) hostActivity.findViewById(focusId)).getText().toString().equals("")) {
                ((EditText) hostActivity.findViewById(R.id.maxPositiveStepsEditText)).setText("");
            }
        }
    }

    /**
     * This method ensures that the edit text it's called on has this keyboard used as it's main
     * input method. It also sets up the exclusions so text fields that shouldn't be full at the
     * same time can be emptied by changing focus where appropriate.
     *
     * @param e             The editText field to use this keyboard
     * @param exclusion     The editText field that should be empty if this is full
     */
    public void registerEditText(EditText e, EditText exclusion) {
        final EditText et = e;
        final EditText ex = exclusion;
        e.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    showKeyboard(view);
                } else {
                    hideKeyboard();
                    if (!et.getText().toString().equals("")) {
                        ex.setText("");
                    }
                }
            }
        });
        e.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showKeyboard(view);
            }
        });
        e.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                // We want the input type to still be text, but not while we do the onTouch, so we
                // don't end up with an extra keyboard
                EditText editText = (EditText) view;
                int inType = editText.getInputType();
                editText.setInputType(InputType.TYPE_NULL);
                editText.onTouchEvent(event);
                editText.setInputType(inType);

                // Put the cursor in the right place
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Layout layout = editText.getLayout();
                    float x = event.getX() + editText.getScrollX();
                    int offset = layout.getOffsetForHorizontal(0, x);
                    if (offset > 0)
                        if (x > layout.getLineMax(0))
                            editText.setSelection(offset);
                        else
                            editText.setSelection(offset - 1);
                }
                return true;
            }
        });

    }

    /**
     * This method ensures that the edit text it's called on has this keyboard used as it's main
     * input method.
     *
     * @param e             The editText field to use this keyboard
     */
    public void registerEditText(EditText e) {
        e.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    showKeyboard(view);
                } else {
                    hideKeyboard();
                }
            }
        });
        e.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showKeyboard(view);
            }
        });
        e.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                // We want the input type to still be text, but not while we do the onTouch, so we
                // don't end up with an extra keyboard
                EditText editText = (EditText) view;
                int inType = editText.getInputType();
                editText.setInputType(InputType.TYPE_NULL);
                editText.onTouchEvent(event);
                editText.setInputType(inType);

                // Put the cursor in the right place
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Layout layout = editText.getLayout();
                    float x = event.getX() + editText.getScrollX();
                    int offset = layout.getOffsetForHorizontal(0, x);
                    if (offset > 0)
                        if (x > layout.getLineMax(0))
                            editText.setSelection(offset);
                        else
                            editText.setSelection(offset - 1);
                }
                return true;
            }
        });
    }
}
