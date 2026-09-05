package de.baumann.browser.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import de.baumann.browser.unit.RecordUnit;

class RecordHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Ninja4.db";
    private static final int DATABASE_VERSION = 6;

    RecordHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(RecordUnit.CREATE_HISTORY);
        database.execSQL(RecordUnit.CREATE_WHITELIST);
        database.execSQL(RecordUnit.CREATE_JAVASCRIPT);
        database.execSQL(RecordUnit.CREATE_COOKIE);
        database.execSQL(RecordUnit.CREATE_GRID);
        database.execSQL(RecordUnit.CREATE_BOOKMARK);
        database.execSQL(RecordUnit.CREATE_REMOTE);
        database.execSQL(RecordUnit.CREATE_TAB);
    }

    // UPGRADE ATTENTION!!!
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        switch(oldVersion) {
            case 1:
                database.execSQL(RecordUnit.CREATE_BOOKMARK);
            case 2:
                database.execSQL(RecordUnit.CREATE_REMOTE);
            case 3:
                database.execSQL(RecordUnit.CREATE_TAB);
            case 4:
                // P1 step 4: add PROFILE_ID to the four existing whitelist
                // tables, defaulting all pre-existing rows to "default".
                safeAddProfileIdColumn(database, RecordUnit.ADD_PROFILE_ID_WHITELIST);
                safeAddProfileIdColumn(database, RecordUnit.ADD_PROFILE_ID_JAVASCRIPT);
                safeAddProfileIdColumn(database, RecordUnit.ADD_PROFILE_ID_COOKIE);
                safeAddProfileIdColumn(database, RecordUnit.ADD_PROFILE_ID_REMOTE);
            case 5:
                // P1 step 5: app-owned browsing records become profile-local.
                // ADD COLUMN preserves every existing row and assigns it to
                // the default profile through the SQL DEFAULT clause.
                safeAddProfileIdColumn(database, RecordUnit.ADD_PROFILE_ID_HISTORY);
                safeAddProfileIdColumn(database, RecordUnit.ADD_PROFILE_ID_BOOKMARK);
                safeAddProfileIdColumn(database, RecordUnit.ADD_PROFILE_ID_TAB);
        }
    }

    private void safeAddProfileIdColumn(SQLiteDatabase database, String alterTableSql) {
        try {
            database.execSQL(alterTableSql);
        } catch (SQLException alreadyExists) {
            // Tolerate a partially completed upgrade. Existing data remains
            // intact and the column is already present in this case.
        }
    }
}
