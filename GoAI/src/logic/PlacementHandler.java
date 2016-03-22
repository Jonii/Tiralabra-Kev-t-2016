/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import goai.Pelilauta;
import logic.Pino;
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
        if ((x < 0) || (x >= koko) || (y < 0) || (y >= koko)) {
            return false;
        }
        return false;
    }

    /**
     * Pelaa laudalle siirron.
     *
     */
    public void pelaaSiirto(int x, int y) {
        final int pelaaja = lauta.getTurn();
        int uusiX, uusiY;

        //tarkista ko;
        uusiX = x - 1;
        uusiY = y;
        if (lauta.getRisteys(uusiX, uusiY) != pelaaja && lauta.getRisteys(uusiX, uusiY) != Pelilauta.TYHJA) {
            if (lauta.getVapaus(uusiX, uusiY) == 1) {
                poistaKivet(uusiX, uusiY);
            }
        }
        uusiX = x + 1;
        uusiY = y;
        if (lauta.getRisteys(uusiX, uusiY) != pelaaja && lauta.getRisteys(uusiX, uusiY) != Pelilauta.TYHJA) {
            if (lauta.getVapaus(uusiX, uusiY) == 1) {
                poistaKivet(uusiX, uusiY);
            }
        }
        uusiX = x;
        uusiY = y + 1;
        if (lauta.getRisteys(uusiX, uusiY) != pelaaja && lauta.getRisteys(uusiX, uusiY) != Pelilauta.TYHJA) {
            if (lauta.getVapaus(uusiX, uusiY) == 1) {
                poistaKivet(uusiX, uusiY);
            }
        }
        uusiX = x;
        uusiY = y - 1;
        if (lauta.getRisteys(uusiX, uusiY) != pelaaja && lauta.getRisteys(uusiX, uusiY) != Pelilauta.TYHJA) {
            if (lauta.getVapaus(uusiX, uusiY) == 1) {
                poistaKivet(uusiX, uusiY);
            }
        }

        lauta.setRisteys(x, y, pelaaja);
        laskeVapaudet(x, y);
        lauta.changeTurn();
    }

    private void laskeVapaudet(int x, int y) {
        Pino<Integer> pino = new Pino<>();
        Pino<Integer> kiviKetju = new Pino<>();

        boolean[] visited = new boolean[koko * koko];

        int pelaaja = lauta.getRisteys(x, y);
        int vapaudet = 0;

        int uusiSimple;
        
        int currentX, currentY;

        int current = lauta.transformToSimpleCoordinates(x, y);
        pino.add(current);

        while (pino.IsNotEmpty()) {
            current = pino.pop();
            
            currentX = lauta.transformToXCoordinate(current);
            currentY = lauta.transformToYCoordinate(current);

            if (visited[current]) {
                continue;
            }
            visited[current] = true;

            if (lauta.getRisteys(currentX, currentY) == pelaaja) {
                kiviKetju.add(current);

                //y-akselilla siirtyminen simple-koordinaatistossa on koko-parametrin ynnäystä,
                //kun taas x-akselilla siirtyminen on ykkösen ynnäystä. Rumaa mutta toimii.
                uusiSimple = lauta.moveLeft(current);
                if (uusiSimple != -1) {
                    pino.add(uusiSimple);
                }
                uusiSimple = lauta.moveRight(current);
                if (uusiSimple != -1) {
                    pino.add(uusiSimple);
                }
                uusiSimple = lauta.moveUp(current);
                if (uusiSimple != -1) {
                    pino.add(uusiSimple);
                }
                uusiSimple = lauta.moveDown(current);
                if (uusiSimple != -1) {
                    pino.add(uusiSimple);
                }
            }

            if (lauta.getRisteys(currentX, currentY) == Pelilauta.TYHJA) {
                vapaudet++;
            }

        }

        while (kiviKetju.IsNotEmpty()) {
            current = kiviKetju.pop();
            lauta.setVapaus(lauta.transformToXCoordinate(current), lauta.transformToYCoordinate(current), vapaudet);
        }
    }

    private void poistaKivet(int x, int y) {
        Pino<Integer> pino = new Pino<>();

        boolean[] visited = new boolean[koko * koko];

        int pelaaja = lauta.getRisteys(x, y);

        int uusiSimple;
        
        int currentX, currentY;

        int current = lauta.transformToSimpleCoordinates(x, y);
        pino.add(current);

        while (pino.IsNotEmpty()) {
            current = pino.pop();
            currentX = lauta.transformToXCoordinate(current);
            currentY = lauta.transformToYCoordinate(current);
            
            if (visited[current]) {
                continue;
            }
            visited[current] = true;

            if (lauta.getRisteys(currentX, currentY) == pelaaja) {
                
                lauta.setRisteys(currentX, currentY, Pelilauta.TYHJA);
                
                uusiSimple = lauta.moveLeft(current);
                if (uusiSimple != -1) {
                    pino.add(uusiSimple);
                }
                uusiSimple = lauta.moveRight(current);
                if (uusiSimple != -1) {
                    pino.add(uusiSimple);
                }
                uusiSimple = lauta.moveUp(current);
                if (uusiSimple != -1) {
                    pino.add(uusiSimple);
                }
                uusiSimple = lauta.moveDown(current);
                if (uusiSimple != -1) {
                    pino.add(uusiSimple);
                }
            }
            if (lauta.getRisteys(currentX, currentY) != Pelilauta.TYHJA) {
                laskeVapaudet(currentX, currentY);
            }

        }
    }
}
