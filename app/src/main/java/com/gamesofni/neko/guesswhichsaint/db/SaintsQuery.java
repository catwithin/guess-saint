package com.gamesofni.neko.guesswhichsaint.db;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.text.TextUtils;

import com.gamesofni.neko.guesswhichsaint.data.Painting;
import com.gamesofni.neko.guesswhichsaint.data.Saint;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import static com.gamesofni.neko.guesswhichsaint.db.SaintsContract.CATEGORY_MAGI;
import static com.gamesofni.neko.guesswhichsaint.db.SaintsContract.GENDER_MALE;


public class SaintsQuery {

    public static final String CATEGORY_MAGI_KEY = "magi";
    public static final String MALE_KEY = "male";
    public static final String FEMALE_KEY = "female";


    private static final String JOIN_TRANSLATION_CONDITION = SaintsContract.SaintEntry._ID + " = " + SaintsContract.SaintTranslation.TRANSLATION_ID;

    private static String saintsJoinedTranslationTable(Context context) {
        return SaintsContract.SaintEntry.TABLE_NAME + " , " + context.getString(SaintsContract.SaintTranslation.TABLE_NAME);
    }

    public static Cursor getAllSaintsWithIcons(Context context, SQLiteDatabase db) {
        String[] projection = {
            SaintsContract.SaintEntry._ID,
            SaintsContract.SaintEntry.ICON,
            SaintsContract.SaintTranslation.NAME,
            SaintsContract.SaintTranslation.ATTRIBUTES
        };
        String sortOrder = SaintsContract.SaintTranslation.NAME + " ASC";

        final String filterNoneOfTheAboveMagi = " NOT " + SaintsContract.SaintEntry._ID + "=" + String.valueOf(0);
        return db.query(
            saintsJoinedTranslationTable(context),
            projection,
            JOIN_TRANSLATION_CONDITION + " AND " + filterNoneOfTheAboveMagi,
            null, null, null,
            sortOrder
        );
    }

    private static Cursor queryAllSaintsWithCategories(Context context) {
        String[] projection = {
            SaintsContract.SaintEntry._ID,
            SaintsContract.SaintTranslation.NAME,
            SaintsContract.SaintEntry.GENDER,
            SaintsContract.SaintEntry.CATEGORY
        };

        return DbAccess.getDbAccess(context).getDatabase().query(
            saintsJoinedTranslationTable(context),
            projection,
            JOIN_TRANSLATION_CONDITION,
            null, null, null, null
        );
    }

    public static Map<String, Map<Long, String>> getAllSaintsIdToNames(Context context) {
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

        DbAccess.getDbAccess(context).closeDatabase();

        allSaintsGrouped.put(FEMALE_KEY, saintIdsToNamesFemale);
        allSaintsGrouped.put(MALE_KEY, saintIdsToNamesMale);
        allSaintsGrouped.put(CATEGORY_MAGI_KEY, saintIdsToNamesMagi);

        return allSaintsGrouped;
    }

    public static Saint getSaint(Context context, long id) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(SaintsContract.SaintEntry.TABLE_NAME + " AS s " +
            " INNER JOIN " + PaintingsContract.PaintingsEntry.TABLE_NAME + " AS p " +
                " ON s." + SaintsContract.SaintEntry._ID + "=p." + PaintingsContract.PaintingsEntry.SAINT_ID +
            " INNER JOIN " + context.getString(SaintsContract.SaintTranslation.TABLE_NAME) + " AS tr " +
                " ON s." + SaintsContract.SaintEntry._ID + "=tr." + SaintsContract.SaintTranslation.TRANSLATION_ID);

        final String[] projection = {
                "s." + SaintsContract.SaintEntry._ID + " AS s_id ",
                "tr." + SaintsContract.SaintTranslation.NAME,
                PaintingsContract.PaintingsEntry.FILE_NAME,
                "p." + PaintingsContract.PaintingsEntry.COUNT,
                "p." + PaintingsContract.PaintingsEntry._ID + " AS p_id ",
                SaintsContract.SaintTranslation.ATTRIBUTES,
                SaintsContract.SaintEntry.ICON,
                SaintsContract.SaintTranslation.DESCRIPTION,
                "tr." + SaintsContract.SaintTranslation.WIKI_LINK,
                SaintsContract.SaintEntry.GENDER,
                SaintsContract.SaintEntry.CATEGORY
        };
        final String selection = "s." + SaintsContract.SaintEntry._ID + " = ?";
        final String[] selectionArgs = { String.valueOf(id) };
        Cursor cursor = queryBuilder.query(
                DbAccess.getDbAccess(context).getDatabase(),
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
            DbAccess.getDbAccess(context).closeDatabase();
        }
    }

    // this cursor is join of tables saints w paintings, for one saint
    // from the first row, we exctract info about first painting, then extract info about Saint
    // then iterate through rows to exctract info about all the rest of paintings and add em to Saint object
    public static Saint convertSaintFromCursorOnPosition(Cursor cursor, Context context) {

        final int idColumnIndex = cursor.getColumnIndex("s" + SaintsContract.SaintEntry._ID);
        final int nameColumnIndex = cursor.getColumnIndex(SaintsContract.SaintTranslation.NAME);
        final int attributesColumnIndex = cursor.getColumnIndex(SaintsContract.SaintTranslation.ATTRIBUTES);
        final int iconColumnIndex = cursor.getColumnIndex(SaintsContract.SaintEntry.ICON);
        final int descriptionColumnIndex = cursor.getColumnIndex(SaintsContract.SaintTranslation.DESCRIPTION);
        final int wikiLinkColumnIndex = cursor.getColumnIndex(SaintsContract.SaintTranslation.WIKI_LINK);
        final int genderColumnIndex = cursor.getColumnIndex(SaintsContract.SaintEntry.GENDER);
        final int categoryColumnIndex = cursor.getColumnIndex(SaintsContract.SaintEntry.CATEGORY);

        ArrayList<Painting> paintings = new ArrayList<>();
        paintings.add(PaintingsContract.convertPaintingFromSaintJoinedCursor(cursor, context));

        Saint saint = new Saint(
                (idColumnIndex != -1) ? cursor.getLong(idColumnIndex) : -1,
                (nameColumnIndex != -1) ? cursor.getString(nameColumnIndex) : null,
                paintings,
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
            saint.getPaintings().add(PaintingsContract.convertPaintingFromSaintJoinedCursor(cursor, context));
        }

        return saint;
    }

}
