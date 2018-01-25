package com.gamesofni.neko.guesswhichsaint.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.gamesofni.neko.guesswhichsaint.R;
import com.gamesofni.neko.guesswhichsaint.db.SaintsDbQuery;
import com.gamesofni.neko.guesswhichsaint.view.SaintsCursorAdapter;


public class SaintsList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SaintsDbQuery db = new SaintsDbQuery();
        Cursor cursor = db.getAllSaintsWithIcons(this);

        if (cursor.getCount() < 1) {
            setContentView(R.layout.empty_db);
            return;
        }

        setContentView(R.layout.activity_list_saints);

        ListView saintsList = findViewById(R.id.activity_list_saints);
        SaintsCursorAdapter saintsCursorAdapter = new SaintsCursorAdapter(this, cursor);
        saintsList.setAdapter(saintsCursorAdapter);
        saintsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onCursorItemListCLick(id);
            }
        });

        getWindow().setBackgroundDrawable(null); // reduces overdraw

    }

    private void onCursorItemListCLick(long saintId) {
        Intent intent = new Intent(SaintsList.this, SaintInfo.class);
        intent.putExtra(Intent.EXTRA_UID, saintId);
        startActivity(intent);
    }

}
