package de.baumann.browser.unit;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class RecordUnitTest {

    @Test
    public void browsingRecordTablesPersistProfileIdentity() {
        assertProfileColumn(RecordUnit.CREATE_HISTORY);
        assertProfileColumn(RecordUnit.CREATE_BOOKMARK);
        assertProfileColumn(RecordUnit.CREATE_TAB);
    }

    @Test
    public void browsingRecordMigrationsDefaultLegacyRowsToDefaultProfile() {
        assertMigration(RecordUnit.ADD_PROFILE_ID_HISTORY, RecordUnit.TABLE_HISTORY);
        assertMigration(RecordUnit.ADD_PROFILE_ID_BOOKMARK, RecordUnit.TABLE_BOOKMARK);
        assertMigration(RecordUnit.ADD_PROFILE_ID_TAB, RecordUnit.TABLE_TAB);
    }

    private static void assertProfileColumn(String createSql) {
        assertTrue(createSql.contains(RecordUnit.COLUMN_PROFILE_ID));
        assertTrue(createSql.contains("DEFAULT '" + RecordUnit.DEFAULT_PROFILE_ID + "'"));
    }

    private static void assertMigration(String migrationSql, String table) {
        assertTrue(migrationSql.startsWith("ALTER TABLE " + table));
        assertTrue(migrationSql.contains("ADD COLUMN " + RecordUnit.COLUMN_PROFILE_ID));
        assertTrue(migrationSql.contains("DEFAULT '" + RecordUnit.DEFAULT_PROFILE_ID + "'"));
    }
}
