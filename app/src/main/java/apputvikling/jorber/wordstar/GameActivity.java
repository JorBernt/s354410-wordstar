package apputvikling.jorber.wordstar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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

    TextView textInput, pointsView, hintView, wordView;
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        //Initializes and sets up the word star buttons.
        for (CharButtons button : CharButtons.values()) {
            Button b = findViewById(button.id);
            b.setTextSize(36);
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
        shuffleBtn.setOnClickListener(view -> {
            List<Character> chosenCharacters = GameManager.instance().shuffleCharacters();
            int index = 0;
            for (GameActivity.CharButtons button : GameActivity.CharButtons.values()) {
                if (button == GameActivity.CharButtons.MID_MID)
                    continue;
                CharButton b = charButtonsMap.get(button.id);
                b.setLetter(chosenCharacters.get(index++));
                b.getButton().setText(Character.toString(b.getLetter()));
            }
        });

        TextView foundWordsView = findViewById(R.id.foundWordsView);
        foundWordsView.setMovementMethod(new ScrollingMovementMethod());

        Button submitBtn = findViewById(R.id.submitBtn);
        submitBtn.setText(R.string.submit_btn);
        submitBtn.setOnClickListener(view -> {
            String input = textInput.getText().toString();
            int points = GameManager.instance().submitAnswer(input);
            if (points > 0) {
                showMessage(String.format(getString(R.string.correct_respons), points));
                updatePoints(GameManager.instance().getPoints());
                foundWordsView.setText(GameManager.instance().getFoundWords());
            }
            else if(points == -1) {
                showMessage(getString(R.string.already_found_respons));
            }
            else {
                showMessage(getString(R.string.wrong_respons));
            }
            reset();
        });

        Button hintButton = findViewById(R.id.hintBtn);
        hintButton.setText(R.string.hint_btn);
        hintButton.setOnClickListener(view -> hintView.setText(GameManager.instance().getHint()));

        Button fasitButton = findViewById(R.id.fasitBtn);
        fasitButton.setText("?");
        fasitButton.setOnClickListener(view -> {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle(getString(R.string.solution));
            alertDialog.setMessage(GameManager.instance().getSolution());
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", (dialogInterface, i) -> { });
            alertDialog.show();
        });

        textInput = findViewById(R.id.textInput);
        textInput.setTextSize(36);
        hintView = findViewById(R.id.hintView);
        pointsView = findViewById(R.id.pointView);
        wordView = findViewById(R.id.wordView);

        pb = findViewById(R.id.scoreProgressBar);
        pb.setMax(100);

        initializeGame();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outstate) {
        super.onSaveInstanceState(outstate);
        outstate.putString("textInput", textInput.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        drawText(savedInstanceState.getString("textInput"));
        TextView foundWordsView = findViewById(R.id.foundWordsView);
        foundWordsView.setText(GameManager.instance().getFoundWords());
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
        TextView foundWordsView = findViewById(R.id.foundWordsView);
        foundWordsView.setText(GameManager.instance().getFoundWords());
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
        pointsView.setText(String.format(getString(R.string.point_view_text), n, game.getMaxPoints()));
        wordView.setText(String.format(getString(R.string.foundword_view_text), game.getFoundWordAmount(), game.getTotalWordAmount()));
        pb.setProgress(game.getScoreProgress());
    }

    private void showMessage(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    private void reset() {
        textInput.setText("");
    }
}