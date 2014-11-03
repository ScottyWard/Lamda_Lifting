/*
 * Author: Scotty Ward, sward2011@my.fit.edu
 * Author: Said Al Batrani, salbatrani2014@my.fit.edu
 * Course: CSE 4051, Fall 2014
 * Project: proj08, Lamda Lifting
 */

import java.util.Scanner;
import java.util.regex.Pattern;

/*
 * 
 */
public class DefaultMiner {

    final static Scanner STDIN = new Scanner(System.in, "US-ASCII").useDelimiter("");

    private static final Pattern DOT = Pattern.compile(".", Pattern.DOTALL);

    // Return next character in input stream, or
    // raise null pointer exception (on EOF)
    public static char nextChar (Scanner s) throws NullPointerException {
        return s.findWithinHorizon(DOT, 1).charAt(0);
    }

    public static char next (final MineInterface mine) {
        final char ch = nextChar(STDIN);
        return ch;
    }
}
