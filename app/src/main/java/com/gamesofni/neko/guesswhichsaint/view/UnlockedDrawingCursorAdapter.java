package com.gamesofni.neko.guesswhichsaint.view;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gamesofni.neko.guesswhichsaint.R;
import com.gamesofni.neko.guesswhichsaint.data.Painting;

import static com.gamesofni.neko.guesswhichsaint.db.PaintingsContract.unlockedPaintingFromCursor;


public class UnlockedDrawingCursorAdapter extends CursorAdapter {
    public UnlockedDrawingCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_drawing, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView name = view.findViewById(R.id.list_drawing_name);
        ImageView preview = view.findViewById(R.id.drawing_preview);

        final Painting painting = unlockedPaintingFromCursor(cursor, context);
        name.setText(painting.getName());
        preview.setImageResource(painting.getResourceName());
    }
}