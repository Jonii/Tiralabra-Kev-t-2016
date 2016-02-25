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
    
    int vierailut, voitot;
    
    public void Node() {
    }
    
    public void Node(Pelilauta lauta) {
        this.lauta = lauta;
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
        //for (Node node : visited) {
        //    node.updateStats(value);
        //}
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
        int pisteita = 6;
        int p;
        int[] lapsitaulu = new int[6];
        int[] vapaatpisteet = lauta.getVapaatPisteet();
        for (int i = 0; i<pisteita; i++) {
            do {
                p = r.nextInt(vapaatpisteet.length);
            } while (vapaatpisteet[p] == -1);
            lapsitaulu[i] = vapaatpisteet[p];
            vapaatpisteet[p] = -1;
        }
        return;
    }
    
    protected boolean isLeaf() {
        if (children == null) return true;
        if (children.length == 0) throw new IllegalStateException("Malformed search tree?"); 
        return false;
    }

    protected void updateStats(double value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
