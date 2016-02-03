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
     * pelaamalla koordinaatteihin -1, -1, pelaaja passaa.
     */
    public void pelaaSiirto(int x, int y) {
        //if (onkoLaillinenSiirto)
        lautaTaulu[x][y] = pelaaja;
        if (pelaaja == MUSTA) pelaaja = VALKEA;
        else pelaaja = MUSTA;
        return;
    }
    
    public int tarkistaRisteys(int x, int y) {
        return lautaTaulu[x][y];
    }
    
    /**
     * Palauttaa listattuna laudan tyhjät risteykset. 
     * @return Taulukossa on n:s x-koordinaatti paikalla 2*n, ja
     * n:s y-koordinaatti paikalla 2*n+1
     */
    public int[] annaVapaatPisteet() {
        
        return null;
    }
}
