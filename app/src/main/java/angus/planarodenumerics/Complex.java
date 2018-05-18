package angus.planarodenumerics;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * This class is an implementation of complex numbers using doubles to store the real and imaginary
 * parts. All calculations are done in cartesian form except the sqrt function.
 *
 * TODO: Add a power function for completeness
 */
public class Complex {
    double re;
    double im;

    public Complex(double a) {
        re = a;
        im = 0;
    }

    public Complex(double a, double b) {
        re = a;
        im = b;
    }

    public Complex neg() {
        return new Complex(-re, -im);
    }

    public Complex plus(double x) {
        return new Complex(re + x, im);
    }

    public Complex plus(Complex z) {
        return new Complex(re + z.re, im + z.im);
    }

    public Complex minus(double x) {
        return new Complex(re - x, im);
    }

    public Complex minus(Complex z) {
        return new Complex(re - z.re, im - z.im);
    }

    public Complex squared() {
        return new Complex(re*re - im*im, 2*re*im);
    }

    public Complex times(double x) {
        return new Complex(re * x, im * x);
    }

    public Complex times(Complex z) {
        return new Complex(re*z.re - im*z.im, re*z.im + im*z.re);
    }

    public Complex conjugate() {
        return new Complex(re, -im);
    }

    public double modulus_squared() {
        return im*im + re*re;
    }

    public double modulus() {
        return Math.sqrt(modulus_squared());
    }

    public double abs() { return modulus(); }

    public double argument() {
        return Math.atan2(im, re);
    }

    public Complex div(double x) {
        return new Complex(re/x, im/x);
    }

    public Complex div(Complex z) {
        return this.times(z.conjugate()).div(z.modulus_squared());
    }

    public Complex recip() {
        return (new Complex(1)).div(this);
    }

    public Complex sqrt() {
        double new_mod = Math.sqrt(this.modulus());
        double new_arg = this.argument()/2;
        return new Complex(new_mod * Math.cos(new_arg), new_mod * Math.sin(new_arg));
    }

    public boolean equals(Complex z) {
        return (re == z.re && im == z.im);
    }

    public boolean equals(double x) {
        return (re == x && im == 0);
    }

    public String toString(int precision) {
        MathContext mathContext = new MathContext(precision, RoundingMode.HALF_UP);
        BigDecimal a = new BigDecimal(re, mathContext);
        BigDecimal b = new BigDecimal(im, mathContext);
        if (Math.abs(im) < Math.pow(10, -precision + 1)) { return a.toString(); }
        if (im < 0)  { return a.toString() + " - " + b.abs().toString() + "i"; }
        return a.toString() + " + " + b.toString() + "i";
    }

    public String toString() {
        if (im == 0) { return Double.toString(re); }
        if (im < 0)  { return Double.toString(re) + " - " + Double.toString(Math.abs(im)) + "i"; }
        return Double.toString(re) + " + " + Double.toString(im) + "i";
    }
}
