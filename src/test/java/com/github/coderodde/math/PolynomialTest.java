package com.github.coderodde.math;

import static com.github.coderodde.math.Utils.getRandomPolynomial;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

public final class PolynomialTest {
    
    @Test
    public void evaluate1() {
        // 2x - 1
        Polynomial p1 = 
                Polynomial.getPolynomialBuilder().withLongs(-1, 2).build();
        
        BigDecimal value = p1.evaluate(BigDecimal.valueOf(3.0));
        value = value.setScale(1);
        assertEquals(BigDecimal.valueOf(5.0), value);
    }
    
    @Test
    public void evaluate2() {
        Polynomial p1 = 
                Polynomial.getPolynomialBuilder()
                          .withDoubles(-5.0, 3.0, 2.0)
                          .build(); // 2x^2 + 3x - 5
        
        BigDecimal value = p1.evaluate(BigDecimal.valueOf(4.0));
        value = value.setScale(1);
        assertEquals(BigDecimal.valueOf(39.0), value);
    }
    
    @Test
    public void getCoefficient() {
        Polynomial p = Polynomial.getPolynomialBuilder()
                                 .withLongs(1, 2, 3, 4)
                                 .build();
        
        assertEquals(BigDecimal.valueOf(1), p.getCoefficient(0));
        assertEquals(BigDecimal.valueOf(2), p.getCoefficient(1));
        assertEquals(BigDecimal.valueOf(3), p.getCoefficient(2));
        assertEquals(BigDecimal.valueOf(4), p.getCoefficient(3));
    }
    
    @Test
    public void length() {
        assertEquals(5, Polynomial.getPolynomialBuilder()
                                  .withLongs(3, -2, -1, 4, 2)
                                  .build()
                                  .length());
    }
    
    @Test
    public void sum() {
        Polynomial p1 = Polynomial.getPolynomialBuilder()
                                  .withLongs(3, -1, 2)
                                  .build();
        
        Polynomial p2 = Polynomial.getPolynomialBuilder()
                                  .withLongs(5, 4)
                                  .build();
        
        Polynomial sum = p1.sum(p2);
        
        Polynomial expected = Polynomial.getPolynomialBuilder()
                                        .withLongs(8, 3, 2)
                                        .build();
        
        assertEquals(expected, sum);
    }

    @Test
    public void getDegree() {
        assertEquals(3, Polynomial.getPolynomialBuilder()
                                  .withLongs(1, -2, 3, -4)
                                  .build()
                                  .getDegree());
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
    public void integrateDerivateTwice() {
        Polynomial p =
                Polynomial.getPolynomialBuilder()
                          .withLongs(1, -2, 3)
                          .build()
                          .setScale(3);
        
        Polynomial next = p.integrate()
                           .setScale(3)
                           .derivate()
                           .setScale(3);
        
        assertEquals(p, next);
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
        
        Polynomial expected = Polynomial.getPolynomialBuilder()
                                        .withLongs(12, -5, 2, 1)
                                        .build();
        
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
        
        Polynomial expected = Polynomial.getPolynomialBuilder()
                                        .withLongs(12, -5, 2, 1)
                                        .build();
        
        product  = product .setScale(2);
        expected = expected.setScale(2);
        
        assertEquals(expected, product);
    }
    
    @Test
    public void multiplyFFT() {
        Polynomial a = // 2x + 3
                Polynomial
                        .getPolynomialBuilder()
                        .withLongs(3, 2)
                        .build();
        Polynomial b = // 4x - 5
                Polynomial
                        .getPolynomialBuilder()
                        .withLongs(-5, 4)
                        .build();
        
        // (2x + 3) (4x - 5) = 8x^2 - 10x + 12x - 15
        // = 8x^2 + 2x - 15
        Polynomial r = 
                PolynomialMultiplier
                        .multiplyViaFFT(a, b)
                        .setScale(2, RoundingMode.HALF_EVEN)
                        .minimizeDegree(BigDecimal.valueOf(0.01));
        
        Polynomial expected = 
                PolynomialMultiplier.multiplyViaNaive(a, b)
                          .setScale(2, RoundingMode.HALF_EVEN)
                          .minimizeDegree(BigDecimal.valueOf(0.01));
        
        assertEquals(expected, r);
    }
    
    @Test
    public void multiplyFFT2() {
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
        Polynomial product = 
                PolynomialMultiplier
                        .multiplyViaFFT(p1, p2)
                        .setScale(2, RoundingMode.HALF_UP)
                        .minimizeDegree(BigDecimal.valueOf(0.01));
        
        Polynomial expected =
                PolynomialMultiplier
                        .multiplyViaNaive(p1, p2)
                        .setScale(2, RoundingMode.HALF_UP)
                        .minimizeDegree(BigDecimal.valueOf(0.01));
        
        product  = product .setScale(2);
        expected = expected.setScale(2);
        
        assertEquals(3, product.getDegree());
        
        assertEquals(expected, product);
    }
    
    @Test
    public void negate() {
        Polynomial p = 
                Polynomial
                        .getPolynomialBuilder()
                        .withLongs(2, -3, 4, -5)
                        .build();
        
        Polynomial expected = 
                Polynomial
                        .getPolynomialBuilder()
                        .withLongs(-2, 3, -4, 5)
                        .build();
        
        assertEquals(expected, p.negate());
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
        Polynomial.getPolynomialBuilder().add(3, Double.NaN);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void throwDoubleBuilderOnInfiniteCoefficient() {
        Polynomial.getPolynomialBuilder()
                  .add(4, Double.NEGATIVE_INFINITY);
    }
    
    @Test
    public void getDegree2() {
        Polynomial p = 
                Polynomial.getPolynomialBuilder()
                          .add(0, BigDecimal.ZERO)
                          .add(0, BigDecimal.ZERO)
                          .add(0, BigDecimal.ZERO)
                          .build();
        
        assertEquals(0, p.getDegree());
        
        p = Polynomial.getPolynomialBuilder()
                      .add(0, BigDecimal.ZERO)
                      .add(0, BigDecimal.ZERO)
                      .build();
        
        assertEquals(0, p.getDegree());
        
        p = Polynomial.getPolynomialBuilder()
                      .add(0, BigDecimal.ZERO)
                      .build();
        
        assertEquals(0, p.getDegree());
        
        p = Polynomial.getPolynomialBuilder()
                      .build();
        
        assertEquals(0, p.getDegree());
        
        p = Polynomial.getPolynomialBuilder()
                      .add(1, BigDecimal.ONE)
                      .add(2, BigDecimal.ZERO)
                      .build();
        
        assertEquals(2, p.getDegree());
        
        p = Polynomial.getPolynomialBuilder()
                      .add(1, BigDecimal.ONE)
                      .add(2, BigDecimal.ZERO.setScale(4))
                      .add(4, BigDecimal.ZERO.setScale(1))
                      .build();
        
        assertEquals(4, p.getDegree());
        
        p = Polynomial.getPolynomialBuilder()
                      .add(1, BigDecimal.ONE)
                      .add(2, BigDecimal.ZERO.setScale(4))
                      .add(4, BigDecimal.ZERO.setScale(1))
                      .add(5, BigDecimal.valueOf(4.0).setScale(10))
                      .add(7, BigDecimal.ZERO)
                      .build();
        
        assertEquals(7, p.getDegree());
    }
    
    @Test
    public void karatsuba() {
        // -2x + 3
        final Polynomial p1 = Polynomial.getPolynomialBuilder()
                                        .add(0, BigDecimal.valueOf(3))
                                        .add(1, BigDecimal.valueOf(-2))
                                        .build()
                                        .setScale(3);
        
        // 5x - 1
        final Polynomial p2 = Polynomial.getPolynomialBuilder()
                                        .add(0, BigDecimal.valueOf(-1))
                                        .add(1, BigDecimal.valueOf(5))
                                        .build()
                                        .setScale(3);
        
        // (-2x + 3)(5x - 1) = -10x^2 + 2x + 15x - 3 = -10x^2 + 17x - 3
        final Polynomial expected = Polynomial.getPolynomialBuilder()
                                              .add(0, BigDecimal.valueOf(-3))
                                              .add(1, BigDecimal.valueOf(17))
                                              .add(2, BigDecimal.valueOf(-10))
                                              .build();
        
        final Polynomial naive = PolynomialMultiplier.multiplyViaNaive(p1, p2);
        final Polynomial karat = PolynomialMultiplier.multiplyViaKaratsuba(p1, 
                                                                           p2);
        
        assertTrue(expected.approximateEquals(naive, BigDecimal.valueOf(0.01)));
        assertTrue(expected.approximateEquals(karat, BigDecimal.valueOf(0.01)));
    }
    
    @Test
    public void karatsuba2() {
        
        // -2x^2 + 3x + 4 
        final Polynomial p1 = Polynomial.getPolynomialBuilder()
                                        .add(0, BigDecimal.valueOf(4))
                                        .add(1, BigDecimal.valueOf(3))
                                        .add(2, BigDecimal.valueOf(-2))
                                        .build();
        
        // 5x - 1
        final Polynomial p2 = Polynomial.getPolynomialBuilder()
                                        .add(0, BigDecimal.valueOf(-1))
                                        .add(1, BigDecimal.valueOf(5))
                                        .build();
        
        // (-2x^2 + 3x + 4)(5x - 1) = 
        // (-10x^3 + 15x^2 + 20x) - (-2x^2 + 3x + 4) =
        // -10x^3 + 17x^2 + 17x - 4
        final Polynomial expected = Polynomial.getPolynomialBuilder()
                                              .add(0, BigDecimal.valueOf(-4))
                                              .add(1, BigDecimal.valueOf(17))
                                              .add(2, BigDecimal.valueOf(17))
                                              .add(3, BigDecimal.valueOf(-10))
                                              .build();
        
        final Polynomial naive = PolynomialMultiplier.multiplyViaNaive(p1, p2);
        final Polynomial karat = PolynomialMultiplier.multiplyViaKaratsuba(p1, 
                                                                           p2);
        assertEquals(expected, naive);
        assertEquals(expected, karat);
    }
    
    @Test
    public void equals() {
        Polynomial p1 = Polynomial.getPolynomialBuilder().add(0, -2)
                                                         .add(1, 4)
                                                         .build()
                                                         .setScale(3);
        
        Polynomial p2 = Polynomial.getPolynomialBuilder().add(0, -2)
                                                         .add(1, 4)
                                                         .build()
                                                         .setScale(3);
        
        assertTrue(p1.equals(p2));
    }
    
    @Test
    public void bruteForceKaratsubaVsNaive() {
        final Random random = new Random(13L);
        final BigDecimal epsilon = BigDecimal.valueOf(0.01);
        
        for (int iter = 0; iter < 100; iter++) {
            final Polynomial p1 = getRandomPolynomial(random).setScale(4);
            final Polynomial p2 = getRandomPolynomial(random).setScale(4);
            
            final Polynomial product1 = 
                    PolynomialMultiplier
                            .multiplyViaNaive(p1,
                                              p2).setScale(2);
            
            final Polynomial product2 = 
                    PolynomialMultiplier
                            .multiplyViaKaratsuba(p1,
                                                  p2).setScale(2);
            
            assertTrue(product1.approximateEquals(product2, epsilon));
        }
    }
    
    @Test
    public void bruteForceNaiveVsFFT() {
        final Random random = new Random(13L);
        final BigDecimal epsilon = BigDecimal.valueOf(0.01);
        
        for (int iter = 0; iter < 100; iter++) {
            final Polynomial p1 = getRandomPolynomial(random).setScale(4);
            final Polynomial p2 = getRandomPolynomial(random).setScale(4);
            
            final Polynomial product1 = 
                    PolynomialMultiplier
                            .multiplyViaNaive(p1,
                                              p2).setScale(2);
            
            final Polynomial product2 = 
                    PolynomialMultiplier
                            .multiplyViaFFT(p1,
                                            p2)
                            .minimizeDegree(BigDecimal.valueOf(0.01))
                            .setScale(2);
            
            assertTrue(product1.approximateEquals(product2, epsilon));
        }
    }
}
