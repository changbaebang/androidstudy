package com.example.bangchangbae.helloworld;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;

public class MyContentProvider extends ContentProvider {
    private SQLiteDatabase db;
    static final String DATABASE_NAME = "mydb";
    static final String TABLE_NAME = "greetings";
    static final String name = "greeting";

    static final Uri CONTENT_URI = Uri.parse("content://com.example.bangchangbae.MyProvider/greeting");
    static final int ALL_GREETINGS = 1;
    static final int GREETING = 2;
    static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI("com.example.bangchangbae.MyProvider", "greeting", ALL_GREETINGS);
        uriMatcher.addURI("com.example.bangchangbae.MyProvider", "greeting/#", GREETING);
    }
    private static HashMap<String, String> valueMap;

    public MyContentProvider() {
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int count;
        switch (uriMatcher.match(uri)){
            case ALL_GREETINGS:
                count = db.delete(TABLE_NAME, selection, selectionArgs);
                break;
            case GREETING:
                String id = uri.getLastPathSegment();
                count = db.delete(TABLE_NAME, "id = "+ id + ((TextUtils.isEmpty(selection))? " AND ("+selection+")" : ""), selectionArgs );
            default:
                Log.e("MyContentProvider", "Unknown URI : " + uri);
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)){
            case ALL_GREETINGS:
                return "vnd.android.cursor.dir/greeting";
            case GREETING:
                return "vnd.android.cursor.item/greeting";
            default:
                Log.e("MyContentProvider", "Unknown URI : " + uri);
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        long rowID = db.insert(TABLE_NAME, "", values);
        if (rowID <= 0) {
            Log.e("MyContentProvider", "Failed to add record into " + uri);
            throw new SQLException("Failed to add record into " + uri);
        }
        Uri appendedUri = ContentUris.withAppendedId(CONTENT_URI, rowID);
        getContext().getContentResolver().notifyChange(appendedUri, null);
        return appendedUri;
    }

    @Override
    public boolean onCreate() {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        db = dbHelper.getWritableDatabase();

        return db != null;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE_NAME);

        switch (uriMatcher.match(uri)){
            case ALL_GREETINGS:
                qb.setProjectionMap(valueMap);
                break;
            case GREETING:
                String id = uri.getLastPathSegment();
                qb.appendWhere("id = "+ id);
                break;
            default:
                Log.e("MyContentProvider", "Unknown URI : " + uri);
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        //if(sortOrder == null || sortOrder.isEmpty())
        //    sortOrder = name;

        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int count;
        switch (uriMatcher.match(uri)){
            case ALL_GREETINGS:
                count = db.update(TABLE_NAME, values, selection, selectionArgs);
                break;
            case GREETING:
                String id = uri.getLastPathSegment();
                count = db.update(TABLE_NAME, values, "id = " + id + ((TextUtils.isEmpty(selection)) ? " AND (" + selection + ")" : ""), selectionArgs);
            default:
                Log.e("MyContentProvider", "Unknown URI : " + uri);
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper{
        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, 1/*DATABASE_VERSION*/);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(" CREATE TABLE "+ TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT, " + name + " TEXT NOT NULL);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}
