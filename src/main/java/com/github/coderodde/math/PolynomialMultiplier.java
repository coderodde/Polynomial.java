package com.github.coderodde.math;

import java.math.BigDecimal;
import java.util.Arrays;

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
        
        // Initialize the result coefficient array:
        Arrays.fill(
                coefficientArray, 
                0, 
                coefficientArrayLength, 
                BigDecimal.ZERO);
        
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
                        .add(coefficient1.multiply(coefficient2));
            }
        }
        
        return new Polynomial(coefficientArray);
    }
    
    public static Polynomial multiplyViaKaratsuba(final Polynomial p1,
                                                  final Polynomial p2) {
        
        final int n = Math.max(p1.length(), 
                               p2.length());
        
        p1.setLength(n);
        p2.setLength(n);
        
        return multiplyViaKaratsubaImpl(p1, p2);
    }
    
    private static Polynomial multiplyViaKaratsubaImpl(final Polynomial p,
                                                       final Polynomial q) {
            
        final int n = Math.max(p.length(),
                               q.length()) - 1;
        
        if (n == 0 || n == 1) {
            return multiplyViaNaive(p, q);
        }
        
        final int m = (int) Math.ceil(n / 2.0);
        
        final KaratsubaPolynomials karatsubaPolynomials = 
                getKaratsubaPolynomials(p, q);
        
        final Polynomial p1 = karatsubaPolynomials.p1;
        final Polynomial p2 = karatsubaPolynomials.p2;
        final Polynomial q1 = karatsubaPolynomials.q1;
        final Polynomial q2 = karatsubaPolynomials.q2;
        
        final Polynomial r1 = multiplyViaKaratsubaImpl(p1, q1);
        final Polynomial r4 = multiplyViaKaratsubaImpl(p2, q2);
        
        final Polynomial pPrime = p1.sum(p2);
        final Polynomial qPrime = q1.sum(q2);
        
        final Polynomial s = multiplyViaKaratsubaImpl(pPrime,
                                                      qPrime);
        
        final Polynomial t = s.sum(r1.negate())
                              .sum(r4.negate());
        return stitchImpl(r1, 
                          r4,
                          t,
                          m);
    }
    
    private static Polynomial stitchImpl(Polynomial r1,
                                         Polynomial r4,
                                         Polynomial t,
                                         final int m) {
        t  = t.shift(m);
        r4 = r4.shift(2 * m);
        
        return r1.sum(t).sum(r4);
    }
    
    private static final class KaratsubaPolynomials {
        Polynomial p1;
        Polynomial p2;
        Polynomial q1;
        Polynomial q2;
    }
    
    private static KaratsubaPolynomials 
        getKaratsubaPolynomials(
                final Polynomial p,
                final Polynomial q) {
     
        final KaratsubaPolynomials karatsubaPolynomials = 
                new KaratsubaPolynomials();
        
        loadP(karatsubaPolynomials, p);
        loadQ(karatsubaPolynomials, q);
        
        return karatsubaPolynomials;
    }
        
    private static void loadP(final KaratsubaPolynomials karatsubaPolynomials,
                              final Polynomial p) {
        
        final int n = p.length();
        final int m = n / 2;
        
        final BigDecimal[] p1Coefficients = new BigDecimal[m];
        final BigDecimal[] p2Coefficients = new BigDecimal[n - m];
        
        for (int i = 0; i < m; i++) {
            p1Coefficients[i] = p.getCoefficient(i);
        }
        
        for (int i = 0; i < n - m; i++) {
            p2Coefficients[i] = p.getCoefficient(i + m);
        }
        
        karatsubaPolynomials.p1 = new Polynomial(p1Coefficients);
        karatsubaPolynomials.p2 = new Polynomial(p2Coefficients);
    }
        
    private static void loadQ(final KaratsubaPolynomials karatsubaPolynomials,
                              final Polynomial q) {
        
        final int n = q.length();
        final int m = n / 2;
        
        final BigDecimal[] q1Coefficients = new BigDecimal[m];
        final BigDecimal[] q2Coefficients = new BigDecimal[n - m];
        
        for (int i = 0; i < m; i++) {
            q1Coefficients[i] = q.getCoefficient(i);
        }
        
        for (int i = 0; i < n - m; i++) {
            q2Coefficients[i] = q.getCoefficient(i + m);
        }
        
        karatsubaPolynomials.q1 = new Polynomial(q1Coefficients);
        karatsubaPolynomials.q2 = new Polynomial(q2Coefficients);
    }
}
