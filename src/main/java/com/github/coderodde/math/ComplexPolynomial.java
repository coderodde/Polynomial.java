package com.github.coderodde.math;

import java.math.BigDecimal;

/**
 * This class implements the polynomial over complex number. We need this class
 * in the FFT algorithms.
 * 
 * @version 1.0.0 (Nov 23, 2024)
 * @since 1.0.0 (Nov 23, 2024)
 */
public final class ComplexPolynomial {
    
    private final ComplexNumber[] coefficients;
    
    public ComplexPolynomial(final int length) {
        this.coefficients = new ComplexNumber[length];
    }
    
    public ComplexPolynomial(final Polynomial polynomial) {
        this.coefficients = new ComplexNumber[polynomial.length()];
        
        for (int i = 0; i < coefficients.length; i++) {
            coefficients[i] = new ComplexNumber(polynomial.getCoefficient(i));
        }
    }
    
    public ComplexPolynomial growToNearestPowerOfTwo() {
        int n = 1;
        
        while (n < coefficients.length) {
            n *= 2;
        }
        
        if (n == coefficients.length) {
            return this;
        }
        
        final ComplexPolynomial result = new ComplexPolynomial(n);
        
        System.arraycopy(
                this.coefficients,
                0,
                result.coefficients, 
                0, 
                this.coefficients.length);
        
        final ComplexNumber zero = new ComplexNumber(BigDecimal.ZERO,
                                                     BigDecimal.ZERO);
        
        for (int i = this.coefficients.length; i < n; i++) {
            result.set(i, zero);
        }
        
        return result;
    }
    
    public void set(final int coefficientIndex, 
                    final ComplexNumber coefficient) {
        
        this.coefficients[coefficientIndex] = coefficient;
    }
    
    
    
    public ComplexPolynomial[] split() {
        final int nextLength = coefficients.length / 2;
        final ComplexPolynomial[] result = new ComplexPolynomial[2];
        
        result[0] = new ComplexPolynomial(nextLength);
        result[1] = new ComplexPolynomial(nextLength);
        
        boolean readToFirst = true;
        int coefficientIndex1 = 0;
        int coefficientIndex2 = 0;
        
        for (int i = 0; i < coefficients.length; i++) {
            final ComplexNumber currentComplexNumber = coefficients[i];
            
            if (readToFirst) {
                readToFirst = false;
                result[0].set(coefficientIndex1++, currentComplexNumber);
            } else {
                readToFirst = true;
                result[1].set(coefficientIndex2++, currentComplexNumber);
            }
        }
        
        return result;
    }
}