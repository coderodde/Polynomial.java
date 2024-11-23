package com.github.coderodde.math;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This class implements a polynomial represented in coefficient form.
 * 
 * @version 2.1.0 (Nov 23, 2024)
 * @since 1.0.0 (Nov 21, 2024)
 */
public final class Polynomial {
    
    /**
     * Maps each digit character to its superscript counterpart.
     */
    private static final Map<Character, Character>
            MAP_DIGIT_CHARACTER_TO_SUPERSCRIPT = new HashMap<>(10);
    
    static {
        MAP_DIGIT_CHARACTER_TO_SUPERSCRIPT.put('2', '²');
        MAP_DIGIT_CHARACTER_TO_SUPERSCRIPT.put('3', '³');
        MAP_DIGIT_CHARACTER_TO_SUPERSCRIPT.put('4', '\u2074');
        MAP_DIGIT_CHARACTER_TO_SUPERSCRIPT.put('5', '\u2075');
        MAP_DIGIT_CHARACTER_TO_SUPERSCRIPT.put('6', '\u2076');
        MAP_DIGIT_CHARACTER_TO_SUPERSCRIPT.put('7', '\u2077');
        MAP_DIGIT_CHARACTER_TO_SUPERSCRIPT.put('8', '\u2078');  
        MAP_DIGIT_CHARACTER_TO_SUPERSCRIPT.put('9', '\u2079');   
    }
    
    /**
     * The actual array storing coefficients. {@code coefficients[i]} is the 
     * coefficient of the term with degree {@code i}, so the constant term 
     * appears at {@code coefficients[0]}.
     */
    final BigDecimal[] coefficients;
    
    private final int degree;
     
    /**
     * Constructs a polynomial with given coefficients. The coefficients of the 
     * constant term is {@code coefficients[0]} and the coefficient of the 
     * highest degree term is {@code coefficients[coefficients.length - 1]}.
     * 
     * @param coefficients the array of coefficients.
     */
    public Polynomial(final BigDecimal... coefficients) {
        if (coefficients.length == 0) {
            // Special case: the null polynomial y = 0:
            this.coefficients = new BigDecimal[]{ BigDecimal.ZERO };
            this.degree = 0;
        } else {
            this.coefficients = coefficients;
            this.degree = computeDegree();
        }
    }
    
    /**
     * Evaluates this polynomial at the point {@code x} using Horner's rule.
     * 
     * @param x the argument value for this polynomial.
     * 
     * @return the value of this polynomial at the specified {@code x}
     *         coordinate.
     */
    public BigDecimal evaluate(final BigDecimal x) {
        Objects.requireNonNull(x, "The x coordinate is null");
        BigDecimal value = coefficients[coefficients.length - 1];
        
        for (int i = coefficients.length - 2; i >= 0; i--) {
            value = value.multiply(x).add(coefficients[i]);
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
    public BigDecimal getCoefficient(final int coefficientIndex) {
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
        return degree + 1;
    }
    
    /**
     * Constructs and returns an instance of {@link Polynomial} that is the 
     * summation of {@code this} polynomial and {@code othr}.
     * 
     * @param otherPolynomial the second polynomial to sum.
     * 
     * @return the sum of {@code this} and {@code othr} polynomials.
     */
    public Polynomial sum(final Polynomial otherPolynomial) {
        final int thisPolynomialLength = this.length();
        final int othrPolynomialLength = otherPolynomial.length();
        
        final int longestPolynomialLength = Math.max(thisPolynomialLength, 
                                                     othrPolynomialLength);
        
        final BigDecimal[] sumCoefficients = 
                new BigDecimal[longestPolynomialLength];
        
        // Initialize the result coefficients:
        Arrays.fill(
                sumCoefficients, 
                0, 
                sumCoefficients.length, 
                BigDecimal.ZERO);
        
        final Polynomial shortPolynomial;
        final Polynomial longPolynomial;
        
        if (thisPolynomialLength <= othrPolynomialLength) {
            shortPolynomial = this;
            longPolynomial  = otherPolynomial;
        } else {
            shortPolynomial = otherPolynomial;
            longPolynomial  = this;
        }
        
        int coefficientIndex = 0;
        
        for (; coefficientIndex < shortPolynomial.length();
               coefficientIndex++) {
            
            sumCoefficients[coefficientIndex] = 
                sumCoefficients[coefficientIndex]
                    .add(shortPolynomial.getCoefficient(coefficientIndex)
                    .add(longPolynomial .getCoefficient(coefficientIndex)));
        }
        
        for (; coefficientIndex < longPolynomial.length(); 
               coefficientIndex++) {
            
            sumCoefficients[coefficientIndex] = 
                    longPolynomial.getCoefficient(coefficientIndex);
        }
        
        return new Polynomial(sumCoefficients);
    }
    
    /**
     * Returns the scale of this polynomial. This is essentially the scale of 
     * each coefficient {@link BigDecimal} in the coefficient array.
     * 
     * @return the scale of this polynomial.
     */
    public int scale() {
        return coefficients[0].scale();
    }
    
    /**
     * Checks that all coefficients have the same scale.
     * 
     * @return {@code true} iff all the coefficients in this polynomial are 
     *         same.
     */
    public boolean isUniformlyScaled() {
        final int scale = scale();
        
        for (int i = 1; i < coefficients.length; i++) {
            final BigDecimal coefficient = coefficients[i];
            
            if (coefficient != BigDecimal.ZERO 
                    && scale != coefficient.scale()) {
                
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Returns the degree of this polynomial.
     * 
     * @return the degree of this polynomial.
     */
    public int getDegree() {
        return degree;
    }
    
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
        
        final Polynomial other = (Polynomial) o;
        
        if (getDegree() != other.getDegree()) {
            return false;
        }
        
        for (int i = 0; i < length(); i++) {
            if (!getCoefficient(i).equals(other.getCoefficient(i))) {
                return false;
            }
        }
        
        return true;
    }
    
    public boolean approximateEquals(final Polynomial other,
                                     final BigDecimal epsilon) {
        if (other == this) {
            return true;
        }
        
        if (other == null) {
            return false;
        }
        
        if (getDegree() != other.getDegree()) {
            return false;
        }
        
        for (int i = 0; i < length(); i++) {
            final BigDecimal coefficient1 = getCoefficient(i);
            final BigDecimal coefficient2 = other.getCoefficient(i);
            
            if (!approximatelyEquals(coefficient1,
                                     coefficient2, 
                                     epsilon)) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Copies this polynomial with the input scale applied to all coefficients 
     * in this polynomial.
     * 
     * @param scale the next scale to use.
     * 
     * @return a new scaled polynomial copy.
     */
    public Polynomial setScale(final int scale) {
        final BigDecimal[] coefficients = new BigDecimal[length()];
        
        for (int i = 0; i < length(); i++) {
            coefficients[i] = this.coefficients[i].setScale(scale);
        }
        
        return new Polynomial(coefficients);
    }
    
    /**
     * Negates this polynomial.
     * 
     * @return the negation of this polynomial.
     */
    public Polynomial negate() {
        final BigDecimal[] coefficients = new BigDecimal[length()];
        
        for (int i = 0; i < coefficients.length; i++) {
            coefficients[i] = this.coefficients[i].negate();
        }
        
        return new Polynomial(coefficients);
    }
    
    /**
     * Computes and returns the derivative of this polynomial.
     * 
     * @return the derivative of this polynomial. 
     */
    public Polynomial derivate() {
        final BigDecimal[] coefficients = new BigDecimal[length() - 1];
        
        for (int pow = 1;
                 pow < length();
                 pow++) {
            
            coefficients[pow - 1] = 
                    getDerivateCoefficient(
                            this.coefficients[pow],
                            pow);
        }
        
        return new Polynomial(coefficients);
    }
    
    /**
     * Computes and returns the integral of this polynomial. The integration 
     * constant is given as input.
     * 
     * @param integrationConstant the integration constant.
     * 
     * @return the integral of this polynomial. 
     */
    public Polynomial integrate(final BigDecimal integrationConstant) {
        Objects.requireNonNull(
                integrationConstant, 
                "The input integration constant is null");
        
        final BigDecimal[] coefficients = new BigDecimal[length() + 1];
        coefficients[0] = integrationConstant;
        
        for (int pow = 0; 
                 pow < length(); 
                 pow++) { 
            
            coefficients[pow + 1] = 
                    integrateTerm(
                            this.coefficients[pow],
                            pow);
        }
        
        return new Polynomial(coefficients);
    }
    
    /**
     * Integrates this polynomial with zero integration constant.
     * 
     * @return the integral of this polynomial with zero integration constant. 
     */
    public Polynomial integrate() {
        return integrate(BigDecimal.ZERO);
    }
    
    /**
     * Shifts all the coefficients {@code m} positions towards the terms with 
     * higher degree terms. Effectively, this method produces the product of 
     * this polynomial and {@code x^m}.
     * 
     * @param m the shift length.
     * 
     * @return the shifted polynomial.
     */
    public Polynomial shift(final int m) {
        final BigDecimal[] coefficients = new BigDecimal[length() + m];
        
        System.arraycopy(
                this.coefficients,
                0,
                coefficients, 
                m,
                length());
        
        Arrays.fill(
                coefficients, 
                0, 
                m, 
                BigDecimal.ZERO);
        
        return new Polynomial(coefficients);
    }
    
    private BigDecimal integrateTerm(final BigDecimal coefficient, 
                                     final int pow) {
        final BigDecimal powBigDecimal = BigDecimal.valueOf(pow);
        
        return coefficient.divide(
                powBigDecimal
                        .add(BigDecimal.ONE),
                         coefficient.scale(),
                         RoundingMode.CEILING);
    }
    
    private BigDecimal getDerivateCoefficient(final BigDecimal coefficient,
                                              final int pow) {
        
        return coefficient.multiply(BigDecimal.valueOf(pow));
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
        if (getDegree() == 0) {
            return String.format("%f", coefficients[0]).replace(",", ".");
        }
        
        final StringBuilder sb = new StringBuilder();
        
        boolean first = true;
        
        for (int pow = getDegree(); pow >= 0; pow--) {
            if (first) {
                first = false;
                sb.append(getCoefficient(pow))
                  .append("x");
                
                   if (pow > 1) {
                      sb.append(powerToSuperscript(pow));
                   }
            } else {
                final BigDecimal coefficient = getCoefficient(pow);
                
                if (coefficient.compareTo(BigDecimal.ZERO) > 0) {
                    sb.append(" + ")
                      .append(coefficient);
                } else if (coefficient.compareTo(BigDecimal.ZERO) < 0.0) {
                    sb.append(" - ")
                      .append(coefficient.abs());
                } else {
                    // Once here, there is no term with degree pow:
                    continue;
                }
                
                if (pow > 0) {
                    sb.append("x");
                }
                
                if (pow > 1) {
                    sb.append(powerToSuperscript(pow));
                }
            }
        }
        
        return sb.toString().replaceAll(",", ".");
    }
    
    /**
     * Makes sure that this polynomial is {@code requestedLength} coefficients 
     * long.
     * 
     * @param requestedLength the requested length in number of coefficients.
     */
    Polynomial setLength(final int requestedLength) {
        if (requestedLength <= length()) {
            return this;
        }
        
        final BigDecimal[] nextCoefficients = new BigDecimal[requestedLength];
        
        System.arraycopy(
                coefficients,
                0, 
                nextCoefficients,
                0,
                length());
        
        Arrays.fill(
                nextCoefficients,
                length(),
                requestedLength,
                BigDecimal.ZERO);
        
        return new Polynomial(nextCoefficients);
    }
    
    private int computeDegree() {
        for (int i = coefficients.length - 1; i >= 0; i--) {
            final BigDecimal coefficient = coefficients[i];
            final int coefficientScale = coefficient.scale();
            final BigDecimal MY_ZERO = 
                    BigDecimal.ZERO.setScale(coefficientScale);
            
            if (!coefficient.equals(MY_ZERO)) {
                return i;
            }
        }
        
        return 0;
    }
    
    /**
     * Converts the input power to a superscript string.
     * 
     * @param pow the power to process.
     * 
     * @return a superscript representation of the input power.
     */
    private static String powerToSuperscript(final int pow) {
        final String powString = Integer.toString(pow);
        final int powStringLength = powString.length();
        final StringBuilder sb = new StringBuilder(powStringLength);
        
        for (int i = 0; i < powStringLength; i++) {
            final char ch = powString.charAt(i);
            final char nextChar = MAP_DIGIT_CHARACTER_TO_SUPERSCRIPT.get(ch);
            sb.append(nextChar);
        }
        
        return sb.toString();
    }
    
    private static boolean approximatelyEquals(final BigDecimal bd1,
                                               final BigDecimal bd2,
                                               final BigDecimal epsilon) {
        BigDecimal diff = bd1.subtract(bd2);
        diff = diff.abs();
        return diff.compareTo(epsilon) < 0;
    }
    
    /**
     * Creates a new builder and returns it.
     * 
     * @return a new polynomial builder.
     */
    public static Builder getPolynomialBuilder() {
        return new Builder();
    }
    
    /**
     * Creates a new double builder and returns it.
     * 
     * @return a new polynomial double builder. 
     */
    public static DoubleBuilder getPolynomialDoubleBuilder() {
        return new DoubleBuilder();
    }
    
    /**
     * The class for building sparse polynomials.
     */
    public static final class Builder {
        
        /**
         * Maps the coefficient index to its coefficient value.
         */
        private final Map<Integer, BigDecimal> 
                mapCoefficientIndexToCoefficient = new HashMap<>();
        
        /**
         * The maximum coefficient index so far.
         */
        private int maximumCoefficientIndex = 0;
        
        /**
         * Adds a new coefficient to this builder.
         * 
         * @param coefficientIndex the index of the coefficient.
         * @param coefficient      the value of the coefficient.
         * 
         * @return this builder.
         */
        public Builder add(final int coefficientIndex,
                           final BigDecimal coefficient) {
            
            this.maximumCoefficientIndex = 
                    Math.max(this.maximumCoefficientIndex,
                             coefficientIndex);
            
            mapCoefficientIndexToCoefficient.put(coefficientIndex, 
                                                 coefficient);
            
            return this;
        }
        
        /**
         * Adds a new coefficient to this builder.
         * 
         * @param coefficientIndex the index of the coefficient.
         * @param coefficient      the value of the coefficient.
         * 
         * @return this builder.
         */
        public Builder add(final int coefficientIndex,
                           final double coefficient) {
            
            return add(coefficientIndex, 
                       BigDecimal.valueOf(coefficient));
        }
        
        /**
         * Adds a new coefficient to this builder.
         * 
         * @param coefficientIndex the index of the coefficient.
         * @param coefficient      the value of the coefficient.
         * 
         * @return this builder.
         */
        public Builder add(final int coefficientIndex,
                           final long coefficient) {
            
            return add(coefficientIndex, 
                       BigDecimal.valueOf(coefficient));
        }
        
        /**
         * Builds and returns the polynomial from this builder.
         * 
         * @return a polynomial.
         */
        public Polynomial build() {
            final BigDecimal[] coefficients =
                    new BigDecimal[maximumCoefficientIndex + 1];
            
            if (mapCoefficientIndexToCoefficient.isEmpty()) {
                // Special case: return null polynomial y = 0:
                return new Polynomial();
            }
            
            Arrays.fill(
                    coefficients,
                    0,
                    coefficients.length, 
                    BigDecimal.ZERO);
            
            for (final Map.Entry<Integer, BigDecimal> e :
                    mapCoefficientIndexToCoefficient.entrySet()) {
                
                coefficients[e.getKey()] = e.getValue();
            }
            
            return new Polynomial(coefficients);
        }
    }
    
    /**
     * The class for building sparse polynomials from double coefficients.
     */
    public static final class DoubleBuilder {
        
        /**
         * Maps the coefficient index to its coefficient value.
         */
        private final Map<Integer, Double> 
                mapCoefficientIndexToCoefficient = new HashMap<>();
        
        /**
         * The maximum coefficient index so far.
         */
        private int maximumCoefficientIndex = 0;
        
        /**
         * Builds and returns a polynomial using the input coefficients.
         * 
         * @param coefficients the {@code double} array of coefficients.
         * 
         * @return a polynomial.
         */
        public Polynomial buildFrom(final double... coefficients) {
            if (coefficients.length == 0) {
                return new Polynomial();
            }
            
            final DoubleBuilder builder = new DoubleBuilder();
            
            for (int i = 0; i < coefficients.length; i++) {
                builder.add(i, coefficients[i]);
            }
            
            return builder.build();
        }
        
        /**
         * Adds a new coefficient to this builder.
         * 
         * @param coefficientIndex the index of the coefficient.
         * @param coefficient      the value of the coefficient.
         * 
         * @return this builder.
         */
        public DoubleBuilder add(final int coefficientIndex,
                                 final double coefficient) {
            
            validateCoefficient(coefficientIndex,
                                coefficient);
            
            this.maximumCoefficientIndex = 
                    Math.max(this.maximumCoefficientIndex,
                             coefficientIndex);
            
            mapCoefficientIndexToCoefficient.put(coefficientIndex, 
                                                 coefficient);
            
            return this;
        }
        
        /**
         * Builds and returns the polynomial from this builder.
         * 
         * @return a polynomial.
         */
        public Polynomial build() {
            final BigDecimal[] coefficients =
                    new BigDecimal[maximumCoefficientIndex + 1];
            
            if (mapCoefficientIndexToCoefficient.isEmpty()) {
                return new Polynomial();
            }
            
            for (final Map.Entry<Integer, Double> e :
                    mapCoefficientIndexToCoefficient.entrySet()) {
                
                coefficients[e.getKey()] = BigDecimal.valueOf(e.getValue());
            }
            
            return new Polynomial(coefficients);
        }
        
        /**
         * Validates the coefficient.
         * 
         * @param coefficientIndex the index of the coefficient to validate.    
         * @param coefficientValue the value of the coefficient to validate.
         */
        private static void validateCoefficient(final int coefficientIndex,
                                                final double coefficientValue) {
            if (Double.isNaN(coefficientValue)) {
                reportNanException(coefficientIndex);
            }
            
            if (Double.isInfinite(coefficientValue)) {
                reportInfiniteException(coefficientIndex, coefficientValue);
            }
        }
        
        /**
         * Builds and throws an exception reporting NaN coefficients.
         * @param coefficientIndex 
         */
        private static void reportNanException(final int coefficientIndex) {
            final String exceptionString = 
                    String.format(
                            "%dth coefficient is NaN",
                            coefficientIndex);
            
            throw new IllegalArgumentException(exceptionString);
        }
        
        /**
         * Builds and throws an exception reporting infinite coefficients.
         * 
         * @param coefficientIndex the index of the coefficient.
         * @param coefficient      the value of the coefficient (infinite).
         */
        private static void reportInfiniteException(final int coefficientIndex,
                                                    final double coefficient) {
            final String exceptionString = 
                    String.format(
                            "%dth coefficient is infinite: %f",
                            coefficientIndex,
                            coefficient);
            
            throw new IllegalArgumentException(exceptionString);
        }
    }
}
