/*
 * Author: Scotty Ward, sward2011@my.fit.edu
 * Author: Said Al Batrani, salbatrani2014@my.fit.edu
 * Course: CSE 4051, Fall 2014
 * Project: proj08, Lamda Lifting
 */

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;

import javax.swing.SwingUtilities;

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
                final MineEngine engine = new MineEngine(Mine.createMine(args[0]));
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
            final Class<?> agent) {
        try {
            if (display.equals(DISPLAY_GRAPHICS)) {
                // need to start the GUI
                SwingUtilities.invokeLater(new Runnable() {
                    public void run () {
                        new MineGraphics(engine, agent);
                    }
                });
            } else {
                // get the static method from the agent class using reflection
                final Method method = agent.getDeclaredMethod(METHOD_NAME, new Class[] {
                    MineInterface.class
                });
                final Object[] args = new Object[] {
                    engine.getMine()
                };

                // play the game
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
                    engine.updateMap();

                    // print every move with score when display mode is set to text
                    if (display.equals(DISPLAY_TEXT)) {
                        System.out.println(engine.getMineMap());
                        System.out.println(engine.getScore());
                    }
                }

                // display final score at the end of the game
                System.out.println(engine.getScore());
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

}
