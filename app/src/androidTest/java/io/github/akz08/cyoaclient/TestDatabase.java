package io.github.akz08.cyoaclient;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;
import java.util.Set;

import io.github.akz08.cyoaclient.data.DatabaseContract;
import io.github.akz08.cyoaclient.data.DatabaseHelper;

@TargetApi(11)
public class TestDatabase extends AndroidTestCase {

    private Resources res;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    public void setUp() throws Exception {
        super.setUp();
        // Assign resources from the project for testing purposes
        res = getContext().getResources();
        // Provide a different context name, so that the device database is left intact
        RenamingDelegatingContext context = new RenamingDelegatingContext(getContext(), "test_");
        databaseHelper = new DatabaseHelper(context);
        database = databaseHelper.getWritableDatabase();
    }

    public void tearDown() throws Exception {
        super.tearDown();
        databaseHelper.close();
    }

    public void testDatabaseIsOpen() {
        assertEquals(true, database.isOpen());
    }

    public void testInsertReadTables() {
        // Generate character mock data
        ContentValues characterValues = createCharacterValues();
        long characterRowId = database.insert(DatabaseContract.CharacterEntry.TABLE_NAME, null, characterValues);
        assertTrue(characterRowId != -1);
        Cursor characterTable = database.query(DatabaseContract.CharacterEntry.TABLE_NAME, null, null, null, null, null, null);
        validateCursor(characterTable, characterValues);
        // Generate scene mock data
        ContentValues sceneValues = createSceneValues((int) characterRowId);
        long sceneRowId = database.insert(DatabaseContract.SceneEntry.TABLE_NAME, null, sceneValues);
        assertTrue(sceneRowId != -1);
        Cursor sceneTable = database.query(DatabaseContract.SceneEntry.TABLE_NAME, null, null, null, null, null, null);
        validateCursor(sceneTable, sceneValues);
        // Generate message mock data
        ContentValues messageValues = createMessageValues((int) sceneRowId);
        long messageRowId = database.insert(DatabaseContract.MessageEntry.TABLE_NAME, null, messageValues);
        assertTrue(messageRowId != -1);
        Cursor messageTable = database.query(DatabaseContract.MessageEntry.TABLE_NAME, null, null, null, null, null, null);
        validateCursor(messageTable, messageValues);
        // Generate choice mock data
        ContentValues choiceValues = createChoiceValues((int) messageRowId);
        long choiceRowId = database.insert(DatabaseContract.ChoiceEntry.TABLE_NAME, null, choiceValues);
        assertTrue(choiceRowId != -1);
        Cursor choiceTable = database.query(DatabaseContract.ChoiceEntry.TABLE_NAME, null, null, null, null, null, null);
        validateCursor(choiceTable, choiceValues);
    }

    private void validateCursor(Cursor valueCursor, ContentValues expectedValues) {
        assertTrue(valueCursor.moveToFirst());
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(idx == -1);
            int columnType = valueCursor.getType(idx);
            if (columnType == Cursor.FIELD_TYPE_BLOB) {
                byte[] expectedValue = (byte[]) entry.getValue();
                assertEquals(Arrays.toString(expectedValue), Arrays.toString(valueCursor.getBlob(idx)));
            }
            else if (columnType == Cursor.FIELD_TYPE_FLOAT) {
                float expectedValue = (Float) entry.getValue();
                assertEquals(expectedValue, valueCursor.getFloat(idx));
            }
            else if (columnType == Cursor.FIELD_TYPE_INTEGER) {
                int expectedValue = (Integer) entry.getValue();
                assertEquals(expectedValue, valueCursor.getInt(idx));
            }
            else if (columnType == Cursor.FIELD_TYPE_NULL) {
                Object expectedValue = entry.getValue();
                assertNull(expectedValue);
            }
            else if (columnType == Cursor.FIELD_TYPE_STRING) {
                String expectedValue = entry.getValue().toString();
                assertEquals(expectedValue, valueCursor.getString(idx));
            }
        }
        valueCursor.close();
    }

    // Helper methods for generating mock data.

    private ContentValues createCharacterValues() {
        ContentValues characterValues = new ContentValues();
        characterValues.put(DatabaseContract.CharacterEntry.COLUMN_NAME, "Claire");
        characterValues.put(DatabaseContract.CharacterEntry.COLUMN_AGE, 28);
        characterValues.put(DatabaseContract.CharacterEntry.COLUMN_DESCRIPTION, "Has just met someone.");
        Bitmap image = BitmapFactory.decodeResource(res, R.drawable.ic_launcher);
        byte[] buffer = blobifyImage(image);
        characterValues.put(DatabaseContract.CharacterEntry.COLUMN_PHOTO, buffer);
        return characterValues;
    }

    private ContentValues createSceneValues(int characterRowId) {
        ContentValues sceneValues = new ContentValues();
        sceneValues.put(DatabaseContract.SceneEntry.COLUMN_CHARACTER_ID, characterRowId);
        sceneValues.put(DatabaseContract.SceneEntry.COLUMN_SCENE_INFORMATION, "Claire had a date last night. She is contacting you to ask for advice.");
        return sceneValues;
    }

    private ContentValues createMessageValues(int sceneRowId) {
        ContentValues messageValues = new ContentValues();
        messageValues.put(DatabaseContract.MessageEntry.COLUMN_SCENE_ID, sceneRowId);
        messageValues.put(DatabaseContract.MessageEntry.COLUMN_MESSAGE_TEXT, "Hi. I just met someone last night. Should I see him again?");
        messageValues.put(DatabaseContract.MessageEntry.COLUMN_MESSAGE_DATETIME, Calendar.getInstance().get(Calendar.SECOND));
        return messageValues;
    }

    private ContentValues createChoiceValues(int messageRowId) {
        ContentValues choiceValues = new ContentValues();
        choiceValues.put(DatabaseContract.ChoiceEntry.COLUMN_MESSAGE_ID, messageRowId);
        choiceValues.put(DatabaseContract.ChoiceEntry.COLUMN_CHOICE_TEXT, "Sure.");
        choiceValues.putNull(DatabaseContract.ChoiceEntry.COLUMN_CHOICE_SELECTED_DATETIME);
        return choiceValues;
    }

    // Helper methods for data processing.

    private byte[] blobifyImage(Bitmap image) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, out);
        return out.toByteArray();
    }

}