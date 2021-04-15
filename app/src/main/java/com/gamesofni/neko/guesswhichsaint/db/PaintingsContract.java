package com.gamesofni.neko.guesswhichsaint.db;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;

import com.gamesofni.neko.guesswhichsaint.data.Painting;


public class PaintingsContract {
    private PaintingsContract() {}

    static class PaintingsEntry implements BaseColumns {
        static final String TABLE_NAME = "paintings";
        public final static String _ID = BaseColumns._ID;
        public static final String SAINT_ID = "saintId";
        public static final String FILE_NAME = "fileName"; // drawables file name
        public static final String NAME = "name";
        public static final String AUTHOR = "author";
        public static final String WIKI_LINK = "wikiLink";
        public static final String EXPLANATION = "explanation";
        public static final String COUNT = "correctCount";
    }

    static Painting convertPaintingFromSaintJoinedCursor(Cursor cursor, Context context) {
        return convertPaintingFromCursor(cursor, context, "p" + PaintingsEntry._ID);
    }

    static Painting convertPaintingFromPaintingCursor(Cursor cursor, Context context) {
        return convertPaintingFromCursor(cursor, context, PaintingsEntry._ID);
    }

    private static Painting convertPaintingFromCursor(Cursor cursor, Context context, String idColumnName) {
        final int paintingFileNameColumnIndex = cursor.getColumnIndex(PaintingsEntry.FILE_NAME);
        final int paintingCountColumnIndex = cursor.getColumnIndex(PaintingsEntry.COUNT);
        if (paintingFileNameColumnIndex == -1 || paintingCountColumnIndex == -1) {
            return null;
        }
        final String paintingFileName = cursor.getString(paintingFileNameColumnIndex);
        final Integer fileIdentifier = context.getResources().getIdentifier(paintingFileName , "drawable", context.getPackageName());
        final Integer correctCount = cursor.getInt(paintingCountColumnIndex);
        final Long id = cursor.getLong(cursor.getColumnIndex(idColumnName));

        final int saintIdColumnIndex = cursor.getColumnIndex(PaintingsEntry.SAINT_ID);
        if (saintIdColumnIndex == -1) {
            return new Painting(id, fileIdentifier, correctCount);
        } else {
            final long saintId = cursor.getLong(saintIdColumnIndex);
            return new Painting(id, fileIdentifier, correctCount, saintId);
        }
    }

    public static Painting unlockedPaintingFromCursor(Cursor cursor, Context context) {
        final int idColumnIndex = cursor.getColumnIndex(PaintingsEntry._ID);
        final int paintingFileNameColumnIndex = cursor.getColumnIndex(PaintingsEntry.FILE_NAME);
        final int nameColumnIndex = cursor.getColumnIndex(PaintingsEntry.NAME);
        final String paintingFileName = cursor.getString(paintingFileNameColumnIndex);
        final Integer fileIdentifier = context.getResources().getIdentifier(paintingFileName , "drawable", context.getPackageName());
        final String name = cursor.getString(nameColumnIndex);
        final Long id = cursor.getLong(idColumnIndex);

        return new Painting(id, name, fileIdentifier);
    }
}
