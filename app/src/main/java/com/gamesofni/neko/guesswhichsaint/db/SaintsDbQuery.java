package com.gamesofni.neko.guesswhichsaint.db;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.text.TextUtils;
import com.gamesofni.neko.guesswhichsaint.data.Saint;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

    public Saint getSaint(Context context, long id) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(SaintsContract.SaintEntry.TABLE_NAME + " AS s " +
            " INNER JOIN " + PaintingsContract.PaintingsEntry.TABLE_NAME + " AS p " +
                " ON s." + SaintsContract.SaintEntry._ID + "=p." + PaintingsContract.PaintingsEntry.SAINT_ID +
            " INNER JOIN " + context.getString(SaintsContract.SaintTranslation.TABLE_NAME) + " AS tr " +
                " ON s." + SaintsContract.SaintEntry._ID + "=tr." + SaintsContract.SaintTranslation.TRANSLATION_ID);

        final String[] projection = {
                SaintsContract.SaintEntry._ID,
                SaintsContract.SaintTranslation.NAME,
                PaintingsContract.PaintingsEntry.FILE_NAME,
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
        Cursor cursor = queryBuilder.query(
                getDb(context),
                projection,
                selection,
                selectionArgs,
                null, null, null
        );
        return convertSaintFromCursor(cursor, context);
    }

    private static Saint convertSaintFromCursor(Cursor cursor, Context context) {
        try {
            if (cursor.moveToFirst()) {
                return convertSaintFromCursorOnPosition(cursor, context);
            } else {
                return null;
            }
        } finally {
            cursor.close();
        }
    }

    public static Saint convertSaintFromCursorOnPosition(Cursor cursor, Context context) {

        final int idColumnIndex = cursor.getColumnIndex(SaintsContract.SaintEntry._ID);
        final int nameColumnIndex = cursor.getColumnIndex(SaintsContract.SaintTranslation.NAME);
        final int paintingNameColumnIndex = cursor.getColumnIndex(PaintingsContract.PaintingsEntry.FILE_NAME);
        final int attributesColumnIndex = cursor.getColumnIndex(SaintsContract.SaintTranslation.ATTRIBUTES);
        final int iconColumnIndex = cursor.getColumnIndex(SaintsContract.SaintEntry.ICON);
        final int descriptionColumnIndex = cursor.getColumnIndex(SaintsContract.SaintTranslation.DESCRIPTION);
        final int wikiLinkColumnIndex = cursor.getColumnIndex(SaintsContract.SaintTranslation.WIKI_LINK);
        final int genderColumnIndex = cursor.getColumnIndex(SaintsContract.SaintEntry.GENDER);
        final int categoryColumnIndex = cursor.getColumnIndex(SaintsContract.SaintEntry.CATEGORY);

        Saint saint = new Saint(
                (idColumnIndex != -1) ? cursor.getLong(idColumnIndex) : -1,
                (nameColumnIndex != -1) ? cursor.getString(nameColumnIndex) : null,
                (paintingNameColumnIndex != -1) ? Collections.singletonList(getPainting(paintingNameColumnIndex, cursor, context)) : new ArrayList<Integer>(),
                // TODO: stub, save attr in separate table
                (attributesColumnIndex != -1) ?
                        new ArrayList<>(Arrays.asList(TextUtils.split(cursor.getString(attributesColumnIndex), ","))) :
                        null,
                (iconColumnIndex != -1) ?
                        context.getResources().getIdentifier(cursor.getString(iconColumnIndex), "drawable", context.getPackageName()) :
                        -1,
                (descriptionColumnIndex != -1) ? cursor.getString(descriptionColumnIndex) : null,
                (wikiLinkColumnIndex != -1) ? cursor.getString(wikiLinkColumnIndex) : null,
                (genderColumnIndex != -1) ? cursor.getInt(genderColumnIndex) : null,
                (categoryColumnIndex != -1) ? cursor.getString(categoryColumnIndex) : null
        );

        while (cursor.moveToNext()) {
            saint.getPaintings().add(getPainting(paintingNameColumnIndex, cursor, context));
        }

        return saint;
    }

    private static Integer getPainting(int paintingNameColumnIndex, Cursor cursor, Context context) {
        if (paintingNameColumnIndex == -1) {
            return null;
        }
        final String paintingName = cursor.getString(paintingNameColumnIndex);
        return context.getResources().getIdentifier(paintingName , "drawable", context.getPackageName());
    }
}
