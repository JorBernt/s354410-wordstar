package apputvikling.jorber.wordstar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

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
        GameManager.instance().loadWordList(getAssets());
        getSupportActionBar().hide();

        for (CharButtons button : CharButtons.values()) {
            Button b = findViewById(button.id);
            b.setOnClickListener(view -> {
                if (textInput.getText().toString().length() >= 18)
                    return;
                CharButton cbutton = charButtonsMap.get(view.getId());
                if (cbutton.isChosen()) {
                    textInput.append(Html.fromHtml("<font color=#ff0000>" + cbutton.getLetter() + "</font>", Html.FROM_HTML_MODE_LEGACY));
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
                    textInput.append(Html.fromHtml("<font color=#ff0000>" + c + "</font>", Html.FROM_HTML_MODE_LEGACY));
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

        submitBtn.setOnClickListener(view -> {
            String input = textInput.getText().toString();
            int points = GameManager.instance().submitAnswer(input);
            if (points > 0) {
                showMessage(String.format("Correct word! Points: %s", points));
                updatePoints(GameManager.instance().getPoints());
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
        GameManager.instance().initalizeGame();
        updatePoints(0);
        int charIndex = 0;
        CharButton midButton = charButtonsMap.get(CharButtons.MID_MID.id);
        Character chosenChar = GameManager.instance().getChosenCharacter();
        midButton.setLetter(chosenChar);
        midButton.setChosen(true);
        midButton.getButton().setText(Character.toString(chosenChar));
        for (CharButtons button : CharButtons.values()) {
            if (button.equals(CharButtons.MID_MID))
                continue;
            CharButton b = charButtonsMap.get(button.id);
            b.setLetter(GameManager.instance().getChosenCharacters().get(charIndex));
            b.getButton().setText(Character.toString(GameManager.instance().getChosenCharacters().get(charIndex++)));
        }
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