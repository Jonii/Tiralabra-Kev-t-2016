/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import goai.Pelilauta;

/**
 *
 * @author jphanski
 */
public class PlacementHandlerTest {
    static PlacementHandler handler;
    static Pelilauta lauta;
    
    
    public PlacementHandlerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        lauta = new Pelilauta();
        handler = new PlacementHandler(lauta);
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
     * Test of onkoLaillinenSiirto method, of class PlacementHandler.
     */
    @Test
    public void testOnkoLaillinenSiirto() {
        System.out.println("onkoLaillinenSiirto");
        int x = 0;
        int y = 0;
        PlacementHandler instance = handler;
        boolean expResult = false;
        boolean result = instance.onkoLaillinenSiirto(x, y);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of pelaaSiirto method, of class PlacementHandler.
     */
    @Test
    public void testPelaaSiirto() {
        System.out.println("pelaaSiirto");
        int x = 0;
        int y = 0;
        PlacementHandler instance = handler;
        instance.pelaaSiirto(x, y);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
    @Test
    public void testLaskeVapaudet() {
        handler.pelaaSiirto(0, 0);
        handler.pelaaSiirto(5, 5);
        assertEquals(4, lauta.getVapaus(5, 5));
        assertEquals(2, lauta.getVapaus(0, 0));
        handler.pelaaSiirto(1, 0);
        assertEquals(3, lauta.getVapaus(0, 0));
    }
    
    @Test
    public void testPoistaKivet() {
        handler.pelaaSiirto(1, 0);
        handler.pelaaSiirto(0, 0);
        handler.pelaaSiirto(0, 1);
        assertEquals(Pelilauta.TYHJA, lauta.getRisteys(0, 0));
        assertEquals(3, lauta.getVapaus(1, 0));
        assertEquals(3, lauta.getVapaus(0, 1));
    }
}
