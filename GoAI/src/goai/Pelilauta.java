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
    
    public Pelilauta(int koko, int[][] valmisTaulu, int pelaaja) {
        this.koko = koko;
        this.lautaTaulu = valmisTaulu;
        this.pelaaja = pelaaja;
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
     *  Mikäli risteys o sen valmiiks sinne maaliskuun puoliväliin mennessä tai joskus niihin aikoihin?
10:06:16 <Jonii> Emt, mää kaipaisin pientä sellasta... Mä kirjotin nyt aika kiireessä ja kuumeessa hurjan määrän koodia, tosi vähän testejä mut mulla nyt olis sellane 
                 hyvä fiilis et tästä ehkä kuitenki tulee jotain, mut jos pystyisit katsomaa tota mun koodia hiukan että olenko menossa jossai kohtaa iha täysillä puuta 
                 päin vai eteneeks kaikki ihan ok näkösest?
10:06:39 <osyn> joo voin kattoo, voi tosin mennä maanantaille
10:06:53 <Jonii> Toi koodikatselmointi oli hyödylline, kopioin ton rakenteen idean melkee suoraa koodikatselmoinnista :D
10:06:59 <osyn> noni hyvä:D
10:07:07 <osyn> siitä tais olla useemmalleki hyötyä
10:07:17 <Jonii> Piti kirjottaa about kan epäkelpo, palauttaa TYHJA
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
