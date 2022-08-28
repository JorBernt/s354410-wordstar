package apputvikling.jorber.wordstar;

import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;


public class GameManager {

    private static GameManager instance;
    private final List<String> words = new ArrayList<>();
    private Set<String> matchingWords;
    private List<Character> chosenCharacters;
    private char chosenChar;
    private int points = 0;
    private int maxPoints = 0;
    private int foundWords = 0;

    private GameManager() {
    }

    public static GameManager instance() {
        return instance = instance == null ? new GameManager() : instance;
    }

    public void initializeGame() {
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
        for (String s : matchingWords)
            maxPoints += getWordPointValue(s);

    }

    private boolean containsCharacters(String s, List<Character> characters) {
        for (char c : s.toCharArray())
            if (!characters.contains(c))
                return false;
        return true;
    }

    public void loadWordList(AssetManager am) {
        try {
            Scanner in = new Scanner(new InputStreamReader(am.open("no-wordlist.txt"), StandardCharsets.UTF_8));
            while (in.hasNext()) {
                words.add(in.nextLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void shuffleCharactersButtons(Map<Integer, CharButton> buttonMap) {
        Collections.shuffle(chosenCharacters);
        int index = 0;
        for (GameActivity.CharButtons button : GameActivity.CharButtons.values()) {
            if (button == GameActivity.CharButtons.MID_MID)
                continue;
            CharButton b = buttonMap.get(button.id);
            b.setLetter(chosenCharacters.get(index++));
            b.getButton().setText(Character.toString(b.getLetter()));
        }
    }

    public int submitAnswer(String input) {
        if (validWord(input)) {
            int p = getWordPointValue(input);
            points += p;
            foundWords++;
            return p;
        }
        return 0;
    }

    private boolean validWord(String word) {
        for (String s : words)
            if (word.equals(s))
                return true;
        return false;
    }

    private Set<String> getWords(List<Character> characters, Character chosenCharacter) {
        Set<String> matchingWords = new HashSet<>();
        for (String s : words) {
            if (containsCharacters(s, characters) && s.contains(chosenCharacter + ""))
                matchingWords.add(s);
        }
        return matchingWords;
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

    public List<Character> getChosenCharacters() {
        return chosenCharacters;
    }

    public Character getChosenCharacter() {
        return chosenChar;
    }

    public String getHint() {
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
        return String.format("Hint: %s**%s", hint.substring(0, mid - 1), hint.substring(mid + 1));
    }

    public int getPoints() {
        return points;
    }

    public int getTotalWordAmount() {
        return matchingWords.size();
    }

    private int getWordPointValue(String word) {
        int p = 0;
        for (char c : word.toCharArray()) {
            switch (c) {
                case 'A':
                case 'D':
                case 'E':
                case 'I':
                case 'L':
                case 'N':
                case 'R':
                case 'S':
                case 'T':
                    p += 1;
                    break;
                case 'F':
                case 'G':
                case 'K':
                case 'M':
                case 'O':
                    p += 2;
                    break;
                case 'H':
                    p += 3;
                    break;
                case 'B':
                case 'J':
                case 'P':
                case 'U':
                case 'V':
                case 'Å':
                    p += 4;
                    break;
                case 'Ø':
                    p += 5;
                    break;
                case 'Y':
                case 'Æ':
                    p += 6;
                    break;
                case 'W':
                    p += 8;
                    break;
                default:
                    p += 10;
                    break;
            }
        }
        return p;
    }

    public int getFoundWordAmount() {
        return foundWords;
    }

    public int getMaxPoints() {
        return maxPoints;
    }

    public int getScoreProgress() {
        System.out.println(points + " " + maxPoints);
        return (int) Math.ceil(((double) points / maxPoints) * 100);
    }
}
