package com.gamesofni.neko.guesswhichsaint.db;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.HashMap;
import java.util.Map;
import static com.gamesofni.neko.guesswhichsaint.db.SaintsContract.CATEGORY_MAGI;
import static com.gamesofni.neko.guesswhichsaint.db.SaintsContract.GENDER_MALE;


public class SaintsDbQuery {

    public static final String CATEGORY_MAGI_KEY = "magi";
    public static final String MALE_KEY = "male";
    public static final String FEMALE_KEY = "female";

    public SaintsDbQuery() {}

    private SaintsDbHelper saintsDbHelper;

    private synchronized SaintsDbHelper getSaintsDbHelper(Context context)
    {
        if (saintsDbHelper == null)
            saintsDbHelper = new SaintsDbHelper(context);
        return saintsDbHelper;
    }

    private static final String JOIN_TRANSLATION_CONDITION = SaintsContract.SaintEntry._ID + " = " + SaintsContract.SaintTranslation.TRANSLATION_ID;

    private static String saintsJoinedTranslationTable(Context context) {
        return SaintsContract.SaintEntry.TABLE_NAME + " , " + context.getString(SaintsContract.SaintTranslation.TABLE_NAME);
    }

    public Cursor getAllSaintsWithIcons(Context context) {
        String[] projection = {
            SaintsContract.SaintEntry._ID,
            SaintsContract.SaintEntry.ICON,
            SaintsContract.SaintTranslation.NAME,
            SaintsContract.SaintTranslation.ATTRIBUTES
        };
        String sortOrder = SaintsContract.SaintTranslation.NAME + " ASC";

        final String filterNoneOfTheAbove = " NOT " + SaintsContract.SaintEntry._ID + "=" + String.valueOf(0);

        // TODO: do in another thread
        return getDb(context).query(
            saintsJoinedTranslationTable(context),
            projection,
            JOIN_TRANSLATION_CONDITION + " AND " + filterNoneOfTheAbove,
            null, null, null,
            sortOrder
        );
    }

    private Cursor queryAllSaintsWithCategories(Context context) {
        String[] projection = {
            SaintsContract.SaintEntry._ID,
            SaintsContract.SaintTranslation.NAME,
            SaintsContract.SaintEntry.GENDER,
            SaintsContract.SaintEntry.CATEGORY
        };

        // TODO: do in another thread
        return getDb(context).query(
            saintsJoinedTranslationTable(context),
            projection,
            JOIN_TRANSLATION_CONDITION,
            null, null, null, null
        );
    }

    public Map<String, Map<Long, String>> getAllSaintsIdToNames(Context context) {
        Cursor cursor =  queryAllSaintsWithCategories(context);

        HashMap<Long, String> saintIdsToNamesMale = new HashMap<>();
        HashMap<Long, String> saintIdsToNamesFemale = new HashMap<>();
        HashMap<Long, String> saintIdsToNamesMagi = new HashMap<>();
        Map<String, Map<Long, String>> allSaintsGrouped = new HashMap<>();

        try {
            while (cursor.moveToNext()) {
                long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(SaintsContract.SaintEntry._ID));
                String saintName = cursor.getString(cursor.getColumnIndexOrThrow(SaintsContract.SaintTranslation.NAME));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(SaintsContract.SaintEntry.CATEGORY));

                if (CATEGORY_MAGI.equals(category)) {
                    saintIdsToNamesMagi.put(itemId, saintName);
                    continue;
                }

                final int gender = cursor.getInt(cursor.getColumnIndexOrThrow(SaintsContract.SaintEntry.GENDER));
                if (gender == GENDER_MALE) {
                    saintIdsToNamesMale.put(itemId, saintName);
                    continue;
                }

                saintIdsToNamesFemale.put(itemId, saintName);
            }
        } finally {
            cursor.close();
        }

        allSaintsGrouped.put(FEMALE_KEY, saintIdsToNamesFemale);
        allSaintsGrouped.put(MALE_KEY, saintIdsToNamesMale);
        allSaintsGrouped.put(CATEGORY_MAGI_KEY, saintIdsToNamesMagi);

        return allSaintsGrouped;
    }

    private SQLiteDatabase getDb(Context context) {
        return getSaintsDbHelper(context).getReadableDatabase();
    }

    public Cursor getSaint(Context context, long id) {
        final String[] projection = {
                SaintsContract.SaintEntry._ID,
                SaintsContract.SaintTranslation.NAME,
                SaintsContract.SaintEntry.PAINTINGS,
                SaintsContract.SaintTranslation.ATTRIBUTES,
                SaintsContract.SaintEntry.ICON,
                SaintsContract.SaintTranslation.DESCRIPTION,
                SaintsContract.SaintTranslation.WIKI_LINK,
                SaintsContract.SaintEntry.GENDER,
                SaintsContract.SaintEntry.CATEGORY
        };
        final String selection = SaintsContract.SaintEntry._ID + " = ?";
        final String[] selectionArgs = { String.valueOf(id) };
        // TODO: do in another thread
        return getDb(context).query(
                saintsJoinedTranslationTable(context),
                projection,
                JOIN_TRANSLATION_CONDITION + " AND " + selection,
                selectionArgs,
                null, null, null
        );
    }

}
