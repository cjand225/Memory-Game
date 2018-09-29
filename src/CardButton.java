/*

    Name:           CardButton.java
    Programmer:     Charles J Andrews
    Description:    card class that inherits from JButton to give a card like feel of flipping over
                    when clicked on.
    Known Bugs:

 */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.Timer;

public class CardButton extends JButton {
  private ImageIcon[] icons = new ImageIcon[2];
  private int iconId = 0;
  private int id = 0;
  private int toggleDelay = 500;
  private boolean toggled = false;
  private boolean matched = false;
  private boolean aniFinished = false;
  private Timer toggleTimer;


  private static final int FRONT = 1;
  private static final int BACK = 0;

  /** Constuctor for card class, combining JButtons and two ImageIcons. */
  CardButton(int givenId, int givenIid, ImageIcon back, ImageIcon front) {
    super(back);

    setIconId(givenIid);
    setId(givenId);
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


  /** Switches Icons that are showing on JButton. */
  public void toggle() {
    if (!toggled) {
      setFace(FRONT);
      toggled = true;
    } else if (toggled && !matched) {
      setFace(BACK);
      toggled = false;
    }
  }

  /** Defaults Card class back to original state. */
  public void reset() {
    setFace(BACK);
    toggled = false;
    matched = false;
  }

  /** Switches Icon that is the "Face" of card. */
  public void resetIcon(int givenId, ImageIcon givenIcon) {
    setIconId(givenId);
    icons[1] = givenIcon;
  }

  /** Sets the "sides" of card. */
  private void setIconSides(ImageIcon backside, ImageIcon frontside) {
    icons[BACK] = backside;
    icons[FRONT] = frontside;
    setIcon(icons[BACK]);
  }

  public void startTimer() {
    toggleTimer.start();
  }

  public void setMatched(boolean choice) {
    matched = choice;
  }

  public int getIconId() {
    return iconId;
  }

  public int getId() {
    return id;
  }

  public void setId(int givenId) {
    id = givenId;
  }

  public void setIconId(int givenIid) {
    iconId = givenIid;
  }

  public boolean isToggled() {
    return toggled;
  }

  private void setFace(int chosenFace) {
    setIcon(icons[chosenFace]);
  }
}