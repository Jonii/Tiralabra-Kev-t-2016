package goai;

import java.util.LinkedList;
import java.util.Random;
import java.util.TreeSet;
import logic.Pino;
import logic.PlacementHandler;

/**
 * Hakupuun solmu.
 *
 * @author jphanski
 */
public class Node {

    NodenLapset children;
    Pelilauta lauta;
    static Random r = new Random();
    static double epsilon = 1e-6;
    private boolean scoreable;
    private int x, y, simple;
    private int raveVierailut;
    private int raveVoitot;
    private static int raveSuoritukset = 300;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getSimple() {
        return lauta.transformToSimpleCoordinates(x, y);
    }

    public void setSimple(int simple) {
        this.x = lauta.transformToXCoordinate(simple);
        this.y = lauta.transformToYCoordinate(simple);
    }

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
    }

    /**
     * Luo uusi pelilauta, aiemman pohjalta. Pelataan uusi siirto
     * koordinaatteihin x,y sen pelaajan puolesta jonka vuoro nyt on.
     * Herrasmiessopimuksella siirto -1, -1 tarkoittaa passausta.
     *
     * @param lauta
     * @param x
     * @param y
     */
    public Node(Pelilauta lauta, int x, int y) {
        this.lauta = lauta.kopioi();
        this.x = x;
        this.y = y;
        if (PlacementHandler.onkoLaillinenSiirto(this.lauta, x, y)) {
            PlacementHandler.pelaaSiirto(this.lauta, x, y);
        } else {
            PlacementHandler.pass(this.lauta);
        }
    }

    /**
     * Yrittää parhaalta vaikuttavaa siirtoa, simuloi siitä edespäin pelin
     * loppuun saakka, ja päivittää jokaisen MC-puun solmun matkan varrella
     * tällä tuloksella.
     */
    public void selectAction() {
        if (isScoreable()) {
            return;
        }
        Pino<Node> visited = new Pino<>();
        visited.add(this);
        Node currentNode = this;
        int tulos;

        while (!currentNode.isLeaf()) {
            if (vierailut > raveSuoritukset) {
                currentNode = currentNode.select();
            }
            else {
                currentNode = currentNode.selectRAVE(vierailut);
            }
            visited.add(currentNode);
        }
        currentNode.expand();
        if (!currentNode.isScoreable()) {
            if (vierailut > raveSuoritukset) {
                currentNode = currentNode.select();
            }
            else {
                currentNode = currentNode.selectRAVE(vierailut);
            }
            visited.add(currentNode);
        }
        tulos = currentNode.simulate();

        if (vierailut > raveSuoritukset) {
            update(visited, tulos);
        }
        else {
            updateRAVE(visited, tulos);
        }
        return;
    }

    private void update(Pino<Node> visited, int tulos) {
        Node currentNode;
        while (visited.IsNotEmpty()) {
            currentNode = visited.pop();
            currentNode.vierailut++;
            if ((currentNode.lauta.getTurn() == Pelilauta.VALKEA && tulos > 0)
                    || (currentNode.lauta.getTurn() == Pelilauta.MUSTA && tulos < 0)) {
                currentNode.voitot++;
            }
        }
    }

    private void updateRAVE(Pino<Node> visited, int tulos) {
        Node currentNode;
        Pino<Node> juggleOne = new Pino<>();
        Pino<Node> juggleTwo = new Pino<>();
        Node raveUpdateNode;
        int raveUpdateIndex;

        while (visited.IsNotEmpty()) {
            currentNode = visited.pop();
            currentNode.vierailut++;
            if ((currentNode.lauta.getTurn() == Pelilauta.VALKEA && tulos > 0)
                    || (currentNode.lauta.getTurn() == Pelilauta.MUSTA && tulos < 0)) {
                currentNode.voitot++;
            }
            if (juggleOne.IsNotEmpty()) {
                juggleTwo.add(currentNode);
                while (juggleOne.IsNotEmpty()) {
                    raveUpdateNode = juggleOne.pop();
                    juggleTwo.add(raveUpdateNode);
                    raveUpdateIndex = currentNode.children.find(raveUpdateNode.getSimple());
                    if (raveUpdateIndex != -1) {
                        currentNode.children.getNode(raveUpdateIndex).raveVierailut++;
                        if ((currentNode.children.getNode(raveUpdateIndex).lauta.getTurn() == Pelilauta.VALKEA && tulos > 0)
                                || (currentNode.children.getNode(raveUpdateIndex).lauta.getTurn() == Pelilauta.MUSTA && tulos < 0)) {
                            currentNode.children.getNode(raveUpdateIndex).raveVoitot++;
                        }
                    }                    
                }
            } else {
                juggleOne.add(currentNode);
                while (juggleTwo.IsNotEmpty()) {
                    raveUpdateNode = juggleTwo.pop();
                    juggleOne.add(raveUpdateNode);
                    raveUpdateIndex = currentNode.children.find(raveUpdateNode.getSimple());
                    if (raveUpdateIndex != -1) {
                        currentNode.children.getNode(raveUpdateIndex).raveVierailut++;
                        if ((currentNode.children.getNode(raveUpdateIndex).lauta.getTurn() == Pelilauta.VALKEA && tulos > 0)
                                || (currentNode.children.getNode(raveUpdateIndex).lauta.getTurn() == Pelilauta.MUSTA && tulos < 0)) {
                            currentNode.children.getNode(raveUpdateIndex).raveVoitot++;
                        }
                    }
                }
            }
            
        }
    }

    /**
     * RAVE valinta.
     *
     * Sisältää hiukan go-spesifiä logiikkaa siirron valintaan, nimellisesti
     * RAVEn.
     *
     * @param raveSimulaatioita montako siirtoa rave-politiikkaa käytetään.
     * @return paras node, RAVE/UCT-poliitikkojen sekoituksella.
     */
    private Node selectRAVE(int raveSimulaatioita) {
        Node selected = null;
        if (scoreable) {
            return null;
        }
        double bestValue = Double.MIN_VALUE;
        Node c;
        for (int i = 0; i < children.getKoko(); i++) {
            c = children.getNode(i);
            double raveValue
                    = raveB(raveSimulaatioita) * (c.raveVoitot / (c.raveVierailut + epsilon))
                    + (1 - raveB(raveSimulaatioita)) * (c.voitot / (c.vierailut + epsilon))
                    + Math.sqrt(Math.log(vierailut + 1) / (c.vierailut + epsilon))
                    + r.nextDouble() * epsilon;
            // small random number to break ties randomly in unexpanded nodes
            // System.out.println("UCT value = " + uctValue);
            if (raveValue > bestValue) {
                selected = c;
                bestValue = raveValue;
            }
        }
        // System.out.println("Returning: " + selected);
        return selected;
    }

    private double raveB(int raveSimulaatioita) {
        return Math.min(1, 1.0 * raveSimulaatioita / raveSuoritukset);
    }

    /**
     * UCT valinta.
     *
     * Kuulemma ei käytännöllinen, mutta kokeillaan.. Suorittaa valinnan omista
     * lapsista, mikä niistä vaikuttaa lupaavimmalta. Käyttää UCT-metodia, joka
     * ei sisällä mitään go-spesifiä logiikkaa
     *
     * @return paras node, UCT politiikalla
     */
    private Node select() {
        Node selected = null;
        if (scoreable) {
            return null;
        }
        double bestValue = Double.MIN_VALUE;
        Node c;
        for (int i = 0; i < children.getKoko(); i++) {
            c = children.getNode(i);
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

    /**
     * Päättää mitä siirtoja tästä nodesta eteenpäin ylipäätään tullaan
     * harkitsemaan. Tämänhetkinen versio ottaa satunnaisesti 20 pistettä.
     */
    public void expand() {
        if (scoreable) {
            return;
        }

        boolean[] mahdollisetPisteet = new boolean[lauta.getKoko() * lauta.getKoko()];
        int pisteita = 0;
        Pino<Node> lapsiJono = new Pino<>();
        int[] mahdolliset = lauta.getMahdollisetPisteet();

        if (mahdolliset == null) {
            scoreable = true;
            return;
        }

        for (int i = 0; i < mahdolliset.length; i++) {
            mahdollisetPisteet[mahdolliset[i]] = true;
        }
        int offset = r.nextInt(mahdolliset.length);
        int indeksi = 0;
        int x, y;
        int simple;
        //lisätään edellisten siirtojen ympäristöt. Tämä bugaa jostain syystä.
        simple = lauta.moveDown(lauta.getEdellinen());
        if (lisaaJonoon(simple, lapsiJono, mahdollisetPisteet)) {
            pisteita++;
        }

        simple = lauta.moveUp(lauta.getEdellinen());
        if (lisaaJonoon(simple, lapsiJono, mahdollisetPisteet)) {
            pisteita++;
        }

        simple = lauta.moveLeft(lauta.getEdellinen());
        if (lisaaJonoon(simple, lapsiJono, mahdollisetPisteet)) {
            pisteita++;
        }

        simple = lauta.moveRight(lauta.getEdellinen());
        if (lisaaJonoon(simple, lapsiJono, mahdollisetPisteet)) {
            pisteita++;
        }

        simple = lauta.moveRight(lauta.moveRight(lauta.getEdellinen()));
        if (lisaaJonoon(simple, lapsiJono, mahdollisetPisteet)) {
            pisteita++;
        }

        simple = lauta.moveUp(lauta.moveRight(lauta.getEdellinen()));
        if (lisaaJonoon(simple, lapsiJono, mahdollisetPisteet)) {
            pisteita++;
        }

        simple = lauta.moveDown(lauta.moveRight(lauta.getEdellinen()));
        if (lisaaJonoon(simple, lapsiJono, mahdollisetPisteet)) {
            pisteita++;
        }

        simple = lauta.moveLeft(lauta.moveLeft(lauta.getEdellinen()));
        if (lisaaJonoon(simple, lapsiJono, mahdollisetPisteet)) {
            pisteita++;
        }

        simple = lauta.moveUp(lauta.moveLeft(lauta.getEdellinen()));
        if (lisaaJonoon(simple, lapsiJono, mahdollisetPisteet)) {
            pisteita++;
        }

        simple = lauta.moveUp(lauta.moveUp(lauta.getEdellinen()));
        if (lisaaJonoon(simple, lapsiJono, mahdollisetPisteet)) {
            pisteita++;
        }

        simple = lauta.moveDown(lauta.moveDown(lauta.getEdellinen()));
        if (lisaaJonoon(simple, lapsiJono, mahdollisetPisteet)) {
            pisteita++;
        }

        simple = lauta.moveDown(lauta.getSitaEdellinen());
        if (lisaaJonoon(simple, lapsiJono, mahdollisetPisteet)) {
            pisteita++;
        }

        simple = lauta.moveUp(lauta.getSitaEdellinen());
        if (lisaaJonoon(simple, lapsiJono, mahdollisetPisteet)) {
            pisteita++;
        }

        simple = lauta.moveLeft(lauta.getSitaEdellinen());
        if (lisaaJonoon(simple, lapsiJono, mahdollisetPisteet)) {
            pisteita++;
        }

        simple = lauta.moveRight(lauta.getSitaEdellinen());
        if (lisaaJonoon(simple, lapsiJono, mahdollisetPisteet)) {
            pisteita++;
        }

        while ((indeksi < mahdolliset.length) && (pisteita < 29)) {
            x = lauta.transformToXCoordinate(mahdolliset[(indeksi + offset) % mahdolliset.length]);
            y = lauta.transformToYCoordinate(mahdolliset[(indeksi + offset) % mahdolliset.length]);
            if (mahdollisetPisteet[mahdolliset[(indeksi + offset) % mahdolliset.length]]) {
                lapsiJono.add(new Node(lauta, x, y));
                mahdollisetPisteet[mahdolliset[(indeksi + offset) % mahdolliset.length]] = false; //Varmistetaan että samaa siirtoa ei lasketa moneen kertaan
                pisteita++;
                indeksi = 0;
                offset = r.nextInt(mahdolliset.length);
                continue;
            }
            indeksi++;

        }

        Node passaus = new Node(lauta, -1, -1);
        lapsiJono.add(passaus);
        pisteita++;

        Node[] pisteet = new Node[pisteita];
        for (int i = 0; i < pisteita; i++) {
            pisteet[i] = lapsiJono.pop();
        }
        this.children = new NodenLapset(pisteet);
    }

    private boolean lisaaJonoon(int simple, Pino<Node> lapsiJono, boolean[] mahdollisetPisteet) {
        int x;
        int y;
        if (simple != -1) {
            x = lauta.transformToXCoordinate(simple);
            y = lauta.transformToYCoordinate(simple);
            if (mahdollisetPisteet[simple]) {
                lapsiJono.add(new Node(lauta, x, y));
                mahdollisetPisteet[simple] = false;
                return true;
            }
        }
        return false;
    }

    protected boolean isLeaf() {
        if (children == null) {
            return true;
        }
        if (children.getKoko() == 0) {
            throw new IllegalStateException("Hakupuu rikki");
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
        if (children == null) {
            return null;
        }
        for (int i = 0; i < children.getKoko(); i++) {
            if (children.getNode(i).vierailut > highest) {
                highest = children.getNode(i).vierailut;
                highestIndex = i;
            }
        }
        return children.getNode(highestIndex);
    }

    /**
     * simuloi pelin. Pelaa sarjan
     *
     * @return +1 jos musta voittaa, -1 jos valkea voittaa.
     */
    public int simulate() {
        Pelilauta simulateBoard = lauta.kopioi();
        int[] vapaatpisteet;
        int offset;
        int x, y;

        boolean noSensibleMovesLeft = false;
        boolean loytyiSiirto = false;

        while (!noSensibleMovesLeft && simulateBoard.getMoveNumber() < 700) {
            vapaatpisteet = simulateBoard.getMahdollisetPisteet();

            loytyiSiirto = false;
            if (simulateBoard.isPassedOnLastMove()) {
                noSensibleMovesLeft = true;
            }
            if (vapaatpisteet != null) {

                offset = r.nextInt(vapaatpisteet.length);
                for (int i = 0; i < vapaatpisteet.length; i++) {
                    x = simulateBoard.transformToXCoordinate(vapaatpisteet[(i + offset) % vapaatpisteet.length]);
                    y = simulateBoard.transformToYCoordinate(vapaatpisteet[(i + offset) % vapaatpisteet.length]);
                    if (!PlacementHandler.tuhoaakoSiirtoOmanSilman(simulateBoard, x, y)) {
                        PlacementHandler.pelaaSiirto(simulateBoard, x, y);
                        noSensibleMovesLeft = false;
                        loytyiSiirto = true;
                        break;
                    }

                }
            }
            if (!loytyiSiirto) {
                PlacementHandler.pass(simulateBoard);
            }
        }

        double pisteet = -1 * lauta.getKomi(); // Alkuarvo on komi, valkealle annettava etu.

        /*
            Stone scoring + silmien lasku
         */
        int kivenvari;
        for (int i = 0; i < simulateBoard.getKoko() * simulateBoard.getKoko(); i++) {
            kivenvari = simulateBoard.getRisteys(simulateBoard.transformToXCoordinate(i), simulateBoard.transformToYCoordinate(i));
            if (kivenvari == Pelilauta.MUSTA) {
                pisteet++;
            } else if (kivenvari == Pelilauta.VALKEA) {
                pisteet--;
            } else { // Tyhjä risteys
                x = simulateBoard.transformToXCoordinate(i);
                y = simulateBoard.transformToYCoordinate(i);
                //Tämä laskee sekin väärin mutta ero on yleisesti ottaen hyvin pieni.
                if (simulateBoard.getRisteys(x + 1, y) == Pelilauta.MUSTA || simulateBoard.getRisteys(x - 1, y) == Pelilauta.MUSTA) {
                    pisteet++;
                }
                if (simulateBoard.getRisteys(x + 1, y) == Pelilauta.VALKEA || simulateBoard.getRisteys(x - 1, y) == Pelilauta.VALKEA) {
                    pisteet--;
                }
            }
        }
        //GoAI.piirraLauta(simulateBoard);
        //System.out.println(pisteet + ", ");

        if (pisteet > 0) {
            return 1;
        }
        return -1;
    }

    public static double voitonTodennakoisyys(Node node) {
        double voitot = 1.0;
        double vierailut = 1.0;
        if (node.children == null) {
            return 1;
        }
        for (int i = 0; i < node.children.getKoko(); i++) {
            vierailut += node.children.getNode(i).vierailut;
            voitot += node.children.getNode(i).voitot;
        }
        return voitot / vierailut;
    }

}
