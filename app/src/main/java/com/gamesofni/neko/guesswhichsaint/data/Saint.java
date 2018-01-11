package com.gamesofni.neko.guesswhichsaint.data;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.gamesofni.neko.guesswhichsaint.db.SaintsContract;

import java.util.ArrayList;
import java.util.Arrays;


public class Saint {
    private long id;
    private String name;
    private ArrayList<Integer> paintings;
    private ArrayList<String> attributes;
    private int icon;
    private String info;
    private String wikiUrl;
    private Integer gender;
    private String category;


    public Saint(long id, String name, ArrayList<Integer> paintings, ArrayList<String> attributes, int icon, String info, String wikiUrl, Integer gender, String category) {
        this.id = id;
        this.name = name;
        this.paintings = paintings;
        this.attributes = attributes;
        this.icon = icon;
        this.info = info;
        this.wikiUrl = wikiUrl;
        this.gender = gender;
        this.category = category;
    }

    public static Saint convertSaintFromCursor(Cursor cursor, Context context) {
        try {
            if (cursor.moveToNext()) {
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
        final int paintingsColumnIndex = cursor.getColumnIndex(SaintsContract.SaintEntry.PAINTINGS);
        final int attributesColumnIndex = cursor.getColumnIndex(SaintsContract.SaintTranslation.ATTRIBUTES);
        final int iconColumnIndex = cursor.getColumnIndex(SaintsContract.SaintEntry.ICON);
        final int descriptionColumnIndex = cursor.getColumnIndex(SaintsContract.SaintTranslation.DESCRIPTION);
        final int wikiLinkColumnIndex = cursor.getColumnIndex(SaintsContract.SaintTranslation.WIKI_LINK);
        final int genderColumnIndex = cursor.getColumnIndex(SaintsContract.SaintEntry.GENDER);
        final int categoryColumnIndex = cursor.getColumnIndex(SaintsContract.SaintEntry.CATEGORY);

        return new Saint(
                (idColumnIndex != -1) ? cursor.getLong(idColumnIndex) : -1,
                (nameColumnIndex != -1) ? cursor.getString(nameColumnIndex) : null,
                // TODO: stub, hjoin paintings table
                getPaintingsList(paintingsColumnIndex, cursor, context),
//                new ArrayList<Integer>(Arrays.asList(new Integer[]{R.drawable.jerome1})),

                // TODO: stub, save attr in separate table
                (attributesColumnIndex != -1) ?
                        new ArrayList<String>(Arrays.asList(TextUtils.split(cursor.getString(attributesColumnIndex), ","))) :
                        null,
                (iconColumnIndex != -1) ?
                        context.getResources().getIdentifier(cursor.getString(iconColumnIndex), "drawable", context.getPackageName()) :
                        -1,
                (descriptionColumnIndex != -1) ? cursor.getString(descriptionColumnIndex) : null,
                (wikiLinkColumnIndex != -1) ? cursor.getString(wikiLinkColumnIndex) : null,
                (genderColumnIndex != -1) ? cursor.getInt(genderColumnIndex) : null,
                (categoryColumnIndex != -1) ? cursor.getString(categoryColumnIndex) : null
        );
    }

    private static ArrayList<Integer> getPaintingsList(int paintingsColumnIndex, Cursor cursor, final Context context) {
        if (paintingsColumnIndex != -1) {
            ArrayList<String> paintingNames = new ArrayList<>(Arrays.asList(TextUtils.split(cursor.getString(paintingsColumnIndex), ",")));
            ArrayList<Integer> paintingIds = new ArrayList<>(paintingNames.size());
            for (String name : paintingNames) {
                paintingIds.add(context.getResources().getIdentifier(name, "drawable", context.getPackageName()));
            }
            return paintingIds;
        }
        return null;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Integer> getPaintings() {
        return paintings;
    }

    public ArrayList<String> getAttributes() {
        return attributes;
    }

    public int getIcon() {
        return icon;
    }

    public String getInfo() {
        return info;
    }

    public String getWikiUrl() {
        return wikiUrl;
    }

    public Integer getGender() {
        return gender;
    }

    public String getCategory() {
        return category;
    }
}
