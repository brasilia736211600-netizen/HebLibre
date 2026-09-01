package de.baumann.browser.database;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import de.baumann.browser.unit.RecordUnit;

public class RecordAction {
    private SQLiteDatabase database;
    private final RecordHelper helper;

    public RecordAction(Context context) {
        this.helper = new RecordHelper(context);
    }
    public void open(boolean rw) { database = rw ? helper.getWritableDatabase() : helper.getReadableDatabase(); }
    public void close() {
        helper.close();
    }


    //StartSite

    public boolean addGridItem(Record record) {
        if (record == null
                || record.getTitle() == null
                || record.getTitle().trim().isEmpty()
                || record.getURL() == null
                || record.getURL().trim().isEmpty()
                || record.getOrdinal() < 0) {
            return false;
        }
        ContentValues values = new ContentValues();
        values.put(RecordUnit.COLUMN_TITLE, record.getTitle().trim());
        values.put(RecordUnit.COLUMN_URL, record.getURL().trim());
        values.put(RecordUnit.COLUMN_ORDINAL, record.getOrdinal());
        database.insert(RecordUnit.TABLE_GRID, null, values);
        return true;
    }

    public List<Record> listStartSite (Activity activity) {

        List<Record> list = new LinkedList<>();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        String sortBy = Objects.requireNonNull(sp.getString("sort_startSite", "ordinal"));

        Cursor cursor;
        cursor = database.query(
                RecordUnit.TABLE_GRID,
                new String[] {
                        RecordUnit.COLUMN_TITLE,
                        RecordUnit.COLUMN_URL,
                        RecordUnit.COLUMN_FILENAME,
                        RecordUnit.COLUMN_ORDINAL
                },
                null,
                null,
                null,
                null,
                sortBy
        );
        if (cursor == null) {
            return list;
        }
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(getRecord(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    //BOOKMARK

    public void addBookmark (Record record) {
        if (record == null
                || record.getTitle() == null
                || record.getTitle().trim().isEmpty()
                || record.getURL() == null
                || record.getURL().trim().isEmpty()
                || record.getTime() < 0L) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(RecordUnit.COLUMN_TITLE, record.getTitle().trim());
        values.put(RecordUnit.COLUMN_URL, record.getURL().trim());
        values.put(RecordUnit.COLUMN_TIME, record.getTime());
        database.insert(RecordUnit.TABLE_BOOKMARK, null, values);
    }

    public List<Record> listBookmark (Context context, boolean filter, long filterBy) {

        List<Record> list = new LinkedList<>();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String sortBy = Objects.requireNonNull(sp.getString("sort_bookmark", "title"));

        Cursor cursor;
        cursor = database.query(
                RecordUnit.TABLE_BOOKMARK,
                new String[] {
                        RecordUnit.COLUMN_TITLE,
                        RecordUnit.COLUMN_URL,
                        RecordUnit.COLUMN_TIME
                },
                null,
                null,
                null,
                null,
                sortBy
        );
        if (cursor == null) {
            return list;
        }
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            if (filter) {
                if (getRecord(cursor).getTime() == filterBy) {
                    list.add(getRecord(cursor));
                }
            } else {
                list.add(getRecord(cursor));
            }


            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    //Tab

    public void addTab(Record record) {
        if (record == null
                || record.getTitle() == null
                || record.getTitle().trim().isEmpty()
                || record.getURL() == null
                || record.getURL().trim().isEmpty()
                || record.getTime() < 0L) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(RecordUnit.COLUMN_TITLE, record.getTitle().trim());
        values.put(RecordUnit.COLUMN_URL, record.getURL().trim());
        values.put(RecordUnit.COLUMN_TIME, record.getTime());
        database.insert(RecordUnit.TABLE_TAB, null, values);
    }

    public List<Record> listTab () {
        List<Record> list = new ArrayList<>();
        Cursor cursor;
        cursor = database.query(
                RecordUnit.TABLE_TAB,
                new String[] {
                        RecordUnit.COLUMN_TITLE,
                        RecordUnit.COLUMN_URL,
                        RecordUnit.COLUMN_TIME
                },
                null,
                null,
                null,
                null,
                RecordUnit.COLUMN_TITLE + " asc"
        );

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(getRecord(cursor));
            cursor.moveToNext();
        }
        cursor.close();

        return list;
    }

    //History

    public void addHistory(Record record) {
        if (record == null
                || record.getTitle() == null
                || record.getTitle().trim().isEmpty()
                || record.getURL() == null
                || record.getURL().trim().isEmpty()
                || record.getTime() < 0L) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(RecordUnit.COLUMN_TITLE, record.getTitle().trim());
        values.put(RecordUnit.COLUMN_URL, record.getURL().trim());
        values.put(RecordUnit.COLUMN_TIME, record.getTime());
        database.insert(RecordUnit.TABLE_HISTORY, null, values);
    }

    public List<Record> listHistory () {
        List<Record> list = new ArrayList<>();
        Cursor cursor;
        cursor = database.query(
                RecordUnit.TABLE_HISTORY,
                new String[] {
                        RecordUnit.COLUMN_TITLE,
                        RecordUnit.COLUMN_URL,
                        RecordUnit.COLUMN_TIME
                },
                null,
                null,
                null,
                null,
                RecordUnit.COLUMN_TIME + " asc"
        );

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(getRecord(cursor));
            cursor.moveToNext();
        }
        cursor.close();

        return list;
    }


    // General
    //
    // P1 step 4: the four whitelist domain tables (WHITELIST/JAVASCRIPT/
    // COOKIE/REMOTE - the only callers of addDomain/checkDomain/
    // deleteDomain/listDomains) are now profile-scoped via PROFILE_ID.
    // Every read/write below is filtered/tagged by profileId so that two
    // different profile ids never see or affect each other's rows, while
    // callers that pass the same profileId continue to share full CRUD
    // visibility exactly as before this step.

    public void addDomain(String domain, String table, String profileId) {
        if (domain == null || domain.trim().isEmpty()) { return; }
        ContentValues values = new ContentValues();
        values.put(RecordUnit.COLUMN_DOMAIN, domain.trim());
        values.put(RecordUnit.COLUMN_PROFILE_ID, profileId);
        database.insert(table, null, values);
    }

    public boolean checkDomain(String domain, String table, String profileId) {
        if (domain == null || domain.trim().isEmpty()) {
            return false;
        }
        Cursor cursor = database.query(
                table,
                new String[] {RecordUnit.COLUMN_DOMAIN},
                RecordUnit.COLUMN_DOMAIN + "=? AND " + RecordUnit.COLUMN_PROFILE_ID + "=?",
                new String[] {domain.trim(), profileId},
                null,
                null,
                null
        );
        if (cursor != null) {
            boolean result = cursor.moveToFirst();
            cursor.close();
            return result;
        }
        return false;
    }

    public void deleteDomain(String domain, String table, String profileId) {
        if (domain == null || domain.trim().isEmpty()) { return; }
        // Kept as the pre-existing raw execSQL/string-concatenation style
        // (unchanged parameterization strategy) - only the extra
        // PROFILE_ID condition is new, per the P1 step 4 scope.
        database.execSQL("DELETE FROM " + table
                + " WHERE " + RecordUnit.COLUMN_DOMAIN + " = " + "\"" + domain.trim() + "\""
                + " AND " + RecordUnit.COLUMN_PROFILE_ID + " = " + "\"" + profileId + "\"");
    }

    public List<String> listDomains(String table, String profileId) {
        List<String> list = new ArrayList<>();
        Cursor cursor = database.query(
                table,
                new String[] {RecordUnit.COLUMN_DOMAIN},
                RecordUnit.COLUMN_PROFILE_ID + "=?",
                new String[] {profileId},
                null,
                null,
                RecordUnit.COLUMN_DOMAIN
        );
        if (cursor == null) {
            return list;
        }
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    public boolean checkUrl (String url, String table) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        Cursor cursor = database.query(
                table,
                new String[] {RecordUnit.COLUMN_URL},
                RecordUnit.COLUMN_URL + "=?",
                new String[] {url.trim()},
                null,
                null,
                null
        );
        if (cursor != null) {
            boolean result = cursor.moveToFirst();
            cursor.close();

            return result;
        }
        return false;
    }

    public void deleteURL (String domain, String table) {
        if (domain == null || domain.trim().isEmpty()) { return; }
        database.execSQL("DELETE FROM "+ table + " WHERE " + RecordUnit.COLUMN_URL + " = " + "\"" + domain.trim() + "\"");
    }

    public void clearTable (String table) {
        database.execSQL("DELETE FROM " + table);
    }

    // P1 step 4: profile-scoped clear, used only by the four whitelist
    // tables so that clearing one profile's whitelist cannot affect any
    // other profile's rows in the same table. The unscoped clearTable(table)
    // above remains unchanged and is still used by GRID/BOOKMARK/HISTORY.
    public void clearTable (String table, String profileId) {
        database.delete(table, RecordUnit.COLUMN_PROFILE_ID + "=?", new String[] {profileId});
    }

    private Record getRecord(Cursor cursor) {
        Record record = new Record();
        record.setTitle(cursor.getString(0));
        record.setURL(cursor.getString(1));
        record.setTime(cursor.getLong(2));
        return record;
    }

    public List<Record> listEntries (Activity activity) {
        List<Record> list = new ArrayList<>();
        RecordAction action = new RecordAction(activity);
        action.open(false);
        list.addAll(action.listStartSite(activity));
        list.addAll(action.listHistory());
        list.addAll(listBookmark(activity, false, 0));
        return list;
    }
}
