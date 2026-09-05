package de.baumann.browser.unit;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
    public void plainRoundTripPreservesProfileAndRecords() {
        List<Record> history = Collections.singletonList(
                new Record("History | title", "https://example.com/a?x=1&y=2", 11L, -1));
        List<Record> bookmarks = Collections.singletonList(
                new Record("Bookmark", "https://example.com/book", 12L, -1));
        List<Record> tabs = Collections.singletonList(
                new Record("Tab", "https://example.com/tab", 13L, -1));

        String encoded = ProfileTransferCodec.encodePlain(metadata(), history, bookmarks, tabs);
        ProfileTransferCodec.TransferPackage decoded = ProfileTransferCodec.decode(encoded, null);

        assertEquals("work", decoded.getMetadata().getId());
        assertEquals("Work Profile", decoded.getMetadata().getName());
        assertEquals(Arrays.asList("work", "research,deep"), decoded.getMetadata().getTags());
        assertEquals(1, decoded.getHistory().size());
        assertEquals(history.get(0).getURL(), decoded.getHistory().get(0).getURL());
        assertEquals(1, decoded.getBookmarks().size());
        assertEquals(1, decoded.getTabs().size());
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

        String tampered = encoded.replaceFirst("ciphertext=([0-9a-f])", "ciphertext=$1")
                .replace("ciphertext=", "ciphertext=f", 1);
        try {
            ProfileTransferCodec.decode(tampered, "correct-horse");
            throw new AssertionError("Expected tampered ciphertext to fail");
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }

    @Test
    public void rejectsShortPasswordAndUnknownFormat() {
        try {
            ProfileTransferCodec.encodeEncrypted(metadata(), null, null, null, "short");
            throw new AssertionError("Expected short password to fail");
        } catch (IllegalArgumentException expected) {
            // expected
        }

        try {
            ProfileTransferCodec.decode("UNKNOWN", null);
            throw new AssertionError("Expected unknown format to fail");
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }
}
