package com.github.coderodde.math;

import static com.github.coderodde.math.Utils.powerToSuperscript;
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
     * The actual array storing coefficients. {@code coefficients[i]} is the 
     * coefficient of the term with degree {@code i}, so the constant term 
     * appears at {@code coefficients[0]}.
     */
    Map<Integer, BigDecimal> coefficientMap = new HashMap<>();
    
    /**
     * The degree of this polynomial, i.e., the power of the highest-order term.
     */
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
            this.coefficientMap.put(0, BigDecimal.ZERO);
            this.degree = 0;
        } else {
            final Polynomial p = 
                    getPolynomialBuilder()
                            .withBigDecimals(coefficients)
                            .build();
            
            this.degree = p.degree;
            this.coefficientMap = p.coefficientMap;
        }
    }
    
    public Polynomial(final int length) {
        this.degree = length - 1;
    }
    
    private Polynomial(final int degree,
                       final Map<Integer, BigDecimal> coefficientMap) {
        this.degree = degree;
        this.coefficientMap = coefficientMap;
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
        BigDecimal value = getCoefficient(degree);
        
        for (int i = degree - 1; i >= 0; i--) {
            value = value.multiply(x).add(getCoefficient(i));
        }
        
        return value;
    }
    
    /**
     * Evaluates this polynomial at the point {@code x} using Horner's rule.
     * 
     * @param x the argument value for this polynomial.
     * 
     * @return the value of this polynomial at the specified {@code x}
     *         coordinate.
     */
    public BigDecimal evaluate(final double x) {
        Objects.requireNonNull(x, "The x coordinate is null");
        BigDecimal value = getCoefficient(degree);
        BigDecimal xx = BigDecimal.valueOf(x);
        
        for (int i = degree - 1; i >= 0; i--) {
            value = value.multiply(xx).add(getCoefficient(i));
        }
        
        return value;
    }
    
    /**
     * Evaluates this polynomial at the point {@code x} using Horner's rule.
     * 
     * @param x the argument value for this polynomial.
     * 
     * @return the value of this polynomial at the specified {@code x}
     *         coordinate.
     */
    public BigDecimal evaluate(final long x) {
        Objects.requireNonNull(x, "The x coordinate is null");
        BigDecimal value = getCoefficient(degree);
        BigDecimal xx = BigDecimal.valueOf(x);
        
        for (int i = degree - 1; i >= 0; i--) {
            value = value.multiply(xx).add(getCoefficient(i));
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
        checkCoefficientIndex(coefficientIndex);
        
        if (!coefficientMap.containsKey(coefficientIndex)) {
            return BigDecimal.ZERO;
        }
        
        return coefficientMap.get(coefficientIndex);
    }
    
    BigDecimal getCoefficientInternal(final int coefficientIndex) {
        if (!coefficientMap.containsKey(coefficientIndex)) {
            return BigDecimal.ZERO;
        }
        
        return coefficientMap.get(coefficientIndex);
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
        return coefficientMap.values()
                             .iterator()
                             .next()
                             .scale();
    }
    
    /**
     * Checks that all coefficients have the same scale.
     * 
     * @return {@code true} iff all the coefficients in this polynomial are 
     *         same.
     */
    public boolean isUniformlyScaled() {
        final int scale = scale();
        
        for (final BigDecimal coefficient : coefficientMap.values()) {
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
        
        final int len = Math.max(this.length(), other.length());
        
        for (int i = 0; i < len; i++) {
            final BigDecimal coefficient1 = getCoefficientInternal(i);
            final BigDecimal coefficient2 = other.getCoefficientInternal(i);
            
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
     * @param scale        the next scale to use.
     * @param roundingMode the rounding mode.
     * 
     * @return a new scaled polynomial copy.
     */
    public Polynomial setScale(final int scale, 
                               final RoundingMode roundingMode) {
        
        final Map<Integer, BigDecimal> nextCoefficientMap = 
                new HashMap<>(coefficientMap.size());
        
        for (final Map.Entry<Integer, BigDecimal> entry
                : coefficientMap.entrySet()) {
            
            nextCoefficientMap.put(
                    entry.getKey(), 
                    new BigDecimal(
                            entry.getValue()
                                 .toString())
                            .setScale(scale, roundingMode));
        }
        
        return new Polynomial(degree, 
                              nextCoefficientMap);
    }
    
    public Polynomial setScale(final int scale) {
        return setScale(scale, RoundingMode.HALF_UP);
    }  
    
    /**
     * Negates this polynomial.
     * 
     * @return the negation of this polynomial.
     */
    public Polynomial negate() {
        final Map<Integer, BigDecimal> nextCoefficietMap = 
                new HashMap<>(coefficientMap.size());
        
        for (final Map.Entry<Integer, BigDecimal> entry 
                : coefficientMap.entrySet()) {
            
            nextCoefficietMap.put(entry.getKey(), entry.getValue().negate());
        }
        
        return new Polynomial(degree,
                              nextCoefficietMap);
    }
    
    /**
     * Computes and returns the derivative of this polynomial.
     * 
     * @return the derivative of this polynomial. 
     */
    public Polynomial derivate() {
        final Map<Integer, BigDecimal> nextCoefficientMap = 
                new HashMap<>(coefficientMap.size() - 1);
        
        for (int pow = 1;
                 pow < length();
                 pow++) {
            
            nextCoefficientMap.put(
                    pow - 1, 
                    getDerivateCoefficient(
                            getCoefficient(pow),
                            pow));
        }
        
        return new Polynomial(degree - 1, 
                              nextCoefficientMap);
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
        
        final Map<Integer, BigDecimal> nextCoefficientMap = 
                new HashMap<>(coefficientMap.size() + 1);
        
        nextCoefficientMap.put(0, integrationConstant);
        
        for (int pow = 0; 
                 pow < length(); 
                 pow++) { 
            
            nextCoefficientMap.put(
                    pow + 1,
                    integrateTerm(
                            getCoefficient(pow), 
                            pow));
        }
        
        return new Polynomial(degree + 1, nextCoefficientMap);
    }
    
    /**
     * Integrates this polynomial with zero integration constant.
     * 
     * @return the integral of this polynomial with zero integration constant. 
     */
    public Polynomial integrate() {
        return integrate(BigDecimal.ZERO);
    }
    
    Polynomial minimizeDegree(final BigDecimal zeroEpsilon) {
        int d = degree;
        
        for (; d >= 0; d--) {
            final BigDecimal coefficient = getCoefficient(d);
            
            if (!isZero(coefficient, zeroEpsilon)) {
                break;
            } else {
                // Remove the term with zero coefficient:
                coefficientMap.remove(d);
            }
        }
        
        if (d == -1) {
            return new Polynomial();
        }
        
        return new Polynomial(d, coefficientMap);
    }
    
    BigDecimal[] toCoefficientArray(final int m) {
        final BigDecimal[] coefficientArray = new BigDecimal[m];
        
        for (int i = 0; i < m; i++) {
            final BigDecimal coefficient = coefficientMap.get(i);
            
            coefficientArray[i] = 
                    coefficient == null ?
                    BigDecimal.ZERO :
                    coefficient;
        }
        
        return coefficientArray;
    }
    
    BigDecimal[] toCoefficientArray(final int m,
                                    final int n) {
        
        final BigDecimal[] coefficientArray = new BigDecimal[n - m + 1];
        
        for (int i = m; i <= n; i++) {
            final BigDecimal coefficient = coefficientMap.get(i);
            
            coefficientArray[i - m] = 
                    coefficient == null ?
                    BigDecimal.ZERO :
                    coefficient;
        }
        
        return coefficientArray;
    }
    
    /**
     * Validates the input coefficient index.
     * 
     * @param coefficientIndex the coefficient index to validate.
     */
    private void checkCoefficientIndex(final int coefficientIndex) {
        if (coefficientIndex < 0) {
            final String exceptionMessage = 
                    String.format("Invalid coefficient index: %d",
                                  coefficientIndex);
            
            throw new IndexOutOfBoundsException(exceptionMessage);
        }
        
        if (coefficientIndex > degree) {
            final String exceptionMessage = 
                String.format(
                    "Coefficient index too large: %d, must be at most %d",
                    coefficientIndex,
                    degree);
            
            throw new IndexOutOfBoundsException(exceptionMessage);
        }
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
        return coefficientMap.hashCode();
    }
    
    @Override
    public String toString() {
        if (getDegree() == 0) {
            return String.format(
                    "%f",
                    coefficientMap
                            .values()
                            .iterator()
                            .next())
                    .replace(",", ".");
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
        
        final Map<Integer, BigDecimal> nextCoefficientMap = 
                new HashMap<>(coefficientMap);
        
        return new Polynomial(requestedLength - 1, 
                              nextCoefficientMap);
    }
    
    private static boolean approximatelyEquals(final BigDecimal bd1,
                                               final BigDecimal bd2,
                                               final BigDecimal epsilon) {
        return bd1.subtract(bd2)
                  .abs()
                  .compareTo(epsilon) < 0;
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
        private int maximumDegree = 0;
        
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
            
            this.maximumDegree = 
                    Math.max(this.maximumDegree,
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
         * Populates the least-significant coefficients with the given 
         * {@code long} values.
         * 
         * @param longs the coefficients to use.
         * 
         * @return this builder.
         */
        public Builder withLongs(final long... longs) {
            for (int i = 0; i < longs.length; i++) {
                add(i, longs[i]);
            }
            
            return this;
        }
        
        /**
         * Populates the least-significant coefficients with the given 
         * {@code double} values.
         * 
         * @param doubles the coefficients to use.
         * 
         * @return this builder.
         */
        public Builder withDoubles(final double... doubles) {
            for (int i = 0; i < doubles.length; i++) {
                add(i, doubles[i]);
            }
            
            return this;
        }
        
        /**
         * Populates the least-significant coefficients with the given 
         * {@code bigDecimals} values.
         * 
         * @param bigDecimals the coefficients to use.
         * 
         * @return this builder.
         */
        public Builder withBigDecimals(final BigDecimal... bigDecimals) {
            for (int i = 0; i < bigDecimals.length; i++) {
                add(i, bigDecimals[i]);
            }
            
            return this;
        }
        
        /**
         * Builds and returns the polynomial from this builder.
         * 
         * @return a polynomial.
         */
        public Polynomial build() {
            final Map<Integer, BigDecimal> nextCoefficientMap =
                    new HashMap<>(mapCoefficientIndexToCoefficient);
            
            return new Polynomial(maximumDegree, 
                                  nextCoefficientMap);
        }
    }
    
    private static boolean isZero(BigDecimal bd, 
                                  final BigDecimal zeroEpsilon) {
        if (bd.compareTo(BigDecimal.ZERO) < 0) {
            bd = bd.abs();
        }
        
        return bd.compareTo(zeroEpsilon) < 0;
    }
}
