package io.github.akz08.cyoaclient;

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import io.github.akz08.cyoaclient.data.DatabaseContract;
import io.github.akz08.cyoaclient.data.DatabaseHelper;

public class TestDatabase extends AndroidTestCase {

    public static final String LOG_CAT = TestDatabase.class.getSimpleName();

    public void testCreateDatabase() throws Throwable {
        mContext.deleteDatabase(DatabaseContract.DATABASE_NAME);
        SQLiteDatabase database = new DatabaseHelper(this.mContext).getWritableDatabase();
        assertEquals(true, database.isOpen());
        database.close();
    }

}