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
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        lauta = new Pelilauta();
        handler = new PlacementHandler(lauta);
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of onkoLaillinenSiirto method, of class PlacementHandler.
     */
    @Test
    public void testOnkoLaillinenSiirto() {
        assertEquals(true, handler.onkoLaillinenSiirto(0, 0));
        assertEquals(true, handler.onkoLaillinenSiirto(18, 18));
        handler.pelaaSiirto(0, 0);
        assertEquals(false, handler.onkoLaillinenSiirto(0, 0));
        assertEquals(false, handler.onkoLaillinenSiirto(-1, 0));
        assertEquals(false, handler.onkoLaillinenSiirto(0, 19));
        handler.pelaaSiirto(1, 0);
        handler.pelaaSiirto(1, 1);
        handler.pelaaSiirto(0, 1);
        assertEquals(false, handler.onkoLaillinenSiirto(0, 0));
        handler.pelaaSiirto(2, 0);
        handler.pelaaSiirto(10, 10);
        assertEquals(true, handler.onkoLaillinenSiirto(0, 0));
    }

    /**
     * Test of pelaaSiirto method, of class PlacementHandler.
     */
    @Test
    public void testPelaaSiirto() {
        assertEquals(Pelilauta.TYHJA, lauta.getRisteys(0, 0));
        handler.pelaaSiirto(0, 0);
        assertEquals(Pelilauta.MUSTA, lauta.getRisteys(0, 0));
        handler.pelaaSiirto(1, 0);
        assertEquals(Pelilauta.VALKEA, lauta.getRisteys(1, 0));
        handler.pelaaSiirto(2, 0);
        assertEquals(Pelilauta.MUSTA, lauta.getRisteys(2, 0));
    }
    
    @Test
    public void testLaskeVapaudet() {
        handler.pelaaSiirto(0, 0);
        handler.pelaaSiirto(18, 18);
        handler.pelaaSiirto(5, 5);
        assertEquals(4, lauta.getVapaus(5, 5));
        assertEquals(2, lauta.getVapaus(18, 18));     
        assertEquals(2, lauta.getVapaus(0, 0));    
        handler.pelaaSiirto(15, 15);
        handler.pelaaSiirto(1, 0);
        assertEquals(3, lauta.getVapaus(0, 0));
        handler.pelaaSiirto(2, 0);
        assertEquals(2, lauta.getVapaus(2, 0));        
        assertEquals(2, lauta.getVapaus(1, 0));
        assertEquals(2, lauta.getVapaus(0, 0));
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
    
    @Test
    public void testSilmanTuhoamisenHuomaaminen() {
        handler.pelaaSiirto(1, 0); //                                    ______
        handler.pelaaSiirto(0, 0); //                                   | x
        handler.pelaaSiirto(0, 1); //                                   |xx
        handler.pelaaSiirto(3, 3); //                                   |
        assertEquals(false, handler.tuhoaakoSiirtoOmanSilman(0, 0)); // |    o
        handler.pelaaSiirto(1, 1);
        assertEquals(false, handler.tuhoaakoSiirtoOmanSilman(0, 0));
        handler.pass();
        assertEquals(true, handler.tuhoaakoSiirtoOmanSilman(0, 0));
    }
}
