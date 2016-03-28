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

    public static void pass(Pelilauta lauta) {
        lauta.setPassedOnLastMove(true);
        lauta.setKo(-1);
        lauta.setSitaEdellinen(lauta.getEdellinen());
        lauta.setEdellinen(-1);
        lauta.changeTurn();
        lauta.setMoveNumber(lauta.getMoveNumber() + 1);
    }

    public static boolean onkoLaillinenSiirto(Pelilauta lauta, int x, int y) {
        final int pelaaja = lauta.getTurn();
        final int koko = lauta.getKoko();

        if ((x < 0) || (x >= koko) || (y < 0) || (y >= koko)) {
            return false;
        }
        if (lauta.getRisteys(x, y) != Pelilauta.TYHJA) {
            return false;
        }

        //kon tarkistus
        int uusiX, uusiY;
        if (lauta.transformToSimpleCoordinates(x, y) == lauta.getKo()) {
            return false;
        }

        //vapauksien tarkistus. otetaanKivia tarkistaa saadaanko vapauksia ottamalla laudalta kiviä.
        //vapauksia tarkistaa onko vierellä tyhjiä risteyksiä tai yhteys ryhmiin joilla on vapauksia
        boolean otetaanKivia = false;
        boolean vapauksia = false;

        uusiX = x - 1;
        uusiY = y;
        if (lauta.getRisteys(uusiX, uusiY) != pelaaja && lauta.getRisteys(uusiX, uusiY) != Pelilauta.TYHJA) {
            if (lauta.getVapaus(uusiX, uusiY) == 1) {
                otetaanKivia = true;
            }
        } else if (lauta.getRisteys(uusiX, uusiY) == pelaaja) {
            if (lauta.getVapaus(uusiX, uusiY) > 1) {
                vapauksia = true;
            }
        } else if (lauta.getRisteys(uusiX, uusiY) == Pelilauta.TYHJA) {
            if ((uusiX < 0) || (uusiX >= koko) || (uusiY < 0) || (uusiY >= koko)) {

            } else {
                vapauksia = true;
            }
        }

        uusiX = x + 1;
        uusiY = y;
        if (lauta.getRisteys(uusiX, uusiY) != pelaaja && lauta.getRisteys(uusiX, uusiY) != Pelilauta.TYHJA) {
            if (lauta.getVapaus(uusiX, uusiY) == 1) {
                otetaanKivia = true;
            }
        } else if (lauta.getRisteys(uusiX, uusiY) == pelaaja) {
            if (lauta.getVapaus(uusiX, uusiY) > 1) {
                vapauksia = true;
            }
        } else if (lauta.getRisteys(uusiX, uusiY) == Pelilauta.TYHJA) {
            if ((uusiX < 0) || (uusiX >= koko) || (uusiY < 0) || (uusiY >= koko)) {

            } else {
                vapauksia = true;
            }
        }

        uusiX = x;
        uusiY = y + 1;
        if (lauta.getRisteys(uusiX, uusiY) != pelaaja && lauta.getRisteys(uusiX, uusiY) != Pelilauta.TYHJA) {
            if (lauta.getVapaus(uusiX, uusiY) == 1) {
                otetaanKivia = true;
            }
        } else if (lauta.getRisteys(uusiX, uusiY) == pelaaja) {
            if (lauta.getVapaus(uusiX, uusiY) > 1) {
                vapauksia = true;
            }
        } else if (lauta.getRisteys(uusiX, uusiY) == Pelilauta.TYHJA) {
            if ((uusiX < 0) || (uusiX >= koko) || (uusiY < 0) || (uusiY >= koko)) {

            } else {
                vapauksia = true;
            }
        }

        uusiX = x;
        uusiY = y - 1;
        if (lauta.getRisteys(uusiX, uusiY) != pelaaja && lauta.getRisteys(uusiX, uusiY) != Pelilauta.TYHJA) {
            if (lauta.getVapaus(uusiX, uusiY) == 1) {
                otetaanKivia = true;
            }
        } else if (lauta.getRisteys(uusiX, uusiY) == pelaaja) {
            if (lauta.getVapaus(uusiX, uusiY) > 1) {
                vapauksia = true;
            }
        } else if (lauta.getRisteys(uusiX, uusiY) == Pelilauta.TYHJA) {
            if ((uusiX < 0) || (uusiX >= koko) || (uusiY < 0) || (uusiY >= koko)) {

            } else {
                vapauksia = true;
            }
        }

        if (!vapauksia && !otetaanKivia) {
            return false;
        }

        return true;
    }

    /**
     * Pelaa laudalle siirron.
     *
     */
    public static void pelaaSiirto(Pelilauta lauta, int x, int y) {
        lauta.setMoveNumber(lauta.getMoveNumber() + 1);
        lauta.setPassedOnLastMove(false);

        final int pelaaja = lauta.getTurn();
        int uusiX, uusiY;
        if (!onkoLaillinenSiirto(lauta, x, y)) {
            pass(lauta);
            return;
        }
        lauta.setKo(-1);
        lauta.setSitaEdellinen(lauta.getEdellinen());
        lauta.setEdellinen(lauta.transformToSimpleCoordinates(x, y));
        uusiX = x - 1;
        uusiY = y;
        if (lauta.getRisteys(uusiX, uusiY) != pelaaja && lauta.getRisteys(uusiX, uusiY) != Pelilauta.TYHJA) {
            if (lauta.getVapaus(uusiX, uusiY) == 1) {
                poistaKivet(lauta, uusiX, uusiY);
            }
        }
        uusiX = x + 1;
        uusiY = y;
        if (lauta.getRisteys(uusiX, uusiY) != pelaaja && lauta.getRisteys(uusiX, uusiY) != Pelilauta.TYHJA) {
            if (lauta.getVapaus(uusiX, uusiY) == 1) {
                poistaKivet(lauta, uusiX, uusiY);
            }
        }
        uusiX = x;
        uusiY = y + 1;
        if (lauta.getRisteys(uusiX, uusiY) != pelaaja && lauta.getRisteys(uusiX, uusiY) != Pelilauta.TYHJA) {
            if (lauta.getVapaus(uusiX, uusiY) == 1) {
                poistaKivet(lauta, uusiX, uusiY);
            }
        }
        uusiX = x;
        uusiY = y - 1;
        if (lauta.getRisteys(uusiX, uusiY) != pelaaja && lauta.getRisteys(uusiX, uusiY) != Pelilauta.TYHJA) {
            if (lauta.getVapaus(uusiX, uusiY) == 1) {
                poistaKivet(lauta, uusiX, uusiY);
            }
        }

        lauta.setRisteys(x, y, pelaaja);
        laskeVapaudet(lauta, x, y);

        uusiX = x;
        uusiY = y + 1;
        if (lauta.getRisteys(uusiX, uusiY) != Pelilauta.TYHJA) {
            laskeVapaudet(lauta, uusiX, uusiY);
        }
        uusiX = x;
        uusiY = y - 1;
        if (lauta.getRisteys(uusiX, uusiY) != Pelilauta.TYHJA) {
            laskeVapaudet(lauta, uusiX, uusiY);
        }
        uusiX = x + 1;
        uusiY = y;
        if (lauta.getRisteys(uusiX, uusiY) != Pelilauta.TYHJA) {
            laskeVapaudet(lauta, uusiX, uusiY);
        }
        uusiX = x - 1;
        uusiY = y;
        if (lauta.getRisteys(uusiX, uusiY) != Pelilauta.TYHJA) {
            laskeVapaudet(lauta, uusiX, uusiY);
        }

        lauta.changeTurn();
    }

    private static void laskeVapaudet(Pelilauta lauta, int x, int y) {
        final int koko = lauta.getKoko();
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

    private static void poistaKivet(Pelilauta lauta, int x, int y) {
        final int koko = lauta.getKoko();

        Pino<Integer> pino = new Pino<>();

        boolean[] visited = new boolean[koko * koko];

        int pelaaja = lauta.getRisteys(x, y);

        int uusiSimple;

        int currentX, currentY;

        int poistettujenMaara = 0;

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
                poistettujenMaara++;

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
                laskeVapaudet(lauta, currentX, currentY);
            }

        }
        if (poistettujenMaara == 1) {
            lauta.setKo(lauta.transformToSimpleCoordinates(x, y));
        }
    }

    /**
     * Tarkistaa tuhoaako siirto simulaatiovaiheessa silmän. Tämä aiheuttaa
     * tiettyjen tilanteiden väärinarvioimisen(simulaatiossa tietyt ikävät
     * tilanteet ovat yleisempiä kuin mitä niiden kuuluisi olla), mutta
     * algoritmi on hyvin kevyt eikä systemaattisesti tee mitään 100% varmaa
     * virhettä.
     *
     * @param lauta
     * @param x
     * @param y
     * @return
     */
    public static boolean tuhoaakoSiirtoOmanSilman(Pelilauta lauta, int x, int y) {
        final int koko = lauta.getKoko();
        final int pelaaja = lauta.getTurn();

        //tarkistetaan onko risteys ympäröity omanvärisillä kivillä
        if ((lauta.getRisteys(x - 1, y) != pelaaja && x > 0)
                || (lauta.getRisteys(x + 1, y) != pelaaja && x < koko - 1)
                || (lauta.getRisteys(x, y - 1) != pelaaja && y > 0)
                || (lauta.getRisteys(x, y + 1) != pelaaja && y < koko - 1)) {
            return false;
        }
        //tarkistetaan onko jokin ympäröivä kivi atarissa
        else if ((x == 0 || lauta.getVapaus(x - 1, y) != 1)
                && (x == koko - 1 || lauta.getVapaus(x + 1, y) != 1)
                && (y == 0 || lauta.getVapaus(x, y - 1) != 1)
                && (y == koko - 1 || lauta.getVapaus(x, y + 1) != 1)) {
            return true;
        }
        return false;
    }

    /*public static boolean onkoRyhmaElossa(Pelilauta lauta, int x, int y) {
        

        //risteys ympäröity, eli täytyy tarkistaa saadaanko kivet jotenkin vangittua.
        //Tarkistetaan siis onko sarja yksittäisiä laillisia vastustajan siirtoja,
        //jotka saisivat jonkin ympäröivän kiviryhmän atariin.

        int iteraatio;

        Pino<Integer> pino = new Pino<>();
        Pino<Integer> alkupistePino = new Pino<>();

        int[] iteraatioTaulu;
        int uusiSimple;

        int currentX, currentY;
        int current;
        if (lauta.moveLeft(lauta.transformToSimpleCoordinates(x, y)) != -1) {
            current = lauta.moveLeft(lauta.transformToSimpleCoordinates(x, y));
            alkupistePino.add(current);
        }
        if (lauta.moveRight(lauta.transformToSimpleCoordinates(x, y)) != -1) {
            current = lauta.moveRight(lauta.transformToSimpleCoordinates(x, y));
            alkupistePino.add(current);
        }
        if (lauta.moveUp(lauta.transformToSimpleCoordinates(x, y)) != -1) {
            current = lauta.moveUp(lauta.transformToSimpleCoordinates(x, y));
            alkupistePino.add(current);
        }
        if (lauta.moveDown(lauta.transformToSimpleCoordinates(x, y)) != -1) {
            current = lauta.moveDown(lauta.transformToSimpleCoordinates(x, y));
            alkupistePino.add(current);
        }
        while (alkupistePino.IsNotEmpty()) {            // Käydään kaikki ympäröivät kiviryhmät läpi erikseen 
            pino.add(alkupistePino.pop());
            iteraatio = 1;
            iteraatioTaulu = new int[koko * koko];

            while (pino.IsNotEmpty()) {
                current = pino.pop();
                currentX = lauta.transformToXCoordinate(current);
                currentY = lauta.transformToYCoordinate(current);

                if (iteraatioTaulu[current] == iteraatio) {
                    continue;
                }

                uusiSimple = lauta.moveLeft(current);
                if (uusiSimple != -1) {
                    if (lauta.getRisteys(lauta.transformToXCoordinate(uusiSimple), lauta.transformToYCoordinate(uusiSimple)) == Pelilauta.TYHJA) {
                        if (iteraatioTaulu[uusiSimple] == 0) {
                            lauta.changeTurn();
                            if (onkoLaillinenSiirto(lauta, lauta.transformToXCoordinate(uusiSimple), lauta.transformToYCoordinate(uusiSimple))) {
                                iteraatio++;
                                iteraatioTaulu[uusiSimple] = iteraatio;
                            }
                            lauta.changeTurn();

                        }
                    } else if (lauta.getRisteys(lauta.transformToXCoordinate(uusiSimple), lauta.transformToYCoordinate(uusiSimple)) == pelaaja) {
                        pino.add(uusiSimple);
                    }
                    
                }
                uusiSimple = lauta.moveRight(current);
                if (uusiSimple != -1) {
                    if (lauta.getRisteys(lauta.transformToXCoordinate(uusiSimple), lauta.transformToYCoordinate(uusiSimple)) == Pelilauta.TYHJA) {
                        if (iteraatioTaulu[uusiSimple] == 0) {
                            lauta.changeTurn();
                            if (onkoLaillinenSiirto(lauta, lauta.transformToXCoordinate(uusiSimple), lauta.transformToYCoordinate(uusiSimple))) {
                                iteraatio++;
                                iteraatioTaulu[uusiSimple] = iteraatio;
                            }
                            lauta.changeTurn();

                        }
                    } 
                    
                    else if (lauta.getRisteys(lauta.transformToXCoordinate(uusiSimple), lauta.transformToYCoordinate(uusiSimple)) == pelaaja) {
                        pino.add(uusiSimple);
                    }                    
                }
                uusiSimple = lauta.moveUp(current);
                if (uusiSimple != -1) {
                    if (lauta.getRisteys(lauta.transformToXCoordinate(uusiSimple), lauta.transformToYCoordinate(uusiSimple)) == Pelilauta.TYHJA) {
                        if (iteraatioTaulu[uusiSimple] == 0) {
                            lauta.changeTurn();
                            if (onkoLaillinenSiirto(lauta, lauta.transformToXCoordinate(uusiSimple), lauta.transformToYCoordinate(uusiSimple))) {
                                iteraatio++;
                                iteraatioTaulu[uusiSimple] = iteraatio;
                            }
                            lauta.changeTurn();

                        }
                    } else if (lauta.getRisteys(lauta.transformToXCoordinate(uusiSimple), lauta.transformToYCoordinate(uusiSimple)) == pelaaja) {
                        pino.add(uusiSimple);
                    }                    
                }
                uusiSimple = lauta.moveDown(current);
                if (uusiSimple != -1) {
                    if (lauta.getRisteys(lauta.transformToXCoordinate(uusiSimple), lauta.transformToYCoordinate(uusiSimple)) == Pelilauta.TYHJA) {
                        if (iteraatioTaulu[uusiSimple] == 0) {
                            lauta.changeTurn();
                            if (onkoLaillinenSiirto(lauta, lauta.transformToXCoordinate(uusiSimple), lauta.transformToYCoordinate(uusiSimple))) {
                                iteraatio++;
                                iteraatioTaulu[uusiSimple] = iteraatio;
                            }
                            lauta.changeTurn();

                        }
                    } else if (lauta.getRisteys(lauta.transformToXCoordinate(uusiSimple), lauta.transformToYCoordinate(uusiSimple)) == pelaaja) {
                        pino.add(uusiSimple);
                    }                    
                }
                iteraatioTaulu[current] = iteraatio;
                if (lauta.getVapaus(currentX, currentY) - iteraatio == 0) return false;
            }
            
        }

        return true;
    }*/
}
