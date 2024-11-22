package com.github.coderodde.math;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This class implements a polynomial.
 * 
 * @version 2.0.0 (Nov 22, 2024)
 * @since 1.0.0 (Nov 21, 2024)
 */
public final class Polynomial {
    
    private static final Map<Character, Character>
            MAP_DIGIT_CHARACTER_TO_SUPERSCRIPT = new HashMap<>(10);
    
    static {
        MAP_DIGIT_CHARACTER_TO_SUPERSCRIPT.put('0', '⁰');
        MAP_DIGIT_CHARACTER_TO_SUPERSCRIPT.put('1', '¹');
        MAP_DIGIT_CHARACTER_TO_SUPERSCRIPT.put('2', '²');
        MAP_DIGIT_CHARACTER_TO_SUPERSCRIPT.put('3', '³');
        MAP_DIGIT_CHARACTER_TO_SUPERSCRIPT.put('4', '⁴');
        MAP_DIGIT_CHARACTER_TO_SUPERSCRIPT.put('5', '⁵');
        MAP_DIGIT_CHARACTER_TO_SUPERSCRIPT.put('6', '⁶');
        MAP_DIGIT_CHARACTER_TO_SUPERSCRIPT.put('7', '⁷');
        MAP_DIGIT_CHARACTER_TO_SUPERSCRIPT.put('8', '⁸');
        MAP_DIGIT_CHARACTER_TO_SUPERSCRIPT.put('9', '⁹');   
    }
    
    /**
     * The actual array storing coefficients. {@code coefficients[i]} is the 
     * coefficient of the term with degree {@code i}, so the constant term 
     * appears at {@code coefficients[0]}.
     */
    private final BigDecimal[] coefficients;
    
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
        } else {
            this.coefficients = coefficients;
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
        return coefficients.length;
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
     * Returns the degree of this polynomial.
     * 
     * @return the degree of this polynomial.
     */
    public int getDegree() {
        return coefficients.length - 1;
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

    public void setScale(final int scale) {
        for (int i = 0; i < coefficients.length; i++) {
            coefficients[i] = coefficients[i].setScale(scale);
        }
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
    
    public static Builder getPolynomialBuilder() {
        return new Builder();
    }
    
    public static DoubleBuilder getPolynomialDoubleBuilder() {
        return new DoubleBuilder();
    }
    
    public static final class Builder {
        private final Map<Integer, BigDecimal> 
                mapCoefficientIndexToCoefficient = new HashMap<>();
        
        private int maximumCoefficientIndex = 0;
        
        public Builder add(final int coefficientIndex,
                           final BigDecimal coefficient) {
            
            this.maximumCoefficientIndex = 
                    Math.max(this.maximumCoefficientIndex,
                             coefficientIndex);
            
            mapCoefficientIndexToCoefficient.put(coefficientIndex, 
                                                 coefficient);
            
            return this;
        }
        
        public Polynomial build() {
            final BigDecimal[] coefficients =
                    new BigDecimal[maximumCoefficientIndex + 1];
            
            for (final Map.Entry<Integer, BigDecimal> e :
                    mapCoefficientIndexToCoefficient.entrySet()) {
                
                coefficients[e.getKey()] = e.getValue();
            }
            
            return new Polynomial(coefficients);
        }
    }
    
    public static final class DoubleBuilder {
        
        private final Map<Integer, Double> 
                mapCoefficientIndexToCoefficient = new HashMap<>();
        
        private int maximumCoefficientIndex = 0;
        
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
        
        public Polynomial build() {
            final BigDecimal[] coefficients =
                    new BigDecimal[maximumCoefficientIndex + 1];
            
            for (final Map.Entry<Integer, Double> e :
                    mapCoefficientIndexToCoefficient.entrySet()) {
                
                coefficients[e.getKey()] = BigDecimal.valueOf(e.getValue());
            }
            
            return new Polynomial(coefficients);
        }
        
        private static void validateCoefficient(final int coefficientIndex,
                                                final double coefficient) {
            if (Double.isNaN(coefficient)) {
                reportNanException(coefficientIndex);
            }
            
            if (Double.isInfinite(coefficient)) {
                reportInfiniteException(coefficientIndex, coefficient);
            }
        }
        
        private static void reportNanException(final int coefficientIndex) {
            final String exceptionString = 
                    String.format(
                            "%dth coefficient is NaN",
                            coefficientIndex);
            
            throw new IllegalArgumentException(exceptionString);
        }
        
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
