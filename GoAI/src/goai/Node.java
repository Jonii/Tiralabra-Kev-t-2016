package goai;
import java.util.LinkedList;
import java.util.Random;

/**
 * Hakupuun solmu.
 * @author jphanski
 */
public class Node {
    Node[] children;
    Pelilauta lauta;
    static Random r = new Random();
    static double epsilon = 1e-6;
    
    int x, y;
    
    int vierailut, voitot;
    
    public Node() {
    }
    
    public Node(Pelilauta lauta) {
        this.lauta = lauta;
    }
    public Node(Pelilauta lauta, int x, int y) {
        this.lauta = lauta;
        this.x = x;
        this.y = y;
    }
    
    public void selectAction() { 
        LinkedList<Node> visited = new LinkedList<>();
        visited.add(this);
        Node currentNode = this;
        
        while (!currentNode.isLeaf()) {
            currentNode = currentNode.select();
            visited.add(currentNode);
        }
        currentNode.expand();
        Node newNode = currentNode.select();
        visited.add(newNode);
        //Simulation phase
        //Update phase
        return;
    }
    
    /**
     * UCT valinta.
     * 
     * Kuulemma ei käytännöllinen, mutta kokeillaan..
     * @return paras node, UCT politiikalla
     */
    private Node select() {
        Node selected = null;
        double bestValue = Double.MIN_VALUE;
        for (Node c : children) {
            double uctValue =
                    c.voitot / (c.vierailut + epsilon) +
                            Math.sqrt(Math.log(vierailut+1) / (c.vierailut + epsilon)) +
                            r.nextDouble() * epsilon;
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
        int pisteita = 20;
        int p;
        int[] vapaatpisteet = lauta.getVapaatPisteet();
        if (vapaatpisteet.length < pisteita) pisteita = vapaatpisteet.length;
        children = new Node[pisteita];
        for (int i = 0; i<pisteita; i++) {
            do {
                p = r.nextInt(vapaatpisteet.length);
            } while (vapaatpisteet[p] == -1);
            children[i] = new Node(lauta, lauta.transformToXCoordinate(vapaatpisteet[p]), lauta.transformToYCoordinate(vapaatpisteet[p]));
            vapaatpisteet[p] = -1;
        }
    }
    
    protected boolean isLeaf() {
        if (children == null) return true;
        if (children.length == 0) throw new IllegalStateException("Malformed search tree?"); 
        return false;
    }

    protected void updateStats(double value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    /**
     * Annetaan eniten vierailtu solmu siirron pelaamista varten. Tämän funktion voi ajaa koska tahansa
     * mutta tietty on parempi mitä useampia simulaatioita on keretty ajaa.
     * @return 
     */
    Node annaValinta() {
        int highest = 0;
        int highestIndex = 0;
        for (int i = 0; i<children.length; i++) {
            if (children[i].vierailut > highest) {
                highest = children[i].vierailut;
                highestIndex = i;
            }
        }
        return children[highestIndex];
    }
    
}
