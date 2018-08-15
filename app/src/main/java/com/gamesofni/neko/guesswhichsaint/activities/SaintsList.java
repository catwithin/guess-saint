package com.gamesofni.neko.guesswhichsaint.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.gamesofni.neko.guesswhichsaint.R;
import com.gamesofni.neko.guesswhichsaint.db.DbAccess;
import com.gamesofni.neko.guesswhichsaint.db.SaintsQuery;
import com.gamesofni.neko.guesswhichsaint.view.SaintsCursorAdapter;


public class SaintsList extends AppCompatActivity {

    private static final String TAG = SaintsList.class.getSimpleName();
    private Cursor cursor;
    private SQLiteDatabase db;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = DbAccess.getDatabaseInstance(this);
        cursor = SaintsQuery.getAllSaintsWithIcons(this, db);

        if (cursor.getCount() < 1) {
            setContentView(R.layout.empty_db);
            return;
        }

        setContentView(R.layout.activity_list_saints);

        ListView saintsList = findViewById(R.id.activity_list_saints);
        SaintsCursorAdapter saintsCursorAdapter = new SaintsCursorAdapter(this, cursor);
        saintsList.setAdapter(saintsCursorAdapter);
        saintsList.setOnItemClickListener((parent, view, position, id) -> onCursorItemListCLick(id));

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

    private void onCursorItemListCLick(long saintId) {
        Intent intent = new Intent(SaintsList.this, SaintInfo.class);
        intent.putExtra(Intent.EXTRA_UID, saintId);
        startActivity(intent);
    }

}
