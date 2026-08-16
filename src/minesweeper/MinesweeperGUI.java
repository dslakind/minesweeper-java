package minesweeper;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * Swing window for playing a standard Minesweeper game.
 * Left-clicking reveals squares, right-clicking toggles flags, and the new-game
 * button creates a fresh randomized board.
 */
public class MinesweeperGUI extends JFrame {
    // instance variables
    /** Game currently displayed by this window. */
    private Game currentGame;
    // User selected difficulty; default to BEGINNER
    private Difficulty selectedDifficulty;
    /** Generator used to create each new field. */
    private final MineFieldGenerator fieldGenerator;
    /** Container that arranges the square buttons. */
    private final JPanel boardPanel;
    /** Container for the boardPanel and Square buttons */
    private final JPanel boardContainer;
    /** Label displaying whether the game is active, won, or lost. */
    private final JLabel gameStatusLabel;
    private final JLabel remainingMineLabel;
    /** Buttons indexed by their corresponding field coordinates. */
    private JButton[][] squareButtons;
    private JComboBox<Difficulty> difficultyJComboBox;

    // constants
    private static final int SQUARE_SIZE = 40;
    private static final String MINE_SYMBOL = "\u2738"; //💣"\uD83D\uDCA3"
    private static final Color HIDDEN_BACKGROUND =
        new Color(190, 190, 190);

    private static final Color REVEALED_BACKGROUND =
        new Color(225, 225, 225);

    private static final Color FLAG_COLOR =
        new Color(180, 0, 0);

    private static final Color EXPLODED_MINE_BACKGROUND =
        new Color(220, 60, 60);

    private static final Color IN_PROGRESS_COLOR =
        new Color(70, 70, 70);

    private static final Color WON_COLOR =
        new Color(0, 130, 0);

    private static final Color LOST_COLOR =
        new Color(180, 0, 0);

    private static final Font NUMBER_FONT =
        new Font(Font.SANS_SERIF, Font.BOLD, 18);

    private static final Font BOMB_FONT =
        new Font("Segoe UI Emoji", Font.PLAIN, 20);

    /** Builds the window and starts the initial game. */
    public MinesweeperGUI() {
        
        fieldGenerator = new MineFieldGenerator();
        selectedDifficulty = Difficulty.BEGINNER;
        currentGame = new Game(fieldGenerator.generate(
            selectedDifficulty.getRows(), 
            selectedDifficulty.getColumns(), 
            selectedDifficulty.getMineCount())
        );

        setTitle("Minesweeper");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(5, 5));
        
        gameStatusLabel = new JLabel("Game in progress.");
        gameStatusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        remainingMineLabel = new JLabel("Mines remaining: " + currentGame.numRemainingMines());
        remainingMineLabel.setHorizontalAlignment(SwingConstants.LEFT);

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout()); 
        
        JPanel difficultyJPanel = new JPanel();
        difficultyJPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        JLabel difficultyLabel = new JLabel("Difficulty: ");
        difficultyJComboBox = new JComboBox<>(Difficulty.values());
        difficultyJComboBox.getSelectedItem();
        difficultyJPanel.add(difficultyLabel);
        difficultyJPanel.add(difficultyJComboBox);
        headerPanel.add(gameStatusLabel, BorderLayout.CENTER);
        headerPanel.add(remainingMineLabel, BorderLayout.WEST);
        headerPanel.add(difficultyJPanel, BorderLayout.EAST);
        
        gameStatusLabel.setFont(
            new Font(Font.SANS_SERIF, Font.BOLD, 16)
        );
        gameStatusLabel.setBorder(
            BorderFactory.createEmptyBorder(6, 6, 6, 6)
        );

        remainingMineLabel.setFont(
            new Font(Font.SANS_SERIF, Font.BOLD, 16)
        );
        remainingMineLabel.setBorder(
            BorderFactory.createEmptyBorder(6, 6, 6, 6)
        );

        boardContainer = new JPanel();
        boardContainer.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

        boardPanel = new JPanel();
        
        JButton newGameButton = new JButton("New Game");
        newGameButton.setFont(
            new Font(Font.SANS_SERIF, Font.BOLD, 14)
        );
        newGameButton.setMargin(new Insets(6, 12, 6, 12));
        newGameButton.setFocusPainted(false);
        newGameButton.addActionListener(
            event -> startNewGame()
        );        

        // Add an action listener to the difficulty combo box.
        difficultyJComboBox.addActionListener( e -> {
            Object item = difficultyJComboBox.getSelectedItem();            
            if (item != null) {
                selectedDifficulty = (Difficulty) item; 
            }
        });
 
        add(headerPanel, BorderLayout.NORTH);
        add(boardContainer, BorderLayout.CENTER);
        boardContainer.add(boardPanel);
        add(newGameButton, BorderLayout.SOUTH);


        buildBoard(); // add the field buttons to the window
        pack(); // size window to the contents
        setResizable(false);
        setLocationRelativeTo(null); // center on screen
    }

    /** Creates and arranges one button for every square in the current game. */
    private void buildBoard() {
        boardPanel.removeAll(); // Remove any existing controls from boardPanel.

        boardPanel.setLayout(
            new GridLayout(
                currentGame.numRows(), 
                currentGame.numCols(), 
                1, 
                1
            )
        ); // Give boardPanel a GridLayout.

        boardPanel.setPreferredSize(
            new Dimension(
                currentGame.numCols() * SQUARE_SIZE,
                currentGame.numRows() * SQUARE_SIZE
            )
        );

        // Create the JButton[][].
        squareButtons = new JButton[currentGame.numRows()][currentGame.numCols()];

        /*  Create one button per game square.
            Store each button in the array.
            Set button dimensions. 
            Add each button to boardPanel.
        */
        for (int r = 0; r < currentGame.numRows(); r++) {
            for (int c = 0; c < currentGame.numCols(); c++) {
                JButton squareButton = new JButton();
                squareButton.setMargin(new Insets(0, 0, 0, 0));
                squareButton.setFont(
                    new Font(Font.SANS_SERIF, Font.BOLD, 18)
                );
                squareButton.setFocusable(false);
                squareButton.setOpaque(true);
                squareButton.setContentAreaFilled(true);

                int buttonRow = r;
                int buttonCol = c;

                squareButton.addActionListener(
                    event -> handleLeftClick(buttonRow, buttonCol)
                );

                squareButton.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent event) {
                        if (SwingUtilities.isRightMouseButton(event)) {
                            handleRightClick(buttonRow, buttonCol);
                        }
                    }
                });

                squareButtons[r][c] = squareButton;
                boardPanel.add(squareButton);
            }
        }

        refreshView();
    }

    /** Updates all square buttons and status text from the game model. */
    private void refreshView() {
        // get the Square state and set the button text for each button
        for (int r = 0; r < currentGame.numRows(); r++) {
            for (int c = 0; c < currentGame.numCols(); c++) {
                Square square = currentGame.getSquare(r, c);
                SquareState squareState = square.getState();
                JButton button = squareButtons[r][c];
                button.setFont(NUMBER_FONT);
                button.setText("");
                button.setForeground(Color.BLACK);
                button.setBackground(HIDDEN_BACKGROUND);
                button.setBorderPainted(true);
                button.setEnabled(true);

                if (squareState == SquareState.FLAGGED) {
                    button.setText("⚑");
                    button.setForeground(FLAG_COLOR);
                    button.setBackground(HIDDEN_BACKGROUND);
                    button.setBorderPainted(true);
                    button.setEnabled(true);
                } else if (squareState == SquareState.HIDDEN) {
                    button.setText("");
                    button.setEnabled(true);
                } else if (
                    squareState == SquareState.REVEALED 
                    && square.hasMine()
                ) {
                    button.setFont(BOMB_FONT);
                    button.setText(MINE_SYMBOL);
                    button.setForeground(Color.BLACK);
                    button.setBackground(EXPLODED_MINE_BACKGROUND);
                    button.setBorderPainted(false);                    
                    // button.setEnabled(false);
                    button.setBorderPainted(false);
                } else if (
                    squareState == SquareState.REVEALED 
                    && square.getAdjacentMineCount() == 0
                ) {
                    button.setText("");
                    button.setBackground(REVEALED_BACKGROUND);
                    button.setBorderPainted(false);     
                    // button.setEnabled(false);
                    button.setBorderPainted(false);
                } else {
                    int count = square.getAdjacentMineCount();

                    button.setText(Integer.toString(count));
                    button.setForeground(getNumberColor(count));
                    button.setBackground(REVEALED_BACKGROUND);
                    button.setBorderPainted(false);                    
                    // button.setEnabled(false);
                    button.setBorderPainted(false);
                }
            }  
        }

        // If the game is over, disable every square button.
        if (currentGame.getStatus() != GameStatus.IN_PROGRESS) {
            gameOver();
        }            
        
        // After updating the buttons, update gameStatusLabel:
        updateGameStatusLabel();
        updateRemainingMineLabel();
    }

    /** Displays all mines and removes button borders when the game has ended. */
    private void gameOver() {
        GameStatus gameStatus = currentGame.getStatus() ;
        // disable every square button.
        if (gameStatus != GameStatus.IN_PROGRESS) {
            for (int r = 0; r < currentGame.numRows(); r++) {
                for (int c = 0; c < currentGame.numCols(); c++) {
                    // squareButtons[r][c].setEnabled(false);
                    squareButtons[r][c].setBorderPainted(false);

                    if (
                        gameStatus == GameStatus.LOST
                        && currentGame.getSquare(r, c).hasMine()
                    ) {
                        squareButtons[r][c].setText(MINE_SYMBOL);
                    }
                }
            }
        }
    }

    /**
     * Reveals a square in response to a primary-button click.
     *
     * @param row zero-based row of the clicked square
     * @param col zero-based column of the clicked square
     */
    private void handleLeftClick(int row, int col) {
        currentGame.revealSquare(row, col);
        refreshView();
    }
    
    /**
     * Toggles a flag in response to a secondary-button click.
     *
     * @param row zero-based row of the clicked square
     * @param col zero-based column of the clicked square
     */
    private void handleRightClick(int row, int col) {
        currentGame.toggleFlag(row, col);
        refreshView();
    }

    /** Generates a fresh minefield and rebuilds the board controls. */
    private void startNewGame() {
        currentGame = new Game(fieldGenerator.generate(
            selectedDifficulty.getRows(), 
            selectedDifficulty.getColumns(), 
            selectedDifficulty.getMineCount() 
        ));

        buildBoard();
        boardPanel.revalidate();
        boardPanel.repaint();

        pack();
    }

    /**
     * Selects the conventional display color for a neighboring-mine count.
     *
     * @param count adjacent mine count
     * @return color used to draw the number
     */
    private Color getNumberColor(int count) {
        switch (count) {
            case 1:
                return new Color(0, 0, 200);
            case 2:
                return new Color(0, 125, 0);
            case 3:
                return new Color(200, 0, 0);
            case 4:
                return new Color(0, 0, 110);
            case 5:
                return new Color(125, 0, 0);
            case 6:
                return new Color(0, 125, 125);
            case 7:
                return Color.BLACK;
            case 8:
                return Color.DARK_GRAY;
            default:
                return Color.BLACK;
        }
    }    

    /** Updates the status label's message and color for the current game state. */
    private void updateGameStatusLabel() {
        GameStatus gameStatus = currentGame.getStatus();

        if (gameStatus == GameStatus.IN_PROGRESS) {
            gameStatusLabel.setText("Game in progress.");
            gameStatusLabel.setForeground(IN_PROGRESS_COLOR);
        } else if (gameStatus == GameStatus.WON) {
            gameStatusLabel.setText("You won!");
            gameStatusLabel.setForeground(WON_COLOR);
        } else {
            gameStatusLabel.setText("Game over.");
            gameStatusLabel.setForeground(LOST_COLOR);
        }
    }

    private void updateRemainingMineLabel() {
        remainingMineLabel.setText("Mines remaining: " + currentGame.numRemainingMines());
    }
}
