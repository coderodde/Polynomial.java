package com.github.coderodde.math;

import java.util.Arrays;

/**
 * This class implements a polynomial.
 * 
 * @version 1.0.0 (Nov 21, 2024)
 * @since 1.0.0 (Nov 21, 2024)
 */
public final class Polynomial {
    
    /**
     * The actual array storing coefficients. {@code coefficients[i]} is the 
     * coefficient of the term with degree {@code i}, so the constant term 
     * appears at {@code coefficients[0]}.
     */
    private final double[] coefficients;
    
    /**
     * Constructs this polynomial from the input coefficients. The lowest degree
     * coefficients is in {@code coefficients[0]} and so on.
     * 
     * @param coefficients the array of coefficients. Each array component must
     *                     be a real number.
     */
    public Polynomial(double... coefficients) {
        int i;

        for (i = coefficients.length - 1; i >= 0; i--) {
            if (coefficients[i] != 0.0) {
                break;
            }
        }

        if (i == -1) {
            // Special case: a "null" polynomial, we convert it to y = 0.
            this.coefficients = new double[]{ 0.0 };
        } else {
            this.coefficients = new double[i + 1];

            System.arraycopy(coefficients,
                             0,
                             this.coefficients,
                             0, 
                             this.coefficients.length);
        
            validateCoefficients();
        }
    }
    
    /**
     * Evaluates this polynomial at the point {@code x}.
     * 
     * @param x the argument value for this polynomial.
     * 
     * @return the value of this polynomial at the specified {@code x}
     *         coordinate.
     */
    public double evaluate(final double x) {
        validateX(x);
        
        double value = 0.0;
        
        for (int pow = 0; pow < coefficients.length; pow++) {
            value += coefficients[pow] * Math.pow(x, pow);
        }
        
        return value;
    }
    
    /**
     * Gets the {@code coefficientIndex}th coefficient.
     * 
     * @param coefficientIndex the index of the target coefficient.
     * 
     * @return                 the target coefficient.
     */
    public double getCoefficient(final int coefficientIndex) {
        try {
            return coefficients[coefficientIndex];
        } catch (final ArrayIndexOutOfBoundsException ex) {
            final String exceptionMessage = 
                    String.format(
                            "coefficientIndex[%d] is out of " + 
                            "valid bounds [0, %d)", 
                            coefficientIndex, 
                            coefficients.length);
            
            throw new IllegalArgumentException(exceptionMessage, ex);
        }
    }
    
    /**
     * Gets the number of coefficients in this polynomial.
     * 
     * @return the number of coefficients.
     */
    public int length() {
        return coefficients.length;
    }
    
    /**
     * Constructs and returns an instance of {@link Polynomial} that is the 
     * summation of {@code this} polynomial and {@code othr}.
     * 
     * @param othr the second polynomial to sum.
     * 
     * @return the sum of {@code this} and {@code othr} polynomials.
     */
    public Polynomial sum(final Polynomial othr) {
        final int thisPolynomialLength = this.length();
        final int othrPolynomialLength = othr.length();
        
        final int longestPolynomialLength = Math.max(thisPolynomialLength, 
                                                     othrPolynomialLength);
        
        final double[] sumCoefficients = new double[longestPolynomialLength];
        
        final Polynomial shrtPolynomial;
        final Polynomial longPolynomial;
        
        if (thisPolynomialLength <= othrPolynomialLength) {
            shrtPolynomial = this;
            longPolynomial = othr;
        } else {
            shrtPolynomial = othr;
            longPolynomial = this;
        }
        
        int coefficientIndex = 0;
        
        for (; coefficientIndex < shrtPolynomial.length();
               coefficientIndex++) {
            
            sumCoefficients[coefficientIndex] += 
                    shrtPolynomial.getCoefficient(coefficientIndex) + 
                    longPolynomial.getCoefficient(coefficientIndex);
        }
        
        for (; coefficientIndex < longPolynomial.length(); 
               coefficientIndex++) {
            
            sumCoefficients[coefficientIndex] = 
                    longPolynomial.getCoefficient(coefficientIndex);
        }
        
        return new Polynomial(sumCoefficients);
    }
    
    /**
     * Returns the degree of this polynomial.
     * 
     * @return the degree of this polynomial.
     */
    public int getDegree() {
        return coefficients.length - 1;
    }
    
    /**
     * Constructs and returns an instance of {@link Polynomial} that is the 
     * multiplication of {@code this} and {@code othr} polynomials.
     * 
     * @param othr the second polynomial to multiply.
     * 
     * @return the product of this and the input polynomials. 
     */
    public Polynomial multiply(final Polynomial othr) {
        final int coefficientArrayLength = this.getDegree() 
                                         * othr.getDegree() 
                                         + 1;
        
        final double[] coefficientArray = new double[coefficientArrayLength];
        int coefficientIndex = 0;
        
        for (int index1 = 0; 
                 index1 < this.length();
                 index1++) {
            
            for (int index2 = 0;
                     index2 < othr.length();
                     index2++) {
                
                coefficientArray[coefficientIndex++] =
                        this.getCoefficient(index1) *
                        othr.getCoefficient(index2);
            }
        }
        
        return new Polynomial(coefficientArray);
    }
    
    private void validateCoefficients() {
        for (int i = 0; i < coefficients.length; i++) {
            validateCoefficient(i);
        }
    }
    
    private void validateCoefficient(final int coefficientIndex) {
        final double coefficient = coefficients[coefficientIndex];
        
        if (Double.isNaN(coefficient)) {
            final String exceptionMessage = 
                    String.format("The coefficients[%d] is NaN.",
                                  coefficientIndex);
            
            throw new IllegalArgumentException(exceptionMessage);
        }
        
        if (Double.isInfinite(coefficient)) {
            final String exceptionMessage =
                    String.format("The coefficient[%d] is infinite: %f",
                                  coefficientIndex,
                                  coefficient);
            
            throw new IllegalArgumentException(exceptionMessage);
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        
        if (o == null) {
            return false;
        }
        
        if (!getClass().equals(o.getClass())) {
            return false;
        }
        
        final Polynomial othr = (Polynomial) o;
        
        return Arrays.equals(this.coefficients, 
                             othr.coefficients);
    }

    @Override
    public int hashCode() {
        // Generated by NetBeans 23:
        int hash = 3;
        hash = 43 * hash + Arrays.hashCode(coefficients);
        return hash;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        
        boolean first = true;
        
        for (int pow = getDegree(); pow >= 0; pow--) {
            if (first) {
                first = false;
                sb.append(getCoefficient(pow))
                  .append("^")
                  .append(pow);
            } else {
                final double coefficient = getCoefficient(pow);
                
                if (coefficient > 0.0) {
                    sb.append(" + ")
                      .append(coefficient);
                } else if (coefficient < 0.0) {
                    sb.append(" - ")
                      .append(Math.abs(coefficient));
                } else {
                    // Once here, there is no term with degree pow:
                    continue;
                }
                
                if (pow > 0) {
                    sb.append("^")
                      .append(pow);
                }
            }
        }
        
        return sb.toString();
    }
    
    private void validateX(final double x) {
        
        if (Double.isNaN(x)) {
            throw new IllegalArgumentException("x is NaN.");
        }
        
        if (Double.isInfinite(x)) {
            
            final String exceptionMessage =
                    String.format("x is infinite: %f", x);
            
            throw new IllegalArgumentException(exceptionMessage);
        }
    }
}
