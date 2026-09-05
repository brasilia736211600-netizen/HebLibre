package de.baumann.browser.unit;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import de.baumann.browser.database.Record;

/** Versioned portable representation for app-owned profile state. */
public final class ProfileTransferCodec {
    private static final String PLAIN_HEADER = "HEBLIBRE_PROFILE_V1_PLAIN";
    private static final String ENCRYPTED_HEADER = "HEBLIBRE_PROFILE_V1_AES_GCM";
    private static final int SALT_BYTES = 16;
    private static final int IV_BYTES = 12;
    private static final int GCM_TAG_BYTES = 16;
    private static final int KEY_BITS = 256;
    private static final int PBKDF2_ITERATIONS = 120_000;

    private ProfileTransferCodec() {
    }

    public static String encodePlain(ProfileMetadata metadata,
                                     List<Record> history,
                                     List<Record> bookmarks,
                                     List<Record> tabs) {
        return encodePlain(metadata, history, bookmarks, tabs, Collections.<String, String>emptyMap());
    }

    public static String encodePlain(ProfileMetadata metadata,
                                     List<Record> history,
                                     List<Record> bookmarks,
                                     List<Record> tabs,
                                     Map<String, String> preferences) {
        StringBuilder out = new StringBuilder();
        out.append(PLAIN_HEADER).append('\n');
        line(out, "id", metadata.getId());
        line(out, "name", metadata.getName());
        line(out, "color", metadata.getColor());
        line(out, "icon", metadata.getIcon());
        line(out, "notes", metadata.getNotes());
        line(out, "tags", joinEncodedTags(metadata.getTags()));
        line(out, "group", metadata.getGroup());
        out.append("PREFERENCES\n");
        appendPreferences(out, preferences);
        appendRecords(out, "HISTORY", history);
        appendRecords(out, "BOOKMARKS", bookmarks);
        appendRecords(out, "TABS", tabs);
        return out.toString();
    }

    public static String encodeEncrypted(ProfileMetadata metadata,
                                          List<Record> history,
                                          List<Record> bookmarks,
                                          List<Record> tabs,
                                          String password) {
        return encodeEncrypted(metadata, history, bookmarks, tabs, password,
                Collections.<String, String>emptyMap());
    }

    public static String encodeEncrypted(ProfileMetadata metadata,
                                          List<Record> history,
                                          List<Record> bookmarks,
                                          List<Record> tabs,
                                          String password,
                                          Map<String, String> preferences) {
        requirePassword(password);
        byte[] salt = randomBytes(SALT_BYTES);
        byte[] iv = randomBytes(IV_BYTES);
        try {
            SecretKey key = deriveKey(password, salt);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(128, iv));
            byte[] ciphertext = cipher.doFinal(encodePlain(
                    metadata, history, bookmarks, tabs, preferences)
                    .getBytes(StandardCharsets.UTF_8));
            StringBuilder out = new StringBuilder();
            out.append(ENCRYPTED_HEADER).append('\n');
            line(out, "salt", toHex(salt));
            line(out, "iv", toHex(iv));
            line(out, "ciphertext", toHex(ciphertext));
            return out.toString();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Unable to encrypt profile export", e);
        }
    }

    public static TransferPackage decode(String content, String password) {
        if (content == null) {
            throw new IllegalArgumentException("Empty profile export");
        }
        String normalized = content.trim();
        List<String> lines = lines(normalized);
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("Empty profile export");
        }
        String header = lines.get(0);
        if (ENCRYPTED_HEADER.equals(header)) {
            return decodeEncrypted(normalized, password);
        }
        if (PLAIN_HEADER.equals(header)) {
            return decodePlain(normalized);
        }
        throw new IllegalArgumentException("Unsupported profile export format");
    }

    private static TransferPackage decodeEncrypted(String content, String password) {
        requirePassword(password);
        List<String> lines = lines(content);
        String saltHex = findValue(lines, "salt");
        String ivHex = findValue(lines, "iv");
        String ciphertextHex = findValue(lines, "ciphertext");
        try {
            byte[] salt = fromHex(saltHex);
            byte[] iv = fromHex(ivHex);
            byte[] ciphertext = fromHex(ciphertextHex);
            if (salt.length != SALT_BYTES || iv.length != IV_BYTES || ciphertext.length < GCM_TAG_BYTES) {
                throw new IllegalArgumentException("Invalid encrypted profile export dimensions");
            }
            SecretKey key = deriveKey(password, salt);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(128, iv));
            String plain = new String(cipher.doFinal(ciphertext), StandardCharsets.UTF_8);
            return decodePlain(plain.trim());
        } catch (GeneralSecurityException | IllegalArgumentException e) {
            throw new IllegalArgumentException("Incorrect password or damaged profile export", e);
        }
    }

    private static TransferPackage decodePlain(String content) {
        List<String> lines = lines(content);
        if (lines.isEmpty() || !PLAIN_HEADER.equals(lines.get(0))) {
            throw new IllegalArgumentException("Unsupported profile export format");
        }
        String id = decode(findValue(lines, "id"));
        String name = decode(findValue(lines, "name"));
        String color = decode(findValue(lines, "color"));
        String icon = decode(findValue(lines, "icon"));
        String notes = decode(findValue(lines, "notes"));
        String tagsValue = decode(findValue(lines, "tags"));
        String group = decode(findValue(lines, "group"));
        ProfileMetadata metadata = new ProfileMetadata(
                id,
                name,
                color,
                icon,
                notes,
                splitEncodedTags(tagsValue),
                group);

        List<Record> history = new ArrayList<>();
        List<Record> bookmarks = new ArrayList<>();
        List<Record> tabs = new ArrayList<>();
        Map<String, String> preferences = new LinkedHashMap<>();
        List<Record> target = null;
        for (String line : lines) {
            if ("PREFERENCES".equals(line)) {
                target = null;
            } else if ("HISTORY".equals(line)) {
                target = history;
            } else if ("BOOKMARKS".equals(line)) {
                target = bookmarks;
            } else if ("TABS".equals(line)) {
                target = tabs;
            } else if (line.startsWith("P|")) {
                String[] parts = line.split("\\|", 3);
                if (parts.length != 3 || parts[1].isEmpty()) {
                    throw new IllegalArgumentException("Malformed profile preference");
                }
                preferences.put(parts[1], decode(parts[2]));
            } else if (line.startsWith("R|")) {
                if (target == null) {
                    throw new IllegalArgumentException("Record found outside a section");
                }
                target.add(parseRecord(line));
            }
        }
        return new TransferPackage(metadata, history, bookmarks, tabs, preferences);
    }

    private static Record parseRecord(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length != 4) {
            throw new IllegalArgumentException("Malformed profile record");
        }
        long time;
        try {
            time = Long.parseLong(parts[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Malformed profile record timestamp", e);
        }
        return new Record(decode(parts[2]), decode(parts[3]), time, -1);
    }

    private static void appendRecords(StringBuilder out, String section, List<Record> records) {
        out.append(section).append('\n');
        if (records == null) {
            return;
        }
        for (Record record : records) {
            if (record == null) {
                continue;
            }
            out.append("R|")
                    .append(record.getTime())
                    .append('|')
                    .append(encode(record.getTitle()))
                    .append('|')
                    .append(encode(record.getURL()))
                    .append('\n');
        }
    }

    private static void appendPreferences(StringBuilder out, Map<String, String> preferences) {
        if (preferences == null || preferences.isEmpty()) {
            return;
        }
        for (Map.Entry<String, String> entry : preferences.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) {
                continue;
            }
            out.append("P|")
                    .append(entry.getKey())
                    .append('|')
                    .append(encode(entry.getValue()))
                    .append('\n');
        }
    }

    private static void line(StringBuilder out, String key, String value) {
        out.append(key).append('=').append(encode(value)).append('\n');
    }

    private static String encode(String value) {
        try {
            return URLEncoder.encode(value == null ? "" : value, "UTF-8");
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static String decode(String value) {
        try {
            return URLDecoder.decode(value == null ? "" : value, "UTF-8");
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid profile export encoding", e);
        }
    }

    private static String joinEncodedTags(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        for (String tag : tags) {
            if (result.length() > 0) {
                result.append(',');
            }
            result.append(encode(tag));
        }
        return result.toString();
    }

    private static List<String> splitEncodedTags(String tags) {
        if (tags == null || tags.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String[] values = tags.split(",", -1);
        List<String> result = new ArrayList<>();
        for (String value : values) {
            result.add(decode(value));
        }
        return result;
    }

    private static List<String> lines(String content) {
        String[] values = content.split("\\R");
        List<String> result = new ArrayList<>();
        Collections.addAll(result, values);
        return result;
    }

    private static String findValue(List<String> lines, String key) {
        String prefix = key + "=";
        for (String line : lines) {
            if (line.startsWith(prefix)) {
                return line.substring(prefix.length());
            }
        }
        throw new IllegalArgumentException("Missing profile export field: " + key);
    }

    private static SecretKey deriveKey(String password, byte[] salt) throws GeneralSecurityException {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATIONS, KEY_BITS);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        } finally {
            spec.clearPassword();
        }
    }

    private static void requirePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must contain at least 8 characters");
        }
    }

    private static byte[] randomBytes(int length) {
        byte[] value = new byte[length];
        new SecureRandom().nextBytes(value);
        return value;
    }

    private static String toHex(byte[] bytes) {
        StringBuilder result = new StringBuilder(bytes.length * 2);
        for (byte value : bytes) {
            result.append(String.format(java.util.Locale.ROOT, "%02x", value & 0xff));
        }
        return result.toString();
    }

    private static byte[] fromHex(String value) {
        if (value == null || (value.length() & 1) != 0) {
            throw new IllegalArgumentException("Invalid hex payload");
        }
        byte[] result = new byte[value.length() / 2];
        for (int i = 0; i < result.length; i++) {
            int high = Character.digit(value.charAt(i * 2), 16);
            int low = Character.digit(value.charAt(i * 2 + 1), 16);
            if (high < 0 || low < 0) {
                throw new IllegalArgumentException("Invalid hex payload");
            }
            result[i] = (byte) ((high << 4) | low);
        }
        return result;
    }

    public static final class TransferPackage {
        private final ProfileMetadata metadata;
        private final List<Record> history;
        private final List<Record> bookmarks;
        private final List<Record> tabs;
        private final Map<String, String> preferences;

        private TransferPackage(ProfileMetadata metadata,
                                List<Record> history,
                                List<Record> bookmarks,
                                List<Record> tabs,
                                Map<String, String> preferences) {
            this.metadata = metadata;
            this.history = Collections.unmodifiableList(new ArrayList<>(history));
            this.bookmarks = Collections.unmodifiableList(new ArrayList<>(bookmarks));
            this.tabs = Collections.unmodifiableList(new ArrayList<>(tabs));
            this.preferences = Collections.unmodifiableMap(new LinkedHashMap<>(preferences));
        }

        public ProfileMetadata getMetadata() { return metadata; }
        public List<Record> getHistory() { return history; }
        public List<Record> getBookmarks() { return bookmarks; }
        public List<Record> getTabs() { return tabs; }
        public Map<String, String> getPreferences() { return preferences; }
    }
}
