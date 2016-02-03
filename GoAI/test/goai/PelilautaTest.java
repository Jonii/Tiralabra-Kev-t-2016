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
    public void testaaPelataLaudalle() {
        lauta.pelaaSiirto(1, 1);
        assertEquals(lauta.tarkistaRisteys(1, 1), lauta.MUSTA);
        assertEquals(lauta.tarkistaRisteys(0, 0), lauta.TYHJA);
    }
    
    @Test
    public void testaaVaihtuukoVarit() {
        lauta.pelaaSiirto(0, 0);
        lauta.pelaaSiirto(0, 1);
        lauta.pelaaSiirto(0, 2);
        assertEquals(lauta.tarkistaRisteys(0, 1), lauta.VALKEA);
        assertEquals(lauta.tarkistaRisteys(0, 2), lauta.MUSTA);
    }
    
    @Test
    public void testaaPassata() {
        lauta.pelaaSiirto(-1, -1);
        
        for (int i = 0; i<19; i++) {
            for (int j = 0; j<19; j++) {
                assertEquals(lauta.tarkistaRisteys(i, j), lauta.TYHJA);
            }           
        }
    }
    
    @Test
    public void testaaNapataRyhma() {
        lauta.pelaaSiirto(1, 1);
        lauta.pelaaSiirto(1, 2);
        
        lauta.pelaaSiirto(1, 3);
        lauta.pelaaSiirto(2, 2);
        
        lauta.pelaaSiirto(0, 2);
        lauta.pelaaSiirto(10, 10);
        
        lauta.pelaaSiirto(2, 1);
        lauta.pelaaSiirto(10, 11);
        
        lauta.pelaaSiirto(2, 3);
        lauta.pelaaSiirto(10, 12);
        
        lauta.pelaaSiirto(3, 2);
        assertEquals(lauta.tarkistaRisteys(1, 2), lauta.TYHJA);
        assertEquals(lauta.tarkistaRisteys(2, 2), lauta.TYHJA);
    }
}
