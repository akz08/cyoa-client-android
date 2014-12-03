package io.github.akz08.cyoaclient.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * Manages the local SQLite database.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    Context context = null;

    public DatabaseHelper(Context context) {
        super(context, DatabaseContract.DATABASE_NAME, null, DatabaseContract.DATABASE_VERSION);
        this.context = context;
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

    public void insertCharacterIntoDatabase(String id, String name, String age, String description) {
        // Format the values to be inserted into the database
        ContentValues characterValues = new ContentValues();
        characterValues.put(DatabaseContract.CharacterEntry.COLUMN_CHARACTER_ID, id);
        characterValues.put(DatabaseContract.CharacterEntry.COLUMN_CHARACTER_NAME, name);
        characterValues.put(DatabaseContract.CharacterEntry.COLUMN_CHARACTER_AGE, age);
        characterValues.put(DatabaseContract.CharacterEntry.COLUMN_CHARACTER_DESCRIPTION, description);
        // Open the database for writing and insert the formatted values
        SQLiteDatabase database = this.getWritableDatabase();
        database.insert(DatabaseContract.CharacterEntry.TABLE_NAME, null, characterValues);
    }

    public void insertSceneIntoDatabase(String id, String characterId, String sceneInformation) {
        // Format the values to be inserted into the database
        ContentValues sceneValues = new ContentValues();
        sceneValues.put(DatabaseContract.SceneEntry.COLUMN_SCENE_ID, id);
        sceneValues.put(DatabaseContract.SceneEntry.COLUMN_CHARACTER_ID, characterId);
        sceneValues.put(DatabaseContract.SceneEntry.COLUMN_SCENE_INFORMATION, sceneInformation);
        // Open the database for writing and insert the formatted values
        SQLiteDatabase database = this.getWritableDatabase();
        database.insert(DatabaseContract.SceneEntry.TABLE_NAME, null, sceneValues);
    }

    public void insertMessageIntoDatabase(String id, String sceneId, String messageText) {
        // Format the values to be inserted into the database
        ContentValues messageValues = new ContentValues();
        messageValues.put(DatabaseContract.MessageEntry.COLUMN_MESSAGE_ID, id);
        messageValues.put(DatabaseContract.MessageEntry.COLUMN_SCENE_ID, sceneId);
        messageValues.put(DatabaseContract.MessageEntry.COLUMN_MESSAGE_TEXT, messageText);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        messageValues.put(DatabaseContract.MessageEntry.COLUMN_MESSAGE_DATETIME, dateFormat.format(new Date()));
        // Open the database for writing and insert the formatted values
        SQLiteDatabase database = this.getWritableDatabase();
        database.insert(DatabaseContract.MessageEntry.TABLE_NAME, null, messageValues);
    }

    public void insertChoiceIntoDatabase(String id, String characterId, String sceneInformation) {
        // Format the values to be inserted into the database
        ContentValues choiceValues = new ContentValues();
        choiceValues.put(DatabaseContract.ChoiceEntry.COLUMN_CHOICE_ID, id);
        choiceValues.put(DatabaseContract.ChoiceEntry.COLUMN_MESSAGE_ID, characterId);
        choiceValues.put(DatabaseContract.ChoiceEntry.COLUMN_CHOICE_TEXT, sceneInformation);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        choiceValues.put(DatabaseContract.ChoiceEntry.COLUMN_CHOICE_SELECTED_DATETIME, dateFormat.format(new Date()));
        // Open the database for writing and insert the formatted values
        SQLiteDatabase database = this.getWritableDatabase();
        database.insert(DatabaseContract.ChoiceEntry.TABLE_NAME, null, choiceValues);
    }

}