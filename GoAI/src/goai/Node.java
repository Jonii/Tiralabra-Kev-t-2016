package goai;
import java.util.LinkedList;
import java.util.Random;

/**
 * Hakupuun solmu.
 * @author jphanski
 */
public class Node {
    Node[] children;
    
    static Random r = new Random();
    static double epsilon = 1e-6;
    
    int vierailut, voitot;
    
    Pelilauta lauta;
    
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
        double value = simulate(newNode);
        for (Node node : visited) {
            node.updateStats(value);
        }
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
        return;
    }
    
    public double simulate(Node node) {
        int length = lauta.annaVapaatPisteet().length / 2;
        r.nextInt(length);
        return -1;
    }

    protected boolean isLeaf() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    protected void updateStats(double value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
