/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package goai;

import goai.Node;
import goai.Pelilauta;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import logic.Pino;
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
    public static int simulaatioCount = 4000;
    private static boolean gtp = false;
    public static double simulateKomi;
    public static int actuaSimulateWins;
    public static int actualSimulateGames;
    public static Logger logger;
    
    /**
     * By default logging is enabled, and log file will go to this file.
     * Change at will. Also use -logfile <file path> command line option
     */
    public static String logFile = "/home/jphanski/BottiLogs/generic/gtp.log";

    /**
     * @param args kvk = kone vastaan kone matsi, gtp = Go Text Protocol
     * interfacen käyttö tavallisen käyttöliittymän sijaan.
     */
    public static void main(String[] args) {
        
        boolean koneVastaanKone = false;
        int x;
        int y;
        String komento;
        boolean selfLearn = false;
        
        int i = 0;
        while (i < args.length) {

            /**
             * GTP vai ei GTP:tä.
             */
            if (args[i].compareToIgnoreCase("-gtp") == 0) {
                gtp = true;
            } /**
             * Simulaatioiden määrä per siirto.
             */
            else if (args[i].compareToIgnoreCase("-simulations") == 0) {
                i++;
                simulaatioCount = Integer.parseInt(args[i]);
            } /**
             * Kuinka paljolti haaraudutaan per siirto.
             */
            else if (args[i].compareToIgnoreCase("-branches") == 0) {
                i++;
                Node.branchingFactor = Integer.parseInt(args[i]);
            } /**
             * Kuinka paljon painotetaan RAVEn tuotoksia. Jos -rave 1000,
             * tällöin RAVEn käyttö lopetetaan Noden kohdalla jossa on vierailtu
             * 1000 kertaa.
             */
            else if (args[i].compareToIgnoreCase("-rave") == 0) {
                i++;
                Node.raveSuoritukset = Integer.parseInt(args[i]);
            } 
            else if (args[i].compareToIgnoreCase("-logfile") == 0) {
                i++;
                GoAI.logFile = args[i];
            }
            else if (args[i].compareToIgnoreCase("-kvk") == 0) {
                koneVastaanKone = true;
            } else if (args[i].compareToIgnoreCase("-learn") == 0) {
                i++;
                selfLearn = true;
            }
            i++;
        }
        
        logger = Logger.getLogger("goailog");
        logger.setUseParentHandlers(false);
        
        FileHandler fh;

        try {

            // This block configure the logger with handler and formatter  
            fh = new FileHandler(GoAI.logFile, true);
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        
        Pattern.init();
        
        if (selfLearn) {
            BayesLearner.main();
        }
        else if (gtp) {
            GTP.read();
        } else {

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
            Pelilauta.setKomi(6.5);
            PlacementHandler.pelaaSiirto(lauta, 3, 3); // ________
            PlacementHandler.pelaaSiirto(lauta, 3, 4); //|
            PlacementHandler.pelaaSiirto(lauta, 4, 4); //|
            PlacementHandler.pass(lauta);              //|
            PlacementHandler.pelaaSiirto(lauta, 2, 4); //|     X
                                                       //|   X O X
            /*Pelilauta simuLauta;
            int voitot = 1;
            int simut = 1;
            int[] amafTaulu = new int[9*9];
            while (true) {
                simuLauta = lauta.kopioi();
                Node.simulate(simuLauta, amafTaulu);
                if (Node.simulScore(simuLauta) > 0) voitot++;
                simut++;
                System.out.println("Voittoprosentti: " + (1.0 * voitot / simut));
            }*/
            
            if (true) {
                apulautaVierailut = new int[lauta.getKoko()][lauta.getKoko()];
                apulautaVoitot = new int[lauta.getKoko()][lauta.getKoko()];
            }

            while (resign == 0) {
                simulaatioita = 0;

                piirraLauta(lauta);
                System.out.print("Tietokone miettii...");
                GTP.decideSimulationKomi(lauta); 
                pelaaSiirtoKoneelle();

                System.out.println("Suoritettu " + simulaatioita + " simulaatiota");
                if (koneVastaanKone) {
                    continue; //uncomment this if you want to play yourself
                }
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
            } else {
                System.out.println("Valkea luovutti");
            }
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
        if (koko > 7) {
            koko++;
        }
        for (int i = 0; i < koko; i++) {
            if (i == 8) {
                continue;
            }
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
        Node root = new Node();
        root.setTurn(lauta.getTurn());

        long now = System.currentTimeMillis();
        int miettimisAika = 22500;
        int n = 1;
        while (System.currentTimeMillis() < now + miettimisAika) {
            root.selectAction(lauta);
            simulaatioita++;
            if (System.currentTimeMillis() > now + (n * miettimisAika) / 10) {
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

        if (1.0 * uusiNode.voitot / uusiNode.vierailut < 0.15) {
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
                    apulautaVierailut[x][y] = diagnostiikkaNode.children.getNode(i).raveVierailut;
                    apulautaVoitot[x][y] = diagnostiikkaNode.children.getNode(i).raveVoitot;
                    //apulautaVierailut[x][y] = diagnostiikkaNode.children.getNode(i).vierailut;
                    //apulautaVoitot[x][y] = diagnostiikkaNode.children.getNode(i).voitot;
                }
            }
        }

    }

    public static boolean[] decideDead(Pelilauta lauta) {
        Pelilauta simuLauta;
        int[] amafTaulu = new int[Pelilauta.getKoko() * Pelilauta.getKoko()];
        int[] ryhmaTaulu = new int[Pelilauta.getKoko() * Pelilauta.getKoko()];
        int[] collisionTaulu = new int[Pelilauta.getKoko() * Pelilauta.getKoko()];

        for (int i = 0; i < Pelilauta.getKoko() * Pelilauta.getKoko(); i++) {
            if (lauta.getRisteys(Pelilauta.toX(i), Pelilauta.toY(i)) != Pelilauta.TYHJA) {
                ryhmaTaulu[i] = lauta.getRisteys(Pelilauta.toX(i), Pelilauta.toY(i));
            }
        }
        for (int j = 0; j < 100; j++) {
            simuLauta = lauta.kopioi();
            Node.simulate(simuLauta, amafTaulu);
            for (int i = 0; i < Pelilauta.getKoko() * Pelilauta.getKoko(); i++) {
                if (ryhmaTaulu[i] != 0) {
                    if (simuLauta.getRisteys(Pelilauta.toX(i), Pelilauta.toY(i)) != ryhmaTaulu[i]) {
                        collisionTaulu[i]++;
                    }
                }
            }

        }
        boolean[] palautus = new boolean[Pelilauta.getKoko() * Pelilauta.getKoko()];
        for (int i = 0; i < Pelilauta.getKoko() * Pelilauta.getKoko(); i++) {
            if (collisionTaulu[i] == 100) {
                palautus[i] = true;
            }
        }
        return palautus;
    }

    public static double laskePisteet(boolean[] kuolleet, Pelilauta lauta) {
        final int koko = Pelilauta.getKoko();
        int pelaaja;
        Pino<Integer> pino = new Pino<>();
        Pino<Integer> emptyPino = new Pino<>();

        boolean[] visited = new boolean[koko * koko];
        boolean[] visitedBackup = new boolean[koko * koko];
        boolean noPoints = false;
        int uusiSimple;

        int currentX, currentY;

        int current = lauta.toSimple(0, 0);
        pino.add(current);
        
        
        double pisteet = 0; 
        pisteet = lauta.getKomi() * (-1);
        int pisteetTyhjaltaAlueelta = 0;

        while (pino.IsNotEmpty()) {

            current = pino.pop();

            currentX = Pelilauta.toX(current);
            currentY = Pelilauta.toY(current);

            if (visited[current]) {
                continue;
            }
            
            visited[current] = true;

            if (lauta.getRisteys(currentX, currentY) == Pelilauta.TYHJA || kuolleet[current]) {
                visited[current] = false; //Tämä jotta tyhjien risteyksien läpikäynti ei keskeydy heti alkuunsa.
                
                for (int i = 0; i < koko * koko; i++) {
                    visitedBackup[i] = visited[i];
                }
                pelaaja = Pelilauta.TYHJA;
                noPoints = false;
                pisteetTyhjaltaAlueelta = 0;
                

                emptyPino.add(current);

                while (emptyPino.IsNotEmpty()) {
                    current = emptyPino.pop();

                    if (visited[current]) {
                        continue;
                    }
                    
                    visited[current] = true;

                    uusiSimple = Pelilauta.moveLeft(current);
                    if (uusiSimple != -1) {
                        pino.add(uusiSimple);
                        if (lauta.getRisteys(Pelilauta.toX(uusiSimple), Pelilauta.toY(uusiSimple)) == Pelilauta.TYHJA || kuolleet[uusiSimple]) {
                            emptyPino.add(uusiSimple);
                        } else if (!noPoints) {
                            if (pelaaja == Pelilauta.TYHJA) {
                                pelaaja = lauta.getRisteys(Pelilauta.toX(uusiSimple), Pelilauta.toY(uusiSimple));
                                visited = visitedBackup;
                            } else if (lauta.getRisteys(Pelilauta.toX(uusiSimple), Pelilauta.toY(uusiSimple)) != pelaaja) {
                                noPoints = true;
                            }
                        }
                    }
                    uusiSimple = Pelilauta.moveRight(current);
                    if (uusiSimple != -1) {
                        pino.add(uusiSimple);
                        if (lauta.getRisteys(Pelilauta.toX(uusiSimple), Pelilauta.toY(uusiSimple)) == Pelilauta.TYHJA || kuolleet[uusiSimple]) {
                            emptyPino.add(uusiSimple);
                        } else if (!noPoints) {
                            if (pelaaja == Pelilauta.TYHJA) {
                                pelaaja = lauta.getRisteys(Pelilauta.toX(uusiSimple), Pelilauta.toY(uusiSimple));
                                visited = visitedBackup;
                            } else if (lauta.getRisteys(Pelilauta.toX(uusiSimple), Pelilauta.toY(uusiSimple)) != pelaaja) {
                                noPoints = true;
                            }
                        }
                    }
                    uusiSimple = Pelilauta.moveUp(current);
                    if (uusiSimple != -1) {
                        pino.add(uusiSimple);
                        if (lauta.getRisteys(Pelilauta.toX(uusiSimple), Pelilauta.toY(uusiSimple)) == Pelilauta.TYHJA || kuolleet[uusiSimple]) {
                            emptyPino.add(uusiSimple);
                        } else if (!noPoints) {
                            if (pelaaja == Pelilauta.TYHJA) {
                                pelaaja = lauta.getRisteys(Pelilauta.toX(uusiSimple), Pelilauta.toY(uusiSimple));
                                visited = visitedBackup;
                            } else if (lauta.getRisteys(Pelilauta.toX(uusiSimple), Pelilauta.toY(uusiSimple)) != pelaaja) {
                                noPoints = true;
                            }
                        }
                    }
                    uusiSimple = Pelilauta.moveDown(current);
                    if (uusiSimple != -1) {
                        pino.add(uusiSimple);
                        if (lauta.getRisteys(Pelilauta.toX(uusiSimple), Pelilauta.toY(uusiSimple)) == Pelilauta.TYHJA || kuolleet[uusiSimple]) {
                            emptyPino.add(uusiSimple);
                        } else if (!noPoints) {
                            if (pelaaja == Pelilauta.TYHJA) {
                                pelaaja = lauta.getRisteys(Pelilauta.toX(uusiSimple), Pelilauta.toY(uusiSimple));
                                visited = visitedBackup;
                            } else if (lauta.getRisteys(Pelilauta.toX(uusiSimple), Pelilauta.toY(uusiSimple)) != pelaaja) {
                                noPoints = true;
                            }
                        }
                    }
                    if (pelaaja != Pelilauta.TYHJA) {
                        pisteetTyhjaltaAlueelta++;
                    }
                    visited[current] = true;
                }
                if (!noPoints) {
                    if (pelaaja == Pelilauta.MUSTA) {
                        pisteet += pisteetTyhjaltaAlueelta;
                    } else {
                        pisteet -= pisteetTyhjaltaAlueelta;
                    }
                }
            } else if (lauta.getRisteys(currentX, currentY) == Pelilauta.MUSTA) {
                pisteet++;

            } else {
                pisteet--;
            } 
            uusiSimple = Pelilauta.moveDown(current);
            if (uusiSimple != -1) {
                pino.add(uusiSimple);
            }
            uusiSimple = Pelilauta.moveRight(current);
            if (uusiSimple != -1) {
                pino.add(uusiSimple);
            }
            uusiSimple = Pelilauta.moveUp(current);
            if (uusiSimple != -1) {
                pino.add(uusiSimple);
            }
            uusiSimple = Pelilauta.moveLeft(current);
            if (uusiSimple != -1) {
                pino.add(uusiSimple);
            }
        }
        return pisteet;
    }

}
