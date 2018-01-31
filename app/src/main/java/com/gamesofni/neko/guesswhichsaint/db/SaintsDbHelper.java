package com.gamesofni.neko.guesswhichsaint.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;


public class SaintsDbHelper extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "saints.db";
    private static final int DATABASE_VERSION = 21;

    private static final String SQL_DROP_SAINTS =
            "DROP TABLE IF EXISTS saints; ";
    private static final String SQL_DROP_SAINTS_DEFAULT =
            "DROP TABLE IF EXISTS saints_default; ";
    private static final String SQL_DROP_SAINTS_RU =
            "DROP TABLE IF EXISTS saints_ru; ";
    private static final String SQL_DROP_PAINTINGS =
            "DROP TABLE IF EXISTS " + PaintingsContract.PaintingsEntry.TABLE_NAME + "; ";

    private static final String TAG = SaintsDbHelper.class.getSimpleName();

    SaintsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setForcedUpgrade();
    }

    // db uploads from assets in createOrOpenDatabase()
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.v(TAG, "Dropping all tables for update");
        db.execSQL(SQL_DROP_SAINTS);
        db.execSQL(SQL_DROP_SAINTS_DEFAULT);
        db.execSQL(SQL_DROP_SAINTS_RU);
        db.execSQL(SQL_DROP_PAINTINGS);
    }

}
