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

import de.baumann.browser.unit.ProfileCatalogStore;
import de.baumann.browser.unit.ProfileIdentity;
import de.baumann.browser.unit.RecordUnit;

public class RecordAction {
    private SQLiteDatabase database;
    private final RecordHelper helper;
    private final Context context;

    public RecordAction(Context context) {
        this.context = context.getApplicationContext();
        this.helper = new RecordHelper(this.context);
    }
    public void open(boolean rw) { database = rw ? helper.getWritableDatabase() : helper.getReadableDatabase(); }
    public void close() {
        helper.close();
    }

    private String activeProfileId() {
        return ProfileCatalogStore.getActiveProfileId(context);
    }

    private boolean isProfileScopedRecordTable(String table) {
        return RecordUnit.TABLE_HISTORY.equals(table)
                || RecordUnit.TABLE_BOOKMARK.equals(table)
                || RecordUnit.TABLE_TAB.equals(table);
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
        addBookmark(record, activeProfileId());
    }

    public void addBookmark (Record record, String profileId) {
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
        values.put(RecordUnit.COLUMN_PROFILE_ID, ProfileIdentity.normalize(profileId));
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
                RecordUnit.COLUMN_PROFILE_ID + "=?",
                new String[] {activeProfileId()},
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
        addTab(record, activeProfileId());
    }

    public void addTab(Record record, String profileId) {
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
        values.put(RecordUnit.COLUMN_PROFILE_ID, ProfileIdentity.normalize(profileId));
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
                RecordUnit.COLUMN_PROFILE_ID + "=?",
                new String[] {activeProfileId()},
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
        values.put(RecordUnit.COLUMN_PROFILE_ID, activeProfileId());
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
                RecordUnit.COLUMN_PROFILE_ID + "=?",
                new String[] {activeProfileId()},
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
    // Profile-scoped domain tables are handled by the overloads below, while
    // HISTORY/BOOKMARK/TAB obtain the active profile automatically for all
    // existing callers. Explicit profile overloads are used by session
    // persistence so an in-flight old-profile WebView can never be written
    // into a newly selected active profile during restart.

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
        database.delete(table,
                RecordUnit.COLUMN_DOMAIN + "=? AND " + RecordUnit.COLUMN_PROFILE_ID + "=?",
                new String[] {domain.trim(), profileId});
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
                isProfileScopedRecordTable(table)
                        ? RecordUnit.COLUMN_URL + "=? AND " + RecordUnit.COLUMN_PROFILE_ID + "=?"
                        : RecordUnit.COLUMN_URL + "=?",
                isProfileScopedRecordTable(table)
                        ? new String[] {url.trim(), activeProfileId()}
                        : new String[] {url.trim()},
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
        if (isProfileScopedRecordTable(table)) {
            database.delete(table,
                    RecordUnit.COLUMN_URL + "=? AND " + RecordUnit.COLUMN_PROFILE_ID + "=?",
                    new String[] {domain.trim(), activeProfileId()});
        } else {
            database.delete(table,
                    RecordUnit.COLUMN_URL + "=?",
                    new String[] {domain.trim()});
        }
    }

    public void clearTable (String table) {
        if (isProfileScopedRecordTable(table)) {
            database.delete(table, RecordUnit.COLUMN_PROFILE_ID + "=?", new String[] {activeProfileId()});
            return;
        }
        database.execSQL("DELETE FROM " + table);
    }

    public void clearTable (String table, String profileId) {
        database.delete(table, RecordUnit.COLUMN_PROFILE_ID + "=?", new String[] {ProfileIdentity.normalize(profileId)});
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
        list.addAll(action.listBookmark(activity, false, 0));
        action.close();
        return list;
    }
}