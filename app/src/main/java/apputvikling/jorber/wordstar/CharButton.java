package apputvikling.jorber.wordstar;

import android.widget.Button;

public class CharButton {
    private int id;
    private char letter;
    private final Button button;
    private boolean chosen;

    public CharButton(int id, Button button) {
        this.id = id;
        this.button = button;
    }

    public int getId() {
        return id;
    }

    public char getLetter() {
        return letter;
    }

    public void setLetter(Character c) {
        this.letter = c;
    }

    public Button getButton() {
        return button;
    }

    public boolean isChosen() {
        return chosen;
    }

    public void setChosen(boolean val) {
        this.chosen = val;
    }
}
