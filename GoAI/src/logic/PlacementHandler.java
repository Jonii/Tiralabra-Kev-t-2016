/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;
import goai.Pelilauta;
import static goai.Pelilauta.MUSTA;
import static goai.Pelilauta.VALKEA;

/**
 *
 * @author jphanski
 */
public class PlacementHandler {
    private Pelilauta lauta;
    private int koko;

    public PlacementHandler(Pelilauta lauta) {
        this.lauta = lauta;
        this.koko = this.lauta.getKoko();
    }
    
    public boolean onkoLaillinenSiirto(int x, int y) {
        if ((x < 0) || (x>=koko) || (y < 0) || (y >= koko)) return false;
        return false;
    }
    
    /** Pelaa laudalle siirron.
    * 
    */
    public void pelaaSiirto(int x, int y) {
        final int pelaaja = lauta.getTurn();
        //if (onkoLaillinenSiirto)
        lauta.setRisteys(x, y, pelaaja);
        int[] visited = new int[koko * koko];
        
        lauta.changeTurn();
    }
}
