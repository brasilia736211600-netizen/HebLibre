package de.baumann.browser.unit;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Characterizes the P1 step 4 persisted whitelist profile_id contract at the
 * SQL-string level.
 *
 * RecordAction/RecordHelper themselves require a real Android SQLite
 * runtime and cannot be exercised on the plain JVM unit test classpath
 * (android.database.sqlite.* is stubbed out and throws
 * "not mocked" if invoked). RecordUnit, however, has no Android
 * dependency at all - it only builds the DDL strings used by
 * RecordHelper.onCreate/onUpgrade - so it is the smallest legitimate,
 * non-Android-runtime-dependent contract for this step:
 *
 *  - each of the four whitelist tables' CREATE statement includes a
 *    PROFILE_ID column that defaults existing/omitted rows to "default";
 *  - each of the four ALTER TABLE migration statements (used to upgrade
 *    an existing v4 database to v5) targets its OWN table and adds the
 *    same PROFILE_ID column with the same default, so upgrading
 *    preserves all existing rows under the "default" profile without
 *    guessing/omitting a table.
 *
 * This guards specifically against a copy/paste mistake across the four
 * near-identical CREATE and ADD_PROFILE_ID constants (e.g. accidentally
 * pointing the JAVASCRIPT migration at the WHITELIST table).
 */
public class RecordUnitProfileIdTest {

    @Test
    public void columnAndDefaultConstants_haveExpectedValues() {
        assertTrue(RecordUnit.COLUMN_PROFILE_ID.equals("PROFILE_ID"));
        assertTrue(RecordUnit.DEFAULT_PROFILE_ID.equals("default"));
    }

    @Test
    public void createStatements_includeProfileIdColumnWithDefault_forAllFourWhitelistTables() {
        String[] createStatements = {
                RecordUnit.CREATE_WHITELIST,
                RecordUnit.CREATE_JAVASCRIPT,
                RecordUnit.CREATE_COOKIE,
                RecordUnit.CREATE_REMOTE
        };
        String[] tableNames = {
                RecordUnit.TABLE_WHITELIST,
                RecordUnit.TABLE_JAVASCRIPT,
                RecordUnit.TABLE_COOKIE,
                RecordUnit.TABLE_REMOTE
        };

        for (int i = 0; i < createStatements.length; i++) {
            String sql = createStatements[i];
            assertTrue("CREATE statement must target its own table: " + sql,
                    sql.contains("CREATE TABLE " + tableNames[i]));
            assertTrue("CREATE statement must define " + RecordUnit.COLUMN_PROFILE_ID + ": " + sql,
                    sql.contains(RecordUnit.COLUMN_PROFILE_ID));
            assertTrue("CREATE statement must default profile_id to '"
                            + RecordUnit.DEFAULT_PROFILE_ID + "': " + sql,
                    sql.contains("DEFAULT '" + RecordUnit.DEFAULT_PROFILE_ID + "'"));
        }
    }

    @Test
    public void migrationStatements_eachTargetOwnTable_addProfileIdWithDefault() {
        // Each migration statement must be anchored to its own table via
        // ALTER TABLE <table> ... - startsWith enforces the anchor so a
        // copy/paste of the wrong table constant would fail this test.
        assertTrue(RecordUnit.ADD_PROFILE_ID_WHITELIST
                .startsWith("ALTER TABLE " + RecordUnit.TABLE_WHITELIST));
        assertTrue(RecordUnit.ADD_PROFILE_ID_JAVASCRIPT
                .startsWith("ALTER TABLE " + RecordUnit.TABLE_JAVASCRIPT));
        assertTrue(RecordUnit.ADD_PROFILE_ID_COOKIE
                .startsWith("ALTER TABLE " + RecordUnit.TABLE_COOKIE));
        assertTrue(RecordUnit.ADD_PROFILE_ID_REMOTE
                .startsWith("ALTER TABLE " + RecordUnit.TABLE_REMOTE));

        String[] migrations = {
                RecordUnit.ADD_PROFILE_ID_WHITELIST,
                RecordUnit.ADD_PROFILE_ID_JAVASCRIPT,
                RecordUnit.ADD_PROFILE_ID_COOKIE,
                RecordUnit.ADD_PROFILE_ID_REMOTE
        };
        for (String sql : migrations) {
            assertTrue("migration must add " + RecordUnit.COLUMN_PROFILE_ID + ": " + sql,
                    sql.contains("ADD COLUMN " + RecordUnit.COLUMN_PROFILE_ID));
            assertTrue("migration must default existing rows to '"
                            + RecordUnit.DEFAULT_PROFILE_ID + "': " + sql,
                    sql.contains("DEFAULT '" + RecordUnit.DEFAULT_PROFILE_ID + "'"));
            assertFalse("migration must not drop/recreate the table: " + sql,
                    sql.toUpperCase().contains("DROP"));
        }
    }
}
