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
public class PatternTest {

    public PatternTest() {
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
     * Test of match method, of class Pattern.
     */
    @Test
    public void testMatch() {
        
        fail("The test case is a prototype.");
    }

    @Test
    public void testInsertAndDecode() {
        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 2; y++) {
                assertEquals(0, Pattern.decode(0, x, y));
                assertEquals(2, Pattern.decode(Pattern.insert(2, x, y), x, y));
                assertEquals(1, Pattern.decode(Pattern.insert(1, x, y), x, y));

            }
        }
    }

}
