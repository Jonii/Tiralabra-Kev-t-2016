/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import goai.Node;
import goai.Pelilauta;

/**
 *
 * @author jphanski
 */
public class CriticalPointObserver {

    private static int selfAtari;
    //private static int[] captureMoves = null;
    //private static int captureMovesKoko = 0;
    //private static int[] captureMovesWeight = null;
    private static int weight = 500;

    public static void addSelfAtariMove(int atariPolicy) {
        selfAtari = atariPolicy;
    }

    public static int getSelfAtariMove() {
        return selfAtari;
    }

    public static void nollaaKaikki() {
        selfAtari = -1;
    }

    public static void removeGroup(int simple) {

    }

    /*public static void addCaptureMove(int simple) {
        if (captureMoves == null) {
            captureMoves = new int[15];
            captureMovesWeight = new int[15];
            captureMovesKoko = 0;
        }
        if (captureMoves.length == captureMovesKoko) {
            int[] uusiTaulu = new int[captureMoves.length + 10];
            int[] uusiWeightTaulu = new int[captureMovesWeight.length + 10];
            for (int i = 0; i < captureMovesKoko; i++) {
                uusiTaulu[i] = captureMoves[i];
                uusiWeightTaulu[i] = captureMovesWeight[i];
            }
            captureMoves = uusiTaulu;
            captureMovesWeight = uusiWeightTaulu;
        }
        for (int i = 0; i < captureMovesKoko; i++) {
            if (captureMoves[i] == simple) {
                return;
            }
        }
        captureMoves[captureMovesKoko] = simple;
        captureMovesWeight[captureMovesKoko] = 200;
        captureMovesKoko++;

    }*/
    public static int getCapturePoints(Pelilauta lauta, Pino<Node> lapsiJono, boolean[] notVisited) {
        Node uusi;
        int poistetut = 0;
        int currentWeight = weight;
        for (int i = Pelilauta.getKoko() * Pelilauta.getKoko() - 1; i >= 0; i--) {
            if (!PlacementHandler.onkoLaillinenSiirto(lauta, Pelilauta.toX(i), Pelilauta.toY(i))) {
                poistetut++;
                continue;
            }
            if (surroundingLiberties(lauta, i, 2) || surroundingLiberties(lauta, i, 1)) {
                if (surroundingLiberties(lauta, i, 1)) {
                    currentWeight *= 2;
                }
                if (wouldBeSelfAtari(i, lauta)) {
                    currentWeight /= 10;
                }
                uusi = new Node(lauta, Pelilauta.toX(i), Pelilauta.toY(i));
                uusi.setRaveVierailut(currentWeight);
                uusi.setRaveVoitot((currentWeight * 11) / 20);
                lapsiJono.add(uusi);
                notVisited[i] = false;
                currentWeight = weight;
            } else {
                poistetut++;
            }

        }
        return (Pelilauta.getKoko() * Pelilauta.getKoko()) - poistetut;
    }
    
    /**
     * Checks if surrounding points include a group that has @liberties liberties
     * @param lauta Game board to check from
     * @param simple Coordinate in simple form, whose surroundings we check.
     * @param liberties How many liberties we hope to find
     * @return True if a group, friend or foe, has this count of liberties.
     */
    public static boolean surroundingLiberties(Pelilauta lauta, int simple, int liberties) {
        return (lauta.getVapaus(Pelilauta.toX(simple) + 1, Pelilauta.toY(simple)) == liberties)
                || (lauta.getVapaus(Pelilauta.toX(simple) - 1, Pelilauta.toY(simple)) == liberties)
                || (lauta.getVapaus(Pelilauta.toX(simple), Pelilauta.toY(simple) + 1) == liberties)
                || (lauta.getVapaus(Pelilauta.toX(simple), Pelilauta.toY(simple) - 1) == liberties);
    }

    /*private static void removeIndex(int poistettava) {
        for (int i = poistettava + 1; i<captureMovesKoko; i++) {
            captureMoves[i-1] = captureMoves[i];
            captureMovesWeight[i-1] = captureMovesWeight[i];
        }
        captureMovesKoko--;
    }*/
    private static boolean wouldBeSelfAtari(int simple, Pelilauta lauta) {
        int vapaudet = 0;
        int x = Pelilauta.toX(simple);
        int y = Pelilauta.toY(simple);
        vapaudet += providesLiberties(x + 1, y, lauta);
        vapaudet += providesLiberties(x - 1, y, lauta);
        vapaudet += providesLiberties(x, y + 1, lauta);
        vapaudet += providesLiberties(x, y - 1, lauta);
        if (vapaudet > 1) {
            return false;
        }
        Pelilauta vapausTesti = lauta.kopioi();
        PlacementHandler.pelaaSiirto(vapausTesti, x, y);

        return (vapausTesti.getVapaus(x, y) == 1);
    }
    /**
     * checks if move would provide extra liberties. Doesn't try to guess what groups of two liberties would provide, since playing close
     * to two such groups may or may not be self-atari. 
     * @param x Tested x coordinate on game board
     * @param y tested y coordinate on game board
     * @param lauta Game board
     * @return 
     */
    public static int providesLiberties(int x, int y, Pelilauta lauta) {
        if (Pelilauta.onLaudalla(x, y)) {
            if (lauta.getRisteys(x, y) == lauta.getTurn()) {
                if (lauta.getVapaus(x, y) > 2) {
                    return 2;
                }
            } else if (lauta.getRisteys(x, y) == Pelilauta.TYHJA) {
                return 1;
            } else if (lauta.getVapaus(x, y) == 1) {
                return 1;
            }
        }
        return 0;
    }
}
