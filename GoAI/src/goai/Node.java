package goai;

import java.util.LinkedList;
import java.util.Random;
import java.util.TreeSet;
import logic.CriticalPointObserver;
import logic.Pino;
import logic.PlacementHandler;

/**
 * Hakupuun solmu.
 *
 * @author jphanski
 */
public class Node {

    NodenLapset children;

    static Random r = new Random();
    static double epsilon = 1e-6;
    private boolean noLegalMovesAvailable;
    private int x, y, simple;
    int raveVierailut;
    int raveVoitot;

    /**
     * Rave-faktori. Kuinka monen simulaation jälkeen
     * RAVE ja UCT saavat saman painoarvon. Aluksi käytetään RAVEa,
     * mutta loppua kohden UCT:ta. Muuta parametetrillä -rave <numero>
     */
    public static int raveSuoritukset = 1000;
    public static int branchingFactor = 150;
    public static int laudanKoko;
    private int turn;
    private double tieBreaker;

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
        return Pelilauta.toSimple(x, y);
    }

    public void setSimple(int simple) {
        this.x = Pelilauta.toX(simple);
        this.y = Pelilauta.toY(simple);
    }

    int vierailut, voitot;

    public boolean isNoLegalMovesAvailable() {
        return noLegalMovesAvailable;
    }

    public void setNoLegalMovesAvailable(boolean noLegalMovesAvailable) {
        this.noLegalMovesAvailable = noLegalMovesAvailable;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public int getRaveVierailut() {
        return raveVierailut;
    }

    public void setRaveVierailut(int raveVierailut) {
        this.raveVierailut = raveVierailut;
    }

    public int getRaveVoitot() {
        return raveVoitot;
    }

    public void setRaveVoitot(int raveVoitot) {
        this.raveVoitot = raveVoitot;
    }
    public double getTieBreaker() {
        return tieBreaker;
    }

    public Node() {
        tieBreaker = r.nextDouble();
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
        this.x = x;
        this.y = y;
        this.simple = Pelilauta.toSimple(x, y);

        lauta.changeTurn();
        this.turn = lauta.getTurn();
        lauta.changeTurn();
        this.raveVierailut = 8;
        this.raveVoitot = 2;
        this.vierailut = 4;
        this.voitot = 1;
        tieBreaker = r.nextDouble();
    }

    /**
     * Yrittää parhaalta vaikuttavaa siirtoa, simuloi siitä edespäin pelin
     * loppuun saakka, ja päivittää jokaisen MC-puun solmun matkan varrella
     * tällä tuloksella.
     */
    public void selectAction(Pelilauta lauta) {
        Pelilauta currentLauta = lauta.kopioi();
        int[] amafTaulu = new int[Pelilauta.getKoko() * Pelilauta.getKoko()];

        if (isNoLegalMovesAvailable()) {
            return;
        }
        Pino<Node> visited = new Pino<>();
        visited.add(this);
        Node currentNode = this;
        int tulos;

        while (!currentNode.isLeaf()) {
            if (raveSuoritukset == 0) {
                currentNode = currentNode.select();
                PlacementHandler.pelaaSiirto(currentLauta, currentNode.getX(), currentNode.getY());
            } else {
                currentNode = currentNode.selectRAVE(currentNode.vierailut);
                if ((Pelilauta.toSimple(currentNode.getX(), currentNode.getY()) > -1) && (amafTaulu[Pelilauta.toSimple(currentNode.getX(), currentNode.getY())] == 0)) {
                    amafTaulu[Pelilauta.toSimple(currentNode.getX(), currentNode.getY())] = currentNode.getTurn();
                }
                PlacementHandler.pelaaSiirto(currentLauta, currentNode.getX(), currentNode.getY());
            }
            visited.add(currentNode);
        }
        currentNode.expand(currentLauta);

        if (raveSuoritukset == 0) {
            currentNode = currentNode.select();
            PlacementHandler.pelaaSiirto(currentLauta, currentNode.getX(), currentNode.getY());
        } else {
            currentNode = currentNode.selectRAVE(currentNode.vierailut);
            if ((Pelilauta.toSimple(currentNode.getX(), currentNode.getY()) > -1) && (amafTaulu[Pelilauta.toSimple(currentNode.getX(), currentNode.getY())] == 0)) {
                amafTaulu[Pelilauta.toSimple(currentNode.getX(), currentNode.getY())] = currentNode.getTurn();
            }
            PlacementHandler.pelaaSiirto(currentLauta, currentNode.getX(), currentNode.getY());
        }

        visited.add(currentNode);

        currentLauta = currentNode.simulate(currentLauta, amafTaulu);
        tulos = simulScore(currentLauta);

        if (raveSuoritukset == 0) {
            update(visited, tulos);
        } else {
            updateRAVE(visited, tulos, amafTaulu);
        }
        return;
    }

    private void update(Pino<Node> visited, int tulos) {
        Node currentNode;
        while (visited.IsNotEmpty()) {
            currentNode = visited.pop();
            currentNode.vierailut++;
            if ((currentNode.getTurn() == Pelilauta.VALKEA && tulos > 0)
                    || (currentNode.getTurn() == Pelilauta.MUSTA && tulos < 0)) {
                currentNode.voitot++;
            }
        }
    }

    private void updateRAVE(Pino<Node> visited, int tulos, int[] amafTaulu) {
        Node currentNode;

        int lapsiIndeksi;

        while (visited.IsNotEmpty()) {
            currentNode = visited.pop();
            currentNode.vierailut++;
            if ((currentNode.getTurn() == Pelilauta.VALKEA && tulos > 0)
                    || (currentNode.getTurn() == Pelilauta.MUSTA && tulos < 0)) {
                currentNode.voitot++;
            }

            lapsiIndeksi = 0;
            for (int i = Pelilauta.getKoko() * Pelilauta.getKoko() - 1; i >= 0; i--) {
                if (currentNode.children == null || lapsiIndeksi == currentNode.children.getKoko()) {
                    break;
                }

                if (currentNode.children.getNode(lapsiIndeksi).getSimple() > i) {
                    throw new IllegalStateException("Rave rikki");
                }
                if (currentNode.children.getNode(lapsiIndeksi).getSimple() == i) {
                    if (amafTaulu[i] == currentNode.getTurn()) {
                        currentNode.children.getNode(lapsiIndeksi).raveVierailut++;
                        if ((currentNode.getTurn() == Pelilauta.MUSTA && tulos > 0) // Värit käännetty koska kyse lapsista.
                                || (currentNode.getTurn() == Pelilauta.VALKEA && tulos < 0)) { // hiukan epäselvä tapa mutta toiminee.
                            currentNode.children.getNode(lapsiIndeksi).raveVoitot++;
                        }
                    }
                    lapsiIndeksi++;

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
        if (noLegalMovesAvailable) {
            return null;
        }
        double bestValue = Double.MIN_VALUE;
        Node c;
        for (int i = 0; i < children.getKoko(); i++) {
            c = children.getNode(i);
            double raveValue
                    = raveB(raveSimulaatioita) * (c.raveVoitot / (c.raveVierailut + epsilon))
                    + (1 - raveB(raveSimulaatioita)) * (c.voitot / (c.vierailut + epsilon))
                    + 0.2 * Math.sqrt(Math.log(vierailut + 1) / (c.vierailut + epsilon)) // exploration perhaps unnecessary in RAVE?
                    + r.nextDouble() * epsilon;
            // small random number to break ties randomly in unexpanded nodes
            //System.out.println(c.getSimple() + "'s RAVE value = " + raveValue);
            if (raveValue > bestValue) {
                selected = c;
                bestValue = raveValue;
            }
        }
        //System.out.println("Returning: " + selected.getSimple());
        return selected;
    }

    private double raveB(int raveSimulaatioita) {
        //return Math.min(1, 1.0 * raveSimulaatioita / raveSuoritukset);
        return Math.sqrt(1.0 * raveSuoritukset / ((3 * raveSimulaatioita) + raveSuoritukset));
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
        if (noLegalMovesAvailable) {
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
    public void expand(Pelilauta lauta) {
        boolean[] visited = new boolean[Pelilauta.getKoko() * Pelilauta.getKoko()];
        int pisteita = 0;
        Pino<Node> lapsiJono = new Pino<>();

        for (int i = 0; i < visited.length; i++) {
            visited[i] = true;
        }
        int offset = r.nextInt(visited.length);
        int indeksi = 0;
        int uusiX, uusiY;
        int uusiSimple;
        pisteita = CriticalPointObserver.getCapturePoints(lauta, lapsiJono, visited);
        while ((indeksi < visited.length) && (pisteita < branchingFactor)) {
            uusiX = Pelilauta.toX((indeksi + offset) % visited.length);
            uusiY = Pelilauta.toY((indeksi + offset) % visited.length);
            if (visited[(indeksi + offset) % visited.length]) {
                visited[(indeksi + offset) % visited.length] = false; //Varmistetaan että samaa siirtoa ei lasketa moneen kertaan
                
                if (PlacementHandler.onkoLaillinenSiirto(lauta, uusiX, uusiY)) {
                    lapsiJono.add(new Node(lauta, uusiX, uusiY));
                    pisteita++;
                    indeksi = 0;
                    offset = r.nextInt(visited.length);
                    continue;
                }
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
    
    public void bayesExpand(Pelilauta lauta) {
        double[] values = new double[Pelilauta.getKoko() * Pelilauta.getKoko()];
        int[] strengths = new int[values.length];
        children = new NodenLapset();
        double boardSum = 0.0;
        int strengthOfPrediction = 0;
        Node newNode;

        for (int i = 0; i < values.length; i++) {
            values[i] = Pattern.valueOf(Pattern.match(lauta, Pelilauta.toX(i), Pelilauta.toY(i)));
            strengths[i] = Pattern.getSeenTotal(Pattern.match(lauta, Pelilauta.toX(i), Pelilauta.toY(i)));
            boardSum += values[i];
            strengthOfPrediction += Pattern.getSeenTotal(Pattern.match(lauta, Pelilauta.toX(i), Pelilauta.toY(i)));
        }
        Pelilauta testiLauta;
        double difference;
        int differenceInStrength;
        for (int i = 0; i<values.length; i++) {
            if (!PlacementHandler.onkoLaillinenSiirto(lauta, Pelilauta.toX(i), Pelilauta.toY(i))) {
                continue;
            }
            difference = 0.0;
            differenceInStrength = 0;
            testiLauta = lauta.kopioi();
            PlacementHandler.pelaaSiirto(testiLauta, Pelilauta.toX(i), Pelilauta.toY(i));
            for (int x = -1; x < 2; x++) {
                for (int y = -1; y < 2; y++) {
                    if (Pelilauta.onLaudalla(Pelilauta.toX(i) + x, Pelilauta.toY(i) + y)) {
                        difference += values[i] - Pattern.valueOf(Pattern.match(testiLauta, Pelilauta.toX(i) + x, Pelilauta.toY(i) + y));
                        differenceInStrength += strengths[i] - Pattern.getSeenTotal(Pattern.match(testiLauta, Pelilauta.toX(i), Pelilauta.toY(i)));
                    }
                }
            }
            newNode = new Node(lauta, Pelilauta.toX(i), Pelilauta.toY(i));
            newNode.raveVierailut = (int) Math.round(Math.log(strengthOfPrediction - differenceInStrength) * 1000);
            newNode.raveVoitot = (int) Math.round(newNode.raveVierailut * (1 / (1 + Math.pow(Math.E, boardSum - difference))));
            children.addNode(newNode);
        }

        Node passaus = new Node(lauta, -1, -1);
        children.addNode(passaus);
    }

    /*private boolean lisaaJonoon(int simple, Pino<Node> lapsiJono, boolean[] visited) {
        int x;
        int y;
        if (simple != -1) {
            x = Pelilauta.toX(simple);
            y = Pelilauta.toY(simple);
            if (visited[simple]) {
                lapsiJono.add(new Node(lauta, x, y));
                visited[simple] = false;
                return true;
            }
        }
        return false;
    }*/
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
     * simuloi pelin. Pelaa sarjan siirtoja.
     *
     * @return Pelilauta, kun simulaatio on päättynyt. Laudalla ei pitäisi olla
     * yhtäkään kuollutta ryhmää, ja silmien tulisi olla yhden pisteen kokoisia.
     */
    public static Pelilauta simulate(Pelilauta lauta, int[] amafTaulu) {
        int[] vapaatpisteet;
        int offset;
        int x, y;

        boolean noSensibleMovesLeft = false;
        boolean loytyiSiirto;

        while (!noSensibleMovesLeft && lauta.getMoveNumber() < 700) {
            vapaatpisteet = lauta.getMahdollisetPisteet();

            loytyiSiirto = false;
            if (lauta.isPassedOnLastMove()) {
                noSensibleMovesLeft = true;
            }
            if (vapaatpisteet != null) {
                if (CriticalPointObserver.getSelfAtariMove() != -1) {
                    x = Pelilauta.toX(CriticalPointObserver.getSelfAtariMove());
                    y = Pelilauta.toY(CriticalPointObserver.getSelfAtariMove());
                    
                    if (!PlacementHandler.tuhoaakoSiirtoOmanSilman(lauta, x, y)) {
                        if (amafTaulu[Pelilauta.toSimple(x, y)] == 0) {
                            amafTaulu[Pelilauta.toSimple(x, y)] = lauta.getTurn();
                        }
                        PlacementHandler.pelaaSiirto(lauta, x, y);
                        noSensibleMovesLeft = false;
                        loytyiSiirto = true;
                        continue;
                    }
                }

                offset = r.nextInt(vapaatpisteet.length);

                for (int i = 0; i < vapaatpisteet.length; i++) {
                    x = Pelilauta.toX(vapaatpisteet[(i + offset) % vapaatpisteet.length]);
                    y = Pelilauta.toY(vapaatpisteet[(i + offset) % vapaatpisteet.length]);
                    if (!PlacementHandler.tuhoaakoSiirtoOmanSilman(lauta, x, y)) {
                        if (amafTaulu[Pelilauta.toSimple(x, y)] == 0) {
                            amafTaulu[Pelilauta.toSimple(x, y)] = lauta.getTurn();
                        }
                        PlacementHandler.pelaaSiirto(lauta, x, y);
                        noSensibleMovesLeft = false;
                        loytyiSiirto = true;
                        break;
                    }

                }
            }
            if (!loytyiSiirto) {
                PlacementHandler.pass(lauta);
            }
        }
        //GoAI.piirraLauta(lauta);
        /*String debug = "\n";
        for (int j = Pelilauta.getKoko() - 1; j >= 0; j--) {
            debug += "   ";
            for (int i = 0; i < Pelilauta.getKoko(); i++) {
                if (lauta.getRisteys(i, j) == Pelilauta.MUSTA) {
                    debug += " " + "X" + " ";
                }
                else if (lauta.getRisteys(i, j) == Pelilauta.VALKEA) {
                    debug += " " + "O" + " ";
                }
                else {
                    debug += " " + "." + " ";
                }
            }
            debug += "\n";
        }
        GTP.logger.info(debug);*/
        return lauta;
    }

    public static int simulScore(Pelilauta lauta) {
        int x;
        int y;
        double pisteet = -1 * GoAI.simulateKomi; // Alkuarvo on komi, valkealle annettava etu.
        /*
        Stone scoring + silmien lasku
         */
        int kivenvari;
        for (int i = 0; i < Pelilauta.getKoko() * Pelilauta.getKoko(); i++) {
            kivenvari = lauta.getRisteys(Pelilauta.toX(i), Pelilauta.toY(i));
            if (kivenvari == Pelilauta.MUSTA) {
                pisteet++;
            } else if (kivenvari == Pelilauta.VALKEA) {
                pisteet--;
            } else { // Tyhjä risteys
                x = Pelilauta.toX(i);
                y = Pelilauta.toY(i);

                if (lauta.getRisteys(x + 1, y) == Pelilauta.MUSTA
                        || lauta.getRisteys(x - 1, y) == Pelilauta.MUSTA
                        || lauta.getRisteys(x, y - 1) == Pelilauta.MUSTA
                        || lauta.getRisteys(x, y + 1) == Pelilauta.MUSTA) {
                    pisteet++;
                }
                if (lauta.getRisteys(x + 1, y) == Pelilauta.VALKEA
                        || lauta.getRisteys(x - 1, y) == Pelilauta.VALKEA
                        || lauta.getRisteys(x, y - 1) == Pelilauta.VALKEA
                        || lauta.getRisteys(x, y + 1) == Pelilauta.VALKEA) {
                    pisteet--;
                }
            }
        }
        //GoAI.piirraLauta(lauta);
        //System.out.println(pisteet + ", ");
        if (pisteet + GoAI.simulateKomi - Pelilauta.getKomi() > 0) { // NOTE: Simulate komi is added and real komi taken away, not the other way around.
            GoAI.actuaSimulateWins++;
        }
        GoAI.actualSimulateGames++;
        if (pisteet > 0) {
            return 1;
        }
        return -1;
    }

    public static double voitonTodennakoisyys(Node node) {
        if (node.getTurn() == Pelilauta.MUSTA) {
            return 1.0 * GoAI.actuaSimulateWins / GoAI.actualSimulateGames;
        }
        return 1.0 * (GoAI.actualSimulateGames - GoAI.actuaSimulateWins) / GoAI.actualSimulateGames;
        /*double voitot = 1.0;
        double vierailut = 1.0;
        if (node.children == null) {
            return 1;
        }
        for (int i = 0; i < node.children.getKoko(); i++) {
            vierailut += node.children.getNode(i).vierailut;
            voitot += node.children.getNode(i).voitot;
        }
        return voitot / vierailut;*/
    }

}
