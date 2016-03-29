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
public class NodenLapsetTest {
    NodenLapset children;
    Pelilauta lauta;
    
    public NodenLapsetTest() {
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
        Node[] lista = new Node[5];
        for (int i = 0; i<5; i++) {
            lista[i] = new Node(lauta, 1*i, 1*i);
        }
        children = new NodenLapset(lista);
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getNode method, of class NodenLapset.
     */
    @Test
    public void testGetNode() {
        assertEquals(4, children.getNode(0).getX());
        assertEquals(3, children.getNode(1).getY());
    }
  

    /**
     * Test of find method, of class NodenLapset.
     */
    @Test
    public void testFind() {
        assertTrue(children.find(lauta.transformToSimpleCoordinates(4, 4)) == 0);
        assertTrue(children.find(lauta.transformToSimpleCoordinates(3, 4)) == -1);
        assertTrue(children.find(lauta.transformToSimpleCoordinates(3, 3)) == 1);
        assertTrue(children.find(lauta.transformToSimpleCoordinates(0, 0)) == 4);
    }
    
}
