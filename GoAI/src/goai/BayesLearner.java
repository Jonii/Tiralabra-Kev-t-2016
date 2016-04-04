/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package goai;

import java.util.logging.Logger;
import logic.PlacementHandler;

/**
 *
 * @author jphanski
 */
public class BayesLearner {

    static Pelilauta lauta;
    static Logger logger;

    static void main() {
        logger = GoAI.logger;
        
        Pattern.init();
        double score = 0;
        int voittaja;
        int kerrat = 0;
        Node valinta;
        while (kerrat < 100) {
            lauta = new Pelilauta(9);
            Pelilauta.setKomi(7.5);
            voittaja = 0;
            valinta = new Node();
            while (true) {
                Node root = valinta;
                root.setTurn(lauta.getTurn());

                GTP.decideSimulationKomi(lauta);
                int simulaatioita = 0;
                //while (System.currentTimeMillis() < now + miettimisAika) {
                while (simulaatioita < GoAI.simulaatioCount) {
                    root.selectAction(lauta);
                    simulaatioita++;
                }
                valinta = root.annaValinta();
                if (valinta == null) {
                    throw new IllegalStateException("Tyhjä siirto valittu");
                }
                //luovutus jos voittotodennäköisyys alle 15%

                if (Node.voitonTodennakoisyys(root) < 0.15) {
                    logger.info("Peli valmis, luovutusvoitto.");
                    if (lauta.getTurn() == Pelilauta.MUSTA) {
                        voittaja = -1;
                    }
                    else {
                        voittaja = 1;
                    }
                    break;
                } else if (valinta.getX() == -1 && valinta.getY() == -1) {
                    if (lauta.isPassedOnLastMove()) {
                        logger.info("Peli valmis, lasketaan pisteet.");
                        break;
                    }
                    PlacementHandler.pass(lauta);
                } else {
                    if (!PlacementHandler.onkoLaillinenSiirto(lauta, valinta.getX(), valinta.getY())) {
                        throw new IllegalStateException("Learning gone wrong, illegal move attempted");
                    }
                    PlacementHandler.pelaaSiirto(lauta, valinta.getX(), valinta.getY());
                    Pattern.pelaaPiste(lauta,valinta.getX(), valinta.getY());
                }
                logger.info("Siirto numero " + lauta.getMoveNumber() + " pelattu: " + GTP.produceCoord(valinta.getX(), valinta.getY()));
                System.out.println("Siirto numero " + lauta.getMoveNumber() + " pelattu: " + GTP.produceCoord(valinta.getX(), valinta.getY()));
            }
            
            //päivitä tiedot, aloita uusi peli.
            System.out.println("Peli valmis");
            if (voittaja == 0) {
                score = GTP.score(lauta);
                Pattern.ilmoitaVoittaja(score);
            }
            else {
                Pattern.ilmoitaVoittaja(voittaja);
            }
            Pattern.writeOut();
            kerrat++;
            
        }
    }
}
