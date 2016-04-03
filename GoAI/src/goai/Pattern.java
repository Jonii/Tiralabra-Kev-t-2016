/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package goai;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import logic.PlacementHandler;

/**
 *
 * @author jphanski
 */
public class Pattern {
    private int blackWins;
    private int seenTotal;
    private int seen;

    private int pattern;
    private int[] symmetries;
    private int[] colorSwapSymmetries;
    public static final int variations = Math.round((float) Math.pow(3, 9));
    private static Pattern[] patterns;
    private static Logger logger;
    public static String patternDataFilePath = "/home/jphanski/goai/patterndata";
    
    public Pattern() {
    }

    public static void init() {
        logger = GoAI.logger;
        patterns = new Pattern[variations];
        Path file = Paths.get(patternDataFilePath);
        Charset charset = Charset.forName("US-ASCII");
        try (BufferedReader reader = Files.newBufferedReader(file, charset)) {

            for (int i = 0; i < variations; i++) {
                patterns[i] = new Pattern(i);
                patterns[i].blackWins = Integer.parseInt(reader.readLine());
                patterns[i].seenTotal = Integer.parseInt(reader.readLine());
            }
        } catch (IOException ex) {
            logger.warning("Pattern data file missing:\n" + ex);
            for (int i = 0; i < variations; i++) {
                patterns[i] = new Pattern(i);
            }
        }

    }

    public static void writeOut() {
        Charset charset = Charset.forName("US-ASCII");
        Path file = Paths.get(patternDataFilePath);
        try (BufferedWriter writer = Files.newBufferedWriter(file, charset)) {
            for (int i = 0; i < variations; i++) {
                writer.write(patterns[i].blackWins + "\n");
                writer.write(patterns[i].seenTotal + "\n");
            }
            
        } catch (IOException ex) {
            logger.warning("Could not open Pattern data file(" + file +") for writing: \n" + ex);
        }
    }
    
    static void pelaaPiste(Pelilauta lauta, int x, int y) {
        for (int i = -1; i< 2; i++) {
            for (int j = -1; j<2; j++) {
                if (Pelilauta.onLaudalla(x+i, y+j)) {
                    patterns[match(lauta, x+i, y+j)].seen++;
                }
            }
        }
    }
    
    static void ilmoitaVoittaja(double score) {
        for (int i = 0; i<variations; i++) {
            for (int j = 0; j < patterns[i].symmetries.length; j++) {
                patterns[patterns[i].colorSwapSymmetries[j]].seenTotal += patterns[i].seen;
                patterns[patterns[i].symmetries[j]].seenTotal += patterns[i].seen;
                if (score < 0) {
                    patterns[patterns[i].colorSwapSymmetries[j]].blackWins += patterns[i].seen;
                }
                else {
                    patterns[patterns[i].symmetries[j]].blackWins += patterns[i].seen;
                }
            }
            patterns[i].seen = 0;
        }
    }
    

    public Pattern(int pattern) {
        this.pattern = pattern;
        boolean[] visited = new boolean[variations];
        symmetries = new int[matchSymmetries(visited, pattern)];
        colorSwapSymmetries = new int[symmetries.length];
        int indeksi = 0;
        for (int i = 0; i < variations; i++) {
            if (visited[i]) {
                symmetries[indeksi] = i;
                colorSwapSymmetries[indeksi] = swapColors(i);
                {   // Dunno if necessary, but make colorswapsymmetries an ordered list. This can be helpful, not sure yet.
                    int swapOrder = indeksi;
                    int temp;
                    while (swapOrder > 0) {
                        if (colorSwapSymmetries[swapOrder] < colorSwapSymmetries[swapOrder - 1]) {
                            temp = colorSwapSymmetries[swapOrder];
                            colorSwapSymmetries[swapOrder] = colorSwapSymmetries[swapOrder - 1];
                            colorSwapSymmetries[swapOrder - 1] = temp;
                            swapOrder--;
                        } else {
                            break;
                        }
                    }
                }
                indeksi++;
            }
        }
    }

    public static int match(Pelilauta lauta, int x, int y) {
        int testiPattern = 0;

        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                testiPattern += insert(lauta.getRisteys(x + i, y + j), i, j);
            }
        }
        return testiPattern;
    }

    /**
     * writes true to all positions corresponding to pattern int, on table
     * visited. returns the number of symmetric positions. Pattern itself is
     * always symmetric with itself, so returns always at least 1.
     *
     * @param visited In the beginning this should be false-initialized boolean
     * table. In the end it has true written on each index corresponding to
     * pattern number which is symmetric with this pattern.
     * @param pattern Pattern to be matched.
     * @return The number of symmetric positions.
     */
    private static int matchSymmetries(boolean[] visited, int pattern) {
        if (visited[pattern]) {
            return 0;
        }
        visited[pattern] = true;

        int clockPattern;
        int mirrorPattern;

        //rotate clockwise
        clockPattern = 0;
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                clockPattern += insert(decode(pattern, i, j), (-1 * j), i);
            }
        }

        //mirror
        mirrorPattern = 0;
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                mirrorPattern += insert(decode(pattern, i, j), (-1 * i), j);
            }
        }
        int tulos = 0;
        return 1 + matchSymmetries(visited, mirrorPattern) + matchSymmetries(visited, clockPattern);
    }

    static int insert(int color, int x, int y) {
        return Math.round((float) Math.pow(3, (x + 1) + 3 * (y + 1))) * color;
    }

    static int decode(int pattern, int x, int y) {
        int index = x + 1 + 3 * (y + 1);
        int kolmosenKanta = Math.round((float) Math.pow(3, index));
        return (pattern / kolmosenKanta) % 3;
    }

    private static int swapColors(int pattern) {
        int palautusPattern = 0;
        int color;
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                color = decode(pattern, i, j);
                if (color == Pelilauta.MUSTA) {
                    palautusPattern += insert(Pelilauta.VALKEA, i, j);
                } else if (color == Pelilauta.VALKEA) {
                    palautusPattern += insert(Pelilauta.MUSTA, i, j);
                }
            }
        }
        return palautusPattern;
    }
}
