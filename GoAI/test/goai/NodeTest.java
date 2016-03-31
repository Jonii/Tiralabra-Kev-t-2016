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
public class NodeTest {
    
    Node node;
    Pelilauta lauta;
    
    public NodeTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        node = new Node();
        lauta = new Pelilauta();
        
    }
    
    @After
    public void tearDown() {
    }

    /*@Test
    public void testUpdateStats() {
        node.updateStats(1.0);
        assertEquals(node.vierailut, 1);
        assertEquals(node.voitot, 1);
        node.updateStats(0.0);
        assertEquals(node.vierailut, 2);
        assertEquals(node.voitot, 1);
    }
    @Test
    public void testSelectAction() {
        node.selectAction();
        assertNotNull("Expand ei toimi", node.children[0]);
        assertEquals("Update ei toimi", node.vierailut, 1);
    }*/
    
}
