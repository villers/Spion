package com.ghota.spi0n.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ghota on 02/03/2014.
 */
public class BaseSQLite extends SQLiteOpenHelper {

    private static final String TABLE_POSTS = "table_posts";
    private static final String COL_ID = "ID";
    private static final String COL_GUID = "GUID";
    private static final String COL_SLUG = "SLUG";
    private static final String COL_URL = "URL";
    private static final String COL_TITLE = "TITLE";
    private static final String COL_CONTENT = "CONTENT";
    private static final String COL_EXCERPT = "EXCERPT";
    private static final String COL_DATE = "DATE";
    private static final String COL_CATEGORY = "CATEGORY";
    private static final String COL_COMMENT = "COMMENT";
    private static final String COL_THUMBURL = "THUMBURL";


    private static final String CREATE_BDD = "CREATE TABLE " + TABLE_POSTS + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_GUID + " INTEGER UNIQUE, "
            + COL_SLUG + " TEXT NOT NULL, "
            + COL_URL + " TEXT NOT NULL, "
            + COL_TITLE + " TEXT NOT NULL, "
            + COL_CONTENT + " TEXT NOT NULL, "
            + COL_EXCERPT + " TEXT NOT NULL, "
            + COL_DATE + " TEXT NOT NULL, "
            + COL_CATEGORY + " TEXT NOT NULL, "
            + COL_COMMENT + " TEXT NOT NULL, "
            + COL_THUMBURL + " TEXT NOT NULL);";

    public BaseSQLite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //on créé la table à partir de la requête écrite dans la variable CREATE_BDD
        db.execSQL(CREATE_BDD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //On peut fait ce qu'on veut ici moi j'ai décidé de supprimer la table et de la recréer
        //comme ça lorsque je change la version les id repartent de 0
        db.execSQL("DROP TABLE " + TABLE_POSTS + ";");
        onCreate(db);
    }

}
