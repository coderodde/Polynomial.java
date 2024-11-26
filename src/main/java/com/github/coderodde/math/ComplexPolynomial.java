package com.github.coderodde.math;

import static com.github.coderodde.math.Utils.powerToSuperscript;
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
    
    ComplexPolynomial(final ComplexNumber[] coefficients) {
        this.coefficients = coefficients;
    }
    
    public ComplexPolynomial shrinkToHalf() {
        final ComplexNumber[] coefficients = new ComplexNumber[length() / 2];
        
        for (int i = 0; i < coefficients.length; i++) {
            coefficients[i] = this.getCoefficient(i);
        }
        
        return new ComplexPolynomial(coefficients);
    }
    
    public ComplexPolynomial getConjugate() {
        final ComplexNumber[] coefficients = new ComplexNumber[length()];
        
        for (int i = 0; i < coefficients.length; i++) {
            coefficients[i] = this.coefficients[i].getConjugate();
        }
        
        return new ComplexPolynomial(coefficients);
    }
    
    public int length() {
        return coefficients.length;
    }
    
    public ComplexNumber getCoefficient(final int coefficientIndex) {
        return coefficients[coefficientIndex];
    }
    
    public void setCoefficient(final int coefficientIndex, 
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
                result[0].setCoefficient(coefficientIndex1++,
                                         currentComplexNumber);
            } else {
                readToFirst = true;
                result[1].setCoefficient(coefficientIndex2++, 
                                         currentComplexNumber);
            }
        }
        
        return result;
    }
    
    /**
     * Converts this complex polynomial to an ordinary polynomial by ignoring 
     * the imaginary parts in this complex polynomial and copying only the 
     * real part to the resultant polynomial.
     * 
     * @return an ordinary polynomial.
     */
    public Polynomial convertToPolynomial() {
        final BigDecimal[] data = new BigDecimal[length()];
        
        for (int i = 0; i < length(); i++) {
            data[i] = coefficients[i].getRealPart();
        }
        
        return new Polynomial(data);
    }
    
    @Override
    public String toString() {
        if (length() == 0) {
            return coefficients[0].toString();
        }
        final StringBuilder sb = new StringBuilder();
        
        boolean first = true;
        
        for (int pow = length() - 1; pow >= 0; pow--) {
            if (first) {
                first = false;
                sb.append(getCoefficient(pow))
                  .append("x");
                
                   if (pow > 1) {
                      sb.append(powerToSuperscript(pow));
                   }
            } else {
                sb.append(" + ").append(getCoefficient(pow));
                
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
}
