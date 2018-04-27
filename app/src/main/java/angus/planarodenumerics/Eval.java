package angus.planarodenumerics;

public class Eval {

    public static double eval(String s, double x, double y) {
        //s = s.replace(" ", "");

        // The order of operations: (), functions (eg. log), ^, * and /, + and -

        // Search for the right most, lowest precedence operator that's not in brackets
        int bracket_level_count = 0;
        for (int i = s.length() - 1; i >= 0; i--) {
            if (s.charAt(i) == '+' && bracket_level_count == 0) {
                return eval(s.substring(0, i), x, y) + eval(s.substring(i+1), x, y);
            }
            if (s.charAt(i) == '-' && bracket_level_count == 0) {
                return eval(s.substring(0, i), x, y) - eval(s.substring(i+1), x, y);
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
                return eval(s.substring(0, i), x, y) * eval(s.substring(i+1), x, y);
            }
            if (s.charAt(i) == '/' && bracket_level_count == 0) {
                return eval(s.substring(0, i), x, y) / eval(s.substring(i+1), x, y);
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
                return Math.pow(eval(s.substring(0, i), x, y), eval(s.substring(i+1), x, y));
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
        if (s.equals("pi")) {return 3.141592653589793;}
        if (s.equals("e")) {return 2.718281828459045;}
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {}

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
            return eval(s.substring(1, s.length() - 1), x, y);
        }

        // Search for the left most function
        // Ordering longest to shortest is important to avoid cosecx being evaluated as cos(ecx)
        if (s.length() > 5) {
            if (s.substring(0,5).equals("cosec")) {
                return 1 / Math.sin(eval(s.substring(5), x, y));
            }
        }
        if (s.length() > 4) {
            String s4 = s.substring(0,4);
            if (s4.equals("sqrt")) {
                return Math.sqrt(eval(s.substring(4), x, y));
            }
            if (s4.equals("sinh")) {
                return Math.sinh(eval(s.substring(4), x, y));
            }
            if (s4.equals("cosh")) {
                return Math.cosh(eval(s.substring(4), x, y));
            }
            if (s4.equals("tanh")) {
                return Math.tanh(eval(s.substring(4), x, y));
            }
            if (s4.equals("asin")) {
                return Math.asin(eval(s.substring(4), x, y));
            }
            if (s4.equals("acos")) {
                return Math.acos(eval(s.substring(4), x, y));
            }
            if (s4.equals("atan")) {
                return Math.atan(eval(s.substring(4), x, y));
            }
        }
        if (s.length() > 3) {
            String s3 = s.substring(0,3);
            if (s3.equals("sin")) {
                return Math.sin(eval(s.substring(3), x, y));
            }
            if (s3.equals("cos")) {
                return Math.cos(eval(s.substring(3), x, y));
            }
            if (s3.equals("tan")) {
                return Math.tan(eval(s.substring(3), x, y));
            }
            if (s3.equals("log")) {
                return Math.log(eval(s.substring(3), x, y));
            }
            if (s3.equals("abs")) {
                return Math.abs(eval(s.substring(3), x, y));
            }
            if (s3.equals("cot")) {
                return 1 / Math.tan(eval(s.substring(3), x, y));
            }
            if (s3.equals("sec")) {
                return 1 / Math.cos(eval(s.substring(3), x, y));
            }
        }
        if (s.length() > 2) {
            if (s.substring(0,2).equals("ln")) {
                return Math.log(eval(s.substring(2), x, y));
            }
        }
        // TODO: functions that take two variables - eg logs with different bases, atan2

        // Search for the right most bracket juxtaposition (to be treated as multiplication)
        bracket_level_count = 0;
        for (int i = s.length() - 1; i >= 0; i--) {
            if (i != 0 && i != s.length() - 1 && bracket_level_count == 0) {
                return eval(s.substring(0, i+1), x, y) * eval(s.substring(i+1), x, y);
            }
            if (s.charAt(i) == ')') {
                bracket_level_count += 1;
            }
            if (s.charAt(i) == '(') {
                bracket_level_count -= 1;
            }
        }

        //ERROR
        // TODO: Throw exception
        return 0;
    }
}
