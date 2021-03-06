/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package goai;

import java.util.Arrays;
import logic.PlacementHandler;

/**
 * Laudan risteyksen sisältöä kuvaava olio. Sisältää paitsi tiedon siitä, onko
 * risteys tyhjä, vai onko sillä jommankumman pelaajan kivi, myös tiedon siitä,
 * montako vapautta on sillä kiviketjulla johon tässä risteyksessä oleva kivi kuulu.
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

/**
 * Pelilauta. Sisältää kaiken tiedon pelilaudan tilanteesta.
 * @author jphanski
 */
public class Pelilauta {

    public static final int MUSTA = 1;
    public static final int VALKEA = 2;
    public static final int TYHJA = 0;
    
    private static int koko;
    
    private Alkio[][] lautaTaulu;
    private int pelaaja;
    private boolean passedOnLastMove;
    
    private int moveNumber;
    
    private int edellinen;
    private int sitaEdellinen;
    
    /**
     * simplistiseen kon tarkistukseen käytettävä muuttuja
     */
    private int ko;    
    
    /**
     * Valkealle annettava tasoitus
     */
    private static double komi = 0.5;
    
    public Pelilauta() {
        this(19);
    }

    public Pelilauta(int koko) {
        Pelilauta.koko = koko;
        Node.laudanKoko = koko;
        
        this.edellinen = -1;
        this.sitaEdellinen = -1;
        this.ko = -1;
        
        lautaTaulu = new Alkio[koko][koko];
        for (int i = 0; i<koko; i++) {
            for (int j = 0; j < koko; j++) {
                lautaTaulu[i][j] = new Alkio();
            }
        }
        pelaaja = MUSTA;
        passedOnLastMove = false;
    }

    public int getKo() {
        return ko;
    }

    public void setKo(int ko) {
        this.ko = ko;
    }

    public int getEdellinen() {
        return edellinen;
    }

    public void setEdellinen(int edellinen) {
        this.edellinen = edellinen;
    }

    public int getSitaEdellinen() {
        return sitaEdellinen;
    }

    public void setSitaEdellinen(int sitaEdellinen) {
        this.sitaEdellinen = sitaEdellinen;
    }

    public int getMoveNumber() {
        return moveNumber;
    }

    public void setMoveNumber(int moveNumber) {
        this.moveNumber = moveNumber;
    }

    public static double getKomi() {
        return komi;
    }

    public static void setKomi(double komi) {
        Pelilauta.komi = komi;
    }

    public boolean isPassedOnLastMove() {
        return passedOnLastMove;
    }

    public void setPassedOnLastMove(boolean passedOnLastMove) {
        this.passedOnLastMove = passedOnLastMove;
    }

    /**
     * Antaa kivijonon vapaudet. Voi olla suurempi kuin 4, pienimmillään 0. Toiminta ei määriteltyä jos risteyksessä ei kiveä.
     * 
     * @param x koordinaatti 9 - koko-1
     * @param y koordinaattii 0 - koko-1
     * 
     */    
    public int getVapaus(int x, int y) {
        if (!onLaudalla(x, y)) {
            return -1;
        }
        if (getRisteys(x, y) == TYHJA) {
            return -1;
        }
        return lautaTaulu[x][y].getVapaudet();  
    }
    /**
     * Muuttaa yhden kiven vapausmuuttujaa. Jokaisen samaan kiviketjuun kuuluvan
     * kiven vapausarvoa tulisi muuttaa samalla kertaa, samaan arvoon.
     * @param x
     * @param y
     * @param vapaus Nolla tai suurempi arvo. 
     */
    public void setVapaus(int x, int y, int vapaus) {
        if (!onLaudalla(x, y)) {
            return;
        }
        lautaTaulu[x][y].setVapaudet(vapaus);
    }
    
    public static boolean onLaudalla(int x, int y) {
        if ((x < 0) || (x >= koko) || (y < 0) || (y >= koko)) {
            return false;
        }
        return true;
    }
    /**
     * Asettaa laudan risteyksen sisällön. Ei sisällä mitään siirtologiikkaa,
     * käytä luokkaa placementhandler varmistamaan että sääntöjä noudatetaan.
     * @param x
     * @param y
     * @param pelaaja MUSTA, VALKEA tai TYHJA.
     */
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
 toX ja toY() funktioita.
     */
    public int[] getMahdollisetPisteet() {
        int pos = 0;
        int[] taulu = new int[koko * koko];

        for (int i = 0; i < koko; i++) {
            for (int j = 0; j < koko; j++) {
                if (PlacementHandler.onkoLaillinenSiirto(this, i, j)) {
                    taulu[pos] = toSimple(i, j);
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
    public static int getKoko() {
        return koko;
    }
    
    /** Kopioi laudan
     * 
     * @return uusi Pelilauta-olio joka on täysin samassa tilanteessa kuin nykyinen. 
     */
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
        palautus.setMoveNumber(getMoveNumber());
        
        palautus.setEdellinen(edellinen);
        palautus.setSitaEdellinen(sitaEdellinen);
        palautus.setKo(ko);
        return palautus;
    }
    
    /** Vaihtaa siirtovuoroa.
     * 
     */
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
    /**
     * Apufunktioita koordinaattien muuttamiseen yhdeksi int-arvoksi ja takaisin.
     * toSimple(int, int) ottaa koordinaatit x ja y, ja palauttaa
 yhden numeron, simplen, joka koodaa tätä koordinaattiyhdistelmää.
 toX(int) ottaa simplen, ja palauttaa sitä vastaavan x-koordinaatin.
 toY(int) ottaa simplen, ja palauttaa sitä vastaavan y-koordinaatin.
     * 
     * @param x 0:sta lähtevä x-koordinaatti pelilaudalla
     * @param y 0:sta lähtevä y-koordinaatti pelilaudalla
     * @return nämä numerot 1 to 1 koodaava simple-koordinaatti
     */
    public static int toSimple(int x, int y) {
        if (x < 0 || x >= Pelilauta.getKoko() || y < 0 || y >= Pelilauta.getKoko()) return -1;
        return y + Pelilauta.getKoko() * x;
    }
    
    /**
     * Apufunktioita koordinaattien muuttamiseen yhdeksi int-arvoksi ja takaisin.
     * toSimple(int, int) ottaa koordinaatit x ja y, ja palauttaa
 yhden numeron, simplen, joka koodaa tätä koordinaattiyhdistelmää.
 toX(int) ottaa simplen, ja palauttaa sitä vastaavan x-koordinaatin.
 toY(int) ottaa simplen, ja palauttaa sitä vastaavan y-koordinaatin.
     * 
     * @param simple Simple-arvo, joka on saatu toSimple() funktiolla.
     * @return simple-arvoa vastaava X-koordinaatti.
     */
    public static int toX(int simple) {
        return simple / Pelilauta.getKoko();
    }
    /**
     * Apufunktioita koordinaattien muuttamiseen yhdeksi int-arvoksi ja takaisin.
     * toSimple(int, int) ottaa koordinaatit x ja y, ja palauttaa
 yhden numeron, simplen, joka koodaa tätä koordinaattiyhdistelmää.
 toX(int) ottaa simplen, ja palauttaa sitä vastaavan x-koordinaatin.
 toY(int) ottaa simplen, ja palauttaa sitä vastaavan y-koordinaatin.
     * 
     * @param simple Simple-arvo, joka on saatu toSimple() funktiolla.
     * @return simple-arvoa vastaava Y-koordinaatti.
     */
    public static int toY(int simple) {
        return simple % Pelilauta.getKoko();
    }

    /**
     * apufunktioita simple-koordinaatistossa hyppäämiseen sivulle, ylös tai alas.
     * @param simple simple-arvo
     * @return uusi simple-koordinaatti halutussa suunnassa, tai -1 jos laudan ulkopuolella
     */
    public static int moveLeft(int simple) {
        if (simple < 0 || simple >= Pelilauta.getKoko() * Pelilauta.getKoko()) {
            return -1;
        }
        return toSimple(toX(simple) - 1, toY(simple));
    }
    /**
     * apufunktioita simple-koordinaatistossa hyppäämiseen sivulle, ylös tai alas.
     * @param simple simple-arvo
     * @return uusi simple-koordinaatti halutussa suunnassa, tai -1 jos laudan ulkopuolella
     */
    public static int moveRight(int simple) {
        if (simple < 0 || simple >= Pelilauta.getKoko() * Pelilauta.getKoko()) {
            return -1;
        }
        return toSimple(toX(simple) + 1, toY(simple));
    }    
    /**
     * apufunktioita simple-koordinaatistossa hyppäämiseen sivulle, ylös tai alas.
     * @param simple simple-arvo
     * @return uusi simple-koordinaatti halutussa suunnassa, tai -1 jos laudan ulkopuolella
     */
    public static int moveUp(int simple) {
        if (simple < 0 || simple >= Pelilauta.getKoko() * Pelilauta.getKoko()) {
            return -1;
        }
 
        return toSimple(toX(simple), toY(simple) + 1);
    }
    /**
     * apufunktioita simple-koordinaatistossa hyppäämiseen sivulle, ylös tai alas.
     * @param simple simple-arvo
     * @return uusi simple-koordinaatti halutussa suunnassa, tai -1 jos laudan ulkopuolella
     */
    public static int moveDown(int simple) {
        if (simple < 0 || simple >= Pelilauta.getKoko() * Pelilauta.getKoko()) {
            return -1;
        }
        return toSimple(toX(simple), toY(simple) - 1);
    }

}
