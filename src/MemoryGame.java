/*

    Name:       MemoryGame.java
    Programmer: Charles J Andrews
    Description: It's a memory game in which the user flips cards until a match
                is made, once all matches have been made the user can choose
                to reset the game and play again.
    Depends on: cardButton.java, Back.png, 0-13.png
    Known Bugs: None known at the time, but some poorly scoped logic awaits
                for the ActionListener & also probably poorly formmated
                comments.

 */
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.ImageIcon;


/**
    Name: memoryGame
    Description: Class that inherits functionality from Jframe and
                 implements game rules in conjunction with a card
                 class to implement the game "Memory" in which
                 a user has to match pairs of cards.

 */
public class MemoryGame extends JFrame
                        implements ActionListener {

    //Constants Needed for Non-Magic #s
    private static final int MAX_CARDS = 25;
    private static final int MAX_ICONS = 14;
    private static final int WILDCARD = 13;
    private static final int MAX_TOGGLED = 2;
    private static final int NONE_CHOSEN = -1;
    private static final int FRAME_HEIGHT = 600;
    private static final int FRAME_WIDTH = 1050;
    private static final int GRID_SIZE = 5;
    private static final int GRID_GAP = 10;
    private static final int BORDER_GAP = 100;

    //for JFrame
    private static final String JFRAME_NAME = "Memory Game";
    private JPanel cardPanel;
    private JPanel menuPanel;

    //for cards
    private cardButton[] cards = new cardButton[MAX_CARDS];
    private ImageIcon[] pics = new ImageIcon[MAX_ICONS];
    //private int[] cardOrder = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12,
    //                            1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13 };

    private int[] cardOrder;

    //Tracking Stats/Matches
    private int matches = 0;
    private int guesses = 0;
    private int lastChosen  = NONE_CHOSEN;
    private int cardsToggled = 0;
    private int[] cardAnimations = {0, 0};

    //variables for Menu Panel
    private JButton start;
    private JLabel matchText;
    private JLabel guessText;
    private ActionListener startListener;



    //constructor for entire game
    public MemoryGame() {
        super(JFRAME_NAME);

        setLayout(new FlowLayout());      //SetLayout of main JFrame
        initIcons();                      //Initialize Icons
        initCardOrder();                  //Initalize card Array
        shuffleOrder();                   //Initialize Order of first Shuffle
        initMenuPanel();                  //Initalize the Menu Panel
        initCardPanel();                  //Inialize the Card Panel
        pack();
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void actionPerformed(final ActionEvent e) {
        for (int i = 0; i < MAX_CARDS; i++) {
            if (e.getSource() == cards[i]) {
                if (!cardToggleCheck(cards[i].getID())) {
                    cards[i].toggle();
                    cardsToggled++;

                    //has already made choice & not same exact card
                    if (lastChosen != NONE_CHOSEN && cards[i].getID() != lastChosen) {
                        if (matchCheck(lastChosen, i)) {
                            handleMatch(i, lastChosen);
                        } else { //else pair doesn't match
                            handleMisMatch(i, lastChosen);
                        }
                    } else {                                               //else user needs to choose 1st card
                        if (cards[i].getIconID() == WILDCARD) {            //if user's choice is our wildcard, Mark it a match
                            handleWildcard(cards[i].getID());

                        } else {                                           //else its a normal card
                            lastChosen = cards[i].getID();
                        }
                    }
                }
                updateScore();
            }
        }
    }

    /**
        Name: initCardPanel.
        Description: Initializes a new JPanel named cardPanel, sets a 5x5
                    gridLayout as its layout and then calls loadCards
                    before adding itself to the MemoryGame JFrame
     */
    private void initCardPanel() {

        cardPanel = new JPanel();
        GridLayout grid =
                new GridLayout(GRID_SIZE, GRID_SIZE, GRID_GAP, GRID_GAP);
        cardPanel.setLayout(grid);
        loadCards();
        add(cardPanel);
    }

    /**
        Name: loadCards
        Decription: Initializes new instances of cardButton class to an array
                    of cardButtons,as well as associating an actionListener
                    before adding it to the JPanel named cardPanel.
     */
    private void loadCards() {



        for (int i = 0; i < MAX_CARDS; i++) {
            cards[i] = new cardButton(i, cardOrder[i],
                            pics[0], pics[cardOrder[i]]);
            cards[i].addActionListener(this);

            //cards[i].addActionListener(new ActionListener() {
            //    public void actionPerformed(ActionEvent e) {

                    //logic check
                    //match check
            //    }
            //} );

            cardPanel.add(cards[i]);
        }
    }

    /**
        Name: initMenuPanel
        Description: Initializes a new JPanel named menuPanel, sets a
                     BorderLayout as its Layout and then adds two
                     JLabels and a JButton (which are used for keeping
                     score & defaulting game back to original
                     condition) before adding itself to the
                     MemoryGame JFrame.

     */
    private void initMenuPanel() {

        menuPanel = new JPanel();
        menuPanel.setLayout(new BorderLayout(BORDER_GAP, BORDER_GAP));

        guessText = new JLabel("Guesses Made: " + (int) guesses);
        matchText = new JLabel("Matches Made: " + (int) matches);
        start = new JButton("Restart");

        startListener = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                restartGame();
            }
        };

        start.addActionListener(startListener);
        menuPanel.add(guessText, BorderLayout.WEST);
        menuPanel.add(start, BorderLayout.CENTER);
        menuPanel.add(matchText, BorderLayout.EAST);
        add(menuPanel);
    }


    /**
        Name: initIcons
        Description: Initializes icons from hardcoded files to an
                     ImageIcon Array named pics.

     */
    private void initIcons() {
        pics[0] = new ImageIcon(MemoryGame.class.getResource("/etc/Back.png"));
        for (int i = 1; i < MAX_ICONS; i++) {
            pics[i] = new ImageIcon(
                    MemoryGame.class.getResource("/etc/" + (int) (i) + ".png")
            );
        }
    }

    private void initCardOrder() {
        cardOrder = new int[MAX_CARDS];
        int inter = 0;

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < MAX_ICONS - 2; j++) {
                cardOrder[inter] = j + 1;
                inter++;
            }
        }
        cardOrder[MAX_CARDS - 1] = WILDCARD;
    }


    /**
            Name: updateScore
            Description: takes the score count from both integers "matches"
                         and "guesses" and set the new text of their
                         respective JLabels.

     */
    private void updateScore() {
        guessText.setText("Guesses Made: " + (int) guesses);
        matchText.setText("Matches Made: " + (int) matches);
        repaint();
    }

    /**
            Name: setScores
            Description: sets matches/guesses to whatever number you want.
                         It isUsed primarily to reset/initalize score for
                         the start/restart of the game.

     */

    private void setScores(final int matchNum, final int guessNum) {
        matches = matchNum;
        guesses = guessNum;
    }

    /**
        Name: matchCheck
        Description: Boolean function that checks if the two cards match or
                    the first card happens to be the wildcard, returns
                    true if either situations occur, false for everything else.

     */

    private boolean matchCheck(final int last, final int current) {
        return ((cards[last].getIconID() == cards[current].getIconID())
                || cards[last].getIconID() == WILDCARD);
    }

    private boolean cardToggleCheck(final int currCardID) {
        return (cards[currCardID].isToggled()
                && cardsToggled < MAX_TOGGLED + 1);
    }

    private void handleMatch(final int current, final int previous) {

        cards[previous].removeActionListener(this);              //remove both listeners
        cards[current].removeActionListener(this);
        cards[current].setMatched(true);                            //set both cards to matched
        cards[previous].setMatched(true);

        matches++;
        lastChosen = NONE_CHOSEN;
        cardsToggled = 0;
    }

    private void handleMisMatch(final int current, final int previous) {
        cardAnimations[0] = previous;
        cardAnimations[1] = cards[current].getID();

        cards[previous].startTimer();                               //restart timer of previous & current card
        cards[current].startTimer();

        lastChosen = NONE_CHOSEN;
        cardsToggled = 0;
        guesses++;
    }

    private void handleWildcard(final int current) {
        cards[current].removeActionListener(this);           //remove listener
        cards[current].setMatched(true);
        matches++;
    }

    /*
            Name: shuffleOrder
            Description: Implementation of a modern version of the Fisher–Yates shuffle
                            algorithm, which rolls the range of 0 to n-2 elements, and
                            switches the random element rolled with the current element in the
                            current loop iteration, allowing swaps with no possible repeats.
     */
    private void shuffleOrder() {

        for (int i = 0; i < MAX_CARDS - 2; i++) {
            int randomNum = ((int) (Math.random() * (MAX_CARDS)) + 1) - 1;
            int temp = cardOrder[i];
            cardOrder[i] = cardOrder[randomNum];
            cardOrder[randomNum] = temp;
        }
    }

    /*
        Name:        resetCards
        Description: Does what it says, resets cards back to original states,
                     by removing all actionlisteners, adding them back,
                     setting them to unmatched, flipping them back over,
                     and resetting the icon by adding the newest
                     randomized order that is made by the ShuffleOrder() function.
     */

    private void resetCards() {
        cardAnimations[0] = 0;
        cardAnimations[1] = 0;
        shuffleOrder();

        for (int i = 0; i < MAX_CARDS; i++) {
            //remove existing listeners if exists
            if (cards[i].getActionListeners() != null) {
                cards[i].removeActionListener(this);
            }
            cards[i].addActionListener(this); //re-add ActionListeners
            cards[i].reset();
            cards[i].resetIcon(cardOrder[i],
                    pics[cardOrder[i]]); //randomize icon
        }
    }

    /*
        Name: restartGame
        Description: reset scores (matches/guesses), Update Scores on UI,
                     reset cards back to original state, and forces
                     a redraw.
 */
    public void restartGame() {
        setScores(0, 0);
        updateScore();
        resetCards();
        repaint();
    }


    //MAIN
    public static void main(final String[] args) {

        MemoryGame mGame = new MemoryGame();
        mGame.addWindowListener(
                new WindowAdapter() {
                    public void windowClosing(final WindowEvent e) {
                        System.exit(0);
                    }
                });
    }


}
