package de.baumann.browser.unit;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.baumann.browser.database.Record;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ProfileTransferCodecTest {

    private ProfileMetadata metadata() {
        return new ProfileMetadata(
                "work",
                "Work Profile",
                "#123456",
                "briefcase",
                "private notes",
                Arrays.asList("work", "research,deep"),
                "main");
    }

    @Test
    public void plainRoundTripPreservesProfileRecordsAndPreferences() {
        List<Record> history = Collections.singletonList(
                new Record("History | title", "https://example.com/a?x=1&y=2", 11L, -1));
        List<Record> bookmarks = Collections.singletonList(
                new Record("Bookmark", "https://example.com/book", 12L, -1));
        List<Record> tabs = Collections.singletonList(
                new Record("Tab", "https://example.com/tab", 13L, -1));
        Map<String, String> preferences = new HashMap<>();
        preferences.put("desktop_mode", "b:true");
        preferences.put("userAgent", "s:Profile-UA & value");

        String encoded = ProfileTransferCodec.encodePlain(metadata(), history, bookmarks, tabs, preferences);
        ProfileTransferCodec.TransferPackage decoded = ProfileTransferCodec.decode(encoded, null);

        assertEquals("work", decoded.getMetadata().getId());
        assertEquals("Work Profile", decoded.getMetadata().getName());
        assertEquals(Arrays.asList("work", "research,deep"), decoded.getMetadata().getTags());
        assertEquals(1, decoded.getHistory().size());
        assertEquals(history.get(0).getURL(), decoded.getHistory().get(0).getURL());
        assertEquals(1, decoded.getBookmarks().size());
        assertEquals(1, decoded.getTabs().size());
        assertEquals("b:true", decoded.getPreferences().get("desktop_mode"));
        assertEquals("s:Profile-UA & value", decoded.getPreferences().get("userAgent"));
    }

    @Test
    public void encryptedRoundTripWrongPasswordAndTamperingFail() {
        String encoded = ProfileTransferCodec.encodeEncrypted(
                metadata(),
                Collections.singletonList(new Record("H", "https://example.com", 1L, -1)),
                Collections.<Record>emptyList(),
                Collections.<Record>emptyList(),
                "correct-horse");

        assertTrue(encoded.startsWith("HEBLIBRE_PROFILE_V1_AES_GCM"));
        ProfileTransferCodec.TransferPackage decoded = ProfileTransferCodec.decode(encoded, "correct-horse");
        assertEquals("work", decoded.getMetadata().getId());
        assertEquals(1, decoded.getHistory().size());

        try {
            ProfileTransferCodec.decode(encoded, "wrong-password");
            throw new AssertionError("Expected wrong password to fail");
        } catch (IllegalArgumentException expected) {
            // expected
        }

        int payloadStart = encoded.indexOf("ciphertext=") + "ciphertext=".length();
        StringBuilder tamperedBuilder = new StringBuilder(encoded);
        char original = tamperedBuilder.charAt(payloadStart);
        tamperedBuilder.setCharAt(payloadStart, original == '0' ? '1' : '0');
        try {
            ProfileTransferCodec.decode(tamperedBuilder.toString(), "correct-horse");
            throw new AssertionError("Expected tampered ciphertext to fail");
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }

    @Test
    public void rejectsShortPasswordUnknownHeaderAndInvalidEncryptedDimensions() {
        try {
            ProfileTransferCodec.encodeEncrypted(metadata(), null, null, null, "short");
            throw new AssertionError("Expected short password to fail");
        } catch (IllegalArgumentException expected) {
            // expected
        }

        String plain = ProfileTransferCodec.encodePlain(metadata(), null, null, null);
        try {
            ProfileTransferCodec.decode("HEBLIBRE_PROFILE_V1_PLAIN_EXTRA\n" + plain.substring(plain.indexOf('\n') + 1), null);
            throw new AssertionError("Expected header suffix to fail");
        } catch (IllegalArgumentException expected) {
            // expected
        }

        try {
            ProfileTransferCodec.decode("UNKNOWN", null);
            throw new AssertionError("Expected unknown format to fail");
        } catch (IllegalArgumentException expected) {
            // expected
        }

        String encrypted = ProfileTransferCodec.encodeEncrypted(
                metadata(), Collections.<Record>emptyList(), Collections.<Record>emptyList(),
                Collections.<Record>emptyList(), "correct-horse");
        String badSalt = replaceLine(encrypted, "salt=", "salt=00");
        try {
            ProfileTransferCodec.decode(badSalt, "correct-horse");
            throw new AssertionError("Expected invalid salt length to fail");
        } catch (IllegalArgumentException expected) {
            // expected
        }

        String badIv = replaceLine(encrypted, "iv=", "iv=00");
        try {
            ProfileTransferCodec.decode(badIv, "correct-horse");
            throw new AssertionError("Expected invalid IV length to fail");
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }

    @Test
    public void legacyExportsDecodeWithEmptyPreferences() {
        String encoded = ProfileTransferCodec.encodePlain(metadata(), null, null, null);
        ProfileTransferCodec.TransferPackage decoded = ProfileTransferCodec.decode(encoded, null);
        assertTrue(decoded.getPreferences().isEmpty());
    }

    private static String replaceLine(String content, String key, String replacement) {
        String[] lines = content.split("\\R");
        StringBuilder result = new StringBuilder();
        for (String line : lines) {
            if (line.startsWith(key)) {
                line = replacement;
            }
            if (result.length() > 0) {
                result.append('\n');
            }
            result.append(line);
        }
        return result.toString();
    }
}
