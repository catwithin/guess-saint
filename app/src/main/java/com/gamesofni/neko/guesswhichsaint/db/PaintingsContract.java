package com.gamesofni.neko.guesswhichsaint.db;

import android.provider.BaseColumns;


public class PaintingsContract {
    private PaintingsContract() {}

    public static class PaintingsEntry implements BaseColumns {
        static final String TABLE_NAME = "paintings";
        public final static String _ID = BaseColumns._ID;
        public static final String SAINT_ID = "saintId";
        public static final String FILE_NAME = "fileName"; // drawables file name
        public static final String NAME = "name";
        public static final String AUTHOR = "author";
        public static final String WIKI_LINK = "wikiLink";
        public static final String EXPLANATION = "explanation";
        public static final String COUNT = "correctCount";
    }

}
