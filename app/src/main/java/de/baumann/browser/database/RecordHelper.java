package de.baumann.browser.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import de.baumann.browser.unit.RecordUnit;

class RecordHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Ninja4.db";
    private static final int DATABASE_VERSION = 5;

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
                // Each ALTER TABLE only adds a column - no table is
                // dropped or recreated, and no other table is touched.
                // Wrapped individually so a partially-upgraded database
                // (e.g. PROFILE_ID already added by a previous, interrupted
                // upgrade attempt) cannot crash-loop the app.
                safeAddProfileIdColumn(database, RecordUnit.ADD_PROFILE_ID_WHITELIST);
                safeAddProfileIdColumn(database, RecordUnit.ADD_PROFILE_ID_JAVASCRIPT);
                safeAddProfileIdColumn(database, RecordUnit.ADD_PROFILE_ID_COOKIE);
                safeAddProfileIdColumn(database, RecordUnit.ADD_PROFILE_ID_REMOTE);
                // we want all updates, so no break statement here...
        }
    }

    private void safeAddProfileIdColumn(SQLiteDatabase database, String alterTableSql) {
        try {
            database.execSQL(alterTableSql);
        } catch (SQLException alreadyExists) {
            // Column already present (e.g. re-run of this upgrade step) -
            // existing rows are preserved either way, nothing further to do.
        }
    }
}
