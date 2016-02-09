/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package goai;

/**
 *
 * @author jphanski
 */
public class Pelilauta {
    public static final int MUSTA = 1;
    public static final int VALKEA = 2;
    public static final int TYHJA = 0;
    final int koko;
    int[][] lautaTaulu;
    int pelaaja;
    
    public Pelilauta() {
        koko = 19;
        lautaTaulu = new int[koko][koko];
        pelaaja = MUSTA;
    }
    public boolean onkoLaillinenSiirto(int x, int y) {
        if ((x < 0) || (x>=koko) || (y < 0) || (y >= koko)) return false;
        return false;
    }
    /**
     * Tarkistaa onko siirto tyhmä. Toisin sanoen, varmistaa että siirto ei
     * täytä omaa silmää. Poislukien tämä, simulaation aikana tehdyt siirrot ovat
     * täysin satunnaisia.
     * @param x
     * @param y
     * @return 
     */
    public boolean onkoTyhmaSiirto(int x, int y) {
        return false;
    }
    
    /** Pelaa laudalle siirron.
     * 
     */
    public void pelaaSiirto(int x, int y) {
        //if (onkoLaillinenSiirto)
        lautaTaulu[x][y] = pelaaja;
        if (pelaaja == MUSTA) pelaaja = VALKEA;
        else pelaaja = MUSTA;
        return;
    }
    /**
     * Passaa.
     * @param x
     * @param y 
     */
    public void passaa(int x, int y) {
        return;
    }
    
    /** Laittaa kiven laudalle. Ei salli laittomia siirtoja
     * 
     * @param x
     * @param y
     * @param pelaaja puolen numeerinen id, käytä pelilaudan vakioita MUSTA
     * ja VALKEA
     */
    public void laitaKivi(int x, int y, int pelaaja) {
        return;
    }
    
    /**
     * 
     * @param x
     * @param y
     * @return pelaaja jonka kivi on risteyksessä, tai 0 mikäli risteys on tyhjä
     */
    
    public int tarkistaRisteys(int x, int y) {
        return lautaTaulu[x][y];
    }
    
    /**
     * Palauttaa listattuna laudan tyhjät risteykset. 
     * @return Taulukossa on n:s x-koordinaatti paikalla 2*n, ja
     * n:s y-koordinaatti paikalla 2*n+1
     */
    public int[] annaVapaatPisteet() {
        int pos = 0;
        int[] taulu = new int[2*koko*koko];
        
        for (int i = 0; i<koko; i++) {
            for (int j = 0; j<koko; j++) {
                if (this.lautaTaulu[i][j] == TYHJA) {
                    taulu[pos] = i;
                    taulu[pos+1] = j;
                    pos++;
                }
            }
        }
        int[] palautusTaulu = new int[2*pos];
        System.arraycopy(taulu, 0, palautusTaulu, 0, 2*pos);
        return palautusTaulu;
    }

    public int getKoko() {
        return koko;
    }

    public int getPelaaja() {
        return pelaaja;
    }
}
