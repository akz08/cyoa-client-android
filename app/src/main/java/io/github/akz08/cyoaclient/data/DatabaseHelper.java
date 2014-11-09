package io.github.akz08.cyoaclient.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*
 * Manages the local SQLite database.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        super(context, DatabaseContract.DATABASE_NAME, null, DatabaseContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseContract.CharacterEntry.CREATE_TABLE);
        db.execSQL(DatabaseContract.SceneEntry.CREATE_TABLE);
        db.execSQL(DatabaseContract.MessageEntry.CREATE_TABLE);
        db.execSQL(DatabaseContract.ChoiceEntry.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DatabaseContract.CharacterEntry.DELETE_TABLE);
        db.execSQL(DatabaseContract.MessageEntry.DELETE_TABLE);
        db.execSQL(DatabaseContract.MessageEntry.DELETE_TABLE);
        db.execSQL(DatabaseContract.ChoiceEntry.DELETE_TABLE);
        onCreate(db);
    }

}