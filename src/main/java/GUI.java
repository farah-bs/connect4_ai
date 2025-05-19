import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GUI {
    public static void main(String[] args) {
        // Look & Feel moderne
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("🎮 Connect 4");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        final GameState state = new GameState();
        final BoardDrawing board = new BoardDrawing(state);

        // Barre de boutons pour les colonnes
        JPanel columnButtons = new JPanel();
        columnButtons.setLayout(new GridLayout(1, 7, 10, 0));

        for (int i = 1; i <= 7; i++) {
            final int column = i;
            JButton button = new JButton("Col " + column);
            button.setFocusPainted(false);
            button.addActionListener(e -> {
                boolean success = state.move(column);
                if (!success) {
                    JOptionPane.showMessageDialog(frame,
                            "La colonne " + column + " est pleine !",
                            "Erreur de coup",
                            JOptionPane.WARNING_MESSAGE);
                }
                board.repaint();
            });
            columnButtons.add(button);
        }

        // Boutons Undo et Restart
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton buttonUndo = new JButton("↩️ Annuler");
        buttonUndo.addActionListener(e -> {
            boolean success = state.undo();
            if (!success) {
                JOptionPane.showMessageDialog(frame,
                        "Aucun coup à annuler !",
                        "Annulation impossible",
                        JOptionPane.INFORMATION_MESSAGE);
            }
            board.repaint();
        });

        JButton buttonRestart = new JButton("🔄 Recommencer");
        buttonRestart.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(frame,
                    "Voulez-vous vraiment recommencer la partie ?",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                state.restart();
                board.repaint();
            }
        });

        controlPanel.add(buttonUndo);
        controlPanel.add(buttonRestart);

        // Placement des composants
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.add(Box.createVerticalStrut(10));
        bottomPanel.add(controlPanel);

        frame.add(board, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null); // centre la fenêtre
        frame.setVisible(true);
    }
}
