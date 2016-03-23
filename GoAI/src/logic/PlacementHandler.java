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
    
    public void pass() {
        lauta.setPassedOnLastMove(true);
        lauta.changeTurn();
        lauta.setMoveNumber(lauta.getMoveNumber() + 1);
    }
    
    public boolean onkoLaillinenSiirto(int x, int y) {
        final int pelaaja = lauta.getTurn();
        
        if ((x < 0) || (x >= koko) || (y < 0) || (y >= koko)) {
            return false;
        }
        if (lauta.getRisteys(x, y) != Pelilauta.TYHJA) return false;
        
        //vapauksien tarkistus. otetaanKivia tarkistaa saadaanko vapauksia ottamalla laudalta kiviä.
        //vapauksia tarkistaa onko vierellä tyhjiä risteyksiä tai yhteys ryhmiin joilla on vapauksia
        boolean otetaanKivia = false;
        boolean vapauksia = false;
        
        int uusiX = x - 1;
        int uusiY = y;
        if (lauta.getRisteys(uusiX, uusiY) != pelaaja && lauta.getRisteys(uusiX, uusiY) != Pelilauta.TYHJA) {
            if (lauta.getVapaus(uusiX, uusiY) == 1) {
                otetaanKivia = true;
            }
        }
        else if (lauta.getRisteys(uusiX, uusiY) == pelaaja) {
            if (lauta.getVapaus(uusiX, uusiY) > 1) {
                vapauksia = true;
            }
        }
        else if (lauta.getRisteys(uusiX, uusiY) == Pelilauta.TYHJA) {
            if ((uusiX < 0) || (uusiX >= koko) || (uusiY < 0) || (uusiY >= koko)) {
                
            }
            else vapauksia = true;
        }
        
        
        uusiX = x + 1;
        uusiY = y;
        if (lauta.getRisteys(uusiX, uusiY) != pelaaja && lauta.getRisteys(uusiX, uusiY) != Pelilauta.TYHJA) {
            if (lauta.getVapaus(uusiX, uusiY) == 1) {
                otetaanKivia = true;   
            }
        }
        else if (lauta.getRisteys(uusiX, uusiY) == pelaaja) {
            if (lauta.getVapaus(uusiX, uusiY) > 1) {
                vapauksia = true;
            }
        }
        else if (lauta.getRisteys(uusiX, uusiY) == Pelilauta.TYHJA) {
            if ((uusiX < 0) || (uusiX >= koko) || (uusiY < 0) || (uusiY >= koko)) {
                
            }
            else vapauksia = true;
        }
        
        
        uusiX = x;
        uusiY = y + 1;
        if (lauta.getRisteys(uusiX, uusiY) != pelaaja && lauta.getRisteys(uusiX, uusiY) != Pelilauta.TYHJA) {
            if (lauta.getVapaus(uusiX, uusiY) == 1) {
                otetaanKivia = true;
            }
        }
        else if (lauta.getRisteys(uusiX, uusiY) == pelaaja) {
            if (lauta.getVapaus(uusiX, uusiY) > 1) {
                vapauksia = true;
            }
        }
        else if (lauta.getRisteys(uusiX, uusiY) == Pelilauta.TYHJA) {
            if ((uusiX < 0) || (uusiX >= koko) || (uusiY < 0) || (uusiY >= koko)) {
                
            }
            else vapauksia = true;
        }
        
        
        uusiX = x;
        uusiY = y - 1;
        if (lauta.getRisteys(uusiX, uusiY) != pelaaja && lauta.getRisteys(uusiX, uusiY) != Pelilauta.TYHJA) {
            if (lauta.getVapaus(uusiX, uusiY) == 1) {
                otetaanKivia = true;
            }
        }
        else if (lauta.getRisteys(uusiX, uusiY) == pelaaja) {
            if (lauta.getVapaus(uusiX, uusiY) > 1) {
                vapauksia = true;
            }
        }
        else if (lauta.getRisteys(uusiX, uusiY) == Pelilauta.TYHJA) {
            if ((uusiX < 0) || (uusiX >= koko) || (uusiY < 0) || (uusiY >= koko)) {
                
            }
            else vapauksia = true;
        }
        
        if (!vapauksia && !otetaanKivia) return false;
        
        return true;
    }

    /**
     * Pelaa laudalle siirron.
     *
     */
    public void pelaaSiirto(int x, int y) {
        lauta.setMoveNumber(lauta.getMoveNumber() + 1);
        final int pelaaja = lauta.getTurn();
        int uusiX, uusiY;
        if (!onkoLaillinenSiirto(x, y)) {
            lauta.changeTurn();
            return;
        }
        
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
        
        uusiX = x;
        uusiY = y+1;
        if (lauta.getRisteys(uusiX, uusiY) != Pelilauta.TYHJA) laskeVapaudet(uusiX, uusiY);
        uusiX = x;
        uusiY = y-1;
        if (lauta.getRisteys(uusiX, uusiY) != Pelilauta.TYHJA) laskeVapaudet(uusiX, uusiY);
        uusiX = x+1;
        uusiY = y;
        if (lauta.getRisteys(uusiX, uusiY) != Pelilauta.TYHJA) laskeVapaudet(uusiX, uusiY);
        uusiX = x-1;
        uusiY = y;
        if (lauta.getRisteys(uusiX, uusiY) != Pelilauta.TYHJA) laskeVapaudet(uusiX, uusiY);
        
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

                //-1 meinaa että siirtymä ei ollut mahdollinen
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
    
    public boolean tuhoaakoSiirtoOmanSilman(int x, int y) {
        final int pelaaja = lauta.getTurn();
        
        //tarkistetaan onko risteys ympäröity omanvärisillä kivillä
        if 
                ((lauta.getRisteys(x-1, y) != pelaaja && x > 0) || 
                (lauta.getRisteys(x+1, y) != pelaaja && x < lauta.getKoko()-1) || 
                (lauta.getRisteys(x, y-1) != pelaaja && y > 0) || 
                (lauta.getRisteys(x, y+1) != pelaaja && y < lauta.getKoko()-1)) {
            return false;
        }
        
        //risteys ympäröity, eli täytyy tarkistaa ovatko kaikki kivet yhdistetty
        //tätä varten jonkinlainen haku pitkin kiviketjun kiviä. Jokaiselle kohdatulle
        //ympäröivälle kivelle oma boolean. Tämä ei huomaa tiettyjä patologisia silmiä,
        //mutta tuolle ei nyt voi mitään
        boolean eka = false;
        boolean toka = false;
        boolean kolmas = false;
        boolean neljas = false;
        
        Pino<Integer> pino = new Pino<>();

        boolean[] visited = new boolean[koko * koko];

        int uusiSimple;
        
        int currentX, currentY;
        int current;
        if (x > 0) current = lauta.transformToSimpleCoordinates(x-1, y);
        else {
            current = lauta.transformToSimpleCoordinates(x+1, y);
        }
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
                
                if (currentX == x-1 && currentY == y) {
                    eka = true;
                }
                if (currentX == x+1 && currentY == y) {
                    toka = true;
                }
                if (currentX == x && currentY == y-1) {
                    kolmas = true;
                }
                if (currentX == x && currentY == y+1 ) {
                    neljas = true;
                }
                
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

        }
        
        return (eka || x==0) && (toka || x==lauta.getKoko()-1) && (kolmas || y==0) && (neljas || y == lauta.getKoko()-1);
    }
}
