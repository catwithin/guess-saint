package com.gamesofni.neko.guesswhichsaint.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbAccess {

    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static DbAccess dbAccess;

    private static final String TAG = DbAccess.class.getSimpleName();

    private DbAccess(Context context) {
        this.openHelper = new SaintsDbHelper(context);
    }

    public static DbAccess getDbAccess(Context context) {
        if (dbAccess == null) {
            dbAccess = new DbAccess(context);
        }
        return dbAccess;
    }

    SQLiteDatabase getDatabase() {
        openDatabase();
        return database;
    }

    // hack for cursor adapter list
    public static SQLiteDatabase getDatabaseInstance(Context context) {
        return new SaintsDbHelper(context).getWritableDatabase();
    }

    private void openDatabase() {
        if (database != null) {
            android.util.Log.w(TAG, "DB was opened before and it seems it wasn't properly closed");
        }
        this.database = openHelper.getWritableDatabase();
    }

    public void closeDatabase() {
        if (database != null) {
            this.database.close();
            this.database = null;
        }
    }
}
