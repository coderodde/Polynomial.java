package com.github.coderodde.math;

import java.math.BigDecimal;

/**
 * This class implements basic facilities for dealing with complex number in the
 * Fast Fourier Transform algorithms.
 * 
 * @version 1.0.0 (Nov 23, 2024)
 * @since 1.0.0 (Nov 23, 2024)
 */
public final class ComplexNumber {
    
    /**
     * The real part of this complex number.
     */
    private final BigDecimal realPart;
    
    /**
     * The imaginary part of this complex number.
     */
    private final BigDecimal imagPart;
    
    public ComplexNumber(final BigDecimal realPart,
                         final BigDecimal imagPart) {
        
        this.realPart = realPart;
        this.imagPart = imagPart;
    }
    
    /**
     * Constructs this complex number as the real unity.
     */
    public ComplexNumber() {
        this(BigDecimal.ONE,
             BigDecimal.ZERO);
    }
    
    /**
     * Constructs this complex number.
     * 
     * @param realPart the real part.
     * @param imagPart the imaginary part.
     */
    public ComplexNumber(final double realPart,
                         final double imagPart) {
        
        this(BigDecimal.valueOf(realPart),
             BigDecimal.valueOf(imagPart));
    }
    
    /**
     * Construct this complex number as a real number with value 
     * {@code realPart}.
     * 
     * @param realPart the real part.
     */
    public ComplexNumber(final BigDecimal realPart) {
        this(realPart, BigDecimal.ZERO);
    }
    
    public BigDecimal getRealPart() {
        return realPart;
    }
    
    public BigDecimal getImaginaryPart() {
        return imagPart;
    }
    
    public ComplexNumber add(final ComplexNumber other) {
        return new ComplexNumber(realPart.add(other.realPart),
                                 imagPart.add(other.imagPart));
    }
    
    public ComplexNumber negate() {
        return new ComplexNumber(realPart.negate(),
                                 imagPart.negate());
    }
    
    public ComplexNumber substract(final ComplexNumber other) {
        return add(other.negate());
    }
    
    /**
     * Multiplies this complex number with the {@code other} complex number.
     * 
     * @param other the second complex number to multiply.
     * 
     * @return the complex product of this and {@code other} complex numbers. 
     */
    public ComplexNumber multiply(final ComplexNumber other) {
        final BigDecimal resultRealPart = 
                realPart.multiply(other.realPart)
                        .subtract(imagPart.multiply(other.imagPart));
        
        final BigDecimal resultImagPart = 
                realPart.multiply(other.imagPart)
                        .add(imagPart.multiply(other.realPart));
        
        return new ComplexNumber(resultRealPart,
                                 resultImagPart);
    }
    
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        
        sb.append("[Complex; re = ")
          .append(realPart)
          .append(", im = ")
          .append(imagPart)
          .append("]");
        
        return sb.toString();
    }
    
    /**
     * Returns the {@code n}th principal complex root of unity.
     * 
     * @param n the root order.
     * 
     * @return a principal complex root of unity.
     */
    public static ComplexNumber getPrincipalRootOfUnity(final int n) {
        
        final double u  = 2.0 * Math.PI / (double) n;
        final double re = Math.cos(u);
        final double im = Math.sin(u);
        
        return new ComplexNumber(re, im);
    }
    
    public static ComplexNumber one() {
        return new ComplexNumber();
    }
}
