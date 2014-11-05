import java.util.Random;

public class RandomWalk {

    final static Random rng = new Random(Long.getLong("seed", System.nanoTime()));

    /*
     * Identify this robot miner.
     */
    public static String name () {
        return "Random Walk by Ryan Stansifer";
    }

    /*
     * Determine the next move based on the current situation in the mine.
     */
    public static char next (final MineInterface mine) {
        final int rows = mine.getRows(), cols = mine.getCols();
        final int row = mine.getMinerRow(), col = mine.getMinerCol();

        // At the position (row,col) is the robot miner
        assert mine.getChar(row, col) == 'R';

        // All maps have borders
        assert 1 < row && row < rows : String.format("1<%d<%d", row, rows);
        assert 1 < col && col < cols : String.format("1<%d<%d", col, cols);

        // Assemble potential moves
        final StringBuilder sb = new StringBuilder();
        if (mine.getChar(row, col + 1) != '#')
            sb.append('R');
        if (mine.getChar(row, col - 1) != '#')
            sb.append('L');
        if (mine.getChar(row - 1, col) != '#')
            sb.append('D');
        if (mine.getChar(row + 1, col) != '#')
            sb.append('U');

        // Choose randomly from among them
        final int n = sb.length();
        final int rc = rng.nextInt(n);
        return sb.charAt(rc);
    }
}
