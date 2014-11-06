/*
 * Author: Scotty Ward, sward2011@my.fit.edu
 * Author: Said Al Batrani, salbatrani2014@my.fit.edu
 * Course: CSE 4051, Fall 2014
 * Project: proj08, Lamda Lifting
 */

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public final class MineGraphics extends JFrame {

    private static final Map<Character, ImageIcon> ICONS =
            new HashMap<Character, ImageIcon>();
    static {
        final String folder = "src";
        ICONS.put(Character.valueOf(Mine.BRICK),
                new ImageIcon(String.format("%s/wall.png", folder)));
        ICONS.put(Character.valueOf(Mine.CLOSED_LIFT),
                new ImageIcon(String.format("%s/lift.png", folder)));
        ICONS.put(Character.valueOf(Mine.EARTH),
                new ImageIcon(String.format("%s/earth.png", folder)));
        ICONS.put(Character.valueOf(Mine.EMPTY),
                new ImageIcon(String.format("%s/empty.png", folder)));
        ICONS.put(Character.valueOf(Mine.LAMBDA),
                new ImageIcon(String.format("%s/lambda.png", folder)));
        ICONS.put(Character.valueOf(Mine.OPEN_LIFT),
                new ImageIcon(String.format("%s/open.png", folder)));
        ICONS.put(Character.valueOf(Mine.ROBOT),
                new ImageIcon(String.format("%s/miner.png", folder)));
        ICONS.put(Character.valueOf(Mine.ROCK),
                new ImageIcon(String.format("%s/rock.png", folder)));
    };

    private final MineEngine engine;
    private final Class<?> agent;
    private final JPanel board;
    private final JLabel status;
    private final JLabel moves;

    public MineGraphics (final MineEngine aEngine, final Class<?> aAgent) {
        engine = aEngine;
        agent = aAgent;

        // setup the mine map
        final Mine mine = engine.getMine();
        final int rows = mine.getRows();
        final int cols = mine.getCols();
        board = new JPanel(new GridLayout(rows, cols));
        for (int row = rows; row >= 1; row--) {
            for (int col = 1; col <= cols; col++) {
                board.add(new JLabel(ICONS.get(Character.valueOf(mine.getChar(row, col)))));
            }
        }
        add(board, BorderLayout.CENTER);

        // setup the status bar
        status = new JLabel();
        moves = new JLabel();
        final JPanel south = new JPanel();
        south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));
        south.add(status);
        south.add(moves);
        updateStatusBar();
        add(south, BorderLayout.SOUTH);

        // add keyboard listener if no agent supplied
        if (agent == null) {
            class KL extends KeyAdapter {
                @Override
                public void keyReleased (final KeyEvent e) {
                    final char move;
                    switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_L:
                        move = MineEngine.LEFT;
                        break;
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_R:
                        move = MineEngine.RIGHT;
                        break;
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_U:
                        move = MineEngine.UP;
                        break;
                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_D:
                        move = MineEngine.DOWN;
                        break;
                    case KeyEvent.VK_A:
                        move = MineEngine.ABORT;
                        break;
                    default:
                        move = MineEngine.WAIT;
                    }
                    processMove(move);
                }
            }
            addKeyListener(new KL());
        }

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Lambda Lift");
        pack();
        setVisible(true);
    }

    private void updateStatusBar () {
        //
        final Mine mine = engine.getMine();
        status.setText(String.format("%s, score: %d, lambdas: %d of %d",
                engine.getStatus(), engine.getScore(), mine.getCollectedLambdas(),
                mine.getLambdas()));

        //
        final StringBuilder sb = new StringBuilder();
        final List<Character> movesList = engine.getMoves();
        sb.append(String.format("moves (%d): ", movesList.size()));
        for (final Character move : movesList) {
            sb.append(move);
        }
        moves.setText(sb.toString());
    }

    private void processMove (final char move) {
        if (!engine.isGameOver()) {
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
            updateBoard();
            updateStatusBar();
        }
    }

    private void updateBoard () {
        final Mine mine = engine.getMine();
        final int rows = mine.getRows();
        final int cols = mine.getCols();
        int index = 0;
        for (int row = rows; row >= 1; row--) {
            for (int col = 1; col <= cols; col++) {
                final JLabel label = (JLabel) board.getComponent(index);
                label.setIcon(ICONS.get(Character.valueOf(mine.getChar(row, col))));
                index++;
            }
        }
    }
}
