package com.github.coderodde.math;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public final class PolynomialTest {
    
    private static final double E = 1E-3;
    
    @Test
    public void evaluate1() {
        Polynomial p1 = new Polynomial(-1.0, 2.0); // 2x - 1
        double value = p1.evaluate(3.0);
        assertEquals(5.0, value, E);
    }
    
    @Test
    public void evaluate2() {
        Polynomial p1 = new Polynomial(-5.0, 3.0, 2.0); // 2x^2 + 3x - 5
        double value = p1.evaluate(4.0);
        assertEquals(39.0, value, E);
    }
    
    @Test
    public void getCoefficient() {
        Polynomial p = new Polynomial(1, 2, 3, 4);
        
        assertEquals(1, p.getCoefficient(0), E);
        assertEquals(2, p.getCoefficient(1), E);
        assertEquals(3, p.getCoefficient(2), E);
        assertEquals(4, p.getCoefficient(3), E);
    }
    
    @Test
    public void length() {
        assertEquals(5, new Polynomial(3, -2, -1, 4, 2).length());
    }
    
    @Test
    public void sum() {
        Polynomial p1 = new Polynomial(3, -1, 2);
        Polynomial p2 = new Polynomial(5, 4);
        Polynomial sum = p1.sum(p2);
        
        Polynomial expected = new Polynomial(8, 3, 2);
        
        assertEquals(expected, sum);
    }

    @Test
    public void getDegree() {
        assertEquals(3, new Polynomial(1, -2, 3, -4));
    }
    
    @Test
    public void multiply() {
        Polynomial p1 = new Polynomial(3, -2, 1); // x^2 - 2x + 3
        Polynomial p2 = new Polynomial(4, 1);     // x + 4
        
        // (x^3 - 2x^2 + 3x) + (4x^2 - 8x + 12) = x^3 + 2x^2 - 5x + 12
        Polynomial product = p1.multiply(p2); 
        
        assertEquals(4, product.getDegree());
        
        Polynomial expected = new Polynomial(12, -5, 2, 1);
        
        assertEquals(expected, product);
    }
    
    @Test
    public void testConstructEmptyPolynomial() {
        Polynomial p = new Polynomial();
        
        assertEquals(1, p.length());
        assertEquals(0, p.getDegree());
        assertEquals(0, p.getCoefficient(0), E);
        assertEquals(0, p.evaluate(4), E);
        assertEquals(0, p.evaluate(-3), E);
    }
    
    @Test
    public void testToString() {
        Polynomial p;
        
        p = new Polynomial();
        
        assertEquals("0", p.toString());
        
        p = new Polynomial(-2);
        
        assertEquals("-2", p.toString());
        
        p = new Polynomial(-2, 3);
        
        assertEquals("3x - 2", p.toString());
        
        p = new Polynomial(-2, -3);
        
        assertEquals("-3x - 2", p.toString());
        
        p = new Polynomial(-2, 0, 5);
        
        assertEquals("5x^2 - 2", p.toString());
    }
}
