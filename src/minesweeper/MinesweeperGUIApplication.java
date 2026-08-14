package minesweeper;

import javax.swing.SwingUtilities;

/** Entry point that launches the Swing Minesweeper interface. */
public class MinesweeperGUIApplication {
    /** Creates a GUI application launcher. */
    public MinesweeperGUIApplication() {
    }

    /**
     * Creates and displays the interface on Swing's event-dispatch thread.
     *
     * @param args command-line arguments; currently ignored
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MinesweeperGUI gui = new MinesweeperGUI();
            gui.setVisible(true);
        });        
    }    
}
