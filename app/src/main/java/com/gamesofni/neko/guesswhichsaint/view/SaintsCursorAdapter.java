package com.gamesofni.neko.guesswhichsaint.view;


import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gamesofni.neko.guesswhichsaint.R.layout;
import com.gamesofni.neko.guesswhichsaint.R.id;
import com.gamesofni.neko.guesswhichsaint.data.Saint;

import static com.gamesofni.neko.guesswhichsaint.db.SaintsQuery.convertSaintFromCursorOnPosition;

public class SaintsCursorAdapter extends CursorAdapter{
// TODO: when coming bk from list item info view the list is on top, better would be on same position - only for our button, hardware back works great
    // TODO: deprecated, "As an alternative, use android.app.LoaderManager with a android.content.CursorLoader"
    public SaintsCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(layout.saints_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView name = view.findViewById(id.list_saint_name);
        TextView attributes = view.findViewById(id.list_saint_attributes);
        ImageView icon = view.findViewById(id.list_saint_icon);

        final Saint saint = convertSaintFromCursorOnPosition(cursor, context);
        name.setText(saint.getName());
        attributes.setText(TextUtils.join(",", saint.getAttributes()));
        icon.setImageResource(saint.getIcon());
    }
}
