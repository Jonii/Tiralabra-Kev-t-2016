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
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of onkoLaillinenSiirto method, of class PlacementHandler.
     */
    @Test
    public void testOnkoLaillinenSiirto() {
        assertEquals(true, PlacementHandler.onkoLaillinenSiirto(lauta, 0, 0));
        assertEquals(true, PlacementHandler.onkoLaillinenSiirto(lauta, 18, 18));
        PlacementHandler.pelaaSiirto(lauta, 0, 0);
        assertEquals(false, PlacementHandler.onkoLaillinenSiirto(lauta, 0, 0));
        assertEquals(false, PlacementHandler.onkoLaillinenSiirto(lauta, -1, 0));
        assertEquals(false, PlacementHandler.onkoLaillinenSiirto(lauta, 0, 19));
        PlacementHandler.pelaaSiirto(lauta, 1, 0);
        PlacementHandler.pelaaSiirto(lauta, 1, 1);
        PlacementHandler.pelaaSiirto(lauta, 0, 1);
        assertEquals(false, PlacementHandler.onkoLaillinenSiirto(lauta, 0, 0));
        PlacementHandler.pelaaSiirto(lauta, 2, 0);
        PlacementHandler.pelaaSiirto(lauta, 10, 10);
        assertEquals(true, PlacementHandler.onkoLaillinenSiirto(lauta, 0, 0));
    }

    /**
     * Test of pelaaSiirto method, of class PlacementHandler.
     */
    @Test
    public void testPelaaSiirto() {
        assertEquals(Pelilauta.TYHJA, lauta.getRisteys(0, 0));
        PlacementHandler.pelaaSiirto(lauta, 0, 0);
        assertEquals(Pelilauta.MUSTA, lauta.getRisteys(0, 0));
        PlacementHandler.pelaaSiirto(lauta, 1, 0);
        assertEquals(Pelilauta.VALKEA, lauta.getRisteys(1, 0));
        PlacementHandler.pelaaSiirto(lauta, 2, 0);
        assertEquals(Pelilauta.MUSTA, lauta.getRisteys(2, 0));
    }
    
    @Test
    public void testLaskeVapaudet() {
        PlacementHandler.pelaaSiirto(lauta, 0, 0);
        PlacementHandler.pelaaSiirto(lauta, 18, 18);
        PlacementHandler.pelaaSiirto(lauta, 5, 5);
        assertEquals(4, lauta.getVapaus(5, 5));
        assertEquals(2, lauta.getVapaus(18, 18));     
        assertEquals(2, lauta.getVapaus(0, 0));    
        PlacementHandler.pelaaSiirto(lauta, 15, 15);
        PlacementHandler.pelaaSiirto(lauta, 1, 0);
        assertEquals(3, lauta.getVapaus(0, 0));
        PlacementHandler.pelaaSiirto(lauta, 2, 0);
        assertEquals(2, lauta.getVapaus(2, 0));        
        assertEquals(2, lauta.getVapaus(1, 0));
        assertEquals(2, lauta.getVapaus(0, 0));
    }
    
    @Test
    public void testPoistaKivet() {
        PlacementHandler.pelaaSiirto(lauta, 1, 0);
        PlacementHandler.pelaaSiirto(lauta, 0, 0);
        PlacementHandler.pelaaSiirto(lauta, 0, 1);
        assertEquals(Pelilauta.TYHJA, lauta.getRisteys(0, 0));
        assertEquals(3, lauta.getVapaus(1, 0));
        assertEquals(3, lauta.getVapaus(0, 1));
    }
    
    @Test
    public void testSilmanTuhoamisenHuomaaminen() {
        PlacementHandler.pelaaSiirto(lauta, 1, 0); //                                    ______
        PlacementHandler.pelaaSiirto(lauta, 0, 0); //                                   | x
        PlacementHandler.pelaaSiirto(lauta, 0, 1); //                                   |xx
        PlacementHandler.pelaaSiirto(lauta, 3, 3); //                                   |
        assertEquals(false, PlacementHandler.tuhoaakoSiirtoOmanSilman(lauta, 0, 0)); // |    o
        PlacementHandler.pelaaSiirto(lauta, 1, 1);
        assertEquals(false, PlacementHandler.tuhoaakoSiirtoOmanSilman(lauta, 0, 0));
        PlacementHandler.pass(lauta);
        assertEquals(true, PlacementHandler.tuhoaakoSiirtoOmanSilman(lauta, 0, 0));
    }
}
