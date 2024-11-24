package com.github.coderodde.math;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * This class contains some polynomial multiplication algorithms.
 * 
 * @version 1.1.1 (Nov 24, 2024)
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
    
    public static Polynomial multiplyViaKaratsuba(Polynomial p1,
                                                  Polynomial p2) {
        
        final int n = Math.max(p1.length(), 
                               p2.length());
        
        p1 = p1.setLength(n);
        p2 = p2.setLength(n);
        
        final Polynomial rawPolynomial = multiplyViaKaratsubaImpl(p1, p2);
        
        return rawPolynomial.adjustPolynomial();
    }
    
    public static Polynomial multiplyViaFFT(Polynomial p1,
                                            Polynomial p2) {
        
        final int length = Math.max(p1.length(), 
                                    p2.length());
        
        final int n = Utils.getClosestUpwardPowerOfTwo(length) * 2;
        
        p1 = p1.setLength(n);
        p2 = p2.setLength(n);
        
        return null;
    }
    
    private static ComplexPolynomial 
        computeFFT(final ComplexPolynomial complexPolynomial) {
            
        final int n = complexPolynomial.length();
        
        if (n == 1) {
            return complexPolynomial;
        }
        
        ComplexNumber omega = ComplexNumber.one(); 
        
        final ComplexNumber root  = ComplexNumber.getPrincipalRootOfUnity(n);
        
        final ComplexPolynomial[] a = complexPolynomial.split();
        final ComplexPolynomial a0  = a[0];
        final ComplexPolynomial a1  = a[1];
        
        final ComplexPolynomial y0 = computeFFT(a0);
        final ComplexPolynomial y1 = computeFFT(a1);
        final ComplexPolynomial y  = new ComplexPolynomial(n);
        
        for (int k = 0; k < n / 2; k++) {
            final ComplexNumber y0k = y0.getCoefficient(k);
            final ComplexNumber y1k = y1.getCoefficient(k);
            
            y.setCoefficient(k,         
                             y0k.add(omega.multiply(y1k)));
            
            y.setCoefficient(k + n / 2, 
                             y0k.substract(omega.multiply(y1k)));
            
            omega = omega.multiply(root);
        }
        
        return y;
    }
    
    private static Polynomial multiplyViaKaratsubaImpl(final Polynomial p1,
                                                       final Polynomial p2) {
        final int n = Math.max(p1.getDegree(),
                               p2.getDegree());
        
        if (n == 0 || n == 1) {
            return multiplyViaNaive(p1, p2);
        }
        
        final int m = (int) Math.ceil(n / 2.0);
        
        final BigDecimal[] pPrime = new BigDecimal[m + 1];
        final BigDecimal[] qPrime = new BigDecimal[m + 1];
        
        for (int i = 0; i < m; i++) {
            pPrime[i] = p1.getCoefficientInternal(i)
                          .add(p1.getCoefficientInternal(m + i));
            
            qPrime[i] = p2.getCoefficientInternal(i)
                          .add(p2.getCoefficientInternal(m + i));
        }
        
        if (n > 2 * m - 1) {
            pPrime[m] = p1.getCoefficientInternal(n);
            qPrime[m] = p2.getCoefficientInternal(n);
        } else {
            pPrime[m] = BigDecimal.ZERO;
            qPrime[m] = BigDecimal.ZERO;
        }
        
        Polynomial r1 = getR1Polynomial(p1, p2, m);
        Polynomial r2 = getR2Polynomial(p1, p2, m, n);
        Polynomial r3 = getR3Polynomial(pPrime, 
                                        qPrime);
        
        final BigDecimal[] r4Coefficients = new BigDecimal[2 * m + 1];
        
        for (int i = 0; i <= 2 * m; i++) {
            r4Coefficients[i] = getCoefficient(r1, 
                                               r2, 
                                               r3, 
                                               i);
        }
        
        final Polynomial r4 = new Polynomial(r4Coefficients);
        
        final BigDecimal[] rCoefficients = new BigDecimal[2 * n + 1];
        
        for (int i = 0; i <= 2 * n; i++) {
            rCoefficients[i] = getCoefficient(r1,
                                              r2,
                                              r4,
                                              i,
                                              m);
        }
        
        return new Polynomial(rCoefficients);
    }
    
    private static BigDecimal getCoefficient(final Polynomial r1,
                                             final Polynomial r2,
                                             final Polynomial r3,
                                             final int i) {
        
        final BigDecimal term1 = r1.getCoefficientInternal(i);
        final BigDecimal term2 = r2.getCoefficientInternal(i);
        final BigDecimal term3 = r3.getCoefficientInternal(i);
        
        return term3.add(term1.negate()).add(term2.negate());
    }
    
    private static BigDecimal getCoefficient(final Polynomial r1, 
                                             final Polynomial r2,
                                             final Polynomial r4,
                                             final int i,
                                             final int m) {
        
        final BigDecimal term1 = r1.getCoefficientInternal(i);
        final BigDecimal term4 = r4.getCoefficientInternal(i - m);
        final BigDecimal term2 = r2.getCoefficientInternal(i - m * 2);
        
        return term1.add(term2).add(term4);
    }
    
    private static Polynomial getR1Polynomial(final Polynomial p,
                                              final Polynomial q,
                                              final int m) {
        
        final BigDecimal[] pCoefficients = p.toCoefficientArray(m);
        final BigDecimal[] qCoefficients = q.toCoefficientArray(m);
        
        final Polynomial pResultPolynomial = new Polynomial(pCoefficients);
        final Polynomial qResultPolynomial = new Polynomial(qCoefficients);
        
        return multiplyViaKaratsubaImpl(pResultPolynomial,
                                        qResultPolynomial);
    }
    
    private static Polynomial getR2Polynomial(final Polynomial p,
                                              final Polynomial q,
                                              final int m,
                                              final int n) {
        
        final BigDecimal[] pCoefficients = p.toCoefficientArray(m, n);
        final BigDecimal[] qCoefficients = q.toCoefficientArray(m, n);
        
        final Polynomial pResultPolynomial = new Polynomial(pCoefficients);
        final Polynomial qResultPolynomial = new Polynomial(qCoefficients);
        
        return multiplyViaKaratsubaImpl(pResultPolynomial, 
                                        qResultPolynomial);
    }
    
    private static Polynomial getR3Polynomial(final BigDecimal[] pCoefficients,
                                              final BigDecimal[] qCoefficients) 
    {
        final Polynomial pResultPolynomial = new Polynomial(pCoefficients);
        final Polynomial qResultPolynomial = new Polynomial(qCoefficients);
        
        return multiplyViaKaratsubaImpl(pResultPolynomial,
                                        qResultPolynomial);
    }
}
