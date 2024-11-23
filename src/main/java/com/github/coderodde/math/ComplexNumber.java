package com.github.coderodde.math;

import java.math.BigDecimal;

/**
 * This class implements basic facilities for dealing with complex number in the
 * Fast Fourier Transform algorihtms.
 * 
 * @version 1.0.0 (Nov 23, 2024)
 * @since 1.0.0 (Nov 23, 2024)
 */
public final class ComplexNumber {
    
    private final BigDecimal realPart;
    private final BigDecimal imagPart;
    
    public ComplexNumber(final BigDecimal realPart,
                         final BigDecimal imagPart) {
        
        this.realPart = realPart;
        this.imagPart = imagPart;
    }
    
    public ComplexNumber() {
        this(BigDecimal.ONE,
             BigDecimal.ZERO);
    }
    
    public ComplexNumber(final double realPart,
                         final double imagPart) {
        
        this(BigDecimal.valueOf(realPart),
             BigDecimal.valueOf(imagPart));
    }
    
    public ComplexNumber(final BigDecimal realPart) {
        this(realPart, BigDecimal.ZERO);
    }
    
    public BigDecimal getRealPart() {
        return realPart;
    }
    
    public BigDecimal getImaginaryPart() {
        return imagPart;
    }
    
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
    
    public static ComplexNumber getPrincipalRootOfUnity(final int n) {
        
        final double u = 2.0 * Math.PI / (double) n;
        final double re = Math.cos(u);
        final double im = Math.sin(u);
        
        return new ComplexNumber(re, im);
    }
}
