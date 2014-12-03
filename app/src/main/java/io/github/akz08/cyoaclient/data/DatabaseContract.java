package io.github.akz08.cyoaclient.data;

import android.provider.BaseColumns;

/*
 * Defines table and columns names for the database.
 */
public class DatabaseContract {

    // Must increment the database version when changing the database schema.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "cyoa.db";

    // String constants used repeatedly in SQL query construction.
    private static final String COMMA_SEP = " , ";
    private static final String L_PAREN = " ( ";
    private static final String R_PAREN = " ) ";
    private static final String SEMI_COLON = " ; ";

    private static final String INTEGER_TYPE = " INTEGER ";
    private static final String TEXT_TYPE = " TEXT ";
    private static final String BLOB_TYPE = " BLOB ";

    private static final String INTEGER_PRIMARY_KEY = " INTEGER PRIMARY KEY ";
    private static final String FOREIGN_KEY = " FOREIGN KEY ";
    private static final String REFERENCES = " REFERENCES ";

    // To prevent accidental instantiation of the contract class, give it an empty constructor.
    private DatabaseContract() {}

    /*
     * Fields and create/delete methods associated with the 'character' table.
     */
    public static final class CharacterEntry implements BaseColumns {

        // Table fields
        public static final String TABLE_NAME = "character";
        public static final String COLUMN_CHARACTER_ID = "character_id";
        public static final String COLUMN_CHARACTER_NAME = "character_name";
        public static final String COLUMN_CHARACTER_AGE = "character_age";
        public static final String COLUMN_CHARACTER_DESCRIPTION = "character_description";
        public static final String COLUMN_CHARACTER_PHOTO = "character_photo";

        // Create table method
        public static final String CREATE_TABLE = "CREATE TABLE " +
            TABLE_NAME + L_PAREN +
                COLUMN_CHARACTER_ID + INTEGER_PRIMARY_KEY + COMMA_SEP +
                COLUMN_CHARACTER_NAME + TEXT_TYPE + COMMA_SEP +
                COLUMN_CHARACTER_AGE + TEXT_TYPE + COMMA_SEP +
                COLUMN_CHARACTER_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                COLUMN_CHARACTER_PHOTO + BLOB_TYPE +
            R_PAREN + SEMI_COLON;

        // Delete table method
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    }

    /*
     * Fields and create/delete methods associated with the 'scene' table.
     */
    public static final class SceneEntry implements BaseColumns {

        // Table fields
        public static final String TABLE_NAME = "scene";
        public static final String COLUMN_SCENE_ID = "scene_id";
        public static final String COLUMN_CHARACTER_ID = "character_id";
        public static final String COLUMN_SCENE_INFORMATION = "scene_information";

        // Create table method
        public static final String CREATE_TABLE = "CREATE TABLE " +
            TABLE_NAME + L_PAREN +
                COLUMN_SCENE_ID + INTEGER_PRIMARY_KEY + COMMA_SEP +
                COLUMN_CHARACTER_ID + INTEGER_TYPE + COMMA_SEP +
                COLUMN_SCENE_INFORMATION + TEXT_TYPE + COMMA_SEP +
                FOREIGN_KEY + L_PAREN + COLUMN_CHARACTER_ID + R_PAREN + REFERENCES +
                    CharacterEntry.TABLE_NAME + L_PAREN + CharacterEntry._ID + R_PAREN +
            R_PAREN + SEMI_COLON;

        // Delete table method
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    }

    /*
     * Fields and create/delete methods associated with the 'message' table.
     */
    public static final class MessageEntry implements BaseColumns {

        // Table fields
        public static final String TABLE_NAME = "message";
        public static final String COLUMN_MESSAGE_ID = "message_id";
        public static final String COLUMN_SCENE_ID = "scene_id";
        public static final String COLUMN_MESSAGE_TEXT = "message_text";
        public static final String COLUMN_MESSAGE_DATETIME = "message_datetime";

        // Create table method
        public static final String CREATE_TABLE = "CREATE TABLE " +
            TABLE_NAME + L_PAREN +
                COLUMN_MESSAGE_ID + INTEGER_PRIMARY_KEY + COMMA_SEP +
                COLUMN_SCENE_ID + INTEGER_TYPE + COMMA_SEP +
                COLUMN_MESSAGE_TEXT + TEXT_TYPE + COMMA_SEP +
                COLUMN_MESSAGE_DATETIME + TEXT_TYPE + COMMA_SEP +
                FOREIGN_KEY + L_PAREN + COLUMN_SCENE_ID + R_PAREN + REFERENCES +
                    CharacterEntry.TABLE_NAME + L_PAREN + CharacterEntry._ID + R_PAREN +
            R_PAREN + SEMI_COLON;

        // Delete table method
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    }

    /*
     * Fields and create/delete methods associated with the 'choice' table.
     */
    public static final class ChoiceEntry implements BaseColumns {

        // Table fields
        public static final String TABLE_NAME = "choice";
        public static final String COLUMN_CHOICE_ID = "choice_id";
        public static final String COLUMN_MESSAGE_ID = "message_id";
        public static final String COLUMN_CHOICE_TEXT = "choice_text";
        public static final String COLUMN_CHOICE_SELECTED_DATETIME = "choice_selected_datetime";

        // Create table method
        public static final String CREATE_TABLE = "CREATE TABLE " +
            TABLE_NAME + L_PAREN +
                COLUMN_CHOICE_ID + INTEGER_PRIMARY_KEY + COMMA_SEP +
                COLUMN_MESSAGE_ID + INTEGER_TYPE + COMMA_SEP +
                COLUMN_CHOICE_TEXT + TEXT_TYPE + COMMA_SEP +
                COLUMN_CHOICE_SELECTED_DATETIME + TEXT_TYPE + COMMA_SEP +
                FOREIGN_KEY + L_PAREN + COLUMN_MESSAGE_ID + R_PAREN + REFERENCES +
                    MessageEntry.TABLE_NAME + L_PAREN + MessageEntry._ID + R_PAREN +
            R_PAREN + SEMI_COLON;

        // Delete table method
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    }

}