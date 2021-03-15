package com.gamesofni.neko.guesswhichsaint.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import com.gamesofni.neko.guesswhichsaint.R;
import com.gamesofni.neko.guesswhichsaint.db.PaintingsQuery;


public class Settings extends AppCompatActivity implements ResetDbDialogFragment.ResetDbDialogListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }


    @Override
    public void onDialogPositiveClick(ResetDbDialogFragment dialog) {
        PaintingsQuery.reset_counters(this.getApplicationContext());
        Toast.makeText(this.getApplicationContext(), R.string.reset_db_done, Toast.LENGTH_SHORT).show();
    }
}
