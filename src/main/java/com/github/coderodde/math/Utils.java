package com.github.coderodde.math;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * This class provides some facilities needed in unit testing and benchmarking.
 * 
 * @version 1.1.0 (Nov 24, 2024)
 * @since 1.0.0 (Nov 23, 2024)
 */
public final class Utils {
    
    private static final int POLINOMIAL_LENGTH = 10;
    private static final long MIN_COEFFICIENT = -10L;
    private static final long MAX_COEFFICIENT = +10L;
    
    /**
     * Maps each digit character to its superscript counterpart.
     */
    private static final Map<Character, String>
            MAP_DIGIT_CHARACTER_TO_SUPERSCRIPT = new HashMap<>(10);
    
    static {
        MAP_DIGIT_CHARACTER_TO_SUPERSCRIPT.put('0', "");
        MAP_DIGIT_CHARACTER_TO_SUPERSCRIPT.put('1', "");
        MAP_DIGIT_CHARACTER_TO_SUPERSCRIPT.put('2', "²");
        MAP_DIGIT_CHARACTER_TO_SUPERSCRIPT.put('3', "³");
        MAP_DIGIT_CHARACTER_TO_SUPERSCRIPT.put('4', "\u2074");
        MAP_DIGIT_CHARACTER_TO_SUPERSCRIPT.put('5', "\u2075");
        MAP_DIGIT_CHARACTER_TO_SUPERSCRIPT.put('6', "\u2076");
        MAP_DIGIT_CHARACTER_TO_SUPERSCRIPT.put('7', "\u2077");
        MAP_DIGIT_CHARACTER_TO_SUPERSCRIPT.put('8', "\u2078");  
        MAP_DIGIT_CHARACTER_TO_SUPERSCRIPT.put('9', "\u2079");   
    }
    
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
    
    /**
     * Computes and returns the power of two that is no less than {@code n} and
     * is the closest power of two to {@code n}.
     * 
     * @param n the source integer.
     * 
     * @return a closest power of two above {@code n}.
     */
    public static int getClosestUpwardPowerOfTwo(final int n) {
        int m = 1;
        
        while (m < n) {
            m *= 2;
        }
        
        return m;
    }
    
    /**
     * Converts the input power to a superscript string.
     * 
     * @param pow the power to process.
     * 
     * @return a superscript representation of the input power.
     */
    public static String powerToSuperscript(final int pow) {
        final String powString = Integer.toString(pow);
        final int powStringLength = powString.length();
        final StringBuilder sb = new StringBuilder(powStringLength);
        
        for (int i = 0; i < powStringLength; i++) {
            final char ch = powString.charAt(i);
            final String nextChar = MAP_DIGIT_CHARACTER_TO_SUPERSCRIPT.get(ch);
            sb.append(nextChar);
        }
        
        return sb.toString();
    }
}
