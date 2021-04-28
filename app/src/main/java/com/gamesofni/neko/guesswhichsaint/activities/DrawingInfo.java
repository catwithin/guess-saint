package com.gamesofni.neko.guesswhichsaint.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.gamesofni.neko.guesswhichsaint.R;
import com.gamesofni.neko.guesswhichsaint.data.Painting;
import com.gamesofni.neko.guesswhichsaint.db.PaintingsQuery;


public class DrawingInfo extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing_detail);

        Intent intentThatStartedThisActivity = getIntent();
        if (!intentThatStartedThisActivity.hasExtra(Intent.EXTRA_UID)) {
            return;
        }

        final long drawingId = intentThatStartedThisActivity.getLongExtra(Intent.EXTRA_UID, -1L);
        if (drawingId == -1L) {
            return;
        }

        TextView drawing_name = findViewById(R.id.drawing_name);
        TextView drawing_description = findViewById(R.id.drawing_description);
        ImageView drawing_pic = findViewById(R.id.guessMergeImageView);

        Painting painting = PaintingsQuery.getPainting(this, drawingId);

        if (painting == null) {
            return;
        }

        drawing_name.setText(painting.getName());
        // TODO: add descriptions
        drawing_description.setText(painting.getName());
        drawing_pic.setImageResource(painting.getResourceName());

    }

}
