package com.github.coderodde.math.demos;

import com.github.coderodde.math.Polynomial;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @version 1.0.0 (Nov 21, 2024)
 * @since 1.0.0 (Nov 21, 2024)
 */
public final class PolynomialDemo {
    
    private static final Map<String, Polynomial> environment = new HashMap<>();
    private static Polynomial previousPolynomial = null;
    
    public static void main(final String[] args) {
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
                } if (line.contains("+")) {
                    sumImpl(line);
                } else if (line.contains("*")) {
                    productImpl(line);
                } else {
                    parsePolynomial(line);
                }
            } catch (final Exception ex) {
                System.out.printf(">>> Exception: %s!", ex.getMessage());
            }
        }
    }
    
    private static void saveImpl(final String line) {
        final String[] parts = line.split("\\s+");
        final String polynommialName = parts[0];
        environment.put(polynommialName,
                        previousPolynomial);
    }
    
    private static void sumImpl(final String line) {
        final String[] parts = line.split("\\+");
        
        if (parts.length != 2) {
            final String exceptionMessage = 
                    String.format("Invalid line: \"%s\"", line);
            
            throw new IllegalArgumentException(exceptionMessage);
        }
        
        final String polynomialName1 = parts[0];
        final String polynomialName2 = parts[1];
        final Polynomial polynomial1 = environment.get(polynomialName1);
        final Polynomial polynomial2 = environment.get(polynomialName2);
        
        final Polynomial sum = polynomial1.sum(polynomial2);
        previousPolynomial = sum;
        System.out.println(sum);
    }
    
    private static void productImpl(final String line) {
        final String[] parts = line.split("\\+");
        
        if (parts.length != 2) {
            final String exceptionMessage = 
                    String.format("Invalid line: \"%s\"", line);
            
            throw new IllegalArgumentException(exceptionMessage);
        }
        
        final String polynomialName1 = parts[0];
        final String polynomialName2 = parts[1];
        final Polynomial polynomial1 = environment.get(polynomialName1);
        final Polynomial polynomial2 = environment.get(polynomialName2);
        
        final Polynomial product = polynomial1.multiply(polynomial2);
        previousPolynomial = product;
        System.out.println(product);
    }
    
    private static Polynomial parsePolynomial(final String line) {
        final String[] termsStrings = line.split("\\+|-");
        final Polynomial.Builder builder = Polynomial.getPolynomialBuilder();
        
        int coefficientIndex = 0;
        
        for (String termString : termsStrings) {
            termString = termString.trim();
            
            if (termString.isEmpty()) {
                throw new IllegalArgumentException("Missing term.");
            }
            
            final double coefficient;
            
            try {
                coefficient = Double.parseDouble(termString);
            } catch (final NumberFormatException ex) {
                final String exceptionMessage =
                        String.format(
                                "coefficient \"%s\" is not a real number.", 
                                termString);
                
                throw new IllegalArgumentException(exceptionMessage);
            }
            
            builder.add(coefficientIndex++, 
                        coefficient);
        }
        
        return builder.build();
    }
}
