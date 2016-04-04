/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package goai;

import java.util.Random;

/** Binäärihaun toteutus RAVEa varten.
 * This class operates in two separate modes, controlled by sortedBySimple
 * In the first mode, all children nodes are sorted by their simple coordinate value
 * to enable fast update of RAVE values. In the second mode, all children
 * nodes are sorted by their value, as measured by valueOf() function, to enable
 * quickly adding new nodes in the MCTS node expansion phase.
 *
 * @author jphanski
 */
public class NodenLapset {
    private Node[] lapset;
    private int koko;
    private int maxKoko;
    private boolean sortedBySimple;
    
    public int getKoko() {
        return koko;
    }
    
    public void setMaxKoko(int maxKoko) {
        if (sortedBySimple) {
            sortByVisits();
        }
        this.maxKoko = maxKoko;
        if (koko > maxKoko) {
            koko = maxKoko;
            Node[] uusiLapset = new Node[maxKoko];
            for (int i = 0; i<maxKoko; i++) {
                uusiLapset[i] = lapset[i];
            }
            lapset = uusiLapset;
        }        
        
    }
    public NodenLapset() {
        this.koko = 0;
        this.maxKoko = 1000;
        this.lapset = new Node[23];
    }
    public NodenLapset(Node[] children) {
        this.lapset = children;
        this.koko = children.length;
        this.maxKoko = 1000;
        sort();
    }
    public Node getNode(int index) {
        if ((index < 0 ) || index >= koko) return null;
        return lapset[index];
    }
    public void addNode(Node node) {
        if (sortedBySimple) {
            sortByVisits();
        }
        if (koko == maxKoko) {
            if (valueOf(node) + node.getTieBreaker() > valueOf(lapset[koko-1]) + lapset[koko-1].getTieBreaker()) {
                lapset[koko-1] = node;
                sortByVisits();
            }
            return;
        }
        if (lapset.length == koko) {
            Node[] uusiLapset = new Node[koko * 2];
            for (int i = 0; i<koko; i++) {
                uusiLapset[i] = lapset[i];
            }
            lapset = uusiLapset;
        }
        lapset[koko] = node;
        koko++;
        sortByVisits();
    }
   
    
    public void sortByVisits() {
        sortedBySimple = false;
        int bubbleRange = 0;
        int bubbleBacktrack = 1;
        Node tmp;
        while (bubbleRange < koko-1) {
            bubbleBacktrack = bubbleRange + 1;
            while (valueOf(getNode(bubbleBacktrack)) + getNode(bubbleBacktrack).getTieBreaker() > valueOf(getNode(bubbleBacktrack-1)) + getNode(bubbleBacktrack-1).getTieBreaker()) {
                tmp = lapset[bubbleBacktrack];
                lapset[bubbleBacktrack] = lapset[bubbleBacktrack-1];
                lapset[bubbleBacktrack-1] = tmp;
                if (bubbleBacktrack > 1) {
                    bubbleBacktrack--;
                }
            }
            bubbleRange++;            
        
        }
    }
     private int valueOf(Node node) {
        return node.raveVierailut + node.vierailut * 10;
    }
    /**
     * Järjestää suurimmasta simple-alkiosta pienimpään.
     */        
   
    /**
     * Järjestää suurimmasta simple-alkiosta pienimpään.
     */

    
        
   
    /**
     * Järjestää suurimmasta simple-alkiosta pienimpään.
     */
    public void sort() {
        sortedBySimple = true;
        int bubbleRange = 0;
        int bubbleBacktrack;
        Node tmp;
        while (bubbleRange < koko-1) {
            bubbleBacktrack = bubbleRange + 1;
            while (getNode(bubbleBacktrack).getSimple() > getNode(bubbleBacktrack-1).getSimple()) {
                tmp = lapset[bubbleBacktrack];
                lapset[bubbleBacktrack] = lapset[bubbleBacktrack-1];
                lapset[bubbleBacktrack-1] = tmp;
                if (bubbleBacktrack > 1) {
                    bubbleBacktrack--;
                }
            }
            bubbleRange++;            
        }
    }
    
    /**
     * Hakee lapsista sellaisen, jonka siirron koordinaatit vastaavat simpleä.
     * Binäärihaku.
     * 
     * @param simple koordinaatti simple-muodossa
     * @return indeksi josta oikea vastaus löytyy, tai -1 jos ei löydy.
     */
    public int find(int simple) {
        if (!sortedBySimple) {
            sort();
        }
        int leftIndex, rightIndex;
        leftIndex = 0;
        rightIndex = koko - 1;
        
        int current;
        while (leftIndex <= rightIndex) {
            current = (leftIndex + rightIndex)/2;
            if (getNode(current).getSimple() == simple) return current;
            else if (getNode(current).getSimple() > simple) {
                leftIndex = current + 1;
            }
            else if (getNode(current).getSimple() < simple) {
                rightIndex = current - 1;
            }
        }
        
        return -1;
    }
}
