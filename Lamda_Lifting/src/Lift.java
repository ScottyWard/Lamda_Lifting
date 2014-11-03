
/*
 * Author: Scotty Ward, sward2011@my.fit.edu
 * Author: Said Al Batrani, salbatrani2014@my.fit.edu
 * Course: CSE 4051, Fall 2014
 * Project: proj08, Lamda Lifting
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Scanner;

public class Lift {

    public static final String METHOD_NAME = "next";
    private static final String DISPLAY_GRAPHICS = "graphics";
    private static final String DISPLAY_TEXT = "text";
    private static final String DISPLAY_NONE = "none";

    public static void main (final String[] args) {
        /*
         * Manditory for first arg to be the map. However, it may be a file or a URL. We
         * need to check if args[0] is a url. Then we need to transform it into either a
         * file or string. Not sure what would be the best yet.
         */
        final String display = getDisplayProperty();
        if (args.length > 0) {
            try {
                final MineEngine engine = new MineEngine(getMine(args[0]));
                if (args.length > 1) {
                    // custom agent
                    simulate(engine, display, Class.forName(args[1]));
                } else if (!display.equals(DISPLAY_GRAPHICS)) {
                    // no agent - manually (stdin)
                    simulate(engine, display, DefaultMiner.class);
                } else {
                    // no agent - graphically (keyboard)
                    simulate(engine, display, null);
                }
            } catch (MalformedURLException e) {
                // invalid URL
            } catch (IOException e) {
                // I/O stream errors
            } catch (ClassNotFoundException e) {
                // invalid agent class or not in the class path
            }
        } else {
            // error map path not supplied
        }

    }

    private static void simulate (final MineEngine engine, final String display,
            final Class agent) {
        try {
            if (display.equals(DISPLAY_GRAPHICS)) {
                // need to start the GUI

            } else {
                final Method method = agent.getDeclaredMethod(METHOD_NAME, new Class[] {
                    MineInterface.class
                });
                final Object[] args = new Object[] {
                    engine.getMine()
                };
                System.out.println(engine.getMineMap());
                int counter = 0;
                while (!engine.isGameOver()) {
                    final char move = (char) method.invoke(null, args);
                    switch (move) {
                    case MineEngine.UP:
                        engine.moveUp();
                        break;
                    case MineEngine.DOWN:
                        engine.moveDown();
                        break;
                    case MineEngine.LEFT:
                        engine.moveLeft();
                        break;
                    case MineEngine.RIGHT:
                        engine.moveRight();
                        break;
                    case MineEngine.ABORT:
                        engine.doAbort();
                        break;
                    default:
                        engine.doWait();
                    }
                    counter++;
                    engine.updateMap();

                    if (display.equals(DISPLAY_TEXT)) {
                        System.out.println(engine.getMineMap());
                        System.out.println(engine.getScore() + " " + move);
                    }
                }
                
                System.out.println(engine.getScore());
                System.out.println("Number of moves: " + counter);
                for (Character c: engine.getMoves()){
                    System.out.printf("%c", c);
                }
            }
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static String getDisplayProperty () {
        final String display = System.getProperty("display");
        if (display != null) {
            if (display.equals(DISPLAY_GRAPHICS) || display.equals(DISPLAY_TEXT)
                    || display.equals(DISPLAY_NONE)) {
                return display;
            }
        }
        return DISPLAY_NONE;
    }

    private static Mine getMine (final String fileName) throws MalformedURLException,
            IOException {
        /*
         * Scotty: Is there a better way to check if the file argument supplied is a URL
         * or a local file?
         */
        InputStream input = null;
        final File file = new File(fileName);
        if (file.isFile()) {
            input = new FileInputStream(file);
        } else {
            input = new URL(fileName).openStream();
        }

        /*
         * read the map text lines (rows), where is the first row line is the last row in
         * the mine map
         */
        int cols = 0;
        final ArrayDeque<String> mapRows = new ArrayDeque<String>();
        try (final Scanner mapFile = new Scanner(input, "US-ASCII");) {
            while (mapFile.hasNextLine()) {
                final String line = mapFile.nextLine();
                if (line == null || line.isEmpty()) {
                    break;
                }
                // track maximum column size of the mine
                final int chars = line.length();
                if (chars > cols) {
                    cols = chars;
                }
                mapRows.push(line);
            }
        }

        /*
         * setup the mine map from raw text to an ASCII character matrix of size rows X
         * columns
         */
        final int rows = mapRows.size();
        final char[][] map = new char[rows][cols];
        int robotRow = 0;
        int robotCol = 0;
        int lambdas = 0;
        for (int y = 0; y < rows; y++) {
            // build rows bottom-up
            final String rowText = mapRows.pop();
            final int chars = rowText.length();
            for (int x = 0; x < cols; x++) {
                // build columns left-right
                if (x < chars) {
                    final char item = rowText.charAt(x);

                    // try to locate the position of the miner robot
                    if (item == Mine.ROBOT) {
                        robotRow = y + 1; // 1..ROWS indexing
                        robotCol = x + 1; // 1..COLS indexing
                    }

                    // count the number of lambdas to be collected
                    if (item == Mine.LAMBDA) {
                        lambdas++;
                    }
                    map[y][x] = item;
                } else {
                    /*
                     * fill the rest trailing cells of the current row in the matrix with
                     * a space object
                     */
                    map[y][x] = Mine.EMPTY;
                }
            }
        }

        assert 1 < robotRow && robotRow < rows;
        assert 1 < robotCol && robotCol < cols;
        return new Mine(map, lambdas, robotRow, robotCol);
    }
}
