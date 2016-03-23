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
class Alkio {

    private int kivi;
    private int vapaudet;    
    
    public int getKivi() {
        return kivi;
    }

    public void setKivi(int kivi) {
        this.kivi = kivi;
    }

    public int getVapaudet() {
        return vapaudet;
    }

    public void setVapaudet(int vapaudet) {
        this.vapaudet = vapaudet;
    }
    Alkio() {
        kivi = 0;
        vapaudet = 0;
    }
    
    
}

public class Pelilauta {

    public static final int MUSTA = 1;
    public static final int VALKEA = 2;
    public static final int TYHJA = 0;
    private final int koko;
    Alkio[][] lautaTaulu;
    private int pelaaja;
    private boolean passedOnLastMove;
    
    private int moveNumber;

    public Pelilauta() {
        this(19);
    }

    public Pelilauta(int koko) {
        this.koko = koko;
        lautaTaulu = new Alkio[koko][koko];
        for (int i = 0; i<koko; i++) {
            for (int j = 0; j < koko; j++) {
                lautaTaulu[i][j] = new Alkio();
            }
        }
        pelaaja = MUSTA;
        passedOnLastMove = false;
    }

    public int getMoveNumber() {
        return moveNumber;
    }

    public void setMoveNumber(int moveNumber) {
        this.moveNumber = moveNumber;
    }

    public boolean isPassedOnLastMove() {
        return passedOnLastMove;
    }

    public void setPassedOnLastMove(boolean passedOnLastMove) {
        this.passedOnLastMove = passedOnLastMove;
    }

    /*public Pelilauta(int koko, int[][] valmisTaulu, int pelaaja) {
        this.koko = koko;
        this.lautaTaulu = valmisTaulu;
        this.pelaaja = pelaaja;
    }*/

    /**
     * Antaa kivijonon vapaudet. Voi olla suurempi kuin 4, pienimmillään 0. Toiminta ei määriteltyä jos risteyksessä ei kiveä.
     * 
     * @param x koordinaatti 9 - koko-1
     * @param y koordinaattii 0 - koko-1
     */
    
    
    
    public int getVapaus(int x, int y) {
        return lautaTaulu[x][y].getVapaudet();
    }
    public void setVapaus(int x, int y, int vapaus) {
        lautaTaulu[x][y].setVapaudet(vapaus);
    }
    public void setRisteys(int x, int y, int pelaaja) {
        lautaTaulu[x][y].setKivi(pelaaja);
    }

    /**
     * Palauttaa laudan risteyksen sisällön.
     *
     * @param x
     * @param y
     * @return pelaaja jonka kivi on risteyksessä, tai TYHJA. Jos risteys ei ole
     * laudalla, palauttaa TYHJA
     */
    public int getRisteys(int x, int y) {
        if ((x < 0) || (x >= koko) || (y < 0) || (y >= koko)) {
            return TYHJA;
        }
        return lautaTaulu[x][y].getKivi();
    }

    /**
     * Palauttaa listattuna laudan tyhjät risteykset.
     *
     * @return Tyhjät risteykset listattuna simple-formaattia käyttäen. Käytä
     * transformToXCoordinate ja transformToYCoordinate() funktioita.
     */
    public int[] getVapaatPisteet() {
        int pos = 0;
        int[] taulu = new int[koko * koko];

        for (int i = 0; i < koko; i++) {
            for (int j = 0; j < koko; j++) {
                if (this.lautaTaulu[i][j].getKivi() == TYHJA) {
                    taulu[pos] = transformToSimpleCoordinates(i, j);
                    pos++;
                }
            }
        }
        if (pos == 0) {
            return null;
        }
        int[] palautusTaulu = new int[pos];
        System.arraycopy(taulu, 0, palautusTaulu, 0, pos);
        return palautusTaulu;
    }

    /**
     * Palauttaa laudan koon. Lauta on aina neliö, yleensä 5x5, 7x7, 9x9, 13x13
     * tai 19x19
     *
     * @return laudan sivus pituus.
     */
    public int getKoko() {
        return koko;
    }

    public int[][] getArray() {
        int[][] palautus = new int[koko][koko];
        for (int i = 0; i < koko; i++) {
            for (int j = 0; j < koko; j++) {
                palautus[i][j] = this.lautaTaulu[i][j].getKivi();
            }
        }
        return palautus;
    }
    public Pelilauta kopioi() {
        Pelilauta palautus = new Pelilauta(getKoko());
        Alkio uusi;
        for (int i = 0; i<getKoko(); i++) {
            for (int j = 0; j<getKoko(); j++) {
                uusi = new Alkio();
                uusi.setKivi(getRisteys(i, j));
                uusi.setVapaudet(getVapaus(i, j));
                palautus.lautaTaulu[i][j] = uusi;
            }
        }
        if (getTurn() != Pelilauta.MUSTA) palautus.changeTurn();
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
     *
     * @return MUSTA tai VALKEA
     */
    public int getTurn() {
        return pelaaja;
    }

    /**
     * Muuttaa sen, kenen vuoro on siirtää seuraavaksi, koskematta laudan
     * tilanteeseen.
     *
     * @param pelaaja
     */
    public void setTurn(int pelaaja) {
        if (pelaaja == MUSTA) {
            this.pelaaja = pelaaja;
        }
        if (pelaaja == VALKEA) {
            this.pelaaja = pelaaja;
        }
    }

    public int transformToSimpleCoordinates(int x, int y) {
        if (x < 0 || x >= koko || y < 0 || y >= koko) return -1;
        return y + koko * x;
    }

    public int transformToXCoordinate(int simple) {
        return simple / koko;
    }

    public int transformToYCoordinate(int simple) {
        return simple % koko;
    }

    boolean sensible(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    public int moveLeft(int simple) {
        int palautus = simple - 1;
        if (palautus % this.getKoko() < this.getKoko() -1) return palautus;
        return -1;
    }
    public int moveRight(int simple) {
        int palautus = simple + 1;
        if (palautus % this.getKoko() > 0) return palautus;
        return -1;
    }
    public int moveUp(int simple) {
        int palautus = simple + this.getKoko();
        if (palautus < this.getKoko() * this.getKoko()) return palautus;
        return -1;
    }
    public int moveDown(int simple) {
        int palautus = simple - this.getKoko();
        if (palautus >= 0) return palautus;
        return -1;
    }

}
