package com.gamesofni.neko.guesswhichsaint.activities;


import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.gamesofni.neko.guesswhichsaint.R;
import com.gamesofni.neko.guesswhichsaint.data.Painting;
import com.gamesofni.neko.guesswhichsaint.data.Saint;
import com.gamesofni.neko.guesswhichsaint.db.PaintingsQuery;
import com.gamesofni.neko.guesswhichsaint.db.SaintsContract;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

import com.gamesofni.neko.guesswhichsaint.db.SaintsQuery;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import static com.gamesofni.neko.guesswhichsaint.db.SaintsContract.CATEGORY_MAGI;
import static com.gamesofni.neko.guesswhichsaint.db.SaintsQuery.CATEGORY_MAGI_KEY;
import static com.gamesofni.neko.guesswhichsaint.db.SaintsQuery.FEMALE_KEY;
import static com.gamesofni.neko.guesswhichsaint.db.SaintsQuery.MALE_KEY;


public class GuessSaint extends AppCompatActivity implements ResetDbDialogFragment.ResetDbDialogListener {

    private static final String USER_CHOICE = "userChoice";
    public static final String CORRECT_SAINT_NAME = "correctSaintName";
    public static final String TAG = GuessSaint.class.getSimpleName();

    private HashSet<Long> saintIds;
    private Map<Long, String> saintIdsToNamesFemale;
    private Map<Long, String> saintIdsToNamesMale;
    private Map<Long, String> saintIdsToNamesMagi;

    private ArrayList<Painting> unguessedPaintings;

    private ArrayList<MaterialButton> buttons;
    MaterialButtonToggleGroup optionsGroup;
    private String correctSaintName;

    private int correctChoice;
    private int correctAnswers = 0;
    private int wrongAnswers = 0;
    private boolean hasChecked = false;

    private TextView scoreView;
    PhotoView pictureView;
    private Painting questionPainting;
    private SharedPreferences sharedPreferences;

    private boolean autoNext;
    private static final Random ran = new Random();

    private int correctChoiceColor;
    private int wrongChoiceColor;
    private ColorStateList defaultTint;
    private ColorStateList colorStateCorrect;
    private ColorStateList colorStateWrong;

    private Toast correctAnswerToast;
    private Toast noAnswerToast;

    private static final String CORRECT_ANSWERS_KEY = "correct";
    private static final String WRONG_ANSWERS_KEY = "wrong";
    public static final String HAS_CHECKED_KEY = "guessed";
    public static final String BUTTON_NAMES = "buttonNames";
    private static final String PAINTING = "painting";
    private static final String CORRECT_CHOICE = "correctChoice";
    private static final String TINT = "tint";

    public static final int MIN_GUESSES_FOR_SCORE_UPDATE = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUp();

        if (saintIds.size() < 4 || unguessedPaintings.size() < 1) {
            return;
        }

        if (savedInstanceState == null) {
            setQuestion();
        }
    }

    private void setUp() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        autoNext = sharedPreferences.getBoolean("autoNext", false);


        // retrieve all saints
        Map<String, Map <Long, String>> allSaintsIdToNamesByCategory = SaintsQuery.getAllSaintsIdToNames(this.getApplicationContext());

        saintIdsToNamesFemale = allSaintsIdToNamesByCategory.get(FEMALE_KEY);
        saintIdsToNamesMale = allSaintsIdToNamesByCategory.get(MALE_KEY);
        saintIdsToNamesMagi = allSaintsIdToNamesByCategory.get(CATEGORY_MAGI_KEY);

        saintIds = new HashSet<>();

        saintIds.addAll(saintIdsToNamesFemale.keySet());
        saintIds.addAll(saintIdsToNamesMale.keySet());
        saintIds.addAll(saintIdsToNamesMagi.keySet());

        if (saintIds.size() < 4) {
            setContentView(R.layout.empty_db);
            return;
        }


        // retrieve unguessed paintings and set up reset if none left
        unguessedPaintings = PaintingsQuery.getAllUnguessedPaintings(this.getApplicationContext());

        if (unguessedPaintings.size() < 1) {
            setContentView(R.layout.guessed_all_paintings);

            Button resetPaintingsScore = findViewById(R.id.reset_paintings_score);
            resetPaintingsScore.setOnClickListener(
                    view -> {
                        DialogFragment resetConfirmationDialog = new ResetDbDialogFragment();
                        resetConfirmationDialog.show(GuessSaint.this.getFragmentManager(), TAG);
                    }
            );

            return;
        }


        // set up quiz view
        setContentView(R.layout.activity_guess);

        pictureView = findViewById(R.id.guessMergeImageView);
        scoreView = findViewById(R.id.guess_menu_score);
        optionsGroup = findViewById(R.id.optionsGroup);

        setUpColors();

        setUpButtons();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            defaultTint = getColorStateList(R.color.guess_button);
        } else {
            defaultTint = buttons.get(0).getBackgroundTintList();
        }

        Button guessActivityCheckButton = findViewById(R.id.guess_menu_next);
        guessActivityCheckButton.setOnClickListener(this::onSubmitChoice);

        final Button guessActivityBackButton = findViewById(R.id.guess_menu_back);
        guessActivityBackButton.setOnClickListener(view -> GuessSaint.this.onBackPressed());

    }

    private void setUpColors() {
        correctChoiceColor = getResources().getColor(R.color.awesome_green);
        wrongChoiceColor = getResources().getColor(R.color.bad_red);

        int[][] states = new int[][] {
                new int[] { android.R.attr.state_checked}, // enabled
                new int[] {-android.R.attr.state_checked}, // unchecked
        };

        int[] correct_colors = new int[] {
                correctChoiceColor,
                correctChoiceColor,
        };


        int[] wrong_colors = new int[] {
                wrongChoiceColor,
                wrongChoiceColor,
        };


        colorStateCorrect = new ColorStateList(states, correct_colors);
        colorStateWrong = new ColorStateList(states, wrong_colors);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(CORRECT_ANSWERS_KEY)
                && savedInstanceState.containsKey(WRONG_ANSWERS_KEY)) {
//            setUp();
            restoreState(savedInstanceState);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void restoreState(Bundle state) {
        correctAnswers = state.getInt(CORRECT_ANSWERS_KEY, 0);
        wrongAnswers = state.getInt(WRONG_ANSWERS_KEY, 0);
        setScore();

        questionPainting = (Painting) state.getSerializable(PAINTING);
        pictureView.setImageResource(questionPainting.getResourceName());

        hasChecked = state.getBoolean(HAS_CHECKED_KEY, false);

        HashMap<Integer, String> buttonNames = (HashMap<Integer, String>) state.getSerializable(BUTTON_NAMES);
        for (Map.Entry<Integer, String> e : buttonNames.entrySet()) {
            buttons.get(e.getKey()).setText(e.getValue());
        }
        clearAllButtons();

        correctChoice = state.getInt(CORRECT_CHOICE);
        correctSaintName = state.getString(CORRECT_SAINT_NAME);
        final int userChoice = state.getInt(USER_CHOICE, -1);
        if (userChoice != -1) {
            buttons.get(userChoice).setChecked(true);
            if (hasChecked) {
                if (userChoice != correctChoice) {
                    buttons.get(userChoice).setBackgroundTintList(colorStateWrong);
                }
                buttons.get(correctChoice).setBackgroundTintList(colorStateCorrect);
            }
        }

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(WRONG_ANSWERS_KEY, wrongAnswers);
        outState.putInt(CORRECT_ANSWERS_KEY, correctAnswers);
        outState.putBoolean(HAS_CHECKED_KEY, hasChecked);
        outState.putInt(CORRECT_CHOICE, correctChoice);
        outState.putString(CORRECT_SAINT_NAME, correctSaintName);
        outState.putInt(USER_CHOICE, getCheckedButtonId(optionsGroup.getCheckedButtonId()));
        outState.putSerializable(PAINTING, questionPainting);

        HashMap<Integer, String> buttonNames = new HashMap<>(4);
        for (int i = 0; i < buttons.size(); i++) {
            String name = buttons.get(i).getText().toString();
            buttonNames.put(i, name);
        }
        outState.putSerializable(BUTTON_NAMES, buttonNames);

        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (wrongAnswers + correctAnswers < MIN_GUESSES_FOR_SCORE_UPDATE) {
            return;
        }

        // save stats to preferences
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();

        // save score to prefs only if got higher
        float savedScore = sharedPreferences.getFloat("score", 0.0f);
        float currentScore = getScore();
        if (savedScore < currentScore) {
            prefEditor.putFloat("score", getScore());
        }

        prefEditor.apply();
    }

    private void setQuestion() {
        questionPainting = unguessedPaintings.get(ran.nextInt(unguessedPaintings.size()));

        final Saint correctSaint = SaintsQuery.getSaint(this, questionPainting.getSaintId());
        this.correctSaintName = correctSaint.getName();

        pictureView.setImageResource(questionPainting.getResourceName());

        correctChoice = ran.nextInt(buttons.size());

        // get suitable saints for the options
        ArrayList<Long> saintsListIds;

        if (correctSaint.getGender().equals(SaintsContract.GENDER_FEMALE)) {
            saintsListIds = new ArrayList<>(saintIdsToNamesFemale.keySet());
        } else if (CATEGORY_MAGI.equals(correctSaint.getCategory())) {
            saintsListIds = new ArrayList<>(saintIdsToNamesMagi.keySet());
        } else {
            saintsListIds = new ArrayList<>(saintIdsToNamesMale.keySet());
        }
        saintsListIds.remove(correctSaint.getId());

        // set up text on the buttons
        for (int i = 0; i < buttons.size(); i++) {
            MaterialButton button = buttons.get(i);
            if (i == correctChoice) {
                button.setText(correctSaint.getName());
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

            button.setText(name);
        }

        clearAllButtons();
    }

    public void onSubmitChoice(View view) {
        // if already checked correctness of the answer, render the next question
        if (hasChecked) {
            onNext();
            return;
        }

        final int userChoiceId = getCheckedButtonId(optionsGroup.getCheckedButtonId());

        // check whether an option was selected
        if (userChoiceId == View.NO_ID) {
            if (noAnswerToast != null) {
                noAnswerToast.cancel();
            }

            noAnswerToast = Toast.makeText(this, R.string.no_answer_toast_text, Toast.LENGTH_SHORT);
            noAnswerToast.show();

            return;
        }
        final boolean isCorrectAnswer = userChoiceId == correctChoice;

        PaintingsQuery.updateCorrectAnswersCount(this, questionPainting.getId(), isCorrectAnswer);

        if (isCorrectAnswer) {
            correctAnswers++;
            if (PaintingsQuery.isCountOverTreshold(this.getApplicationContext(), questionPainting.getId())) {
                unguessedPaintings.remove(new Painting (questionPainting.getId()));
            }
        } else {
            buttons.get(userChoiceId).setBackgroundTintList(colorStateWrong);
            wrongAnswers++;
        }

        buttons.get(correctChoice).setBackgroundTintList(colorStateCorrect);;

        setScore();

        if (autoNext) {
            if (correctAnswerToast != null) {
                correctAnswerToast.cancel();
            }
            final String message = isCorrectAnswer ?
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
        // TODO: add a feature with true auto next
//        MaterialButton.OnCheckedChangeListener onCheckedChangeListener = (button, isChecked) -> {
//            if (isChecked) {
//                clearAllButtons();
//                button.setChecked(true);
//            }
//        };

        buttons = new ArrayList<>(4);

        setUpButton(R.id.guess_button1);
        setUpButton(R.id.guess_button2);
        setUpButton(R.id.guess_button3);
        setUpButton(R.id.guess_button4);

    }

    private void setUpButton(int guess_button_id) {
        MaterialButton guessButton = findViewById(guess_button_id);
        buttons.add(guessButton);
    }

    private void clearAllButtons() {
        optionsGroup.clearChecked();
        for (MaterialButton button : buttons) {
            button.setBackgroundTintList(defaultTint);
        }

    }

    private int getCheckedButtonId(int checkedButtonId) {
        for (int i = 0; i < buttons.size(); i++) {
            MaterialButton button = buttons.get(i);
            if (button.getId() == checkedButtonId) {
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

    @Override
    public void onDialogPositiveClick(ResetDbDialogFragment dialog) {
        PaintingsQuery.reset_counters(getApplicationContext());
        finish();
        startActivity(getIntent());
        Toast.makeText(this, R.string.reset_db_done, Toast.LENGTH_SHORT).show();
    }

}
