/*
 * Author: Scotty Ward, sward2011@my.fit.edu
 * Author: Said Al Batrani, salbatrani2014@my.fit.edu
 * Course: CSE 4051, Fall 2014
 * Project: proj08, Lamda Lifting
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class Map {

    private static char[][] MAP;
    private final char[] LEGAL_MOVES = {'L', 'R', 'U', 'D', 'W', 'A'};
    private int gameScore = 0;
    private int width = 0;
    private int height = 0;
    private int minerH = 0;
    private int minerW = 0;
    private int numLamdas = 0;
    
    public Map (final File f) throws FileNotFoundException{
        getWidthHeight(f);
        MAP = new char[height][width];
        fillMap(f);
        printMap();
    }

    /*
     * The rules state that the board is size (n x m) and that n and m
     * are the size of the last line. This method gets the heigth of the board
     * by counting the number of lines and gets the width by how many characters
     * are in the last line.
     */
    private void getWidthHeight (final File f) throws FileNotFoundException {
        Scanner scan = new Scanner(f);
        String line = "";
        while (scan.hasNext()){
            line = scan.nextLine();
            height++;
        }
        width = line.length();
        scan.close();
    }

    /*
     * This method fills the MAP with the input. Some inputs can be shorter
     * on each line than the width of the board. In that case that spot is
     * filled with a space. Otherwise the map is taken straight from the
     * input.
     */
    private void fillMap(final File f) throws FileNotFoundException {
        Scanner scan = new Scanner(f);
        String line = "";
        for (int i = 0; i < height; i++){
            line = scan.nextLine();
            for (int j = 0; j < width; j++){
                if (j >= line.length()){
                    MAP[i][j] = ' ';
                } else {
                    MAP[i][j] = line.charAt(j);
                    /*
                     * gives location of miner (minerH, minerW)
                     */
                    if (MAP[i][j] == 'R'){
                        minerH = i;
                        minerW = j;
                    }
                    if (MAP[i][j] == '\\'){
                        numLamdas++;
                    }
                }
            }
        }
        scan.close();
    }

    /*
     * Moves the robot according to what move is called. If it is an
     * invalid move, wait is executed.
     */
    public void moveRobot(final char move) {
        /*
         * Illegal moves are treated as W moves which
         * is a wait
         */
        if (!Arrays.asList(LEGAL_MOVES).contains(move)){
            return;
        }
        // !!--------- IMPLEMENT HERE -----------!!
        update();
    }

    /*
     * (x,y) -> (width, height)
     * Update rules:
     * if (x,y) contains a Rcok and (x, y-1) is Empty
     *      - (x,y) is updated to Empty, (x, y-1) is Rock
     *      
     * 0..1..2..3..width
     * 1
     * 2
     * 3
     * ..
     * height.....
     */
    public void update() {
        char[][] updatedMap = new char[height][width];
        for (int i = height - 1; i >= 0; i--) {
            for (int j = 0; j < width; j++) {
                char c = MAP[i][j];
                switch (c) {
                case '*': // Rock
                    updatedMap[i][j] = c;
                    if (MAP[i + 1][j] == ' '){
                        updatedMap[i][j] = ' ';
                        updatedMap[i + 1][j] = '*';
                    }

                    if (MAP[i + 1][j] == '*' && MAP[i][j + 1] == ' '
                        && MAP[i + 1][j + 1] == ' '){
                        updatedMap[i][j] = ' ';
                        updatedMap[i + 1][j + 1] = '*';
                    }

                    if (MAP[i + 1][j] == '*' &&
                        (MAP[i][j + 1] != ' ' || MAP[i + 1][j + 1] != ' ') &&
                        (MAP[i][j - 1] == ' ') && (MAP[i + 1][j - 1] == ' ')){
                        updatedMap[i][j] = ' ';
                        updatedMap[i + 1][j - 1] = '*';
                    }

                    if (MAP[i + 1][j] == '\\' && MAP[i][j + 1] == ' ' &&
                        MAP[i + 1][j + 1] == ' '){
                        updatedMap[i][j] = ' ';
                        updatedMap[i + 1][j + 1] = '*';
                    }
                    break;

                case 'L': // Closed Lamda Lift
                    if (numLamdas == 0) {
                        updatedMap[i][j] = 'O';
                    } else {
                        updatedMap[i][j] = c;
                    }
                    break;

                default : // Everything else remains same
                    updatedMap[i][j] = c;
                    break;
                }

            }
        }
        updateMap(updatedMap);
        //printMap();
    }

    /*
     * Assigns the updatedMap from update() to MAP
     */
    private void updateMap(final char[][] uMap) {
        for (int i = 0; i < height; i++){
            for (int j = 0; j < width; j++){
                MAP[i][j] = uMap[i][j];
            }
        }
    }

    public char[][] getMap() {
        return MAP;
    }
    
    public int getGameScore() {
        return gameScore;
    }

    private void printMap() {
        for (int i = 0; i < height; i++){
            for (int j = 0; j < width; j++){
                System.out.printf("%c", MAP[i][j]);
            }
            System.out.println();
        }
    }
}
