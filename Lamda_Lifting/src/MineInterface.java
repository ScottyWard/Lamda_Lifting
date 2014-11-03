/*
 * Author: Scotty Ward, sward2011@my.fit.edu
 * Author: Said Al Batrani, salbatrani2014@my.fit.edu
 * Course: CSE 4051, Fall 2014
 * Project: proj08, Lamda Lifting
 */

/*
 * This interface defines the contracts of the mine status. Designed by Dr. Stansifer 
 * in the Lambda Lift web page located at http://cs.fit.edu/~ryan/java/programs/llift/
 */
public interface MineInterface {
    public int getRows (); // (bottom/down) 1..R (top/up)

    public int getCols (); // (left/L) 1..C (right/R)

    public int lambdasRemaining (); // the number of lambdas left

    public int getMinerRow (); // The current location of the robot miner (row).

    public int getMinerCol (); // The current location of the robot miner (column).

    // The object "#* \R.LO" in the mine at any given position
    public char getChar (final int row, final int col);
}
