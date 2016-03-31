/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import goai.Pelilauta;

/**
 *
 * @author jphanski
 */
public class CriticalPointObserver {
    private static int selfAtari;

    public static void addSelfAtariMove(int atariPolicy) {
        selfAtari = atariPolicy;
    }
    public static int getSelfAtariMove() {
        return selfAtari;
    }
    public static void nollaaKaikki() {
        selfAtari = -1;
    }
}
