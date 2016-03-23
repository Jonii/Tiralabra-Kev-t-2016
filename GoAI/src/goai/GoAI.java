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
    static Node diagnostiikkaNode;
    static int[][] apulauta;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.print("Anna laudan koko: ");
        Scanner reader = new Scanner(System.in);
        
        lauta = new Pelilauta(reader.nextInt());
        System.out.println("");
        
        handler = new PlacementHandler(lauta);
        
        
        int x;
        int y;
        apulauta = new int[lauta.getKoko()][lauta.getKoko()];
        
        while (true) {
            simulaatioita = 0;
            
            piirraLauta();
            x = reader.nextInt() - 1;
            y = reader.nextInt() - 1;
            handler.pelaaSiirto(x, y);
            
            diagnostiikkaNode = pelaaSiirtoKoneelle();
            apulauta = new int[lauta.getKoko()][lauta.getKoko()];
            for (int i = 0; i<diagnostiikkaNode.children.length; i++) {
                x = diagnostiikkaNode.children[i].x;
                y = diagnostiikkaNode.children[i].y;
                apulauta[x][y] = diagnostiikkaNode.children[i].voitot;
            }
            System.out.println("Suoritettu " + simulaatioita + " simulaatiota");
        }
    }
    
    public static void piirraLauta() {
        for (int j = lauta.getKoko()-1; j>=0; j--) {
            System.out.format("%2d", (j+1));
            System.out.print("|");
            
            for (int i = 0; i < lauta.getKoko(); i++) {
                if (lauta.getRisteys(i, j) == Pelilauta.MUSTA) {
                    System.out.print(" X ");
                }
                else if (lauta.getRisteys(i, j) == Pelilauta.VALKEA) {
                    System.out.print(" O "); 
                }
                else System.out.print(" . ");
            }
            
            //for (int i = 0; i < lauta.getKoko(); i++) {                 //aputaulu diagnostiikkaa varten
            //    System.out.format("(%5d) ", apulauta[i][j]);
            //}
            // 
            System.out.println();
        }
        System.out.print("   ");
        for (int i = 0; i<lauta.getKoko(); i++) {
            System.out.format("%2d ", (i+1));
        }
        System.out.print("\nAnna seuraava siirto: ");
    }

    private static Node pelaaSiirtoKoneelle() {
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
        
        return root;
    }
    
}
