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
    static Pelilauta lauta;
    static PlacementHandler handler;
    static int simulaatioita;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        lauta = new Pelilauta(9);
        handler = new PlacementHandler(lauta);
        Scanner reader = new Scanner(System.in);
        
        int x;
        int y;
        
        while (true) {
            simulaatioita = 0;
            piirraLauta();
            x = reader.nextInt() - 1;
            y = reader.nextInt() - 1;
            handler.pelaaSiirto(x, y);
            
            pelaaSiirtoKoneelle();
            System.out.println("Suoritettu " + simulaatioita + "simulaatiota");
        }
    }
    
    public static void piirraLauta() {
        for (int j = lauta.getKoko()-1; j>=0; j--) {
            for (int i = 0; i < lauta.getKoko(); i++) {
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
        Node root = new Node(lauta);
        long now = System.nanoTime();
        while (System.nanoTime() < now + 2000000000) {
            root.selectAction();
            simulaatioita++;
        }
        int x, y;
        Node uusiNode = root.annaValinta();
        
        x = uusiNode.x;
        y = uusiNode.y;
        
        handler.pelaaSiirto(x,y);
    }
    
}
