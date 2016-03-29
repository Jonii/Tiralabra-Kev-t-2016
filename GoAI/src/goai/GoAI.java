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
    static int simulaatioita;
    static Node diagnostiikkaNode;
    static int[][] apulautaVierailut;
    static int[][] apulautaVoitot;
    static int passauksia;
    static int resign = 0;

    /**
     * @param args kvk = kone vastaan kone matsi, gtp = Go Text Protocol interfacen käyttö tavallisen käyttöliittymän sijaan.
     */
    public static void main(String[] args) {
        boolean koneVastaanKone = false;
        int x;
        int y;
        String komento;

        if ((args.length > 0) && (args[0].compareToIgnoreCase("gtp") == 0)) {
            lauta = new Pelilauta(7);
            GTP.read();
        } else {
            if ((args.length > 0) && (args[0].compareToIgnoreCase("kvk") == 0)) {
                koneVastaanKone = true;
            }
            Scanner reader = new Scanner(System.in);
            /*System.out.print("Anna laudan koko: ");
            
            lauta = new Pelilauta(reader.nextInt());
            System.out.println("");

            reader.nextLine();*/
            //System.out.print("Tulostetaanko diagnostiikkalauta(k/E): ");
            //if (reader.nextLine().compareToIgnoreCase("k") == 0) {
            
            /* Asetetaan sopivat alkuarvot Tietokone vs Ihminen -matsille:
                6.5 komi, 9x9 laudankoko. Diagnostiikkataulu pois näkyvistä.
            */
            lauta = new Pelilauta(9);
            lauta.setKomi(6.5);
            if (true) {
                apulautaVierailut = new int[lauta.getKoko()][lauta.getKoko()];
                apulautaVoitot = new int[lauta.getKoko()][lauta.getKoko()];
            }

            while (resign == 0) {
                simulaatioita = 0;
                
                piirraLauta(lauta);
                System.out.print("Tietokone miettii...");
                pelaaSiirtoKoneelle();

                System.out.println("Suoritettu " + simulaatioita + " simulaatiota");
                if (koneVastaanKone) continue; //uncomment this if you want to play yourself
                piirraLauta(lauta);
                if (lauta.getTurn() == Pelilauta.MUSTA) {
                    System.out.println("Mustan vuoro");
                } else {
                    System.out.println("Valkean vuoro");
                }
                System.out.print("Anna seuraava siirto: ");
                do {
                    komento = reader.nextLine();
                    x = GTP.readFirstCoord(komento);
                    y = GTP.readSecondCoord(komento);
                } while (!PlacementHandler.onkoLaillinenSiirto(lauta, x, y));

                PlacementHandler.pelaaSiirto(lauta, x, y);

            }
            if (resign == Pelilauta.MUSTA) {
                System.out.println("Musta luovutti");
            }
            else System.out.println("Valkea luovutti");
        }
    }

    public static void piirraLauta(Pelilauta lauta) {
        System.out.println("passauksia: " + passauksia);
        for (int j = lauta.getKoko() - 1; j >= 0; j--) {
            System.out.format("%2d", (j + 1));
            System.out.print("|");

            for (int i = 0; i < lauta.getKoko(); i++) {
                if (lauta.getRisteys(i, j) == Pelilauta.MUSTA) {
                    System.out.print(" X ");
                } else if (lauta.getRisteys(i, j) == Pelilauta.VALKEA) {
                    System.out.print(" O ");
                } else {
                    System.out.print(" . ");
                }
            }

            if (apulautaVierailut != null) {
                for (int i = 0; i < lauta.getKoko(); i++) {                 //aputaulu diagnostiikkaa varten
                    System.out.format("(%4d/%5d)", apulautaVoitot[i][j], apulautaVierailut[i][j]);
                }
            }
            System.out.println();
        }
        System.out.print("   ");
        int koko = lauta.getKoko();
        if (koko > 7) koko++;
        for (int i = 0; i < koko; i++) {
            if (i == 8) continue;
            System.out.print(" " + ((char) (i + 'A')) + " ");
        }
        if (lauta.isPassedOnLastMove() && lauta.getMoveNumber() > 1) {
            System.out.print("Passaus");
        }
        System.out.println("");

    }
    
    /**
     * Pelaa siirron tietokoneelle. Päivittää siinä sivussa myös
     * diagnostiikkataulua jonka avulla voi kartoittaa 
     */
    private static void pelaaSiirtoKoneelle() {
        Node root = new Node(lauta);
        long now = System.currentTimeMillis();
        int miettimisAika = 400;
        int n = 1;
        while (System.currentTimeMillis() < now + miettimisAika) {
            root.selectAction();
            simulaatioita++;
            if (System.currentTimeMillis() > now + (n * miettimisAika)/10) {
                System.out.print(".");
                n++;
            }
        }
        int x, y;
        Node uusiNode = root.annaValinta();

        if (uusiNode == null) {
            PlacementHandler.pass(lauta);
            return;
        }

        x = uusiNode.getX();
        y = uusiNode.getY();
        
        if (1.0 * uusiNode.voitot / uusiNode.vierailut < 0.2) {
            resign = lauta.getTurn();
        }

        PlacementHandler.pelaaSiirto(lauta, x, y);

        if (apulautaVierailut != null) {
            diagnostiikkaNode = root;
            apulautaVierailut = new int[lauta.getKoko()][lauta.getKoko()];
            apulautaVoitot = new int[lauta.getKoko()][lauta.getKoko()];
            passauksia = 0;
            int length = 0;
            if (diagnostiikkaNode != null) {
                length = diagnostiikkaNode.children.getKoko();
            }

            for (int i = 0; i < length; i++) {
                x = diagnostiikkaNode.children.getNode(i).getX();
                y = diagnostiikkaNode.children.getNode(i).getY();
                if ((x == -1) && (y == -1)) {
                    passauksia++;
                } else {
                    apulautaVierailut[x][y] = diagnostiikkaNode.children.getNode(i).vierailut;
                    apulautaVoitot[x][y] = diagnostiikkaNode.children.getNode(i).voitot;
                }
            }
        }
        
    }

}
