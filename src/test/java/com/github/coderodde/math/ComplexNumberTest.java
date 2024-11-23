package com.github.coderodde.math;

import java.math.BigDecimal;
import org.junit.Test;
import static org.junit.Assert.*;

public final class ComplexNumberTest {
    
    @Test
    public void testMultiply() {
        final ComplexNumber c1 = new ComplexNumber(3, 2);
        final ComplexNumber c2 = new ComplexNumber(4, -1);
        
        final ComplexNumber product = c1.multiply(c2);
        
        assertEquals(BigDecimal.valueOf(14).setScale(2),
                     product.getRealPart());
        
        assertEquals(BigDecimal.valueOf(5).setScale(2), 
                     product.getImaginaryPart());
    }
}
