/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package goai;


/** Binäärihaun toteutus RAVEa varten
 *
 * @author jphanski
 */
public class NodenLapset {
    private Node[] lapset;
    private int koko;

    public int getKoko() {
        return koko;
    }

    public void setKoko(int koko) {
        this.koko = koko;
    }
    
    public NodenLapset(Node[] children) {
        this.lapset = children;
        this.koko = children.length;
        sort();
    }
    public Node getNode(int index) {
        if ((index < 0 ) || index >= koko) return null;
        return lapset[index];
    }
    
    /**
     * Järjestää suurimmasta simple-alkiosta pienimpään.
     */
    public void sort() {
        int bubbleRange = 0;
        int bubbleBacktrack = 1;
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
