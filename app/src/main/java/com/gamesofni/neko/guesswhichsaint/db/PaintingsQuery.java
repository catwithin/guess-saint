package com.gamesofni.neko.guesswhichsaint.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class PaintingsQuery {


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

}
