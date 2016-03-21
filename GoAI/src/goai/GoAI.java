/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package goai;
import goai.Node;
import goai.Pelilauta;
import logic.PlacementHandler;
/**
 *
 * @author jphanski
 */
public class GoAI {
    static Node root;
    static Pelilauta lauta;
    static PlacementHandler handler;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        lauta = new Pelilauta();
        root = new Node(lauta);
    }
    
    public static void piirraLauta() {
        for (int i = 0; i<19; i++) {
            for (int j = 0; j<19; j++) {
                if (lauta.getRisteys(i, j) == Pelilauta.MUSTA) {
                    System.out.print(" X ");
                }
                else System.out.print(" O "); 
            }
            System.out.println();
        }
    }
    
}
