package com.gamesofni.neko.guesswhichsaint.activities;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.gamesofni.neko.guesswhichsaint.R;
import com.gamesofni.neko.guesswhichsaint.data.Saint;
import com.gamesofni.neko.guesswhichsaint.db.SaintsContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

import com.gamesofni.neko.guesswhichsaint.db.SaintsDbQuery;
import com.github.chrisbanes.photoview.PhotoView;

import static com.gamesofni.neko.guesswhichsaint.db.SaintsContract.CATEGORY_MAGI;
import static com.gamesofni.neko.guesswhichsaint.db.SaintsDbQuery.CATEGORY_MAGI_KEY;
import static com.gamesofni.neko.guesswhichsaint.db.SaintsDbQuery.FEMALE_KEY;
import static com.gamesofni.neko.guesswhichsaint.db.SaintsDbQuery.MALE_KEY;


public class GuessSaint extends AppCompatActivity {

    private static final String USER_CHOICE = "userChoice";
    public static final String CORRECT_SAINT_NAME = "correctSaintName";

    private HashSet<Long> saintIds;
    private Map<Long, String> saintIdsToNamesFemale;
    private Map<Long, String> saintIdsToNamesMale;
    private Map<Long, String> saintIdsToNamesMagi;

    private ArrayList<ToggleButton> buttons;
    private String correctSaintName;

    private int correctChoice;
    private int correctAnswers = 0;
    private int wrongAnswers = 0;
    private boolean hasChecked = false;

    private TextView scoreView;
    PhotoView pictureView;
    Integer pictureResId;
    private SharedPreferences sharedPreferences;

    private boolean autoNext;
    private boolean christmasDone;
    private int christmasCorrectCount = 0;
    private static final Random ran = new Random();

    private int correctChoiceColor;
    private int wrongChoiceColor;

    private Toast correctAnswerToast;
    private Toast noAnswerToast;

    private static final String CORRECT_ANSWERS_KEY = "correct";
    private static final String WRONG_ANSWERS_KEY = "wrong";
    public static final String HAS_CHECKED_KEY = "guessed";
    public static final String BUTTON_NAMES = "buttonNames";
    private static final String PICTURE_RES_ID = "pictureResId";
    private static final String CORRECT_CHOICE = "correctChoice";

    public static final int SCORE_MIN_GUESSES = 5;
    public static final int CHRISTMAS_CORRECT_ANSWERS = 21;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUp();

//        if (saintIds.size() < 4) {
//            return;
//        }

        if (savedInstanceState == null) {
            setQuestion();
        }
    }

    private void setUp() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        autoNext = sharedPreferences.getBoolean("autoNext", false);
        christmasDone = sharedPreferences.getBoolean("christmasDone", false);
        christmasCorrectCount = sharedPreferences.getInt("christmasCorrectCount", 0);

        Map<String, Map <Long, String>> allSaintsIdToNames = SaintsDbQuery.getAllSaintsIdToNames(this);

        saintIdsToNamesFemale = allSaintsIdToNames.get(FEMALE_KEY);
        saintIdsToNamesMale = allSaintsIdToNames.get(MALE_KEY);
        saintIdsToNamesMagi = allSaintsIdToNames.get(CATEGORY_MAGI_KEY);

        saintIds = new HashSet<>();

        if (christmasDone) {
            saintIds.addAll(saintIdsToNamesFemale.keySet());
            saintIds.addAll(saintIdsToNamesMale.keySet());
            saintIds.addAll(saintIdsToNamesMagi.keySet());
            if (saintIds.size() < 4) {
                setContentView(R.layout.empty_db);
                return;
            }
        } else {
            saintIds.addAll(saintIdsToNamesMagi.keySet());
        }

        setContentView(R.layout.activity_guess);

        pictureView = (PhotoView) findViewById(R.id.guessMergeImageView);

//        pictureView = (ImageView) findViewById(R.id.guessMergeImageView);
        scoreView = (TextView) findViewById(R.id.guess_menu_score);

        correctChoiceColor = getResources().getColor(R.color.awesome_green);
        wrongChoiceColor = getResources().getColor(R.color.bad_red);

        setUpButtons();

        Button guessActivityCheckButton = (Button) findViewById(R.id.guess_menu_next);
        guessActivityCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                onSubmitChoice(view);
            }
        });

        final Button guessActivityBackButton = (Button) findViewById(R.id.guess_menu_back);
        guessActivityBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                GuessSaint.this.onBackPressed();
            }
        });

    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(CORRECT_ANSWERS_KEY) && savedInstanceState.containsKey(WRONG_ANSWERS_KEY)) {
            restoreState(savedInstanceState);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void restoreState(Bundle state) {
        correctAnswers = state.getInt(CORRECT_ANSWERS_KEY, 0);
        wrongAnswers = state.getInt(WRONG_ANSWERS_KEY, 0);
        setScore();

        pictureResId = state.getInt(PICTURE_RES_ID);
        pictureView.setImageResource(pictureResId);

        hasChecked = state.getBoolean(HAS_CHECKED_KEY, false);

        HashMap<Integer, String> buttonNames = (HashMap<Integer, String>) state.getSerializable(BUTTON_NAMES);
        for (Map.Entry<Integer, String> e : buttonNames.entrySet()) {
            setNameOnButton(buttons.get(e.getKey()), e.getValue());
        }
        clearAllButtons();

        correctChoice = state.getInt(CORRECT_CHOICE);
        correctSaintName = state.getString(CORRECT_SAINT_NAME);
        final int userChoice = state.getInt(USER_CHOICE, -1);
        if (userChoice != -1) {
            buttons.get(userChoice).setChecked(true);
            if (hasChecked) {
                if (userChoice != correctChoice) {
                    buttons.get(userChoice).setBackgroundColor(wrongChoiceColor);
                }
                buttons.get(correctChoice).setBackgroundColor(correctChoiceColor);
            }
        }

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(WRONG_ANSWERS_KEY, wrongAnswers);
        outState.putInt(CORRECT_ANSWERS_KEY, correctAnswers);
        outState.putBoolean(HAS_CHECKED_KEY, hasChecked);
        outState.putInt(PICTURE_RES_ID, pictureResId);
        outState.putInt(CORRECT_CHOICE, correctChoice);
        outState.putString(CORRECT_SAINT_NAME, correctSaintName);
        outState.putInt(USER_CHOICE, getCheckedButtonId());

        HashMap<Integer, String> buttonNames = new HashMap<>(4);
        for (int i = 0; i < buttons.size(); i++) {
            String name = buttons.get(i).getText().toString();
            buttonNames.put(i, name);
        }
        outState.putSerializable(BUTTON_NAMES, buttonNames);

        if (!christmasDone && christmasCorrectCount > CHRISTMAS_CORRECT_ANSWERS) {
            SharedPreferences.Editor prefEditor = sharedPreferences.edit();
            prefEditor.putInt("christmasCorrectCount", 0);
            prefEditor.putBoolean("christmasDone", true);
            prefEditor.apply();
        }

        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (wrongAnswers + correctAnswers < SCORE_MIN_GUESSES) {
            return;
        }

        // save stats to preferences
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();
        prefEditor.putInt("christmasCorrectCount", christmasCorrectCount);

        // save score to prefs only if got higher
        float savedScore = sharedPreferences.getFloat("score", 0.0f);
        float currentScore = getScore();
        if (savedScore < currentScore) {
            prefEditor.putFloat("score", getScore());
        }

        prefEditor.apply();
    }

    private void setQuestion() {
        // TODO: optimize maps lists ops
        ArrayList<Long> saintsListIds = new ArrayList<>(saintIds);

        final long correctSaintId = saintsListIds.remove(ran.nextInt(saintsListIds.size()));
        final Saint correctSaint = Saint.convertSaintFromCursor(SaintsDbQuery.getSaint(this, correctSaintId), this);
        this.correctSaintName = correctSaint.getName();

        ArrayList<Integer> pictureUrls = correctSaint.getPaintings();
        pictureResId = pictureUrls.get(ran.nextInt(pictureUrls.size()));
        pictureView.setImageResource(pictureResId);

        correctChoice = ran.nextInt(buttons.size());

        if (christmasDone) {
            if (correctSaint.getGender().equals(SaintsContract.GENDER_FEMALE)) {
                saintsListIds = new ArrayList<>(saintIdsToNamesFemale.keySet());
            } else if (CATEGORY_MAGI.equals(correctSaint.getCategory())) {
                saintsListIds = new ArrayList<>(saintIdsToNamesMagi.keySet());
            } else {
                saintsListIds = new ArrayList<>(saintIdsToNamesMale.keySet());
            }
        } else {
            saintsListIds = new ArrayList<>(saintIdsToNamesMagi.keySet());
        }
        saintsListIds.remove(correctSaintId);

        for (int i = 0; i < buttons.size(); i++) {
            ToggleButton button = buttons.get(i);
            if (i == correctChoice) {
                setNameOnButton(button, correctSaint.getName());
                continue;
            }

            final long aSaintId = saintsListIds.remove(ran.nextInt(saintsListIds.size()));
            final String name;

            if (correctSaint.getGender().equals(SaintsContract.GENDER_FEMALE)) {
                name = saintIdsToNamesFemale.get(aSaintId);
            } else if (CATEGORY_MAGI.equals(correctSaint.getCategory())) {
                name = saintIdsToNamesMagi.get(aSaintId);
            } else {
                name = saintIdsToNamesMale.get(aSaintId);
            }

            setNameOnButton(button, name);
        }

        clearAllButtons();
    }

    public void onSubmitChoice(View view) {
        if (hasChecked) {
            onNext();
            return;
        }

        final int userChoiceId = getCheckedButtonId();

        if (userChoiceId == -1) {
            if (noAnswerToast != null) {
                noAnswerToast.cancel();
            }

            noAnswerToast = Toast.makeText(this, R.string.no_answer_toast_text, Toast.LENGTH_SHORT);
            noAnswerToast.show();

            return;
        }
        final boolean correctAnswer = userChoiceId == correctChoice;
        if (correctAnswer) {
            correctAnswers++;
            if (!christmasDone) {
                christmasCorrectCount += 1;
            }
        } else {
            buttons.get(userChoiceId).setBackgroundColor(wrongChoiceColor);
            wrongAnswers++;
        }
        buttons.get(correctChoice).setBackgroundColor(correctChoiceColor);

        setScore();

        if (autoNext) {
            if (correctAnswerToast != null) {
                correctAnswerToast.cancel();
            }
            final String message = correctAnswer ?
                    getString(R.string.answer_correct) :
                    getString(R.string.answer_wrong) + correctSaintName;
            correctAnswerToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
            correctAnswerToast.show();

            onNext();
        } else {
            hasChecked = true;
        }

    }

    public void onNext() {
        hasChecked = false;
        setQuestion();
    }


    private void setUpButtons() {
        CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton button, boolean isChecked) {
                if (isChecked) {
                    clearAllButtons();
                    button.setChecked(true);
                }
            }
        };

        buttons = new ArrayList<>(4);

        setUpButton(onCheckedChangeListener, R.id.guess_button1);
        setUpButton(onCheckedChangeListener, R.id.guess_button2);
        setUpButton(onCheckedChangeListener, R.id.guess_button3);
        setUpButton(onCheckedChangeListener, R.id.guess_button4);

    }

    private void setUpButton(CompoundButton.OnCheckedChangeListener onCheckedChangeListener, int guess_button_id) {
        ToggleButton guessButton = (ToggleButton) findViewById(guess_button_id);
        guessButton.setOnCheckedChangeListener(onCheckedChangeListener);
        buttons.add(guessButton);
    }

    private void setNameOnButton(ToggleButton button, String name) {
        button.setTextOff(name);
        button.setTextOn(name);
        button.setText(name);
    }

    private void clearAllButtons() {
        for (ToggleButton button : buttons) {
            button.setBackgroundResource(R.drawable.guess_button);
            button.setChecked(false);
        }
    }

    private int getCheckedButtonId() {
        for (int i = 0; i < buttons.size(); i++) {
            ToggleButton button = buttons.get(i);
            if (button.isChecked()) {
                return i;
            }
        }
        return -1;
    }

    private void setScore() {
        final float score = getScore();
        scoreView.setText(String.format(getString(R.string.score_message), score));
    }

    private float getScore() {
        if (correctAnswers + wrongAnswers == 0) {
            return 0f;
        }
        return 100 * ((float) correctAnswers / (float) (correctAnswers + wrongAnswers));
    }
}
