import java.util.Arrays;

/*
 * Author: Scotty Ward, sward2011@my.fit.edu
 * Author: Said Al Batrani, salbatrani2014@my.fit.edu
 * Course: CSE 4051, Fall 2014
 * Project: proj08, Lamda Lifting
 */

/**
 * Represents the state of the mine
 */
public final class Mine implements MineInterface {

    // possible mine objects
    public static final char ROBOT = 'R';
    public static final char EARTH = '.';
    public static final char EMPTY = ' ';
    public static final char BRICK = '#';
    public static final char ROCK = '*';
    public static final char LAMBDA = '\\';
    public static final char CLOSED_LIFT = 'L';
    public static final char OPEN_LIFT = 'O';

    private final char[][] map;
    private final int rows;
    private final int cols;
    private final int lambdas;
    private int robotRow;
    private int robotCol;
    private int collectedLambdas;

    public Mine (final char[][] aMap, final int aLambdas, final int aRobotRow,
            final int aRobotCol) {
        map = aMap;
        rows = map.length;
        cols = map[0].length;
        lambdas = aLambdas;
        robotRow = aRobotRow;
        robotCol = aRobotCol;
        collectedLambdas = 0;
    }

    @Override
    public int getRows () {
        return rows;
    }

    @Override
    public int getCols () {
        return cols;
    }

    @Override
    public int lambdasRemaining () {
        return lambdas - collectedLambdas;
    }

    @Override
    public int getMinerRow () {
        return robotRow;
    }

    @Override
    public int getMinerCol () {
        return robotCol;
    }

    @Override
    public char getChar (final int row, final int col) {
        return map[row - 1][col - 1];
    }

    private void setChar (final int row, final int col, final char item) {
        map[row - 1][col - 1] = item;
    }

    public int getCollectedLambdas () {
        return collectedLambdas;
    }

    /*
     * Returns an ASCII textual representation of the current mine map
     */
    public String getMineAsText () {
        final StringBuilder sb = new StringBuilder();
        for (int row = rows; row >= 1; row--) {
            for (int col = 1; col <= cols; col++) {
                sb.append(getChar(row, col));
            }
            if (row != 1) {
                sb.append(System.lineSeparator());
            }
        }
        return sb.toString();
    }

    public void collectLambda () {
        collectedLambdas++;
    }

    public char moveRobotTo (final int row, final int col) {
        // get the current object present in this position
        final char item = getChar(row, col);
        char newRobotValue;
        if (item == OPEN_LIFT) {
            // robot exits and disappear from map
            newRobotValue = OPEN_LIFT;
        } else {
            // still visible
            newRobotValue = ROBOT;
        }

        // need to move the robot to its new position and update its old position to empty
        setChar(robotRow, robotCol, EMPTY);
        robotRow = row;
        robotCol = col;
        setChar(row, col, newRobotValue);
        return item;
    }

    public void moveRockLeft (final int row, final int col) {
        setChar(row, col - 1, ROCK);
    }

    public void moveRockRight (final int row, final int col) {
        setChar(row, col + 1, ROCK);
    }

    public void dropRockDown (final int row, final int col) {
        setChar(row, col, EMPTY);
        setChar(row - 1, col, ROCK);
    }

    public void dropRockDownRight (final int row, final int col) {
        setChar(row, col, EMPTY);
        setChar(row - 1, col + 1, ROCK);
    }

    public void dropRockDownLeft (final int row, final int col) {
        setChar(row, col, EMPTY);
        setChar(row - 1, col - 1, ROCK);
    }

    public void openLift (final int row, final int col) {
        setChar(row, col, OPEN_LIFT);
    }

    public char[][] getMapCopy () {
        final char[][] copy = new char[rows][cols];
        for (int i = 0; i < copy.length; i++) {
            copy[i] = Arrays.copyOf(map[i], cols);
        }
        return copy;
    }
}
