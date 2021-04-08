package com.gamesofni.neko.guesswhichsaint.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.gamesofni.neko.guesswhichsaint.R;

import static com.gamesofni.neko.guesswhichsaint.utils.Utils.checkSupportedAction;
import static com.gamesofni.neko.guesswhichsaint.utils.Utils.showNoAppClientsMsg;


public class Main extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Button guessActivityCard = findViewById(R.id.mainScreenGuessActivityButton);
        guessActivityCard.setOnClickListener(
                view -> startActivity(new Intent(Main.this, GuessSaint.class))
        );

        Button saintsListActivityCard = findViewById(R.id.mainScreenSaintsListActivity);
        saintsListActivityCard .setOnClickListener(
                view -> startActivity(new Intent(Main.this, SaintsList.class))
        );

    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        float savedScore = sharedPreferences.getFloat("score", 0.0f);
        TextView scoreValue = findViewById(R.id.mainScreenGuessSaintsScoreValue);
//        scoreValue.setText(String.format(getString(R.string.score_main_screen), savedScore));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, Settings.class));
            return true;
        }

        if (id == R.id.action_feedback) {
            sendFeedbackEmail(findViewById(id));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendFeedbackEmail(View view) {
        Intent emailViewIntent = new Intent(Intent.ACTION_SENDTO);
        emailViewIntent.setData(Uri.parse("mailto:"));
        emailViewIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"sadnessexports@gmail.com"});
        emailViewIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_request_subject));
        emailViewIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.feedback_request_text));

        checkSupportedAction(this, view, emailViewIntent, getString(R.string.unsupported_action_name_email));

        try {
            startActivity(Intent.createChooser(emailViewIntent, null));
        } catch (android.content.ActivityNotFoundException ex) {
            showNoAppClientsMsg(this, view, getString(R.string.unsupported_action_name_email));
        }
    }
}
