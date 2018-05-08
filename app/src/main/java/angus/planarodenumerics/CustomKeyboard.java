package angus.planarodenumerics;

import android.app.Activity;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.Editable;
import android.text.InputType;
import android.text.Layout;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class EqnKeyboard {

    private KeyboardView keyboardView;
    private Activity hostActivity;
    private Keyboard keyboard;

    // The keyboard listener
    private KeyboardView.OnKeyboardActionListener eqnKeyboardListener1 = new KeyboardView.OnKeyboardActionListener() {
        @Override
        public void onPress(int primaryCode) {

        }

        @Override
        public void onRelease(int primaryCode) {

        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {

            // Get edit text and put its text in an editable
            View focusCurrent = hostActivity.getWindow().getCurrentFocus();
            /*
            if (focusCurrent == null || focusCurrent.getClass() != EditText.class) {
                System.out.println(focusCurrent);
                return;
            }
            */
            EditText edittext = (EditText) focusCurrent;
            Editable editable = edittext.getText();
            int start = edittext.getSelectionStart();

            // Deal with the keypress
            switch (primaryCode) {
                case 100:
                    System.out.println("Go to more functions keyboard");
                    return;
                case 101:
                    System.out.println("Go to normal keyboard");
                    return;
                case 102:
                    hideKeyboard(edittext);
                    return;
                case -5:            // Delete
                    if (editable != null && start > 0) { editable.delete(start - 1, start); }
                    return;
                case 55006:         // Clear
                    if (editable != null) { editable.clear(); }
                    return;
                case 40:            // (
                    if (needsTimes(editable)) {
                        editable.insert(start, "*");
                        start += 1;
                    }
                    editable.insert(start, "(");
                    return;
                case 18:            // x
                    if (needsTimes(editable)) {
                        editable.insert(start, "*");
                        start += 1;
                    }
                    editable.insert(start, "x");
                    return;
                case 19:            // y
                    if (needsTimes(editable)) {
                        editable.insert(start, "*");
                        start += 1;
                    }
                    editable.insert(start, "y");
                    return;
                case 22:            // sin
                    if (needsTimes(editable)) {
                        editable.insert(start, "*");
                        start += 1;
                    }
                    editable.insert(start, "sin");
                    return;
                case 23:            // cos
                    if (needsTimes(editable)) {
                        editable.insert(start, "*");
                        start += 1;
                    }
                    editable.insert(start, "cos");
                    return;
                case 24:            // tan
                    if (needsTimes(editable)) {
                        editable.insert(start, "*");
                        start += 1;
                    }
                    editable.insert(start, "tan");
                    return;
                case 25:            // log
                    if (needsTimes(editable)) {
                        editable.insert(start, "*");
                        start += 1;
                    }
                    editable.insert(start, "log");
                    return;
                case 43:            // +
                    editable.insert(start, " + ");
                    return;
                case 45:            // -
                    editable.insert(start, " - ");
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

    EqnKeyboard(Activity host, View v, Layout l) {
        hostActivity = host;
        keyboard = new Keyboard(host, R.layout.keyboard);
        keyboardView = host.findViewById(R.id.keyboardview);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setOnKeyboardActionListener(eqnKeyboardListener1);
    }

    private void showKeyboard(View view) {
        if (view != null) {
            ((InputMethodManager) hostActivity.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        keyboardView.setVisibility(View.VISIBLE);
        keyboardView.setEnabled(true);
    }

    private void hideKeyboard(View view) {
        System.out.println("Hello, keyboard?");
        keyboardView.setVisibility(View.GONE);
        keyboardView.setEnabled(false);
    }

    private boolean needsTimes(Editable e) {
        String s = e.toString();
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

    public void registerEditText(EditText e) {
        e.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    showKeyboard(view);
                } else {
                    hideKeyboard(view);
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
