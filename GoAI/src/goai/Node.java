package goai;

import java.util.LinkedList;
import java.util.Random;
import logic.Pino;
import logic.PlacementHandler;

/**
 * Hakupuun solmu.
 *
 * @author jphanski
 */
public class Node {

    Node[] children;
    Pelilauta lauta;
    PlacementHandler handler;
    static Random r = new Random();
    static double epsilon = 1e-6;
    
    private boolean scoreable;
    int x, y;

    int vierailut, voitot;

    public boolean isScoreable() {
        return scoreable;
    }

    public void setScoreable(boolean scoreable) {
        this.scoreable = scoreable;
    }

    public Node() {
        this(new Pelilauta());
    }

    public Node(Pelilauta lauta) {
        this.lauta = lauta.kopioi();
        this.handler = new PlacementHandler(this.lauta);
    }

    public Node(Pelilauta lauta, int x, int y) {
        this.lauta = lauta.kopioi();
        this.x = x;
        this.y = y;
        this.handler = new PlacementHandler(this.lauta);
        if (handler.onkoLaillinenSiirto(x, y)) {
            handler.pelaaSiirto(x, y);
        } else {
            throw new IllegalStateException("laiton siirto hakupuussa");
        }
    }

    public void selectAction() {
        if (isScoreable()) return;
        Pino<Node> visited = new Pino<>();
        visited.add(this);
        Node currentNode = this;
        int tulos;

        while (!currentNode.isLeaf()) {
            currentNode = currentNode.select();
            visited.add(currentNode);
        }
        currentNode.expand();
        if (!currentNode.isScoreable()) {
            currentNode = currentNode.select();
            visited.add(currentNode);
        }
        tulos = currentNode.simulate();

        while (visited.IsNotEmpty()) {
            currentNode = visited.pop();
            currentNode.vierailut++;
            if ((currentNode.lauta.getTurn() == Pelilauta.VALKEA && tulos > 0)
                    || (currentNode.lauta.getTurn() == Pelilauta.MUSTA && tulos < 0)) {
                currentNode.voitot++;
            }
        }
        return;
    }

    /**
     * UCT valinta.
     *
     * Kuulemma ei käytännöllinen, mutta kokeillaan..
     *
     * @return paras node, UCT politiikalla
     */
    private Node select() {
        Node selected = null;
        if (scoreable) return null;
        double bestValue = Double.MIN_VALUE;
        for (Node c : children) {
            double uctValue
                    = c.voitot / (c.vierailut + epsilon)
                    + Math.sqrt(Math.log(vierailut + 1) / (c.vierailut + epsilon))
                    + r.nextDouble() * epsilon;
            // small random number to break ties randomly in unexpanded nodes
            // System.out.println("UCT value = " + uctValue);
            if (uctValue > bestValue) {
                selected = c;
                bestValue = uctValue;
            }
        }
        // System.out.println("Returning: " + selected);
        return selected;
    }

    public void expand() {
        //select random points for now?
        if (scoreable) {
            return;
        }
        
        int pisteita = 0;
        Pino<Node> lapsiJono = new Pino<>();
        int[] tyhjat = lauta.getVapaatPisteet();
        int offset = r.nextInt(tyhjat.length);
        int indeksi = 0;
        int x, y;

        while ((indeksi < tyhjat.length) && (pisteita < 20)) {
            x = lauta.transformToXCoordinate(tyhjat[(indeksi + offset) % tyhjat.length]);
            y = lauta.transformToYCoordinate(tyhjat[(indeksi + offset) % tyhjat.length]);
            if (handler.onkoLaillinenSiirto(x, y)) {
                lapsiJono.add(new Node(lauta, x, y));
                tyhjat[(indeksi + offset) % tyhjat.length] = -1; //muuttuu laittomaksi siirroksi, joten ei oteta samaa siirtoa moneen kertaan
                pisteita++;
                indeksi = 0;
                offset = r.nextInt(tyhjat.length);
                continue;
            }
            indeksi++;

        }
        if (pisteita == 0) {
            scoreable = true;
            return;
        }
        this.children = new Node[pisteita];
        for (int i = 0; i < pisteita; i++) {
            this.children[i] = lapsiJono.pop();
        }
    }

    protected boolean isLeaf() {
        if (children == null) {
            return true;
        }
        if (children.length == 0) {
            return true;
        }
        return false;
    }

    /**
     * Annetaan eniten vierailtu solmu siirron pelaamista varten. Tämän funktion
     * voi ajaa koska tahansa mutta tietty on parempi mitä useampia
     * simulaatioita on keretty ajaa.
     *
     * @return
     */
    Node annaValinta() {
        int highest = 0;
        int highestIndex = 0;
        for (int i = 0; i < children.length; i++) {
            if (children[i].vierailut > highest) {
                highest = children[i].vierailut;
                highestIndex = i;
            }
        }
        return children[highestIndex];
    }

    /**
     * simuloi pelin. Pelaa sarjan
     *
     * @return +1 jos musta voittaa, -1 jos valkea voittaa.
     */
    private int simulate() {
        Pelilauta simulateBoard = lauta.kopioi();
        PlacementHandler simuhandler = new PlacementHandler(simulateBoard);
        int[] vapaatpisteet;
        int offset;
        int x, y;
        
        
        boolean noSensibleMovesLeft = false;
        boolean loytyiSiirto = false;
        
        while (!noSensibleMovesLeft && simulateBoard.getMoveNumber() < 700) {
            vapaatpisteet = simulateBoard.getVapaatPisteet();
            offset = r.nextInt(vapaatpisteet.length);
            
            loytyiSiirto = false;
            
            for (int i = 0; i < vapaatpisteet.length; i++) {
                x = simulateBoard.transformToXCoordinate(vapaatpisteet[(i + offset) % vapaatpisteet.length]);
                y = simulateBoard.transformToYCoordinate(vapaatpisteet[(i + offset) % vapaatpisteet.length]);
                if (!simuhandler.tuhoaakoSiirtoOmanSilman(x, y)) {
                    simuhandler.pelaaSiirto(x, y);
                    noSensibleMovesLeft = false;
                    loytyiSiirto = true;
                    break;
                }
                if (simulateBoard.isPassedOnLastMove()) {
                    noSensibleMovesLeft = true;
                }

            }
            if (!loytyiSiirto) simuhandler.pass();
        }

        int pisteet = 0; // Alkuarvo on komi, valkealle annettava etu.
        int kivenvari;
        for (int i = 0; i < simulateBoard.getKoko() * simulateBoard.getKoko(); i++) {
            kivenvari = simulateBoard.getRisteys(simulateBoard.transformToXCoordinate(i), simulateBoard.transformToYCoordinate(i));
            if (kivenvari == Pelilauta.MUSTA) {
                pisteet++;
            }
            if (kivenvari == Pelilauta.VALKEA) {
                pisteet--;
            }
        }
        if (pisteet > 0) {
            return 1;
        }
        return -1;
    }

}
