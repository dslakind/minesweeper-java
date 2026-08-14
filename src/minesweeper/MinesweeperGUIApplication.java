package minesweeper;

import javax.swing.SwingUtilities;

public class MinesweeperGUIApplication {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MinesweeperGUI gui = new MinesweeperGUI();
            gui.setVisible(true);
        });        
    }    
}
