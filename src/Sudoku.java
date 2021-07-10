import javax.sound.midi.VoiceStatus;
import javax.swing.*;
import java.awt.*; // Uses AWT's Layout Managers
import java.awt.event.*; // Uses AWT's Event Handlers
import java.util.Random;
import javax.swing.*; // Uses Swing's Container/Components

public class Sudoku extends JFrame {
    // Name-constants for the game properties
    public static final int GRID_SIZE = 9; // Size of the board
    public static final int SUBGRID_SIZE = 3; // Size of the sub-grid

    // Name-constants for UI control (sizes, colors and fonts)
    public static final int CELL_SIZE = 60; // Cell width/height in pixels
    public static final int CANVAS_WIDTH = CELL_SIZE * GRID_SIZE;
    public static final int CANVAS_HEIGHT = CELL_SIZE * GRID_SIZE;

    // Board width/height in pixels
    public static final Color OPEN_CELL_BGCOLOR = new Color(165, 165, 165);
    public static final Color OPEN_CELL_TEXT_YES = new Color(0, 150, 100); // RGB
    public static final Color OPEN_CELL_TEXT_NO = new Color(255, 0, 50);
    public static final Color CLOSED_CELL_BGCOLOR1 = new Color(255, 255, 0);
    public static final Color CLOSED_CELL_BGCOLOR2 = new Color(0, 155, 255);
    public static final Color CLOSED_CELL_TEXT = Color.BLACK;
    public static final Font FONT_NUMBERS = new Font("SansSerif", Font.BOLD, 20);

    // Set difficulty of game
    public static final int EASY = 1;
    public static final int MEDIUM = 2;
    public static final int HARD = 3;


    // Count cells remaining
    public static int count = 0;

    //Time variables
    private static final String INITIAL_LABEL_TIME = "00:00:00";
    public long timestart, timeend, pausestart,totaltimestart;
    public long pauseCount = 0;
    public long timeused;
    public boolean stopped = true;
    public boolean paused = false;


    // Components
    public static JLabel cellsRemainLabel = new JLabel("Cells remaining");
    public static JTextField cellsRemainTF = new JTextField(count + "", 15);


    //Component to show time used

    public JLabel showtime = new JLabel(INITIAL_LABEL_TIME);
    public static JLabel timerstartLabel = new JLabel("Time used:");
    public JTextField timerstartLabelTF = new JTextField(timeused+ "");

    // The game board composes of 9x9 JTextFields,
    // each containing String "1" to "9", or empty String
    private JTextField[][] tfCells = new JTextField[GRID_SIZE][GRID_SIZE];

    // Puzzle to be solved and the mask (which can be used to control the
    // difficulty level).
    // Hardcoded here. Extra credit for automatic puzzle generation
    // with various difficulty levels.
    // For testing, open only 2 cells.
    private int[][] puzzle;
    private boolean[][] masks = new boolean[GRID_SIZE][GRID_SIZE];
    {
        for (int row = 0; row < GRID_SIZE; ++row) {
            for (int col = 0; col < GRID_SIZE; ++col) {
                masks[row][col] = false; // initialise to show all numbers
            }
        }
    }

    public static boolean[][] temp = new boolean[GRID_SIZE][GRID_SIZE];

    /**
     * Constructor to setup the game and the UI Components
     */

    public Sudoku() {
        // SoundEffect.init();
        // SoundEffect.volume = SoundEffect.Volume.HIGH;
        // SoundEffect.sound.playbackground();
        // Construct listeners
        InputListener listener = new InputListener();
        BtnListener btnListener = new BtnListener();

        // Add the sudoku game into a panel
        JPanel mainGame = new JPanel();
        mainGame.setLayout(new GridLayout(GRID_SIZE, GRID_SIZE)); // 9x9 GridLayout

        //Add timer into a panel
        JPanel timerpane = new JPanel();
        timerpane.setLayout(new GridLayout(10,0,3,3));

        // Add status bar into a panel
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new FlowLayout());

        // Label for cells remaining
        statusPanel.add(cellsRemainLabel);

        // Text field to show cells remaining
        statusPanel.add(cellsRemainTF);
        cellsRemainTF.setEditable(false);

        // time pause Button
        JButton timepauseButton = new JButton("Pause");
        timerpane.add(timepauseButton);
        timepauseButton.addActionListener(btnListener);

        // time resume Button
        JButton timeresumeButton = new JButton("Resume");
        timerpane.add(timeresumeButton);
        timeresumeButton.addActionListener(btnListener);

        // time label
        timerpane.add(showtime);

        // Sound buttons
        JButton soundOnButton = new JButton("Sound On");
        soundOnButton.addActionListener(btnListener);
        JButton muteButton = new JButton("Mute");
        muteButton.addActionListener(btnListener);

        // Hint button
        JButton hintButton = new JButton("Hint");
        statusPanel.add(hintButton);
        hintButton.addActionListener(btnListener);

        // Reset Button
        JButton resetButton = new JButton("Reset");
        statusPanel.add(resetButton);
        resetButton.addActionListener(btnListener);

        // Finish button
        JButton finishButton = new JButton("Finish");
        statusPanel.add(finishButton);
        finishButton.addActionListener(btnListener);

        // Menu bar
        JMenuBar menuBar = new JMenuBar();

        // Menu
        JMenu menu = new JMenu("File");
        // menu.setMnemonic(KeyEvent.VK_A);

        // a group of JMenuItems
        JMenuItem newGame = new JMenuItem("New Game", KeyEvent.VK_A);
        menu.add(newGame);
        newGame.addActionListener(evt -> {
            resetDifficulty();
            startTimer();
            countRemain();
        });

        JMenuItem exit = new JMenuItem("Exit", KeyEvent.VK_A);
        menu.add(exit);
        exit.addActionListener(evt -> {
            System.exit(0);
        });

        // Pop-Window for 'Help' .
        JMenuItem displayhelp = new JMenuItem("Help", KeyEvent.VK_A);
        menu.add(displayhelp);
        displayhelp.addActionListener(evt -> {

            JOptionPane.showMessageDialog(null,
                    "Instructions:\n Sudoku is a single-player mind game. \n The objective is to fill a 9×9 grid with digits 1 to 9, so that each column, each row, and each of the nine 3×3 sub-grids contains all of the digits from 1 to 9. \n The puzzle setter provides a partially completed grid, which for a well-posed puzzle has a unique solution.\n\n Key in your input and press\"Enter Key\"\n Green:Correct.Red:Wrong");

        });

        //// Using SWing API for layout
        menuBar.add(menu);
        menuBar.add(soundOnButton);
        menuBar.add(muteButton);
        menuBar.add(timepauseButton);
        menuBar.add(timeresumeButton);
        menuBar.add(showtime);
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(mainGame, BorderLayout.CENTER);
        cp.add(statusPanel, BorderLayout.SOUTH);
        cp.add(menuBar, BorderLayout.NORTH);

        // Ask for difficulty, generate puzzle, change masks accordingly, set all
        setDifficulty();
        //add textfields based on puzzle generated
        for (int row = 0; row < GRID_SIZE; ++row) {
            for (int col = 0; col < GRID_SIZE; ++col) {
                tfCells[row][col] = new JTextField(); // Allocate element of array
                mainGame.add(tfCells[row][col]); // ContentPane adds JTextField, the numbers
            }
        }
        //resetPuzzle base on puzzle generated
        resetPuzzle();
        startTimer();
        // Set the size of the content-pane and pack all the components under container
        cp.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Handle window closing
        setTitle("Sudoku");
        setVisible(true);
        countRemain();
    }

    /** The entry main() entry method */
    public static void main(String[] args) {
        // [TODO 1] (Now)
        // Check Swing program template on how to run the constructor
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Sudoku();
            }
        });
    }

    // [TODO 2]
    // Inner class to be used as ActionEvent listener for ALL JTextFields
    private class InputListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // All the 9*9 JTextFileds invoke this handler. We need to determine
            // which JTextField (which row and column) is the source for this invocation.
            int rowSelected = -1;
            int colSelected = -1;
            // Get the source object that fired the event
            JTextField source = (JTextField) e.getSource();
            // Scan JTextFileds for all rows and columns, and match with the source object
            boolean found = false;
            for (int row = 0; row < GRID_SIZE && !found; ++row) {
                for (int col = 0; col < GRID_SIZE && !found; ++col) {
                    if (tfCells[row][col] == source) {
                        rowSelected = row;
                        colSelected = col;
                        found = true; // break the inner/outer loops
                    }
                }
            }

            /*
             * [TODO 5] 1. Get the input String via
             * tfCells[rowSelected][colSelected].getText() 2. Convert the String to int via
             * Integer.parseInt(). 3. Assume that the solution is unique. Compare the input
             * number with the number in the puzzle[rowSelected][colSelected]. If they are
             * the same, set the background to green (Color.GREEN); otherwise, set to red
             * (Color.RED).
             */
            if (Integer.parseInt(tfCells[rowSelected][colSelected].getText()) == puzzle[rowSelected][colSelected]) {
                setRight(rowSelected, colSelected);
                ImageIcon image1 = new ImageIcon ("src/images/Yayy.png");
                JOptionPane.showMessageDialog(null, "Right Answer", "Yayy", JOptionPane.PLAIN_MESSAGE, image1);
            } else {
                setWrong(rowSelected, colSelected);
                ImageIcon image2 = new ImageIcon ("src/images/Booo.png");
                JOptionPane.showMessageDialog(null, "Wrong Answer", "Booo", JOptionPane.PLAIN_MESSAGE, image2);
            }

            /*
             * [TODO 6] Check if the player has solved the puzzle after this move. You could
             * update the masks[][] on correct guess, and check the masks[][] if any input
             * cell pending.
             */
            countRemain();
            checkComplete();
        }
    }

    private class BtnListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            String btnLabel = evt.getActionCommand();

            if (btnLabel.equals("Hint")) { // if click button hint
                // Find the first mask that is still true(not revealed)

                int rowSelected = -1;
                int colSelected = -1;
                boolean found = false;
                for (int row = 0; row < GRID_SIZE && !found; ++row) {
                    for (int col = 0; col < GRID_SIZE && !found; ++col) {
                        if (masks[row][col]) {
                            rowSelected = row;
                            colSelected = col;
                            found = true; // break the inner/outer loops
                        }
                    }
                }
                setTextDefault();

                if (checkComplete())
                    return;

                int hint = puzzle[rowSelected][colSelected];
                tfCells[rowSelected][colSelected].setText(hint + "");
                tfCells[rowSelected][colSelected].setBackground(Color.green);
                masks[rowSelected][colSelected] = false; //black text = computer generated
                count--;
                cellsRemainTF.setText(count + "");
            }

            else if (btnLabel.equals("Finish")) { // if click button finish
                for (int row = 0; row < GRID_SIZE; ++row) { // loop to fill in all unmasked numbers
                    for (int col = 0; col < GRID_SIZE; ++col) {
                        if (masks[row][col]) {
                            int finish = puzzle[row][col];
                            tfCells[row][col].setText(finish + "");
                            tfCells[row][col].setBackground(Color.green);
                            masks[row][col] = false;
                            setTextDefault();
                            countRemain();
                        }
                    }
                }
                endOfPuzzle();

            } else if (btnLabel.equals("Reset")) {
                resetGame();
            }

            else if(btnLabel.equals("Pause")) {
                paused = true;
                pausestart();
                Time2(2);

            }else if(btnLabel.equals("Resume")) {
                paused = false;
                timestart();
                Time2(2);

            }
            if (btnLabel.equals("Mute")) {
                // SoundEffect.sound.mute();
                // SoundEffect.volume = SoundEffect.Volume.MUTE;

            }
            if (btnLabel.equals("Sound On")) {
                // SoundEffect.volume = SoundEffect.Volume.HIGH;
                // SoundEffect.sound.playbackground();

            }

        }
    }

    // Methods used above

    public void endOfPuzzle() {

        // SoundEffect.sound.playYay();

        ImageIcon image3 = new ImageIcon ("src/images/Finish.png");
        JOptionPane.showMessageDialog(null, "All done! You've completed the puzzle!", "Yayy", JOptionPane.PLAIN_MESSAGE, image3);
        endTimer();

        int dialogResult = JOptionPane.showConfirmDialog(null, "Do you want to restart the game?",
                "Do you want to restart the game?", JOptionPane.YES_NO_OPTION);
        if (dialogResult == JOptionPane.YES_OPTION) { // if select yes
            resetDifficulty();
            startTimer();
            countRemain();
        }
    }

    public boolean checkComplete() {
        boolean complete = true;
        for (int row = 0; row < GRID_SIZE; ++row) {
            for (int col = 0; col < GRID_SIZE; ++col) {
                if (masks[row][col] ) { // if contain true, means not complete
                    complete = false; // since not complete, set complete to false
                    break;
                }
            }
        }
        if (complete) {
            endOfPuzzle();
        }
        return complete;
    }

    public void countRemain() {
        count = 0;
        for (int row = 0; row < GRID_SIZE; ++row) { // loop to count all unmasked numbers
            for (int col = 0; col < GRID_SIZE; ++col) {
                if (masks[row][col])
                    ++count;
            }
        }
        cellsRemainTF.setText(count + "");
    }

    public void setMasks(int diff) {

        for (int row = 0; row < GRID_SIZE; ++row) {
            for (int col = 0; col < GRID_SIZE; ++col) {
                masks[row][col] = false; // initialise to show all numbers
            }
        }
        // Generate 3 random numbers between 1-9, every row has equal missing boxes,
        // column selected is random
        Random rand = new Random();
        int countNumber = 0;
        for (int row = 0; row < GRID_SIZE; ++row) {
            int value = ((rand.nextInt(9))); // rand numb from 0 - 9
            int value2 = (value + 2) % 9;
            int value3 = (value + 3) % 9;
            switch (diff) {
                case 1:
                    if (countNumber != 9) // remove all of 1 random number
                        masks[row][value] = true; // true means not filled in
                    countNumber++;
                    break;
                case 2:
                    if (countNumber != 18) // remove all of 1 random number
                        masks[row][value] = true; // true means not filled in
                    masks[row][value2] = true;
                    countNumber++;
                    break;
                case 3:
                    if (countNumber != 27) // remove all of 1 random number
                        masks[row][value] = true; // true means not filled in
                    masks[row][value2] = true;
                    masks[row][value3] = true;
                    countNumber++;
                    break;
            }

        }
        for (int row = 0; row < GRID_SIZE; ++row) {
            for (int col = 0; col < GRID_SIZE; ++col) {
                temp[row][col] = masks[row][col]; // initialise to show all numbers, temp array for resetPuzzle
            }
        }
    }

    public void setDifficulty() { // Set difficulty at beginning
        String[] choices = { "EASY", "MEDIUM", "HARD" }; // 3 different choices
        // drop lists and give options
        String input = (String) JOptionPane.showInputDialog(null, "Choose difficulty", "Choose difficulty",
                JOptionPane.QUESTION_MESSAGE, null, choices, // Array of choices
                choices[0]); // Initial choice, which is easy

        // This parameter may be null, in which case a default Frame is used as the
        // parent, and the dialog will be centered on the screen (depending on the L&F).

        setNewPuzzle(); // Generate new puzzle
        if (input == "EASY") {
            setMasks(EASY); // remove all of 1 random number
        } else if (input == "MEDIUM") {
            setMasks(MEDIUM); // remove all of 2 random numbers
        } else if (input == "HARD") {
            setMasks(HARD); // remove all of 3 random numbers
        } else if (input == null) {
            System.exit(0);
        }
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Handle window closing
    }

    // Reset puzzle methods
    // exactly the same but rename it as resetdifficulty
    // https://docs.oracle.com/javase/tutorial/uiswing/components/dialog.html

    public void resetDifficulty() {
        String[] choices = { "EASY", "MEDIUM", "HARD" };
        String input = (String) JOptionPane.showInputDialog(null, "Choose difficulty", "Choose difficulty",
                JOptionPane.QUESTION_MESSAGE, null, choices, // Array of choices
                choices[0]); // Initial choice

        setNewPuzzle();
        // Easy was set = 1, Medium = 2, Hard = 3
        if (input == "EASY") {
            setMasks(EASY); // remove all of 1 random number
            resetPuzzle();
        } else if (input == "MEDIUM") {
            setMasks(MEDIUM); // remove all of 2 random numbers
            resetPuzzle();
        } else if (input == "HARD") {
            setMasks(HARD); // remove all of 3 random numbers
            resetPuzzle();
        }
    }

    public void setNewPuzzle() {
        puzzle = new SudokuPuzzleMatrixGenerator().generateSudokuArray();
    }

    public void resetPuzzle() { // different from top because dont add tfCells to maingame panel again
        InputListener listener = new InputListener();
        // SoundEffect.sound.playbackground();
        for (int row = 0; row < GRID_SIZE; ++row) {
            for (int col = 0; col < GRID_SIZE; ++col) {
                if (masks[row][col]) {
                    tfCells[row][col].setText(""); // set to empty string
                    tfCells[row][col].setEditable(true);
                    tfCells[row][col].setBackground(OPEN_CELL_BGCOLOR);
                    tfCells[row][col].addActionListener(listener);
                } else {
                    tfCells[row][col].setText(puzzle[row][col] + ""); // set puzzle
                    tfCells[row][col].setEditable(false);
                    tfCells[row][col].setForeground(CLOSED_CELL_TEXT);

                    if ((row < 3 || row > 5) && (col < 3 || col > 5)) {
                        tfCells[row][col].setBackground(CLOSED_CELL_BGCOLOR1);
                    } else {
                        tfCells[row][col].setBackground(CLOSED_CELL_BGCOLOR2);
                        if (row > 2 && row < 6 && col > 2 && col < 6) {
                            tfCells[row][col].setBackground(CLOSED_CELL_BGCOLOR1);
                        }
                    }
                    // Beautify all the cells
                }
                // Beautify all the cells
                tfCells[row][col].setHorizontalAlignment(JTextField.CENTER);
                tfCells[row][col].setFont(FONT_NUMBERS);
            }
        }
    }

    public void resetGame() {
        for (int row = 0; row < GRID_SIZE; ++row) {
            for (int col = 0; col < GRID_SIZE; ++col) {
                masks[row][col] = temp[row][col];
            }
        }
        resetPuzzle();
        countRemain();

    }

    public void setTextDefault() {
        for (int row = 0; row < GRID_SIZE; ++row) {
            for (int col = 0; col < GRID_SIZE; ++col) {
                if (!masks[row][col]) {
                    tfCells[row][col].setForeground(Color.BLACK); // turn the rest back to black
                }
            }
        }
    }
    public void setRight(int rowSelected, int colSelected) {
        tfCells[rowSelected][colSelected].setBackground(Color.GREEN);
        // SoundEffect.sound.playding();
        for (int row = 0; row < GRID_SIZE; ++row) {
            for (int col = 0; col < GRID_SIZE; ++col) {
                if (!masks[row][col]) {
                    if (puzzle[row][col] == Integer.parseInt(tfCells[rowSelected][colSelected].getText())) {
                        tfCells[row][col].setForeground(OPEN_CELL_TEXT_YES); // when correct input, turn all
                        // that number green)
                    } else {
                        tfCells[row][col].setForeground(Color.BLACK); // when correct input, turn all numbers
                        // back to black(if red), except that
                        // correct number
                    }
                }
            }
        }
        masks[rowSelected][colSelected] = false;
    }


    public void setWrong(int rowSelected, int colSelected) {
        tfCells[rowSelected][colSelected].setBackground(Color.RED);
        // SoundEffect.sound.playaww();

        for (int row = 0; row < GRID_SIZE; ++row) {
            for (int col = 0; col < GRID_SIZE; ++col) {
                if (!masks[row][col]) {
                    if (puzzle[row][col] == Integer.parseInt(tfCells[rowSelected][colSelected].getText())) {
                        tfCells[row][col].setForeground(OPEN_CELL_TEXT_NO); // when wrong input, turn all the
                        // same number red
                    } else {
                        tfCells[row][col].setForeground(Color.BLACK); // turn the rest back to black
                    }
                }
            }
        }
    }




    public void timestart() {
        timestart = System.currentTimeMillis();
    }
    public void totaltimestart() {
        totaltimestart = System.currentTimeMillis();
    }


    public void pausestart() {
        pausestart = System.currentTimeMillis();
    }


    public void timeend() {
        timeend = System.currentTimeMillis();
    }

    public void timeused() {
        timeused = -(totaltimestart - timeend)/1000;
        JOptionPane.showMessageDialog(null,"You've spent " + timeused + " seconds on this puzzle!");
    }

    public void startTimer() {
        timestart();
        totaltimestart();
        stopped = false;
        Time2(1);
    }

    public void endTimer() {
        stopped = true;
        timeend();
        timeused();
        stopped = true;
        Time2(0);
    }



    public void Time2(int i)  {


        final Timer  timer =new Timer(1,new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e)
            {
                long clock = (System.currentTimeMillis() - timestart);
                if(!stopped)
                {showtime.setText(format(clock));
                }
                if(stopped) {

                    ((Timer)e.getSource()).stop();
                    showtime.setText(format(clock));

                }
                else if(paused) {
                    ((Timer)e.getSource()).stop();
                    pauseCount=clock;
                    showtime.setText(format(clock));



                }else if(!paused) {
                    ((Timer)e.getSource()).stop();
                    ((Timer)e.getSource()).start();
                    clock = (System.currentTimeMillis() - timestart+ pauseCount);
                    showtime.setText(format(clock));
                }

            }
        });

        timer.start();


    }

    private String format(long clock) {
        int hour, minute, second, milli;

        milli = (int) (clock % 1000);
        clock = clock / 1000;

        second = (int) (clock % 60);
        clock = clock / 60;

        minute = (int) (clock % 60);
        clock = clock / 60;

        hour = (int) (clock % 60);

        return String.format("%02d:%02d:%02d %03d", hour, minute, second, milli);
    }
}
