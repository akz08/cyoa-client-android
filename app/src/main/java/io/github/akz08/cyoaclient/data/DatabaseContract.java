package io.github.akz08.cyoaclient.data;

import android.provider.BaseColumns;

/*
 * Defines table and columns names for the database.
 */
public class DatabaseContract {

    // Must increment the database version when changing the database schema.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "cyoa.db";

    private static final String INTEGER_TYPE = " INTEGER";
    private static final String TEXT_TYPE = " TEXT";
    private static final String DATETIME_TYPE = " DATETIME";
    private static final String AUTO_DATETIME = " DEFAULT CURRENT_TIMESTAMP";
    private static final String BLOB_TYPE = " BLOB";
    private static final String COMMA_SEP = ",";

    // To prevent accidental instantiation of the contract class, give it an empty constructor.
    private DatabaseContract() {}

    /*
     * Fields and create/delete methods associated with the 'character' table.
     */
    public static final class CharacterEntry implements BaseColumns {

        // Table fields
        public static final String TABLE_NAME = "character";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_AGE = "age";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_PHOTO = "photo";

        // Create table method
        public static final String CREATE_TABLE = "CREATE TABLE " +
            TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY," +
            COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
            COLUMN_AGE + TEXT_TYPE + COMMA_SEP +
            COLUMN_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
            COLUMN_PHOTO + BLOB_TYPE +
            " )";

        // Delete table method
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    }

    /*
     * Fields and create/delete methods associated with the 'message' table.
     */
    public static final class MessageEntry implements BaseColumns {

        // Table fields
        public static final String TABLE_NAME = "message";
        public static final String COLUMN_CHARACTER_ID = "character_id";
        public static final String COLUMN_MESSAGE_TEXT = "message_text";
        public static final String COLUMN_MESSAGE_DATETIME = "message_datetime";

        // Create table method
        public static final String CREATE_TABLE = "CREATE TABLE " +
            TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY," +
            COLUMN_CHARACTER_ID + INTEGER_TYPE + COMMA_SEP +
            COLUMN_MESSAGE_TEXT + TEXT_TYPE + COMMA_SEP +
            COLUMN_MESSAGE_DATETIME + DATETIME_TYPE + AUTO_DATETIME + COMMA_SEP +
            " FOREIGN KEY (" + COLUMN_CHARACTER_ID + ") REFERENCES " +
            CharacterEntry.TABLE_NAME + " (" + CharacterEntry._ID + "), " +
            " UNIQUE (" + MessageEntry.COLUMN_CHARACTER_ID + ", " +
            MessageEntry.COLUMN_MESSAGE_DATETIME + ") ON CONFLICT REPLACE" +
            ");";

        // Delete table method
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    }

    /*
     * Fields and create/delete methods associated with the 'choice' table.
     */
    public static final class ChoiceEntry implements BaseColumns {

        // Table fields
        public static final String TABLE_NAME = "choice";
        public static final String COLUMN_MESSAGE_ID = "message_id";
        public static final String COLUMN_CHOICE_TEXT = "choice_text";
        public static final String COLUMN_CHOICE_DATETIME = "choice_datetime";

        // Create table method
        public static final String CREATE_TABLE = "CREATE TABLE " +
            TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY," +
            COLUMN_MESSAGE_ID + INTEGER_TYPE + COMMA_SEP +
            COLUMN_CHOICE_TEXT + TEXT_TYPE + COMMA_SEP +
            COLUMN_CHOICE_DATETIME + DATETIME_TYPE + AUTO_DATETIME + COMMA_SEP +
            " FOREIGN KEY (" + COLUMN_MESSAGE_ID + ") REFERENCES " +
            MessageEntry.TABLE_NAME + " (" + MessageEntry._ID + "), " +
            " UNIQUE (" + ChoiceEntry.COLUMN_MESSAGE_ID + ", " +
            ChoiceEntry.COLUMN_CHOICE_DATETIME + ") ON CONFLICT REPLACE" +
            ");";

        // Delete table method
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    }

}