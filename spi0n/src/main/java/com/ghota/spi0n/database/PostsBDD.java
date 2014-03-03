package com.ghota.spi0n.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ghota.spi0n.model.PostData;

/**
 * Created by Ghota on 02/03/2014.
 */
public class PostsBDD {

    private static final int VERSION_BDD = 1;
    private static final String NOM_BDD = "eleves.db";
    private static final String TABLE_POSTS = "table_posts";

    private static final String COL_GUID = "GUID";
    private static final int NUM_COL_GUID = 0;

    private static final String COL_SLUG = "SLUG";
    private static final int NUM_COL_SLUG = 1;

    private static final String COL_URL = "URL";
    private static final int NUM_COL_URL = 2;

    private static final String COL_TITLE = "TITLE";
    private static final int NUM_COL_TITLE = 3;

    private static final String COL_CONTENT = "CONTENT";
    private static final int NUM_COL_CONTENT = 4;

    private static final String COL_EXCERPT = "EXCERPT";
    private static final int NUM_COL_EXCERPT = 5;

    private static final String COL_DATE = "DATE";
    private static final int NUM_COL_DATE = 6;

    private static final String COL_CATEGORY = "CATEGORY";
    private static final int NUM_COL_CATEGORY = 7;

    private static final String COL_COMMENT = "COMMENT";
    private static final int NUM_COL_COMMENT = 8;

    private static final String COL_THUMBURL = "THUMBURL";
    private static final int NUM_COL_THUMBURL = 9;

    private SQLiteDatabase bdd;
    private BaseSQLite maBaseSQLite;

    public PostsBDD(Context context){
        maBaseSQLite = new BaseSQLite(context, NOM_BDD, null, VERSION_BDD);
    }

    public void open(){
        bdd = maBaseSQLite.getWritableDatabase();
    }

    public void close(){
        bdd.close();
    }

    public SQLiteDatabase getBDD(){
        return bdd;
    }

    public long insertPost(PostData post){
        ContentValues values = new ContentValues();
        values.put(COL_GUID, Integer.valueOf(post.postGuid));
        values.put(COL_SLUG, post.postSlug);
        values.put(COL_URL, post.postUrl);
        values.put(COL_TITLE, post.postTitle);
        values.put(COL_CONTENT, post.postContent);
        values.put(COL_EXCERPT, post.postExcerpt);
        values.put(COL_DATE, post.postDate);
        values.put(COL_CATEGORY, post.postCategory);
        values.put(COL_COMMENT, post.postComment);
        values.put(COL_THUMBURL, post.postThumbUrl);

        return bdd.insert(TABLE_POSTS, null, values);
    }

    public int removePostWithID(int id){
        return bdd.delete(TABLE_POSTS, COL_GUID + " = " +id, null);
    }

    public PostData getPostWithID(int id){
        Cursor c = bdd.query(TABLE_POSTS, new String[] {COL_GUID, COL_SLUG, COL_URL, COL_TITLE, COL_CONTENT, COL_EXCERPT, COL_DATE, COL_CATEGORY, COL_COMMENT, COL_THUMBURL}, COL_GUID + "=" + id, null, null, null, null);
        return cursorToPost(c);
    }

    //Cette méthode permet de convertir un cursor en un Post
    private PostData cursorToPost(Cursor c){
        if (c.getCount() == 0)
            return null;

        c.moveToFirst();
        PostData post = new PostData();
        post.postGuid = String.valueOf(c.getInt(NUM_COL_GUID));
        post.postSlug = c.getString(NUM_COL_SLUG);
        post.postUrl = c.getString(NUM_COL_URL);
        post.postTitle = c.getString(NUM_COL_TITLE);
        post.postContent = c.getString(NUM_COL_CONTENT);
        post.postExcerpt = c.getString(NUM_COL_EXCERPT);
        post.postDate = c.getString(NUM_COL_DATE);
        post.postCategory = c.getString(NUM_COL_CATEGORY);
        post.postComment = c.getString(NUM_COL_COMMENT);
        post.postThumbUrl = c.getString(NUM_COL_THUMBURL);
        c.close();

        return post;
    }
}
