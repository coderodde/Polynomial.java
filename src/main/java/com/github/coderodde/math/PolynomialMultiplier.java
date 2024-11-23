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
    
    public static Polynomial multiplyViaKaratsuba(Polynomial p1,
                                                  Polynomial p2) {
        
        final int n = Math.max(p1.length(), 
                               p2.length());
        
        p1 = p1.setLength(n);
        p2 = p2.setLength(n);
        
        final BigDecimal[] productCoefficients = 
                multiplyViaKaratsubaImpl(p1, p2).coefficients;
        
        int i = productCoefficients.length - 1;
        
        final int scale = productCoefficients[i].scale();
        final BigDecimal MY_ZERO = BigDecimal.valueOf(0L).setScale(scale);
        
        for (; i >= 0; i--) {
            if (!productCoefficients[i].equals(MY_ZERO)) {
                break;
            }
        }
        
        final BigDecimal[] coefficients = new BigDecimal[i + 1];
        
        System.arraycopy(
                productCoefficients, 
                0, 
                coefficients, 
                0, 
                coefficients.length);
        
        return new Polynomial(coefficients);
    }
    
    private static Polynomial multiplyViaKaratsubaImpl(final Polynomial p1,
                                                       final Polynomial p2) {
            
        final int n = Math.max(p1.length(),
                               p2.length());
        
        if (n == 0 || n == 1) {
            return multiplyViaNaive(p1, p2);
        }
        
        final int m = (int) Math.ceil(n / 2.0);
        
        final BigDecimal[] pPrime = new BigDecimal[m];
        final BigDecimal[] qPrime = new BigDecimal[m];
        
        for (int i = 0; i < m; i++) {
            pPrime[i] = p1.getCoefficient(i).add(p1.getCoefficient(m + i));
            qPrime[i] = p2.getCoefficient(i).add(p2.getCoefficient(m + i));
        }
        
        if (n > 2 * m - 1) {
            pPrime[m] = p1.getCoefficient(n);
            qPrime[m] = p2.getCoefficient(n);
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
        
        return r3.getCoefficient(i)
                 .add(r1.getCoefficient(i).negate())
                 .add(r2.getCoefficient(i).negate());
    }
    
    private static BigDecimal getCoefficient(final Polynomial r1, 
                                             final Polynomial r2,
                                             final Polynomial r4,
                                             final int i,
                                             final int m) {
        final BigDecimal term1 = 
                i < 0 || i >= r1.length() ? 
                BigDecimal.ZERO :
                r1.getCoefficient(i);
        
        final BigDecimal term2 = 
                i - m < 0 || i - m >= r4.length() ? 
                BigDecimal.ZERO :
                r4.getCoefficient(i - m);
        
        final BigDecimal term3 = 
                i - 2 * m < 0 || i - 2 * m >= r2.length() ?
                BigDecimal.ZERO :
                r2.getCoefficient(i - 2 * m);
        
        return term1.add(term2).add(term3);
    }
    
    private static Polynomial getR3Polynomial(final BigDecimal[] pCoefficients,
                                              final BigDecimal[] qCoefficients) 
    {
        final Polynomial pResultPolynomial = new Polynomial(pCoefficients);
        final Polynomial qResultPolynomial = new Polynomial(qCoefficients);
        
        return multiplyViaKaratsubaImpl(pResultPolynomial,
                                        qResultPolynomial);
    }
    
    private static Polynomial getR1Polynomial(final Polynomial p,
                                              final Polynomial q,
                                              final int m) {
        
        final BigDecimal[] pCoefficients = new BigDecimal[m];
        final BigDecimal[] qCoefficients = new BigDecimal[m];
        
        System.arraycopy(p.coefficients, 0, pCoefficients, 0, m);
        System.arraycopy(q.coefficients, 0, qCoefficients, 0, m);
        
        final Polynomial pResultPolynomial = new Polynomial(pCoefficients);
        final Polynomial qResultPolynomial = new Polynomial(qCoefficients);
        
        return multiplyViaKaratsubaImpl(pResultPolynomial,
                                        qResultPolynomial);
    }
    
    private static Polynomial getR2Polynomial(final Polynomial p,
                                              final Polynomial q,
                                              final int m,
                                              final int n) {
        
        final BigDecimal[] pCoefficients = new BigDecimal[n - m + 1];
        final BigDecimal[] qCoefficients = new BigDecimal[n - m + 1];
        
        System.arraycopy(p.coefficients, m, pCoefficients, 0, n - m + 1);
        System.arraycopy(q.coefficients, m, qCoefficients, 0, n - m + 1);
        
        final Polynomial pResultPolynomial = new Polynomial(pCoefficients);
        final Polynomial qResultPolynomial = new Polynomial(qCoefficients);
        
        return multiplyViaKaratsubaImpl(pResultPolynomial, 
                                        qResultPolynomial);
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
