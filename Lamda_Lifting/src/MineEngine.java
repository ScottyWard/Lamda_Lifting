/*
 * Author: Scotty Ward, sward2011@my.fit.edu
 * Author: Said Al Batrani, salbatrani2014@my.fit.edu
 * Course: CSE 4051, Fall 2014
 * Project: proj08, Lamda Lifting
 */

import java.util.ArrayList;
import java.util.List;

public final class MineEngine {

    // scoring
    public static final int MOVE_COST = -1;
    public static final int LAMBDA_BONUS = 25;
    public static final int LAMBDA_ABORT_BONUS = 25;
    public static final int LAMBDA_WON_BONUS = 50;

    // possible moves
    public static final char UP = 'U';
    public static final char DOWN = 'D';
    public static final char LEFT = 'L';
    public static final char RIGHT = 'R';
    public static final char WAIT = 'W';
    public static final char ABORT = 'A';

    // possible miner state
    public static final String MINING = "mining";
    public static final String WON = "won";
    public static final String DESTROYED = "destroyed";
    public static final String ABORTED = "aborted";

    private final Mine mine;
    private final List<Character> moves;
    private int score;
    private String status;

    public MineEngine (final Mine aMine) {
        mine = aMine;
        moves = new ArrayList<Character>();
        score = 0;
        status = MINING;
    }

    public Mine getMine () {
        return mine;
    }

    public List<Character> getMoves () {
        return moves;
    }

    public int getScore () {
        return score;
    }

    public String getStatus () {
        return status;
    }

    public String getMineMap () {
        return mine.getMineAsText();
    }

    public boolean isGameOver () {
        if (status.equals(MINING)) {
            return false;
        }
        return true;
    }

    public char doWait () {
        score += MOVE_COST;
        moves.add(Character.valueOf(WAIT));
        return WAIT;
    }

    public char doAbort () {
        status = ABORTED;
        score += LAMBDA_ABORT_BONUS * mine.getCollectedLambdas();
        score += MOVE_COST;
        moves.add(Character.valueOf(ABORT));
        return ABORT;
    }

    public char moveUp () {
        return move(mine.getMinerRow() + 1, mine.getMinerCol(), UP);
    }

    public char moveDown () {
        return move(mine.getMinerRow() - 1, mine.getMinerCol(), DOWN);
    }

    public char moveLeft () {
        return move(mine.getMinerRow(), mine.getMinerCol() - 1, LEFT);
    }

    public char moveRight () {
        return move(mine.getMinerRow(), mine.getMinerCol() + 1, RIGHT);
    }

    private char move (final int row, final int col, final char direction) {
        // check the supplied position is within the mine boundaries
        if (!isValidRow(row) || !isValidCol(col)) {
            return doWait();
        }

        /*
         * check if the robot has space in that position to move into and must consider
         * moving rocks sideways if necessary
         */
        if (!isSpaceAvailable(row, col, direction)) {
            return doWait();
        }

        // it is safe to move
        final char prevItem = mine.moveRobotTo(row, col);
        if (prevItem == Mine.LAMBDA) {
            mine.collectLambda();
            score += LAMBDA_BONUS;
        } else if (prevItem == Mine.OPEN_LIFT) {
            score += LAMBDA_WON_BONUS * mine.getCollectedLambdas();
            status = WON;
        } else if (prevItem == Mine.ROCK) {
            moveRockAt(row, col, direction);
        }
        score += MOVE_COST;
        moves.add(Character.valueOf(direction));
        return direction;
    }

    private boolean isSpaceAvailable (final int row, final int col, final char direction) {
        switch (mine.getChar(row, col)) {
        case Mine.EMPTY:
        case Mine.EARTH:
        case Mine.LAMBDA:
        case Mine.OPEN_LIFT:
            return true;

        case Mine.ROCK:
            if ((direction == LEFT && canMoveRockTo(row, col - 1))
                    || (direction == RIGHT && canMoveRockTo(row, col + 1))) {
                return true;
            }
        default:
            return false;
        }
    }

    private boolean canMoveRockTo (final int row, final int col) {
        // check positions within the map boundaries
        if (!isValidRow(row) || !isValidCol(col)) {
            return false;
        }
        return mine.getChar(row, col) == Mine.EMPTY;
    }

    private boolean isValidRow (final int row) {
        if (row < 1 || row > mine.getRows()) {
            return false;
        }
        return true;
    }

    private boolean isValidCol (final int col) {
        if (col < 1 || col > mine.getCols()) {
            return false;
        }
        return true;
    }

    private void moveRockAt (final int row, final int col, char direction) {
        if (direction == LEFT) {
            mine.moveRockLeft(row, col);
        } else if (direction == RIGHT) {
            mine.moveRockRight(row, col);
        }
    }

    public void updateMap () {
        final char[][] oldState = mine.getMapCopy();
        final int rows = mine.getRows();
        final int cols = mine.getCols();
        for (int row = 1; row <= rows; row++) {
            for (int col = 1; col <= cols; col++) {
                final char item = oldState[row - 1][col - 1];
                if (item == Mine.ROCK) {
                    checkFallingRock(row, col, oldState);
                } else if (item == Mine.CLOSED_LIFT && mine.lambdasRemaining() == 0) {
                    mine.openLift(row, col);
                }
            }
        }
    }

    private void checkFallingRock (int row, int col, char[][] oldState) {
        if (!isValidRow(row - 1)) {
            return;
        }
        final char item = oldState[row - 2][col - 1];

        // falling into empty space
        if (item == Mine.EMPTY) {
            mine.dropRockDown(row, col);
            checkLosingCondition(row - 1, col);
        }

        // falling into another rock - slide it to right side
        if (item == Mine.ROCK && isValidCol(col + 1)) {

            // check can we slide it to right side?
            if (oldState[row - 1][col] == Mine.EMPTY
                    && oldState[row - 2][col] == Mine.EMPTY) {
                mine.dropRockDownRight(row, col);
                checkLosingCondition(row - 1, col + 1);
            }
        }

        // falling into another rock - slide it to left side
        if (item == Mine.ROCK && isValidCol(col + 1) && isValidCol(col - 1)) {

            // 1st check we cannot slide it to right side
            if (oldState[row - 1][col] != Mine.EMPTY
                    || oldState[row - 2][col] != Mine.EMPTY) {
                // 2nd check can we slide it to the left side?
                if (oldState[row - 1][col - 2] == Mine.EMPTY
                        && oldState[row - 2][col - 2] == Mine.EMPTY) {
                    mine.dropRockDownLeft(row, col);
                    checkLosingCondition(row - 1, col - 1);
                }
            }
        }

        // falling into lambda - slide right only
        if (item == Mine.LAMBDA && isValidCol(col + 1)) {
            // check can we slide it to right side?
            if (oldState[row - 1][col] == Mine.EMPTY
                    && oldState[row - 2][col] == Mine.EMPTY) {
                mine.dropRockDownRight(row, col);
                checkLosingCondition(row - 1, col + 1);
            }
        }
    }

    private void checkLosingCondition(final int row, final int col) {
        if (isValidRow(row) && isValidRow(row) && isValidCol(col)) {
            if (mine.getChar(row - 1, col) == Mine.ROBOT) {
                status = DESTROYED;
            }
        }
    }
}
