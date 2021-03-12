package com.gamesofni.neko.guesswhichsaint.db;

import android.content.Context;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;


// TODO: Take a look at Android Room db lib
class SaintsDbHelper extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "saints.db";
    private static final int DATABASE_VERSION = 30;

    // 28 is the last version which just copies db from assets
    // from this version on user data is stored in the paintings table
    // thus from 29 version on upgrade scripts are used for db upgrades
    private static final int LAST_FORCED_UPGRADE_VERSION = 28;

    SaintsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setForcedUpgrade(LAST_FORCED_UPGRADE_VERSION);
    }

}
