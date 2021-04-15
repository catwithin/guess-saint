package com.gamesofni.neko.guesswhichsaint.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.gamesofni.neko.guesswhichsaint.R;
import com.gamesofni.neko.guesswhichsaint.db.DbAccess;
import com.gamesofni.neko.guesswhichsaint.db.PaintingsQuery;
import com.gamesofni.neko.guesswhichsaint.view.UnlockedDrawingCursorAdapter;

public class UnlockedDrawingsList extends AppCompatActivity {
    private static final String TAG = SaintsList.class.getSimpleName();
    private Cursor cursor;
    private SQLiteDatabase db;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = DbAccess.getDatabaseInstance(this);
        cursor = PaintingsQuery.getAllUnlockedPaintings(this, db);

        if (cursor.getCount() < 1) {
            setContentView(R.layout.no_unlocked_paintings);
            Button guessActivity = findViewById(R.id.guessActivityButton);
            guessActivity.setOnClickListener(
                    view -> startActivity(new Intent(this, GuessSaint.class))
            );
            return;
        }

        setContentView(R.layout.activity_list_paintings);

        ListView paintingsList = findViewById(R.id.activity_list_paintings);
        UnlockedDrawingCursorAdapter drawingsCursorAdapter = new UnlockedDrawingCursorAdapter(this, cursor);
        paintingsList.setAdapter(drawingsCursorAdapter);
        paintingsList.setOnItemClickListener((parent, view, position, id) -> onCursorItemListCLick(id));

        getWindow().setBackgroundDrawable(null); // reduces overdraw

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            cursor.close();
            db.close();
        } catch (Exception e) {
            Log.e(TAG, "Error when trying to close db: ", e);
        }
    }

    private void onCursorItemListCLick(long paintingId) {
        Intent intent = new Intent(UnlockedDrawingsList.this, SaintInfo.class);
        intent.putExtra(Intent.EXTRA_UID, paintingId);
        startActivity(intent);
    }
}
