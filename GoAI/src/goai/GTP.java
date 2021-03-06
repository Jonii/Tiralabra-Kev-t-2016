/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package goai;

import java.io.IOException;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import logic.PlacementHandler;

/**
 * Huolehtii GTP-toteutuksesta. Go Text Protocol mahdollistaa rajapinnan
 * monenlaisten muiden sovellusten kanssa, ja sitä käytetään esimerkiksi bottien
 * peluuttamiseen toisia vastaan. Esimerkkinä sovelluksesta jota voi käyttää
 * tähän on GoGUI, jonka voi ladata SourceForgesta. Samaa softaa käytettiin
 * AlphaGon siirtojen välittämiseen. GTP:ssä go-botti tottelee sokeasti saatuja
 * käskyjä.
 *
 * @author jphanski
 */
public class GTP {

    private static String komento;
    private static Pelilauta lauta;
    private static int koko;
    private static Logger logger;

    /**
     * lukee GTP-komentoja hamaan loppuun saakka.
     */
    public static void read() {
        double score;
        logger = GoAI.logger;
        
        logger.info("----------------------------------\nStarting GTP");

        lauta = new Pelilauta(9);
        Scanner reader = new Scanner(System.in);
        while (true) {
            komento = reader.nextLine();
            logger.info("Received \"" + komento + "\".");
            komento = komento.toUpperCase(Locale.ROOT);
            if (komento.startsWith("PLAY")) {
                pelaaSiirto();

            } else if (komento.startsWith("GENMOVE")) {
                GeneroiSiirto();
            } else if (komento.startsWith("NAME")) {
                System.out.println("= Strabot");
            } else if (komento.startsWith("PROTOCOL_VERSION")) {
                System.out.println("= 2");
            } else if (komento.startsWith("VERSION")) {
                System.out.println("= 1.5.6");
            } else if (komento.startsWith("QUIT")) {
                return;
            } else if (komento.startsWith("BOARDSIZE")) {
                koko = Integer.parseInt(komento.substring(10));
                lauta = new Pelilauta(koko);
                System.out.println("= ");
            } else if (komento.startsWith("CLEAR_BOARD")) {
                lauta = new Pelilauta(koko);
                System.out.println("= ");
            } else if (komento.startsWith("KOMI")) {
                Pelilauta.setKomi(Double.parseDouble(komento.substring(5)));
                System.out.println("= ");
            } else if (komento.startsWith("FINAL_STATUS_LIST")) {
                final_status_list();
            } else if (komento.startsWith("FINAL_SCORE")) {
                score = score(lauta);
                if (score < 0) {
                    System.out.println("= W+" + (-1 * score));
                } else {
                    System.out.println("= B+" + score);
                }
            } else if (komento.startsWith("LIST_COMMANDS")) {
                System.out.println("= protocol_version\n"
                        + "name\n"
                        + "version\n"
                        + "known_command\n"
                        + "list_commands\n"
                        + "quit\n"
                        + "boardsize\n"
                        + "clear_board\n"
                        + "komi\n"
                        + "play\n"
                        + "genmove\n"
                        + "final_score\n"
                        + "final_status_list");
            } else if (komento.startsWith("KNOWN_COMMAND")) {
                if (komento.startsWith("KNOWN_COMMAND PLAY")) {
                    System.out.println("= known");
                } else if (komento.startsWith("KNOWN_COMMAND GENMOVE")) {
                    System.out.println("= known");
                } else if (komento.startsWith("KNOWN_COMMAND NAME")) {
                    System.out.println("= known");
                } else if (komento.startsWith("KNOWN_COMMAND PROTOCOL_VERSION")) {
                    System.out.println("= known");
                } else if (komento.startsWith("KNOWN_COMMAND VERSION")) {
                    System.out.println("= known");
                } else if (komento.startsWith("KNOWN_COMMAND QUIT")) {
                    System.out.println("= known");
                } else if (komento.startsWith("KNOWN_COMMAND BOARDSIZE")) {
                    System.out.println("= known");
                } else if (komento.startsWith("KNOWN_COMMAND CLEAR_BOARD")) {
                    System.out.println("= known");
                } else if (komento.startsWith("KNOWN_COMMAND KOMI")) {
                    System.out.println("= known");
                } else if (komento.startsWith("KNOWN_COMMAND LIST_COMMANDS")) {
                    System.out.println("= known");
                } else if (komento.startsWith("KNOWN_COMMAND KNOWN_COMMAND")) {
                    System.out.println("= known");
                } else if (komento.startsWith("KNOWN_COMMAND FINAL_SCORE")) {
                    System.out.println("= known");
                } else if (komento.startsWith("KNOWN_COMMAND FINAL_STATUS_LIST")) {
                    System.out.println("= known");
                } else {
                    System.out.println("= false");
                }
            } else {
                System.out.println("? ");
            }
            System.out.println();
            System.out.flush();
        }
    }

    /**
     * Anna kuolleet kivet.
     *
     */
    public static void final_status_list() {
        if (komento.startsWith("FINAL_STATUS_LIST DEAD")) {
            System.out.print("= ");
            boolean[] kuolleet = GoAI.decideDead(lauta);
            for (int i = 0; i < Pelilauta.getKoko() * Pelilauta.getKoko(); i++) {
                if (kuolleet[i]) {
                    System.out.println(produceCoord(Pelilauta.toX(i), Pelilauta.toY(i)));
                }
            }
            System.out.println();
        } else {
            System.out.println("? ");
        }
    }

    public static double score(Pelilauta lauta) {
        return GoAI.laskePisteet(GoAI.decideDead(lauta), lauta);
    }

    /**
     * Lue D14 -tyylisestä stringistä ensimmäinen, kirjaimen kuvaama
     * koordinaatti, ja palauta se välillä 0-laudan koko.
     *
     * @param koordinaatti XY tai XYY muotoa oleva String, jossa X on kirjain, Y
     * on 0-9 numero
     * @return
     */
    public static int readFirstCoord(String koordinaatti) {
        koordinaatti = koordinaatti.toUpperCase(Locale.ROOT);
        int x = (koordinaatti.charAt(0) - 'A');
        if (x == 8) {
            return -1; //Koordinaatit 19x19 laudalla ovat A-T, I-koordinaattia ei ole koska käytännöt
        }
        if (x > 7) {
            x--;
        }
        return x;
    }

    /**
     * Lue D14 -tyylisestä stringistä toinen, numeron kuvaama koordinaatti, ja
     * palauta se välillä 0-laudan koko.
     *
     * @param koordinaatti XY tai XYY muotoa oleva String, jossa X on kirjain, Y
     * on 0-9 numero
     * @return
     */
    public static int readSecondCoord(String koordinaatti) {
        String numero = koordinaatti.substring(1);
        int y = Integer.parseInt(numero);
        return y - 1;
    }

    /**
     * palauttaa tekstimuotoisen esityksen koordinaatista. Kuten aina, I ei ole
     * kirjain tässä yhteydessä.
     *
     * @param x
     * @param y
     * @return "A1" tai "T15" olisivat esimerkkejä mahdollisista palautuksista.
     */
    public static String produceCoord(int x, int y) {
        char eka;

        eka = (char) ('A' + x);
        if (x > 7) {
            eka++;
        }
        return "" + eka + (y + 1);
    }

    /**
     * GTP-komento "Play" jolla pelataan siirto laudalle. Komento on muotoa
     * "PLAY [Väri] [Koordinaatit]"
     */
    private static void pelaaSiirto() {
        int cutoff = 7;
        if (komento.startsWith("PLAY B")) {
            if (lauta.getTurn() != Pelilauta.MUSTA) {
                lauta.changeTurn();
            }
            if (komento.startsWith("PLAY BLACK")) {
                cutoff = 11;
            }
            if (komento.substring(cutoff).compareTo("PASS") == 0) {
                PlacementHandler.pass(lauta);
                System.out.println("= ");
            } else if (PlacementHandler.onkoLaillinenSiirto(lauta, readFirstCoord(komento.substring(cutoff)), readSecondCoord(komento.substring(cutoff)))) {
                PlacementHandler.pelaaSiirto(lauta, readFirstCoord(komento.substring(cutoff)), readSecondCoord(komento.substring(cutoff)));
                System.out.println("= ");
            } else {
                System.out.println("?");
            }
        } else if (komento.startsWith("PLAY W")) {
            if (lauta.getTurn() != Pelilauta.VALKEA) {
                lauta.changeTurn();
            }
            if (komento.startsWith("PLAY WHITE")) {
                cutoff = 11;
            }
            if (komento.substring(cutoff).compareTo("PASS") == 0) {
                PlacementHandler.pass(lauta);
                System.out.println("= ");
            } else if (PlacementHandler.onkoLaillinenSiirto(lauta, readFirstCoord(komento.substring(cutoff)), readSecondCoord(komento.substring(cutoff)))) {
                PlacementHandler.pelaaSiirto(lauta, readFirstCoord(komento.substring(cutoff)), readSecondCoord(komento.substring(cutoff)));
                System.out.println("= ");
            } else {
                System.out.println("?");
            }
        } else {
            System.out.println("?");
        }
    }

    /**
     * Tulostaa botin mielipiteen halutun värin seuraavasta siirrosta.
     * Tulostuksen lisäksi oma sisäinen tila päivitetään tällä siirrolla.
     */
    private static void GeneroiSiirto() {
        logger = GoAI.logger;
        int simulaatioita = 0;
        if (komento.startsWith("GENMOVE B")) {
            if (lauta.getTurn() != Pelilauta.MUSTA) {
                lauta.changeTurn();
            }
        }
        if (komento.startsWith("GENMOVE W")) {
            if (lauta.getTurn() != Pelilauta.VALKEA) {
                lauta.changeTurn();
            }
        }
        
        long now = System.currentTimeMillis();
        Node uusiNode = GoAI.genmove(GTP.lauta, simulaatioita);
        logger.info("Suoritettiin " + simulaatioita + " simulaatiota ajassa " + (1.0 * (System.currentTimeMillis() - now) / 1000) + "s.");

        if (uusiNode == null) {
            throw new IllegalStateException("Tyhjä siirto valittu");
        }
        //luovutus jos voittotodennäköisyys alle 15%

        if (GoAI.voitonTodennakoisyys(lauta.getTurn()) < 0.20) {
            System.out.println("= resign");
        } else if (uusiNode.getX() == -1 && uusiNode.getY() == -1) {
            System.out.println("= pass");
            PlacementHandler.pass(lauta);
        } else if (decidePass(lauta)) {
            System.out.println("= pass");
            PlacementHandler.pass(lauta);
        } else {
            System.out.println("= " + produceCoord(uusiNode.getX(), uusiNode.getY()));
            PlacementHandler.pelaaSiirto(lauta, uusiNode.getX(), uusiNode.getY());
        }
        logger.info("Mustan voiton todennäköisyys: " + GoAI.voitonTodennakoisyys(Pelilauta.MUSTA) + ".\nValittu node: " + produceCoord(uusiNode.getX(), uusiNode.getY()) + ". Playouts: " + uusiNode.voitot + "/" + uusiNode.vierailut + ".");
 
    }


    /**
     * Tries to estimate how difficult a situation is, and adjust expectations accordingly, by modifying komi used for simulation evaluations.
     */
    public static void decideSimulationKomi(Pelilauta lauta) {
        logger = GoAI.logger;
        GoAI.simulateKomi = Pelilauta.getKomi();
        GoAI.actuaSimulateWins = 1;
        GoAI.actualSimulateGames = 1;
        int voitot = 1;
        int simut = 1;
        int kerrat = 0;
        Pelilauta simuLauta;
        int kominMuutos = 1;
        if (lauta.getTurn() == Pelilauta.MUSTA) {
            kominMuutos = -1;
        }
        int[] amafTaulu = new int[Pelilauta.getKoko() * Pelilauta.getKoko()];
        while (true) {
            simuLauta = lauta.kopioi();
            Node.simulate(simuLauta, amafTaulu);
            if (Node.simulScore(simuLauta) == -1 * kominMuutos) {
                voitot++;
            }
            simut++;
            if (simut > 80) {
                kerrat++;
                if (kerrat > 5) {
                    break;
                }
                if (1.0 * voitot / simut > 0.47) {
                    if (1.0 * voitot / simut < 0.53) {
                        break;
                    }
                    else { 
                        GoAI.simulateKomi -= kominMuutos;
                        simut = 1;
                        voitot = 1;
                        continue;
                    }
                }
                simut = 1;
                voitot = 1;
                GoAI.simulateKomi += kominMuutos;
            }
        }
        //System.out.println("Simulation komi used: " + GoAI.simulateKomi);
        logger.info("Simulation komi used: " + GoAI.simulateKomi);
    }

    public static boolean decidePass(Pelilauta lauta) {
        Pelilauta simuLauta;
        GoAI.simulateKomi = Pelilauta.getKomi();
        int[] amafTaulu = new int[Pelilauta.getKoko() * Pelilauta.getKoko()];
        int voitot = 0;
        double score = score(lauta);
        if ((score > 0 && lauta.getTurn() == Pelilauta.VALKEA) || (score < 0 && lauta.getTurn() == Pelilauta.MUSTA)) {
            return false;
        }
        for (int i = 0; i<200; i++) {
            simuLauta = lauta.kopioi();
            PlacementHandler.pass(simuLauta);
            Node.simulate(simuLauta, amafTaulu);
            if ((Node.simulScore(simuLauta) > 0 && lauta.getTurn() == Pelilauta.MUSTA) ||
                    (Node.simulScore(simuLauta) < 0 && lauta.getTurn() == Pelilauta.VALKEA)) {
                voitot++;
            }
        }
        if (voitot > 195) return true;
        return false;
    }
}
