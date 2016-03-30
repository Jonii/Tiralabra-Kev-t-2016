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
         assertEquals(7, lauta.toX(lauta.toSimple(x, y)));
         assertEquals(10, lauta.toY(lauta.toSimple(x, y)));
         
         int simple = 120;
         x = lauta.toX(simple);
         y = lauta.toY(simple);
         assertEquals(120, lauta.toSimple(x, y));
    }
    
    @Test
    public void testaaSiirtymat() {
        assertEquals(-1, lauta.moveLeft(lauta.toSimple(0, 5)));
        assertEquals(-1, lauta.moveRight(lauta.toSimple(18, 12)));
        assertEquals(-1, lauta.moveUp(lauta.toSimple(4, 18)));
        assertEquals(-1, lauta.moveDown(lauta.toSimple(6, 0)));
        assertEquals(lauta.toSimple(5, 3), lauta.moveLeft(lauta.toSimple(6, 3)));
        assertEquals(lauta.toSimple(7, 3), lauta.moveRight(lauta.toSimple(6, 3)));
        assertEquals(lauta.toSimple(6, 2), lauta.moveDown(lauta.toSimple(6, 3)));
        assertEquals(lauta.toSimple(6, 4), lauta.moveUp(lauta.toSimple(6, 3)));
        assertNotEquals(-1, lauta.moveRight(lauta.toSimple(0, 0)));
        assertNotEquals(-1, lauta.moveUp(lauta.toSimple(0, 0)));
        assertEquals(-1, lauta.moveDown(lauta.toSimple(0, 0)));
        assertEquals(-1, lauta.moveLeft(lauta.toSimple(0, 0)));
    }

}
