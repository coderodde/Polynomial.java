package com.github.coderodde.math.demos;

import com.github.coderodde.math.Polynomial;
import com.github.coderodde.math.PolynomialMultiplier;
import com.github.coderodde.math.Utils;
import java.math.BigDecimal;
import java.util.Random;

/**
 * This is demonstration program for the {@link Polynomial} class.
 * 
 * @version 2.0.0 (Nov 27, 2024)
 * @since 1.0.0 (Nov 21, 2024)
 */
public final class PolynomialDemo {
    
    private static final int POLYNOMIAL_LENGTH = 10000;
    
    public static void main(final String[] args) {
        warmup();
        benchmark();
    }
    
    private static void benchmark() {
        final Random random = new Random(13L);
        final Polynomial p1 = Utils.getRandomPolynomial(random, 
                                                        POLYNOMIAL_LENGTH);
        
        final Polynomial p2 = Utils.getRandomPolynomial(random, 
                                                        POLYNOMIAL_LENGTH);
        
        long start = System.currentTimeMillis();
        
        Polynomial r1 = PolynomialMultiplier.multiplyViaNaive(p1, p2);
        
        long end = System.currentTimeMillis();
        
        System.out.printf("Naïve: %d milliseconds.\n", end - start);
        
        start = System.currentTimeMillis();
        
        Polynomial r2 = PolynomialMultiplier.multiplyViaKaratsuba(p1, p2);
        
        end = System.currentTimeMillis();
        
        System.out.printf("Karatsuba: %d milliseconds.\n", end - start);
        
        start = System.currentTimeMillis();
        
        Polynomial r3 = PolynomialMultiplier.multiplyViaFFT(p1, p2);
        
        end = System.currentTimeMillis();
        
        System.out.printf("FFT: %d milliseconds.\n", end - start);
        
        System.out.printf("Agree: %b.\n", 
                          r1.approximateEquals(r2, BigDecimal.valueOf(0.1)) &&
                          r1.approximateEquals(r3, BigDecimal.valueOf(0.1)));
        
        System.out.println(r1);
        System.out.println(r3);
    }
    
    private static void warmup() {
        final Random random = new Random(13L);
        final Polynomial p1 = Utils.getRandomPolynomial(random, 
                                                        POLYNOMIAL_LENGTH);
        
        final Polynomial p2 = Utils.getRandomPolynomial(random, 
                                                        POLYNOMIAL_LENGTH);
        
        PolynomialMultiplier.multiplyViaNaive(p1, p2);
        PolynomialMultiplier.multiplyViaKaratsuba(p1, p2);
        PolynomialMultiplier.multiplyViaFFT(p1, p2);
    }
}
