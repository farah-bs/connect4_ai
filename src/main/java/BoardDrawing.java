import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import javax.swing.*;
import java.awt.event.*;

public class BoardDrawing extends JComponent {
    private GameState state;
    private Rectangle board;
    private Rectangle textBack;
    private String stateMessage;
    private ArrayList<Ellipse2D.Double> holes;
    final int BOARD_START_X = 182;
    final int BOARD_START_Y = 75;
    final int BOARD_WIDTH = 386;
    final int BOARD_HEIGHT = 340;
    final int HOLE_DIAMETER = 36;
    final int HOLE_DISTANCE = 50;
    final int HOLE_OFFSET = 25;
    final int HOLE_START_X = BOARD_START_X + HOLE_OFFSET;
    final int HOLE_START_Y = BOARD_START_Y + BOARD_HEIGHT - HOLE_OFFSET - HOLE_DIAMETER;

    public BoardDrawing(GameState gs) {
        this.state = gs;

        // Initialisation graphique
        board = new Rectangle(BOARD_START_X, BOARD_START_Y, BOARD_WIDTH, BOARD_HEIGHT);
        textBack = new Rectangle(BOARD_START_X + (BOARD_WIDTH / 2) - 100, BOARD_START_Y - 50, 200, 40);
        holes = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 6; j++) {
                Ellipse2D.Double hole = new Ellipse2D.Double(HOLE_START_X + i * HOLE_DISTANCE,
                        HOLE_START_Y - j * HOLE_DISTANCE, HOLE_DIAMETER, HOLE_DIAMETER);
                holes.add(hole);
            }
        }

        // ➕ Nouveau : rendre le plateau cliquable
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int mouseX = e.getX();

                if (mouseX < HOLE_START_X || mouseX > HOLE_START_X + 6 * HOLE_DISTANCE + HOLE_DIAMETER) {
                    return; // en dehors de la grille
                }

                int col = (mouseX - HOLE_START_X) / HOLE_DISTANCE;

                boolean success = state.move(col + 1); // move prend 1 à 7
                if (!success) {
                    JOptionPane.showMessageDialog(BoardDrawing.this,
                            "❌ La colonne " + (col + 1) + " est pleine !",
                            "Coup invalide",
                            JOptionPane.WARNING_MESSAGE);
                }

                repaint();
            }
        });
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setFont(new Font("TimesRoman", Font.BOLD, 20));

        // Plateau
        g2.setColor(Color.blue);
        g2.fill(board);

        // Trous
        g2.setColor(Color.white);
        for (Ellipse2D.Double hole : holes) g2.fill(hole);

        // Numéros de colonnes
        g2.setColor(Color.black);
        for (int i = 0; i < 7; i++) {
            int w = g2.getFontMetrics().stringWidth(Integer.toString(i + 1));
            g2.drawString(Integer.toString(i + 1),
                    HOLE_START_X + HOLE_DIAMETER / 2 - w / 2 + i * HOLE_DISTANCE,
                    BOARD_START_Y + BOARD_HEIGHT + 25);
        }

        // Messages d’état
        if (state.getRedWins()) {
            g2.setColor(Color.red);
            g2.fill(textBack);
            g.setColor(Color.black);
            stateMessage = "RED WINS!";
        } else if (state.getYellowWins()) {
            g2.setColor(Color.yellow);
            g2.fill(textBack);
            g.setColor(Color.black);
            stateMessage = "YELLOW WINS!";
        } else if (state.getGameOver()) {
            g2.setColor(Color.black);
            g2.fill(textBack);
            g.setColor(Color.white);
            stateMessage = "IT'S A TIE!";
        } else if (state.getRedsTurn()) {
            g2.setColor(Color.lightGray);
            g2.fill(textBack);
            g.setColor(Color.red);
            stateMessage = "Red's Turn";
        } else {
            g2.setColor(Color.lightGray);
            g2.fill(textBack);
            g.setColor(Color.yellow);
            stateMessage = "Yellow's Turn";
        }

        int stringWidth = g2.getFontMetrics().stringWidth(stateMessage);
        int strX = BOARD_START_X + (BOARD_WIDTH - stringWidth) / 2;
        g2.drawString(stateMessage, strX, BOARD_START_Y - 22);

        // Message d'erreur (texte interne)
        if (state.getError() != null) {
            g2.setColor(Color.black);
            g2.drawString("Oops!", 15, 220);
            g2.drawString(state.getError(), 15, 250);
        }

        // Pions
        for (int i = 0; i < state.getPieces().length; i++) {
            for (int j = 0; j < state.getPieces()[0].length; j++) {
                if (state.getPieces()[i][j] == null) continue;
                else if (state.getPieces()[i][j]) g2.setColor(Color.red);
                else g2.setColor(Color.yellow);
                g2.fill(new Ellipse2D.Double(
                        HOLE_START_X + 2 + i * HOLE_DISTANCE,
                        HOLE_START_Y + 2 - j * HOLE_DISTANCE,
                        HOLE_DIAMETER - 4,
                        HOLE_DIAMETER - 4));
            }
        }
    }
}