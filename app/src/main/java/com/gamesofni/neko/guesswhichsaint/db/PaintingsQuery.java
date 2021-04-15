package com.gamesofni.neko.guesswhichsaint.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.gamesofni.neko.guesswhichsaint.data.Painting;
import com.gamesofni.neko.guesswhichsaint.data.Saint;

import java.util.ArrayList;
import java.util.Arrays;


public class PaintingsQuery {


    private static final int CORRECT_COUNT_THRESHOLD = 2;
    private static final String TAG = PaintingsQuery.class.getSimpleName();

    public static void updateCorrectAnswersCount(Context context, long id, boolean isCorrectAnswer) {

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
            // we want to check later for CORRECT_COUNT_THRESHOLD straight answers in a row
            // => increment counter on correct answer, reset to 0 on incorrect
            cv.put(
                    PaintingsContract.PaintingsEntry.COUNT,
                    isCorrectAnswer ? currentCorrectCount + 1 : 0
            );

            // put updated correct count
            db.update(
                    PaintingsContract.PaintingsEntry.TABLE_NAME,
                    cv,
                    PaintingsContract.PaintingsEntry._ID + "=?",
                    new String[]{String.valueOf(id)}
            );

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            DbAccess.getDbAccess(context).closeDatabase();
        }

    }

    public static ArrayList<Painting> getAllUnguessedPaintings(Context context) {
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

    public static boolean isCountOverTreshold(Context context, long paintingId) {
        Painting p = getPainting(context, paintingId);
        Log.d(TAG, "Guessed correctly painting " + p.toString());
        return p.getCorrectCount() >= CORRECT_COUNT_THRESHOLD;
    }

    private static Cursor queryAllPaintingsWithLowScore(Context context) {
        // return paintings for quiz which were guessed correctly less than 2 times in a row
        String[] projection = {
                PaintingsContract.PaintingsEntry._ID,
                PaintingsContract.PaintingsEntry.FILE_NAME,
                PaintingsContract.PaintingsEntry.SAINT_ID,
                PaintingsContract.PaintingsEntry.COUNT
        };

        final String selection = PaintingsContract.PaintingsEntry.COUNT + " < ?";
        final String[] selectionArgs = {String.valueOf(CORRECT_COUNT_THRESHOLD)};

        return DbAccess.getDbAccess(context).getDatabase().query(
                PaintingsContract.PaintingsEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null, null, null
        );
    }

    public static Painting getPaintingForSaint(Context context, long id) {
        String[] projection = {
                PaintingsContract.PaintingsEntry._ID,
                PaintingsContract.PaintingsEntry.FILE_NAME,
                PaintingsContract.PaintingsEntry.SAINT_ID,
                PaintingsContract.PaintingsEntry.COUNT
        };

        final String selection = PaintingsContract.PaintingsEntry.SAINT_ID + " = ?";
        final String[] selectionArgs = {String.valueOf(id)};

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

    private static Painting getPainting(Context context, long id) {
        String[] projection = {
                PaintingsContract.PaintingsEntry._ID,
                PaintingsContract.PaintingsEntry.FILE_NAME,
                PaintingsContract.PaintingsEntry.SAINT_ID,
                PaintingsContract.PaintingsEntry.COUNT
        };

        final String selection = PaintingsContract.PaintingsEntry._ID + " = ?";
        final String[] selectionArgs = {String.valueOf(id)};

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

    public static void reset_counters(Context context) {
        SQLiteDatabase db = DbAccess.getDbAccess(context).getDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put(PaintingsContract.PaintingsEntry.COUNT, 0);

            db.update(
                    PaintingsContract.PaintingsEntry.TABLE_NAME,
                    cv,
                    null,
                    null
            );

        } finally {
            DbAccess.getDbAccess(context).closeDatabase();
        }
    }


}
