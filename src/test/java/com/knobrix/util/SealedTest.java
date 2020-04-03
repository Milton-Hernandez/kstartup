
package com.knobrix.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author milton
 */
public class SealedTest {
    
    public SealedTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of set method, of class Sealed.
     */
    @Test
    public void testSet() {
        var prop = new Property<Integer>();
        var intVal = 5;
        prop.set(intVal);
        if(!prop.equals(intVal))
            fail("Expected " + intVal + ", found " + prop.get());
        
        
        var prop2 = new Property<Double>(0.001);
        if(!prop2.equals(0.001))
            fail("Expected 0.001, found " + prop2.get());
        
        boolean pass = true;
        
        try {
            prop2.set(100.0);
            pass = false;
            
        }
        catch(Throwable t) { }
        assertTrue(pass);
    }

    /**
     * Test of get method, of class Sealed.
     */
    @Test
    public void testGet() {
      var prop1 = new Property<String>();
      boolean pass = true;
      try {
          var res = prop1.get();
          pass = false;
      } catch(Throwable t) {}
      assertTrue(pass);
      
      pass = true;
      prop1.set("Sample");
      try {
          var res = prop1.get();
          if(!res.equals("Sample"))
              fail("Fail to get value after set.");
      } catch(Throwable t) { pass = false; }
      assertTrue(pass);
    }

    /**
     * Test of hashCode method, of class Sealed.
     */
    @Test
    public void testHashCode() {
       Integer val = 100;
       var hc1 = val.hashCode();
       var prop1 = new Property<Integer>(100);
       assertEquals(hc1,prop1.hashCode());
    }

    /**
     * Test of equals method, of class Sealed.
     */
    @Test
    public void testEquals() {
       var prop1 = new Property<String>("String Test");
       var prop2 = new Property<String>("String Test");
       var prop3 = new Property<String>("Different One");
       var prop4 = new Property<String>();
       
       assertTrue(prop1.equals(prop2));
       assertTrue(prop2.equals(prop1));
       assertFalse(prop2.equals(prop4));
       assertFalse(prop1.equals(prop3));
       assertFalse(prop4.equals(prop1));
       assertFalse(prop1.equals(prop4));
       assertTrue(prop1.equals("String Test"));
       assertTrue(prop2.equals("String Test"));
       assertFalse(prop4.equals("String Test"));
       assertFalse(prop1.equals("Different One"));
       assertFalse(prop1.equals(0.0));
    }

    /**
     * Test of compareTo method, of class Sealed.
     */
    @Test
    public void testCompareTo() {
        var prop1 = new Property<Integer>(3);
        var prop2 = new Property<Integer>(2);
        assertEquals(prop1.compareTo(prop2), prop1.get().compareTo(prop2.get()));
        assertEquals(prop1.compareTo(3), prop1.get().compareTo(3));
        
        var p1 = prop1.compareTo(2);
        var p2 = prop1.get().compareTo(2);
        assertEquals(p1 , p2);
        
        p1 = prop2.compareTo(3);
        p2 = prop2.get().compareTo(3);
        assertEquals(p1 , p2);
        
        assertEquals(prop1.compareTo(prop2),  1);
        assertEquals(prop2.compareTo(prop1), -1);
        assertEquals(prop2.compareTo(prop2),  0);
    }
    
}
