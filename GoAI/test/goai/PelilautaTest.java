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
public class PelilautaTest {
    Pelilauta lauta;
    public PelilautaTest() {
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
  
    @Test
    public void testaaToimiikoKoordinaattimuunnokset() {
         int x = 7;
         int y = 10;
         assertEquals(7, lauta.transformToXCoordinate(lauta.transformToSimpleCoordinates(x, y)));
         assertEquals(10, lauta.transformToYCoordinate(lauta.transformToSimpleCoordinates(x, y)));
         
         int simple = 120;
         x = lauta.transformToXCoordinate(simple);
         y = lauta.transformToYCoordinate(simple);
         assertEquals(120, lauta.transformToSimpleCoordinates(x, y));
    }
    
    @Test
    public void testaaSiirtymat() {
        assertEquals(-1, lauta.moveLeft(lauta.transformToSimpleCoordinates(5, 0)));
        assertEquals(-1, lauta.moveRight(lauta.transformToSimpleCoordinates(12, 18)));
        assertEquals(-1, lauta.moveUp(lauta.transformToSimpleCoordinates(18, 2)));
        assertEquals(-1, lauta.moveDown(lauta.transformToSimpleCoordinates(0, 16)));
        assertEquals(lauta.transformToSimpleCoordinates(6, 2), lauta.moveLeft(lauta.transformToSimpleCoordinates(6, 3)));
        assertEquals(lauta.transformToSimpleCoordinates(6, 4), lauta.moveRight(lauta.transformToSimpleCoordinates(6, 3)));
        assertEquals(lauta.transformToSimpleCoordinates(5, 3), lauta.moveDown(lauta.transformToSimpleCoordinates(6, 3)));
        assertEquals(lauta.transformToSimpleCoordinates(7, 3), lauta.moveUp(lauta.transformToSimpleCoordinates(6, 3)));
        assertNotEquals(-1, lauta.moveRight(lauta.transformToSimpleCoordinates(0, 0)));
        assertNotEquals(-1, lauta.moveUp(lauta.transformToSimpleCoordinates(0, 0)));
        assertEquals(-1, lauta.moveDown(lauta.transformToSimpleCoordinates(0, 0)));
        assertEquals(-1, lauta.moveLeft(lauta.transformToSimpleCoordinates(0, 0)));
    }

}
