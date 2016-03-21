/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package goai;
import goai.Node;
import goai.Pelilauta;
import java.util.Scanner;
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
        handler = new PlacementHandler(lauta);
        Scanner reader = new Scanner(System.in);
        
        int x;
        int y;
        
        while (true) {
            piirraLauta();
            x = reader.nextInt();
            y = reader.nextInt();
            handler.pelaaSiirto(x, y);
            
            pelaaSiirtoKoneelle();
        }
    }
    
    public static void piirraLauta() {
        for (int i = 0; i<19; i++) {
            for (int j = 0; j<19; j++) {
                if (lauta.getRisteys(i, j) == Pelilauta.MUSTA) {
                    System.out.print(" X ");
                }
                else if (lauta.getRisteys(i, j) == Pelilauta.VALKEA) {
                    System.out.print(" O "); 
                }
                else System.out.print(" . ");
            }
            System.out.println();
        }
    }

    private static void pelaaSiirtoKoneelle() {
        long now = System.nanoTime();
        while (System.nanoTime() < now + 2000000000) {
            root.selectAction();
        }
        int simple, x, y;
        simple = root.annaValinta();
        
        x = lauta.transformToXCoordinate(simple);
        y = lauta.transformToYCoordinate(simple);
        
        handler.pelaaSiirto(x,y);
    }
    
}
