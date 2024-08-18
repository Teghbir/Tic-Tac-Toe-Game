import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.applet.Applet;
import java.applet.AudioClip;
import java.net.URL;

/**
 * A class modelling a tic-tac-toe (noughts and crosses, Xs and Os) game.
 * Credits for the sound 'violin.wav' go to https://pixabay.com/sound-effects/search/win/
 * Credits for GUI logic go to Sysc 2004 Lab 11- counter-v3-upd
 * 
 * @author Lynn Marshall
 * @version November 8, 2012
 * 
 * @author Teghbir Chadha (101258511)
 * @version April 9, 2024
 */

public class TicTacToe implements ActionListener {
    
    //Constants for player symbols and game states
    private final String PLAYER_X = "X"; // player using "X"
    private final String PLAYER_O = "O"; // player using "O"
    private final String EMPTY = " "; // empty cell
    private final String TIE = "T"; // game ended in a tie

    //Colors for player symbols
    private final Color X_COLOR = Color.BLUE;
    private final Color O_COLOR = Color.RED;
    
    //Variables for player wins
    private String player; // current player (PLAYER_X or PLAYER_O)
    private String winner; // winner: PLAYER_X, PLAYER_O, TIE, EMPTY = in progress
    private int numFreeSquares; // number of squares still free
    private JButton[][] board; // 3x3 array representing the board
    private int xWins = 0; 
    private int oWins = 0;
    
    //GUI components
    private JLabel status;
    private JLabel game;
    private JLabel stats;
    private JPanel statusPanel;
    
    //Menu items
    private JMenuItem newItem;
    private JMenuItem quitItem;
    private JMenuItem gameRules;
    
    //Audio clip for winning sound
    AudioClip click;
    
    /** 
    * Constructs a new Tic-Tac-Toe board.
    */
    public TicTacToe() {
        //Create a new JFrame for the game
        JFrame frame = new JFrame("Tic Tac Toe");
        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout());
        
        
        //Set background color
        contentPane.setBackground(new Color(255, 182, 193));
        
        // Create a menu bar
        JMenuBar menubar = new JMenuBar();
        frame.setJMenuBar(menubar); // add menu bar to our frame
        
        // Create a file menu
        JMenu fileMenu = new JMenu("Options");
        fileMenu.setFont(new Font("Allura", Font.PLAIN, 15));
        menubar.add(fileMenu); // add to our menu bar
        
        //Create menu items
        newItem = new JMenuItem("New"); // create a menu item called "Reset"
        fileMenu.add(newItem); // and add to our menu

        quitItem = new JMenuItem("Quit"); // create a menu item called "Quit"
        fileMenu.add(quitItem); // and add to our menu
        
        gameRules = new JMenuItem("Rules"); // create a menu item called "Rules"
        fileMenu.add(gameRules); // and add to our menu
        
        // Add shortcuts for menu items
        final int SHORTCUT_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(); 
        newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, SHORTCUT_MASK));
        quitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, SHORTCUT_MASK));
        gameRules.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, SHORTCUT_MASK));
        
        // Add action listeners for menu items
        newItem.addActionListener(this); 
        quitItem.addActionListener(new ActionListener() // create an anonymous inner class
        { // start of anonymous subclass of ActionListener
          // this allows us to put the code for this action here  
            public void actionPerformed(ActionEvent event)
            {
                System.exit(0); // quit
            }
        } // end of anonymous subclass
        ); // end of addActionListener parameter list and statement
        
        gameRules.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String rules = "There are two players, X and O. Players alternate choosing" + 
                " a tile from the 3x3 grid, and the first player to get 3 tiles," + 
                " horizontally, vertically, or diagonally wins. If neither player achieves" +
                " this before all tiles are filled, the game ends in a tie. Enjoy!";
                //Opens a dialog box for the rules of the game
                JOptionPane.showMessageDialog(frame, rules, "Rules", JOptionPane.PLAIN_MESSAGE);
            }
        });
        
        // Create and configure status labels
        status = new JLabel();
        status.setHorizontalAlignment(JLabel.CENTER);
        status.setFont(new Font("Allura", Font.PLAIN, 25));
        
        stats = new JLabel();
        stats.setHorizontalAlignment(JLabel.CENTER);
        stats.setFont(new Font("Allura", Font.PLAIN, 25));
        
        //Create a JPanel and add the game stats JLabels to it
        statusPanel = new JPanel(new GridLayout(2, 1)); // 2 rows, 1 column
        statusPanel.add(status);
        statusPanel.add(stats);
        contentPane.add(statusPanel, BorderLayout.SOUTH);
        
        // Create and configure game title label
        game = new JLabel("Tic Tac Toe Inator!");
        game.setFont(new Font("Allura", Font.BOLD, 20));
        game.setHorizontalAlignment(JLabel.CENTER);
        contentPane.add(game,BorderLayout.NORTH);
        
        //Create and configure button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 3));

        // Create the game board
        board = new JButton[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = new JButton();
                buttonPanel.add(board[i][j]);
                board[i][j].setFont(new Font("Nunito", Font.BOLD, 40));
                board[i][j].setFocusable(false);
                board[i][j].addActionListener(this);
            }
        }
        
        // Add button panel to content pane
        contentPane.add(buttonPanel, BorderLayout.CENTER);
        
        // Initialize game state
        clearBoard();
        playGame(-1,-1);
        
        // Configure frame and make it visible
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); 
        frame.pack(); // pack everthing into our frame
        frame.setResizable(true); // we can resize it
        frame.setVisible(true); // it's visible
    }
    
    
    /**
    * Sets everything up for a new game.  Marks all squares in the Tic Tac Toe board as 
    * empty and indicates no winner yet, 9 free squares and the current player is player X.
    */
    private void clearBoard() {
        //clears all the buttons
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j].setText(EMPTY);
            }
        }
        //Resets the game state variables
        winner = EMPTY;
        numFreeSquares = 9;
        player = PLAYER_X; // Player X always has the first turn.
        statusPanel.setBackground(new Color(255, 182, 193));
    }

    /**
     * Updates the GUI to reflect the current game state and checks for a winner or tie.
     */
    private void playGame(int row, int col) {
        //Checks for a winner
        if (haveWinner(row, col)) {
            winner = player;
            statusPanel.setBackground(new Color(0, 255, 0));
            
            //Plays winning sound
            URL urlClick = TicTacToe.class.getResource("violin.wav"); 
            click = Applet.newAudioClip(urlClick);
            click.play(); // just plays clip once
            
            // Update win count
            if (winner.equals(PLAYER_X)) {
                xWins++;
            } else if (winner.equals(PLAYER_O)) {
                oWins++;
            }
            // Update status label
            status.setText((winner.equals(TIE)) ? "It's a tie!" : winner + " wins!");
        } else if (numFreeSquares == 0) {
            winner = TIE;
            status.setText("It's a tie!");
        } else {
            // Update status label with current player's turn
            status.setText("It's " + player + "'s turn");
        }
        // Update stats label with current win count
        stats.setText("X Wins: " + xWins + ", O Wins: " + oWins);
    }
    

   /**
    * Returns true if filling the given square gives us a winner, and false
    * otherwise.
    *
    * @param int row of square just set
    * @param int col of square just set
    * 
    * @return true if we have a winner, false otherwise
    */
    private boolean haveWinner(int row, int col) {
        if (numFreeSquares > 4) return false;

        if (board[row][0].getText().equals(board[row][1].getText()) &&
            board[row][0].getText().equals(board[row][2].getText())) {
            return true; // Check row "row"
        }
    
        if (board[0][col].getText().equals(board[1][col].getText()) &&
            board[0][col].getText().equals(board[2][col].getText())) {
            return true; // Check column "col"
        }
    
        if (row == col) {
            if (board[0][0].getText().equals(board[1][1].getText()) &&
                board[0][0].getText().equals(board[2][2].getText())) {
                return true; // Check diagonal 1
            }
        }
    
        if (row == 2 - col) {
            if (board[0][2].getText().equals(board[1][1].getText()) &&
                board[0][2].getText().equals(board[2][0].getText())) {
                return true; // Check diagonal 2
        }
        }

        return false; // No winner yet
    }

    /**
    * Handles the actions performed when a menu item or a board button is clicked.
    *
    * @param e the ActionEvent representing the action performed
    */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o instanceof JMenuItem) {
            JMenuItem item = (JMenuItem) o;
            if (item == newItem) { // Reset the game
                clearBoard();
                playGame(-1,-1);
            }
        } else if (o instanceof JButton) {
            JButton button = (JButton) o;
            // Find which button was clicked and update the game state accordingly
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (button == board[i][j] && board[i][j].getText().equals(EMPTY) && winner.equals(EMPTY)) {
                        board[i][j].setText(player);
                        board[i][j].setForeground(player.equals(PLAYER_X) ? X_COLOR : O_COLOR); // Set color based on player
                        numFreeSquares--;
                        playGame(i, j);
                        player = (player.equals(PLAYER_X)) ? PLAYER_O : PLAYER_X;
                    }
                }
            }
        }
    }

}
