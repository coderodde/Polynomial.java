package com.github.coderodde.math;

import java.math.BigDecimal;
import org.junit.Test;
import static org.junit.Assert.*;

public final class PolynomialTest {
    
    private static final double E = 1E-3;
    
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
    public void multiply() {
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
        
        product.setScale(2);
        expected.setScale(2);
        
        assertEquals(expected, product);
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
        System.out.println("a");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void throwDoubleBuilderOnInfiniteCoefficient() {
        Polynomial.getPolynomialDoubleBuilder()
                  .add(4, Double.NEGATIVE_INFINITY);
        System.out.println("b");
    }
}
