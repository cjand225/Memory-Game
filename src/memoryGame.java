/*

    Name:       memoryGame.java
    Programmer: Charles J Andrews
    Description: It's a memory game in which the user flips cards until a match is made, once all matches have been
                made the user can choose to reset the game and play again.
    Depends on: cardButton.java, Back.png, 0-13.png
    Known Bugs: None known at the time, but some poorly scoped logic awaits for the ActionListener
                & also probably poorly formmated comments, I wrote them in Intellij using an 1080p monitor
                if they seem illegible.

 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.lang.Math;

public class memoryGame extends JFrame
        implements ActionListener {

    //Constants Needed for Non-Magic #s
    private static final int MAX_CARDS = 25;
    private static final int WILDCARD = 13;
    private static final int MAX_TOGGLED = 2;
    private static final int NONE_CHOSEN = -1;

    //for JFrame
    private JPanel cardPanel;
    private JPanel menuPanel;

    //for cards
    private cardButton[] cards = new cardButton[25];
    private ImageIcon[] pics = new ImageIcon[14];
    private int cardOrder[] = {1,2,3,4,5,6,7,8,9,10,11,12,
            1,2,3,4,5,6,7,8,9,10,11,12,13 };

    //Tracking Stats/Matches
    private int matches = 0;
    private int guesses = 0;
    private int lastChosen  = NONE_CHOSEN;
    private int cardsToggled = 0;
    private int cardAnimations[] = {0,0};

    //variables for Menu Panel
    private JButton start;
    private JLabel matchText;
    private JLabel guessText;
    private ActionListener startListener;



    //constructor for entire game
    public memoryGame() {
        super("Memory Game");

        setLayout(new FlowLayout());                                //SetLayout of main JFrame
        initIcons();                                                //Initialize Icons
        shuffleOrder();                                             //Initialize Order of first Shuffle
        initMenuPanel();                                            //Initalize the Menu Panel
        initCardPanel();                                            //Inialize the Card Panel


        pack();
        setSize(1050,600);
        setLocationRelativeTo(null);
        setVisible(true);
    }


    public void actionPerformed(ActionEvent e) {

        //debug, checks which button is being pressed
        for(int i = 0; i < MAX_CARDS; i++){
            if(e.getSource() == cards[i]){
                if(!cardToggleCheck(cards[i].getID())) {
                    cards[i].toggle();
                    cardsToggled++;

                    //go ahead and flip the card
                    if (lastChosen != NONE_CHOSEN && cards[i].getID() != lastChosen) {  //if user has already made 1 choice & user hasnt clicked the same exact card
                        if (matchCheck(lastChosen, i)) {
                            handleMatch(i, lastChosen);
                        }
                        else { //else pair doesn't match
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

    /*
        Name: initCardPanel
        Description: Initializes a new JPanel named cardPanel, sets a 5x5 gridLayout as its
                     Layout and then calls loadCards before adding itself to the memoryGame JFrame
     */
    private void initCardPanel(){

        cardPanel = new JPanel();
        GridLayout grid = new GridLayout(5,5,10,10);
        cardPanel.setLayout(grid);
        loadCards();
        add(cardPanel);
    }

    /*
        Name: loadCards
        Decription: Initializes new instances of cardButton class to an array of cardButtons,as well as
                    associating an actionListener before adding it to the JPanel named cardPanel.
     */
    private void loadCards(){

        for(int i = 0; i < MAX_CARDS; i++){

            cards[i] = new cardButton(i, cardOrder[i], pics[0], pics[cardOrder[i]]);
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


    /*
        Name: resetCards
        Description: Does what it says, resets cards back to original states, by removing all actionlisteners,
                     adding them back, setting them to unmatched, flipping them back over, and resetting the icon
                     by adding the newest randomized order that is made by the ShuffleOrder() function.
     */

    private void resetCards(){
        cardAnimations[0] = 0;
        cardAnimations[1] = 0;
        shuffleOrder();                                             //shuffle Order of Icons

        for(int i = 0; i < MAX_CARDS; i++){                                //fix buttons
            if(cards[i].getActionListeners() != null){              //remove existing listeners
                cards[i].removeActionListener(this);
            }
            cards[i].addActionListener(this);                       //then add brand spankin new ones
            cards[i].reset();
            cards[i].resetIcon(cardOrder[i], pics[cardOrder[i]]);   //randomize icon
        }
    }



    /*
        Name: initMenuPanel
        Description: Initializes a new JPanel named menuPanel, sets a BorderLayout as its
                     Layout and then adds two JLabels and a JButton (which are used for keeping
                     score & defaulting game back to original condition) before adding itself to the
                     memoryGame JFrame

     */
    private void initMenuPanel(){

        menuPanel = new JPanel();
        menuPanel.setLayout(new BorderLayout(100,100));

        guessText = new JLabel("Guesses Made: " + (int)guesses);
        matchText = new JLabel("Matches Made: " + (int)matches);
        start = new JButton("Restart");

        startListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                restartGame();
            }
        };

        start.addActionListener(startListener);
        menuPanel.add(guessText, BorderLayout.WEST);
        menuPanel.add(start, BorderLayout.CENTER);
        menuPanel.add(matchText, BorderLayout.EAST);
        add(menuPanel);
    }


    /*
        Name: initIcons
        Description: Initializes icons from hardcoded files to an ImageIcon Array named pics

     */
    private void initIcons(){
        pics[0] = new ImageIcon(memoryGame.class.getResource("/etc/Back.png"));
        for(int i = 1; i < 14; i++){
            pics[i] = new ImageIcon(memoryGame.class.getResource( "/etc/" + (int)(i) + ".png"));
        }
    }

    /*
            Name: restartGame
            Description: reset scores (matches/guesses), Update Scores on UI, reset cards back to original state,
                            and forces a redraw.
     */
    public void restartGame(){
        setScores(0,0);
        updateScore();
        resetCards();
        repaint();
    }


    /*
            Name: updateScore
            Description: takes the score count from both integers "matches" and "guesses" and set the new
                          text of their respective JLabels

     */
    private void updateScore(){
        guessText.setText("Guesses Made: " + (int)guesses);
        matchText.setText("Matches Made: " + (int)matches);
        repaint();
    }

    /*
            Name: setScores
            Description: sets matches/guesses to whatever number you want. Used primarily to reset/initalize score
                         for the start/restart of the game.

     */

    private void setScores(int matchNum, int guessNum){
        matches = matchNum;
        guesses = guessNum;
    }

    /*
            Name: matchCheck
            Description: Boolean function that checks if the two cards match or the first card happens to be
                         the wildcard, returns true if either situations occur, false for everything else.

     */

    private boolean matchCheck(int last, int current){
        return ((cards[last].getIconID() == cards[current].getIconID()) || cards[last].getIconID() == WILDCARD);
    }

    private boolean cardToggleCheck(int currCardID){
        return (cards[currCardID].isToggled() &&
                ((cards[cardAnimations[0]].getAniFinished() && cards[cardAnimations[1]].getAniFinished())
                        || (cardAnimations[0] == 0 || cardAnimations[1] == 0))
                && cardsToggled < MAX_TOGGLED + 1);
    }

    private void handleMatch(int current, int previous){

        cards[previous].removeActionListener(this);              //remove both listeners
        cards[current].removeActionListener(this);
        cards[current].setMatched(true);                            //set both cards to matched
        cards[previous].setMatched(true);

        matches++;
        lastChosen = NONE_CHOSEN;
        cardsToggled = 0;
    }

    private void handleMisMatch(int current, int previous){
        cardAnimations[0] = previous;
        cardAnimations[1] = cards[current].getID();

        cards[previous].startTimer();                               //restart timer of previous & current card
        cards[current].startTimer();

        lastChosen = NONE_CHOSEN;
        cardsToggled = 0;
        guesses++;
    }

    private void handleWildcard(int current){
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
    private void shuffleOrder(){

        for(int i = 0; i < MAX_CARDS - 2; i++) {
            int randomNum = ((int) (Math.random() * (MAX_CARDS)) + 1) - 1;
            int temp = cardOrder[i];
            cardOrder[i] = cardOrder[randomNum];
            cardOrder[randomNum] = temp;
        }
    }


    //MAIN
    public static void main(String[] args) {

        memoryGame mGame = new memoryGame();
        mGame.addWindowListener(
                new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                        System.exit(0);
                    }
                });
    }


}