package com.github.coderodde.math;

import java.math.BigDecimal;
import java.util.Random;

/**
 * This class provides some facilities needed in unit testing and benchmarking.
 * 
 * @version 1.0.0 (Nov 23, 2024)
 * @since 1.0.0 (Nov 23, 2024)
 */
public final class Utils {
    
    private static final int POLINOMIAL_LENGTH = 10;
    private static final long MIN_COEFFICIENT = -10L;
    private static final long MAX_COEFFICIENT = +10L;
    
    public static Polynomial getRandomPolynomial(final Random random) {
        return getRandomPolynomial(random, 
                                   POLINOMIAL_LENGTH);
    }
    
    private static BigDecimal getRandomCoefficient(final Random random) {
        return getRandomCoefficient(random, 
                                    MIN_COEFFICIENT, 
                                    MAX_COEFFICIENT);
    }
    
    public static Polynomial 
        getRandomPolynomial(final Random random,
                            final int polynomialLength) {
            
        final Polynomial.Builder builder = Polynomial.getPolynomialBuilder();
        
        for (int i = 0; i < polynomialLength; i++) {
            builder.add(i, getRandomCoefficient(random));
        }
        
        return builder.build();
    }
    
    public static BigDecimal getRandomCoefficient(final Random random,
                                                   final long minCoefficient,
                                                   final long maxCoefficient) {
        final long longCoefficient = minCoefficient
                                   + random.nextLong(
                                           maxCoefficient 
                                         - minCoefficient
                                         + 1);
        
        return BigDecimal.valueOf(longCoefficient);
    }
    
    public static int getClosestUpwardPowerOfTwo(final int n) {
        int m = 1;
        
        while (m < n) {
            m *= 2;
        }
        
        return m;
    }
}
