/*

    Name:           cardButton.java
    Programmer:     Charles J Andrews
    Description:    card class that inherits from JButton to give a card like feel of flipping over
                    when clicked on.
    Known Bugs:

 */
import java.awt.event.*;
import javax.swing.*;

public class cardButton extends JButton{
    private ImageIcon[] icons = new ImageIcon[2];
    private int IconID = 0;
    private int ID = 0;
    private int toggleDelay = 500;
    private boolean Toggled = false;
    private boolean matched = false;
    private boolean aniFinished = false;
    private Timer toggleTimer;


    private static final int FRONT = 1;
    private static final int BACK = 0;


    //constructs a Jbutton with an ID, IconID, and two ImageIcons for sides
    cardButton(int givenID, int givenIID, ImageIcon back, ImageIcon front){
        super(back);

        setIconID(givenIID);
        setID(givenID);
        setIconSides(back, front);

        ActionListener taskPerformer = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                toggle();
                toggleTimer.stop();
                aniFinished = true;
            }
        };

        toggleTimer = new Timer(toggleDelay, taskPerformer);
    }


    //swaps card over
    public void toggle(){

        if(!Toggled) {
            setFace(FRONT);
            Toggled = true;
        }
        else if(Toggled && !matched){
            setFace(BACK);
            Toggled = false;
        }
    }

    public void reset(){
        setFace(BACK);
        Toggled = false;
        matched = false;
    }

    public void resetIcon(int givenID, ImageIcon givenIcon){
        setIconID(givenID);
        icons[1] = givenIcon;
    }

    private void setIconSides(ImageIcon backside, ImageIcon frontside){
        icons[BACK] = backside;
        icons[FRONT] = frontside;
        setIcon(icons[BACK]);
    }

    public void startTimer(){ toggleTimer.start(); }

    public boolean getAniFinished() { return aniFinished; }

    public boolean getMatched() { return matched; }

    public void setMatched(boolean choice) { matched = choice; }

    public int getIconID(){ return IconID; }

    public int getID(){ return ID; }

    public void setID(int givenID){ ID = givenID; }

    public void setIconID(int givenIID){ IconID = givenIID; }

    public boolean isToggled(){ return Toggled; }

    public void setToggle(boolean toggle){ Toggled = toggle; }

    private void setFace(int chosenFace){ setIcon(icons[chosenFace]); }

}