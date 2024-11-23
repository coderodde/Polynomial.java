package com.github.coderodde.math.demos;

import com.github.coderodde.math.Polynomial;
import com.github.coderodde.math.PolynomialMultiplier;
import com.github.coderodde.math.Utils;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

/**
 * This is demonstration program for the {@link Polynomial} class.
 * 
 * @version 1.2.0 (Nov 23, 2024)
 * @since 1.0.0 (Nov 21, 2024)
 */
public final class PolynomialDemo {
    
    private static final int POLYNOMIAL_LENGTH = 10000;
    
    private static final Map<String, Polynomial> environment = new HashMap<>();
    private static Polynomial previousPolynomial = null;
    
    public static void main(final String[] args) {
        warmup();
        benchmark();
        
        System.out.println("\u2076");
        final Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.print("> ");
            
            final String line = scanner.nextLine().trim().toLowerCase();
            
            if (line.equals("quit") || line.equals("exit")) {
                return;
            }
            
            try {
                if (line.contains("save")) {
                    saveImpl(line);
                } else if (line.contains("print")) {
                    print(line);
                } else if (line.contains("+")) {
                    sumImpl(line);
                } else if (line.contains("*")) {
                    productImpl(line);
                } else {
                    parsePolynomial(line);
                }
            } catch (final Exception ex) {
                System.err.printf(">>> Exception: %s!", ex.getMessage());
                System.out.println();
            }
        }
    }
    
    private static void saveImpl(final String line) {
        final String[] parts = line.split("\\s+");
        final String polynommialName = parts[1];
        environment.put(polynommialName,
                        previousPolynomial);
    }
    
    private static void print(final String line) {
        System.out.println(environment.get(line.split("\\s+")[1]));
    }
    
    private static void sumImpl(final String line) {
        final String[] parts = line.split("\\+");
        
        if (parts.length != 2) {
            final String exceptionMessage = 
                    String.format("Invalid line: \"%s\"", line);
            
            throw new IllegalArgumentException(exceptionMessage);
        }
        
        final String polynomialName1 = parts[0].trim();
        final String polynomialName2 = parts[1].trim();
        final Polynomial polynomial1 = environment.get(polynomialName1);
        final Polynomial polynomial2 = environment.get(polynomialName2);
        
        final Polynomial sum = polynomial1.sum(polynomial2);
        previousPolynomial = sum;
        System.out.println(sum);
    }
    
    private static void productImpl(final String line) {
        final String[] parts = line.split("\\*");
        
        if (parts.length != 2) {
            final String exceptionMessage = 
                    String.format("Invalid line: \"%s\"", line);
            
            throw new IllegalArgumentException(exceptionMessage);
        }
        
        final String polynomialName1 = parts[0].trim();
        final String polynomialName2 = parts[1].trim();
        final Polynomial polynomial1 = environment.get(polynomialName1);
        final Polynomial polynomial2 = environment.get(polynomialName2);
        
        final Polynomial product = 
                PolynomialMultiplier
                        .multiplyViaNaive(
                                polynomial1, 
                                polynomial2);
        
        previousPolynomial = product;
        System.out.println(product);
    }
    
    private static Polynomial parsePolynomial(final String line) {
        final String[] termsStrings = line.split("\\s+");
        final Polynomial.Builder builder =
                Polynomial.getPolynomialBuilder();
        
        int coefficientIndex = 0;
        
        for (String termString : termsStrings) {
            termString = termString.trim();
            
            final double coefficient;
            
            try {
                coefficient = Double.parseDouble(termString);
            } catch (final NumberFormatException ex) {
                final String exceptionMessage =
                        String.format(
                                "coefficient \"%s\" is not a real number", 
                                termString);
                
                throw new IllegalArgumentException(exceptionMessage);
            }
            
            builder.add(coefficientIndex++, 
                        coefficient);
        }
        
        final Polynomial p = builder.build();
        previousPolynomial = p;
        System.out.printf(">>> %s\n", p);
        return p;
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
        
        System.out.printf("Agree: %b.\n", 
                          r1.approximateEquals(r2, BigDecimal.valueOf(0.1)));
    }
    
    private static void warmup() {
        final Random random = new Random(13L);
        final Polynomial p1 = Utils.getRandomPolynomial(random, 
                                                        POLYNOMIAL_LENGTH);
        
        final Polynomial p2 = Utils.getRandomPolynomial(random, 
                                                        POLYNOMIAL_LENGTH);
        
        PolynomialMultiplier.multiplyViaNaive(p1, p2);
        PolynomialMultiplier.multiplyViaKaratsuba(p1, p2);
    }
}
