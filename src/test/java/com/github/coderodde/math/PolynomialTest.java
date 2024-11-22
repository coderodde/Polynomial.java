package com.github.coderodde.math;

import java.math.BigDecimal;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

public final class PolynomialTest {
    
    private static final int MAX_POLINOMIAL_LENGTH = 10;
    private static final long MIN_COEFFICIENT = -10L;
    private static final long MAX_COEFFICIENT = +10L;
    
    @Test
    public void evaluate1() {
        // 2x - 1
        Polynomial p1 = 
                Polynomial.getPolynomialDoubleBuilder().buildFrom(-1, 2);
        
        BigDecimal value = p1.evaluate(BigDecimal.valueOf(3.0));
        value = value.setScale(1);
        assertEquals(BigDecimal.valueOf(5.0), value);
    }
    
    @Test
    public void evaluate2() {
        Polynomial p1 = 
                Polynomial.getPolynomialDoubleBuilder()
                          .buildFrom(-5.0, 3.0, 2.0); // 2x^2 + 3x - 5
        
        BigDecimal value = p1.evaluate(BigDecimal.valueOf(4.0));
        value = value.setScale(1);
        assertEquals(BigDecimal.valueOf(39.0), value);
    }
    
    @Test
    public void getCoefficient() {
        Polynomial p = Polynomial.getPolynomialDoubleBuilder()
                                 .buildFrom(1, 2, 3, 4);
        
        assertEquals(BigDecimal.valueOf(1.0), p.getCoefficient(0));
        assertEquals(BigDecimal.valueOf(2.0), p.getCoefficient(1));
        assertEquals(BigDecimal.valueOf(3.0), p.getCoefficient(2));
        assertEquals(BigDecimal.valueOf(4.0), p.getCoefficient(3));
    }
    
    @Test
    public void length() {
        assertEquals(5, Polynomial.getPolynomialDoubleBuilder()
                                  .buildFrom(3, -2, -1, 4, 2).length());
    }
    
    @Test
    public void sum() {
        Polynomial p1 = Polynomial.getPolynomialDoubleBuilder()
                                   .buildFrom(3, -1, 2);
        
        Polynomial p2 = Polynomial.getPolynomialDoubleBuilder()
                                  .buildFrom(5, 4);
        
        Polynomial sum = p1.sum(p2);
        
        Polynomial expected = Polynomial.getPolynomialDoubleBuilder()
                                        .buildFrom(8, 3, 2);
        
        assertEquals(expected, sum);
    }

    @Test
    public void getDegree() {
        assertEquals(3, Polynomial.getPolynomialDoubleBuilder()
                                  .buildFrom(1, -2, 3, -4).getDegree());
    }
    
    @Test
    public void derivate() {
        Polynomial p = 
                Polynomial
                        .getPolynomialBuilder()
                        .add(0, BigDecimal.valueOf(4))
                        .add(1, BigDecimal.valueOf(-3))
                        .add(2, BigDecimal.valueOf(5))
                        .build();
        
        Polynomial d = p.derivate();
        
        assertEquals(2, d.length());
        
        Polynomial expected = 
                Polynomial
                    .getPolynomialBuilder()
                    .add(0, BigDecimal.valueOf(-3))
                    .add(1, BigDecimal.valueOf(10))
                    .build();
        
        assertEquals(expected, d);
    }
    
    @Test
    public void integrate() {
        // 6x^2 - 8x + 4
        Polynomial p = 
                Polynomial
                        .getPolynomialBuilder()
                        .add(0, BigDecimal.valueOf(4.0))
                        .add(1, BigDecimal.valueOf(8.0))
                        .add(2, BigDecimal.valueOf(6.0))
                        .build();
        
        p = p.setScale(2);
        
        // 2x^3 - 4x^2 + 4x + 16
        Polynomial i = p.integrate(BigDecimal.valueOf(16.0));
        
        i = i.setScale(2);
        
        assertEquals(4, i.length());
        
        Polynomial expected = 
                Polynomial
                    .getPolynomialBuilder()
                    .add(0, BigDecimal.valueOf(16.0))
                    .add(1, BigDecimal.valueOf(4.0))
                    .add(2, BigDecimal.valueOf(4.0))
                    .add(3, BigDecimal.valueOf(2.0))
                    .build();
        
        expected = expected.setScale(2);
        
        assertEquals(expected, i);
    }
    
    @Test
    public void multiplyNaive() {
        // x^2 - 2x + 3
        Polynomial p1 = 
                Polynomial
                        .getPolynomialBuilder()
                        .add(0, BigDecimal.valueOf(3).setScale(1))
                        .add(1, BigDecimal.valueOf(-2).setScale(1))
                        .add(2, BigDecimal.valueOf(1).setScale(1))
                        .build();
        // x + 4
        Polynomial p2 = 
                Polynomial
                        .getPolynomialBuilder()
                        .add(0, BigDecimal.valueOf(4).setScale(1))
                        .add(1, BigDecimal.valueOf(1).setScale(1))
                        .build();
        
        // (x^3 - 2x^2 + 3x) + (4x^2 - 8x + 12) = x^3 + 2x^2 - 5x + 12
        Polynomial product = PolynomialMultiplier.multiplyViaNaive(p1, p2); 
        
        assertEquals(3, product.getDegree());
        
        Polynomial expected = Polynomial.getPolynomialDoubleBuilder()
                                        .buildFrom(12, -5, 2, 1);
        
        product  = product .setScale(2);
        expected = expected.setScale(2);
        
        assertEquals(expected, product);
    }
    
    @Test
    public void multiplyKaratsuba() {
        // x^2 - 2x + 3
        Polynomial p1 = 
                Polynomial
                        .getPolynomialBuilder()
                        .add(0, BigDecimal.valueOf(3).setScale(1))
                        .add(1, BigDecimal.valueOf(-2).setScale(1))
                        .add(2, BigDecimal.valueOf(1).setScale(1))
                        .build();
        // x + 4
        Polynomial p2 = 
                Polynomial
                        .getPolynomialBuilder()
                        .add(0, BigDecimal.valueOf(4).setScale(1))
                        .add(1, BigDecimal.valueOf(1).setScale(1))
                        .build();
        
        // (x^3 - 2x^2 + 3x) + (4x^2 - 8x + 12) = x^3 + 2x^2 - 5x + 12
        Polynomial product = PolynomialMultiplier.multiplyViaKaratsuba(p1, p2); 
        
        assertEquals(3, product.getDegree());
        
        Polynomial expected = Polynomial.getPolynomialDoubleBuilder()
                                        .buildFrom(12, -5, 2, 1);
        
        product  = product .setScale(2);
        expected = expected.setScale(2);
        
        assertEquals(expected, product);
    }
    
    @Test
    public void negate() {
        Polynomial p = 
                Polynomial
                        .getPolynomialDoubleBuilder()
                        .buildFrom(2, -3, 4, -5);
        
        Polynomial expected = 
                Polynomial
                        .getPolynomialDoubleBuilder()
                        .buildFrom(-2, 3, -4, 5);
        
        assertEquals(expected, p.negate());
    }
    
    @Test
    public void shift() {
        Polynomial p = 
                Polynomial
                        .getPolynomialDoubleBuilder()
                        .buildFrom(2, -3, 4, -5);
        
        Polynomial expected = 
                Polynomial
                        .getPolynomialDoubleBuilder()
                        .buildFrom(0, 0, 2, -3, 4, -5);
        
        p = p.shift(2);
        
        p = p.setScale(2);
        expected = expected.setScale(2);
        
        assertEquals(expected, p);
    }
    
    @Test
    public void testConstructEmptyPolynomial() {
        Polynomial p = new Polynomial();
        
        assertEquals(1, p.length());
        assertEquals(0, p.getDegree());
        assertEquals(BigDecimal.ZERO, p.getCoefficient(0));
        assertEquals(BigDecimal.ZERO, p.evaluate(BigDecimal.valueOf(4.0)));
        assertEquals(BigDecimal.ZERO, p.evaluate(BigDecimal.valueOf(-3.0)));
    }
    
    @Test
    public void builder() {
        final Polynomial p = Polynomial.getPolynomialBuilder()
                                       .add(10, BigDecimal.valueOf(10))
                                       .add(5000, BigDecimal.valueOf(5000))
                                       .build();
        
        assertEquals(5000, p.getDegree());  
        assertEquals(BigDecimal.valueOf(10),   p.getCoefficient(10));
        assertEquals(BigDecimal.valueOf(5000), p.getCoefficient(5000));
    }
    
    @Test
    public void emptyPolynomial() {
        Polynomial p = new Polynomial();
        
        assertEquals(1, p.length());
        assertEquals(0, p.getDegree());
        
        assertEquals(BigDecimal.ZERO, p.getCoefficient(0));
        assertEquals(BigDecimal.ZERO, p.evaluate(BigDecimal.valueOf(10.0)));
        assertEquals(BigDecimal.ZERO, p.evaluate(BigDecimal.valueOf(-7.0)));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void throwDoubleBuilderOnNanCoefficient() {
        Polynomial.getPolynomialDoubleBuilder().add(3, Double.NaN);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void throwDoubleBuilderOnInfiniteCoefficient() {
        Polynomial.getPolynomialDoubleBuilder()
                  .add(4, Double.NEGATIVE_INFINITY);
    }
    
    @Test
    public void bruteForceKaratsubaVsNaive() {
        final Random random = new Random(13L);
        
        for (int iter = 0; iter < 100; iter++) {
            final Polynomial p1 = getRandomPolynomial(random).setScale(2);
            final Polynomial p2 = getRandomPolynomial(random).setScale(2);
            
            final Polynomial product1 = 
                    PolynomialMultiplier
                            .multiplyViaNaive(p1,
                                              p2).setScale(2);
            
            final Polynomial product2 = 
                    PolynomialMultiplier
                            .multiplyViaKaratsuba(p1,
                                                  p2).setScale(2);
            
            assertEquals(product1,
                         product2);
            
            System.out.println(iter);
        }
    }
    
    private static Polynomial getRandomPolynomial(final Random random) {
        final int polynomialLength = 1 + random.nextInt(MAX_POLINOMIAL_LENGTH);
        final Polynomial.Builder builder = Polynomial.getPolynomialBuilder();
        
        for (int i = 0; i < polynomialLength; i++) {
            builder.add(i, getRandomCoefficient(random));
        }
        
        return builder.build();
    }
    
    private static BigDecimal getRandomCoefficient(final Random random) {
        final long longCoefficient = MIN_COEFFICIENT 
                                   + random.nextLong(
                                           MAX_COEFFICIENT 
                                         - MIN_COEFFICIENT
                                         + 1);
        
        return BigDecimal.valueOf(longCoefficient);
    }
}
