package com.github.coderodde.math;

import java.math.BigDecimal;

/**
 * This class contains some polynomial multiplication algorithms.
 * 
 * @version 1.0.0 (Nov 22, 2024)
 * @since 1.0.0 (Nov 22, 2024)
 */
public final class PolynomialMultiplier {
    
    /**
     * Multiplies {@code p1} and {@code p2} naively.
     * 
     * @param p1 the first polynomial.
     * @param p2 the second polynomial.
     * 
     * @return the product of {@code p1} and {@code p2}.
     */
    public static Polynomial multiplyViaNaive(final Polynomial p1,
                                              final Polynomial p2) {
        final int coefficientArrayLength = p1.length()
                                         + p2.length()
                                         - 1;
        
        final BigDecimal[] coefficientArray = 
                new BigDecimal[coefficientArrayLength];
        
        for (int index1 = 0;
                 index1 < p1.length(); 
                 index1++) {
            
            for (int index2 = 0; 
                     index2 < p2.length(); 
                     index2++) {
                
                final BigDecimal coefficient1 = p1.getCoefficient(index1);
                final BigDecimal coefficient2 = p2.getCoefficient(index2);
                
                coefficientArray[index1 + index2] = 
                        coefficientArray[index1 + index2]
                        .add(coefficient1)
                        .multiply(coefficient2);
            }
        }
        
        return new Polynomial(coefficientArray);
    }
}
