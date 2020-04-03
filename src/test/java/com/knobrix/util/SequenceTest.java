/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.knobrix.util;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author milton
 */
public class SequenceTest {
    
    public SequenceTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }

    /**
     * Test of hasDups method, of class Sequence.
     */
    @Test
    public void testHasDups() {
        var arg = new String[] {"a","b","c"};
        Sequence instance = new Sequence();
        boolean expResult = false;
        boolean result = instance.hasDups(arg);
        assertEquals(expResult, result);
    }


    @Test
    public void testBuild() {
        var arg = new String[] {"a","b","c"};
        Sequence instance = new Sequence(arg);
        assertEquals(instance.get(0),"a");
    }


    @Test
    public void testSet() {
        var arg = new String[] {"a","b","c"};
        Sequence instance = new Sequence();
        instance.set(arg);
        assertEquals(instance.get(0),"a");
    }


    @Test
    public void testGet() {
        var arg = new String[] {"a","b","c"};
        Sequence instance = new Sequence();
        instance.set(arg);
        for(int i=0; i<arg.length; i++)
            assertEquals(instance.get(i),arg[i]);
    }

  
    @Test
    public void testLocate() {
     var arg = new Integer[] {100,200,300};
     Sequence instance = new Sequence();
     instance.set(arg);
     assertEquals(instance.locate(200),1);
     assertEquals(instance.locate(500),-1);
    }

    @Test
    public void testDups() {
     var arg = new String[] {"a","b","c","b"};
     Sequence instance = new Sequence();
     boolean pass = true;
     try {
       instance.set(arg);
       pass = false;
     }
     catch(Throwable t) { }
     if(!pass)
         fail("Failed to detect Dups");
    }
   
}
