package apputvikling.jorber.wordstar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class GameActivity extends AppCompatActivity {

    enum CharButtons {
        TOP_LEFT(R.id.topLeftBtn),
        BOTTOM_LEFT(R.id.bottomLeftBtn),
        TOP_MID(R.id.topMidBtn),
        MID_MID(R.id.midMidBtn),
        BOTTOM_MID(R.id.bottomMidBtn),
        TOP_RIGHT(R.id.topRightBtn),
        BOTTOM_RIGHT(R.id.bottomRightBtn);

        final int id;

        CharButtons(int id) {
            this.id = id;
        }
    }


    private final Map<Integer, CharButton> charButtonsMap = new HashMap<>();

    TextView textInput, pointsView, hintView, popupTextView, wordView;
    RelativeLayout popUpView;
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        for (CharButtons button : CharButtons.values()) {
            Button b = findViewById(button.id);
            b.setOnClickListener(view -> {
                if (textInput.getText().toString().length() >= 18)
                    return;
                CharButton cbutton = charButtonsMap.get(view.getId());
                if (cbutton.isChosen()) {
                    drawText(cbutton.getLetter());
                } else {
                    textInput.append(Character.toString(cbutton.getLetter()));
                }
            });
            charButtonsMap.put(button.id, new CharButton(button.id, b));
        }

        Button deleteBtn = findViewById(R.id.deleteBtn);
        deleteBtn.setText(R.string.delete_btn);
        deleteBtn.setOnClickListener(view -> {
            if (textInput.length() < 1)
                return;
            String text = textInput.getText().toString();
            text = text.substring(0, text.length() - 1);
            textInput.setText("");
            for (char c : text.toCharArray()) {
                if (c == GameManager.instance().getChosenCharacter()) {
                    drawText(c);
                    continue;
                }
                textInput.append(Character.toString(c));
            }
        });

        Button shuffleBtn = findViewById(R.id.shuffleBtn);
        shuffleBtn.setText(R.string.shuffle_btn);

        shuffleBtn.setOnClickListener(view -> GameManager.instance().shuffleCharactersButtons(charButtonsMap));

        Button submitBtn = findViewById(R.id.submitBtn);
        submitBtn.setText(R.string.submit_btn);

        TextView foundWordsView = findViewById(R.id.foundWordsView);
        foundWordsView.setMovementMethod(new ScrollingMovementMethod());
        submitBtn.setOnClickListener(view -> {
            String input = textInput.getText().toString();
            int points = GameManager.instance().submitAnswer(input);
            if (points > 0) {
                showMessage(String.format("Correct word! Points: %s", points));
                updatePoints(GameManager.instance().getPoints());
                foundWordsView.setText(GameManager.instance().getFoundWords());
            } else {
                showMessage("Invalid word");
            }
            reset();
        });

        Button hintButton = findViewById(R.id.hintBtn);
        hintButton.setText(R.string.hint_btn);
        hintButton.setOnClickListener(view -> hintView.setText(GameManager.instance().getHint()));

        textInput = findViewById(R.id.textInput);
        textInput.setTextSize(36);
        hintView = findViewById(R.id.hintView);
        pointsView = findViewById(R.id.pointView);
        wordView = findViewById(R.id.wordView);

        pb = findViewById(R.id.scoreProgressBar);
        pb.setMax(100);

        initializeGame();
    }

    private void initializeGame() {
        GameManager gm = GameManager.instance();
        gm.initializeGame();
        updatePoints(gm.getPoints());
        int charIndex = 0;
        CharButton midButton = charButtonsMap.get(CharButtons.MID_MID.id);
        Character chosenChar = gm.getChosenCharacter();
        midButton.setLetter(chosenChar);
        midButton.setChosen(true);
        midButton.getButton().setText(Character.toString(chosenChar));
        for (CharButtons button : CharButtons.values()) {
            if (button.equals(CharButtons.MID_MID))
                continue;
            CharButton b = charButtonsMap.get(button.id);
            b.setLetter(gm.getChosenCharacters().get(charIndex));
            b.getButton().setText(Character.toString(gm.getChosenCharacters().get(charIndex++)));
        }
    }

    private void drawText(char c) {
        String redColor = String.format("#%06x", ContextCompat.getColor(this, R.color.red_200) & 0xffffff);
        textInput.append(Html.fromHtml("<font color=" + redColor + ">" + c + "</font>", Html.FROM_HTML_MODE_LEGACY));
    }

    private void drawText(String s) {
        for(char c : s.toCharArray())
            drawText(c);
    }

    private void updatePoints(int n) {
        GameManager game = GameManager.instance();
        pointsView.setText(String.format("Points: %s. Total points: %s", n, game.getMaxPoints()));
        wordView.setText(String.format("Found words: %s. Total words: %s", game.getFoundWordAmount(), game.getTotalWordAmount()));
        System.out.println(game.getScoreProgress());
        pb.setProgress(game.getScoreProgress());
    }

    private void showMessage(String s) {

    }

    private void reset() {
        textInput.setText("");
    }
}