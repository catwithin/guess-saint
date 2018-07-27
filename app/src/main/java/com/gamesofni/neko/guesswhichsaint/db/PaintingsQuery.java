package com.gamesofni.neko.guesswhichsaint.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.gamesofni.neko.guesswhichsaint.data.Painting;

import java.util.ArrayList;

public class PaintingsQuery {


    private static final int CORRECT_COUNT_TRESHOLD = 2;
    private static final String TAG = PaintingsQuery.class.getSimpleName();

    public void updateCorrectAnswersCount(Context context, long id, boolean isCorrectAnswer) {

        // TODO: do in another thread
        SQLiteDatabase db = DbAccess.getDbAccess(context).getDatabase();
        db.beginTransaction();

        try {
            // get current count
            Cursor cursor = db.query(PaintingsContract.PaintingsEntry.TABLE_NAME,
                    new String[]{PaintingsContract.PaintingsEntry.COUNT},
                    PaintingsContract.PaintingsEntry._ID + "=?",
                    new String[]{String.valueOf(id)},
                    null, null, null, null);
            final int currentCorrectCount = cursor.moveToFirst() ? cursor.getInt(0) : 0;

            cursor.close();

            ContentValues cv = new ContentValues();
            // we want to check later for 3 straight answers in a row => increment counter on correct answer, reset to 0 on incorrect
            cv.put(PaintingsContract.PaintingsEntry.COUNT, isCorrectAnswer ? currentCorrectCount + 1 : 0);

            // put updated correct count
            db.update(PaintingsContract.PaintingsEntry.TABLE_NAME, cv, PaintingsContract.PaintingsEntry._ID + "=?", new String[]{String.valueOf(id)});

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            DbAccess.getDbAccess(context).closeDatabase();
        }

    }

    public ArrayList<Painting> getAllUnguessedPaintings(Context context) {
        ArrayList<Painting> unguessedPaintings = new ArrayList<>();

        Cursor cursor =  queryAllPaintingsWithLowScore(context);
        try {
            while (cursor.moveToNext()) {
                unguessedPaintings.add(PaintingsContract.convertPaintingFromPaintingCursor(cursor, context));
            }
        } finally {
            cursor.close();
            DbAccess.getDbAccess(context).closeDatabase();
        }

        return unguessedPaintings;
    }

    public boolean isCountOverTreshold(Context context, long paintingId) {
        Painting p = getPainting(context, paintingId);
        Log.d(TAG, "Guessed correctly painting " + p.toString());
        return p.getCorrectCount() >= CORRECT_COUNT_TRESHOLD;
    }

    private Cursor queryAllPaintingsWithLowScore(Context context) {
        // return paintings for quiz which were guessed correctly less than 2 times in a row
        String[] projection = {
                PaintingsContract.PaintingsEntry._ID,
                PaintingsContract.PaintingsEntry.FILE_NAME,
                PaintingsContract.PaintingsEntry.SAINT_ID,
                PaintingsContract.PaintingsEntry.COUNT
        };

        final String selection = PaintingsContract.PaintingsEntry.COUNT + " < ?";
        final String[] selectionArgs = {String.valueOf(CORRECT_COUNT_TRESHOLD)};

        // TODO: do in another thread
        return DbAccess.getDbAccess(context).getDatabase().query(
                PaintingsContract.PaintingsEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null, null, null
        );
    }

    private Painting getPainting(Context context, long id) {
        String[] projection = {
                PaintingsContract.PaintingsEntry._ID,
                PaintingsContract.PaintingsEntry.FILE_NAME,
                PaintingsContract.PaintingsEntry.SAINT_ID,
                PaintingsContract.PaintingsEntry.COUNT
        };

        final String selection = PaintingsContract.PaintingsEntry._ID + " = ?";
        final String[] selectionArgs = {String.valueOf(id)};

        // TODO: do in another thread
        Cursor cursor = DbAccess.getDbAccess(context).getDatabase().query(
                PaintingsContract.PaintingsEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null, null, null
        );

        try {
            if (cursor.moveToFirst()) {
                return PaintingsContract.convertPaintingFromPaintingCursor(cursor, context);
            } else {
                return null;
            }
        } finally {
            cursor.close();
            DbAccess.getDbAccess(context).closeDatabase();
        }
    }

}
