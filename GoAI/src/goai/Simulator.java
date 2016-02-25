/** Simulator class for doing simulation phase of Monte Carlo Tree Search
 * 
 */
package goai;
import goai.Pelilauta;
import java.util.Random;
import logic.PlacementHandler;
/**
 *
 * @author jphanski
 */
public class Simulator {
    private Pelilauta lauta;
    private PlacementHandler handler;
    Random r;
    
    public Simulator(Pelilauta lauta) {
        this.lauta = lauta;
        r = new Random();
        handler = new PlacementHandler(this.lauta);
    }
    
    /**
     * -1 jos valkea voittaa, +1 jos musta voittaa, 0 jos tasapeli.
     * @return 
     */
    public int simulate() {
        int[] vapaatpisteet;
        int offset;
        int x;
        int y;
        boolean noSensibleMovesLeft = false;
        while(!noSensibleMovesLeft) {
            vapaatpisteet = lauta.getVapaatPisteet();
            offset = r.nextInt(vapaatpisteet.length);
            for (int i = 0; i<vapaatpisteet.length; i++) {
                x = lauta.transformToXCoordinate(vapaatpisteet[i+offset]);
                y = lauta.transformToYCoordinate(vapaatpisteet[i+offset]);
                if (!tuhoaakoSiirtoOmanSilman(x, y)) {
                    handler.pelaaSiirto(x, y);
                    noSensibleMovesLeft = false;
                    break;
                }
                noSensibleMovesLeft = true;
            }
            
        }
        //calculate score here
        
        return -2;
    }
    
    /**
     * Tarkistaako onko siirtokandidaatti minimikelvollinen simulaatiosiirroksi.
     * @param x
     * @param y
     * @return 
     */
    private boolean tuhoaakoSiirtoOmanSilman(int x, int y) {
        int i = 0;
        if (lauta.getRisteys(x+1, y) == lauta.getTurn()) i++;
        if (lauta.getRisteys(x+1, y+1) == lauta.getTurn()) i++;
        if (lauta.getRisteys(x+1, y-1) == lauta.getTurn()) i++;
        if (lauta.getRisteys(x, y+1) == lauta.getTurn()) i++;
        if (lauta.getRisteys(x, y-1) == lauta.getTurn()) i++;
        if (lauta.getRisteys(x-1, y) == lauta.getTurn()) i++;
        if (lauta.getRisteys(x-1, y+1) == lauta.getTurn()) i++;
        if (lauta.getRisteys(x-1, y-1) == lauta.getTurn()) i++;
        return i>6;
    }
}
