package apputvikling.jorber.wordstar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
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

    static class CharButton {
        private int id;
        private char letter;
        private final Button button;
        private boolean chosen;

        public CharButton(int id, Button button) {
            this.id = id;
            this.button = button;
        }
    }

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

    private final List<String> words = new ArrayList<>();
    private Set<String> matchingWords;
    List<Character> chosenCharacters;
    private final Map<Integer, CharButton> charButtonsMap = new HashMap<>();
    private char chosenChar;
    private static int points = 0;

    TextView textInput, pointsView, hintView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        for (CharButtons button : CharButtons.values()) {
            Button b = findViewById(button.id);
            b.setOnClickListener(view -> handleCharacterButton(view.getId()));
            charButtonsMap.put(button.id, new CharButton(button.id, b));
        }

        Button deleteBtn = findViewById(R.id.deleteBtn);
        deleteBtn.setText(R.string.delete_btn);

        Button shuffleBtn = findViewById(R.id.shuffleBtn);
        shuffleBtn.setText(R.string.shuffle_btn);
        shuffleBtn.setOnClickListener(view -> handleShuffleButton());

        Button submitBtn = findViewById(R.id.submitBtn);
        submitBtn.setText(R.string.submit_btn);
        submitBtn.setOnClickListener(view -> handleSubmitButton());

        Button hintButton = findViewById(R.id.hintBtn);
        hintButton.setText(R.string.hint_btn);
        hintButton.setOnClickListener(view -> handleHintButton());

        textInput = findViewById(R.id.textInput);
        textInput.setTextSize(36);
        pointsView = findViewById(R.id.pointView);
        pointsView.setText(String.format("Poeng: %s", points));
        hintView = findViewById(R.id.hintView);

        ProgressBar pb = findViewById(R.id.scoreProgressBar);

        findViewById(R.id.deleteBtn).setOnClickListener(view -> handleDeleteButton());
        try {
            Scanner in = new Scanner(new InputStreamReader(getAssets().open("no-wordlist.txt"), StandardCharsets.UTF_8));
            while (in.hasNext()) {
                words.add(in.nextLine());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        initalizeGame();
    }

    private void initalizeGame() {
        while (true) {
            chosenCharacters = getCharacters();
            chosenChar = chosenCharacters.get(new Random().nextInt(7));
            matchingWords = getWords(chosenCharacters, chosenChar);
            chosenCharacters.remove(Character.valueOf(chosenChar));
            if (matchingWords.size() >= 15)
                break;
        }
        System.out.println(chosenChar);
        for (String s : matchingWords)
            System.out.println(s);
        int charIndex = 0;
        CharButton midButton = charButtonsMap.get(CharButtons.MID_MID.id);
        midButton.letter = chosenChar;
        midButton.chosen = true;
        midButton.button.setText(Character.toString(chosenChar));
        for (CharButtons button : CharButtons.values()) {
            if (button.equals(CharButtons.MID_MID))
                continue;
            CharButton b = charButtonsMap.get(button.id);
            b.letter = chosenCharacters.get(charIndex);
            b.button.setText(Character.toString(chosenCharacters.get(charIndex++)));
        }
    }

    private Set<String> getWords(List<Character> characters, Character chosenCharacter) {
        Set<String> matchingWords = new HashSet<>();
        for (String s : words) {
            if (containsCharacters(s, characters) && s.contains(chosenCharacter + ""))
                matchingWords.add(s);
        }
        return matchingWords;
    }


    private boolean containsCharacters(String s, List<Character> characters) {
        for (char c : s.toCharArray())
            if (!characters.contains(c))
                return false;
        return true;
    }

    private boolean validWord(String word) {
        for (String s : words)
            if (word.equals(s))
                return true;
        return false;
    }

    private int getWordPointValue(String word) {
        return 10;
    }

    private List<Character> getCharacters() {
        List<String> characters = Arrays.stream("abcdefghijklmnopqrstuvwxyzæøå".toUpperCase().split("")).collect(Collectors.toList());
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i < 30; i++)
            numbers.add(i);
        Collections.shuffle(numbers);
        List<Character> chosen = new ArrayList<>();
        for (int i = 0; i < 7; i++)
            chosen.add(characters.get(numbers.get(i)).charAt(0));
        return chosen;
    }

    private void updatePoints(int n) {
        points += n;
        pointsView.setText(String.format("Poeng: %d", points));
    }

    private void showMessage(String s) {

    }

    private void reset() {
        textInput.setText("");
    }

    private void handleShuffleButton() {
        Collections.shuffle(chosenCharacters);
        int index = 0;
        for(CharButtons button : CharButtons.values()) {
            if(button == CharButtons.MID_MID)
                continue;
            CharButton b = charButtonsMap.get(button.id);
            b.letter = chosenCharacters.get(index++);
            b.button.setText(Character.toString(b.letter));

        }
    }

    private void handleHintButton() {
        String hint = "";
        int i = 0;
        int randomIndex = new Random().nextInt(matchingWords.size());
        for (String s : matchingWords) {
            if (i++ == randomIndex) {
                hint = s;
                break;
            }
        }
        int mid = hint.length() / 2;
        hintView.setText(String.format("Hint: %s**%s", hint.substring(0, mid - 1), hint.substring(mid + 1)));
    }

    private void handleDeleteButton() {
        if (textInput.length() < 1)
            return;
        String text = textInput.getText().toString();
        text = text.substring(0, text.length() - 1);
        textInput.setText("");
        for (char c : text.toCharArray()) {
            if (c == chosenChar) {
                textInput.append(Html.fromHtml("<font color=#ff0000>" + c + "</font>", Html.FROM_HTML_MODE_LEGACY));
                continue;
            }
            textInput.append(Character.toString(c));
        }

    }

    private void handleCharacterButton(int id) {
        if (textInput.getText().toString().length() >= 18)
            return;
        CharButton button = charButtonsMap.get(id);
        if (button.chosen) {
            textInput.append(Html.fromHtml("<font color=#ff0000>" + button.letter + "</font>", Html.FROM_HTML_MODE_LEGACY));
        } else {
            textInput.append(Character.toString(button.letter));
        }
    }

    private void handleSubmitButton() {
        String input = textInput.getText().toString();
        if (validWord(input)) {
            updatePoints(getWordPointValue(input));
            System.out.println("riktig!");
        } else {
            showMessage("Invalid word");
            System.out.println("feil");
        }
        reset();
    }
}