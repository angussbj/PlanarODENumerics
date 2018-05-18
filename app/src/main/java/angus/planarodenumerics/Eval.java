package angus.planarodenumerics;

import java.util.ArrayList;

public class Eval {

    /**
     * A way of not specifying x, y, or parameters if they're irrelevant. Everything else is as in eval
     */
    public static double eval(String s) {
        return eval(s, 0, 0, new String[]{}, new double[]{});
    }

    /**
     * A way of not specifying parameters if they're irrelevant. Everything else is as in evalTest
     */
    public static ArrayList<String> evalTest(String s) {
        return evalTest(s, new String[]{});
    }

    /**
     * This method is used to test whether eval will be able to work correctly, or whether it will
     * return zero for part of the funciton. IT IS IMPERATIVE THAT EVERY PART OF EVAL IS REFLECTED
     * IN EVALTEST AND VICE VERSA! An ArrayList of Strings is returned containing all the parts that
     * could not be parsed. If it is empty, everything's as it should be.
     *
     * @param s     The string we test
     * @param ps    The allowable parameter names the user has chosen
     * @return      A list containing unrecognised parts of the string
     */
    public static ArrayList<String> evalTest(String s, String[] ps) {
        s = s.replace(" ", "");

        // The order of operations: (), functions (eg. log), ^, * and /, + and -

        if (s.equals("")) {
            ArrayList<String> r = new ArrayList<String>();
            r.add("an empty expression");
            return r;
        }

        // Search for the right most, lowest precedence operator that's not in brackets
        int bracket_level_count = 0;
        for (int i = s.length() - 1; i >= 0; i--) {
            if (s.charAt(i) == '+' && bracket_level_count == 0) {
                ArrayList<String> r = evalTest(s.substring(0, i), ps);
                r.addAll(evalTest(s.substring(i+1), ps));
                return r;
            }
            if (s.charAt(i) == '-' && bracket_level_count == 0) {
                if (i > 1) {
                    ArrayList<String> r = evalTest(s.substring(0, i), ps);
                    r.addAll(evalTest(s.substring(i + 1), ps));
                    return r;
                } else {
                    return evalTest(s.substring(i + 1), ps);
                }
            }
            if (s.charAt(i) == ')') {
                bracket_level_count += 1;
            }
            if (s.charAt(i) == '(') {
                bracket_level_count -= 1;
            }
        }

        // Search for the right most, * or /
        bracket_level_count = 0;
        for (int i = s.length() - 1; i >= 0; i--) {
            if (s.charAt(i) == '*' && bracket_level_count == 0) {
                ArrayList<String> r = evalTest(s.substring(0, i), ps);
                r.addAll(evalTest(s.substring(i+1), ps));
                return r;
            }
            if (s.charAt(i) == '/' && bracket_level_count == 0) {
                ArrayList<String> r = evalTest(s.substring(0, i), ps);
                r.addAll(evalTest(s.substring(i+1), ps));
                return r;
            }
            if (s.charAt(i) == ')') {
                bracket_level_count += 1;
            }
            if (s.charAt(i) == '(') {
                bracket_level_count -= 1;
            }
        }

        // Search for the right most ^
        bracket_level_count = 0;
        for (int i = s.length() - 1; i >= 0; i--) {
            if (s.charAt(i) == '^' && bracket_level_count == 0) {
                ArrayList<String> r = evalTest(s.substring(0, i), ps);
                r.addAll(evalTest(s.substring(i+1), ps));
                return r;
            }
            if (s.charAt(i) == ')') {
                bracket_level_count += 1;
            }
            if (s.charAt(i) == '(') {
                bracket_level_count -= 1;
            }
        }

        if (s.length() == 0) {return new ArrayList<String>();}
        if (s.equals("x")) { return new ArrayList<String>();}
        if (s.equals("y")) { return new ArrayList<String>();}
        if (s.equals("pi") || s.equals("π")) { return new ArrayList<String>();}
        if (s.equals("e")) { return new ArrayList<String>();}
        try {
            Double.parseDouble(s);
            return new ArrayList<String>();
        } catch (NumberFormatException e) {}
        for (int i = 0; i < ps.length; i++) {
            if (s.equals(ps[i])) { return new ArrayList<String>(); }
        }

        // TODO: Juxtaposition multiplication for numbers, x, and y

        // De-bracket if entire expression is nested within brackets
        bracket_level_count = 0;
        if (s.charAt(0) == '(' && s.charAt(s.length()-1) == ')') {
            boolean debracket = true;
            for (int i = s.length() - 1; i >= 0; i--) {
                if (s.charAt(i) == ')') {
                    bracket_level_count += 1;
                }
                if (s.charAt(i) == '(') {
                    bracket_level_count -= 1;
                }
                if (bracket_level_count == 0 && i != 0 && i != s.length() - 1) {
                    debracket = false;
                    break;
                }
            }
            return evalTest(s.substring(1, s.length() - 1), ps);
        }

        // Search for the left most function
        // Ordering longest to shortest is important to avoid cosecx being evaluated as cos(ecx)
        if (s.length() > 5) {
            if (s.substring(0,5).equals("cosec")) {
                return evalTest(s.substring(5), ps);
            }
        }
        if (s.length() > 4) {
            String s4 = s.substring(0,4);
            if (s4.equals("sqrt")) {
                return evalTest(s.substring(4), ps);
            }
            if (s4.equals("sinh")) {
                return evalTest(s.substring(4), ps);
            }
            if (s4.equals("cosh")) {
                return evalTest(s.substring(4), ps);
            }
            if (s4.equals("tanh")) {
                return evalTest(s.substring(4), ps);
            }
            if (s4.equals("asin")) {
                return evalTest(s.substring(4), ps);
            }
            if (s4.equals("acos")) {
                return evalTest(s.substring(4), ps);
            }
            if (s4.equals("atan")) {
                return evalTest(s.substring(4), ps);
            }
        }
        if (s.length() > 3) {
            String s3 = s.substring(0,3);
            if (s3.equals("sin")) {
                return evalTest(s.substring(3), ps);
            }
            if (s3.equals("cos")) {
                return evalTest(s.substring(3), ps);
            }
            if (s3.equals("tan")) {
                return evalTest(s.substring(3), ps);
            }
            if (s3.equals("log")) {
                return evalTest(s.substring(3), ps);
            }
            if (s3.equals("abs")) {
                return evalTest(s.substring(3), ps);
            }
            if (s3.equals("cot")) {
                return evalTest(s.substring(3), ps);
            }
            if (s3.equals("sec")) {
                return evalTest(s.substring(3), ps);
            }
        }
        if (s.length() > 2) {
            if (s.substring(0,2).equals("ln")) {
                return evalTest(s.substring(2), ps);
            }
        }
        // TODO: functions that take two variables - eg logs with different bases, atan2
        /*
        // Search for the right most bracket juxtaposition (to be treated as multiplication)
        bracket_level_count = 0;
        for (int i = s.length() - 1; i >= 0; i--) {
            if (i != 0 && i != s.length() - 1 && bracket_level_count == 0) {
                ArrayList<String> r = evalTest(s.substring(0, i), ps);
                r.addAll(evalTest(s.substring(i+1), ps));

                return r;
            }
            if (s.charAt(i) == ')') {
                bracket_level_count += 1;
            }
            if (s.charAt(i) == '(') {
                bracket_level_count -= 1;
            }
        }
        */

        // None of the above options worked, so we need to add the string to the list of things
        // to complain about.
        ArrayList<String> r = new ArrayList<String>();
        r.add(s);
        return r;
    }

    /**
     * This method evaluates the expression given in the string s for the x and y values given by
     * the variables x and y, and interpreting the symbols given in ps as having the corresponding
     * value given in pv.
     *
     * IT IS ESSENTIAL THAT EVERY PART OF EVAL IS REFLECTED IN EVAL TEST AND VICE VERSA - BE VERY
     * CAREFUL WHEN CHANGING THINGS!!!
     *
     *
     * @param s     The string that should contain mathematical expressions in terms of x, y, and
     *              the strings given as parameter names in ps. Use evalTest to check that the
     *              string is acceptable. Unrecognised sections of strings will be given the value
     *              0 arbitrarily.
     * @param x     The value that will be substituted in place of x in the string s
     * @param y     The value that will be substituted in place of y in the string s
     * @param ps    Parameter symbols - the list of strings that are names of parameters that appear
     *              in s
     * @param pv    Parameter values - the list of values that will be substituted in place of the
     *              symbols in ps
     * @return      The value obtained by evaluating the expression in s with the values given
     */
    public static double eval(String s, double x, double y,  String[] ps, double[] pv) {
        //s = s.replace(" ", "");

        // The order of operations: (), functions (eg. log), ^, * and /, + and -

        // Search for the right most, lowest precedence operator that's not in brackets
        int bracket_level_count = 0;
        for (int i = s.length() - 1; i >= 0; i--) {
            if (s.charAt(i) == '+' && bracket_level_count == 0) {
                return eval(s.substring(0, i), x, y, ps, pv) + eval(s.substring(i+1), x, y, ps, pv);
            }
            if (s.charAt(i) == '-' && bracket_level_count == 0) {
                if (i > 0) {
                    return eval(s.substring(0, i), x, y, ps, pv) - eval(s.substring(i + 1), x, y, ps, pv);
                } else {
                    return -eval(s.substring(i + 1), x, y, ps, pv);
                }
            }
            if (s.charAt(i) == ')') {
                bracket_level_count += 1;
            }
            if (s.charAt(i) == '(') {
                bracket_level_count -= 1;
            }
        }

        // Search for the right most, * or /
        bracket_level_count = 0;
        for (int i = s.length() - 1; i >= 0; i--) {
            if (s.charAt(i) == '*' && bracket_level_count == 0) {
                return eval(s.substring(0, i), x, y, ps, pv) * eval(s.substring(i+1), x, y, ps, pv);
            }
            if (s.charAt(i) == '/' && bracket_level_count == 0) {
                return eval(s.substring(0, i), x, y, ps, pv) / eval(s.substring(i+1), x, y, ps, pv);
            }
            if (s.charAt(i) == ')') {
                bracket_level_count += 1;
            }
            if (s.charAt(i) == '(') {
                bracket_level_count -= 1;
            }
        }

        // Search for the right most ^
        bracket_level_count = 0;
        for (int i = s.length() - 1; i >= 0; i--) {
            if (s.charAt(i) == '^' && bracket_level_count == 0) {
                return Math.pow(eval(s.substring(0, i), x, y, ps, pv),
                        eval(s.substring(i+1), x, y, ps, pv));
            }
            if (s.charAt(i) == ')') {
                bracket_level_count += 1;
            }
            if (s.charAt(i) == '(') {
                bracket_level_count -= 1;
            }
        }

        if (s.length() == 0) {return 0;}
        if (s.equals("x")) {return x;}
        if (s.equals("y")) {return y;}
        if (s.equals("pi") || s.equals("π")) {return 3.141592653589793;}
        if (s.equals("e")) {return 2.718281828459045;}
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {}
        for (int i = 0; i < ps.length; i++) {
            if (s.equals(ps[i])) { return pv[i]; }
        }

        // TODO: Juxtaposition multiplication for numbers, x, and y

        // De-bracket if entire expression is nested within brackets
        bracket_level_count = 0;
        if (s.charAt(0) == '(' && s.charAt(s.length()-1) == ')') {
            boolean debracket = true;
            for (int i = s.length() - 1; i >= 0; i--) {
                if (s.charAt(i) == ')') {
                    bracket_level_count += 1;
                }
                if (s.charAt(i) == '(') {
                    bracket_level_count -= 1;
                }
                if (bracket_level_count == 0 && i != 0 && i != s.length() - 1) {
                    debracket = false;
                    break;
                }
            }
            return eval(s.substring(1, s.length() - 1), x, y, ps, pv);
        }

        // Search for the left most function
        // Ordering longest to shortest is important to avoid cosecx being evaluated as cos(ecx)
        if (s.length() > 5) {
            if (s.substring(0,5).equals("cosec")) {
                return 1 / Math.sin(eval(s.substring(5), x, y, ps, pv));
            }
        }
        if (s.length() > 4) {
            String s4 = s.substring(0,4);
            if (s4.equals("sqrt")) {
                return Math.sqrt(eval(s.substring(4), x, y, ps, pv));
            }
            if (s4.equals("sinh")) {
                return Math.sinh(eval(s.substring(4), x, y, ps, pv));
            }
            if (s4.equals("cosh")) {
                return Math.cosh(eval(s.substring(4), x, y, ps, pv));
            }
            if (s4.equals("tanh")) {
                return Math.tanh(eval(s.substring(4), x, y, ps, pv));
            }
            if (s4.equals("asin")) {
                return Math.asin(eval(s.substring(4), x, y, ps, pv));
            }
            if (s4.equals("acos")) {
                return Math.acos(eval(s.substring(4), x, y, ps, pv));
            }
            if (s4.equals("atan")) {
                return Math.atan(eval(s.substring(4), x, y, ps, pv));
            }
        }
        if (s.length() > 3) {
            String s3 = s.substring(0,3);
            if (s3.equals("sin")) {
                return Math.sin(eval(s.substring(3), x, y, ps, pv));
            }
            if (s3.equals("cos")) {
                return Math.cos(eval(s.substring(3), x, y, ps, pv));
            }
            if (s3.equals("tan")) {
                return Math.tan(eval(s.substring(3), x, y, ps, pv));
            }
            if (s3.equals("log")) {
                return Math.log(eval(s.substring(3), x, y, ps, pv));
            }
            if (s3.equals("abs")) {
                return Math.abs(eval(s.substring(3), x, y, ps, pv));
            }
            if (s3.equals("cot")) {
                return 1 / Math.tan(eval(s.substring(3), x, y, ps, pv));
            }
            if (s3.equals("sec")) {
                return 1 / Math.cos(eval(s.substring(3), x, y, ps, pv));
            }
        }
        if (s.length() > 2) {
            if (s.substring(0,2).equals("ln")) {
                return Math.log(eval(s.substring(2), x, y, ps, pv));
            }
        }
        // TODO: functions that take two variables - eg logs with different bases, atan2

        /*
        // Search for the right most bracket juxtaposition (to be treated as multiplication)
        bracket_level_count = 0;
        for (int i = s.length() - 1; i >= 0; i--) {
            if (i != 0 && i != s.length() - 1 && bracket_level_count == 0) {
                return eval(s.substring(0, i+1), x, y, ps, pv)
                        * eval(s.substring(i+1), x, y, ps, pv);
            }
            if (s.charAt(i) == ')') {
                bracket_level_count += 1;
            }
            if (s.charAt(i) == '(') {
                bracket_level_count -= 1;
            }
        }
        */

        //ERROR
        // TODO: Throw exception
        return 0;
    }
}
