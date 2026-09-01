package de.baumann.browser.unit;

public class RecordUnit {
    public static final String TABLE_HISTORY = "HISTORY";
    public static final String TABLE_WHITELIST = "WHITELIST";
    public static final String TABLE_JAVASCRIPT = "JAVASCRIPT";
    public static final String TABLE_COOKIE = "COOKIE";
    public static final String TABLE_REMOTE = "REMOTE";
    public static final String TABLE_GRID = "GRID";
    public static final String TABLE_TAB = "TAB";

    public static final String TABLE_BOOKMARK = "BOOKAMRK";

    public static final String COLUMN_TITLE = "TITLE";
    public static final String COLUMN_URL = "URL";
    public static final String COLUMN_TIME = "TIME";
    public static final String COLUMN_DOMAIN = "DOMAIN";
    public static final String COLUMN_FILENAME = "FILENAME";
    public static final String COLUMN_ORDINAL = "ORDINAL";

    // P1 step 4: persisted whitelist profile isolation. Added to the four
    // whitelist domain tables only (WHITELIST/JAVASCRIPT/COOKIE/REMOTE).
    // Existing/omitted rows default to DEFAULT_PROFILE_ID, preserving
    // current single-profile behavior for all pre-existing callers.
    public static final String COLUMN_PROFILE_ID = "PROFILE_ID";
    public static final String DEFAULT_PROFILE_ID = "default";

    public static final String CREATE_BOOKMARK = "CREATE TABLE "
            + TABLE_BOOKMARK
            + " ("
            + " " + COLUMN_TITLE + " text,"
            + " " + COLUMN_URL + " text,"
            + " " + COLUMN_TIME + " integer"
            + ")";

    public static final String CREATE_HISTORY = "CREATE TABLE "
            + TABLE_HISTORY
            + " ("
            + " " + COLUMN_TITLE + " text,"
            + " " + COLUMN_URL + " text,"
            + " " + COLUMN_TIME + " integer"
            + ")";

    public static final String CREATE_TAB = "CREATE TABLE "
            + TABLE_TAB
            + " ("
            + " " + COLUMN_TITLE + " text,"
            + " " + COLUMN_URL + " text,"
            + " " + COLUMN_TIME + " integer"
            + ")";

    public static final String CREATE_WHITELIST = "CREATE TABLE "
            + TABLE_WHITELIST
            + " ("
            + " " + COLUMN_DOMAIN + " text,"
            + " " + COLUMN_PROFILE_ID + " text DEFAULT '" + DEFAULT_PROFILE_ID + "'"
            + ")";

    public static final String CREATE_JAVASCRIPT = "CREATE TABLE "
            + TABLE_JAVASCRIPT
            + " ("
            + " " + COLUMN_DOMAIN + " text,"
            + " " + COLUMN_PROFILE_ID + " text DEFAULT '" + DEFAULT_PROFILE_ID + "'"
            + ")";

    public static final String CREATE_COOKIE = "CREATE TABLE "
            + TABLE_COOKIE
            + " ("
            + " " + COLUMN_DOMAIN + " text,"
            + " " + COLUMN_PROFILE_ID + " text DEFAULT '" + DEFAULT_PROFILE_ID + "'"
            + ")";

    public static final String CREATE_REMOTE = "CREATE TABLE "
            + TABLE_REMOTE
            + " ("
            + " " + COLUMN_DOMAIN + " text,"
            + " " + COLUMN_PROFILE_ID + " text DEFAULT '" + DEFAULT_PROFILE_ID + "'"
            + ")";

    // P1 step 4 migration (DATABASE_VERSION 4 -> 5): add PROFILE_ID to each
    // pre-existing whitelist table without dropping/recreating it. SQLite's
    // ADD COLUMN with a DEFAULT applies that default to all existing rows,
    // so every previously-persisted domain becomes profile "default".
    public static final String ADD_PROFILE_ID_WHITELIST = "ALTER TABLE "
            + TABLE_WHITELIST
            + " ADD COLUMN " + COLUMN_PROFILE_ID + " text DEFAULT '" + DEFAULT_PROFILE_ID + "'";

    public static final String ADD_PROFILE_ID_JAVASCRIPT = "ALTER TABLE "
            + TABLE_JAVASCRIPT
            + " ADD COLUMN " + COLUMN_PROFILE_ID + " text DEFAULT '" + DEFAULT_PROFILE_ID + "'";

    public static final String ADD_PROFILE_ID_COOKIE = "ALTER TABLE "
            + TABLE_COOKIE
            + " ADD COLUMN " + COLUMN_PROFILE_ID + " text DEFAULT '" + DEFAULT_PROFILE_ID + "'";

    public static final String ADD_PROFILE_ID_REMOTE = "ALTER TABLE "
            + TABLE_REMOTE
            + " ADD COLUMN " + COLUMN_PROFILE_ID + " text DEFAULT '" + DEFAULT_PROFILE_ID + "'";

    public static final String CREATE_GRID = "CREATE TABLE "
            + TABLE_GRID
            + " ("
            + " " + COLUMN_TITLE + " text,"
            + " " + COLUMN_URL + " text,"
            + " " + COLUMN_FILENAME + " text,"
            + " " + COLUMN_ORDINAL + " integer"
            + ")";
}
