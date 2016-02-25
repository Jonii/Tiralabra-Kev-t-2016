/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package goai;

import java.util.Arrays;

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

    

    
    /** Laittaa kiven laudalle. Sallii minkä tahansa siirron
     * 
     * @param x
     * @param y
     * @param pelaaja puolen numeerinen id, käytä pelilaudan vakioita MUSTA
     * ja VALKEA
     */
    public void setRisteys(int x, int y, int pelaaja) {
        return;
    }
    
    /** Palauttaa laudan risteyksen sisällön.
     *  Mikäli risteys on epäkelpo, palauttaa TYHJA
     * @param x
     * @param y
     * @return pelaaja jonka kivi on risteyksessä, tai TYHJA. Jos risteys ei ole laudalla, palauttaa TYHJA
     */
    
    public int getRisteys(int x, int y) {
        if ((x < 0) || (x >= koko) || (y < 0) || (y >= koko)) return TYHJA;
        return lautaTaulu[x][y];
    }
    
    /**
     * Palauttaa listattuna laudan tyhjät risteykset.
     * @return Tyhjät risteykset listattuna simple-formaattia käyttäen. Käytä
     * transformToXCoordinate ja transformToYCoordinate() funktioita.
     */
    public int[] getVapaatPisteet() {
        int pos = 0;
        int[] taulu = new int[koko*koko];
        
        for (int i = 0; i<koko; i++) {
            for (int j = 0; j<koko; j++) {
                if (this.lautaTaulu[i][j] == TYHJA) {
                    taulu[pos] = transformToSimpleCoordinates(i, j);
                    pos++;
                }
            }
        }
        if (pos == 0) return null;
        int[] palautusTaulu = new int[pos];
        System.arraycopy(taulu, 0, palautusTaulu, 0, 2*pos);
        return palautusTaulu;
    }
    /**
     * Palauttaa laudan koon. Lauta on aina neliö, yleensä 5x5, 7x7, 9x9, 13x13 tai 19x19
     * @return laudan sivus pituus.
     */
    public int getKoko() {
        return koko;
    }

    public int[][] getArray() {
        int[][] palautus = new int[koko][koko];
        for (int i = 0; i<koko; i++) {
            for (int j = 0; j<koko; j++) {
                palautus[i][j] = this.lautaTaulu[i][j];
            }
        }
        return palautus;
    }

    public void changeTurn() {
        if (pelaaja == Pelilauta.MUSTA) {
            pelaaja = Pelilauta.VALKEA;
            return;
        }
        pelaaja = Pelilauta.MUSTA;
    }
    
    /**
     * Palauttaa sen pelaajan koodin jonka senhetkinen vuoro on.
     * @return MUSTA tai VALKEA
     */
    public int getTurn() {
        return pelaaja;
    }
    
    /**
     * Muuttaa sen, kenen vuoro on siirtää seuraavaksi, koskematta laudan tilanteeseen.
     * @param pelaaja 
     */
    public void setTurn(int pelaaja) {
        if (pelaaja == MUSTA) this.pelaaja = pelaaja;
        if (pelaaja == VALKEA) this.pelaaja = pelaaja;
    }
    
    public int transformToSimpleCoordinates(int x, int y) {
        return y + koko * x;
    }
    public int transformToXCoordinate(int simple) {
        return simple / koko;
    }
    public int transformToYCoordinate(int simple) {
        return simple % koko;
    }
}
