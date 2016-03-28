/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package goai;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jphanski
 */
public class GTPTest {
    
    public GTPTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }


    /**
     * Test of readFirstCoord method, of class GTP.
     */
    @Test
    public void testReadFirstCoord() {
        assertTrue(GTP.readFirstCoord("A5") == 0);
        assertTrue(GTP.readFirstCoord("B5") == 1);
        assertTrue(GTP.readFirstCoord("A1") == 0);
        assertTrue(GTP.readFirstCoord("H5") == 7);
        assertTrue(GTP.readFirstCoord("J5") == 8);
        assertTrue(GTP.readFirstCoord("C5") == 2);
        assertTrue(GTP.readFirstCoord("D5") == 3);
        assertTrue(GTP.readFirstCoord("E5") == 4);
        assertTrue(GTP.readFirstCoord("F5") == 5);
        assertTrue(GTP.readFirstCoord("G5") == 6);
        assertTrue(GTP.readFirstCoord("J1") == 8);
    }

    /**
     * Test of readSecondCoord method, of class GTP.
     */
    @Test
    public void testReadSecondCoord() {
        assertTrue(GTP.readSecondCoord("A5") == 4);
        assertTrue(GTP.readSecondCoord("B15") == 14);
        assertTrue(GTP.readSecondCoord("A1") == 0);
        assertTrue(GTP.readSecondCoord("H5") == 4);
        assertTrue(GTP.readSecondCoord("J10") == 9);
    }

    /**
     * Test of produceCoord method, of class GTP.
     */
    @Test
    public void testProduceCoord() {
        assertEquals(0, GTP.produceCoord(0, 0).compareTo("A1"));
        assertEquals(0, GTP.produceCoord(1, 0).compareTo("B1"));
        assertEquals(0, GTP.produceCoord(0, 1).compareTo("A2"));
        assertEquals(0, GTP.produceCoord(1, 1).compareTo("B2"));
        assertEquals(0, GTP.produceCoord(8, 1).compareTo("J2"));
        assertEquals(0, GTP.produceCoord(1, 8).compareTo("B9"));
        assertEquals(0, GTP.produceCoord(8, 8).compareTo("J9"));
    }
    
}
