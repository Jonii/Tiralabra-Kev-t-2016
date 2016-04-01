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
    private static int[] captureMoves = null;
    private static int captureMovesKoko = 0;
    private static int[] captureMovesWeight = null;

    public static void addSelfAtariMove(int atariPolicy) {
        selfAtari = atariPolicy;
    }

    public static int getSelfAtariMove() {
        return selfAtari;
    }

    public static void nollaaKaikki() {
        selfAtari = -1;
        captureMoves = null;
        captureMovesKoko = 0;
        captureMovesWeight = null;
    }

    public static void removeGroup(int simple) {

    }

    public static void addCaptureMove(int simple) {
        if (captureMoves == null) {
            captureMoves = new int[15];
            captureMovesWeight = new int[15];
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
                captureMovesWeight[i] += 20;
                return;
            }
        }
        captureMoves[captureMovesKoko] = simple;
        captureMovesWeight[captureMovesKoko] = 1500;
        captureMovesKoko++;

    }

    public static int getCapturePoints(Pelilauta lauta, Pino<Node> lapsiJono, boolean[] notVisited) {
        Node uusi;
        for (int i = captureMovesKoko - 1; i >= 0; i--) {
            if ((lauta.getVapaus(Pelilauta.toX(captureMoves[i]) + 1, Pelilauta.toY(captureMoves[i])) == 1)
                    || (lauta.getVapaus(Pelilauta.toX(captureMoves[i]) - 1, Pelilauta.toY(captureMoves[i])) == 1)
                    || (lauta.getVapaus(Pelilauta.toX(captureMoves[i]), Pelilauta.toY(captureMoves[i]) + 1) == 1)
                    || (lauta.getVapaus(Pelilauta.toX(captureMoves[i]), Pelilauta.toY(captureMoves[i]) - 1) == 1)) {
                if (!PlacementHandler.onkoLaillinenSiirto(lauta, Pelilauta.toX(captureMoves[i]), Pelilauta.toY(captureMoves[i]))) {
                    removeIndex(i);
                    continue;
                }
                uusi = new Node(lauta, Pelilauta.toX(captureMoves[i]), Pelilauta.toY(captureMoves[i]));
                uusi.setRaveVierailut(captureMovesWeight[i]);
                uusi.setRaveVoitot((captureMovesWeight[i] * 18) / 20);
                lapsiJono.add(uusi);
                notVisited[captureMoves[i]] = false;
            }
            else {
                removeIndex(i);
            }

        }
        return captureMovesKoko;
    }

    private static void removeIndex(int poistettava) {
        for (int i = poistettava + 1; i<captureMovesKoko; i++) {
            captureMoves[i-1] = captureMoves[i];
            captureMovesWeight[i-1] = captureMovesWeight[i];
        }
        captureMovesKoko--;
    }
}
